package com.sobot.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sobot.chat.MarkConfig;
import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.adapter.base.SobotBaseAdapter;
import com.sobot.chat.api.apiUtils.GsonUtil;
import com.sobot.chat.api.model.SobotEvaluateModel;
import com.sobot.chat.api.model.SobotMultiDiaRespInfo;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.api.model.ZhiChiReplyAnswer;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.DateUtil;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.VersionUtils;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.viewHolder.ArticleMessageHolder;
import com.sobot.chat.viewHolder.CardMessageHolder;
import com.sobot.chat.viewHolder.ConsultMessageHolder;
import com.sobot.chat.viewHolder.CusEvaluateMessageHolder;
import com.sobot.chat.viewHolder.FileMessageHolder;
import com.sobot.chat.viewHolder.ImageMessageHolder;
import com.sobot.chat.viewHolder.LocationMessageHolder;
import com.sobot.chat.viewHolder.MiniProgramMessageHolder;
import com.sobot.chat.viewHolder.NoticeMessageHolder;
import com.sobot.chat.viewHolder.OrderCardMessageHolder;
import com.sobot.chat.viewHolder.RemindMessageHolder;
import com.sobot.chat.viewHolder.RetractedMessageHolder;
import com.sobot.chat.viewHolder.RichTextMessageHolder;
import com.sobot.chat.viewHolder.RobotAnswerItemsMsgHolder;
import com.sobot.chat.viewHolder.RobotKeyWordMessageHolder;
import com.sobot.chat.viewHolder.RobotQRMessageHolder;
import com.sobot.chat.viewHolder.RobotTemplateMessageHolder1;
import com.sobot.chat.viewHolder.RobotTemplateMessageHolder2;
import com.sobot.chat.viewHolder.RobotTemplateMessageHolder3;
import com.sobot.chat.viewHolder.RobotTemplateMessageHolder4;
import com.sobot.chat.viewHolder.RobotTemplateMessageHolder5;
import com.sobot.chat.viewHolder.RobotTemplateMessageHolder6;
import com.sobot.chat.viewHolder.SobotChatMsgItemSDKHistoryR;
import com.sobot.chat.viewHolder.SobotMuitiLeavemsgMessageHolder;
import com.sobot.chat.viewHolder.SystemMessageHolder;
import com.sobot.chat.viewHolder.TextMessageHolder;
import com.sobot.chat.viewHolder.VideoMessageHolder;
import com.sobot.chat.viewHolder.VoiceMessageHolder;
import com.sobot.chat.viewHolder.base.MessageHolderBase;

import java.util.List;

/**
 * Created by jinxl on 2017/3/8.
 */
public class SobotMsgAdapter extends SobotBaseAdapter<ZhiChiMessageBase> {

    private static final String[] layoutRes = {
            "sobot_chat_msg_item_txt_l",//文本消息左边的布局文件
            "sobot_chat_msg_item_txt_r",//文本消息右边的布局文件
            "sobot_chat_msg_item_tip",//消息提醒的布局文件
            "sobot_chat_msg_item_rich",//富文本消息布局文件
            "sobot_chat_msg_item_imgt_l",//图片消息左边的布局文件
            "sobot_chat_msg_item_imgt_r",//图片消息右边的布局文件
            "sobot_chat_msg_item_audiot_r",//语音消息右边的布局文件
            "sobot_chat_msg_item_consult",//商品咨询内容的布局文件
            "sobot_chat_msg_item_evaluate",//客服邀请评价的布局文件
            "sobot_chat_msg_item_template1_l",//机器人  多轮会话模板 1
            "sobot_chat_msg_item_template2_l",//机器人  多轮会话模板 2
            "sobot_chat_msg_item_template3_l",//机器人  多轮会话模板 3
            "sobot_chat_msg_item_sdk_history_r",//SDK  历史记录中多轮会话使用的布局
            "sobot_chat_msg_item_template4_l",//机器人  多轮会话模板 4
            "sobot_chat_msg_item_template5_l",//机器人  多轮会话模板 5
            "sobot_chat_msg_item_question_recommend",//热点问题列表
            "sobot_chat_msg_item_retracted_msg",//消息撤回
            "sobot_chat_msg_item_robot_answer_items_l",//多轮会话模板 1511类型  显示的view
            "sobot_chat_msg_item_robot_keyword_items_l",//机器人关键字转人工 布局
            "sobot_chat_msg_item_file_l",//文件消息左边的布局文件
            "sobot_chat_msg_item_file_r",//文件消息右边的布局文件
            "sobot_chat_msg_item_video_r",//小视频右边的布局文件
            "sobot_chat_msg_item_location_r",//位置信息的布局文件
            "sobot_chat_msg_item_notice",//通告消息的布局文件
            "sobot_chat_msg_item_card_r",//商品卡片右侧信息的布局文件
            "sobot_chat_msg_item_order_card_r",//订单卡片右侧消息
            "sobot_chat_msg_item_order_card_l",//订单卡片左侧消息
            "sobot_chat_msg_item_card_l",//商品卡片左侧信息的布局文件
            "sobot_chat_msg_item_template6_l",//机器人  多轮会话模板 6
            "sobot_chat_msg_item_system_tip",//防诈骗系统消息的布局文件
            "sobot_chat_msg_item_video_l",//小视频左边的布局文件
            "sobot_chat_msg_item_muiti_leave_msg",//小视频左边的布局文件
            "sobot_chat_msg_item_mini_program_card_l",//小程序卡片左侧消息
            "sobot_chat_msg_item_article_card_l"//文章卡片左侧消息
    };

    /**
     * 非法消息类型
     */
    private static final int MSG_TYPE_ILLEGAL = 0;
    /**
     * 收到的文本消息
     */
    public static final int MSG_TYPE_TXT_L = 0;
    /**
     * 发送的文本消息
     */
    public static final int MSG_TYPE_TXT_R = 1;
    /**
     * 发送的消息提醒
     */
    public static final int MSG_TYPE_TIP = 2;
    /**
     * 收到富文本消息
     */
    public static final int MSG_TYPE_RICH = 3;
    /**
     * 收到图片消息
     */
    public static final int MSG_TYPE_IMG_L = 4;
    /**
     * 发送图片消息
     */
    public static final int MSG_TYPE_IMG_R = 5;
    /**
     * 语音消息
     */
    public static final int MSG_TYPE_AUDIO_R = 6;
    /**
     * 发送商品咨询
     */
    public static final int MSG_TYPE_CONSULT = 7;
    /**
     * 客服主动邀请客户评价
     */
    public static final int MSG_TYPE_CUSTOM_EVALUATE = 8;
    /**
     * 机器人  多轮会话模板 1
     */
    public static final int MSG_TYPE_ROBOT_TEMPLATE1 = 9;
    /**
     * 机器人  多轮会话模板 2
     */
    public static final int MSG_TYPE_ROBOT_TEMPLATE2 = 10;
    /**
     * 机器人  多轮会话模板 3
     */
    public static final int MSG_TYPE_ROBOT_TEMPLATE3 = 11;
    /**
     * 多轮会话  右边类型
     */
    public static final int MSG_TYPE_MULTI_ROUND_R = 12;
    /**
     * 机器人  多轮会话模板 4
     */
    public static final int MSG_TYPE_ROBOT_TEMPLATE4 = 13;
    /**
     * 机器人  多轮会话模板 5  无模版情况下显示的view
     */
    public static final int MSG_TYPE_ROBOT_TEMPLATE5 = 14;
    /**
     * 机器人热点问题引导
     */
    public static final int MSG_TYPE_ROBOT_QUESTION_RECOMMEND = 15;
    /**
     * 消息撤回
     */
    public static final int MSG_TYPE_RETRACTED_MSG = 16;
    /**
     * 机器人  多轮会话模板 1511类型  显示的view
     */
    public static final int MSG_TYPE_ROBOT_ANSWER_ITEMS = 17;
    /**
     * 机器人关键字转人工 布局类型
     */
    public static final int MSG_TYPE_ROBOT_KEYWORD_ITEMS = 18;
    /**
     * 收到的文件消息
     */
    public static final int MSG_TYPE_FILE_L = 19;
    /**
     * 发送的文件消息
     */
    public static final int MSG_TYPE_FILE_R = 20;
    /**
     * 发送的小视频
     */
    public static final int MSG_TYPE_VIDEO_R = 21;
    /**
     * 发送的位置信息
     */
    public static final int MSG_TYPE_LOCATION_R = 22;
    /**
     * 通告信息
     */
    public static final int MSG_TYPE_NOTICE = 23;
    /**
     * 商品卡片右侧
     */
    public static final int MSG_TYPE_CARD_R = 24;
    /**
     * 订单卡片右侧消息
     */
    public static final int MSG_TYPE_ROBOT_ORDERCARD_R = 25;
    /**
     * 订单卡片左侧侧消息
     */
    public static final int MSG_TYPE_ROBOT_ORDERCARD_L = 26;
    /**
     * 商品卡片左侧
     */
    public static final int MSG_TYPE_CARD_L = 27;
    /**
     * 机器人  多轮会话模板 6(为松果添加的)
     */
    public static final int MSG_TYPE_ROBOT_TEMPLATE6 = 28;

    /**
     * 系统消息 防诈骗
     */
    public static final int MSG_TYPE_FRAUD_PREVENTION = 29;

    /**
     * 客服发送的视频  左侧
     */
    public static final int MSG_TYPE_VIDEO_L = 30;

    /**
     * 多伦节点留言
     */
    public static final int MSG_TYPE_MUITI_LEAVE_MSG_R = 31;

    /**
     * 小程序卡片左侧消息
     */
    public static final int MSG_TYPE_MINIPROGRAM_CARD_L = 32;

    /**
     * 文章卡片左侧消息
     */
    public static final int MSG_TYPE_ARTICLE_CARD_L = 33;


    private SobotMsgCallBack mMsgCallBack;

    public SobotMsgAdapter(Activity context, List<ZhiChiMessageBase> list, SobotMsgCallBack msgCallBack) {
        super(context, list);
        mMsgCallBack = msgCallBack;
    }

    public void addData(List<ZhiChiMessageBase> moreList) {
        if (moreList == null) {
            return;
        }
        setDefaultCid(moreList);
        long previousMsgTime = 0;
        String previousMsgSenderName = "";
        for (int i = 0; i < moreList.size(); i++) {
            ZhiChiMessageBase base = moreList.get(i);
            //相邻两条消息是同一个人发的，并且时间相隔1分钟，不显示头像昵称
            if (previousMsgTime != 0 &&
                    !TextUtils.isEmpty(base.getT())
                    && ((Long.parseLong(base.getT()) - previousMsgTime) < (1000 * 60))
                    && !TextUtils.isEmpty(previousMsgSenderName)
                    && previousMsgSenderName.equals(base.getSenderType())) {
                base.setShowFaceAndNickname(false);
            } else {
                base.setShowFaceAndNickname(true);
            }
            if (base.getT() != null) {
                try {
                    previousMsgTime = Long.parseLong(base.getT());
                } catch (Exception e) {

                }
            }
            previousMsgSenderName = base.getSenderType();
        }
        list.addAll(0, moreList);
    }

    public void addData(ZhiChiMessageBase message) {
        if (message == null) {
            return;
        }
        if (message.getAction() != null && ZhiChiConstant.action_remind_connt_success.equals(message
                .getAction())) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getSugguestionsFontColor() != 1) {
                    list.get(i).setSugguestionsFontColor(1);
                }
            }
        }

        removeByAction(message, ZhiChiConstant.action_remind_no_service, ZhiChiConstant
                .action_remind_no_service, true);

        removeByAction(message, ZhiChiConstant.action_remind_info_paidui, ZhiChiConstant.action_remind_info_paidui, true);

        removeByAction(message, ZhiChiConstant.action_remind_info_paidui, ZhiChiConstant.action_remind_info_post_msg, true);

        removeByAction(message, ZhiChiConstant.action_remind_connt_success, ZhiChiConstant
                .action_remind_info_paidui, false);

        removeByAction(message, ZhiChiConstant.action_remind_info_post_msg, ZhiChiConstant.action_remind_info_post_msg, true);

        removeByAction(message, ZhiChiConstant.action_remind_connt_success, ZhiChiConstant
                .action_remind_info_post_msg, false);

        removeByAction(message, ZhiChiConstant.action_consultingContent_info, ZhiChiConstant
                .action_consultingContent_info, false);

        removeByAction(message, ZhiChiConstant.sobot_outline_leverByManager, ZhiChiConstant.sobot_outline_leverByManager, true);

        removeByAction(message, ZhiChiConstant.action_custom_evaluate, ZhiChiConstant.action_custom_evaluate, true);

        //  转人工后移除点踩后出现的转人工提示语消息
        removeByAction(message, ZhiChiConstant.action_remind_connt_success, ZhiChiConstant
                .action_remind_info_zhuanrengong, false);

        //  转人工后移除继续排队的提示语消息
        removeByAction(message, ZhiChiConstant.action_remind_connt_success, ZhiChiConstant
                .action_remind_keep_queuing, false);

        if (message.getAction() != null && message.getAction().equals(ZhiChiConstant.action_remind_past_time)
                && message.getAnswer() != null && ZhiChiConstant.sobot_remind_type_outline == message.getAnswer().getRemindType()) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getAction() != null) {
                    if (list.get(i).getAction().equals(ZhiChiConstant.action_remind_past_time) && message.getAnswer() != null
                            && ZhiChiConstant.sobot_remind_type_outline == message.getAnswer().getRemindType()) {
                        list.remove(i);
                        message.setShake(true);
                    }
                }
            }
        }

        justAddData(message);
    }

    public void justAddData(ZhiChiMessageBase message) {
        if (message == null) {
            return;
        }
        String lastCid = SharedPreferencesUtil.getStringData(context, "lastCid", "");
        setDefaultCid(lastCid, message);
        if (message.getAnswers() != null && message.getAnswers().size() > 0) {
            for (int i = 0; i < message.getAnswers().size(); i++) {
                ZhiChiMessageBase zhiChiMessageBase = ChatUtils.clone(message);
                if (zhiChiMessageBase != null) {
                    ZhiChiReplyAnswer answer = message.getAnswers().get(i);
                    zhiChiMessageBase.setAnswer(answer);
                    if (i != (message.getAnswers().size() - 1)) {
                        //只有最后一个显示顶踩
                        zhiChiMessageBase.setRevaluateState(0);
                        //只有最后一个显示转人工按钮
                        zhiChiMessageBase.setShowTransferBtn(false);
                        //只有最后一个显示关联问题
                        zhiChiMessageBase.setSugguestions(null);
                        zhiChiMessageBase.setListSuggestions(null);
                        zhiChiMessageBase.setStripe("");
                    }
                    addMsg(zhiChiMessageBase);
                }
            }
        } else {
            addMsg(message);
        }

    }

    private void addMsg(ZhiChiMessageBase message) {
        if (message == null) {
            return;
        }
        try {
            if (list.size() > 0 && (list.get(list.size() - 1) != null) && !TextUtils.isEmpty(list.get(list.size() - 1).getT())) {
                long previousMsgTime = Long.parseLong(list.get(list.size() - 1).getT());
                String previousMsgSenderName = list.get(list.size() - 1).getSenderName();
                //相邻两条消息是同一个人发的，并且时间相隔1分钟，不显示头像昵称
                if (previousMsgTime != 0 &&
                        !TextUtils.isEmpty(message.getT())
                        && ((Long.parseLong(message.getT()) - previousMsgTime) < (1000 * 60))
                        && !TextUtils.isEmpty(previousMsgSenderName)
                        && previousMsgSenderName.equals(message.getSenderName())) {
                    message.setShowFaceAndNickname(false);
                } else {
                    message.setShowFaceAndNickname(true);
                }
            }
        } catch (Exception e) {
        }
        list.add(message);
    }

    /**
     * 删除已有的数据
     *
     * @param message 当前的数据
     * @param when    当前数据类型（action）=when时   才进行删除操作
     * @param element 删除元素类型（action）
     */
    private void removeByAction(ZhiChiMessageBase message, String when, String element, boolean
            isShake) {
        if (message.getAction() != null && message.getAction().equals(when)) {
            //倒叙判断，然后删
            for (int i = list.size() - 1; i >= 0; i--) {
                if (list.get(i).getAction() != null) {
                    if (list.get(i).getAction().equals(element)) {
                        list.remove(i);
                        message.setShake(isShake);
                    }
                }
            }
        }
    }

    /**
     * 删除已有的数据
     *
     * @param when 当前数据类型（action）=when时   才进行删除操作
     */
    public void removeByAction(String when) {
        //倒叙判断，然后删
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).getAction() != null) {
                if (list.get(i).getAction().equals(when)) {
                    list.remove(i);
                }
            }
        }
    }

    public void addDataBefore(ZhiChiMessageBase message) {
        String lastCid = SharedPreferencesUtil.getStringData(context, "lastCid", "");
        setDefaultCid(lastCid, message);
        list.add(0, message);
    }

    public void addMessage(int position, ZhiChiMessageBase message) {
        String lastCid = SharedPreferencesUtil.getStringData(context, "lastCid", "");
        setDefaultCid(lastCid, message);
        list.add(position, message);
    }

    /**
     * 给没有cid的消息添加默认的cid
     */
    private void setDefaultCid(String lastCid, ZhiChiMessageBase message) {
        ZhiChiReplyAnswer answer = message.getAnswer();
        //没有更多记录的提醒不用添加
        if (!(answer != null && answer.getRemindType() == ZhiChiConstant.sobot_remind_type_nomore)) {
            if (message.getCid() == null) {
                message.setCid(lastCid);
            }
        }
    }

    /**
     * 给没有cid的消息添加默认的cid
     */
    private void setDefaultCid(List<ZhiChiMessageBase> messages) {
        String lastCid = SharedPreferencesUtil.getStringData(context, "lastCid", "");
        for (int i = 0; i < messages.size(); i++) {
            setDefaultCid(lastCid, messages.get(i));
        }
    }

    public void updateMsgInfoById(String id, int senderState, int progressBar) {
        ZhiChiMessageBase info = getMsgInfo(id);
        if (info != null && info.getSendSuccessState() != ZhiChiConstant.MSG_SEND_STATUS_SUCCESS) {
            info.setSendSuccessState(senderState);
            info.setProgressBar(progressBar);
        }
    }


    public void updateVoiceStatusById(String id, int sendStatus, String duration) {
        ZhiChiMessageBase info = getMsgInfo(id);
        if (info != null) {
            info.setSendSuccessState(sendStatus);
            if (!TextUtils.isEmpty(duration) && info.getAnswer() != null) {
                info.getAnswer().setDuration(duration);
            }
        }
    }

    public void updateDataStateById(String id, ZhiChiMessageBase data) {
        ZhiChiMessageBase info = getMsgInfo(id);
        if (info != null) {
            info.setSendSuccessState(data.getSendSuccessState());
            info.setSentisive(data.getSentisive());
            info.setSentisiveExplain(data.getSentisiveExplain());
            info.setClickCancleSend(data.isClickCancleSend());
            info.setShowSentisiveSeeAll(data.isShowSentisiveSeeAll());
            info.setDesensitizationWord(data.getDesensitizationWord());
        }
    }

    public void updateDataById(String id, ZhiChiMessageBase data) {
        ZhiChiMessageBase info = getMsgInfo(id);
        if (info != null) {
            info.setAnswer(data.getAnswer());
            info.setSenderType(data.getSenderType());
            info.setSendSuccessState(data.getSendSuccessState());
        }
    }

    public void cancelVoiceUiById(String id) {
        ZhiChiMessageBase info = getMsgInfo(id);
        if (info != null && info.getSendSuccessState() == ZhiChiConstant.MSG_SEND_STATUS_ANIM) {
            list.remove(info);
        }
    }


    //通过msgid 移除该消息
    public void removeByMsgId(String id) {
        ZhiChiMessageBase info = getMsgInfo(id);
        if (info != null) {
            list.remove(info);
        }
    }

    public void updatePicStatusById(String id, int sendStatus) {
        ZhiChiMessageBase info = getMsgInfo(id);
        if (info != null) {
            info.setSendSuccessState(sendStatus);
        }
    }

    private ZhiChiMessageBase getMsgInfo(String id) {
        for (int i = list.size() - 1; i >= 0; i--) {
            ZhiChiMessageBase msgInfo = list.get(i);
            if (msgInfo == null) {
                continue;
            }
            if (msgInfo.getId() != null && msgInfo.getId().equals(id)) {
                return msgInfo;
            }
        }
        return null;
    }

    public ZhiChiMessageBase getMsgInfoByMsgId(String msgId) {
        for (int i = list.size() - 1; i >= 0; i--) {
            ZhiChiMessageBase msgInfo = list.get(i);
            if (msgInfo == null) {
                continue;
            }
            if (msgInfo.getMsgId() != null && msgInfo.getMsgId().equals(msgId)) {
                return msgInfo;
            }
        }
        return null;
    }

    public int getMsgInfoPosition(String id) {
        int position = 0;
        for (Object obj : list) {
            position++;
            if (!(obj instanceof ZhiChiMessageBase)) {
                continue;
            }
            ZhiChiMessageBase msgInfo = (ZhiChiMessageBase) obj;
            if (msgInfo.getId() != null && msgInfo.getId().equals(id)) {
                return position;
            }
        }
        return list.size() - 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ZhiChiMessageBase message = list.get(position);
        if (message != null) {
            int itemType = getItemViewType(position);
            convertView = initView(convertView, itemType, position, message);
            MessageHolderBase holder = (MessageHolderBase) convertView.getTag();
            holder.setMsgCallBack(mMsgCallBack);
            handerRemindTiem(holder, position);
            holder.applyCustomUI();//设置UI
            holder.bindZhiChiMessageBase(message);//设置message
            holder.initNameAndFace(itemType);
            holder.bindData(context, message);
        }
        return convertView;
    }

    private View initView(View convertView, int itemType, int position, final ZhiChiMessageBase message) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(ResourceUtils.getIdByName(context, "layout", layoutRes[itemType]), null);
            MessageHolderBase holder;
            switch (itemType) {
                case MSG_TYPE_TXT_L:
                case MSG_TYPE_TXT_R: {
                    holder = new TextMessageHolder(context, convertView);
                    if (itemType == MSG_TYPE_TXT_L) {
                        holder.setRight(false);
                    } else {
                        holder.setRight(true);
                    }
                    break;
                }
                case MSG_TYPE_TIP: {
                    holder = new RemindMessageHolder(context, convertView);
                    break;
                }
                case MSG_TYPE_RICH: {
                    holder = new RichTextMessageHolder(context, convertView);
                    break;
                }
                case MSG_TYPE_IMG_L:
                case MSG_TYPE_IMG_R: {
                    holder = new ImageMessageHolder(context, convertView);
                    if (itemType == MSG_TYPE_IMG_L) {
                        holder.setRight(false);
                    } else {
                        holder.setRight(true);
                    }
                    break;
                }
                case MSG_TYPE_AUDIO_R: {
                    holder = new VoiceMessageHolder(context, convertView);
                    holder.setRight(true);
                    break;
                }
                case MSG_TYPE_CONSULT: {
                    holder = new ConsultMessageHolder(context, convertView);
                    break;
                }
                case MSG_TYPE_CUSTOM_EVALUATE: {
                    holder = new CusEvaluateMessageHolder(context, convertView);
                    break;
                }
                case MSG_TYPE_ROBOT_TEMPLATE1: {
                    holder = new RobotTemplateMessageHolder1(context, convertView);
                    break;
                }
                case MSG_TYPE_ROBOT_TEMPLATE2: {
                    holder = new RobotTemplateMessageHolder2(context, convertView);
                    break;
                }
                case MSG_TYPE_ROBOT_TEMPLATE3: {
                    holder = new RobotTemplateMessageHolder3(context, convertView);
                    break;
                }
                case MSG_TYPE_ROBOT_TEMPLATE4:
                    holder = new RobotTemplateMessageHolder4(context, convertView);
                    break;
                case MSG_TYPE_ROBOT_TEMPLATE5:
                    holder = new RobotTemplateMessageHolder5(context, convertView);
                    break;
                case MSG_TYPE_ROBOT_TEMPLATE6:
                    holder = new RobotTemplateMessageHolder6(context, convertView);
                    break;
                case MSG_TYPE_ROBOT_KEYWORD_ITEMS:
                    holder = new RobotKeyWordMessageHolder(context, convertView);
                    break;
                case MSG_TYPE_ROBOT_ANSWER_ITEMS:
                    holder = new RobotAnswerItemsMsgHolder(context, convertView);
                    break;
                case MSG_TYPE_MULTI_ROUND_R:
                    holder = new SobotChatMsgItemSDKHistoryR(context, convertView);
                    break;
                case MSG_TYPE_ROBOT_QUESTION_RECOMMEND:
                    holder = new RobotQRMessageHolder(context, convertView);
                    break;
                case MSG_TYPE_RETRACTED_MSG:
                    holder = new RetractedMessageHolder(context, convertView);
                    break;
                case MSG_TYPE_FILE_L:
                case MSG_TYPE_FILE_R: {
                    holder = new FileMessageHolder(context, convertView);
                    if (itemType == MSG_TYPE_FILE_L) {
                        holder.setRight(false);
                    } else {
                        holder.setRight(true);
                    }
                    break;
                }
                case MSG_TYPE_VIDEO_L:
                case MSG_TYPE_VIDEO_R: {
                    holder = new VideoMessageHolder(context, convertView);
                    if (itemType == MSG_TYPE_VIDEO_L) {
                        holder.setRight(false);
                    } else {
                        holder.setRight(true);
                    }
                    break;
                }
                case MSG_TYPE_LOCATION_R: {
                    holder = new LocationMessageHolder(context, convertView);
                    holder.setRight(true);
                    break;
                }
                case MSG_TYPE_NOTICE:
                    holder = new NoticeMessageHolder(context, convertView);
                    break;
                case MSG_TYPE_CARD_L:
                case MSG_TYPE_CARD_R: {
                    holder = new CardMessageHolder(context, convertView);
                    if (itemType == MSG_TYPE_CARD_L) {
                        holder.setRight(false);
                    } else {
                        holder.setRight(true);
                    }
                    break;
                }
                case MSG_TYPE_ROBOT_ORDERCARD_L:
                case MSG_TYPE_ROBOT_ORDERCARD_R: {
                    holder = new OrderCardMessageHolder(context, convertView);
                    if (itemType == MSG_TYPE_ROBOT_ORDERCARD_L) {
                        holder.setRight(false);
                    } else {
                        holder.setRight(true);
                    }
                    break;
                }
                case MSG_TYPE_FRAUD_PREVENTION: {
                    holder = new SystemMessageHolder(context, convertView);
                    break;
                }
                case MSG_TYPE_MUITI_LEAVE_MSG_R: {
                    holder = new SobotMuitiLeavemsgMessageHolder(context, convertView);
                    break;
                }
                case MSG_TYPE_MINIPROGRAM_CARD_L: {
                    holder = new MiniProgramMessageHolder(context, convertView);
                    break;
                }
                case MSG_TYPE_ARTICLE_CARD_L: {
                    holder = new ArticleMessageHolder(context, convertView);
                    break;
                }
                default: {
                    holder = new TextMessageHolder(context, convertView);
                    break;
                }
            }
            convertView.setTag(holder);
        } else {//不复用布局

            switch (itemType) {
                case MSG_TYPE_ROBOT_TEMPLATE1: {
                    convertView = LayoutInflater.from(context).inflate(ResourceUtils.getIdByName(context, "layout", layoutRes[itemType]), null);
                    MessageHolderBase holder;
                    holder = new RobotTemplateMessageHolder1(context, convertView);
                    convertView.setTag(holder);
                    break;
                }
                case MSG_TYPE_ROBOT_TEMPLATE2: {
                    convertView = LayoutInflater.from(context).inflate(ResourceUtils.getIdByName(context, "layout", layoutRes[itemType]), null);
                    MessageHolderBase holder;
                    holder = new RobotTemplateMessageHolder2(context, convertView);
                    convertView.setTag(holder);
                    break;
                }
                case MSG_TYPE_ROBOT_TEMPLATE3: {
                    convertView = LayoutInflater.from(context).inflate(ResourceUtils.getIdByName(context, "layout", layoutRes[itemType]), null);
                    MessageHolderBase holder;
                    holder = new RobotTemplateMessageHolder3(context, convertView);
                    convertView.setTag(holder);
                    break;
                }
            }

        }
        return convertView;
    }

    /**
     * @return 返回有多少种UI布局样式
     */
    @Override
    public int getViewTypeCount() {
        if (layoutRes.length > 0) {
            return layoutRes.length;
        }
        return super.getViewTypeCount();
    }

    @Override
    public ZhiChiMessageBase getItem(int position) {
        if (position < 0 || position >= list.size()) {
            return null;
        }
        return list.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        try {
            ZhiChiMessageBase message = getItem(position);
            if (message == null) {
                return MSG_TYPE_ILLEGAL;
            } else if (message.isRetractedMsg()) {
                return MSG_TYPE_RETRACTED_MSG;
            }
            int senderType = -1;
            if (!TextUtils.isEmpty(message.getSenderType())) {
                senderType = Integer.parseInt(message.getSenderType());
            } else {
                if (ZhiChiConstant.message_type_fraud_prevention == Integer
                        .parseInt(message.getAction())) {
                    //防诈骗消息
                    return MSG_TYPE_FRAUD_PREVENTION;
                }
            }
            if (ZhiChiConstant.message_sender_type_customer == senderType
                    || ZhiChiConstant.message_sender_type_robot == senderType
                    || ZhiChiConstant.message_sender_type_service == senderType) {
                // 发送人类型 0是SDK客户  1是机器人  2 是客服
                // 这些都是平台传过来的消息
                if (message.getAnswer() != null) {
                    if (ZhiChiConstant.message_type_text == Integer
                            .parseInt(message.getAnswer().getMsgType())) {
                        if (ZhiChiConstant.message_sender_type_robot == Integer
                                .parseInt(message.getSenderType())) {
                            return MSG_TYPE_RICH;
                        } else if (ZhiChiConstant.message_sender_type_service == Integer
                                .parseInt(message.getSenderType())) {
                            return MSG_TYPE_TXT_L;
                        } else if (ZhiChiConstant.message_sender_type_customer == Integer
                                .parseInt(message.getSenderType())) {
                            return MSG_TYPE_TXT_R;
                        }
                    } else if (ZhiChiConstant.message_type_pic == Integer.parseInt(message
                            .getAnswer().getMsgType())) {
                        if (ZhiChiConstant.message_sender_type_customer == Integer
                                .parseInt(message.getSenderType())) {
                            return MSG_TYPE_IMG_R;
                        } else {
                            return MSG_TYPE_IMG_L;
                        }
                    } else if (ZhiChiConstant.message_type_voice == Integer
                            .parseInt(message.getAnswer().getMsgType())) {
                        if (ZhiChiConstant.message_sender_type_customer == Integer
                                .parseInt(message.getSenderType())) {
                            if (message.getAnswer() != null && !TextUtils.isEmpty(message.getAnswer().getMsgTransfer())) {
                                return MSG_TYPE_TXT_R;
                            }
                            return MSG_TYPE_AUDIO_R;
                        } else {
                            return MSG_TYPE_ILLEGAL;
                        }

                    } else if (ZhiChiConstant.message_type_emoji == Integer
                            .parseInt(message.getAnswer().getMsgType())) {
                        // 富文本格式
                        if (ZhiChiConstant.message_sender_type_robot == Integer
                                .parseInt(message.getSenderType())
                                || ZhiChiConstant.message_sender_type_service == Integer
                                .parseInt(message.getSenderType())) {
                            return MSG_TYPE_RICH;
                        }
                    } else if (ZhiChiConstant.message_type_textAndPic == Integer
                            .parseInt(message.getAnswer().getMsgType())) {
                        //富文本中有图片
                        if (ZhiChiConstant.message_sender_type_robot == Integer
                                .parseInt(message.getSenderType())
                                || ZhiChiConstant.message_sender_type_service == Integer
                                .parseInt(message.getSenderType())) {
                            return MSG_TYPE_RICH;
                        }
                    } else if (ZhiChiConstant.message_type_textAndText == Integer
                            .parseInt(message.getAnswer().getMsgType())) {
                        //富文本中纯文字
                        if (ZhiChiConstant.message_sender_type_robot == Integer
                                .parseInt(message.getSenderType())
                                || ZhiChiConstant.message_sender_type_service == Integer
                                .parseInt(message.getSenderType())) {
                            return MSG_TYPE_RICH;
                        }
                    } else if (Integer.parseInt(message.getAnswer().getMsgType()) == ZhiChiConstant.message_type_reply) {
                        return MSG_TYPE_RICH;
                    } else if (Integer.parseInt(message.getAnswer().getMsgType()) == ZhiChiConstant.message_type_reply_multi_round) {
                        return MSG_TYPE_RICH;
                    } else if (ZhiChiConstant.message_type_history_custom.equals(message.getAnswer().getMsgType())) {
                        return MSG_TYPE_MULTI_ROUND_R;
                    } else if (ZhiChiConstant.message_type_history_robot.equals(message.getAnswer().getMsgType())) {
                        if (GsonUtil.isMultiRoundSession(message) && message.getAnswer().getMultiDiaRespInfo() != null) {
                            SobotMultiDiaRespInfo multiDiaRespInfo = message.getAnswer().getMultiDiaRespInfo();
                            if ("1511".equals(message.getAnswerType())) {
                                return MSG_TYPE_ROBOT_ANSWER_ITEMS;
                            }
                            if ("1522".equals(message.getAnswerType())) {
                                return MSG_TYPE_RICH;
                            }
                            if (multiDiaRespInfo.getInputContentList() != null && multiDiaRespInfo.getInputContentList().length > 0) {
                                return MSG_TYPE_ROBOT_TEMPLATE2;
                            }
                            if (!TextUtils.isEmpty(multiDiaRespInfo.getTemplate())) {
                                if ("0".equals(multiDiaRespInfo.getTemplate())) {
                                    return MSG_TYPE_ROBOT_TEMPLATE1;
                                } else if ("1".equals(multiDiaRespInfo.getTemplate())) {
                                    return MSG_TYPE_ROBOT_TEMPLATE2;
                                } else if ("2".equals(multiDiaRespInfo.getTemplate())) {
                                    return MSG_TYPE_ROBOT_TEMPLATE3;
                                } else if ("3".equals(multiDiaRespInfo.getTemplate())) {
                                    return MSG_TYPE_ROBOT_TEMPLATE4;
                                } else if ("4".equals(multiDiaRespInfo.getTemplate())) {
                                    return MSG_TYPE_ROBOT_TEMPLATE5;
                                } else if ("99".equals(multiDiaRespInfo.getTemplate())) {
                                    return MSG_TYPE_ROBOT_TEMPLATE6;
                                }
                            } else {
                                if ((multiDiaRespInfo.getInterfaceRetList() == null || multiDiaRespInfo.getInterfaceRetList().size() <= 0) && (multiDiaRespInfo.getInputContentList() == null || multiDiaRespInfo.getInputContentList().length <= 0)) {
                                    return MSG_TYPE_ROBOT_TEMPLATE5;
                                }
                                return MSG_TYPE_ROBOT_TEMPLATE2;
                            }
                        }
                    } else if (ZhiChiConstant.message_type_file.equals(message.getAnswer().getMsgType())) {
                        if (ZhiChiConstant.message_sender_type_customer == Integer
                                .parseInt(message.getSenderType())) {
                            return MSG_TYPE_FILE_R;
                        } else {
                            return MSG_TYPE_FILE_L;
                        }
                    } else if (ZhiChiConstant.message_type_video.equals(message.getAnswer().getMsgType())) {
                        if (ZhiChiConstant.message_sender_type_customer == Integer
                                .parseInt(message.getSenderType())) {
                            if (message.getAnswer().getCacheFile() != null) {
                                return MSG_TYPE_VIDEO_R;
                            }
                        } else {
                            return MSG_TYPE_VIDEO_L;
                        }
                    } else if (ZhiChiConstant.message_type_location.equals(message.getAnswer().getMsgType())) {
                        if (ZhiChiConstant.message_sender_type_customer == Integer
                                .parseInt(message.getSenderType())) {
                            if (message.getAnswer().getLocationData() != null) {
                                return MSG_TYPE_LOCATION_R;
                            }
                        }
                    } else if (ZhiChiConstant.message_type_card == Integer
                            .parseInt(message.getAnswer().getMsgType())) {
                        if (message.getConsultingContent() != null) {
                            if (ZhiChiConstant.message_sender_type_customer == Integer
                                    .parseInt(message.getSenderType())) {
                                return MSG_TYPE_CARD_R;
                            } else {
                                return MSG_TYPE_CARD_L;
                            }
                        }
                    } else if (ZhiChiConstant.message_type_ordercard == Integer
                            .parseInt(message.getAnswer().getMsgType())) {
                        if (message.getOrderCardContent() != null) {
                            if (ZhiChiConstant.message_sender_type_customer == Integer
                                    .parseInt(message.getSenderType())) {
                                return MSG_TYPE_ROBOT_ORDERCARD_R;
                            } else {
                                return MSG_TYPE_ROBOT_ORDERCARD_L;
                            }
                        }
                    } else if (ZhiChiConstant.message_type_miniprogram_card == Integer
                            .parseInt(message.getAnswer().getMsgType())) {
                        if (message.getMiniProgramModel() != null) {
                            return MSG_TYPE_MINIPROGRAM_CARD_L;
                        }
                    } else if (ZhiChiConstant.message_type_muiti_leave_msg.equals(message.getAnswer().getMsgType())) {
                        return MSG_TYPE_MUITI_LEAVE_MSG_R;
                    } else if (ZhiChiConstant.message_type_article_card_msg.equals(message.getAnswer().getMsgType())) {
                        return MSG_TYPE_ARTICLE_CARD_L;
                    }
                } else {
                    return MSG_TYPE_ILLEGAL;
                }
            } else if (ZhiChiConstant.message_sender_type_remide_info == senderType) {
                //提醒的消息
                return MSG_TYPE_TIP;
            } else if (ZhiChiConstant.message_sender_type_customer_sendImage == senderType) {
                // 与我的图片消息
                return MSG_TYPE_IMG_R;
            } else if (ZhiChiConstant.message_sender_type_send_voice == senderType) {
                // 发送语音消息
                return MSG_TYPE_AUDIO_R;
            } else if (ZhiChiConstant.message_sender_type_consult_info == senderType) {
                return MSG_TYPE_CONSULT;
            } else if (ZhiChiConstant.message_sender_type_robot_guide == senderType) {
                return MSG_TYPE_RICH;
            } else if (ZhiChiConstant.message_sender_type_custom_evaluate == senderType) {
                return MSG_TYPE_CUSTOM_EVALUATE;
            } else if (ZhiChiConstant.message_sender_type_questionRecommend == senderType) {
                return MSG_TYPE_ROBOT_QUESTION_RECOMMEND;
            } else if (ZhiChiConstant.message_sender_type_robot_welcome_msg == senderType) {
                return MSG_TYPE_RICH;
            } else if (ZhiChiConstant.message_sender_type_robot_keyword_msg == senderType) {
                return MSG_TYPE_ROBOT_KEYWORD_ITEMS;
            } else if (ZhiChiConstant.message_sender_type_notice == senderType) {
                return MSG_TYPE_NOTICE;
            } else if (ZhiChiConstant.message_type_fraud_prevention == Integer
                    .parseInt(message.getAction())) {
                //防诈骗消息
                return MSG_TYPE_FRAUD_PREVENTION;
            } else if (ZhiChiConstant.action_sensitive_auth_agree.equals(message.getAction())) {
                //发送消息触发隐私，同意后的系统消息
                return MSG_TYPE_TIP;
            } else if (ZhiChiConstant.action_mulit_postmsg_tip_can_click.equals(message.getAction())) {
                //多轮收集节点提醒消息 可以点击
                return MSG_TYPE_TIP;
            } else if (ZhiChiConstant.action_mulit_postmsg_tip_nocan_click.equals(message.getAction())) {
                //多轮收集节点提醒消息 不可以点击
                return MSG_TYPE_TIP;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return MSG_TYPE_ILLEGAL;
        }

        return MSG_TYPE_ILLEGAL;
    }

    /**
     * 统一的时间提醒
     *
     * @param baseHolder
     * @param position
     */
    public void handerRemindTiem(final MessageHolderBase baseHolder, final int position) {
        boolean isHide = SharedPreferencesUtil.getBooleanData(context, ZhiChiConstant.SOBOT_HIDE_TIMEMSG, false);
        if (isHide) {
            return;
        }
        ZhiChiMessageBase message = list.get(position);

        //时间提醒
        if (baseHolder.reminde_time_Text == null) {
            return;
        }
        VersionUtils.setBackground(null, baseHolder.reminde_time_Text);
        baseHolder.reminde_time_Text.setTextColor(context.getResources()
                .getColor(ResourceUtils.getIdByName(context, "color", "sobot_color_remind_time_color")));
        String time = "";

        if (position == 0) {
            ZhiChiReplyAnswer answer = message.getAnswer();
            if (answer != null && answer.getRemindType() == ZhiChiConstant.sobot_remind_type_nomore) {
                baseHolder.reminde_time_Text.setVisibility(View.GONE);
            } else {
                time = getTimeStr(message, position);
                baseHolder.reminde_time_Text.setText(time);
                baseHolder.reminde_time_Text.setVisibility(View.VISIBLE);
            }
        } else {
            if (message.getCid() != null && !message.getCid().equals(list.get(position - 1).getCid())) {
                time = getTimeStr(message, position);
                baseHolder.reminde_time_Text.setVisibility(View.VISIBLE);
                baseHolder.reminde_time_Text.setText(time);
            } else {
                baseHolder.reminde_time_Text.setVisibility(View.GONE);
            }
        }
    }

    private String getTimeStr(ZhiChiMessageBase tempModel, int position) {
        String stringData = SharedPreferencesUtil.getStringData(context, "lastCid", "");
        tempModel.setTs(TextUtils.isEmpty(tempModel.getTs()) ? (DateUtil.getCurrentDateTime()) : tempModel.getTs());
        String time = "";
        String dataTime = DateUtil.stringToFormatString(tempModel.getTs() + "", "yyyy-MM-dd", ZCSobotApi.getSwitchMarkStatus(MarkConfig.AUTO_MATCH_TIMEZONE));
        String nowTime = DateUtil.getCurrentDate();
        if (tempModel.getCid() != null && tempModel.getCid().equals(stringData) && nowTime.equals(dataTime)) {
            time = DateUtil.formatDateTime(tempModel.getTs(), true, "", ZCSobotApi.getSwitchMarkStatus(MarkConfig.AUTO_MATCH_TIMEZONE));
        } else {
            time = DateUtil.stringToFormatString(list.get(position).getTs() +
                    "", "MM-dd HH:mm", ZCSobotApi.getSwitchMarkStatus(MarkConfig.AUTO_MATCH_TIMEZONE));
        }
        return time;
    }

    /**
     * 删除商品详情
     */
    public void removeConsulting() {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getAction() != null) {
                if (list.get(i).getAction().equals(ZhiChiConstant.action_consultingContent_info)) {
                    list.remove(i);
                    break;
                }
            }
        }
    }

    /**
     * 将受邀请评价 model 修改为已评价
     */
    public void submitEvaluateData(int isResolved, int score) {
        String senderType = ZhiChiConstant.message_sender_type_custom_evaluate + "";
        for (int i = list.size() - 1; i >= 0; i--) {
            ZhiChiMessageBase msgInfo = list.get(i);
            if (senderType.equals(msgInfo.getSenderType())) {
                SobotEvaluateModel sobotEvaluateModel = msgInfo.getSobotEvaluateModel();
                if (sobotEvaluateModel != null) {
                    sobotEvaluateModel.setIsResolved(isResolved);
                    sobotEvaluateModel.setScore(score);
                    sobotEvaluateModel.setEvaluateStatus(1);
                    break;
                }
            }
        }
    }

    public void removeKeyWordTranferItem() {

        try {
            List<ZhiChiMessageBase> listData = getDatas();
            for (int i = listData.size() - 1; i >= 0; i--) {
                if (ZhiChiConstant.message_sender_type_robot_keyword_msg == Integer
                        .parseInt(listData.get(i).getSenderType())) {
                    listData.remove(i);
                    break;
                }
            }
        } catch (Exception e) {
            LogUtils.i("error : removeKeyWordTranferItem()");
        }
    }

    /**
     * 评价成功后 删除评价
     */
    public void removeEvaluateData() {
        String senderType = ZhiChiConstant.message_sender_type_custom_evaluate + "";
        for (int i = list.size() - 1; i >= 0; i--) {
            ZhiChiMessageBase msgInfo = list.get(i);
            if (senderType.equals(msgInfo.getSenderType())) {
                SobotEvaluateModel sobotEvaluateModel = msgInfo.getSobotEvaluateModel();
                if (sobotEvaluateModel != null) {
                    list.remove(msgInfo);
                    break;
                }
            }
        }
    }

    public interface SobotMsgCallBack {
        void sendConsultingContent();

        void doEvaluate(final boolean evaluateFlag, final ZhiChiMessageBase message);

        void sendMessageToRobot(ZhiChiMessageBase base, int type, int questionFlag, String docId);

        void sendMessageToRobot(ZhiChiMessageBase base, int type, int questionFlag, String docId, String multiRoundMsg);

        void doClickTransferBtn(ZhiChiMessageBase base);

        void hidePanelAndKeyboard();

        void doRevaluate(final boolean revaluateFlag, final ZhiChiMessageBase message);

        void clickAudioItem(ZhiChiMessageBase message);

        void sendMessage(String content);

        void removeMessageByMsgId(String msgid);

        void addMessage(ZhiChiMessageBase message);

        void mulitDiaToLeaveMsg(String leaveTemplateId, String tipMsgId);
    }
}