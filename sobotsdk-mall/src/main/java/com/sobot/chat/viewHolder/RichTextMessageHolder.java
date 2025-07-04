package com.sobot.chat.viewHolder;

import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.R;
import com.sobot.chat.activity.SobotFileDetailActivity;
import com.sobot.chat.activity.SobotVideoActivity;
import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.adapter.SobotMsgAdapter;
import com.sobot.chat.api.model.ChatMessageRichListModel;
import com.sobot.chat.api.model.SobotCacheFile;
import com.sobot.chat.api.model.SobotLink;
import com.sobot.chat.api.model.Suggestions;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.camera.util.FileUtil;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.listener.NoDoubleClickListener;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.viewHolder.base.MessageHolderBase;
import com.sobot.chat.widget.SobotSectorProgressView;
import com.sobot.chat.widget.attachment.FileTypeConfig;
import com.sobot.network.http.callback.StringResultCallBack;
import com.sobot.pictureframe.SobotBitmapUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 富文本消息
 * Created by jinxl on 2017/3/17.
 */
public class RichTextMessageHolder extends MessageHolderBase implements View.OnClickListener {
    private TextView msg; // 聊天的消息内容
    private LinearLayout sobot_rich_ll;//拆分的富文本消息
    private TextView sobot_msgStripe; // 多轮会话中配置的引导语
    private LinearLayout answersList;
    private TextView stripe;

    private LinearLayout sobot_ll_transferBtn;//只包含转人工按钮
    private TextView sobot_tv_transferBtn;//机器人转人工按钮
    private LinearLayout sobot_ll_bottom_likeBtn;
    private LinearLayout sobot_ll_bottom_dislikeBtn;
    private TextView sobot_tv_bottom_likeBtn;//气泡下边 机器人评价 顶 的按钮
    private TextView sobot_tv_bottom_dislikeBtn;//气泡下边 机器人评价 踩 的按钮

    private RelativeLayout sobot_right_empty_rl;
    private LinearLayout sobot_ll_content;
    private LinearLayout sobot_ll_likeBtn;
    private LinearLayout sobot_ll_dislikeBtn;
    private TextView sobot_tv_likeBtn;//机器人评价 顶 的按钮
    private TextView sobot_tv_dislikeBtn;//机器人评价 踩 的按钮
    private LinearLayout sobot_ll_switch;//换一组按钮
    private TextView sobot_tv_switch;
    private View sobot_view_split;//换一组和查看详情分割线

    public RichTextMessageHolder(Context context, View convertView) {
        super(context, convertView);
        msg = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_msg"));
        sobot_rich_ll = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_rich_ll"));
        sobot_msgStripe = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_msgStripe"));
        sobot_ll_transferBtn = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_ll_transferBtn"));
        sobot_ll_likeBtn = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_ll_likeBtn"));
        sobot_ll_dislikeBtn = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_ll_dislikeBtn"));
        sobot_ll_content = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_ll_content"));
        sobot_ll_switch = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_ll_switch"));
        sobot_tv_switch = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_switch"));
        sobot_tv_switch.setText(ResourceUtils.getResString(context, "sobot_switch"));
        sobot_view_split = convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_view_split"));
        sobot_right_empty_rl = (RelativeLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_right_empty_rl"));

        stripe = (TextView) convertView.findViewById(ResourceUtils
                .getIdByName(context, "id", "sobot_stripe"));
        answersList = (LinearLayout) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id",
                        "sobot_answersList"));

        sobot_tv_transferBtn = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_transferBtn"));
        sobot_tv_transferBtn.setText(ResourceUtils.getResString(context, "sobot_transfer_to_customer_service"));
        sobot_tv_likeBtn = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_likeBtn"));
        sobot_tv_dislikeBtn = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_dislikeBtn"));
        sobot_ll_switch.setOnClickListener(this);
        msg.setMaxWidth(msgMaxWidth);

        sobot_ll_bottom_likeBtn = convertView.findViewById(R.id.sobot_ll_bottom_likeBtn);
        sobot_ll_bottom_dislikeBtn = convertView.findViewById(R.id.sobot_ll_bottom_dislikeBtn);
        sobot_tv_bottom_likeBtn = convertView.findViewById(R.id.sobot_tv_bottom_likeBtn);
        sobot_tv_bottom_dislikeBtn = convertView.findViewById(R.id.sobot_tv_bottom_dislikeBtn);
    }

    @Override
    public void bindData(final Context context, final ZhiChiMessageBase message) {
        // 更具消息类型进行对布局的优化
        if (message.getAnswer() != null) {
            setupMsgContent(context, message);
            if (!TextUtils.isEmpty(message.getAnswer().getMsgStripe())) {
                sobot_msgStripe.setVisibility(View.VISIBLE);
                sobot_msgStripe.setText(message.getAnswer().getMsgStripe());
            } else {
                sobot_msgStripe.setVisibility(View.GONE);
            }
        }

        // 回复语的答复
        String stripeContent = message.getStripe() != null ? message.getStripe().trim() : "";
        if (!TextUtils.isEmpty(stripeContent)) {
            //去掉p标签
            stripeContent = stripeContent.replace("<p>", "").replace("</p>", "<br/>");
            // 设置提醒的内容
            stripe.setVisibility(View.VISIBLE);
            HtmlTools.getInstance(context).setRichText(stripe, stripeContent, getLinkTextColor());
        } else {
            stripe.setText(null);
            stripe.setVisibility(View.GONE);
        }
        resetMaxWidth(stripe);
        resetMaxWidth(sobot_msgStripe);

        if (message.isGuideGroupFlag()//有分组
                && message.getListSuggestions() != null//有分组问题列表
                && message.getGuideGroupNum() > -1//分组不是全部
                && message.getListSuggestions().size() > 0//问题数量大于0
                && message.getGuideGroupNum() < message.getListSuggestions().size()//分组数量小于问题数量
        ) {
            sobot_ll_switch.setVisibility(View.VISIBLE);
            sobot_view_split.setVisibility(View.VISIBLE);
        } else {
            sobot_ll_switch.setVisibility(View.GONE);
            sobot_view_split.setVisibility(View.GONE);

        }

        if (message.getSugguestions() != null && message.getSugguestions().length > 0) {
            resetAnswersList();
        } else {
            answersList.setVisibility(View.GONE);
        }

        checkShowTransferBtn();

        msg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!TextUtils.isEmpty(message.getAnswer().getMsg())) {
                    ToastUtil.showCopyPopWindows(context, view, message.getAnswer().getMsg(), 30, 0);
                }
                return false;
            }
        });

        applyTextViewUIConfig(msg);

        refreshItem();
    }

    //设置问题列表
    private void resetAnswersList() {
        if (message == null) {
            return;
        }
        if (message.getListSuggestions() != null && message.getListSuggestions().size() > 0) {
            ArrayList<Suggestions> listSuggestions = message.getListSuggestions();
            answersList.removeAllViews();
            answersList.setVisibility(View.VISIBLE);
            int startNum = 0;
            int endNum = listSuggestions.size();
            if (message.isGuideGroupFlag() && message.getGuideGroupNum() > -1) {//有分组且不是全部
                startNum = message.getCurrentPageNum() * message.getGuideGroupNum();
                endNum = Math.min(startNum + message.getGuideGroupNum(), listSuggestions.size());
            }
            for (int i = startNum; i < endNum; i++) {
                TextView answer = ChatUtils.initAnswerItemTextView(mContext, false);
                int currentItem = i + 1;
                answer.setOnClickListener(new AnsWerClickLisenter(mContext, null,
                        listSuggestions.get(i).getQuestion(), null, listSuggestions.get(i).getDocId(), msgCallBack));
                String tempStr = processPrefix(message, currentItem) + listSuggestions.get(i).getQuestion();
                answer.setText(tempStr);
                answersList.addView(answer);
            }
        } else {
            String[] answerStringList = message.getSugguestions();
            answersList.removeAllViews();
            answersList.setVisibility(View.VISIBLE);
            for (int i = 0; i < answerStringList.length; i++) {
                TextView answer = ChatUtils.initAnswerItemTextView(mContext, true);
                int currentItem = i + 1;
                String tempStr = processPrefix(message, currentItem) + answerStringList[i];
                answer.setText(tempStr);
                answersList.addView(answer);
            }
        }
        ViewGroup.LayoutParams layoutParams = answersList.getLayoutParams();
        layoutParams.width = msgMaxWidth;
        answersList.setLayoutParams(layoutParams);
    }

    private void resetMinWidth() {
        ViewGroup.LayoutParams layoutParams = sobot_ll_content.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        sobot_ll_content.setLayoutParams(layoutParams);
    }

    private void checkShowTransferBtn() {
        if (message.getTransferType() == 4) {
            //4 多次命中 显示转人工
            showTransferBtn();
        } else {
            if (message.isShowTransferBtn()) {
                showTransferBtn();
            } else {
                hideTransferBtn();
            }
        }
    }

    private void hideContainer() {
        if (!message.isShowTransferBtn()) {
            sobot_ll_transferBtn.setVisibility(View.GONE);
        } else {
            sobot_ll_transferBtn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏转人工按钮
     */
    public void hideTransferBtn() {
        hideContainer();
        sobot_ll_transferBtn.setVisibility(View.GONE);
        sobot_tv_transferBtn.setVisibility(View.GONE);
        if (message != null) {
            message.setShowTransferBtn(false);
        }
    }

    /**
     * 显示转人工按钮
     */
    public void showTransferBtn() {
        sobot_tv_transferBtn.setVisibility(View.VISIBLE);
        sobot_ll_transferBtn.setVisibility(View.VISIBLE);
        if (message != null) {
            message.setShowTransferBtn(true);
        }
        sobot_ll_transferBtn.setOnClickListener(new NoDoubleClickListener() {

            @Override
            public void onNoDoubleClick(View v) {
                if (msgCallBack != null) {
                    msgCallBack.doClickTransferBtn(message);
                }
            }
        });
    }

    public void refreshItem() {
        try {
            //顶 踩的状态 0 不显示顶踩按钮  1显示顶踩 按钮  2 显示顶之后的view  3显示踩之后view
            switch (message.getRevaluateState()) {
                case 1:
                    showRevaluateBtn();
                    break;
                case 2:
                    showLikeWordView();
                    break;
                case 3:
                    showDislikeWordView();
                    break;
                default:
                    hideRevaluateBtn();
                    break;
            }
        } catch (Exception e) {
        }
    }

    /**
     * 显示 顶踩 按钮
     */
    public void showRevaluateBtn() {
        if (dingcaiIsShowRight()) {
            sobot_tv_likeBtn.setVisibility(View.VISIBLE);
            sobot_tv_dislikeBtn.setVisibility(View.VISIBLE);
            sobot_ll_likeBtn.setVisibility(View.VISIBLE);
            sobot_ll_dislikeBtn.setVisibility(View.VISIBLE);
            sobot_right_empty_rl.setVisibility(View.VISIBLE);
            sobot_tv_bottom_likeBtn.setVisibility(View.GONE);
            sobot_tv_bottom_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_bottom_likeBtn.setVisibility(View.GONE);
            sobot_ll_bottom_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_likeBtn.setBackground(mContext.getResources().getDrawable(R.drawable.sobot_chat_dingcai_right_def));
            sobot_ll_dislikeBtn.setBackground(mContext.getResources().getDrawable(R.drawable.sobot_chat_dingcai_right_def));
        } else {
            sobot_tv_bottom_likeBtn.setVisibility(View.VISIBLE);
            sobot_tv_bottom_dislikeBtn.setVisibility(View.VISIBLE);
            sobot_ll_bottom_likeBtn.setVisibility(View.VISIBLE);
            sobot_ll_bottom_dislikeBtn.setVisibility(View.VISIBLE);
            sobot_tv_likeBtn.setVisibility(View.GONE);
            sobot_tv_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_likeBtn.setVisibility(View.GONE);
            sobot_ll_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_bottom_likeBtn.setBackground(mContext.getResources().getDrawable(R.drawable.sobot_chat_dingcai_bottom_def));
            sobot_ll_bottom_dislikeBtn.setBackground(mContext.getResources().getDrawable(R.drawable.sobot_chat_dingcai_bottom_def));
        }
        sobot_tv_likeBtn.setEnabled(true);
        sobot_tv_dislikeBtn.setEnabled(true);
        sobot_tv_likeBtn.setSelected(false);
        sobot_tv_dislikeBtn.setSelected(false);

        sobot_tv_bottom_likeBtn.setEnabled(true);
        sobot_tv_bottom_dislikeBtn.setEnabled(true);
        sobot_tv_bottom_likeBtn.setSelected(false);
        sobot_tv_bottom_dislikeBtn.setSelected(false);

        if (dingcaiIsShowRight()) {
            //有顶和踩时显示信息显示两行 72-10-10=52 总高度减去上下内间距
            msg.setMinHeight(ScreenUtils.dip2px(mContext, 52));
            //有顶和踩时,拆分后的富文本消息如果只有一个并且是文本类型设置最小高度 72-10-10=52 总高度减去上下内间距
            if (sobot_rich_ll != null && sobot_rich_ll.getChildCount() == 1) {
                for (int i = 0; i < sobot_rich_ll.getChildCount(); i++) {
                    View view = sobot_rich_ll.getChildAt(i);
                    if (view instanceof TextView) {
                        TextView tv = (TextView) view;
                        tv.setMinHeight(ScreenUtils.dip2px(mContext, 52));
                    }
                }
            }
        }

        sobot_tv_likeBtn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                doRevaluate(true);
            }
        });
        sobot_tv_dislikeBtn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                doRevaluate(false);
            }
        });

        sobot_tv_bottom_likeBtn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                doRevaluate(true);
            }
        });
        sobot_tv_bottom_dislikeBtn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                doRevaluate(false);
            }
        });
    }

    /**
     * 顶踩 操作
     *
     * @param revaluateFlag true 顶  false 踩
     */
    private void doRevaluate(boolean revaluateFlag) {
        if (msgCallBack != null) {
            msgCallBack.doRevaluate(revaluateFlag, message);
        }
    }

    /**
     * 隐藏 顶踩 按钮
     */
    public void hideRevaluateBtn() {
        hideContainer();
        sobot_tv_likeBtn.setVisibility(View.GONE);
        sobot_tv_dislikeBtn.setVisibility(View.GONE);
        sobot_ll_likeBtn.setVisibility(View.GONE);
        sobot_right_empty_rl.setVisibility(View.GONE);
        sobot_ll_dislikeBtn.setVisibility(View.GONE);
        sobot_tv_bottom_likeBtn.setVisibility(View.GONE);
        sobot_tv_bottom_dislikeBtn.setVisibility(View.GONE);
        sobot_ll_bottom_likeBtn.setVisibility(View.GONE);
        sobot_ll_bottom_dislikeBtn.setVisibility(View.GONE);
        if (dingcaiIsShowRight()) {
            //没有顶和踩时显示信息显示一行 42-10-10=52 总高度减去上下内间距
            msg.setMinHeight(ScreenUtils.dip2px(mContext, 22));
        }
    }

    /**
     * 显示顶之后的view
     */
    public void showLikeWordView() {
        if (dingcaiIsShowRight()) {
            //有顶和踩时显示信息显示两行
            msg.setMinHeight(ScreenUtils.dip2px(mContext, 52));
            sobot_tv_likeBtn.setSelected(true);
            sobot_tv_likeBtn.setEnabled(false);
            sobot_tv_dislikeBtn.setEnabled(false);
            sobot_tv_dislikeBtn.setSelected(false);
            sobot_tv_likeBtn.setVisibility(View.VISIBLE);
            sobot_tv_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_likeBtn.setVisibility(View.VISIBLE);
            sobot_right_empty_rl.setVisibility(View.VISIBLE);
            sobot_ll_dislikeBtn.setVisibility(View.GONE);
            sobot_tv_bottom_likeBtn.setVisibility(View.GONE);
            sobot_tv_bottom_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_bottom_likeBtn.setVisibility(View.GONE);
            sobot_ll_bottom_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_likeBtn.setBackground(mContext.getResources().getDrawable(R.drawable.sobot_chat_dingcai_right_sel));
        } else {
            sobot_tv_bottom_likeBtn.setSelected(true);
            sobot_tv_bottom_likeBtn.setEnabled(false);
            sobot_tv_bottom_dislikeBtn.setEnabled(false);
            sobot_tv_bottom_dislikeBtn.setSelected(false);
            sobot_tv_bottom_likeBtn.setVisibility(View.VISIBLE);
            sobot_tv_bottom_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_bottom_likeBtn.setVisibility(View.VISIBLE);
            sobot_ll_bottom_dislikeBtn.setVisibility(View.GONE);
            sobot_tv_likeBtn.setVisibility(View.GONE);
            sobot_tv_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_likeBtn.setVisibility(View.GONE);
            sobot_ll_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_bottom_likeBtn.setBackground(mContext.getResources().getDrawable(R.drawable.sobot_chat_dingcai_bottom_sel));
        }
    }

    /**
     * 显示踩之后的view
     */
    public void showDislikeWordView() {
        if (dingcaiIsShowRight()) {
            sobot_tv_dislikeBtn.setSelected(true);
            sobot_tv_dislikeBtn.setEnabled(false);
            sobot_tv_likeBtn.setEnabled(false);
            sobot_tv_likeBtn.setSelected(false);
            sobot_tv_likeBtn.setVisibility(View.GONE);
            sobot_tv_dislikeBtn.setVisibility(View.VISIBLE);
            sobot_right_empty_rl.setVisibility(View.VISIBLE);
            sobot_ll_likeBtn.setVisibility(View.GONE);
            sobot_ll_dislikeBtn.setVisibility(View.VISIBLE);
            //有顶和踩时显示信息显示两行
            msg.setMinHeight(ScreenUtils.dip2px(mContext, 52));

            sobot_tv_bottom_likeBtn.setVisibility(View.GONE);
            sobot_tv_bottom_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_bottom_likeBtn.setVisibility(View.GONE);
            sobot_ll_bottom_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_dislikeBtn.setBackground(mContext.getResources().getDrawable(R.drawable.sobot_chat_dingcai_right_sel));
        } else {
            sobot_tv_bottom_dislikeBtn.setSelected(true);
            sobot_tv_bottom_dislikeBtn.setEnabled(false);
            sobot_tv_bottom_likeBtn.setEnabled(false);
            sobot_tv_bottom_likeBtn.setSelected(false);
            sobot_tv_bottom_likeBtn.setVisibility(View.GONE);
            sobot_tv_bottom_dislikeBtn.setVisibility(View.VISIBLE);
            sobot_ll_bottom_likeBtn.setVisibility(View.GONE);
            sobot_ll_bottom_dislikeBtn.setVisibility(View.VISIBLE);
            sobot_tv_likeBtn.setVisibility(View.GONE);
            sobot_tv_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_likeBtn.setVisibility(View.GONE);
            sobot_ll_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_bottom_dislikeBtn.setBackground(mContext.getResources().getDrawable(R.drawable.sobot_chat_dingcai_bottom_sel));
        }
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_ll_switch) {
            // 换一组
            if (message != null && message.getListSuggestions() != null
                    && message.getListSuggestions().size() > 0) {
                LogUtils.i(message.getCurrentPageNum() + "==================");
                int pageNum = message.getCurrentPageNum() + 1;
                int total = message.getListSuggestions().size();
                int pre = message.getGuideGroupNum();
                if (pre == 0) {
                    pre = 5;
                }
                int maxNum = (total % pre == 0) ? (total / pre) : (total / pre + 1);
                LogUtils.i(maxNum + "=========maxNum=========");
                pageNum = (pageNum >= maxNum) ? 0 : pageNum;
                message.setCurrentPageNum(pageNum);

                LogUtils.i(message.getCurrentPageNum() + "==================");
                resetAnswersList();
            }


        }
    }

    // 查看阅读全文的监听
    public static class ReadAllTextLisenter implements View.OnClickListener {
        private String mUrlContent;
        private Context context;

        public ReadAllTextLisenter(Context context, String urlContent) {
            super();
            this.mUrlContent = urlContent;
            this.context = context;
        }

        @Override
        public void onClick(View arg0) {

            if (!mUrlContent.startsWith("http://")
                    && !mUrlContent.startsWith("https://")) {
                mUrlContent = "http://" + mUrlContent;
            }
            // 内部浏览器
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra("url", mUrlContent);
            context.startActivity(intent);
        }
    }

    // 问题的回答监听
    public static class AnsWerClickLisenter implements View.OnClickListener {

        private String msgContent;
        private String id;
        private ImageView img;
        private String docId;
        private Context context;
        private SobotMsgAdapter.SobotMsgCallBack mMsgCallBack;

        public AnsWerClickLisenter(Context context, String id, String msgContent, ImageView image,
                                   String docId, SobotMsgAdapter.SobotMsgCallBack msgCallBack) {
            super();
            this.context = context;
            this.msgContent = msgContent;
            this.id = id;
            this.img = image;
            this.docId = docId;
            mMsgCallBack = msgCallBack;
        }

        @Override
        public void onClick(View arg0) {
            if (img != null) {
                img.setVisibility(View.GONE);
            }

            if (mMsgCallBack != null) {
                mMsgCallBack.hidePanelAndKeyboard();
                ZhiChiMessageBase msgObj = new ZhiChiMessageBase();
                msgObj.setContent(msgContent);
                msgObj.setId(id);
                mMsgCallBack.sendMessageToRobot(msgObj, 0, 1, docId);
            }
        }
    }

    int getTextSize(TextView view) {

        CharSequence text = view.getText();

        TextPaint paint = view.getPaint();

        int textSize = (int) Layout.getDesiredWidth(text, 0, text.length(), paint);

        return textSize;

    }

    private void setupMsgContent(final Context context, final ZhiChiMessageBase message) {
        if (message.getAnswer() != null && message.getAnswer().getRichList() != null && message.getAnswer().getRichList().size() > 0) {
            LinearLayout.LayoutParams wlayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            sobot_rich_ll.removeAllViews();
            try {
                if (message.getAnswer().getRichList().size() > 1) {
                    //richList 数量大于1个，如果里边有不是卡片的超链接，超链接的上个又是文本的情况，需要单独处理（合并到上个文本后边）
                    //处理后的临时richList,替换旧的richList
                    List<ChatMessageRichListModel> tempRichList = new ArrayList<>();
                    for (int i = 0; i < message.getAnswer().getRichList().size(); i++) {
                        ChatMessageRichListModel richListModel = message.getAnswer().getRichList().get(i);
                        if (richListModel != null) {
                            //如果当前是文本,文本又不是卡片，需要处理
                            if (richListModel.getType() == 0 && richListModel.getShowType() != 1) {
                                ChatMessageRichListModel model = new ChatMessageRichListModel();
                                model.setType(0);
                                if (tempRichList.size() > 0) {
                                    //如果上一个是文本,需要合并当前文本到上个文本后边
                                    ChatMessageRichListModel tempRichListModel = tempRichList.get(tempRichList.size() - 1);
                                    if (tempRichListModel != null && tempRichListModel.getType() == 0) {
                                        if (!TextUtils.isEmpty(richListModel.getName()) && HtmlTools.isHasPatterns(richListModel.getMsg())) {
                                            model.setMsg(tempRichListModel.getMsg() + "<a href=\"" + richListModel.getMsg() + "\">" + richListModel.getName() + "</a>");
                                        } else {
                                            model.setMsg(tempRichListModel.getMsg() + richListModel.getMsg());
                                        }
                                        tempRichList.remove(tempRichList.size() - 1);
                                        tempRichList.add(model);
                                    } else {
                                        tempRichList.add(richListModel);
                                    }
                                } else {
                                    if (!TextUtils.isEmpty(richListModel.getName()) && HtmlTools.isHasPatterns(richListModel.getMsg())) {
                                        //当前是超链接，同时又不是卡片
                                        model.setMsg("<a href=\"" + richListModel.getMsg() + "\">" + richListModel.getName() + "</a>");
                                    } else {
                                        model.setMsg(richListModel.getMsg());
                                    }
                                    tempRichList.add(model);
                                }
                            } else {
                                tempRichList.add(richListModel);
                            }
                        }
                    }
                    if (tempRichList != null && tempRichList.size() > 0) {
                        message.getAnswer().setRichList(tempRichList);
                    }
                }
            } catch (Exception e) {
            }
            for (int i = 0; i < message.getAnswer().getRichList().size(); i++) {
                final ChatMessageRichListModel richListModel = message.getAnswer().getRichList().get(i);
                if (richListModel != null) {
                    //如果最后一个是空行，直接过滤掉不显示
                    if (TextUtils.isEmpty(richListModel.getMsg()) && i == (message.getAnswer().getRichList().size() - 1)) {
                        continue;
                    }
                    // 0：文本，1：图片，2：音频，3：视频，4：文件
                    if (richListModel.getType() == 0) {
                        TextView textView = new TextView(mContext);
                        textView.setTextSize(14);
                        textView.setIncludeFontPadding(false);
                        textView.setLayoutParams(wlayoutParams);
                        textView.setMaxWidth(msgMaxWidth);
                        //设置行间距
                        textView.setLineSpacing(0, 1.1f);
                        if (!TextUtils.isEmpty(richListModel.getName()) && HtmlTools.isHasPatterns(richListModel.getMsg())) {
                            textView.setTextColor(ContextCompat.getColor(mContext, ResourceUtils.getResColorId(mContext, "sobot_color_link")));
                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (SobotOption.newHyperlinkListener != null) {
                                        //如果返回true,拦截;false 不拦截
                                        boolean isIntercept = SobotOption.newHyperlinkListener.onUrlClick(mContext, richListModel.getMsg());
                                        if (isIntercept) {
                                            return;
                                        }
                                    }
                                    Intent intent = new Intent(context, WebViewActivity.class);
                                    intent.putExtra("url", richListModel.getMsg());
                                    context.startActivity(intent);
                                }
                            });
                            textView.setText(richListModel.getName());
                            sobot_rich_ll.addView(textView);
                            if (message.getAnswer().getRichList().size() == 1 && richListModel.getShowType() == 1) {
                                //只有一个，是超链接，并且是卡片形式才显示卡片
                                final View view = LayoutInflater.from(mContext).inflate(ResourceUtils.getResLayoutId(mContext, "sobot_chat_msg_link_card"), null);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ScreenUtils.dip2px(mContext, 240), ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParams.setMargins(0, ScreenUtils.dip2px(mContext, 6), 0, ScreenUtils.dip2px(mContext, 6));
                                view.setLayoutParams(layoutParams);
                                TextView tv_title = view.findViewById(ResourceUtils.getIdByName(context, "id", "tv_title"));
                                tv_title.setText(ResourceUtils.getResString(context, "sobot_parsing"));
                                if (richListModel.getSobotLink() != null) {
                                    tv_title = view.findViewById(ResourceUtils.getIdByName(context, "id", "tv_title"));
                                    TextView tv_des = view.findViewById(ResourceUtils.getIdByName(context, "id", "tv_des"));
                                    ImageView image_link = view.findViewById(ResourceUtils.getIdByName(context, "id", "image_link"));
                                    tv_title.setText(richListModel.getSobotLink().getTitle());
                                    tv_des.setText(TextUtils.isEmpty(richListModel.getSobotLink().getDesc()) ? richListModel.getMsg() : richListModel.getSobotLink().getDesc());
                                    SobotBitmapUtil.display(mContext, richListModel.getSobotLink().getImgUrl(), image_link, ResourceUtils.getDrawableId(mContext, "sobot_link_image"), ResourceUtils.getDrawableId(mContext, "sobot_link_image"));

                                } else {
                                    SobotMsgManager.getInstance(mContext).getZhiChiApi().getHtmlAnalysis(context, richListModel.getMsg(), new StringResultCallBack<SobotLink>() {
                                        @Override
                                        public void onSuccess(SobotLink link) {
                                            if (link != null) {
                                                richListModel.setSobotLink(link);
                                                TextView tv_title = view.findViewById(ResourceUtils.getIdByName(context, "id", "tv_title"));
                                                TextView tv_des = view.findViewById(ResourceUtils.getIdByName(context, "id", "tv_des"));
                                                ImageView image_link = view.findViewById(ResourceUtils.getIdByName(context, "id", "image_link"));
                                                tv_title.setText(link.getTitle());
                                                tv_des.setText(TextUtils.isEmpty(link.getDesc()) ? richListModel.getMsg() : link.getDesc());
                                                SobotBitmapUtil.display(mContext, link.getImgUrl(), image_link, ResourceUtils.getDrawableId(mContext, "sobot_link_image"), ResourceUtils.getDrawableId(mContext, "sobot_link_image"));
                                            }
                                        }

                                        @Override
                                        public void onFailure(Exception e, String s) {
                                            if (view != null) {
                                                TextView tv_title = view.findViewById(R.id.tv_title);
                                                tv_title.setText(richListModel.getMsg());
                                                ImageView image_link = view.findViewById(R.id.image_link);
                                                SobotBitmapUtil.display(mContext, "", image_link, R.drawable.sobot_link_image, R.drawable.sobot_link_image);
                                            }
                                        }
                                    });
                                }
                                sobot_rich_ll.addView(view);
                                view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (SobotOption.newHyperlinkListener != null) {
                                            //如果返回true,拦截;false 不拦截
                                            boolean isIntercept = SobotOption.newHyperlinkListener.onUrlClick(mContext, richListModel.getMsg());
                                            if (isIntercept) {
                                                return;
                                            }
                                        }
                                        Intent intent = new Intent(context, WebViewActivity.class);
                                        intent.putExtra("url", richListModel.getMsg());
                                        context.startActivity(intent);
                                    }
                                });
                            }
                        } else {
                            textView.setTextColor(ContextCompat.getColor(mContext, ResourceUtils.getResColorId(mContext, "sobot_left_msg_text_color")));
                            if (!TextUtils.isEmpty(richListModel.getMsg()) && i == (message.getAnswer().getRichList().size() - 1)) {
                                //如果是richlist的最后一个，把这个的尾部的<br/>都去掉
                                String content = richListModel.getMsg().trim();
                                while (content.length() > 5 && "<br/>".equals(content.substring(content.length() - 5, content.length()))) {
                                    content = content.substring(0, content.length() - 5);
                                }
                                HtmlTools.getInstance(mContext).setRichTextViewText(textView, content, getLinkTextColor());
                            } else {
                                HtmlTools.getInstance(mContext).setRichTextViewText(textView, richListModel.getMsg(), getLinkTextColor());
                            }
                            sobot_rich_ll.addView(textView);
                        }
                    } else if (richListModel.getType() == 1 && HtmlTools.isHasPatterns(richListModel.getMsg())) {
                        LinearLayout.LayoutParams mlayoutParams;
                        try {
                            int pictureWidth = ResourceUtils.getResDimenId(mContext, "sobot_rich_msg_picture_width_dp");
                            int pictureHeight = ResourceUtils.getResDimenId(mContext, "sobot_rich_msg_picture_height_dp");
                            if (pictureWidth == 0) {
                                //如果设置的宽度等于0，默认图片的最大宽度是气泡的最大宽度
                                pictureWidth = msgMaxWidth;
                            }
                            if (pictureWidth > msgMaxWidth) {
                                //如果设置的宽度大于气泡的最大宽度，等比例缩放设置的高度
                                float picbili = (float) pictureWidth / msgMaxWidth;
                                pictureWidth = msgMaxWidth;
                                pictureHeight = (int) (pictureHeight / picbili);
                            }
                            mlayoutParams = new LinearLayout.LayoutParams(pictureWidth, pictureHeight);
                        } catch (Exception e) {
                            e.printStackTrace();
                            mlayoutParams = new LinearLayout.LayoutParams(msgMaxWidth,
                                    ScreenUtils.dip2px(context, 200));
                        }
                        mlayoutParams.setMargins(0, ScreenUtils.dip2px(context, 6), 0, ScreenUtils.dip2px(context, 6));
                        ImageView imageView = new ImageView(mContext);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setLayoutParams(mlayoutParams);
                        SobotBitmapUtil.display(mContext, richListModel.getMsg(), imageView, R.drawable.sobot_default_pic, R.drawable.sobot_default_pic_err);
                        imageView.setOnClickListener(new ImageClickLisenter(context, richListModel.getMsg(), isRight));
                        sobot_rich_ll.addView(imageView);
                    } else if (richListModel.getType() == 3 && HtmlTools.isHasPatterns(richListModel.getMsg())) {
                        View videoView = LayoutInflater.from(mContext).inflate(ResourceUtils.getResLayoutId(mContext, "sobot_chat_msg_item_rich_vedio_view"), null);
                        sobot_rich_ll.addView(videoView);
                        videoView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SobotCacheFile cacheFile = new SobotCacheFile();
                                cacheFile.setFileName(richListModel.getName());
                                cacheFile.setUrl(richListModel.getMsg());
                                cacheFile.setFileType(FileTypeConfig.getFileType(FileUtil.checkFileEndWith(richListModel.getMsg())));
                                cacheFile.setMsgId(message.getMsgId() + richListModel.getMsg());
                                Intent intent = SobotVideoActivity.newIntent(mContext, cacheFile);
                                mContext.startActivity(intent);
                            }
                        });
                    } else if ((richListModel.getType() == 4 || richListModel.getType() == 2)) {
                        View view = LayoutInflater.from(mContext).inflate(ResourceUtils.getResLayoutId(mContext, "sobot_chat_msg_file_l"), null);
                        TextView sobot_file_name = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_file_name"));
                        TextView sobot_file_size = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_file_size"));
                        SobotSectorProgressView sobot_progress = (SobotSectorProgressView) view.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_progress"));
                        sobot_file_name.setText(richListModel.getName());
                        sobot_file_size.setText(TextUtils.isEmpty(richListModel.getFileSize()) ? "" : richListModel.getFileSize());
                        SobotBitmapUtil.display(mContext, ChatUtils.getFileIcon(mContext, FileTypeConfig.getFileType(FileUtil.checkFileEndWith(richListModel.getMsg()))), sobot_progress);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ScreenUtils.dip2px(mContext, 240), ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0, ScreenUtils.dip2px(mContext, 6), 0, ScreenUtils.dip2px(mContext, 6));
                        view.setLayoutParams(layoutParams);
                        sobot_rich_ll.addView(view);
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (richListModel.getType() == 2) {
                                    Intent intent = new Intent(context, WebViewActivity.class);
                                    intent.putExtra("url", richListModel.getMsg());
                                    context.startActivity(intent);
                                } else {
                                    // 打开详情页面
                                    Intent intent = new Intent(mContext, SobotFileDetailActivity.class);
                                    SobotCacheFile cacheFile = new SobotCacheFile();
                                    cacheFile.setFileName(richListModel.getName());
                                    cacheFile.setUrl(richListModel.getMsg());
                                    cacheFile.setFileSize(TextUtils.isEmpty(richListModel.getFileSize()) ? "" : richListModel.getFileSize());
                                    cacheFile.setFileType(FileTypeConfig.getFileType(FileUtil.checkFileEndWith(richListModel.getMsg())));
                                    cacheFile.setMsgId(message.getMsgId() + richListModel.getMsg());
                                    intent.putExtra(ZhiChiConstant.SOBOT_INTENT_DATA_SELECTED_FILE, cacheFile);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mContext.startActivity(intent);
                                }
                            }
                        });
                    }
                }
            }
            sobot_rich_ll.setVisibility(View.VISIBLE);
            msg.setVisibility(View.GONE);
        } else {
            sobot_rich_ll.setVisibility(View.GONE);
            if (message.getAnswer() != null && !TextUtils.isEmpty(message.getAnswer().getMsg())) {
                msg.setVisibility(View.VISIBLE);
                String robotAnswer = "";
                if ("9".equals(message.getAnswer().getMsgType())) {
                    if (message.getAnswer().getMultiDiaRespInfo() != null) {
                        robotAnswer = message.getAnswer().getMultiDiaRespInfo().getAnswer();
                    }

                } else {
                    robotAnswer = message.getAnswer().getMsg();
                }
                HtmlTools.getInstance(context).setRichText(msg, robotAnswer, getLinkTextColor());
            } else {
                msg.setVisibility(View.GONE);
            }
        }
    }

}