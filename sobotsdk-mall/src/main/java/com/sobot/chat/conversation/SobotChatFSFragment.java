package com.sobot.chat.conversation;


import static com.sobot.chat.api.enumtype.SobotAutoSendMsgMode.ZCMessageTypeFile;
import static com.sobot.chat.api.enumtype.SobotAutoSendMsgMode.ZCMessageTypePhoto;
import static com.sobot.chat.api.enumtype.SobotAutoSendMsgMode.ZCMessageTypeText;
import static com.sobot.chat.api.enumtype.SobotAutoSendMsgMode.ZCMessageTypeVideo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sobot.chat.MarkConfig;
import com.sobot.chat.R;
import com.sobot.chat.SobotApi;
import com.sobot.chat.SobotUIConfig;
import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.activity.SobotCameraActivity;
import com.sobot.chat.activity.SobotPostLeaveMsgActivity;
import com.sobot.chat.activity.SobotPostMsgActivity;
import com.sobot.chat.activity.SobotSkillGroupActivity;
import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.adapter.SobotMsgAdapter;
import com.sobot.chat.api.apiUtils.SobotBaseUrl;
import com.sobot.chat.api.apiUtils.SobotVerControl;
import com.sobot.chat.api.apiUtils.ZhiChiConstants;
import com.sobot.chat.api.enumtype.CustomerState;
import com.sobot.chat.api.enumtype.SobotAutoSendMsgMode;
import com.sobot.chat.api.enumtype.SobotChatStatusMode;
import com.sobot.chat.api.model.BaseCode;
import com.sobot.chat.api.model.CommonModel;
import com.sobot.chat.api.model.CommonModelBase;
import com.sobot.chat.api.model.ConsultingContent;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.OrderCardContentModel;
import com.sobot.chat.api.model.SobotCommentParam;
import com.sobot.chat.api.model.SobotConnCusParam;
import com.sobot.chat.api.model.SobotEvaluateModel;
import com.sobot.chat.api.model.SobotKeyWordTransfer;
import com.sobot.chat.api.model.SobotLableInfoList;
import com.sobot.chat.api.model.SobotLocationModel;
import com.sobot.chat.api.model.SobotMultiDiaRespInfo;
import com.sobot.chat.api.model.SobotRobot;
import com.sobot.chat.api.model.SobotTransferOperatorParam;
import com.sobot.chat.api.model.ZhiChiCidsModel;
import com.sobot.chat.api.model.ZhiChiGroup;
import com.sobot.chat.api.model.ZhiChiGroupBase;
import com.sobot.chat.api.model.ZhiChiHistoryMessage;
import com.sobot.chat.api.model.ZhiChiHistoryMessageBase;
import com.sobot.chat.api.model.ZhiChiInitModeBase;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.api.model.ZhiChiPushMessage;
import com.sobot.chat.api.model.ZhiChiReplyAnswer;
import com.sobot.chat.core.channel.Const;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.listener.NoDoubleClickListener;
import com.sobot.chat.listener.PermissionListenerImpl;
import com.sobot.chat.listener.SobotFunctionType;
import com.sobot.chat.presenter.StPostMsgPresenter;
import com.sobot.chat.server.SobotSessionServer;
import com.sobot.chat.utils.AnimationUtil;
import com.sobot.chat.utils.AudioTools;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.CustomToast;
import com.sobot.chat.utils.ExtAudioRecorder;
import com.sobot.chat.utils.ImageUtils;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.MediaFileUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.utils.SobotPathManager;
import com.sobot.chat.utils.SobotSerializableMap;
import com.sobot.chat.utils.StServiceUtils;
import com.sobot.chat.utils.StringUtils;
import com.sobot.chat.utils.TimeTools;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConfig;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.viewHolder.CusEvaluateMessageHolder;
import com.sobot.chat.viewHolder.FileMessageHolder;
import com.sobot.chat.viewHolder.ImageMessageHolder;
import com.sobot.chat.viewHolder.MiniProgramMessageHolder;
import com.sobot.chat.viewHolder.RichTextMessageHolder;
import com.sobot.chat.viewHolder.RobotTemplateMessageHolder1;
import com.sobot.chat.viewHolder.RobotTemplateMessageHolder2;
import com.sobot.chat.viewHolder.RobotTemplateMessageHolder3;
import com.sobot.chat.viewHolder.RobotTemplateMessageHolder4;
import com.sobot.chat.viewHolder.RobotTemplateMessageHolder5;
import com.sobot.chat.viewHolder.RobotTemplateMessageHolder6;
import com.sobot.chat.viewHolder.VideoMessageHolder;
import com.sobot.chat.viewHolder.VoiceMessageHolder;
import com.sobot.chat.voice.AudioPlayCallBack;
import com.sobot.chat.voice.AudioPlayPresenter;
import com.sobot.chat.widget.ClearHistoryDialog;
import com.sobot.chat.widget.ContainsEmojiEditText;
import com.sobot.chat.widget.DropdownListView;
import com.sobot.chat.widget.dialog.SobotBackDialog;
import com.sobot.chat.widget.dialog.SobotClearHistoryMsgDialog;
import com.sobot.chat.widget.dialog.SobotEvaluateActivity;
import com.sobot.chat.widget.dialog.SobotRobotListDialog;
import com.sobot.chat.widget.emoji.DisplayEmojiRules;
import com.sobot.chat.widget.emoji.EmojiconNew;
import com.sobot.chat.widget.emoji.InputHelper;
import com.sobot.chat.widget.image.SobotRCImageView;
import com.sobot.chat.widget.kpswitch.CustomeChattingPanel;
import com.sobot.chat.widget.kpswitch.util.KPSwitchConflictUtil;
import com.sobot.chat.widget.kpswitch.util.KeyboardUtil;
import com.sobot.chat.widget.kpswitch.view.ChattingPanelEmoticonView;
import com.sobot.chat.widget.kpswitch.view.ChattingPanelUploadView;
import com.sobot.chat.widget.kpswitch.view.CustomeViewFactory;
import com.sobot.chat.widget.kpswitch.widget.KPSwitchFSPanelLinearLayout;
import com.sobot.network.http.callback.StringResultCallBack;
import com.sobot.network.http.upload.SobotUpload;
import com.sobot.pictureframe.SobotBitmapUtil;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * @author Created by jinxl on 2018/2/1.
 */
public class SobotChatFSFragment extends SobotChatBaseFragment implements View.OnClickListener
        , DropdownListView.OnRefreshListenerHeader, SobotMsgAdapter.SobotMsgCallBack,
        ContainsEmojiEditText.SobotAutoCompleteListener
        , ChattingPanelEmoticonView.SobotEmoticonClickListener
        , ChattingPanelUploadView.SobotPlusClickListener, SobotRobotListDialog.SobotRobotListListener {

    //---------------UI控件 START---------------
    public LinearLayout sobot_header_center_ll;//头部中间部分
    public TextView mTitleTextView;//头部标题
    public SobotRCImageView mAvatarIV;//头像
    public TextView sobot_title_conn_status;
    public LinearLayout sobot_container_conn_status;
    public TextView sobot_tv_right_second;
    public TextView sobot_tv_right_third;
    public ProgressBar sobot_conn_loading;
    public RelativeLayout net_status_remide;
    public TextView sobot_net_not_connect;
    public RelativeLayout relative;
    private TextView sobot_tv_satisfaction, notReadInfo, sobot_tv_message,
            sobot_txt_restart_talk;
    private TextView textReConnect;
    private ProgressBar loading_anim_view;
    private TextView txt_loading;
    private ImageView icon_nonet;
    private Button btn_reconnect;
    private RelativeLayout chat_main; // 聊天主窗口;
    private FrameLayout welcome; // 欢迎窗口;
    private DropdownListView lv_message;/* 带下拉的ListView */
    private ContainsEmojiEditText et_sendmessage;// 当前用户输入的信息
    private Button btn_send; // 发送消息按钮
    private Button btn_send_pic; // 发送图片
    private ImageButton btn_set_mode_rengong; // 转人工button
    private View view_model_split;//机器人模式下,同时可使用语音时显示
    private TextView send_voice_robot_hint;
    private Button btn_upload_view; // 上传图片
    private ImageButton btn_emoticon_view; // 表情面板
    private TextView voice_time_long;/*显示语音时长*/
    private LinearLayout voice_top_image;
    private ImageView image_endVoice;
    private ImageView mic_image;
    private ImageView mic_image_animate; // 图片的动画
    private ImageView recording_timeshort;// 语音太短的图片
    private ImageButton btn_model_edit; // 编辑模式
    private ImageButton btn_model_voice;// 语音模式
    private TextView txt_speak_content; // 发送语音的文字
    private AnimationDrawable animationDrawable;/* 语音的动画 */
    private KPSwitchFSPanelLinearLayout mPanelRoot; // 聊天下面的面板
    private LinearLayout btn_press_to_speak; // 说话view ;
    private LinearLayout edittext_layout; // 输入框view;
    private LinearLayout recording_container;// 语音上滑的动画
    private TextView recording_hint;// 上滑的显示文本；
    private RelativeLayout sobot_ll_restart_talk; // 开始新会话布局ID
    private ImageView image_reLoading;
    private LinearLayout sobot_ll_bottom;//聊天界面底部布局
    //通告
    private RelativeLayout sobot_announcement; // 通告view ;
    private TextView sobot_announcement_right_icon;
    private TextView sobot_announcement_title;
    //机器人切换按钮
    private LinearLayout sobot_ll_switch_robot;
    private TextView sobot_tv_switch_robot;

    private SobotRobotListDialog mRobotListDialog;

    private HorizontalScrollView sobot_custom_menu;//横向滚动布局
    private LinearLayout sobot_custom_menu_linearlayout;
    private TextView sobot_tv_close;
    //---------------UI控件 END---------------


    //-----------
    // 消息列表展示
    private List<ZhiChiMessageBase> messageList = new ArrayList<ZhiChiMessageBase>();


    //--------

    private int showTimeVisiableCustomBtn = 0;/*用户设置几次显示转人工按钮*/
    private List<ZhiChiGroupBase> list_group;

    protected int type = -1;//当前模式的类型
    private static String preCurrentCid = null;//保存上一次会话cid；
    private static int statusFlag = 0; // 保存当前转人工成功的状态
    private boolean isSessionOver = true;//表示此会话是否结束null

    private boolean isComment = false;/* 判断用户是否评价过 */
    private boolean isShowQueueTip = true;//是否显示 排队提醒 用以过滤关键字转人工时出现的提醒
    private int queueNum = 0;//排队的人数
    private int queueTimes = 0;//收到排队顺序变化提醒的次数
    private int mUnreadNum = 0;//未读消息数

    //录音相关
    protected Timer voiceTimer;
    protected TimerTask voiceTimerTask;
    protected int voiceTimerLong = 0;
    protected String voiceTimeLongStr = "00";// 时间的定时的任务
    private int minRecordTime = 60;// 允许录音时间
    private int recordDownTime = minRecordTime - 10;// 允许录音时间 倒计时
    boolean isCutVoice;
    private String voiceMsgId = "";//  语音消息的Id
    private int currentVoiceLong = 0;

    AudioPlayPresenter mAudioPlayPresenter = null;
    AudioPlayCallBack mAudioPlayCallBack = null;
    private String mFileName = null;
    private ExtAudioRecorder extAudioRecorder;

    //以下参数为历史记录需要的接口
    private List<String> cids = new ArrayList<>();//cid的列表
    private int currentCidPosition = 0;//当前查询聊天记录所用的cid位置
    //表示查询cid的接口 当前调用状态 0、未调用 1、调用中 2、调用成功  3、调用失败
    private int queryCidsStatus = ZhiChiConstant.QUERY_CIDS_STATUS_INITIAL;
    private boolean isInGethistory = false;//表示是否正在查询历史记录
    private boolean isConnCustomerService = false;//控制同一时间 只能调一次转人工接口
    private boolean isNoMoreHistoryMsg = false;

    //键盘相关
    public int currentPanelId = 0;//切换聊天面板时 当前点击的按钮id 为了能切换到对应的view上
    private int mBottomViewtype = 0;//记录键盘的状态

    //---------
    //键盘监听
    private ViewTreeObserver.OnGlobalLayoutListener mKPSwitchListener;

    private MyMessageReceiver receiver;
    //本地广播数据类型实例。
    private LocalBroadcastManager localBroadcastManager;
    private LocalReceiver localReceiver;

    //留言处理
    private StPostMsgPresenter mPostMsgPresenter;

    //2.9.2添加 初始化时如果有离线消息直接转对应的客服
    private int offlineMsgConnectFlag;
    private String offlineMsgAdminId;
    //命中后端关键词转人工，机器人接口返回的
    ZhiChiMessageBase keyWordMessageBase;

    String tempMsgContent;//2.9.3 仅人工/人工优先模拟人工模式，临时保存客户真正转人工前第一次发送的消息，转人工成功后自动发送发送，发送完清除

    //初始化接口是否已经结束，防止多次调用
    private boolean isAppInitEnd = true;

    public static SobotChatFSFragment newInstance(Bundle info) {
        Bundle arguments = new Bundle();
        arguments.putBundle(ZhiChiConstant.SOBOT_BUNDLE_INFORMATION, info);
        SobotChatFSFragment fragment = new SobotChatFSFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i("onCreate");
        try {
            String host = SharedPreferencesUtil.getStringData(getSobotActivity(), ZhiChiConstant.SOBOT_SAVE_HOST_AFTER_INITSDK, SobotBaseUrl.getApi_Host());
            if (!host.equals(SobotBaseUrl.getApi_Host())) {
                SobotBaseUrl.setApi_Host(host);
            }
        } catch (Exception e) {
        }
        if (getArguments() != null) {
            Bundle informationBundle = getArguments().getBundle(ZhiChiConstant.SOBOT_BUNDLE_INFORMATION);
            if (informationBundle != null) {
                Serializable sobot_info = informationBundle.getSerializable(ZhiChiConstant.SOBOT_BUNDLE_INFO);
                if (sobot_info != null && sobot_info instanceof Information) {
                    info = (Information) sobot_info;
                    if (info != null && TextUtils.isEmpty(info.getLocale())) {
                        //info.locale 如果为空,使用客户指定或者系统的语言
                        boolean isUseLanguage = SharedPreferencesUtil.getBooleanData(getSobotActivity(), ZhiChiConstant.SOBOT_USE_LANGUAGE, false);
                        if (isUseLanguage) {
                            String languageString = SharedPreferencesUtil.getStringData(getSobotActivity(), ZhiChiConstant.SOBOT_USER_SETTTINNG_LANGUAGE, "");
                            if (!TextUtils.isEmpty(languageString)) {
                                info.setLocale(languageString);
                            }
                        } else {
                            Locale language = (Locale) SharedPreferencesUtil.getObject(getSobotActivity(), ZhiChiConstant.SOBOT_LANGUAGE);
                            if (language != null) {
                                if ("TW".equals(language.getCountry())) {
                                    info.setLocale("zh-Hant");
                                } else if (!"zh".equals(language.getLanguage())) {
                                    info.setLocale(language.getLanguage());
                                }
                            } else {
                                Locale locale = getSobotActivity().getResources().getConfiguration().locale;
                                if (locale != null) {
                                    if ("TW".equals(locale.getCountry())) {
                                        info.setLocale("zh-Hant");
                                    } else if (!"zh".equals(locale.getLanguage())) {
                                        info.setLocale(locale.getLanguage());
                                    }
                                }
                            }
                        }
                    }
                    SharedPreferencesUtil.saveObject(getSobotActivity(),
                            ZhiChiConstant.sobot_last_current_info, info);
                }
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(getResLayoutId("sobot_chat_fs_fragment"), container, false);
        initView(root);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (info == null) {
            LogUtils.e("初始化参数不能为空");
            finish();
            return;
        }

        String platformUnionCode = SharedPreferencesUtil.getStringData(getSobotActivity(), ZhiChiConstant.SOBOT_PLATFORM_UNIONCODE, "");
        if (SobotVerControl.isPlatformVer && !TextUtils.isEmpty(platformUnionCode)) {
            if (TextUtils.isEmpty(info.getApp_key()) && TextUtils.isEmpty(info.getCustomer_code())) {
                LogUtils.i("appkey或者customCode必须设置一项");
                finish();
                return;
            }
        } else {
            if (TextUtils.isEmpty(info.getApp_key())) {
                LogUtils.e("您的AppKey为空");
                finish();
                return;
            }
        }

        SharedPreferencesUtil.saveStringData(mAppContext, ZhiChiConstant.SOBOT_CURRENT_IM_APPID, info.getApp_key());

        //保存自定义配置
        ChatUtils.saveOptionSet(mAppContext, info);

        initData();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (sobot_tv_close != null) {
            if (info.isShowCloseBtn() && current_client_model == ZhiChiConstant.client_model_customService) {
                //显示右上角的关闭按钮
                sobot_tv_close.setVisibility(View.VISIBLE);
            } else {
                sobot_tv_close.setVisibility(View.GONE);
            }
        }
        SharedPreferencesUtil.saveStringData(mAppContext, ZhiChiConstant.SOBOT_CURRENT_IM_APPID, info.getApp_key());
        Intent intent = new Intent(mAppContext, SobotSessionServer.class);
        intent.putExtra(ZhiChiConstant.SOBOT_CURRENT_IM_PARTNERID, info.getPartnerid());
        StServiceUtils.safeStartService(mAppContext, intent);
        SobotMsgManager.getInstance(mAppContext).getConfig(info.getApp_key()).clearCache();
        //人工状态，检查连接
        if (customerState == CustomerState.Online || customerState == CustomerState.Queuing) {
            //获取tcp服务被杀死的时间，如果是0，不进行初始化，直接检查通道就行
            long lastHideTime = SharedPreferencesUtil.getLongData(mAppContext, ZhiChiConstant.SOBOT_HIDE_CHATPAGE_TIME, System.currentTimeMillis());
            if (lastHideTime != 0 && !CommonUtils.isServiceWork(getSobotActivity(), "com.sobot.chat.core.channel.SobotTCPServer")) {
                //LogUtils.i((System.currentTimeMillis() + "-------------" + lastHideTime + "==========" + (System.currentTimeMillis() - lastHideTime)));
                // LogUtils.i("----人工状态 SobotTCPServer 被杀死了");
                if ((System.currentTimeMillis() - lastHideTime) > 30 * 60 * 1000) {
                    //   LogUtils.i("----由于SobotTCPServer 被杀死了超过30分钟，需要重新初始化---------");
                    initSdk(true, 0);
                } else {
                    zhiChiApi.reconnectChannel();
                }
            } else {
                zhiChiApi.reconnectChannel();
            }
        }
    }

    @Override
    public void onPause() {
        if (initModel != null) {
            if (!isSessionOver) {
                //保存会话信息
                saveCache();
            } else {
                //清除会话信息
                clearCache();
            }
            //保存消息列表
            ChatUtils.saveLastMsgInfo(getSobotActivity(), info, info.getApp_key(), initModel, messageList);
        }
        stopInputListener();
        if (AudioTools.getInstance().isPlaying()) {
            //停止播放
            AudioTools.getInstance().stop();
            lv_message.post(new Runnable() {

                @Override
                public void run() {
                    if (info == null) {
                        return;
                    }
                    for (int i = 0, count = lv_message.getChildCount(); i < count; i++) {
                        View child = lv_message.getChildAt(i);
                        if (child == null || child.getTag() == null || !(child.getTag() instanceof VoiceMessageHolder)) {
                            continue;
                        }
                        VoiceMessageHolder holder = (VoiceMessageHolder) child.getTag();
                        if (holder != null) {
                            holder.stopAnim();
                            holder.checkBackground();
                        }
                    }
                }
            });
        }
        ////放弃音频焦点
        abandonAudioFocus();
        // 取消注册传感器
        if (_sensorManager != null) {
            _sensorManager.unregisterListener(this);
            _sensorManager = null;
        }
        if (mProximiny != null)
            mProximiny = null;
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (!isAboveZero) {
            SharedPreferencesUtil.saveLongData(getSobotActivity(), ZhiChiConstant.SOBOT_FINISH_CURTIME, System.currentTimeMillis());
        }
        hideReLoading();
        // 停止用户的定时任务
        stopUserInfoTimeTask();
        // 停止客服的定时任务
        stopCustomTimeTask();
        stopVoice();
        AudioTools.destory();
        SobotUpload.getInstance().unRegister();
        mPostMsgPresenter.destory();

        if (mRobotListDialog != null && mRobotListDialog.isShowing()) {
            mRobotListDialog.dismiss();
        }
        if (SobotOption.sobotViewListener != null) {
            SobotOption.sobotViewListener.onChatActClose(customerState);
        }
        super.onDestroyView();
    }

    private void initView(View rootView) {
        if (rootView == null) {
            return;
        }

        //loading 层
        relative = (RelativeLayout) rootView.findViewById(getResId("sobot_layout_titlebar"));
        sobot_header_center_ll = (LinearLayout) rootView.findViewById(getResId("sobot_header_center_ll"));
        mTitleTextView = (TextView) rootView.findViewById(getResId("sobot_text_title"));
        mAvatarIV = rootView.findViewById(getResId("sobot_avatar_iv"));
        sobot_title_conn_status = (TextView) rootView.findViewById(getResId("sobot_title_conn_status"));
        sobot_container_conn_status = (LinearLayout) rootView.findViewById(getResId("sobot_container_conn_status"));
        sobot_tv_right_second = (TextView) rootView.findViewById(getResId("sobot_tv_right_second"));
        sobot_tv_right_third = (TextView) rootView.findViewById(getResId("sobot_tv_right_third"));
        sobot_conn_loading = (ProgressBar) rootView.findViewById(getResId("sobot_conn_loading"));
        net_status_remide = (RelativeLayout) rootView.findViewById(getResId("sobot_net_status_remide"));
        sobot_net_not_connect = (TextView) rootView.findViewById(getResId("sobot_net_not_connect"));
        sobot_net_not_connect.setText(ResourceUtils.getResString(getSobotActivity(), "sobot_network_unavailable"));

        relative.setVisibility(View.GONE);
        notReadInfo = (TextView) rootView.findViewById(getResId("notReadInfo"));
        chat_main = (RelativeLayout) rootView.findViewById(getResId("sobot_chat_main"));
        welcome = (FrameLayout) rootView.findViewById(getResId("sobot_welcome"));
        txt_loading = (TextView) rootView.findViewById(getResId("sobot_txt_loading"));
        textReConnect = (TextView) rootView.findViewById(getResId("sobot_textReConnect"));
        textReConnect.setText(ResourceUtils.getResString(getSobotActivity(), "sobot_try_again"));
        loading_anim_view = (ProgressBar) rootView.findViewById(getResId("sobot_image_view"));
        image_reLoading = (ImageView) rootView.findViewById(getResId("sobot_image_reloading"));
        icon_nonet = (ImageView) rootView.findViewById(getResId("sobot_icon_nonet"));
        btn_reconnect = (Button) rootView.findViewById(getResId("sobot_btn_reconnect"));
        btn_reconnect.setText(ResourceUtils.getResString(getSobotActivity(), "sobot_reunicon"));
        btn_reconnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                textReConnect.setVisibility(View.GONE);
                icon_nonet.setVisibility(View.GONE);
                btn_reconnect.setVisibility(View.GONE);
                loading_anim_view.setVisibility(View.VISIBLE);
                txt_loading.setVisibility(View.VISIBLE);
                //重新加载，相当于第一次进入
                customerInit(1);
            }
        });

        lv_message = (DropdownListView) rootView.findViewById(getResId("sobot_lv_message"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            lv_message.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        et_sendmessage = (ContainsEmojiEditText) rootView.findViewById(getResId("sobot_et_sendmessage"));
        et_sendmessage.setVisibility(View.VISIBLE);
        btn_send = (Button) rootView.findViewById(getResId("sobot_btn_send"));
        btn_send_pic = (Button) rootView.findViewById(getResId("sobot_btn_send_view"));
        btn_send.setText(ResourceUtils.getResString(getSobotActivity(), "sobot_button_send"));
        btn_set_mode_rengong = (ImageButton) rootView.findViewById(getResId("sobot_btn_set_mode_rengong"));
        view_model_split = rootView.findViewById(getResId("sobot_view_model_split"));
        send_voice_robot_hint = (TextView) rootView.findViewById(getResId("send_voice_robot_hint"));
        send_voice_robot_hint.setHint(ResourceUtils.getResString(getSobotActivity(), "sobot_robot_voice_hint"));
        send_voice_robot_hint.setVisibility(View.GONE);
        btn_upload_view = (Button) rootView.findViewById(getResId("sobot_btn_upload_view"));
        btn_emoticon_view = (ImageButton) rootView.findViewById(getResId("sobot_btn_emoticon_view"));
        btn_model_edit = (ImageButton) rootView.findViewById(getResId("sobot_btn_model_edit"));
        btn_model_voice = (ImageButton) rootView.findViewById(getResId("sobot_btn_model_voice"));
        mPanelRoot = (KPSwitchFSPanelLinearLayout) rootView.findViewById(getResId("sobot_panel_root"));
        btn_press_to_speak = (LinearLayout) rootView.findViewById(getResId("sobot_btn_press_to_speak"));
        edittext_layout = (LinearLayout) rootView.findViewById(getResId("sobot_edittext_layout"));
        recording_hint = (TextView) rootView.findViewById(getResId("sobot_recording_hint"));
        recording_container = (LinearLayout) rootView.findViewById(getResId("sobot_recording_container"));

        // 开始语音的布局的信息
        voice_top_image = (LinearLayout) rootView.findViewById(getResId("sobot_voice_top_image"));
        // 停止语音
        image_endVoice = (ImageView) rootView.findViewById(getResId("sobot_image_endVoice"));
        // 动画的效果
        mic_image_animate = (ImageView) rootView.findViewById(getResId("sobot_mic_image_animate"));
        // 时长的界面
        voice_time_long = (TextView) rootView.findViewById(getResId("sobot_voiceTimeLong"));
        txt_speak_content = (TextView) rootView.findViewById(getResId("sobot_txt_speak_content"));
        txt_speak_content.setText(getResString("sobot_press_say"));
        recording_timeshort = (ImageView) rootView.findViewById(getResId("sobot_recording_timeshort"));
        mic_image = (ImageView) rootView.findViewById(getResId("sobot_mic_image"));

        sobot_ll_restart_talk = (RelativeLayout) rootView.findViewById(getResId("sobot_ll_restart_talk"));
        sobot_txt_restart_talk = (TextView) rootView.findViewById(getResId("sobot_txt_restart_talk"));
        sobot_txt_restart_talk.setText(ResourceUtils.getResString(getSobotActivity(), "sobot_restart_talk"));
        sobot_tv_message = (TextView) rootView.findViewById(getResId("sobot_tv_message"));
        sobot_tv_message.setText(ResourceUtils.getResString(getSobotActivity(), "sobot_str_bottom_message"));
        sobot_tv_satisfaction = (TextView) rootView.findViewById(getResId("sobot_tv_satisfaction"));
        sobot_tv_satisfaction.setText(ResourceUtils.getResString(getSobotActivity(), "sobot_str_bottom_satisfaction"));
        sobot_ll_bottom = (LinearLayout) rootView.findViewById(getResId("sobot_ll_bottom"));
        sobot_ll_switch_robot = (LinearLayout) rootView.findViewById(getResId("sobot_ll_switch_robot"));
        sobot_tv_switch_robot = (TextView) rootView.findViewById(getResId("sobot_tv_switch_robot"));
        sobot_tv_switch_robot.setText(ResourceUtils.getResString(getSobotActivity(), "sobot_switch_business"));

        sobot_announcement = (RelativeLayout) rootView.findViewById(getResId("sobot_announcement"));
        sobot_announcement_right_icon = (TextView) rootView.findViewById(getResId("sobot_announcement_right_icon"));
        sobot_announcement_title = (TextView) rootView.findViewById(getResId("sobot_announcement_title"));
        sobot_announcement_title.setSelected(true);

        sobot_custom_menu = (HorizontalScrollView) rootView.findViewById(getResId("sobot_custom_menu"));
        sobot_custom_menu.setVisibility(View.GONE);
        sobot_custom_menu_linearlayout = (LinearLayout) rootView.findViewById(getResId("sobot_custom_menu_linearlayout"));
        displayInNotch(lv_message);
        displayInNotch(sobot_custom_menu);
        displayInNotch(sobot_ll_bottom);
        applyUIConfig();
        mPostMsgPresenter = StPostMsgPresenter.newInstance(SobotChatFSFragment.this, getContext());
    }

    /* 处理消息 */
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {

        @SuppressWarnings("unchecked")
        public void handleMessage(final android.os.Message msg) {
            if (!isActive()) {
                return;
            }
            switch (msg.what) {
                case ZhiChiConstant.hander_send_msg:
                    //发送消息更新UI
                    updateUiMessage(messageAdapter, msg);
                    lv_message.setSelection(messageAdapter.getCount());
                    break;
                case ZhiChiConstant.send_message_close:
                    //显示关闭按钮
                    //设置导航栏关闭按钮
                    if (sobot_tv_close != null && info.isShowCloseBtn() && current_client_model == ZhiChiConstant.client_model_customService) {

                        sobot_tv_close.setVisibility(View.VISIBLE);
                    }
                    break;
                case ZhiChiConstant.hander_update_msg_status:
                    //消息发送状态更新
                    updateMessageStatus(messageAdapter, msg);
                    break;
                case ZhiChiConstant.update_send_data:
                    ZhiChiMessageBase myMessage = (ZhiChiMessageBase) msg.obj;
                    messageAdapter.updateDataById(myMessage.getId(), myMessage);
                    messageAdapter.notifyDataSetChanged();
                    lv_message.setSelection(messageAdapter.getCount());
                    break;
                case ZhiChiConstant.hander_robot_message:
                    ZhiChiMessageBase zhiChiMessageBasebase = (ZhiChiMessageBase) msg.obj;
                    zhiChiMessageBasebase.setT(Calendar.getInstance().getTime().getTime() + "");
                    if (type == ZhiChiConstant.type_robot_first || type == ZhiChiConstant.type_custom_first) {
                        //智能客服模式下，特定问题类型的机器人回答语下显示“转人工”按钮。
                        if (initModel != null && ChatUtils.checkManualType(initModel.getManualType(),
                                zhiChiMessageBasebase.getAnswerType())) {
                            //如果此项在工作台上勾选 那就显示转人工按钮
                            zhiChiMessageBasebase.setShowTransferBtn(true);
                        }
                    }

                    // 1 直接回答，2 理解回答，3 不能回答, 4引导回答，5、本地寒暄，6互联网寒暄，
                    // 7 私有寒暄（包括第三方天气、快递接口）,8百科, 9 向导回答,10 业务接口
                    //后台app 客服设置 评价机器人推送开关 打开后，类型是1，2, 9，11，12，14显示 152 开头的多伦回话
                    //1525 多伦工单节点不显示顶踩 3.1.1新增
                    if (ZhiChiConstant.type_answer_direct.equals(zhiChiMessageBasebase.getAnswerType())
                            || ZhiChiConstant.type_answer_wizard.equals(zhiChiMessageBasebase.getAnswerType())
                            || "1".equals(zhiChiMessageBasebase.getAnswerType())
                            || "2".equals(zhiChiMessageBasebase.getAnswerType())
                            || "11".equals(zhiChiMessageBasebase.getAnswerType())
                            || "12".equals(zhiChiMessageBasebase.getAnswerType())
                            || "14".equals(zhiChiMessageBasebase.getAnswerType()) || (!TextUtils.isEmpty(zhiChiMessageBasebase.getAnswerType()) && zhiChiMessageBasebase.getAnswerType().startsWith("152"))) {
                        if (initModel != null && initModel.isRealuateFlag()) {
                            //顶踩开关打开 显示顶踩按钮
                            zhiChiMessageBasebase.setRevaluateState(1);
                            if ((ZhiChiConstant.message_sender_type_robot_guide + "").equals(zhiChiMessageBasebase.getSenderType()) || (!TextUtils.isEmpty(zhiChiMessageBasebase.getAnswerType()) && "1525".equals(zhiChiMessageBasebase.getAnswerType()))) {
                                //如果是引导问题，不能显示顶踩
                                zhiChiMessageBasebase.setRevaluateState(0);
                            }
                        } else {
                            //顶踩开关打开 隐藏顶踩按钮
                            zhiChiMessageBasebase.setRevaluateState(0);
                        }
                    }

                    if (zhiChiMessageBasebase.getAnswer() != null && zhiChiMessageBasebase.getAnswer().getMultiDiaRespInfo() != null
                            && zhiChiMessageBasebase.getAnswer().getMultiDiaRespInfo().getEndFlag()) {
                        // 多轮会话结束时禁用所有多轮会话可点击选项
                        restMultiMsg();
                        SobotMultiDiaRespInfo multiDiaRespInfo = zhiChiMessageBasebase.getAnswer().getMultiDiaRespInfo();
                        if (multiDiaRespInfo.getEndFlag() && "1525".equals(zhiChiMessageBasebase.getAnswerType()) && !TextUtils.isEmpty(multiDiaRespInfo.getLeaveTemplateId())) {
                            mulitDiaToLeaveMsg(multiDiaRespInfo.getLeaveTemplateId(), "");
                        }
                    }
                    SobotKeyWordTransfer keyWordTransfer = zhiChiMessageBasebase.getSobotKeyWordTransfer();
                    if (keyWordTransfer != null) {
                        //关键词转人工
                        if (type != ZhiChiConstant.type_robot_only) {
                            if (1 == keyWordTransfer.getTransferFlag()) {
//                                transferFlag=1或3：
//                                queueFlag=1:展示提示语，不展示机器人回复，触发转人工逻辑
//                                        queueFlag=0:
//                                onlineFlag:1 表示有客服在线可接入（展示提示语，不展示机器人回复，触发转人工逻辑）
//                                onlineFlag:2 表示需要弹出分组接待（不展示提示语，不展示机器人回复，触发转人工逻辑）
//                                onlineFlag:3 表示无客服在线 （不执行转人工，展示机器人回复）
                                if (keyWordTransfer.isQueueFlag()) {
                                    //展示提示语，不展示机器人回复，触发转人工逻辑
                                    addKeyWordTipMsg(keyWordTransfer);
                                    transfer2Custom(keyWordTransfer.getGroupId(), keyWordTransfer.getKeyword(), keyWordTransfer.getKeywordId(), keyWordTransfer.isQueueFlag());
                                } else {
                                    if (keyWordTransfer.getOnlineFlag() == 1) {
                                        //表示有客服在线可接入（展示提示语，不展示机器人回复，触发转人工逻辑）
                                        addKeyWordTipMsg(keyWordTransfer);
                                        transfer2Custom(keyWordTransfer.getGroupId(), keyWordTransfer.getKeyword(), keyWordTransfer.getKeywordId(), keyWordTransfer.isQueueFlag());
                                    } else if (keyWordTransfer.getOnlineFlag() == 2) {
                                        //表示需要弹出分组接待（不展示提示语，不展示机器人回复，触发转人工逻辑）
                                        transfer2Custom(keyWordTransfer.getGroupId(), keyWordTransfer.getKeyword(), keyWordTransfer.getKeywordId(), keyWordTransfer.isQueueFlag());
                                    } else if (keyWordTransfer.getOnlineFlag() == 3) {
                                        //表示无客服在线 （不执行转人工，展示机器人回复）
                                        messageAdapter.justAddData(zhiChiMessageBasebase);
                                    }
                                }
                            } else if (2 == keyWordTransfer.getTransferFlag()) {
                                //不展示机器人回复，展示选择技能组文案
                                //转给多个技能组（一个消息cell），用户可以选择
                                ZhiChiMessageBase keyWordBase = new ZhiChiMessageBase();
                                keyWordBase.setSenderFace(zhiChiMessageBasebase.getSenderFace());
                                keyWordBase.setSenderType(ZhiChiConstant.message_sender_type_robot_keyword_msg + "");
                                keyWordBase.setSenderName(zhiChiMessageBasebase.getSenderName());
                                keyWordBase.setSobotKeyWordTransfer(keyWordTransfer);
                                messageAdapter.justAddData(keyWordBase);
                            } else if (3 == keyWordTransfer.getTransferFlag()) {
                                if (keyWordTransfer.isQueueFlag()) {
                                    //展示提示语，不展示机器人回复，触发转人工逻辑
                                    addKeyWordTipMsg(keyWordTransfer);
                                    //默认，按正常转人工的逻辑走
                                    transfer2Custom("", "", "", keyWordTransfer.isQueueFlag());
                                } else {
                                    if (keyWordTransfer.getOnlineFlag() == 1) {
                                        //表示有客服在线可接入（展示提示语，不展示机器人回复，触发转人工逻辑）
                                        addKeyWordTipMsg(keyWordTransfer);
                                        //默认，按正常转人工的逻辑走
                                        transfer2Custom("", "", "", keyWordTransfer.isQueueFlag());
                                    } else if (keyWordTransfer.getOnlineFlag() == 2) {
                                        //表示需要弹出分组接待（不展示提示语，不展示机器人回复，触发转人工逻辑）
                                        //默认，按正常转人工的逻辑走
                                        transfer2Custom("", "", "", keyWordTransfer.isQueueFlag());
                                    } else if (keyWordTransfer.getOnlineFlag() == 3) {
                                        //表示无客服在线 （不执行转人工，展示机器人回复）
                                        messageAdapter.justAddData(zhiChiMessageBasebase);
                                    }
                                }
                            }
                        } else {
                            //展示机器人回复
                            messageAdapter.justAddData(zhiChiMessageBasebase);
                        }
                    } else {
                        if (zhiChiMessageBasebase.getAnswer() != null && StringUtils.isNoEmpty(zhiChiMessageBasebase.getAnswerType()) && "1526".equals(zhiChiMessageBasebase.getAnswerType())) {
                            //多轮 1526 转人工节点
                            SobotMultiDiaRespInfo multiDiaRespInfo = zhiChiMessageBasebase.getAnswer().getMultiDiaRespInfo();
                            String msgStr = StringUtils.stripHtml(ChatUtils.getMultiMsgTitle(multiDiaRespInfo));
                            ZhiChiMessageBase messageBase = ChatUtils.getTipByText(StringUtils.checkStringIsNull(msgStr));
                            messageAdapter.justAddData(messageBase);
                            if (StringUtils.isNoEmpty(zhiChiMessageBasebase.getNodeTransferFlag())) {
                                //1526类型 写死transferType=11（转人工类型）,  activeTransfer=0(机器人人触发)
                                if ("1".equals(zhiChiMessageBasebase.getNodeTransferFlag())) {
                                    //指定客服转 需要同时设置强转
                                    transfer2Custom(ZhiChiConstant.SOBOT_TYEP_TRANSFER_CUSTOM_DUOLUN1526, StringUtils.checkStringIsNull(zhiChiMessageBasebase.getTransferTargetId()), null, null, null, true, 11, zhiChiMessageBasebase.getDocId(), zhiChiMessageBasebase.getOriginQuestion(), "0", zhiChiMessageBasebase.getMsgId(), zhiChiMessageBasebase.getRuleId());
                                } else if ("2".equals(zhiChiMessageBasebase.getNodeTransferFlag())) {
                                    //指定技能组转
                                    transfer2Custom(0, null, StringUtils.checkStringIsNull(zhiChiMessageBasebase.getTransferTargetId()), null, null, true, 11, zhiChiMessageBasebase.getDocId(), zhiChiMessageBasebase.getOriginQuestion(), "0", zhiChiMessageBasebase.getMsgId(), zhiChiMessageBasebase.getRuleId());
                                } else {
                                    //默认转
                                    transfer2Custom(0, null, null, null, null, true, 11, zhiChiMessageBasebase.getDocId(), zhiChiMessageBasebase.getOriginQuestion(), "0", zhiChiMessageBasebase.getMsgId(), zhiChiMessageBasebase.getRuleId());
                                }
                            }
                        } else {
                            messageAdapter.justAddData(zhiChiMessageBasebase);
                        }
                        if (type != ZhiChiConstant.type_robot_only) {
                            //仅机器人不触发转人工
                            if (zhiChiMessageBasebase.getTransferType() == 1
                                    || zhiChiMessageBasebase.getTransferType() == 2 || zhiChiMessageBasebase.getTransferType() == 5) {
                                //重复提问、情绪负向 5自动转人工  转人工
                                ZhiChiMessageBase robot = ChatUtils.getRobotTransferTip(getContext(), initModel);
                                messageAdapter.justAddData(robot);
                                if (zhiChiMessageBasebase.getTransferType() == 5) {
//                                    6. 理解回答转人工 0/1
//                                    7. 引导回答转人工 0/1
//                                    8. 未知回答转人工0/1
//                                    9. 点踩转人工 1
                                    int transferType = 5;
                                    if ("1".equals(zhiChiMessageBasebase.getAnswerType())) {
                                        transferType = 6;
                                    } else if ("2".equals(zhiChiMessageBasebase.getAnswerType())) {
                                        transferType = 7;
                                    } else if ("4".equals(zhiChiMessageBasebase.getAnswerType())) {
                                        transferType = 8;
                                    } else if ("3".equals(zhiChiMessageBasebase.getAnswerType())) {
                                        transferType = 9;
                                    }
                                    transfer2Custom(0, null, null, null, null, true, transferType, zhiChiMessageBasebase.getDocId(), zhiChiMessageBasebase.getOriginQuestion(), "0", "", "");
                                } else {
                                    transfer2Custom(0, null, null, null, null, true, zhiChiMessageBasebase.getTransferType(), zhiChiMessageBasebase.getDocId(), zhiChiMessageBasebase.getOriginQuestion(), "0", "", "");
                                }
                            }
                        }
                    }

                    messageAdapter.notifyDataSetChanged();
                    if (SobotMsgManager.getInstance(mAppContext).getConfig(info.getApp_key()).getInitModel() != null) {
                        //机器人接口比较慢的情况下 用户销毁了view 依旧需要保存好机器人回答
                        SobotMsgManager.getInstance(mAppContext).getConfig(info.getApp_key()).addMessage(zhiChiMessageBasebase);
                    }
                    // 智能转人工：机器人优先时，如果未知问题或者向导问题则显示转人工
                    if (type == ZhiChiConstant.type_robot_first && (ZhiChiConstant.type_answer_unknown.equals(zhiChiMessageBasebase
                            .getAnswerType()) || ZhiChiConstant.type_answer_guide.equals(zhiChiMessageBasebase
                            .getAnswerType()))) {
                        showTransferCustomer();
                    }
                    gotoLastItem();
                    break;
                // 修改语音的发送状态
                case ZhiChiConstant.message_type_update_voice:
                    updateVoiceStatusMessage(messageAdapter, msg);
                    break;
                case ZhiChiConstant.message_type_cancel_voice://取消未发送的语音
                    cancelUiVoiceMessage(messageAdapter, msg);
                    break;
                case ZhiChiConstant.hander_sendPicStatus_success:
                    isAboveZero = true;
                    setTimeTaskMethod(handler);
                    String id = (String) msg.obj;
                    updateUiMessageStatus(messageAdapter, id, ZhiChiConstant.MSG_SEND_STATUS_SUCCESS, 0);
                    break;
                case ZhiChiConstant.hander_sendPicStatus_fail:
                    String resultId = (String) msg.obj;
                    updateUiMessageStatus(messageAdapter, resultId, ZhiChiConstant.MSG_SEND_STATUS_ERROR, 0);
                    break;
                case ZhiChiConstant.hander_sendPicIsLoading:
                    String loadId = (String) msg.obj;
                    int uploadProgress = msg.arg1;
                    updateUiMessageStatus(messageAdapter, loadId, ZhiChiConstant.MSG_SEND_STATUS_LOADING, uploadProgress);
                    break;
                case ZhiChiConstant.hander_timeTask_custom_isBusying: // 客服的定时任务
                    // --客服忙碌
                    updateUiMessage(messageAdapter, msg);
                    LogUtils.i("客服的定时任务:" + noReplyTimeCustoms);
                    stopCustomTimeTask();
                    break;
                case ZhiChiConstant.hander_timeTask_userInfo:// 客户的定时任务
                    updateUiMessage(messageAdapter, msg);
                    stopUserInfoTimeTask();
                    LogUtils.i("客户的定时任务的时间  停止定时任务：" + noReplyTimeUserInfo);
                    break;
                case ZhiChiConstant.voiceIsRecoding:
                    // 录音的时间超过一分钟的时间切断进行发送语音
                    if (voiceTimerLong >= minRecordTime * 1000) {
                        isCutVoice = true;
                        voiceCuttingMethod();
                        voiceTimerLong = 0;
                        recording_hint.setText(getResString("sobot_voiceTooLong"));
                        recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg"));
                        recording_timeshort.setVisibility(View.VISIBLE);
                        mic_image.setVisibility(View.GONE);
                        mic_image_animate.setVisibility(View.GONE);
                        closeVoiceWindows(2);
                        btn_press_to_speak.setPressed(false);
                        currentVoiceLong = 0;
                    } else {
                        final int time = Integer.parseInt(msg.obj.toString());
//					LogUtils.i("录音定时任务的时长：" + time);
                        currentVoiceLong = time;
                        if (time < recordDownTime * 1000) {
                            if (time % 1000 == 0) {
                                voiceTimeLongStr = TimeTools.instance.calculatTime(time);
                                voice_time_long.setText(voiceTimeLongStr.substring(3) + "''");
                            }
                        } else if (time < minRecordTime * 1000) {
                            if (time % 1000 == 0) {
                                voiceTimeLongStr = TimeTools.instance.calculatTime(time);
                                voice_time_long.setText(getResString("sobot_count_down") + (minRecordTime * 1000 - time) / 1000);
                            }
                        } else {
                            voice_time_long.setText(getResString("sobot_voiceTooLong"));
                        }
                    }
                    break;
                case ZhiChiConstant.hander_close_voice_view:
                    int longOrShort = msg.arg1;
                    txt_speak_content.setText(getResString("sobot_press_say"));
                    currentVoiceLong = 0;
                    recording_container.setVisibility(View.GONE);

                    if (longOrShort == 0) {
                        for (int i = messageList.size() - 1; i > 0; i--) {
                            if (!TextUtils.isEmpty(messageList.get(i).getSenderType()) &&
                                    Integer.parseInt(messageList.get(i).getSenderType()) == 8) {
                                messageList.remove(i);
                                break;
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //关键词转人工 显示后台设置的提示语 2.9.9添加
    private void addKeyWordTipMsg(SobotKeyWordTransfer keyWordTransfer) {
        if (!TextUtils.isEmpty(keyWordTransfer.getTransferTips())) {
            ZhiChiMessageBase base = new ZhiChiMessageBase();
            base.setT(Calendar.getInstance().getTime().getTime() + "");
            base.setId((initModel != null ? initModel.getCid() : "") + System.currentTimeMillis() + "");
            base.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");
            ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
            reply.setRemindType(ZhiChiConstant.sobot_remind_type_simple_tip);
            reply.setMsg(keyWordTransfer.getTransferTips());
            base.setAnswer(reply);
            messageAdapter.justAddData(base);
        }
    }

    protected void initData() {
        setToolBar();
        initBrocastReceiver();
        initListener();
        setupListView();
        loadUnreadNum();
        //如果进入页面没咨询过，返回时记录当前时间，下次再进入时计算:    当前时间 - 上次页面关闭时间 =时间差
        //比较时间差和用户超时时间， 如果大于用户超时时间，就重新调用初始化接口 ，使用新的cid ,
        //避免长时间不咨询再次进来，会话的创建时间还是很早之前的，保证cid的准确性
        boolean isReCon = false;
        ZhiChiConfig config = SobotMsgManager.getInstance(mAppContext).getConfig(info.getApp_key());
        if (config != null && config.getInitModel() != null && !config.isAboveZero) {
            long pre_finish_time = SharedPreferencesUtil.getLongData(getSobotActivity(), ZhiChiConstant.SOBOT_FINISH_CURTIME, System.currentTimeMillis());
            long cur_tiem_cha = System.currentTimeMillis() - pre_finish_time;
            if (!TextUtils.isEmpty(config.getInitModel().getUserOutTime()) && pre_finish_time > 0) {
                long userOutTime = Long.parseLong(config.getInitModel().getUserOutTime()) * 60 * 1000;
                isReCon = (cur_tiem_cha - userOutTime) > 0 ? true : false;
                LogUtils.i("进入当前界面减去上次界面关闭的时间差：" + cur_tiem_cha + " ms");
                LogUtils.i("用户超时时间：" + userOutTime + " ms");
                LogUtils.i("是否需要重新初始化：" + isReCon);
            }
        }
        initSdk(isReCon, 1);
        //关闭SobotSessionServer里的定时器
        Intent intent = new Intent();
        intent.setAction(ZhiChiConstants.SOBOT_TIMER_BROCAST);
        intent.putExtra("isStartTimer", false);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void setToolBar() {
        if (getView() == null) {
            return;
        }

        if (info != null && info.getTitleImgId() != 0) {
            relative.setBackgroundResource(info.getTitleImgId());
        }
        View rootView = getView();
        View toolBar = rootView.findViewById(getResId("sobot_layout_titlebar"));
        TextView sobot_tv_left = rootView.findViewById(getResId("sobot_tv_left"));
        TextView sobot_tv_right = rootView.findViewById(getResId("sobot_tv_right"));
        sobot_tv_close = rootView.findViewById(getResId("sobot_tv_close"));
//        sobot_tv_close.setText(ResourceUtils.getResString(getSobotActivity(), "sobot_colse"));//不显示文字，用图片代替
        if (toolBar != null) {
            if (sobot_tv_left != null) {
                //找到 Toolbar 的返回按钮,并且设置点击事件,点击关闭这个 Activity
                //设置导航栏返回按钮
                showLeftMenu(sobot_tv_left, getResDrawableId("sobot_icon_back_grey"), "");
                displayInNotch(sobot_tv_left);
                sobot_tv_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onLeftMenuClick();
                    }
                });
            }

            if (sobot_tv_right != null) {
                if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_moreBtnImgId) {
                    showRightMenu(sobot_tv_right, SobotUIConfig.sobot_moreBtnImgId, "");
                } else {
                    showRightMenu(sobot_tv_right, getResDrawableId("sobot_delete_hismsg_selector"), "");
                }

                sobot_tv_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRightMenuClick(v);
                    }
                });
                if (SobotUIConfig.sobot_title_right_menu1_display) {
                    sobot_tv_right.setVisibility(View.VISIBLE);
                } else {
                    sobot_tv_right.setVisibility(View.GONE);
                }
            }
            if (sobot_tv_close != null && info.isShowCloseBtn() && current_client_model == ZhiChiConstant.client_model_customService) {
                //设置导航栏关闭按钮
                sobot_tv_close.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initBrocastReceiver() {
        if (receiver == null) {
            receiver = new MyMessageReceiver();
        }
        // 创建过滤器，并指定action，使之用于接收同action的广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); // 检测网络的状态
        // 注册广播接收器
        getSobotActivity().registerReceiver(receiver, filter);

        if (localReceiver == null) {
            localReceiver = new LocalReceiver();
        }
        localBroadcastManager = LocalBroadcastManager.getInstance(mAppContext);
        // 创建过滤器，并指定action，使之用于接收同action的广播
        IntentFilter localFilter = new IntentFilter();
        localFilter.addAction(ZhiChiConstants.receiveMessageBrocast);
        localFilter.addAction(ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_LOCATION);
        localFilter.addAction(ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_TEXT);
        localFilter.addAction(ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_OBJECT);
        localFilter.addAction(ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_CARD);
        localFilter.addAction(ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_ORDER_CARD);
        localFilter.addAction(ZhiChiConstant.SOBOT_BROCAST_ACTION_TRASNFER_TO_OPERATOR);
        localFilter.addAction(ZhiChiConstants.chat_remind_post_msg);
        localFilter.addAction(ZhiChiConstants.sobot_click_cancle);
        localFilter.addAction(ZhiChiConstants.SOBOT_POST_MSG_TMP_BROCAST);/*选取完留言模版后跳转到留言界面*/
        localFilter.addAction(ZhiChiConstants.dcrc_comment_state);/* 人工客服评论成功 */
        localFilter.addAction(ZhiChiConstants.sobot_close_now);/* 立即结束 */
        localFilter.addAction(ZhiChiConstants.sobot_close_now_clear_cache);// 立即结束不留缓存
        localFilter.addAction(ZhiChiConstants.SOBOT_CHANNEL_STATUS_CHANGE);/* 接收通道状态变化 */
        localFilter.addAction(ZhiChiConstants.SOBOT_BROCAST_KEYWORD_CLICK);/* 机器人转人工关键字  用户选择  技能组  转人工 */
        localFilter.addAction(ZhiChiConstants.SOBOT_BROCAST_REMOVE_FILE_TASK);//取消文件上传
        localFilter.addAction(ZhiChiConstants.chat_remind_to_customer);//转人工
        localFilter.addAction(ZhiChiConstants.SOBOT_CHAT_MUITILEAVEMSG_TO_CHATLIST);//多伦工单节点留言弹窗留言提交后回显到聊天列表
        localFilter.addAction(ZhiChiConstants.SOBOT_CHAT_MUITILEAVEMSG_RE_COMMIT);//多伦工单节点提醒点击后重复弹窗
        localFilter.addAction(ZhiChiConstants.CHAT_REMIND_KEEP_QAUEUING);//点击继续排队
        // 注册广播接收器
        localBroadcastManager.registerReceiver(localReceiver, localFilter);
    }

    private void initListener() {
        //监听聊天的面板
        mKPSwitchListener = KeyboardUtil.attach(getSobotActivity(), mPanelRoot,
                new KeyboardUtil.OnKeyboardShowingListener() {
                    @Override
                    public void onKeyboardShowing(boolean isShowing) {
                        resetEmoticonBtn();
                        if (isShowing) {
                            lv_message.setSelection(messageAdapter.getCount());
                        }
                    }
                });
        KPSwitchConflictUtil.attach(mPanelRoot, btn_upload_view, et_sendmessage);

        notReadInfo.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        btn_send_pic.setOnClickListener(this);
        btn_upload_view.setOnClickListener(this);
        btn_emoticon_view.setOnClickListener(this);
        btn_model_edit.setOnClickListener(this);
        btn_model_voice.setOnClickListener(this);
        sobot_ll_switch_robot.setOnClickListener(this);
        sobot_tv_right_second.setOnClickListener(this);
        sobot_tv_right_third.setOnClickListener(this);
        if (CommonUtils.checkSDKIsZh(getSobotActivity())) {
        } else if (CommonUtils.checkSDKIsEn(getSobotActivity())) {
            btn_set_mode_rengong.setBackgroundResource(ResourceUtils.getDrawableId(getSobotActivity(), "sobot_icon_common_manualwork_en"));
        } else {
            btn_set_mode_rengong.setBackgroundResource(ResourceUtils.getDrawableId(getSobotActivity(), "sobot_icon_common_manualwork"));
        }
        btn_set_mode_rengong.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                doClickTransferBtn();
            }
        });

        lv_message.setDropdownListScrollListener(new DropdownListView.DropdownListScrollListener() {
            @Override
            public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2, int arg3) {
                if (notReadInfo.getVisibility() == View.VISIBLE && messageList.size() > 0 && messageList.size() > firstVisiableItem) {
                    if (messageList.get(firstVisiableItem) != null && messageList.get(firstVisiableItem).getAnswer() != null
                            && ZhiChiConstant.sobot_remind_type_below_unread == messageList.get(firstVisiableItem).getAnswer().getRemindType()) {
                        notReadInfo.setVisibility(View.GONE);
                    }
                }
            }
        });
        if (sobot_tv_close != null) {
            sobot_tv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCloseMenuClick();
                }
            });
        }
        et_sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                resetBtnUploadAndSend();
            }
        });
        et_sendmessage.setSobotAutoCompleteListener(this);
        et_sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doEmoticonBtn2Blur();
                btn_emoticon_view.setBackgroundResource(ResourceUtils.getDrawableId(getContext(), "sobot_emoticon_button_selector"));
                if (SobotApi.getSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN)) {
                    et_sendmessage.dismissPop();
                }

            }
        });
        et_sendmessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean isFocused) {
                if (isFocused) {
                    int length = et_sendmessage.getText().toString().trim().length();
                    if (length != 0) {
                        if (CommonUtils.checkSDKIsZh(getContext()) || CommonUtils.checkSDKIsEn(getContext())) {
                            btn_send.setVisibility(View.VISIBLE);
                            btn_send_pic.setVisibility(View.GONE);
                        } else {
                            btn_send.setVisibility(View.GONE);
                            btn_send_pic.setVisibility(View.VISIBLE);
                        }
                        btn_upload_view.setVisibility(View.GONE);
                    }
                    //根据是否有焦点切换实际的背景
                    // edittext_layout.setBackgroundResource(getResDrawableId("sobot_chatting_bottom_bg_focus"));
                } else {
                    //  edittext_layout.setBackgroundResource(getResDrawableId("sobot_chatting_bottom_bg_blur"));
                }
            }
        });

        et_sendmessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                resetBtnUploadAndSend();
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        btn_press_to_speak.setOnTouchListener(new PressToSpeakListen());
        lv_message.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    hidePanelAndKeyboard(mPanelRoot);
                }
                return false;
            }
        });

        // 开始新会话
        sobot_txt_restart_talk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                initSdk(true, 0);
            }
        });

        sobot_tv_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startToPostMsgActivty(false);
            }
        });

        sobot_tv_satisfaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                submitEvaluation(true, 5, -1, "");
            }
        });
    }

    private void setupListView() {
        messageAdapter = new SobotMsgAdapter(getSobotActivity(), messageList, this);
        lv_message.setAdapter(messageAdapter);
        lv_message.setPullRefreshEnable(true);// 设置下拉刷新列表
        lv_message.setOnRefreshListenerHead(this);
    }

    /**
     * 按住说话动画开始
     */
    private void startMicAnimate() {
        mic_image_animate.setBackgroundResource(getResDrawableId("sobot_voice_animation"));
        animationDrawable = (AnimationDrawable) mic_image_animate.getBackground();
        mic_image_animate.post(new Runnable() {
            @Override
            public void run() {
                animationDrawable.start();
            }
        });
        recording_hint.setText(getResString("sobot_move_up_to_cancel"));
        recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg1"));
    }

    public void closeVoiceWindows(int toLongOrShort) {
        Message message = handler.obtainMessage();
        message.what = ZhiChiConstant.hander_close_voice_view;
        message.arg1 = toLongOrShort;
        handler.sendMessageDelayed(message, 500);
    }

    // 当时间超过1秒的时候自动发送
    public void voiceCuttingMethod() {
        stopVoice();
        sendVoiceMap(1, voiceMsgId);
        voice_time_long.setText("59" + "''");
    }

    /**
     * 开始录音
     */
    private void startVoice() {
        try {
            stopVoice();
            mFileName = SobotPathManager.getInstance().getVoiceDir() + UUID.randomUUID().toString() + ".wav";
            String state = android.os.Environment.getExternalStorageState();
            if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
                LogUtils.i("sd卡被卸载了");
            }
            File directory = new File(mFileName).getParentFile();
            if (!directory.exists() && !directory.mkdirs()) {
                LogUtils.i("文件夹创建失败");
            }
            extAudioRecorder = ExtAudioRecorder.getInstanse(false);
            extAudioRecorder.setOutputFile(mFileName);
            extAudioRecorder.prepare();
            extAudioRecorder.start(new ExtAudioRecorder.AudioRecorderListener() {
                @Override
                public void onHasPermission() {
                    startMicAnimate();
                    startVoiceTimeTask(handler);
                    sendVoiceMap(0, voiceMsgId);
                }

                @Override
                public void onNoPermission() {
                    ToastUtil.showToast(mAppContext, getResString("sobot_no_record_audio_permission"));
                }
            });
        } catch (Exception e) {
            LogUtils.i("prepare() failed");
        }
    }

    /* 停止录音 */
    private void stopVoice() {
        /* 布局的变化 */
        try {
            if (extAudioRecorder != null) {
                stopVoiceTimeTask();
                extAudioRecorder.stop();
                extAudioRecorder.release();
            }
        } catch (Exception e) {
        }
    }

    /**
     * 录音的时间控制
     */
    public void startVoiceTimeTask(final Handler handler) {
        voiceTimerLong = 0;
        stopVoiceTimeTask();
        voiceTimer = new Timer();
        voiceTimerTask = new TimerTask() {
            @Override
            public void run() {
                // 需要做的事:发送消息
                sendVoiceTimeTask(handler);
            }
        };
        // 500ms进行定时任务
        voiceTimer.schedule(voiceTimerTask, 0, 500);

    }

    /**
     * 发送声音的定时的任务
     *
     * @param handler
     */
    public void sendVoiceTimeTask(Handler handler) {
        Message message = handler.obtainMessage();
        message.what = ZhiChiConstant.voiceIsRecoding;
        voiceTimerLong = voiceTimerLong + 500;
        message.obj = voiceTimerLong;
        handler.sendMessage(message);
    }

    public void stopVoiceTimeTask() {
        if (voiceTimer != null) {
            voiceTimer.cancel();
            voiceTimer = null;
        }
        if (voiceTimerTask != null) {
            voiceTimerTask.cancel();
            voiceTimerTask = null;
        }
        voiceTimerLong = 0;
    }

    /**
     * 发送语音的方式
     *
     * @param type       0：正在录制语音。  1：发送语音。2：取消正在录制的语音显示
     * @param voiceMsgId 语音消息ID
     */
    private void sendVoiceMap(int type, String voiceMsgId) {
        // 发送语音的界面
        if (type == 0) {
            sendVoiceMessageToHandler(voiceMsgId, mFileName, voiceTimeLongStr, ZhiChiConstant.MSG_SEND_STATUS_ANIM, SEND_VOICE, handler);
        } else if (type == 2) {
            sendVoiceMessageToHandler(voiceMsgId, mFileName, voiceTimeLongStr, ZhiChiConstant.MSG_SEND_STATUS_ERROR, CANCEL_VOICE, handler);
        } else {
            sendVoiceMessageToHandler(voiceMsgId, mFileName, voiceTimeLongStr, ZhiChiConstant.MSG_SEND_STATUS_LOADING, UPDATE_VOICE, handler);
            // 发送http 返回发送成功的按钮
            sendVoice(voiceMsgId, voiceTimeLongStr, initModel.getCid(), initModel.getPartnerid(), mFileName, handler);
            lv_message.setSelection(messageAdapter.getCount());
        }
        gotoLastItem();
    }

    /**
     * 获取未读消息
     */
    private void loadUnreadNum() {
        mUnreadNum = SobotMsgManager.getInstance(mAppContext).getUnreadCount(info.getApp_key(), true, info.getPartnerid());
    }

    /**
     * 初始化sdk
     *
     * @param isReConnect 是否是重新接入
     **/
    private void initSdk(boolean isReConnect, int isFirstEntry) {
        if (isReConnect) {
            current_client_model = ZhiChiConstant.client_model_robot;
            showTimeVisiableCustomBtn = 0;
            messageList.clear();
            messageAdapter.notifyDataSetChanged();
            cids.clear();
            currentCidPosition = 0;
            queryCidsStatus = ZhiChiConstant.QUERY_CIDS_STATUS_INITIAL;
            isNoMoreHistoryMsg = false;
            isAboveZero = false;
            isComment = false;// 重新开始会话时 重置为 没有评价过
            customerState = CustomerState.Offline;
            remindRobotMessageTimes = 0;
            queueTimes = 0;
            isSessionOver = false;
            isHasRequestQueryFrom = false;

            sobot_txt_restart_talk.setVisibility(View.GONE);
            sobot_tv_message.setVisibility(View.GONE);
            sobot_tv_satisfaction.setVisibility(View.GONE);
            image_reLoading.setVisibility(View.VISIBLE);
            AnimationUtil.rotate(image_reLoading);

            lv_message.setPullRefreshEnable(true);// 设置下拉刷新列表

            String last_current_dreceptionistId = SharedPreferencesUtil.getStringData(
                    mAppContext, info.getApp_key() + "_" + ZhiChiConstant.SOBOT_RECEPTIONISTID, "");
            info.setChoose_adminid(last_current_dreceptionistId);
            resetUser(isFirstEntry);
        } else {
            //检查配置项是否发生变化
            if (ChatUtils.checkConfigChange(mAppContext, info.getApp_key(), info)) {
                resetUser(isFirstEntry);
            } else {
                doKeepsessionInit(isFirstEntry);
            }
        }
        SharedPreferencesUtil.saveBooleanData(mAppContext,
                "refrashSatisfactionConfig", true);
        resetBtnUploadAndSend();
    }

    /**
     * 重置用户
     */
    private void resetUser(int isFirstEntry) {
        String platformID = SharedPreferencesUtil.getStringData(mAppContext, ZhiChiConstant.SOBOT_PLATFORM_UNIONCODE, "");
        //电商标示为fasle 或者 platformUnionCode 都认为是普通版，重置用户是都要结束会话
        if (!SobotVerControl.isPlatformVer || TextUtils.isEmpty(platformID)) {
            zhiChiApi.disconnChannel();
        }
        clearCache();
        SharedPreferencesUtil.saveStringData(mAppContext,
                info.getApp_key() + "_" + ZhiChiConstant.sobot_last_login_group_id, TextUtils.isEmpty(info.getGroupid()) ? "" : info.getGroupid());
        customerInit(isFirstEntry);
    }

    /**
     * 调用初始化接口
     */
    private void customerInit(int isFirstEntry) {
        LogUtils.i("customerInit初始化接口");
        if (!isAppInitEnd) {
            LogUtils.i("初始化接口appinit 接口还没结束，结束前不能重复调用");
            return;
        }
        isAppInitEnd = false;
        if (info != null) {
            info.setIsFirstEntry(isFirstEntry);
        }

        //如果ZCSobotApi的设置了自定应答语，info里没有设置，会把ZCSobotApi的赋值给info
        String robot_Hello_Word = SharedPreferencesUtil.getStringData(getSobotActivity(), ZhiChiConstant.SOBOT_ROBOT_HELLO_WORD, "");
        String user_Out_Word = SharedPreferencesUtil.getStringData(getSobotActivity(), ZhiChiConstant.SOBOT_USER_OUT_WORD, "");
        String user_Tip_Word = SharedPreferencesUtil.getStringData(getSobotActivity(), ZhiChiConstant.SOBOT_USER_TIP_WORD, "");
        String admin_Hello_Word = SharedPreferencesUtil.getStringData(getSobotActivity(), ZhiChiConstant.SOBOT_ADMIN_HELLO_WORD, "");
        String admin_Offline_Title = SharedPreferencesUtil.getStringData(getSobotActivity(), ZhiChiConstant.SOBOT_ADMIN_OFFLINE_TITLE, "");
        String admin_Tip_Word = SharedPreferencesUtil.getStringData(getSobotActivity(), ZhiChiConstant.SOBOT_ADMIN_TIP_WORD, "");

        if (info != null) {
            if (TextUtils.isEmpty(info.getRobot_hello_word()) && !TextUtils.isEmpty(robot_Hello_Word)) {
                info.setRobot_Hello_Word(robot_Hello_Word);
            }
            if (TextUtils.isEmpty(info.getUser_out_word()) && !TextUtils.isEmpty(user_Out_Word)) {
                info.setUser_Out_Word(user_Out_Word);
            }
            if (TextUtils.isEmpty(info.getUser_tip_word()) && !TextUtils.isEmpty(user_Tip_Word)) {
                info.setUser_Tip_Word(user_Tip_Word);
            }
            if (TextUtils.isEmpty(info.getAdmin_hello_word()) && !TextUtils.isEmpty(admin_Hello_Word)) {
                info.setAdmin_Hello_Word(admin_Hello_Word);
            }
            if (TextUtils.isEmpty(info.getAdmin_offline_title()) && !TextUtils.isEmpty(admin_Offline_Title)) {
                info.setAdmin_Offline_Title(admin_Offline_Title);
            }
            if (TextUtils.isEmpty(info.getAdmin_tip_word()) && !TextUtils.isEmpty(admin_Tip_Word)) {
                info.setAdmin_Tip_Word(admin_Tip_Word);
            }
        }

        zhiChiApi.sobotInit(SobotChatFSFragment.this, info, new StringResultCallBack<ZhiChiInitModeBase>() {
            @Override
            public void onSuccess(ZhiChiInitModeBase result) {
                isAppInitEnd = true;
                if (!isActive()) {
                    return;
                }
                SharedPreferencesUtil.saveStringData(getSobotActivity(), ZhiChiConstant.sobot_connect_group_id, "");
                SharedPreferencesUtil.saveLongData(getSobotActivity(), ZhiChiConstant.SOBOT_FINISH_CURTIME, 0);
                initModel = result;

                processPlatformAppId();
                getAnnouncement();
                if (info.getService_mode() > 0) {
                    initModel.setType(info.getService_mode() + "");
                }
                type = Integer.parseInt(initModel.getType());
                SharedPreferencesUtil.saveIntData(mAppContext,
                        info.getApp_key() + "_" + ZhiChiConstant.initType, type);
                //初始化查询cid
                queryCids();
                SharedPreferencesUtil.saveBooleanData(mAppContext,
                        "refrashSatisfactionConfig", true);
                //查询自定义标签
                sobotCustomMenu();

                //设置初始layout,无论什么模式都是从机器人的UI变过去的
                showRobotLayout();

                if (!TextUtils.isEmpty(initModel.getPartnerid())) {
                    SharedPreferencesUtil.saveStringData(mAppContext, Const.SOBOT_UID, initModel.getPartnerid());
                }
                if (!TextUtils.isEmpty(initModel.getCid())) {
                    SharedPreferencesUtil.saveStringData(mAppContext, Const.SOBOT_CID, initModel.getCid());
                }
                SharedPreferencesUtil.saveIntData(mAppContext,
                        ZhiChiConstant.sobot_msg_flag, initModel.getMsgFlag());
                SharedPreferencesUtil.saveBooleanData(mAppContext,
                        ZhiChiConstant.sobot_leave_msg_flag, initModel.isMsgToTicketFlag());
                SharedPreferencesUtil.saveStringData(mAppContext,
                        "lastCid", initModel.getCid());
                SharedPreferencesUtil.saveStringData(mAppContext,
                        info.getApp_key() + "_" + ZhiChiConstant.sobot_last_current_partnerId, info.getPartnerid());
                SharedPreferencesUtil.saveOnlyStringData(mAppContext,
                        ZhiChiConstant.sobot_last_current_appkey, info.getApp_key());
                SharedPreferencesUtil.saveObject(mAppContext,
                        ZhiChiConstant.sobot_last_current_info, info);
                SharedPreferencesUtil.saveObject(mAppContext,
                        ZhiChiConstant.sobot_last_current_initModel, initModel);
                SharedPreferencesUtil.saveOnlyStringData(mAppContext, ZhiChiConstant.sobot_last_current_customer_code, info.getCustomer_code());
                SharedPreferencesUtil.saveStringData(mAppContext, info.getApp_key() + "_" + ZhiChiConstant.SOBOT_RECEPTIONISTID, TextUtils.isEmpty(info.getChoose_adminid()) ? "" : info.getChoose_adminid());
                SharedPreferencesUtil.saveStringData(mAppContext, info.getApp_key() + "_" + ZhiChiConstant.SOBOT_ROBOT_CODE, TextUtils.isEmpty(info.getRobotCode()) ? "" : info.getRobotCode());
                SharedPreferencesUtil.saveStringData(mAppContext, info.getApp_key() + "_" + ZhiChiConstant.sobot_last_current_remark, TextUtils.isEmpty(info.getRemark()) ? "" : info.getRemark());
                SharedPreferencesUtil.saveStringData(mAppContext, info.getApp_key() + "_" + ZhiChiConstant.sobot_last_current_groupid, TextUtils.isEmpty(info.getGroupid()) ? "" : info.getGroupid());
                SharedPreferencesUtil.saveIntData(mAppContext, info.getApp_key() + "_" + ZhiChiConstant.sobot_last_current_service_mode, info.getService_mode());
                SharedPreferencesUtil.saveStringData(mAppContext, info.getApp_key() + "_" + ZhiChiConstant.sobot_last_current_customer_fields, TextUtils.isEmpty(info.getCustomer_fields()) ? "" : info.getCustomer_fields());
                SharedPreferencesUtil.saveStringData(mAppContext, info.getApp_key() + "_" + ZhiChiConstant.sobot_last_current_params, TextUtils.isEmpty(info.getParams()) ? "" : info.getParams());
                SharedPreferencesUtil.saveStringData(mAppContext, info.getApp_key() + "_" + ZhiChiConstant.sobot_last_current_isvip, TextUtils.isEmpty(info.getIsVip()) ? "" : info.getIsVip());
                SharedPreferencesUtil.saveStringData(mAppContext, info.getApp_key() + "_" + ZhiChiConstant.sobot_last_current_vip_level, TextUtils.isEmpty(info.getVip_level()) ? "" : info.getVip_level());
                SharedPreferencesUtil.saveStringData(mAppContext, info.getApp_key() + "_" + ZhiChiConstant.sobot_last_current_user_label, TextUtils.isEmpty(info.getUser_label()) ? "" : info.getUser_label());
                SharedPreferencesUtil.saveStringData(mAppContext, info.getApp_key() + "_" + ZhiChiConstant.sobot_last_current_robot_alias, TextUtils.isEmpty(info.getRobot_alias()) ? "" : info.getRobot_alias());

                // 通告不置顶
                if (initModel.getAnnounceMsgFlag() && !initModel.isAnnounceTopFlag() && !TextUtils.isEmpty(initModel.getAnnounceMsg())) {
                    ZhiChiMessageBase noticeModel = ChatUtils.getNoticeModel(getContext(), initModel);
                    messageAdapter.justAddData(noticeModel);
                    messageAdapter.notifyDataSetChanged();
                }

                //如果有离线直接转人工功能开启，判断离线客服id有值，直接转人工
                if (initModel.getOfflineMsgConnectFlag() == 1 && !TextUtils.isEmpty(initModel.getOfflineMsgAdminId())
                        && !"null".equals(initModel.getOfflineMsgAdminId())) {
                    offlineMsgConnectFlag = initModel.getOfflineMsgConnectFlag();
                    offlineMsgAdminId = initModel.getOfflineMsgAdminId();
                    SobotConnCusParam param = new SobotConnCusParam();
                    connectCustomerService(null, false);
                    return;
                }

                if (type == ZhiChiConstant.type_robot_only) {
                    remindRobotMessage(handler, initModel, info);
                    showSwitchRobotBtn();
                    if (SobotOption.sobotChatStatusListener != null) {
                        //修改聊天状态为机器人状态
                        SobotOption.sobotChatStatusListener.onChatStatusListener(SobotChatStatusMode.ZCServerConnectRobot);
                    }
                } else if (type == ZhiChiConstant.type_robot_first) {
                    //机器人优先
                    if (initModel.getUstatus() == ZhiChiConstant.ustatus_online || initModel.getUstatus() == ZhiChiConstant.ustatus_queue) {
                        //需要判断  是否需要保持会话
                        if (initModel.getUstatus() == ZhiChiConstant.ustatus_queue) {
                            //机器人会话 欢迎语、常见问题
                            remindRobotMessage(handler, initModel, info);
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                connectCustomerService(null);
                            }
                        }, 700);
                    } else {
                        //仅机器人或者机器人优先，不需要保持会话
                        remindRobotMessage(handler, initModel, info);
                        showSwitchRobotBtn();
                        if (SobotOption.sobotChatStatusListener != null) {
                            //修改聊天状态为机器人状态
                            SobotOption.sobotChatStatusListener.onChatStatusListener(SobotChatStatusMode.ZCServerConnectRobot);
                        }
                    }
                } else {
                    if (type == ZhiChiConstant.type_custom_only) {
                        //仅人工客服
                        if (isUserBlack()) {
                            showLeaveMsg();
                        } else {
                            if (initModel.getUstatus() == ZhiChiConstant.ustatus_online || initModel.getUstatus() == ZhiChiConstant.ustatus_queue) {
                                connectCustomerService(null);
                            } else {
                                if (initModel.getInvalidSessionFlag() == 1) {
                                    //设置底部键盘
                                    setBottomView(ZhiChiConstant.bottomViewtype_onlyrobot);
                                    btn_set_mode_rengong.setVisibility(View.GONE);
                                    btn_model_edit.setVisibility(View.GONE);
                                    btn_model_voice.setVisibility(View.GONE);
                                    btn_emoticon_view.setVisibility(View.VISIBLE);
                                    setAvatar(getResDrawableId("sobot_def_admin"), true);
                                    setTitle("", false);
                                } else {
                                    transfer2Custom(null, null, null, true, "0");
                                }
                            }
                        }
                    } else if (type == ZhiChiConstant.type_custom_first) {
                        if (initModel.getUstatus() == ZhiChiConstant.ustatus_online || initModel.getUstatus() == ZhiChiConstant.ustatus_queue) {
                            //需要判断  是否需要保持会话
                            if (initModel.getUstatus() == ZhiChiConstant.ustatus_queue) {
                                //机器人会话 欢迎语、常见问题
                                remindRobotMessage(handler, initModel, info);
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    connectCustomerService(null);
                                }
                            }, 700);
                        } else {
                            if (initModel.getInvalidSessionFlag() == 1) {
                                remindRobotMessage(handler, initModel, info);
                                //人工优先模式，开启延迟转人工后，只要自动发送消息对象不为空并且不是默认的，就触发转人工
                                if (info.getAutoSendMsgMode() != null && info.getAutoSendMsgMode() != SobotAutoSendMsgMode.Default) {
                                    doClickTransferBtn();
                                }
                            } else {
                                //客服优先
                                showSwitchRobotBtn();
                                transfer2Custom(null, null, null, true, "0");
                            }
                        }
                    }
                }
                isSessionOver = false;
                //检查右上角的关闭按钮是否应该显示
                if (sobot_tv_close != null) {
                    if (info.isShowCloseBtn() && current_client_model == ZhiChiConstant.client_model_customService) {
                        //显示右上角的关闭按钮
                        sobot_tv_close.setVisibility(View.VISIBLE);
                    } else {
                        sobot_tv_close.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                isAppInitEnd = true;
                SharedPreferencesUtil.saveObject(mAppContext,
                        ZhiChiConstant.sobot_last_current_info, info);
                if (!isActive()) {
                    return;
                }
                if (e instanceof IllegalArgumentException && !TextUtils.isEmpty(des)) {
                    ToastUtil.showToast(mAppContext, des);
                    finish();
                } else {
                    showInitError();
                }
                isSessionOver = true;
            }
        });
    }

    /**
     * 特殊处理电商版传CustomerCode的情况
     */
    private void processPlatformAppId() {
        if (SobotVerControl.isPlatformVer && !TextUtils.isEmpty(info.getCustomer_code())) {
            if (!TextUtils.isEmpty(initModel.getAppId())) {
                info.setApp_key(initModel.getAppId());
            }

            SharedPreferencesUtil.saveStringData(mAppContext, ZhiChiConstant.SOBOT_CURRENT_IM_APPID, info.getApp_key());
        }
    }

    /**
     * 会话保持初始化的逻辑
     */
    private void doKeepsessionInit(int isFirstEntry) {
        List<ZhiChiMessageBase> tmpList = SobotMsgManager.getInstance(mAppContext).getConfig(info.getApp_key()).getMessageList();
        if (tmpList != null && SobotMsgManager.getInstance(mAppContext).getConfig(info.getApp_key()).getInitModel() != null) {
            //有数据
            int lastType = SharedPreferencesUtil.getIntData(mAppContext,
                    info.getApp_key() + "_" + ZhiChiConstant.initType, -1);
            if (info.getService_mode() < 0 || lastType == info.getService_mode()) {
                if (!TextUtils.isEmpty(info.getGroupid())) {
                    //判断是否是上次的技能组
                    String lastUseGroupId = SharedPreferencesUtil.getStringData(mAppContext, info.getApp_key() + "_" + ZhiChiConstant.sobot_last_login_group_id, "");
                    if (lastUseGroupId.equals(info.getGroupid())) {
                        keepSession(tmpList);
                    } else {
                        resetUser(isFirstEntry);
                    }
                } else {
                    keepSession(tmpList);
                }
            } else {
                resetUser(isFirstEntry);
            }
        } else {
            resetUser(isFirstEntry);
        }
    }

    /**
     * 显示下线的逻辑
     *
     * @param initModel
     * @param outLineType 下线的类型
     */
    public void customerServiceOffline(ZhiChiInitModeBase initModel, int outLineType) {
        if (initModel == null) {
            return;
        }
        queueNum = 0;
        stopInputListener();
        stopUserInfoTimeTask();
        stopCustomTimeTask();
        customerState = CustomerState.Offline;

        // 设置提醒
        showOutlineTip(initModel, outLineType);
        //更改底部键盘
        setBottomView(ZhiChiConstant.bottomViewtype_outline);
        //隐藏底部标签控件
        sobot_custom_menu.setVisibility(View.GONE);
        mBottomViewtype = ZhiChiConstant.bottomViewtype_outline;

        if (Integer.parseInt(initModel.getType()) == ZhiChiConstant.type_custom_only) {
            if (1 == outLineType) {
                //如果在排队中 客服离开，那么提示无客服
                showLogicTitle(getResString("sobot_no_access"), null, false);
            }
            if (9 == outLineType) {
                //排队自动断开 不显示头部标题
                if (mTitleTextView != null) {
                    mTitleTextView.setVisibility(View.GONE);
                }
            }
        }

        if (6 == outLineType) {
            LogUtils.i("打开新窗口");
        }
        isSessionOver = true;
        // 发送用户离线的广播
        CommonUtils.sendLocalBroadcast(mAppContext, new Intent(Const.SOBOT_CHAT_USER_OUTLINE));
        stopPolling();
    }

    /**
     * 下线的逻辑,无UI修改
     *
     * @param initModel
     */
    public void customerServiceOffline(ZhiChiInitModeBase initModel) {
        if (initModel == null) {
            return;
        }
        queueNum = 0;
        stopInputListener();
        stopUserInfoTimeTask();
        stopCustomTimeTask();
        customerState = CustomerState.Offline;
        //更改底部键盘
        setBottomView(ZhiChiConstant.bottomViewtype_outline);
        //隐藏底部标签控件
        sobot_custom_menu.setVisibility(View.GONE);
        mBottomViewtype = ZhiChiConstant.bottomViewtype_outline;
        isSessionOver = true;
        // 发送用户离线的广播
        CommonUtils.sendLocalBroadcast(mAppContext, new Intent(Const.SOBOT_CHAT_USER_OUTLINE));
        stopPolling();
    }

    /**
     * 发出离线提醒
     *
     * @param initModel
     * @param outLineType 下线类型
     */
    private void showOutlineTip(ZhiChiInitModeBase initModel, int outLineType) {
        if (SobotOption.sobotChatStatusListener != null) {
            //修改聊天状态为离线状态
            SobotOption.sobotChatStatusListener.onChatStatusListener(SobotChatStatusMode.ZCServerConnectOffline);
        }
        String offlineMsg = ChatUtils.getMessageContentByOutLineType(getSobotActivity(), initModel, outLineType);
        if (!TextUtils.isEmpty(offlineMsg)) {
            ZhiChiMessageBase base = new ZhiChiMessageBase();
            base.setT(Calendar.getInstance().getTime().getTime() + "");
            ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
            base.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");
            reply.setRemindType(ZhiChiConstant.sobot_remind_type_outline);
            base.setAnswer(reply);
            if (1 == outLineType) {
                if (!TextUtils.isEmpty(currentUserName)) {
                    offlineMsg = offlineMsg.replace("#" + ResourceUtils.getResString(getContext(), "sobot_cus_service") + "#", currentUserName).replace("#客服#", currentUserName).replace("#agent#", currentUserName);
                } else {
                    offlineMsg = ResourceUtils.getResString(getContext(), "sobot_outline_leverByManager");
                }
                base.setAction(ZhiChiConstant.sobot_outline_leverByManager);
            } else if (2 == outLineType) {
                base.setAction(ZhiChiConstant.sobot_outline_leverByManager);
            } else if (3 == outLineType) {
                base.setAction(ZhiChiConstant.sobot_outline_leverByManager);
                if (initModel != null) {
                    initModel.setIsblack("1");
                }
            } else if (5 == outLineType) {
                base.setAction(ZhiChiConstant.sobot_outline_leverByManager);
            } else if (4 == outLineType) {
                base.setAction(ZhiChiConstant.action_remind_past_time);
            } else if (6 == outLineType) {
                base.setAction(ZhiChiConstant.sobot_outline_leverByManager);
            } else if (99 == outLineType) {
                //留言转离线消息 成功后结束会话，添加提示语
                base.setAction(ZhiChiConstant.sobot_outline_leverByManager);
            } else if (9 == outLineType) {
                //排队断开
                base.setAction(ZhiChiConstant.sobot_outline_leverByManager);
            } else {
                //只要是204消息，最后肯定会结束会话
                base.setAction(ZhiChiConstant.sobot_outline_leverByManager);
            }
            reply.setMsg(offlineMsg);
            // 提示会话结束
            updateUiMessage(messageAdapter, base);
        }
    }

    /**
     * 显示排队提醒
     */
    private void showInLineHint(String queueDoc) {
        // 更新界面的操作
        if (!TextUtils.isEmpty(queueDoc)) {
            updateUiMessage(messageAdapter, ChatUtils.getInLineHint(queueDoc));
            gotoLastItem();
        }
    }

    //保持会话
    private void keepSession(List<ZhiChiMessageBase> tmpList) {
        ZhiChiConfig config = SobotMsgManager.getInstance(mAppContext).getConfig(info.getApp_key());
        initModel = config.getInitModel();
        updateFloatUnreadIcon();
        mUnreadNum = 0;
        messageAdapter.addData(tmpList);
        messageAdapter.notifyDataSetChanged();
        current_client_model = config.current_client_model;
        type = Integer.parseInt(initModel.getType());

        String currentCid = initModel.getCid();
        if (preCurrentCid == null) {
            statusFlag = 0;
        } else if (!currentCid.equals(preCurrentCid)) {
            statusFlag = 0;
        }
        SharedPreferencesUtil.saveIntData(mAppContext,
                info.getApp_key() + "_" + ZhiChiConstant.initType, type);
        LogUtils.i("sobot----type---->" + type);
        showLogicTitle(config.activityTitle, config.adminFace, false);
        showSwitchRobotBtn();
        customerState = config.customerState;
        remindRobotMessageTimes = config.remindRobotMessageTimes;
        isComment = config.isComment;
        isAboveZero = config.isAboveZero;
        currentUserName = config.currentUserName;
        isNoMoreHistoryMsg = config.isNoMoreHistoryMsg;
        currentCidPosition = config.currentCidPosition;
        queryCidsStatus = config.queryCidsStatus;
        isShowQueueTip = config.isShowQueueTip;
        if (config.cids != null) {
            cids.addAll(config.cids);
        }
        showTimeVisiableCustomBtn = config.showTimeVisiableCustomBtn;
        queueNum = config.queueNum;
        if (isNoMoreHistoryMsg) {
            lv_message.setPullRefreshEnable(false);// 设置下拉刷新列表
        }
        setAdminFace(config.adminFace);
        setAdminName(config.adminName);
        mBottomViewtype = config.bottomViewtype;
        setBottomView(config.bottomViewtype);
        isChatLock = config.isChatLock;
        if (type == ZhiChiConstant.type_custom_only && statusFlag == 0) {
            //仅人工客服
            preCurrentCid = currentCid;
            if (isUserBlack()) {
                showLeaveMsg();
            } else {
                if (initModel.getInvalidSessionFlag() == 1) {
                    //设置底部键盘
                    setBottomView(ZhiChiConstant.bottomViewtype_onlyrobot);
                    btn_set_mode_rengong.setVisibility(View.GONE);
                    btn_model_edit.setVisibility(View.GONE);
                    btn_model_voice.setVisibility(View.GONE);
                    btn_emoticon_view.setVisibility(View.VISIBLE);
                    tempMsgContent = config.tempMsgContent;
                    setAvatar(getResDrawableId("sobot_def_admin"), true);
                    setTitle("", false);
                } else {
                    transfer2Custom(null, null, null, true, "1");
                }
            }
        }
        if (type == ZhiChiConstant.type_custom_first && statusFlag == 0) {
            //人工优先
            tempMsgContent = config.tempMsgContent;
        }
        LogUtils.i("sobot----isChatLock--->" + "userInfoTimeTask " + config.userInfoTimeTask + "=====customTimeTask====" + config.customTimeTask + isChatLock);
        paseReplyTimeCustoms = config.paseReplyTimeCustoms;
        paseReplyTimeUserInfo = config.paseReplyTimeUserInfo;
        if (config.userInfoTimeTask && isChatLock != 1) {
            stopUserInfoTimeTask();
            startUserInfoTimeTask(handler);
            noReplyTimeUserInfo = config.paseReplyTimeUserInfo;
        }
        if (config.customTimeTask && isChatLock != 1) {
            stopCustomTimeTask();
            startCustomTimeTask(handler);
            noReplyTimeCustoms = config.paseReplyTimeCustoms;
        }
        if (info.getAutoSendMsgMode().geIsEveryTimeAutoSend()) {
            //每次都发
            config.isProcessAutoSendMsg = true;
        }
        if (config.isProcessAutoSendMsg) {
            //自动发一条信息
            if (info.getAutoSendMsgMode().getAuto_send_msgtype() == ZCMessageTypeText) {
                //自动发送文本消息
                processAutoSendMsg(info);
            } else {
                //只有人工在线的模式下才会自动发送消息 (图片、文件、视频)
                if (info.getAutoSendMsgMode() != null && info.getAutoSendMsgMode() != SobotAutoSendMsgMode.Default && current_client_model == ZhiChiConstant.client_model_customService && !TextUtils.isEmpty(info.getAutoSendMsgMode().getContent())) {
                    if (info.getAutoSendMsgMode() == SobotAutoSendMsgMode.SendToOperator && customerState == CustomerState.Online) {
                        //发送内容
                        String content = info.getAutoSendMsgMode().getContent();
                        if (info.getAutoSendMsgMode().getAuto_send_msgtype() == ZCMessageTypeFile) {
                            //发送文件
                            File sendFile = new File(content);
                            if (sendFile.exists()) {
                                uploadFile(sendFile, handler, lv_message, messageAdapter, false);
                            }
                        } else if (info.getAutoSendMsgMode().getAuto_send_msgtype() == ZCMessageTypeVideo) {
                            //发送视频
                            File sendFile = new File(content);
                            if (sendFile.exists()) {
                                uploadVideo(sendFile, null, messageAdapter);
                            }
                        } else if (info.getAutoSendMsgMode().getAuto_send_msgtype() == ZCMessageTypePhoto) {
                            //发送图片
                            File sendFile = new File(content);
                            if (sendFile.exists()) {
                                uploadFile(sendFile, handler, lv_message, messageAdapter, false);
                            }
                        }
                    }
                }
            }
            config.isProcessAutoSendMsg = false;
        }
        //设置自动补全参数
        et_sendmessage.setRequestParams(initModel.getPartnerid(), initModel.getRobotid());
        if (customerState == CustomerState.Online && current_client_model == ZhiChiConstant.client_model_customService) {
            createConsultingContent(1);
            createOrderCardContent(1);
            //人工模式关闭自动补全功能
            et_sendmessage.setAutoCompleteEnable(false);
            //显示客服头像
            showLogicTitle(null, config.adminFace, false);
        } else {
            //其他状态下开启自动补全
            et_sendmessage.setAutoCompleteEnable(true);
            //显示机器人头像
            showLogicTitle(null, initModel.getRobotLogo(), false);
        }
        lv_message.setSelection(messageAdapter.getCount());
        getAnnouncement();
        sobotCustomMenu();
        SharedPreferencesUtil.saveBooleanData(mAppContext,
                "refrashSatisfactionConfig", true);
        config.clearMessageList();
        config.clearInitModel();
        isSessionOver = false;
        for (int i = messageList.size() - 1; i > 0; i--) {
            if (!TextUtils.isEmpty(messageList.get(i).getSenderType()) && Integer.parseInt(messageList.get(i).getSenderType()) == ZhiChiConstant.message_sender_type_remide_info
                    && messageList.get(i).getAnswer() != null
                    && ZhiChiConstant.sobot_remind_type_simple_tip == messageList.get(i).getAnswer().getRemindType()) {
                messageList.remove(i);
                messageAdapter.notifyDataSetChanged();
                break;
            }
        }
        processNewTicketMsg(handler);
        inPolling = config.inPolling;
        //如果当前是人工模式，又在轮询，就启动轮询方法
        if (current_client_model == ZhiChiConstant.client_model_customService && inPolling && !CommonUtils.isServiceWork(getSobotActivity(), "com.sobot.chat.core.channel.SobotTCPServer")) {
            startPolling();
        }
    }

    /**
     * 机器人智能转人工时，判断是否应该显示转人工按钮
     */
    private void showTransferCustomer() {
        showTimeVisiableCustomBtn++;
        if (showTimeVisiableCustomBtn >= info.getArtificialIntelligenceNum()) {
            btn_set_mode_rengong.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取客户传入的技能组id 直接转人工
     */
    private void transfer2CustomBySkillId(SobotConnCusParam cusParam, int transferType) {
        if (cusParam == null) {
            SobotConnCusParam param = new SobotConnCusParam();
            param.setGroupId(info.getGroupid());
            param.setGroupName(info.getGroup_name());
            param.setTransferType(transferType);
            requestQueryFrom(param, info.isCloseInquiryForm());
        } else {
            requestQueryFrom(cusParam, info.isCloseInquiryForm());
        }
    }

    /**
     * 显示表情按钮   如果没有表情资源则不会显示此按钮
     */
    private void showEmotionBtn() {
        Map<String, String> mapAll = DisplayEmojiRules.getMapAll(mAppContext);
        if (mapAll.size() > 0) {
            btn_emoticon_view.setVisibility(View.VISIBLE);
        } else {
            btn_emoticon_view.setVisibility(View.GONE);
        }
    }

    private void transfer2Custom(String tempGroupId, String keyword, String keywordId, boolean isShowTips) {
        transfer2Custom(0, null, tempGroupId, keyword, keywordId, isShowTips, 0, "", "", "0", "", "");
    }

    private void transfer2Custom(String tempGroupId, String keyword, String keywordId, boolean isShowTips, String activeTransfer) {
        transfer2Custom(0, null, tempGroupId, keyword, keywordId, isShowTips, 0, "", "", activeTransfer, "", "");
    }

    private void transfer2Custom(String tempGroupId, String keyword, String keywordId, boolean isShowTips, String docId, String unknownQuestion, String activeTransfer) {
        transfer2Custom(0, null, tempGroupId, keyword, keywordId, isShowTips, 0, docId, unknownQuestion, activeTransfer, "", "");
    }

    /**
     * 转人工按钮的逻辑封装
     * 如果用户传入了skillId 那么就用这个id直接转人工
     * 如果没有传  那么就检查技能组开关是否打开
     *
     * @param eventType         特殊业务可以根据对应类型处理
     * @param tempChooseAdminId 客服id
     * @param tempGroupId       技能组id
     * @param keyword           触发转人工的关键词
     * @param keywordId         触发转人工的关键词id
     * @param isShowTips        是否显示提示
     * @param transferType      转人工类型 重复提问、情绪负向转人工 传入后台做统计用
     *                          0普通 1重复提问 2情绪负向 转人工 3-关键词转人工 4-多伦会话转人工
     *                          5:机器人自动转人工(拆分 6-9 activeTransfer此时为1 根据answerType转换6-9)
     *                          6直接转人工，7理解转人工，8引导转人工，9未知转人工 10，点踩转人工
     * @param docId             词条触发转人工的词条id 指得是之前的transferType=5，现在的（6-9）的时候的词条id
     * @param unknownQuestion   未知问题触发转人工的客户问的未知问题
     * @param activeTransfer    转人工方式  0：机器人触发转人工 1：客户主动转人工
     * @param answerMsgId       消息id（直接回答的转人工按钮，对应的消息id）
     * @param ruleId            一问多答时的规则id，没有传入“”
     */
    private void transfer2Custom(int eventType, String tempChooseAdminId, String tempGroupId, String keyword, String keywordId,
                                 boolean isShowTips, int transferType, String docId, String unknownQuestion, String
                                         activeTransfer, String answerMsgId, String ruleId) {
        SobotConnCusParam param = new SobotConnCusParam();
        param.setEventType(eventType);
        if (info != null) {
            param.setGroupId(StringUtils.checkStringIsNull(info.getGroupid()));
            param.setGroupName(StringUtils.checkStringIsNull(info.getGroup_name()));
            param.setChooseAdminId(StringUtils.checkStringIsNull(info.getChoose_adminid()));
        }
        param.setKeyword(keyword);
        param.setKeywordId(keywordId);
        param.setDocId(docId);
        param.setUnknownQuestion(unknownQuestion);
        param.setTransferType(transferType);
        param.setActiveTransfer(activeTransfer);
        param.setAnswerMsgId(answerMsgId);
        param.setRuleId(ruleId);
        //如果有业务（关键词转人工、多轮1526 转人工）技能组id 和客服id ，就覆盖进线info里边的技能组id 和客服id
        if (StringUtils.isNoEmpty(tempChooseAdminId)) {
            param.setChooseAdminId(tempChooseAdminId);
        }
        if (StringUtils.isNoEmpty(tempGroupId)) {
            param.setGroupId(tempGroupId);
            param.setGroupName("");
        }
        if (SobotOption.transferOperatorInterceptor != null) {
            // 拦截转人工
            SobotTransferOperatorParam tparam = new SobotTransferOperatorParam();
            tparam.setGroupId(tempGroupId);
            tparam.setKeyword(keyword);
            tparam.setKeywordId(keywordId);
            tparam.setShowTips(isShowTips);
            tparam.setTransferType(transferType);
            tparam.setConsultingContent(info.getConsultingContent());
            SobotOption.transferOperatorInterceptor.onTransferStart(getContext(), tparam);
        } else if (StringUtils.isNoEmpty(param.getGroupId())
                || StringUtils.isNoEmpty(param.getChooseAdminId())
                || StringUtils.isNoEmpty(info.getTransferAction())
                || initModel.isSmartRouteInfoFlag()) {
            //指定了客服id、指定了技能组、配置了转人工溢出、开启智能路由 ，只要有一个条件满足就直接先走询前表单然后转人工
            requestQueryFrom(param, info.isCloseInquiryForm());
        } else {
            if (initModel.getGroupflag().equals(ZhiChiConstant.groupflag_on)) {
                //如果技能组开启，同时没有开启智能路由，那么拉取技能组数据
                getGroupInfo(param);
            } else {
                //直接转人工
                requestQueryFrom(param, info.isCloseInquiryForm());
            }
        }
    }

    /**
     * 获取技能组
     *
     * @param param 转人工参数
     */
    private void getGroupInfo(final SobotConnCusParam param) {
        zhiChiApi.getGroupList(SobotChatFSFragment.this, info.getApp_key(), initModel.getPartnerid(), new StringResultCallBack<ZhiChiGroup>() {
            @Override
            public void onSuccess(ZhiChiGroup zhiChiGroup) {
                if (!isActive()) {
                    return;
                }
                boolean hasOnlineCustom = false;
                if (ZhiChiConstant.groupList_ustatus_time_out.equals(zhiChiGroup.getUstatus())) {
                    customerServiceOffline(initModel, 4);
                } else {
                    list_group = zhiChiGroup.getData();
                    if (list_group != null && list_group.size() > 0) {
                        for (int i = 0; i < list_group.size(); i++) {
                            if ("true".equals(list_group.get(i).isOnline())) {
                                hasOnlineCustom = true;
                                break;
                            }
                        }
                        if (hasOnlineCustom) {
                            if (initModel.getUstatus() == ZhiChiConstant.ustatus_online || initModel.getUstatus() == ZhiChiConstant.ustatus_queue) {
                                // 会话保持直接转人工
                                connectCustomerService(null);
                            } else {
                                //只要有客服在线，就先弹技能组选择，技能组有客服在线，显技能组名字，点击后，查讯前表单；
                                // 无客服，开启留言，点开后留言;无客服在线，又未开启留言，灰色，不可点击
                                if (!TextUtils.isEmpty(info.getGroupid())) {
                                    //指定技能组
                                    transfer2CustomBySkillId(param, param != null ? param.getTransferType() : 0);
                                } else {
                                    Intent intent = new Intent(getSobotActivity(), SobotSkillGroupActivity.class);
                                    intent.putExtra("grouplist", (Serializable) list_group);
                                    intent.putExtra("uid", initModel.getPartnerid());
                                    intent.putExtra("type", type);
                                    intent.putExtra("appkey", info.getApp_key());
                                    intent.putExtra("companyId", initModel.getCompanyId());
                                    intent.putExtra("msgTmp", initModel.getMsgTmp());
                                    intent.putExtra("msgTxt", initModel.getMsgTxt());
                                    intent.putExtra("msgFlag", initModel.getMsgFlag());
                                    intent.putExtra("transferType", param != null ? param.getTransferType() : 0);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_CONNCUSPARAM, param);
                                    intent.putExtras(bundle);
                                    startActivityForResult(intent, ZhiChiConstant.REQUEST_COCE_TO_GRROUP);
                                }
                            }
                        } else {
                            if (messageAdapter != null && keyWordMessageBase != null) {
                                messageAdapter.justAddData(keyWordMessageBase);
                                messageAdapter.notifyDataSetChanged();
                                keyWordMessageBase = null;
                            } else {
                                //技能组没有客服在线
                                connCustomerServiceFail(true);
                                //延迟转人工如果没有转成功（技能组没有客服在线），需要把该消息当留言转离线消息处理下
                                if (!TextUtils.isEmpty(tempMsgContent)) {
                                    String skillGroupId = SharedPreferencesUtil.getStringData(getSobotActivity(), ZhiChiConstant.sobot_connect_group_id, "");
                                    zhiChiApi.leaveMsg(SobotChatFSFragment.this, initModel.getPartnerid(), skillGroupId, tempMsgContent, new StringResultCallBack<BaseCode>() {
                                        @Override
                                        public void onSuccess(BaseCode baseCode) {

                                        }

                                        @Override
                                        public void onFailure(Exception e, String s) {

                                        }
                                    });
                                }
                            }
                        }
                    } else {
                        //没有设置技能组
                        requestQueryFrom(param, info.isCloseInquiryForm());
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                if (!isActive()) {
                    return;
                }
                ToastUtil.showToast(mAppContext, des);
            }
        });
    }

    /**
     * 转人工失败
     */
    private void connCustomerServiceFail(boolean isShowTips) {
        if (type == 2) {
            showLeaveMsg();
        } else {
            showLogicTitle(initModel.getRobotName(), initModel.getRobotLogo(), false);
            showSwitchRobotBtn();
            if (isShowTips) {
                showCustomerOfflineTip();
            }
            if (type == ZhiChiConstant.type_custom_first && current_client_model ==
                    ZhiChiConstant.client_model_robot) {
                remindRobotMessage(handler, initModel, info);
            }
        }
        gotoLastItem();
    }

    /**
     * 转人工 用户是黑名单
     */
    private void connCustomerServiceBlack(boolean isShowTips) {
        showLogicTitle(initModel.getRobotName(), initModel.getRobotLogo(), false);
        showSwitchRobotBtn();
        if (isShowTips) {
            showCustomerUanbleTip();
        }
        if (type == ZhiChiConstant.type_custom_first) {
            remindRobotMessage(handler, initModel, info);
        }
    }

    /**
     * 显示机器人的布局
     */
    private void showRobotLayout() {
        if (initModel != null) {
            if (type == 1) {
                //仅机器人
                setBottomView(ZhiChiConstant.bottomViewtype_onlyrobot);
                mBottomViewtype = ZhiChiConstant.bottomViewtype_onlyrobot;
                showLogicTitle(initModel.getRobotName(), initModel.getRobotLogo(), false);
            } else if (type == 3 || type == 4) {
                //机器人优先
                setBottomView(ZhiChiConstant.bottomViewtype_robot);
                mBottomViewtype = ZhiChiConstant.bottomViewtype_robot;
                showLogicTitle(initModel.getRobotName(), initModel.getRobotLogo(), false);
            } else if (type == 2) {
                setBottomView(ZhiChiConstant.bottomViewtype_customer);
                mBottomViewtype = ZhiChiConstant.bottomViewtype_customer;
                showLogicTitle(getResString("sobot_connecting_customer_service"), null, false);
            }
            //仅人工不需要设置机器人布局
            if (type != ZhiChiConstant.type_custom_only) {
                //除了仅人工模式，打开机器人自动补全功能
                et_sendmessage.setRequestParams(initModel.getPartnerid(), initModel.getRobotid());
                et_sendmessage.setAutoCompleteEnable(true);
            }
        }
    }

    /**
     * 转人工方法
     *
     * @param param 转人工参数对象
     */
    protected void connectCustomerService(SobotConnCusParam param, final boolean isShowTips) {
        if (isConnCustomerService) {
            return;
        }
        isConnCustomerService = true;
        boolean currentFlag = (customerState == CustomerState.Queuing || customerState == CustomerState.Online);

        if (param == null) {
            param = new SobotConnCusParam();
        }
        param.setTran_flag(info.getTranReceptionistFlag());
        param.setPartnerid(initModel.getPartnerid());
        param.setCid(initModel.getCid());
        param.setCurrentFlag(currentFlag);
        param.setTransferAction(info.getTransferAction());
        param.setIs_Queue_First(info.is_queue_first());
        param.setSummary_params(info.getSummary_params());
        param.setOfflineMsgAdminId(offlineMsgAdminId);
        param.setOfflineMsgConnectFlag(offlineMsgConnectFlag);
        SharedPreferencesUtil.saveStringData(getSobotActivity(), ZhiChiConstant.sobot_connect_group_id, param.getGroupId());

        if (param.getEventType() == ZhiChiConstant.SOBOT_TYEP_TRANSFER_CUSTOM_DUOLUN1526) {
            //多轮转人工节点 1526 如果指定客服需要设置强转
            param.setTran_flag(1);
        }

        final String keyword = param.getKeyword();
        final String keywordId = param.getKeywordId();
        final String docId = param.getDocId();
        final String unknownQuestion = param.getUnknownQuestion();
        final String activeTransfer = param.getActiveTransfer();

        zhiChiApi.connnect(SobotChatFSFragment.this, param,
                new StringResultCallBack<ZhiChiMessageBase>() {
                    @Override
                    public void onSuccess(ZhiChiMessageBase zhichiMessageBase) {
                        LogUtils.i("connectCustomerService:zhichiMessageBase= " + zhichiMessageBase);
                        //转人工接口执行完后，先断开通道和停止界面上的轮询,防止之前的轮询用的是上个会话的puid,导致拿不到新会话的消息
                        zhiChiApi.disconnChannel();
                        stopPolling();

                        isConnCustomerService = false;
                        offlineMsgAdminId = "";
                        offlineMsgConnectFlag = 0;
                        if (!isActive()) {
                            return;
                        }

                        if (!TextUtils.isEmpty(zhichiMessageBase.getServiceEndPushMsg())) {
                            initModel.setServiceEndPushMsg(zhichiMessageBase.getServiceEndPushMsg());
                        }

                        int status = 0;
                        try {
                            status = Integer.parseInt(zhichiMessageBase.getStatus());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        statusFlag = status;
                        preCurrentCid = initModel.getCid();
                        setAdminFace(zhichiMessageBase.getAface());
                        setAdminName(zhichiMessageBase.getAname());
                        LogUtils.i("status---:" + status);
                        if (status != 0) {
                            if (status == ZhiChiConstant.transfer_robot_customServeive) {
                                //机器人超时下线转人工
                                customerServiceOffline(initModel, 4);
                            } else if (status == ZhiChiConstant.transfer_robot_custom_status) {
                                //如果设置指定客服的id。并且设置不是必须转入，服务器返回status=6.这个时候要设置receptionistId为null
                                //为null以后继续转人工逻辑。如果技能组开启就弹技能组，如果技能组没有开启，就直接转人工
                                showLogicTitle(initModel.getRobotName(), initModel.getRobotLogo(), false);
                                info.setChoose_adminid(null);
                                //智能路由匹配失败重新转人工
                                initModel.setSmartRouteInfoFlag(false);
                                transfer2Custom(null, keyword, keywordId, isShowTips, docId, unknownQuestion, activeTransfer);
                            } else {
                                if (ZhiChiConstant.transfer_customeServeive_success == status) {
                                    connCustomerServiceSuccess(zhichiMessageBase);
                                } else if (ZhiChiConstant.transfer_customeServeive_fail == status) {
                                    if (messageAdapter != null && keyWordMessageBase != null) {
                                        messageAdapter.justAddData(keyWordMessageBase);
                                        messageAdapter.notifyDataSetChanged();
                                        keyWordMessageBase = null;
                                    } else {
                                        connCustomerServiceFail(isShowTips);
                                    }
                                } else if (ZhiChiConstant.transfer_customeServeive_isBalk == status) {
                                    if (messageAdapter != null && keyWordMessageBase != null) {
                                        messageAdapter.justAddData(keyWordMessageBase);
                                        messageAdapter.notifyDataSetChanged();
                                        keyWordMessageBase = null;
                                    } else {
                                        connCustomerServiceBlack(isShowTips);
                                    }
                                } else if (ZhiChiConstant.transfer_customeServeive_already == status) {
                                    connCustomerServiceSuccess(zhichiMessageBase);
                                } else if (ZhiChiConstant.transfer_robot_custom_max_status == status) {
                                    if (type == 2) {
                                        showLogicTitle(getResString("sobot_wait_full"), null, true);
                                        setBottomView(ZhiChiConstant.bottomViewtype_custom_only_msgclose);
                                        mBottomViewtype = ZhiChiConstant.bottomViewtype_custom_only_msgclose;
                                    }

                                    if (initModel.getMsgFlag() == ZhiChiConstant.sobot_msg_flag_open) {
                                        if (!TextUtils.isEmpty(zhichiMessageBase.getMsg())) {
                                            ToastUtil.showCustomToastWithListenr(getSobotActivity(), zhichiMessageBase.getMsg(), 3000, new ToastUtil.OnAfterShowListener() {
                                                @Override
                                                public void doAfter() {
                                                    startToPostMsgActivty(false);
                                                }
                                            });
                                        } else {
                                            ToastUtil.showCustomToastWithListenr(getSobotActivity(), ResourceUtils.getResString(getSobotActivity(), "sobot_line_transfinite_def_hint"), 3000, new ToastUtil.OnAfterShowListener() {
                                                @Override
                                                public void doAfter() {
                                                    startToPostMsgActivty(false);
                                                }
                                            });
                                        }

                                    }
                                    showSwitchRobotBtn();
                                }
                            }
                            //延迟转人工如果没有转成功（排队除外），需要把该消息当留言转离线消息处理下
                            if (!TextUtils.isEmpty(tempMsgContent) && ZhiChiConstant.transfer_customeServeive_success != status) {
                                String skillGroupId = SharedPreferencesUtil.getStringData(getSobotActivity(), ZhiChiConstant.sobot_connect_group_id, "");
                                zhiChiApi.leaveMsg(SobotChatFSFragment.this, initModel.getPartnerid(), skillGroupId, tempMsgContent, new StringResultCallBack<BaseCode>() {
                                    @Override
                                    public void onSuccess(BaseCode baseCode) {

                                    }

                                    @Override
                                    public void onFailure(Exception e, String s) {

                                    }
                                });
                            }
                        } else {
                            LogUtils.i("转人工--排队");
                            //开启通道
                            zhiChiApi.connChannel(zhichiMessageBase.getWslinkBak(),
                                    zhichiMessageBase.getWslinkDefault(), initModel.getPartnerid(), zhichiMessageBase.getPuid(), info.getApp_key(), zhichiMessageBase.getWayHttp());
                            customerState = CustomerState.Queuing;
                            isShowQueueTip = isShowTips;
                            if (!TextUtils.isEmpty(tempMsgContent)) {
                                //延迟转人工排队时需要添加这个接口
                                zhiChiApi.sendMsgWhenQueue(tempMsgContent, initModel.getPartnerid(), initModel.getCid(), new StringResultCallBack<CommonModelBase>() {
                                    @Override
                                    public void onSuccess(CommonModelBase commonModelBase) {

                                    }

                                    @Override
                                    public void onFailure(Exception e, String s) {

                                    }
                                });
                            }
                            createCustomerQueue(zhichiMessageBase.getCount() + "", status, zhichiMessageBase.getQueueDoc(), isShowTips);
                            if (!CommonUtils.isServiceWork(getSobotActivity(), "com.sobot.chat.core.channel.SobotTCPServer")) {
                                LogUtils.i2Local("转人工排队 开启轮询", "tcpserver 没运行，直接走fragment 界面的轮询");
                                SobotMsgManager.getInstance(getSobotActivity()).getZhiChiApi().disconnChannel();
                                //SobotTCPServer不存在，直接走定时器轮询
                                pollingMsgForOne();
                                startPolling();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Exception e, String des) {
                        LogUtils.i("connectCustomerService:e= " + e.toString() + des);
                        isConnCustomerService = false;
                        if (messageAdapter != null && keyWordMessageBase != null) {
                            messageAdapter.justAddData(keyWordMessageBase);
                            keyWordMessageBase = null;
                        }
                        if (!isActive()) {
                            return;
                        }
                        if (type == 2) {
                            setBottomView(ZhiChiConstant.bottomViewtype_custom_only_msgclose);
                            showLogicTitle(getResString("sobot_no_access"), null, false);
                            isSessionOver = true;
                        }
                        ToastUtil.showToast(mAppContext, des);
                    }
                });
    }

    private void gotoLastItem() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (messageAdapter.getCount() > 0) {
                    lv_message.setSelection(messageAdapter.getCount() - 1);
                } else {
                    lv_message.setSelection(messageAdapter.getCount());
                }
            }
        });
    }

    /**
     * 根据未读消息数更新右上角UI  “XX条未读消息”
     */
    private void updateFloatUnreadIcon() {
        if (mUnreadNum >= 10) {
            notReadInfo.setVisibility(View.VISIBLE);
            notReadInfo.setText(mUnreadNum + getResString("sobot_new_msg"));
        } else {
            notReadInfo.setVisibility(View.GONE);
        }
    }

    /**
     * 转人工成功的方法
     */
    private void connCustomerServiceSuccess(ZhiChiMessageBase base) {
        if (base == null || initModel == null) {
            return;
        }
        initModel.setAdminHelloWord(!TextUtils.isEmpty(base.getAdminHelloWord()) ? base.getAdminHelloWord() : initModel.getAdminHelloWord());
        initModel.setAdminTipTime(!TextUtils.isEmpty(base.getServiceOutTime()) ? base.getServiceOutTime() : initModel.getAdminTipTime());
        initModel.setAdminTipWord(!TextUtils.isEmpty(base.getServiceOutDoc()) ? base.getServiceOutDoc() : initModel.getAdminTipWord());

        //开启通道
        zhiChiApi.connChannel(base.getWslinkBak(), base.getWslinkDefault(), initModel.getPartnerid(),
                base.getPuid(), info.getApp_key(), base.getWayHttp());
        createCustomerService(base.getAname(), base.getAface());
    }

    /**
     * 建立与客服的对话
     *
     * @param name 客服的名称
     * @param face 客服的头像
     */
    private void createCustomerService(String name, String face) {
        //改变变量
        current_client_model = ZhiChiConstant.client_model_customService;
        if (SobotOption.sobotChatStatusListener != null) {
            //修改聊天状态为客服状态
            SobotOption.sobotChatStatusListener.onChatStatusListener(SobotChatStatusMode.ZCServerConnectArtificial);
        }
        customerState = CustomerState.Online;
        isAboveZero = false;
        isComment = false;// 转人工时 重置为 未评价
        queueNum = 0;
        currentUserName = TextUtils.isEmpty(name) ? "" : name;
        //显示被xx客服接入
        messageAdapter.addData(ChatUtils.getServiceAcceptTip(getSobotActivity(), name));

        //转人工成功以后删除通过机器人关键字选择
        messageAdapter.removeKeyWordTranferItem();

        if (initModel.isAdminHelloWordFlag()) {
            if (initModel.getAdminHelloWordCountRule() == 2) {
                //仅首次线上，isNew=1时有效
                if (initModel.getIsNew() == 1) {
                    String adminHelloWord = ZCSobotApi.getCurrentInfoSetting(mAppContext) != null ? ZCSobotApi.getCurrentInfoSetting(mAppContext).getAdmin_hello_word() : "";
                    //显示人工欢迎语
                    if (!TextUtils.isEmpty(adminHelloWord)) {
                        messageAdapter.addData(ChatUtils.getServiceHelloTip(name, face, adminHelloWord));
                    } else {
                        messageAdapter.addData(ChatUtils.getServiceHelloTip(name, face, initModel.getAdminHelloWord()));
                    }
                }
            } else {
                if (!(initModel.getAdminHelloWordCountRule() == 1 && initModel.getUstatus() == ZhiChiConstant.ustatus_online)) {
                    //客户之前在线 并且 客服欢迎语规则只显示一次的开关打开 就不显示此次欢迎语
                    String adminHelloWord = ZCSobotApi.getCurrentInfoSetting(mAppContext) != null ? ZCSobotApi.getCurrentInfoSetting(mAppContext).getAdmin_hello_word() : "";
                    //显示人工欢迎语
                    if (!TextUtils.isEmpty(adminHelloWord)) {
                        messageAdapter.addData(ChatUtils.getServiceHelloTip(name, face, adminHelloWord));
                    } else {
                        messageAdapter.addData(ChatUtils.getServiceHelloTip(name, face, initModel.getAdminHelloWord()));
                    }
                }
            }
        }
        messageAdapter.notifyDataSetChanged();
        //显示标题
        showLogicTitle(name, face, false);
        Message message = handler.obtainMessage();
        message.what = ZhiChiConstant.send_message_close;
        handler.sendMessage(message);
        showSwitchRobotBtn();
        //创建咨询项目
        createConsultingContent(0);
        //创建订单卡片
        createOrderCardContent(0);
        gotoLastItem();
        //设置底部键盘
        setBottomView(ZhiChiConstant.bottomViewtype_customer);
        mBottomViewtype = ZhiChiConstant.bottomViewtype_customer;

        // 启动计时任务
        restartInputListener();
        stopUserInfoTimeTask();
        is_startCustomTimerTask = false;
        startUserInfoTimeTask(handler);
        hideItemTransferBtn();
        //关闭自动补全功能
        et_sendmessage.setAutoCompleteEnable(false);
        //自动发一条信息
        if (info.getAutoSendMsgMode().getAuto_send_msgtype() == ZCMessageTypeText) {
            //自动发送文本消息
            processAutoSendMsg(info);
        } else {
            //只有人工在线的模式下才会自动发送消息
            if (info.getAutoSendMsgMode() != null && info.getAutoSendMsgMode() != SobotAutoSendMsgMode.Default && current_client_model == ZhiChiConstant.client_model_customService && !TextUtils.isEmpty(info.getAutoSendMsgMode().getContent())) {
                if (info.getAutoSendMsgMode() == SobotAutoSendMsgMode.SendToOperator && customerState == CustomerState.Online) {
                    //发送内容
                    String content = info.getAutoSendMsgMode().getContent();
                    if (info.getAutoSendMsgMode().getAuto_send_msgtype() == ZCMessageTypeFile) {
                        //发送文件
                        File sendFile = new File(content);
                        if (sendFile.exists()) {
                            uploadFile(sendFile, handler, lv_message, messageAdapter, false);
                        }
                    } else if (info.getAutoSendMsgMode().getAuto_send_msgtype() == ZCMessageTypeVideo) {
                        //发送视频
                        File sendFile = new File(content);
                        if (sendFile.exists()) {
                            uploadVideo(sendFile, null, messageAdapter);
                        }
                    } else if (info.getAutoSendMsgMode().getAuto_send_msgtype() == ZCMessageTypePhoto) {
                        //发送图片
                        File sendFile = new File(content);
                        if (sendFile.exists()) {
                            uploadFile(sendFile, handler, lv_message, messageAdapter, false);
                        }
                    }
                }
            }
        }
        if (!isRemindTicketInfo) {
            processNewTicketMsg(handler);
        }
        if (!TextUtils.isEmpty(tempMsgContent)) {
            sendMsg(tempMsgContent);
            tempMsgContent = "";
        }
        if (!CommonUtils.isServiceWork(getSobotActivity(), "com.sobot.chat.core.channel.SobotTCPServer")) {
            LogUtils.i2Local("转人工成功后 开启轮询", "tcpserver 没运行，直接走fragment 界面的轮询");
            SobotMsgManager.getInstance(getSobotActivity()).getZhiChiApi().disconnChannel();
            //SobotTCPServer不存在，直接走定时器轮询
            pollingMsgForOne();
            startPolling();
        }
    }

    /**
     * 隐藏条目中的转人工按钮
     */
    public void hideItemTransferBtn() {
        if (!isActive()) {
            return;
        }
        // 把机器人回答中的转人工按钮都隐藏掉
        lv_message.post(new Runnable() {

            @Override
            public void run() {

                for (int i = 0, count = lv_message.getChildCount(); i < count; i++) {
                    View child = lv_message.getChildAt(i);
                    if (child == null || child.getTag() == null || !(child.getTag() instanceof RichTextMessageHolder)) {
                        continue;
                    }
                    RichTextMessageHolder holder = (RichTextMessageHolder) child.getTag();
                    if (holder.message != null) {
                        holder.message.setShowTransferBtn(false);
                    }
                    holder.hideTransferBtn();
                }
            }
        });
    }

    /**
     * 显示客服不在线的提示
     */
    private void showCustomerOfflineTip() {
        if (initModel.isAdminNoneLineFlag()) {
            ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
            reply.setMsgType(null);
            String adminNoneLineTitle = ZCSobotApi.getCurrentInfoSetting(mAppContext) != null ? ZCSobotApi.getCurrentInfoSetting(mAppContext).getAdmin_offline_title() : "";
            if (!TextUtils.isEmpty(adminNoneLineTitle)) {
                reply.setMsg(adminNoneLineTitle);
            } else {
                if (TextUtils.isEmpty(initModel.getAdminNonelineTitle())) {
                    //如果提示语为空，直接返回，不然会显示错误数据
                    return;
                }
                reply.setMsg(initModel.getAdminNonelineTitle());
            }
            reply.setRemindType(ZhiChiConstant.sobot_remind_type_customer_offline);
            ZhiChiMessageBase base = new ZhiChiMessageBase();
            base.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");
            base.setAnswer(reply);
            base.setAction(ZhiChiConstant.action_remind_info_post_msg);
            updateUiMessage(messageAdapter, base);
        }
    }

    /**
     * 显示无法转接客服
     */
    private void showCustomerUanbleTip() {
        ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
        reply.setMsgType(null);
        reply.setMsg(getResString("sobot_unable_transfer_to_customer_service"));
        reply.setRemindType(ZhiChiConstant.sobot_remind_type_unable_to_customer);
        ZhiChiMessageBase base = new ZhiChiMessageBase();
        base.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");
        base.setAnswer(reply);
        base.setAction(ZhiChiConstant.action_remind_info_post_msg);
        updateUiMessage(messageAdapter, base);
    }

    /**
     * 机器人答案点踩 显示未解决问题，点击转人工客服
     */
    private void showCaiToCustomerTip() {
        ZhiChiMessageBase base = new ZhiChiMessageBase();
        base.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");
        base.setAction(ZhiChiConstant.action_remind_info_zhuanrengong);
        updateUiMessage(messageAdapter, base);
        gotoLastItem();
    }

    /**
     * 连接客服时，需要排队
     * 显示排队的处理逻辑
     *
     * @param num      当前排队的位置
     * @param status   当前转人工的返回状态，如果是7，就说明排队已经达到最大值，可以直接留言。
     * @param queueDoc 需要显示的排队提示语
     */
    private void createCustomerQueue(String num, int status, String queueDoc, boolean isShowTips) {
        if (customerState == CustomerState.Queuing && !TextUtils.isEmpty(num)
                && Integer.parseInt(num) > 0) {
            stopUserInfoTimeTask();
            stopCustomTimeTask();
            stopInputListener();

            queueNum = Integer.parseInt(num);
            //显示当前排队的位置
            if (status != ZhiChiConstant.transfer_robot_custom_max_status && isShowTips) {
                showInLineHint(queueDoc);
            }

            if (type == ZhiChiConstant.type_custom_only) {
                showLogicTitle(getResString("sobot_in_line"), null, false);
                setBottomView(ZhiChiConstant.bottomViewtype_onlycustomer_paidui);
                mBottomViewtype = ZhiChiConstant.bottomViewtype_onlycustomer_paidui;
            } else {
                showLogicTitle(initModel.getRobotName(), initModel.getRobotLogo(), false);
                setBottomView(ZhiChiConstant.bottomViewtype_paidui);
                mBottomViewtype = ZhiChiConstant.bottomViewtype_paidui;
            }

            queueTimes = queueTimes + 1;
            if (type == ZhiChiConstant.type_custom_first) {
                if (queueTimes == 1) {
                    //如果当前为人工优先模式那么在第一次收到
                    remindRobotMessage(handler, initModel, info);
                }
            }
            showSwitchRobotBtn();
        }
    }

    /**
     * 初始化查询cid的列表
     */
    private void queryCids() {
        //如果initmodel 或者  querycid的接口调用中或者已经调用成功那么就不再重复查询
        if (initModel == null || queryCidsStatus == ZhiChiConstant.QUERY_CIDS_STATUS_LOADING
                || queryCidsStatus == ZhiChiConstant.QUERY_CIDS_STATUS_SUCCESS) {
            return;
        }
        long time = SharedPreferencesUtil.getLongData(mAppContext, ZhiChiConstant.SOBOT_SCOPE_TIME, 0);
        queryCidsStatus = ZhiChiConstant.QUERY_CIDS_STATUS_LOADING;
        // 初始化查询cid的列表
        zhiChiApi.queryCids(SobotChatFSFragment.this, initModel.getPartnerid(), time, new StringResultCallBack<ZhiChiCidsModel>() {

            @Override
            public void onSuccess(ZhiChiCidsModel data) {
                if (!isActive()) {
                    return;
                }
                queryCidsStatus = ZhiChiConstant.QUERY_CIDS_STATUS_SUCCESS;
                cids = data.getCids();
                if (cids != null) {
                    boolean hasRepeat = false;
                    for (int i = 0; i < cids.size(); i++) {
                        if (cids.get(i).equals(initModel.getCid())) {
                            hasRepeat = true;
                            break;
                        }
                    }
                    if (!hasRepeat) {
                        cids.add(initModel.getCid());
                    }
                    Collections.reverse(cids);
                }
                //拉取历史纪录
                getHistoryMessage(true);
            }

            @Override
            public void onFailure(Exception e, String des) {
                queryCidsStatus = ZhiChiConstant.QUERY_CIDS_STATUS_FAILURE;
            }
        });
    }

    private void showInitError() {
        showLogicTitle("", null, false);
        loading_anim_view.setVisibility(View.GONE);
        txt_loading.setVisibility(View.GONE);
        textReConnect.setVisibility(View.VISIBLE);
        icon_nonet.setVisibility(View.VISIBLE);
        btn_reconnect.setVisibility(View.VISIBLE);
        et_sendmessage.setVisibility(View.GONE);
        relative.setVisibility(View.GONE);
        welcome.setVisibility(View.VISIBLE);
    }

    /*
     * 发送咨询内容
     *
     */
    @Override
    public void sendConsultingContent() {
        sendCardMsg(info.getConsultingContent());
    }

    /**
     * @param base
     * @param type
     * @param questionFlag 0 是正常询问机器人
     *                     1 是有docId的问答
     *                     2 是多轮会话
     * @param docId        没有就传Null
     */
    @Override
    public void sendMessageToRobot(ZhiChiMessageBase base, int type, int questionFlag, String docId) {
        sendMessageToRobot(base, type, questionFlag, docId, null);
    }

    /*发送0、机器人问答 1、文本  2、语音  3、图片 4、多轮会话 5、位置消息*/
    @Override
    public void sendMessageToRobot(ZhiChiMessageBase base, int type, int questionFlag, String docId, String multiRoundMsg) {
        if (type == 5) {
            sendLocation(base.getId(), base.getAnswer().getLocationData(), handler, false);
        }
        if (type == 4) {
            sendMsgToRobot(base, SEND_TEXT, questionFlag, docId, multiRoundMsg);
        }

        /*图片消息*/
        else if (type == 3) {
            // 根据图片的url 上传图片 更新上传图片的进度
            messageAdapter.updatePicStatusById(base.getId(), base.getSendSuccessState());
            messageAdapter.notifyDataSetChanged();
            ChatUtils.sendPicture(mAppContext, initModel.getCid(), initModel.getPartnerid(),
                    base.getContent(), handler, base.getId(), lv_message, messageAdapter);
        }

        /*语音消息*/
        else if (type == 2) {
            // 语音的重新上传
            sendVoiceMessageToHandler(base.getId(), base.getContent(), base.getAnswer()
                    .getDuration(), ZhiChiConstant.MSG_SEND_STATUS_LOADING, UPDATE_VOICE, handler);
            sendVoice(base.getId(), base.getAnswer().getDuration(), initModel.getCid(),
                    initModel.getPartnerid(), base.getContent(), handler);
        }

        /*文本消息*/
        else if (type == 1) {
            // 消息的转换
            sendMsgToRobot(base, UPDATE_TEXT, questionFlag, docId);
        }

        /*机器人问答*/
        else if (type == 0) {

            if (!isSessionOver) {
                // 消息的转换
                ZhiChiReplyAnswer answer = new ZhiChiReplyAnswer();
                answer.setMsgType(ZhiChiConstant.message_type_text + "");
                answer.setMsg(base.getContent());
                base.setAnswer(answer);
                base.setSenderType(ZhiChiConstant.message_sender_type_customer + "");
                if (base.getId() == null || TextUtils.isEmpty(base.getId())) {
                    String msgId = (initModel != null ? initModel.getCid() : "") + System.currentTimeMillis() + "";
                    base.setId(msgId);
                    updateUiMessage(messageAdapter, base);
                }
                sendMessageWithLogic(base.getId(), base.getContent(), initModel, handler, current_client_model, questionFlag, docId);
            } else {
                showOutlineTip(initModel, 2);
            }
        }
        gotoLastItem();
    }

    /**
     * 点击了转人工按钮
     */
    @Override
    public void doClickTransferBtn(ZhiChiMessageBase base) {
        //转人工按钮
        hidePanelAndKeyboard(mPanelRoot);
        doEmoticonBtn2Blur();
        if (base != null) {
            int temptransferType = base.getTransferType();
            if (temptransferType == 0 && !TextUtils.isEmpty(base.getAnswerType())) {
                if (Integer.parseInt(base.getAnswerType()) == 1) {
                    temptransferType = 6;
                } else if (Integer.parseInt(base.getAnswerType()) == 2) {
                    temptransferType = 7;
                } else if (Integer.parseInt(base.getAnswerType()) == 3) {
                    temptransferType = 9;
                } else if (Integer.parseInt(base.getAnswerType()) == 4) {
                    temptransferType = 8;
                }
            }
            transfer2Custom(0, null, null, null, null, true, temptransferType, base.getDocId(), base.getOriginQuestion(), "1", base.getMsgId(), base.getRuleId());
        } else {
            transfer2Custom(null, null, null, true, "1");
        }
    }

    public void doClickTransferBtn() {
        //转人工按钮
        hidePanelAndKeyboard(mPanelRoot);
        doEmoticonBtn2Blur();
        transfer2Custom(null, null, null, true, "1");
    }

    // 点击播放录音及动画
    @Override
    public void clickAudioItem(ZhiChiMessageBase message) {
        if (mAudioPlayPresenter == null) {
            mAudioPlayPresenter = new AudioPlayPresenter(mAppContext);
        }
        if (mAudioPlayCallBack == null) {
            mAudioPlayCallBack = new AudioPlayCallBack() {
                @Override
                public void onPlayStart(ZhiChiMessageBase mCurrentMsg) {
                    showVoiceAnim(mCurrentMsg, true);
                    initAudioManager();
                    requestAudioFocus();
                }

                @Override
                public void onPlayEnd(ZhiChiMessageBase mCurrentMsg) {
                    showVoiceAnim(mCurrentMsg, false);
                    abandonAudioFocus();
                }
            };
        }
        mAudioPlayPresenter.clickAudio(message, mAudioPlayCallBack);
    }

    @Override
    public void sendMessage(String content) {
        sendMsg(content);
    }

    @Override
    public void removeMessageByMsgId(String msgid) {
        if (messageAdapter != null && !TextUtils.isEmpty(msgid)) {
            messageAdapter.removeByMsgId(msgid);
            messageAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void addMessage(ZhiChiMessageBase message) {
        if (message != null) {
            messageAdapter.justAddData(message);
            messageAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void mulitDiaToLeaveMsg(String leaveTemplateId, String tipMsgId) {
        if (mPostMsgPresenter != null) {
            hidePanelAndKeyboard();
            mPostMsgPresenter.obtainTmpConfigToMuItiPostMsg(initModel.getPartnerid(), leaveTemplateId, tipMsgId);
        }
    }

    public void showVoiceAnim(final ZhiChiMessageBase info, final boolean isShow) {
        if (!isActive()) {
            return;
        }
        lv_message.post(new Runnable() {

            @Override
            public void run() {
                if (info == null) {
                    return;
                }
                for (int i = 0, count = lv_message.getChildCount(); i < count; i++) {
                    View child = lv_message.getChildAt(i);
                    if (child == null || child.getTag() == null || !(child.getTag() instanceof VoiceMessageHolder)) {
                        continue;
                    }
                    VoiceMessageHolder holder = (VoiceMessageHolder) child.getTag();
                    holder.stopAnim();
                    if (holder.message == info) {
                        if (isShow) {
                            holder.startAnim();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void hidePanelAndKeyboard() {
        hidePanelAndKeyboard(mPanelRoot);
    }

    /**
     * 调用顶踩接口
     *
     * @param revaluateFlag true 顶  false 踩
     * @param message       顶踩用的 model
     */
    @Override
    public void doRevaluate(final boolean revaluateFlag, final ZhiChiMessageBase message) {
        if (isSessionOver) {
            showOutlineTip(initModel, 2);
            CustomToast.makeText(mAppContext, getResString("sobot_ding_cai_sessionoff"), 1500).show();
            return;
        }
        CustomToast.makeText(mAppContext, revaluateFlag ? getResString("sobot_ding_cai_like") : getResString("sobot_ding_cai_dislike"), 1500).show();
        zhiChiApi.rbAnswerComment(SobotChatFSFragment.this, message.getMsgId(), initModel.getPartnerid(), initModel.getCid(), initModel.getRobotid(),
                message.getDocId(), message.getDocName(), revaluateFlag, message.getOriginQuestion(), message.getAnswerType(), message.getGptAnswerType(), message.getAnswer(), new StringResultCallBack<CommonModelBase>() {
                    @Override
                    public void onSuccess(CommonModelBase data) {
                        if (!isActive()) {
                            return;
                        }
                        //if (ZhiChiConstant.client_sendmsg_to_custom_fali.equals(data.getStatus())) {
                        //  customerServiceOffline(initModel, 1);
                        //} else if (ZhiChiConstant.client_sendmsg_to_custom_success.equals(data.getStatus())) {
                        //改变顶踩按钮的布局
                        refreshItemByCategory(RichTextMessageHolder.class);
                        refreshItemByCategory(ImageMessageHolder.class);
                        refreshItemByCategory(FileMessageHolder.class);
                        refreshItemByCategory(VideoMessageHolder.class);
                        refreshItemByCategory(MiniProgramMessageHolder.class);
                        refreshItemByCategory(RichTextMessageHolder.class);
                        if ((!TextUtils.isEmpty(message.getAnswerType()) && message.getAnswerType().startsWith("152"))) {
                            refreshItemByCategory(RobotTemplateMessageHolder1.class);
                            refreshItemByCategory(RobotTemplateMessageHolder2.class);
                            refreshItemByCategory(RobotTemplateMessageHolder3.class);
                            refreshItemByCategory(RobotTemplateMessageHolder4.class);
                            refreshItemByCategory(RobotTemplateMessageHolder5.class);
                            refreshItemByCategory(RobotTemplateMessageHolder6.class);
                        }
                        //仅机器人不显示
                        if (initModel.getRealuateTransferFlag() == 1 && current_client_model != ZhiChiConstant.client_model_customService && !revaluateFlag && type != ZhiChiConstant.type_robot_only) {
                            //点踩  并且不是人工状态 才显示转人工的系统提示语
                            String content = getSobotActivity().getResources().getString(R.string.sobot_cant_solve_problem_new);
                            String click = getSobotActivity().getResources().getString(R.string.sobot_customer_service);
                            zhiChiApi.insertSysMsg(SobotChatFSFragment.this, initModel.getCid(), initModel.getPartnerid(), String.format(content, click), "点踩转人工提示", new StringResultCallBack<BaseCode>() {
                                @Override
                                public void onSuccess(BaseCode baseCode) {
                                    showCaiToCustomerTip();
                                }

                                @Override
                                public void onFailure(Exception e, String des) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Exception e, String des) {
                        ToastUtil.showToast(mAppContext, ResourceUtils.getResString(getSobotActivity(), "sobot_net_work_err"));
                    }
                });
    }

    /**
     * 客服邀请评价
     *
     * @param evaluateFlag true 直接提交  false 打开评价窗口
     * @param message      data
     */
    @Override
    public void doEvaluate(final boolean evaluateFlag, final ZhiChiMessageBase message) {
        if (initModel == null || message == null) {
            return;
        }
        SobotEvaluateModel sobotEvaluateModel = message.getSobotEvaluateModel();
        if (sobotEvaluateModel == null) {
            return;
        }
        if (evaluateFlag) {
            SobotCommentParam sobotCommentParam = new SobotCommentParam();
            sobotCommentParam.setType("1");
            sobotCommentParam.setScore(message.getSobotEvaluateModel().getScore() + "");
            sobotCommentParam.setScoreFlag(message.getSobotEvaluateModel().getScoreFlag());
            sobotCommentParam.setCommentType(0);
            sobotCommentParam.setProblem(sobotEvaluateModel.getProblem());
            sobotCommentParam.setIsresolve(sobotEvaluateModel.getIsResolved());
            zhiChiApi.comment(SobotChatFSFragment.this, initModel.getCid(), initModel.getPartnerid(), sobotCommentParam, new StringResultCallBack<CommonModel>() {
                @Override
                public void onSuccess(CommonModel commonModel) {
                    if (!isActive()) {
                        return;
                    }
                    Intent intent = new Intent();
                    intent.setAction(ZhiChiConstants.dcrc_comment_state);
                    intent.putExtra("commentState", true);
                    intent.putExtra("commentType", 0);
                    intent.putExtra("score", message.getSobotEvaluateModel().getScore());
                    intent.putExtra("isResolved", message.getSobotEvaluateModel().getIsResolved());
                    CommonUtils.sendLocalBroadcast(mAppContext, intent);
                }

                @Override
                public void onFailure(Exception e, String des) {

                }
            });
        } else {
            submitEvaluation(false, sobotEvaluateModel.getScore(), sobotEvaluateModel.getIsResolved(), sobotEvaluateModel.getProblem());
        }

    }

    /**
     * 刷新所有指定类型viewHolder
     *
     * @param clz viewHolder.class
     */
    private <T> void refreshItemByCategory(final Class<T> clz) {
        if (!isActive()) {
            return;
        }
        lv_message.post(new Runnable() {

            @Override
            public void run() {
                for (int i = 0, count = lv_message.getChildCount(); i < count; i++) {
                    View child = lv_message.getChildAt(i);
                    if (child == null || child.getTag() == null) {
                        continue;
                    }
                    if (clz == RichTextMessageHolder.class && child.getTag() instanceof RichTextMessageHolder) {
                        RichTextMessageHolder holder = (RichTextMessageHolder) child.getTag();
                        holder.refreshItem();
                    } else if (clz == CusEvaluateMessageHolder.class && child.getTag() instanceof CusEvaluateMessageHolder) {
                        CusEvaluateMessageHolder holder = (CusEvaluateMessageHolder) child.getTag();
                        holder.refreshItem();
                    } else if (clz == RobotTemplateMessageHolder1.class && child.getTag() instanceof RobotTemplateMessageHolder1) {
                        RobotTemplateMessageHolder1 holder = (RobotTemplateMessageHolder1) child.getTag();
                        holder.refreshRevaluateItem();
                    } else if (clz == RobotTemplateMessageHolder2.class && child.getTag() instanceof RobotTemplateMessageHolder2) {
                        RobotTemplateMessageHolder2 holder = (RobotTemplateMessageHolder2) child.getTag();
                        holder.refreshRevaluateItem();
                    } else if (clz == RobotTemplateMessageHolder3.class && child.getTag() instanceof RobotTemplateMessageHolder3) {
                        RobotTemplateMessageHolder3 holder = (RobotTemplateMessageHolder3) child.getTag();
                        holder.refreshRevaluateItem();
                    } else if (clz == RobotTemplateMessageHolder4.class && child.getTag() instanceof RobotTemplateMessageHolder4) {
                        RobotTemplateMessageHolder4 holder = (RobotTemplateMessageHolder4) child.getTag();
                        holder.refreshRevaluateItem();
                    } else if (clz == RobotTemplateMessageHolder5.class && child.getTag() instanceof RobotTemplateMessageHolder5) {
                        RobotTemplateMessageHolder5 holder = (RobotTemplateMessageHolder5) child.getTag();
                        holder.refreshRevaluateItem();
                    } else if (clz == RobotTemplateMessageHolder6.class && child.getTag() instanceof RobotTemplateMessageHolder6) {
                        RobotTemplateMessageHolder6 holder = (RobotTemplateMessageHolder6) child.getTag();
                        holder.refreshRevaluateItem();
                    } else if (clz == FileMessageHolder.class && child.getTag() instanceof FileMessageHolder) {
                        FileMessageHolder holder = (FileMessageHolder) child.getTag();
                        holder.refreshItem();
                    } else if (clz == VideoMessageHolder.class && child.getTag() instanceof VideoMessageHolder) {
                        VideoMessageHolder holder = (VideoMessageHolder) child.getTag();
                        holder.refreshItem();
                    } else if (clz == MiniProgramMessageHolder.class && child.getTag() instanceof MiniProgramMessageHolder) {
                        MiniProgramMessageHolder holder = (MiniProgramMessageHolder) child.getTag();
                        holder.refreshItem();
                    }
                }
            }
        });
    }

    //置顶通告设置
    private void getAnnouncement() {
        if (!TextUtils.isEmpty(initModel.getAnnounceClickUrl()) && initModel.getAnnounceClickFlag()) {
//            sobot_announcement_right_icon.setVisibility(View.VISIBLE);
            sobot_announcement_right_icon.setVisibility(View.GONE);
            sobot_announcement_title.setTextColor(ContextCompat.getColor(getContext(), ResourceUtils.getResColorId(getContext(), "sobot_announcement_title_color_2")));
        } else {
            sobot_announcement_title.setTextColor(ContextCompat.getColor(getContext(), ResourceUtils.getResColorId(getContext(), "sobot_announcement_title_color")));
            sobot_announcement_right_icon.setVisibility(View.GONE);
        }

        if (initModel.getAnnounceMsgFlag() && initModel.isAnnounceTopFlag() && !TextUtils.isEmpty(initModel.getAnnounceMsg())) {
            sobot_announcement.setVisibility(View.VISIBLE);
            sobot_announcement_title.setText(initModel.getAnnounceMsg());
            sobot_announcement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 内部浏览器
                    if (!TextUtils.isEmpty(initModel.getAnnounceClickUrl()) && initModel.getAnnounceClickFlag()) {
                        if (SobotOption.hyperlinkListener != null) {
                            SobotOption.hyperlinkListener.onUrlClick(initModel.getAnnounceClickUrl());
                            return;
                        }
                        if (SobotOption.newHyperlinkListener != null) {
                            //如果返回true,拦截;false 不拦截
                            boolean isIntercept = SobotOption.newHyperlinkListener.onUrlClick(getSobotActivity(), initModel.getAnnounceClickUrl());
                            if (isIntercept) {
                                return;
                            }
                        }
                        Intent intent = new Intent(mAppContext, WebViewActivity.class);
                        intent.putExtra("url", initModel.getAnnounceClickUrl());
                        startActivity(intent);
                    }
                }
            });
        } else {
            sobot_announcement.setVisibility(View.GONE);
        }
    }

    /**
     * 设置底部键盘UI
     *
     * @param viewType
     */
    public void setBottomView(int viewType) {
        welcome.setVisibility(View.GONE);
        relative.setVisibility(View.VISIBLE);
        chat_main.setVisibility(View.VISIBLE);
        et_sendmessage.setVisibility(View.VISIBLE);
        sobot_ll_restart_talk.setVisibility(View.GONE);
        sobot_ll_bottom.setVisibility(View.VISIBLE);

        if (isUserBlack()) {
            sobot_ll_restart_talk.setVisibility(View.GONE);
            sobot_ll_bottom.setVisibility(View.VISIBLE);
            btn_model_voice.setVisibility(View.GONE);
            btn_emoticon_view.setVisibility(View.GONE);
        }
        if (info.isHideMenuSatisfaction()) {
            sobot_tv_satisfaction.setVisibility(View.GONE);
        } else {
            sobot_tv_satisfaction.setVisibility(View.VISIBLE);
        }
        sobot_txt_restart_talk.setVisibility(View.VISIBLE);
        sobot_tv_message.setVisibility(View.VISIBLE);

        LogUtils.i("setBottomView:" + viewType);
        switch (viewType) {
            case ZhiChiConstant.bottomViewtype_onlyrobot:
                // 仅机器人
                showVoiceBtn();
                if (image_reLoading.getVisibility() == View.VISIBLE) {
                    sobot_ll_bottom.setVisibility(View.VISIBLE);/* 底部聊天布局 */
                    edittext_layout.setVisibility(View.VISIBLE);/* 文本输入框布局 */
                    sobot_ll_restart_talk.setVisibility(View.GONE);

                    if (btn_press_to_speak.getVisibility() == View.VISIBLE) {
                        btn_press_to_speak.setVisibility(View.GONE);
                    }
                    btn_set_mode_rengong.setClickable(false);
                    btn_set_mode_rengong.setVisibility(View.GONE);
                }
                btn_emoticon_view.setVisibility(View.GONE);
                btn_upload_view.setVisibility(View.VISIBLE);
                btn_send.setVisibility(View.GONE);
                btn_send_pic.setVisibility(View.GONE);
                break;
            case ZhiChiConstant.bottomViewtype_robot:
                //机器人对话框
                if (info.isArtificialIntelligence() && type == ZhiChiConstant.type_robot_first) {
                    //智能转人工只适用于机器人优先
                    if (showTimeVisiableCustomBtn >= info.getArtificialIntelligenceNum()) {
                        btn_set_mode_rengong.setVisibility(View.VISIBLE);
                    } else {
                        btn_set_mode_rengong.setVisibility(View.GONE);
                    }
                } else {
                    btn_set_mode_rengong.setVisibility(View.VISIBLE);
                }

                btn_set_mode_rengong.setClickable(true);
                showVoiceBtn();
                if (Build.VERSION.SDK_INT >= 11)
                    btn_set_mode_rengong.setAlpha(1f);
                if (image_reLoading.getVisibility() == View.VISIBLE) {
                    sobot_ll_bottom.setVisibility(View.VISIBLE);/* 底部聊天布局 */
                    edittext_layout.setVisibility(View.VISIBLE);/* 文本输入框布局 */
                    sobot_ll_restart_talk.setVisibility(View.GONE);

                    if (btn_press_to_speak.getVisibility() == View.VISIBLE) {
                        btn_press_to_speak.setVisibility(View.GONE);
                    }
                    btn_set_mode_rengong.setClickable(true);
                    btn_set_mode_rengong.setEnabled(true);
                }
                btn_upload_view.setVisibility(View.VISIBLE);
                btn_emoticon_view.setVisibility(View.GONE);
                btn_send.setVisibility(View.GONE);
                btn_send_pic.setVisibility(View.GONE);
                break;
            case ZhiChiConstant.bottomViewtype_customer:
                //人工对话框
                hideRobotVoiceHint();
                btn_model_edit.setVisibility(View.GONE);
                btn_set_mode_rengong.setVisibility(View.GONE);
                btn_upload_view.setVisibility(View.VISIBLE);
                btn_send.setVisibility(View.GONE);
                btn_send_pic.setVisibility(View.GONE);
                showEmotionBtn();
                showVoiceBtn();
                btn_model_voice.setEnabled(true);
                btn_model_voice.setClickable(true);
                btn_upload_view.setEnabled(true);
                btn_upload_view.setClickable(true);
                btn_emoticon_view.setClickable(true);
                btn_emoticon_view.setEnabled(true);
                if (Build.VERSION.SDK_INT >= 11) {
                    btn_model_voice.setAlpha(1f);
                    btn_upload_view.setAlpha(1f);
                }

                edittext_layout.setVisibility(View.VISIBLE);
                sobot_ll_bottom.setVisibility(View.VISIBLE);
                btn_press_to_speak.setVisibility(View.GONE);
                btn_press_to_speak.setClickable(true);
                btn_press_to_speak.setEnabled(true);
                txt_speak_content.setText(getResString("sobot_press_say"));
                break;
            case ZhiChiConstant.bottomViewtype_onlycustomer_paidui:
                //仅人工排队中
                onlyCustomPaidui();

                hidePanelAndKeyboard(mPanelRoot);
                if (lv_message.getLastVisiblePosition() != messageAdapter.getCount()) {
                    lv_message.setSelection(messageAdapter.getCount());
                }
                break;
            case ZhiChiConstant.bottomViewtype_outline:
                //被提出
                hideReLoading();
                hidePanelAndKeyboard(mPanelRoot);/*隐藏键盘*/
                sobot_ll_bottom.setVisibility(View.GONE);
                sobot_ll_restart_talk.setVisibility(View.VISIBLE);
                if (info.isHideMenuSatisfaction()) {
                    sobot_tv_satisfaction.setVisibility(View.GONE);
                } else {
                    //会话结束 没有咨询过就不显示评价按钮
                    if (isAboveZero) {
                        sobot_tv_satisfaction.setVisibility(View.VISIBLE);
                    } else {
                        sobot_tv_satisfaction.setVisibility(View.GONE);
                    }
                }
                sobot_txt_restart_talk.setVisibility(View.VISIBLE);
                btn_model_edit.setVisibility(View.GONE);
                if (info.isHideMenuLeave()) {
                    sobot_tv_message.setVisibility(View.GONE);
                } else {
                    sobot_tv_message.setVisibility(initModel.getMsgFlag() == ZhiChiConstant.sobot_msg_flag_close ? View
                            .GONE : View.VISIBLE);
                }
                btn_model_voice.setVisibility(View.GONE);
                lv_message.setSelection(messageAdapter.getCount());
                break;
            case ZhiChiConstant.bottomViewtype_paidui:
                //智能模式下排队中
                if (btn_press_to_speak.getVisibility() == View.GONE) {
                    showVoiceBtn();
                }
                //机器人对话框
                if (info.isArtificialIntelligence() && type == ZhiChiConstant.type_robot_first) {
                    //智能转人工只适用于机器人优先
                    if (showTimeVisiableCustomBtn >= info.getArtificialIntelligenceNum()) {
                        btn_set_mode_rengong.setVisibility(View.VISIBLE);
                    } else {
                        btn_set_mode_rengong.setVisibility(View.GONE);
                    }
                } else {
                    btn_set_mode_rengong.setVisibility(View.VISIBLE);
                }
                btn_emoticon_view.setVisibility(View.GONE);
                if (image_reLoading.getVisibility() == View.VISIBLE) {
                    sobot_ll_bottom.setVisibility(View.VISIBLE);/* 底部聊天布局 */
                    edittext_layout.setVisibility(View.VISIBLE);/* 文本输入框布局 */
                    btn_model_voice.setVisibility(View.GONE);
                    sobot_ll_restart_talk.setVisibility(View.GONE);

                    if (btn_press_to_speak.getVisibility() == View.VISIBLE) {
                        btn_press_to_speak.setVisibility(View.GONE);
                    }
                }
                break;
            case ZhiChiConstant.bottomViewtype_custom_only_msgclose:
                sobot_ll_restart_talk.setVisibility(View.VISIBLE);

                sobot_ll_bottom.setVisibility(View.GONE);
                if (image_reLoading.getVisibility() == View.VISIBLE) {
                    sobot_txt_restart_talk.setVisibility(View.VISIBLE);
                    sobot_txt_restart_talk.setClickable(true);
                    sobot_txt_restart_talk.setEnabled(true);
                }
                if (initModel.getMsgFlag() == ZhiChiConstant.sobot_msg_flag_close) {
                    //留言关闭
                    sobot_tv_satisfaction.setVisibility(View.GONE);
                    sobot_tv_message.setVisibility(View.GONE);
                } else {
                    sobot_tv_satisfaction.setVisibility(View.GONE);
                    sobot_tv_message.setVisibility(View.VISIBLE);
                }
                break;
        }
        hideReLoading();
    }

    //仅人工时排队UI更新
    private void onlyCustomPaidui() {
        if (SobotOption.sobotChatStatusListener != null) {
            //仅人工排队状态
            SobotOption.sobotChatStatusListener.onChatStatusListener(SobotChatStatusMode.ZCServerConnectWaiting);
        }
        sobot_ll_bottom.setVisibility(View.VISIBLE);

        btn_set_mode_rengong.setVisibility(View.GONE);
        btn_set_mode_rengong.setClickable(false);

        btn_upload_view.setVisibility(View.VISIBLE);
        btn_send.setVisibility(View.GONE);
        btn_send_pic.setVisibility(View.GONE);
        btn_upload_view.setClickable(false);
        btn_upload_view.setEnabled(false);

        showEmotionBtn();
        btn_emoticon_view.setClickable(false);
        btn_emoticon_view.setEnabled(false);

        showVoiceBtn();
        btn_model_voice.setClickable(false);
        btn_model_voice.setEnabled(false);
        btn_model_voice.setVisibility(View.GONE);


        edittext_layout.setVisibility(View.GONE);
        btn_press_to_speak.setClickable(false);
        btn_press_to_speak.setEnabled(false);
        btn_press_to_speak.setVisibility(View.VISIBLE);
        txt_speak_content.setText(getResString("sobot_in_line"));
        showLogicTitle(getResString("sobot_in_line"), null, false);
        if (sobot_ll_restart_talk.getVisibility() == View.VISIBLE) {
            sobot_ll_restart_talk.setVisibility(View.GONE);
        }
    }

    //3.0.3 type 0 转人工后创建商品卡片，1 人工状态每次返回再进入聊天页面是否再发送商品卡片
    private void createConsultingContent(int type) {
        ConsultingContent consultingContent = info.getConsultingContent();
        if (consultingContent != null && !TextUtils.isEmpty(consultingContent.getSobotGoodsTitle()) && !TextUtils.isEmpty(consultingContent.getSobotGoodsFromUrl())) {
            ZhiChiMessageBase zhichiMessageBase = new ZhiChiMessageBase();
            zhichiMessageBase.setSenderType(ZhiChiConstant.message_sender_type_consult_info + "");
            if (!TextUtils.isEmpty(consultingContent.getSobotGoodsImgUrl())) {
                zhichiMessageBase.setPicurl(consultingContent.getSobotGoodsImgUrl());
            }
            ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
            zhichiMessageBase.setAnswer(reply);
            zhichiMessageBase.setContent(consultingContent.getSobotGoodsTitle());
            zhichiMessageBase.setUrl(consultingContent.getSobotGoodsFromUrl());
            zhichiMessageBase.setCid(initModel == null ? "" : initModel.getCid());
            zhichiMessageBase.setAname(consultingContent.getSobotGoodsLable());
            zhichiMessageBase.setReceiverFace(consultingContent.getSobotGoodsDescribe());

            zhichiMessageBase.setAction(ZhiChiConstant.action_consultingContent_info);
            updateUiMessage(messageAdapter, zhichiMessageBase);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    lv_message.setSelection(messageAdapter.getCount());
                }
            });
            if (consultingContent.isAutoSend()) {
                if (type == 1) {
                    //人工状态下每次返回再进入聊天页面是否再发送商品卡片
                    if (consultingContent.isEveryTimeAutoSend()) {
                        sendConsultingContent();
                    }
                } else {
                    sendConsultingContent();
                }
            }
        } else {
            if (messageAdapter != null) {
                messageAdapter.removeConsulting();
            }
        }
    }

    //创建订单卡片，根据设置的isAutoSend，判断是否自动发送
    //3.0.3 type 0 转人工后创建订单卡片，1 人工状态每次返回再进入聊天页面是否再发送订单卡片
    private void createOrderCardContent(int type) {
        OrderCardContentModel orderCardContent = info.getOrderGoodsInfo();
        if (orderCardContent != null && !TextUtils.isEmpty(orderCardContent.getOrderCode()) && orderCardContent.isAutoSend()) {
            if (type == 1) {
                //人工状态下每次返回再进入聊天页面是否再发送订单卡片
                if (orderCardContent.isEveryTimeAutoSend()) {
                    sendOrderCardMsg(orderCardContent);
                }
            } else {
                sendOrderCardMsg(orderCardContent);
            }
        }
    }


    /**
     * 导航栏关闭按钮点击事件
     */
    protected void onCloseMenuClick() {
        hidePanelAndKeyboard(mPanelRoot);
        if (isActive()) {
            if (info.isShowCloseSatisfaction() || (info.getIsSetShowSatisfaction() == 0 && initModel != null && initModel.getCommentFlag() == 1)) {
                if (isAboveZero && !isComment) {
                    // 退出时 之前没有评价过的话 才能 弹评价框
                    openEcaluate();
                    return;
                } else {
                    customerServiceOffline(initModel);
                    ChatUtils.userLogout(mAppContext, "onCloseMenuClick 点击右上角关闭按钮");
                }
            } else {
                customerServiceOffline(initModel);
                ChatUtils.userLogout(mAppContext, "onCloseMenuClick 点击右上角关闭按钮");
            }
            finish();
        }
    }

    private void openEcaluate() {
        Intent intent = new Intent(getSobotActivity(), SobotEvaluateActivity.class);
        intent.putExtra("score", 5);
        intent.putExtra("isSessionOver", isSessionOver);
        intent.putExtra("isFinish", true);
        intent.putExtra("isExitSession", true);
        intent.putExtra("initModel", initModel);
        intent.putExtra("current_model", current_client_model);
        intent.putExtra("commentType", 1);
        intent.putExtra("customName", currentUserName);
        intent.putExtra("isSolve", -1);
        intent.putExtra("checklables", "");
        intent.putExtra("isBackShowEvaluate", false);
        intent.putExtra("canBackWithNotEvaluation", true);
        startActivity(intent);
    }

    /**
     * 导航栏左侧返回按钮  弹出是否结束会话框  结束回话 事件
     */
    protected void onLeftBackColseClick() {
        hidePanelAndKeyboard(mPanelRoot);
        if (isActive()) {
            if (info.isShowSatisfaction() || (info.getIsSetShowSatisfaction() == 0 && initModel != null && initModel.getCommentFlag() == 1)) {
                if (isAboveZero && !isComment) {
                    // 退出时 之前没有评价过的话 才能 弹评价框
                    openEcaluate();
                    return;
                } else {
                    customerServiceOffline(initModel);
                    ChatUtils.userLogout(mAppContext, "onLeftBackColseClick 导航栏左侧返回按钮  弹出是否结束会话框  结束回话");
                }
            } else {
                customerServiceOffline(initModel);
                ChatUtils.userLogout(mAppContext, "onLeftBackColseClick 导航栏左侧返回按钮  弹出是否结束会话框  结束回话");
            }
            finish();
        }
    }

    /**
     * 根据输入框里的内容切换显示  发送按钮还是加号（更多方法）
     */
    private void resetBtnUploadAndSend() {
        if (et_sendmessage.getText().toString().length() > 0) {
            btn_upload_view.setVisibility(View.GONE);
            if (CommonUtils.checkSDKIsZh(getContext()) || CommonUtils.checkSDKIsEn(getContext())) {
                btn_send.setVisibility(View.VISIBLE);
                btn_send_pic.setVisibility(View.GONE);
            } else {
                btn_send.setVisibility(View.GONE);
                btn_send_pic.setVisibility(View.VISIBLE);
            }
        } else {
            btn_send.setVisibility(View.GONE);
            btn_send_pic.setVisibility(View.GONE);
            btn_upload_view.setVisibility(View.VISIBLE);
            btn_upload_view.setEnabled(true);
            btn_upload_view.setClickable(true);
            if (Build.VERSION.SDK_INT >= 11) {
                btn_upload_view.setAlpha(1f);
            }
        }
    }

    /**
     * 根据逻辑判断显示当前的title
     * 根据客服传入的title显示模式显示聊天页面的标题
     *
     * @param title       此处传如的值为默认需要显示的昵称 或者提示等等
     * @param avatarUrl   头像
     * @param ignoreLogic 表示忽略逻辑直接显示
     */
    private void showLogicTitle(String title, String avatarUrl, boolean ignoreLogic) {
        if (initModel != null) {
            String avatarStr = ChatUtils.getLogicAvatar(mAppContext, ignoreLogic, avatarUrl, initModel.getCompanyLogo());
            //是否显示头像,true 显示;false 隐藏,默认true
            boolean isShowAvatar = SharedPreferencesUtil.getBooleanData(getContext(), ZhiChiConstant.SOBOT_CHAT_AVATAR_IS_SHOW, true);
            if (TextUtils.isEmpty(avatarUrl)) {
                //如果头像为空，隐藏头像
                isShowAvatar = false;
            }
            setAvatar(avatarStr, isShowAvatar);
            String str = ChatUtils.getLogicTitle(mAppContext, ignoreLogic, title, initModel.getCompanyName());
            //是否显示标题,true 显示;false 隐藏,默认false
            boolean isShowTitle = SharedPreferencesUtil.getBooleanData(getContext(), ZhiChiConstant.SOBOT_CHAT_TITLE_IS_SHOW, false);
            if (TextUtils.isEmpty(avatarUrl)) {
                //如果头像为空，显示标题
                isShowTitle = true;
            }
            setTitle(str, isShowTitle);
        }
    }

    // 设置标题内容或者头像
    public void setTitle(CharSequence title, boolean isShowTitle) {
        if (isShowTitle) {
            mTitleTextView.setVisibility(View.VISIBLE);
        } else {
            mTitleTextView.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(title)) {
            mTitleTextView.setText(title);
        }
        applyTitleTextColor(mTitleTextView);
    }

    // 头部设置头像
    public void setAvatar(String avatarUrl, boolean isShowAvatar) {
        if (isShowAvatar) {
            mAvatarIV.setVisibility(View.VISIBLE);
            mAvatarIV.setRoundAsCircle(true);
            mAvatarIV.setStrokeWidth(ScreenUtils.dip2px(getContext(), 0.4f));
            mAvatarIV.setStrokeColor(ResourceUtils.getResColorValue(getContext(), "sobot_line_1dp"));
            if (!TextUtils.isEmpty(avatarUrl)) {
                SobotBitmapUtil.display(getContext(), avatarUrl, mAvatarIV);
            } else {
                SobotBitmapUtil.display(getContext(), getResDrawableId("sobot_robot"), mAvatarIV);
            }
        } else {
            mAvatarIV.setVisibility(View.GONE);
        }
    }


    // 头部设置本地头像
    public void setAvatar(int avatarUrl, boolean isShowAvatar) {
        if (isShowAvatar) {
            mAvatarIV.setVisibility(View.VISIBLE);
            mAvatarIV.setRoundAsCircle(true);
            mAvatarIV.setStrokeWidth(ScreenUtils.dip2px(getContext(), 0.4f));
            mAvatarIV.setStrokeColor(ResourceUtils.getResColorValue(getContext(), "sobot_line_1dp"));
            SobotBitmapUtil.display(getContext(), avatarUrl, mAvatarIV);
        } else {
            mAvatarIV.setVisibility(View.GONE);
        }
    }

    private SobotBackDialog sobotBackDialog;//左上角返回按钮弹窗

    /**
     * 导航栏左边点击事件
     */
    protected void onLeftMenuClick() {
        //返回时未知问题或引导答案触发智能转人工按钮，把次数改成0，防止机器人模式下次进来还会显示
        showTimeVisiableCustomBtn = 0;
        hidePanelAndKeyboard(mPanelRoot);
        if (!isSessionOver && info.isShowLeftBackPop()) {//会话没有结束并且有提示
            sobotBackDialog = new SobotBackDialog(getSobotActivity(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sobotBackDialog.dismiss();
                    if (v.getId() == ResourceUtils.getResId(getContext(), "sobot_btn_cancle_conversation")) {
                        //结束会话
                        onLeftBackColseClick();
                    } else if (v.getId() == ResourceUtils.getResId(getContext(), "sobot_btn_temporary_leave")) {
                        //暂时离开
                        if (isActive()) {
                            //按返回按钮的时候 如果面板显示就隐藏面板  如果面板已经隐藏那么就是用户想退出
                            if (mPanelRoot.getVisibility() == View.VISIBLE) {
                                hidePanelAndKeyboard(mPanelRoot);
                                return;
                            }
                            finish();
                        }
                    }
                }
            });
            sobotBackDialog.show();
        } else {
            onBackPress();
        }
    }

    @Override
    public void onDestroy() {
        if (SobotOption.functionClickListener != null) {
            SobotOption.functionClickListener.onClickFunction(getSobotActivity(), SobotFunctionType.ZC_CloseChat);
        }
        try {
            // 取消广播接受者
            if (getSobotActivity() != null) {
                if (receiver != null) {
                    getSobotActivity().unregisterReceiver(receiver);
                }
                if (mKPSwitchListener != null) {
                    KeyboardUtil.detach(getSobotActivity(), mKPSwitchListener);
                }
            }
            if (localBroadcastManager != null) {
                localBroadcastManager.unregisterReceiver(localReceiver);
            }
        } catch (Exception e) {
            //ignor
        }
        super.onDestroy();
    }

    /**
     * 导航栏右边点击事件
     *
     * @param view
     */
    protected void onRightMenuClick(View view) {
        hidePanelAndKeyboard(mPanelRoot);
        ClearHistoryDialog clearHistoryDialog = new ClearHistoryDialog(getSobotActivity());
        clearHistoryDialog.setCanceledOnTouchOutside(true);
        clearHistoryDialog.setOnClickListener(new ClearHistoryDialog.DialogOnClickListener() {
            @Override
            public void onSure() {
                clearHistory();
            }
        });
        clearHistoryDialog.show();
    }

    private SobotClearHistoryMsgDialog clearHistoryMsgDialog;//清楚历史记录弹窗

    public void clearHistory() {
        if (clearHistoryMsgDialog == null) {
            clearHistoryMsgDialog = new SobotClearHistoryMsgDialog(getSobotActivity(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearHistoryMsgDialog.dismiss();
                    if (v.getId() == ResourceUtils.getResId(getContext(), "sobot_btn_cancle_conversation")) {

                        zhiChiApi.deleteHisMsg(SobotChatFSFragment.this, initModel.getPartnerid(), new StringResultCallBack<CommonModelBase>() {
                            @Override
                            public void onSuccess(CommonModelBase modelBase) {
                                if (!isActive()) {
                                    return;
                                }
                                messageList.clear();
                                cids.clear();
                                messageAdapter.notifyDataSetChanged();
                                lv_message.setPullRefreshEnable(true);// 设置下拉刷新列表
                            }

                            @Override
                            public void onFailure(Exception e, String des) {
                            }
                        });
                    } else if (v.getId() == ResourceUtils.getResId(getContext(), "sobot_btn_temporary_leave")) {
                        clearHistoryMsgDialog.dismiss();
                    }
                }
            });
            clearHistoryMsgDialog.show();
        } else {
            clearHistoryMsgDialog.show();
        }
    }

    /**
     * 隐藏重新开始会话的菊花
     */
    public void hideReLoading() {
        image_reLoading.clearAnimation();
        image_reLoading.setVisibility(View.GONE);
    }

    /**
     * 重置表情按钮的焦点键盘
     */
    public void resetEmoticonBtn() {
        String panelViewTag = getPanelViewTag(mPanelRoot);
        String instanceTag = CustomeViewFactory.getInstanceTag(mAppContext, btn_emoticon_view.getId());
        if (mPanelRoot.getVisibility() == View.VISIBLE && instanceTag.equals(panelViewTag)) {
            doEmoticonBtn2Focus();
        } else {
            doEmoticonBtn2Blur();
        }
    }

    /**
     * 使表情按钮获取焦点
     */
    public void doEmoticonBtn2Focus() {
        btn_emoticon_view.setSelected(true);
    }

    /**
     * 使表情按钮失去焦点
     */
    public void doEmoticonBtn2Blur() {
        btn_emoticon_view.setSelected(false);
    }

    /**
     * 仅人工的无客服在线的逻辑
     */
    private void showLeaveMsg() {
        LogUtils.i("仅人工，无客服在线");
        showLogicTitle(getResString("sobot_no_access"), null, false);
        setBottomView(ZhiChiConstant.bottomViewtype_custom_only_msgclose);
        mBottomViewtype = ZhiChiConstant.bottomViewtype_custom_only_msgclose;
        if (isUserBlack()) {
            showCustomerUanbleTip();
        } else {
            showCustomerOfflineTip();
        }
        isSessionOver = true;
    }

    /**
     * 输入表情的方法
     *
     * @param item
     */
    @Override
    public void inputEmoticon(EmojiconNew item) {
        InputHelper.input2OSC(et_sendmessage, item);
    }

    /**
     * 输入框删除的方法
     */
    @Override
    public void backspace() {
        InputHelper.backspace(et_sendmessage);
    }

    /**
     * 提供给聊天面板执行的方法
     * 图库
     */
    @Override
    public void btnPicture() {
        hidePanelAndKeyboard(mPanelRoot);
        selectPicFromLocal();
        lv_message.setSelection(messageAdapter.getCount());
    }

    /**
     * 提供给聊天面板执行的方法
     * 视频
     */
    @Override
    public void btnVedio() {
        hidePanelAndKeyboard(mPanelRoot);
        selectVedioFromLocal();
        lv_message.setSelection(messageAdapter.getCount());
    }

    /**
     * 提供给聊天面板执行的方法
     * 照相
     */
    @Override
    public void btnCameraPicture() {
        hidePanelAndKeyboard(mPanelRoot);
        selectPicFromCamera(); // 拍照 上传
        lv_message.setSelection(messageAdapter.getCount());
    }

    /**
     * 提供给聊天面板执行的方法
     * 满意度
     */
    @Override
    public void btnSatisfaction() {
        lv_message.setSelection(messageAdapter.getCount());
        //满意度逻辑 点击时首先判断是否评价过 评价过 弹您已完成提示 未评价 判断是否达到可评价标准
        submitEvaluation(true, 5, -1, "");
    }

    /**
     * 提供给聊天面板执行的方法
     * 选择文件
     */
    @Override
    public void chooseFile() {
//        permissionListener = new PermissionListenerImpl() {
//            @Override
//            public void onPermissionSuccessListener() {
//             Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//             intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
//             intent.addCategory(Intent.CATEGORY_OPENABLE);
//             startActivityForResult(intent, ZhiChiConstant.REQUEST_COCE_TO_CHOOSE_FILE);
//            }
//        };
//        if (!isHasPermission(1, 3)) {
//            return;
//        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, ZhiChiConstant.REQUEST_COCE_TO_CHOOSE_FILE);
    }

    @Override
    public void startToPostMsgActivty(final boolean flag_exit_sdk) {
        startToPostMsgActivty(flag_exit_sdk, false);
    }

    /**
     * 打开留言页面
     *
     * @param flag_exit_sdk 当留言页面退出时 所需执行动作的标识
     * @param isShowTicket  打开留言页的类型  false：表示正常显示留言页 true：表示查看留言记录
     */
    public void startToPostMsgActivty(final boolean flag_exit_sdk, final boolean isShowTicket) {
        if (initModel == null) {
            return;
        }

        if (SobotOption.sobotLeaveMsgListener != null) {
            SobotOption.sobotLeaveMsgListener.onLeaveMsg();
            return;
        }
        hidePanelAndKeyboard();
        if (initModel.isMsgToTicketFlag()) {
            Intent intent = SobotPostLeaveMsgActivity.newIntent(getContext(), initModel.getMsgLeaveTxt()
                    , initModel.getMsgLeaveContentTxt(), initModel.getPartnerid());
            startActivityForResult(intent, SobotPostLeaveMsgActivity.EXTRA_MSG_LEAVE_REQUEST_CODE);
        } else {
            String tempGroupId = SharedPreferencesUtil.getStringData(getSobotActivity(), ZhiChiConstant.sobot_connect_group_id, "");
            mPostMsgPresenter.obtainTemplateList(initModel.getPartnerid(), tempGroupId, flag_exit_sdk, isShowTicket, new StPostMsgPresenter.ObtainTemplateListDelegate() {
                @Override
                public void onSuccess(Intent intent) {
                    intent.putExtra(StPostMsgPresenter.INTENT_KEY_COMPANYID, initModel.getCompanyId());
                    intent.putExtra(StPostMsgPresenter.INTENT_KEY_CUSTOMERID, initModel.getCustomerId());
                    intent.putExtra(ZhiChiConstant.FLAG_EXIT_SDK, flag_exit_sdk);
                    intent.putExtra(StPostMsgPresenter.INTENT_KEY_GROUPID, info.getLeaveMsgGroupId());
                    intent.putExtra(StPostMsgPresenter.INTENT_KEY_IS_SHOW_TICKET, isShowTicket);
                    startActivity(intent);
                    if (getSobotActivity() != null) {
                        getSobotActivity().overridePendingTransition(ResourceUtils.getIdByName(mAppContext, "anim", "sobot_push_left_in"),
                                ResourceUtils.getIdByName(mAppContext, "anim", "sobot_push_left_out"));
                    }
                }
            });
        }
    }

    /**
     * 切换表情按钮焦点
     */
    public void switchEmoticonBtn() {
        boolean flag = btn_emoticon_view.isSelected();
        if (flag) {
            doEmoticonBtn2Blur();
        } else {
            doEmoticonBtn2Focus();
        }
        //切换表情和键盘
        if (btn_emoticon_view.isSelected()) {
            btn_emoticon_view.setBackgroundResource(ResourceUtils.getDrawableId(getContext(), "sobot_keyboard_normal"));
        } else {
            btn_emoticon_view.setBackgroundResource(ResourceUtils.getDrawableId(getContext(), "sobot_emoticon_button_selector"));
        }
    }

    //切换键盘和面板的方法
    public void switchPanelAndKeyboard(final View panelLayout, final View switchPanelKeyboardBtn, final View focusView) {
        if (currentPanelId == 0 || currentPanelId == switchPanelKeyboardBtn.getId()) {
            //没选中的时候或者  点击是自身的时候正常切换面板和键盘
            boolean switchToPanel = panelLayout.getVisibility() != View.VISIBLE;
            if (!switchToPanel) {
                KPSwitchConflictUtil.showKeyboard(panelLayout, focusView);
            } else {
                KPSwitchConflictUtil.showPanel(panelLayout);
                setPanelView(panelLayout, switchPanelKeyboardBtn.getId());
            }
        } else {
            //之前选过  但是现在点击的不是自己的时候  显示自己的面板
            KPSwitchConflictUtil.showPanel(panelLayout);
            setPanelView(panelLayout, switchPanelKeyboardBtn.getId());
        }
        currentPanelId = switchPanelKeyboardBtn.getId();
    }

    /*
     * 切换键盘和面板的方法   考虑了当键盘为按住说话时的情况 一般都用这个就行
     * 参数是按下的那个按钮
     */
    public void pressSpeakSwitchPanelAndKeyboard(final View switchPanelKeyboardBtn) {
        if (btn_press_to_speak.isShown()) {
            btn_model_edit.setVisibility(View.GONE);
            showVoiceBtn();
            btn_press_to_speak.setVisibility(View.GONE);
            edittext_layout.setVisibility(View.VISIBLE);

            et_sendmessage.requestFocus();
            KPSwitchConflictUtil.showPanel(mPanelRoot);
            setPanelView(mPanelRoot, switchPanelKeyboardBtn.getId());
            currentPanelId = switchPanelKeyboardBtn.getId();
        } else {
            //切换更多方法的面板
            switchPanelAndKeyboard(mPanelRoot, switchPanelKeyboardBtn, et_sendmessage);
        }
    }

    /**
     * 设置聊天面板的view
     *
     * @param panelLayout
     * @param btnId
     */
    private void setPanelView(final View panelLayout, int btnId) {
        if (panelLayout instanceof KPSwitchFSPanelLinearLayout) {
            KPSwitchFSPanelLinearLayout tmpView = (KPSwitchFSPanelLinearLayout) panelLayout;
            View childView = tmpView.getChildAt(0);
            if (childView != null && childView instanceof CustomeChattingPanel) {
                CustomeChattingPanel customeChattingPanel = (CustomeChattingPanel) childView;
                Bundle bundle = new Bundle();
                bundle.putInt("current_client_model", current_client_model);
                customeChattingPanel.setupView(btnId, bundle, SobotChatFSFragment.this);
            }
        }
    }

    /**
     * 获取当前显示的聊天面板的tag
     *
     * @param panelLayout
     */
    private String getPanelViewTag(final View panelLayout) {
        String str = "";
        if (panelLayout instanceof KPSwitchFSPanelLinearLayout) {
            KPSwitchFSPanelLinearLayout tmpView = (KPSwitchFSPanelLinearLayout) panelLayout;
            View childView = tmpView.getChildAt(0);
            if (childView != null && childView instanceof CustomeChattingPanel) {
                CustomeChattingPanel customeChattingPanel = (CustomeChattingPanel) childView;
                str = customeChattingPanel.getPanelViewTag();
            }
        }
        return str;
    }

    /**
     * 隐藏键盘和面板
     *
     * @param layout
     */
    public void hidePanelAndKeyboard(KPSwitchFSPanelLinearLayout layout) {
        if (layout != null) {
            layout.setVisibility(View.GONE);
        }
        et_sendmessage.dismissPop();
        KPSwitchConflictUtil.hidePanelAndKeyboard(layout);
        doEmoticonBtn2Blur();
        currentPanelId = 0;
    }

    /*
     * 弹出提示
     */
    private void showHint(String content) {
        ZhiChiMessageBase zhichiMessageBase = new ZhiChiMessageBase();
        ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
        zhichiMessageBase.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");
        reply.setMsg(content);
        reply.setRemindType(ZhiChiConstant.sobot_remind_type_tip);
        zhichiMessageBase.setAnswer(reply);
        zhichiMessageBase.setAction(ZhiChiConstant.action_remind_no_service);
        updateUiMessage(messageAdapter, zhichiMessageBase);
    }

    @Override
    public void onRobotGuessComplete(String question) {
        //分词联想 选中事件
        et_sendmessage.setText("");
        sendMsg(question);
    }

    @Override
    public void onRefresh() {
        getHistoryMessage(false);
    }

    /**
     * 获取聊天记录
     *
     * @param isFirst 第一次查询历史记录
     */
    public void getHistoryMessage(final boolean isFirst) {
        if (initModel == null)
            return;

        if (queryCidsStatus == ZhiChiConstant.QUERY_CIDS_STATUS_INITIAL || queryCidsStatus == ZhiChiConstant.QUERY_CIDS_STATUS_FAILURE) {
            //cid列表接口未调用或获取失败的时候重新获取cid
            onLoad();
            queryCids();
        } else if ((queryCidsStatus == ZhiChiConstant.QUERY_CIDS_STATUS_LOADING && !isFirst) || isInGethistory) {
            //1.查询cid接口调用中 又不是第一次查询历史记录  那么 直接什么也不做就返回
            //2.如果查询历史记录的接口正在跑   那么什么也不做
            onLoad();
        } else {
            String currentCid = ChatUtils.getCurrentCid(initModel, cids, currentCidPosition);
            if ("-1".equals(currentCid)) {
                showNoHistory();
                onLoad();
                return;
            }
            isInGethistory = true;
            zhiChiApi.getChatDetailByCid(SobotChatFSFragment.this, initModel.getPartnerid(), currentCid, new StringResultCallBack<ZhiChiHistoryMessage>() {
                @Override
                public void onSuccess(ZhiChiHistoryMessage zhiChiHistoryMessage) {
                    isInGethistory = false;
                    if (!isActive()) {
                        return;
                    }
                    onLoad();
                    currentCidPosition++;
                    List<ZhiChiHistoryMessageBase> data = zhiChiHistoryMessage.getData();
                    if (data != null && data.size() > 0) {
                        showData(data);
                    } else {
                        //没有数据的时候继续拉
                        getHistoryMessage(false);
                    }
                }

                @Override
                public void onFailure(Exception e, String des) {
                    isInGethistory = false;
                    if (!isActive()) {
                        return;
                    }
                    mUnreadNum = 0;
                    updateFloatUnreadIcon();
                    onLoad();
                }
            });
        }
    }

    private void showData(List<ZhiChiHistoryMessageBase> result) {
        List<ZhiChiMessageBase> msgLists = new ArrayList<>();
        List<ZhiChiMessageBase> msgList;
        for (int i = 0; i < result.size(); i++) {
            ZhiChiHistoryMessageBase historyMsg = result.get(i);
            msgList = historyMsg.getContent();

            for (ZhiChiMessageBase base : msgList) {
                base.setSugguestionsFontColor(1);
                if ((ZhiChiConstant.message_type_fraud_prevention + "").equals(base.getAction())) {
                } else {
                    if (base.getSdkMsg() != null) {
                        ZhiChiReplyAnswer answer = base.getSdkMsg().getAnswer();
                        if (answer != null) {
                            if (answer.getMsgType() == null) {
                                answer.setMsgType("0");
                            }

                            if (!TextUtils.isEmpty(answer.getMsg()) && answer.getMsg().length() > 4) {
                                String msg = answer.getMsg().replace("&lt;/p&gt;", "<br>");
                                if (msg.endsWith("<br>")) {
                                    msg = msg.substring(0, msg.length() - 4);
                                }
                                answer.setMsg(msg);
                            }
                        }
                        if (TextUtils.isEmpty(base.getSenderType())) {
                            base = null;
                            continue;
                        }
                        if (ZhiChiConstant.message_sender_type_robot == Integer
                                .parseInt(base.getSenderType())) {
                            base.setSenderName(TextUtils.isEmpty(base.getSenderName()) ? initModel
                                    .getRobotName() : base.getSenderName());
                            base.setSenderFace(TextUtils.isEmpty(base.getSenderFace()) ? initModel
                                    .getRobotLogo() : base.getSenderFace());
                        }
                        base.setAnswer(answer);
                        base.setAnswerType(base.getSdkMsg()
                                .getAnswerType());
                    }
                }
            }
            msgLists.addAll(msgList);
        }

        if (msgLists.size() > 0) {
            if (mUnreadNum > 0) {
                ZhiChiMessageBase unreadMsg = ChatUtils.getUnreadMode(getSobotActivity());
                unreadMsg.setCid(msgLists.get(msgLists.size() - 1).getCid());
                msgLists.add((msgLists.size() - mUnreadNum) < 0 ? 0 : (msgLists.size() - mUnreadNum)
                        , unreadMsg);
                updateFloatUnreadIcon();
                mUnreadNum = 0;
            }
            messageAdapter.addData(msgLists);
            messageAdapter.notifyDataSetChanged();
            lv_message.setSelection(msgLists.size());
        }
    }

    /**
     * 显示没有更多历史记录
     */
    private void showNoHistory() {
        ZhiChiMessageBase base = new ZhiChiMessageBase();

        base.setSenderType(ZhiChiConstant.message_sender_type_remide_info + "");

        ZhiChiReplyAnswer reply1 = new ZhiChiReplyAnswer();
        reply1.setRemindType(ZhiChiConstant.sobot_remind_type_nomore);
        reply1.setMsg("");
        base.setAnswer(reply1);
        // 更新界面的操作
        updateUiMessageBefore(messageAdapter, base);
        lv_message.setSelection(0);

        lv_message.setPullRefreshEnable(false);// 设置下拉刷新列表
        isNoMoreHistoryMsg = true;
        mUnreadNum = 0;
    }

    private void onLoad() {
        lv_message.onRefreshCompleteHeader();
    }

    // 键盘编辑模式转换为语音模式
    private void editModelToVoice(int typeModel, String str) {
        btn_model_edit.setVisibility(View.GONE == typeModel ? View.GONE
                : View.VISIBLE); // 键盘编辑隐藏
        btn_model_voice.setVisibility(View.VISIBLE != typeModel ? View.VISIBLE
                : View.GONE);// 语音模式开启
        btn_press_to_speak.setVisibility(View.GONE != typeModel ? View.VISIBLE
                : View.GONE);
        edittext_layout.setVisibility(View.VISIBLE == typeModel ? View.GONE
                : View.VISIBLE);

        if (!TextUtils.isEmpty(et_sendmessage.getText().toString()) && str.equals("123")) {
            if (CommonUtils.checkSDKIsZh(getContext()) || CommonUtils.checkSDKIsEn(getContext())) {
                btn_send.setVisibility(View.VISIBLE);
                btn_send_pic.setVisibility(View.GONE);
            } else {
                btn_send.setVisibility(View.GONE);
                btn_send_pic.setVisibility(View.VISIBLE);
            }
            btn_upload_view.setVisibility(View.GONE);
        } else {
            btn_send.setVisibility(View.GONE);
            btn_send_pic.setVisibility(View.GONE);
            btn_upload_view.setVisibility(View.VISIBLE);
        }
    }

    public void setShowNetRemind(boolean isShow) {
        net_status_remide.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 广播接受者：
     */
    public class MyMessageReceiver extends BroadcastReceiver {
        @SuppressWarnings("deprecation")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                if (!CommonUtils.isNetWorkConnected(mAppContext)) {
                    //没有网络
                    if (welcome.getVisibility() != View.VISIBLE) {
                        setShowNetRemind(true);
                    }
                } else {
                    // 有网络
                    setShowNetRemind(false);
                }
            }
        }
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                LogUtils.i("广播是  :" + intent.getAction());
                if (ZhiChiConstants.receiveMessageBrocast.equals(intent.getAction())) {
                    // 接受下推的消息
                    ZhiChiPushMessage pushMessage = null;
                    try {
                        Bundle extras = intent.getExtras();
                        if (extras != null) {
                            pushMessage = (ZhiChiPushMessage) extras.getSerializable(ZhiChiConstants.ZHICHI_PUSH_MESSAGE);
                            LogUtils.i("广播对象是  :" + pushMessage.toString());
                        }
                    } catch (Exception e) {
                        //ignor
                    }
                    if (pushMessage == null || !info.getApp_key().equals(pushMessage.getAppId())) {
                        return;
                    }
                    ZhiChiMessageBase base = new ZhiChiMessageBase();

                    //接收到系统消息，直接刷新数据
                    if (ZhiChiConstant.push_message_receverSystemMessage == pushMessage
                            .getType()) {// 接收系统消息
                        base.setT(Calendar.getInstance().getTime().getTime() + "");
                        base.setMsgId(pushMessage.getMsgId());
                        if (TextUtils.isEmpty(pushMessage.getAface())) {
                            base.setSenderFace(getAdminFace());
                        } else {
                            base.setSenderFace(pushMessage.getAface());
                        }
                        if (TextUtils.isEmpty(pushMessage.getAname())) {
                            base.setSenderName(getAdminName());
                            base.setSender(getAdminName());
                        } else {
                            base.setSenderName(pushMessage.getAname());
                            base.setSender(pushMessage.getAname());
                        }
                        if (!TextUtils.isEmpty(pushMessage.getSysType()) && ("1".equals(pushMessage.getSysType()) || "2".equals(pushMessage.getSysType()) || "5".equals(pushMessage.getSysType()) || "6".equals(pushMessage.getSysType()))) {
                            //客服超时提示 1
                            //客户超时提示 2 都显示在左侧
                            //排队断开说辞系统消息 5  都显示在左侧
                            base.setSenderType(ZhiChiConstant.message_sender_type_service + "");
                            ZhiChiReplyAnswer reply = new ZhiChiReplyAnswer();
                            reply.setMsg(pushMessage.getContent());
                            reply.setMsgType(ZhiChiConstant.message_type_text + "");
                            base.setAnswer(reply);
                        } else {
                            base.setAction(ZhiChiConstant.message_type_fraud_prevention + "");
                            base.setMsgId(pushMessage.getMsgId());
                            base.setMsg(pushMessage.getContent());
                            stopCustomTimeTask();
                            startUserInfoTimeTask(handler);
                        }
                        // 更新界面的操作
                        messageAdapter.justAddData(base);
                        messageAdapter.notifyDataSetChanged();
                        ChatUtils.msgLogicalProcess(getContext(), initModel, messageAdapter, pushMessage);
                        messageAdapter.notifyDataSetChanged();
                        if (!TextUtils.isEmpty(pushMessage.getSysType()) && "6".equals(pushMessage.getSysType())) {
                            ZhiChiMessageBase keepQueuingMessageBase = ChatUtils.getKeepQueuingHint(ResourceUtils.getResString(context, "sobot_keep_queuing_string") + "<a href='sobot:SobotKeepQueuing'> " + ResourceUtils.getResString(context, "sobot_keep_queuing") + "</a>");
                            messageAdapter.justAddData(keepQueuingMessageBase);
                            messageAdapter.notifyDataSetChanged();
                        }
                        return;
                    }
                    //客服发送的消息需要转换，每次添加新的对象都需要在这里重新赋值
                    base.setT(Calendar.getInstance().getTime().getTime() + "");
                    base.setMsgId(pushMessage.getMsgId());
                    base.setSender(pushMessage.getAname());
                    base.setSenderName(pushMessage.getAname());
                    base.setSenderFace(pushMessage.getAface());
                    base.setOrderCardContent(pushMessage.getOrderCardContent());
                    base.setConsultingContent(pushMessage.getConsultingContent());
                    base.setArticleModel(pushMessage.getArticleModel());
                    base.setMiniProgramModel(pushMessage.getMiniProgramModel());
                    base.setSenderType(ZhiChiConstant.message_sender_type_service + "");
                    base.setAnswer(pushMessage.getAnswer());

                    if (ZhiChiConstant.push_message_createChat == pushMessage.getType()) {
                        setAdminFace(pushMessage.getAface());
                        setAdminName(pushMessage.getAname());
                        if (initModel != null) {
                            initModel.setAdminHelloWord(!TextUtils.isEmpty(pushMessage.getAdminHelloWord()) ? pushMessage.getAdminHelloWord() : initModel.getAdminHelloWord());
                            initModel.setServiceEndPushMsg(!TextUtils.isEmpty(pushMessage.getServiceEndPushMsg()) ? pushMessage.getServiceEndPushMsg() : initModel.getServiceEndPushMsg());
                        }
                        createCustomerService(pushMessage.getAname(), pushMessage.getAface());
                    } else if (ZhiChiConstant.push_message_paidui == pushMessage.getType()) {
                        // 排队的消息类型
                        createCustomerQueue(pushMessage.getCount(), 0, pushMessage.getQueueDoc(), isShowQueueTip);
                    } else if (ZhiChiConstant.push_message_receverNewMessage == pushMessage.getType()) {
                        // 接收到新的消息
                        base.setMsgId(pushMessage.getMsgId());
                        base.setSender(pushMessage.getAname());
                        base.setSenderName(pushMessage.getAname());
                        base.setSenderFace(pushMessage.getAface());
                        base.setSenderType(ZhiChiConstant.message_sender_type_service + "");
                        base.setAnswer(pushMessage.getAnswer());
                        stopCustomTimeTask();
                        startUserInfoTimeTask(handler);
                        // 更新界面的操作
                        messageAdapter.justAddData(base);
                        messageAdapter.notifyDataSetChanged();
                        ChatUtils.msgLogicalProcess(getContext(), initModel, messageAdapter, pushMessage);
                        messageAdapter.notifyDataSetChanged();
                        //修改客服状态为在线
                        customerState = CustomerState.Online;
                        LogUtils.i2Local("收到消息4", "横屏ChatFragment接受到新消息 msgId: " + pushMessage.getMsgId());
                    } else if (ZhiChiConstant.push_message_outLine == pushMessage.getType()) {
                        if (messageAdapter != null) {
                            messageAdapter.removeByAction(ZhiChiConstant
                                    .action_remind_keep_queuing);
                            messageAdapter.notifyDataSetChanged();
                        }
                        if (6 == Integer.parseInt(pushMessage.getStatus())) {
                            // 打开新窗口 单独处理
                            String puid = SharedPreferencesUtil.getStringData(getSobotActivity(), Const.SOBOT_PUID, "");
                            if (!TextUtils.isEmpty(puid) && !TextUtils.isEmpty(pushMessage.getPuid()) && puid.equals(pushMessage.getPuid())) {
                                customerServiceOffline(initModel, Integer.parseInt(pushMessage.getStatus()));
                            }
                        } else {
                            // 用户被下线
                            customerServiceOffline(initModel, Integer.parseInt(pushMessage.getStatus()));
                            if (initModel.getCommentFlag() == 1) {
                                if (isAboveZero && !isComment) {
                                    // 满足评价条件，并且之前没有评价过的话 才能 弹评价框
                                    pushMessage.setIsQuestionFlag(1);
                                    ZhiChiMessageBase customEvaluateMode = ChatUtils.getCustomEvaluateMode(getSobotActivity(), pushMessage);
                                    // 更新界面的操作
                                    updateUiMessage(messageAdapter, customEvaluateMode);
                                }
                            }
                        }
                    } else if (ZhiChiConstant.push_message_transfer == pushMessage.getType()) {
                        LogUtils.i("用户被转接--->" + pushMessage.getName());
                        //替换标题 转接后客服头像取face 和name
                        showLogicTitle(pushMessage.getName(), pushMessage.getFace(), false);
                        setAdminFace(pushMessage.getFace());
                        setAdminName(pushMessage.getAname());
                        currentUserName = pushMessage.getName();
                    } else if (ZhiChiConstant.push_message_user_get_session_lock_msg == pushMessage.getType()) {
                        if (customerState == CustomerState.Online) {
                            //1 会话锁定
                            if (1 == pushMessage.getLockType()) {
                                paseReplyTimeCustoms = noReplyTimeCustoms;
                                paseReplyTimeUserInfo = noReplyTimeUserInfo;
                                isChatLock = 1;
                                if (is_startCustomTimerTask) {
                                    LogUtils.i("客服定时任务 锁定--->" + noReplyTimeCustoms);
                                    stopCustomTimeTask();
                                    is_startCustomTimerTask = true;
                                    //如果会话锁定，客服计时器暂停计时,计时不归0；
                                    noReplyTimeCustoms = paseReplyTimeCustoms;
                                    customTimeTask = true;
                                } else {
                                    LogUtils.i("用户定时任务 锁定--->" + noReplyTimeUserInfo);
                                    stopUserInfoTimeTask();
                                    noReplyTimeUserInfo = paseReplyTimeUserInfo;
                                    userInfoTimeTask = true;
                                }
                            } else {
                                isChatLock = 2;
                                //2 会话解锁
                                if (current_client_model == ZhiChiConstant.client_model_customService) {
                                    if (is_startCustomTimerTask) {
                                        stopCustomTimeTask();
                                        startCustomTimeTask(handler);
                                        //如果会话锁定，客服计时器暂停计时,计时不归0；
                                        noReplyTimeCustoms = paseReplyTimeCustoms;
                                        customTimeTask = true;
                                        LogUtils.i("客服定时任务 解锁--->" + noReplyTimeCustoms);
                                    } else {
                                        stopUserInfoTimeTask();
                                        startUserInfoTimeTask(handler);
                                        userInfoTimeTask = true;
                                        noReplyTimeUserInfo = paseReplyTimeUserInfo;
                                        LogUtils.i("用户定时任务 解锁--->" + noReplyTimeUserInfo);
                                    }
                                }
                            }
                        }
                    } else if (ZhiChiConstant.push_message_custom_evaluate == pushMessage.getType()) {
                        LogUtils.i("客服推送满意度评价.................");
                        //显示推送消息体
                        if (isAboveZero && customerState == CustomerState.Online) {
                            // 满足评价条件，并且之前没有评价过的话 才能 弹评价框
                            ZhiChiMessageBase customEvaluateMode = ChatUtils.getCustomEvaluateMode(getSobotActivity(), pushMessage);
                            // 更新界面的操作
                            updateUiMessage(messageAdapter, customEvaluateMode);
                        }
                    } else if (ZhiChiConstant.push_message_retracted == pushMessage.getType()) {
                        if (!TextUtils.isEmpty(pushMessage.getRevokeMsgId())) {
                            List<ZhiChiMessageBase> datas = messageAdapter.getDatas();
                            for (int i = datas.size() - 1; i >= 0; i--) {
                                ZhiChiMessageBase msgData = datas.get(i);
                                if (pushMessage.getRevokeMsgId().equals(msgData.getMsgId())) {
                                    if (!msgData.isRetractedMsg()) {
                                        msgData.setRetractedMsg(true);
                                        messageAdapter.notifyDataSetChanged();
                                    }
                                    break;
                                }
                            }
                        }
                    }
                } else if (ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_LOCATION.equals(intent.getAction())) {
                    SobotLocationModel locationData = (SobotLocationModel) intent.getSerializableExtra(ZhiChiConstant.SOBOT_LOCATION_DATA);
                    if (locationData != null) {
                        sendLocation(null, locationData, handler, true);
                    }
                } else if (ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_TEXT.equals(intent.getAction())) {
                    String content = intent.getStringExtra(ZhiChiConstant.SOBOT_SEND_DATA);
                    String sendTextTo = intent.getStringExtra("sendTextTo");
                    if (ZhiChiConstant.client_model_robot == current_client_model && "robot".equals(sendTextTo)) { // 客户和机械人进行聊天
                        if (!TextUtils.isEmpty(content)) {
                            sendMsg(content);
                        }
                    } else if (ZhiChiConstant.client_model_customService == current_client_model && "user".equals(sendTextTo)) {
                        if (!TextUtils.isEmpty(content)) {
                            sendMsg(content);
                        }
                    }
                } else if (ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_OBJECT.equals(intent.getAction())) {
                    String content = intent.getStringExtra(ZhiChiConstant.SOBOT_SEND_DATA);
                    String type = intent.getStringExtra(ZhiChiConstant.SOBOT_TYPE_DATA);
                    if (ZhiChiConstant.client_model_customService == current_client_model) {
                        if (TextUtils.isEmpty(content)) {
                            LogUtils.i("发送内容不能为空");
                            return;
                        }
                        if ("0".equals(type)) {
                            //发送文本
                            sendMsg(content);
                        } else if ("1".equals(type)) {
                            //发送图片
                            File sendFile = new File(content);
                            if (sendFile.exists()) {
                                uploadFile(sendFile, handler, lv_message, messageAdapter, false);
                            }
                        } else if ("3".equals(type)) {
                            //发送视频
                            File sendFile = new File(content);
                            if (sendFile.exists()) {
                                uploadVideo(sendFile, null, messageAdapter);
                            }
                        } else if ("4".equals(type)) {
                            //发送文件
                            File sendFile = new File(content);
                            if (sendFile.exists()) {
                                uploadFile(sendFile, handler, lv_message, messageAdapter, false);
                            }
                        }
                    }
                } else if (ZhiChiConstant.SOBOT_BROCAST_ACTION_TRASNFER_TO_OPERATOR.equals(intent.getAction())) {
                    //外部调用转人工
                    SobotTransferOperatorParam transferParam = (SobotTransferOperatorParam) intent.getSerializableExtra(ZhiChiConstant.SOBOT_SEND_DATA);
                    if (transferParam != null) {
                        if (transferParam.getConsultingContent() != null) {
                            info.setConsultingContent(transferParam.getConsultingContent());
                        }
                        if (transferParam.getSummary_params() != null) {
                            info.setSummary_params(transferParam.getSummary_params());
                        }
                        SobotConnCusParam param = new SobotConnCusParam();
                        param.setGroupId(transferParam.getGroupId());
                        param.setGroupName(transferParam.getGroupName());
                        param.setKeyword(transferParam.getKeyword());
                        param.setKeywordId(transferParam.getKeywordId());
                        connectCustomerService(param, transferParam.isShowTips());
                    }
                } else if (ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_CARD.equals(intent.getAction())) {
                    ConsultingContent consultingContent = (ConsultingContent) intent.getSerializableExtra(ZhiChiConstant.SOBOT_SEND_DATA);
                    sendCardMsg(consultingContent);
                } else if (ZhiChiConstant.SOBOT_BROCAST_ACTION_SEND_ORDER_CARD.equals(intent.getAction())) {
                    OrderCardContentModel orderCardContent = (OrderCardContentModel) intent.getSerializableExtra(ZhiChiConstant.SOBOT_SEND_DATA);
                    sendOrderCardMsg(orderCardContent);
                }

                if (ZhiChiConstants.chat_remind_post_msg.equals(intent.getAction())) {
                    boolean isShowTicket = intent.getBooleanExtra("isShowTicket", false);
                    if (isShowTicket) {
                        for (int i = messageList.size() - 1; i > 0; i--) {
                            if (!TextUtils.isEmpty(messageList.get(i).getSenderType()) && Integer.parseInt(messageList.get(i).getSenderType()) == ZhiChiConstant.message_sender_type_remide_info
                                    && messageList.get(i).getAnswer() != null
                                    && ZhiChiConstant.sobot_remind_type_simple_tip == messageList.get(i).getAnswer().getRemindType()) {
                                messageList.remove(i);
                                messageAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                        Intent intent2 = mPostMsgPresenter.newPostMsgIntent(initModel.getUid(), null);
                        intent2.putExtra(StPostMsgPresenter.INTENT_KEY_COMPANYID, initModel.getCompanyId());
                        intent2.putExtra(StPostMsgPresenter.INTENT_KEY_CUSTOMERID, initModel.getCustomerId());
                        intent2.putExtra(ZhiChiConstant.FLAG_EXIT_SDK, false);
                        intent2.putExtra(StPostMsgPresenter.INTENT_KEY_GROUPID, info.getLeaveMsgGroupId());
                        intent2.putExtra(StPostMsgPresenter.INTENT_KEY_IS_SHOW_TICKET, true);
                        startActivity(intent2);
                        if (getSobotActivity() != null) {
                            getSobotActivity().overridePendingTransition(ResourceUtils.getIdByName(mAppContext, "anim", "sobot_push_left_in"),
                                    ResourceUtils.getIdByName(mAppContext, "anim", "sobot_push_left_out"));
                        }
                    } else {
                        startToPostMsgActivty(false, false);
                    }

                } else if (ZhiChiConstants.sobot_click_cancle.equals(intent.getAction())) {
                    //打开技能组后点击了取消
                    if (type == ZhiChiConstant.type_custom_first && current_client_model ==
                            ZhiChiConstant.client_model_robot) {
                        remindRobotMessage(handler, initModel, info);
                    }
                } else if (ZhiChiConstants.chat_remind_to_customer.equals(intent.getAction())) {
                    //转人工
                    doClickTransferBtn();
                } else if (ZhiChiConstants.SOBOT_POST_MSG_TMP_BROCAST.equals(intent.getAction())) {
                    //选完留言模版后跳转到留言界面
                    Intent postMsgIntent = new Intent(getContext(), SobotPostMsgActivity.class);
                    postMsgIntent.putExtra("intent_key_uid", intent.getStringExtra("uid"));
                    postMsgIntent.putExtra("intent_key_config", intent.getSerializableExtra("sobotLeaveMsgConfig"));
                    postMsgIntent.putExtra(StPostMsgPresenter.INTENT_KEY_COMPANYID, initModel.getCompanyId());
                    postMsgIntent.putExtra(StPostMsgPresenter.INTENT_KEY_CUSTOMERID, initModel.getCustomerId());
                    postMsgIntent.putExtra(ZhiChiConstant.FLAG_EXIT_SDK, intent.getBooleanExtra("mflag_exit_sdk", false));
                    postMsgIntent.putExtra(StPostMsgPresenter.INTENT_KEY_GROUPID, info.getLeaveMsgGroupId());
                    postMsgIntent.putExtra(StPostMsgPresenter.INTENT_KEY_IS_SHOW_TICKET, intent.getBooleanExtra("mIsShowTicket", false));
                    startActivity(postMsgIntent);
                    if (getSobotActivity() != null) {
                        getSobotActivity().overridePendingTransition(ResourceUtils.getIdByName(mAppContext, "anim", "sobot_push_left_in"),
                                ResourceUtils.getIdByName(mAppContext, "anim", "sobot_push_left_out"));
                    }
                } else if (ZhiChiConstants.dcrc_comment_state.equals(intent.getAction())) {
                    //评价完客户后所需执行的逻辑
                    isComment = intent.getBooleanExtra("commentState", false);
                    boolean isFinish = intent.getBooleanExtra("isFinish", false);
                    boolean isExitSession = intent.getBooleanExtra("isExitSession", false);
                    int commentType = intent.getIntExtra("commentType", 1);

                    //如果是邀请评价 更新ui
                    int score = intent.getIntExtra("score", 5);
                    int isResolved = intent.getIntExtra("isResolved", 0);
//                messageAdapter.submitEvaluateData(isResolved, score);
                    messageAdapter.removeEvaluateData();
                    messageAdapter.notifyDataSetChanged();
//                refreshItemByCategory(CusEvaluateMessageHolder.class);

                    if (isExitSession || ChatUtils.isEvaluationCompletedExit(mAppContext, isComment, current_client_model)) {
                        //如果是人工并且评价完毕就释放会话
                        isSessionOver = true;
                        customerServiceOffline(initModel);
                        if (isExitSession) {
                            ChatUtils.userLogout(mAppContext, "左上角返回弹窗结束会话和右上角关闭 弹窗评价后评价完毕 结束会话");
                        } else {
                            ChatUtils.userLogout(mAppContext, "开启了用户提交人工满意度评价后结束会话");
                        }
                    }
                    if (isActive()) {
                        ChatUtils.showThankDialog(getSobotActivity(), handler, isFinish);
                    }
                } else if (ZhiChiConstants.sobot_close_now.equals(intent.getAction())) {
                    if (intent.getBooleanExtra("isExitSession", true)) {
                        //右上角点击关闭，暂不评价 ，结束会话，在返回
                        customerServiceOffline(initModel);
                        isSessionOver = true;
                        ChatUtils.userLogout(mAppContext, "左上角返回弹窗结束会话和右上角关闭  弹窗评价后点击暂不评价 结束会话");
                        finish();
                    } else {
                        //左上角 返回 满意度评价弹窗 暂不评价，直接返回
                        finish();
                    }
                } else if (ZhiChiConstants.sobot_close_now_clear_cache.equals(intent.getAction())) {
                    isSessionOver = true;
                    finish();
                } else if (ZhiChiConstants.SOBOT_CHANNEL_STATUS_CHANGE.equals(intent.getAction())) {
                    if (customerState == CustomerState.Online || customerState == CustomerState.Queuing) {
                        int connStatus = intent.getIntExtra("connStatus", Const.CONNTYPE_IN_CONNECTION);
                        LogUtils.i("connStatus:" + connStatus);
                        switch (connStatus) {
                            case Const.CONNTYPE_IN_CONNECTION:
                                sobot_container_conn_status.setVisibility(View.VISIBLE);
                                sobot_title_conn_status.setText(getResString("sobot_conntype_in_connection"));
                                if (sobot_header_center_ll != null) {
                                    sobot_header_center_ll.setVisibility(View.GONE);
                                }
                                sobot_conn_loading.setVisibility(View.VISIBLE);
                                break;
                            case Const.CONNTYPE_CONNECT_SUCCESS:
                                setShowNetRemind(false);
                                sobot_container_conn_status.setVisibility(View.GONE);
                                sobot_title_conn_status.setText("");
                                if (sobot_header_center_ll != null) {
                                    sobot_header_center_ll.setVisibility(View.VISIBLE);
                                }
                                sobot_conn_loading.setVisibility(View.GONE);
                                stopPolling();
                                break;
                            case Const.CONNTYPE_UNCONNECTED:
                                sobot_container_conn_status.setVisibility(View.VISIBLE);
                                sobot_title_conn_status.setText(getResString("sobot_conntype_unconnected"));
                                if (sobot_header_center_ll != null) {
                                    sobot_header_center_ll.setVisibility(View.GONE);
                                }
                                sobot_conn_loading.setVisibility(View.GONE);
                                if (welcome.getVisibility() != View.VISIBLE) {
                                    setShowNetRemind(true);
                                }
                                break;
                        }
                    } else {
                        mTitleTextView.setVisibility(View.GONE);
                        mAvatarIV.setVisibility(View.VISIBLE);
                        sobot_container_conn_status.setVisibility(View.GONE);
                    }
                } else if (ZhiChiConstants.SOBOT_BROCAST_KEYWORD_CLICK.equals(intent.getAction())) {
                    String tempGroupId = intent.getStringExtra("tempGroupId");
                    String keyword = intent.getStringExtra("keyword");
                    String keywordId = intent.getStringExtra("keywordId");
                    transfer2Custom(tempGroupId, keyword, keywordId, true);
                } else if (ZhiChiConstants.SOBOT_BROCAST_REMOVE_FILE_TASK.equals(intent.getAction())) {
                    try {
                        String msgId = intent.getStringExtra("sobot_msgId");
                        if (!TextUtils.isEmpty(msgId)) {
                            for (int i = messageList.size() - 1; i >= 0; i--) {
                                if (msgId.equals(messageList.get(i).getId())) {
                                    messageList.remove(i);
                                    break;
                                }
                            }
                            messageAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        //ignor
                    }
                } else if (ZhiChiConstants.SOBOT_CHAT_MUITILEAVEMSG_TO_CHATLIST.equals(intent.getAction())) {
                    //多伦工单节点留言弹窗留言提交后回显到聊天列表
                    if (intent != null) {
                        Bundle bundle = intent.getExtras();
                        SobotSerializableMap sobotSerializableMap = (SobotSerializableMap) bundle.get("leaveMsgData");
                        if (sobotSerializableMap != null) {
                            LinkedHashMap mapData = sobotSerializableMap.getMap();
                            StringBuilder tempSb = new StringBuilder();
                            Iterator iterator = mapData.entrySet().iterator();
                            while (iterator.hasNext()) {
                                Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
                                tempSb.append(entry.getKey()).append("\n").append(entry.getValue()).append("\n");
                            }
                            if (!TextUtils.isEmpty(tempSb.toString())) {
                                sendMuitidiaLeaveMsg(null, tempSb.toString().substring(0, tempSb.toString().lastIndexOf("\n")), handler, true);
                            }
                            String tipMsgId = (String) bundle.get("tipMsgId");
                            if (!TextUtils.isEmpty(tipMsgId)) {
                                ZhiChiMessageBase base =
                                        messageAdapter.getMsgInfoByMsgId(tipMsgId);
                                base.setAction(ZhiChiConstant.action_mulit_postmsg_tip_nocan_click);
                                messageAdapter.notifyDataSetChanged();
                            }
                        } else {
                            String msgId = intent.getStringExtra("msgId");
                            String msg = intent.getStringExtra("msg");
                            String deployId = intent.getStringExtra("deployId");
                            ZhiChiMessageBase base = new ZhiChiMessageBase();
                            base.setMsgId(msgId);
                            base.setDeployId(deployId);
                            base.setAction(ZhiChiConstant.action_mulit_postmsg_tip_can_click);
                            base.setMsg(msg);
                            updateUiMessage(messageAdapter, base);
                        }
                    }
                } else if (ZhiChiConstants.SOBOT_CHAT_MUITILEAVEMSG_RE_COMMIT.equals(intent.getAction())) {
                    String templateId = intent.getStringExtra("templateId");
                    String msgId = intent.getStringExtra("msgId");
                    //多伦工单节点提醒点击后重复弹窗
                    mulitDiaToLeaveMsg(templateId, msgId);
                } else if (ZhiChiConstants.CHAT_REMIND_KEEP_QAUEUING.equals(intent.getAction())) {
                    //点击继续排队
                    zhiChiApi.keepQueuing(SobotChatFSFragment.this, initModel.getPartnerid(), new StringResultCallBack<CommonModel>() {
                        @Override
                        public void onSuccess(CommonModel commonModel) {
                            if (messageAdapter != null) {
                                messageAdapter.removeByAction(ZhiChiConstant
                                        .action_remind_keep_queuing);
                                messageAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Exception e, String s) {

                        }
                    });
                }
            } catch (Exception e) {

            }
        }
    }

    //保存当前的数据，进行会话保持
    private void saveCache() {
        ZhiChiConfig config = SobotMsgManager.getInstance(mAppContext).getConfig(info.getApp_key());
        config.isShowUnreadUi = true;
        config.setMessageList(messageList);
        config.setInitModel(initModel);
        config.current_client_model = current_client_model;
        //离开界面时,如果当前聊天模式是人工
        if (current_client_model == ZhiChiConstant.client_model_customService) {
            if (!CommonUtils.isServiceWork(getSobotActivity(), "com.sobot.chat.server.SobotSessionServer")) {
                LogUtils.i2Local("fragment ", "离开聊天页面，当前聊天模式是人工，检测到SobotSessionServer 没有在运行，再次启动SobotSessionServer服务");
                Intent intent = new Intent(mAppContext, SobotSessionServer.class);
                intent.putExtra(ZhiChiConstant.SOBOT_CURRENT_IM_PARTNERID, info.getPartnerid());
                StServiceUtils.safeStartService(mAppContext, intent);
            }
        }
        if (queryCidsStatus == ZhiChiConstant.QUERY_CIDS_STATUS_SUCCESS) {
            config.cids = cids;
            config.currentCidPosition = currentCidPosition;
            config.queryCidsStatus = queryCidsStatus;
        }

        config.activityTitle = getActivityTitle();
        config.customerState = customerState;
        config.remindRobotMessageTimes = remindRobotMessageTimes;
        config.isAboveZero = isAboveZero;
        config.isComment = isComment;
        config.adminFace = getAdminFace();
        config.adminName = getAdminName();
        config.paseReplyTimeCustoms = noReplyTimeCustoms;
        config.customTimeTask = customTimeTask;
        config.paseReplyTimeUserInfo = noReplyTimeUserInfo;
        config.userInfoTimeTask = userInfoTimeTask;
        config.isChatLock = isChatLock;
        config.currentUserName = currentUserName;
        config.isNoMoreHistoryMsg = isNoMoreHistoryMsg;
        config.showTimeVisiableCustomBtn = showTimeVisiableCustomBtn;
        config.bottomViewtype = mBottomViewtype;
        config.queueNum = queueNum;
        config.isShowQueueTip = isShowQueueTip;
        config.tempMsgContent = tempMsgContent;
        config.inPolling = inPolling;

        if (config.isChatLock == 2 || config.isChatLock == 0) {
            Intent intent = new Intent();
            intent.setAction(ZhiChiConstants.SOBOT_TIMER_BROCAST);
            intent.putExtra("info", info);
            intent.putExtra("isStartTimer", true);
            localBroadcastManager.sendBroadcast(intent);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == notReadInfo) {
            for (int i = messageList.size() - 1; i >= 0; i--) {
                if (messageList.get(i).getAnswer() != null && ZhiChiConstant.
                        sobot_remind_type_below_unread == messageList.get(i).getAnswer().getRemindType()) {
                    lv_message.setSelection(i);
                    break;
                }
            }
            notReadInfo.setVisibility(View.GONE);
        }

        if (view == btn_send || view == btn_send_pic) {// 发送消息按钮
            //获取发送内容
            final String message_result = et_sendmessage.getText().toString().trim();
            if (TextUtils.isEmpty(message_result)) {
                et_sendmessage.setText("");
            }
            if (!TextUtils.isEmpty(message_result) && !isConnCustomerService) {
                //转人工接口没跑完的时候  屏蔽发送，防止统计出现混乱
                resetEmoticonBtn();
                try {
                    et_sendmessage.setText("");
                    sendMsg(message_result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (view == btn_upload_view) {
            LogUtils.i("-------点击加号-------");
            pressSpeakSwitchPanelAndKeyboard(btn_upload_view);
            doEmoticonBtn2Blur();
            gotoLastItem();
        }

        if (view == btn_emoticon_view) {//显示表情面板
            // 切换表情面板
            pressSpeakSwitchPanelAndKeyboard(btn_emoticon_view);
            //切换表情按钮的状态
            switchEmoticonBtn();
            gotoLastItem();
        }

        if (view == btn_model_edit) {// 从编辑模式转换到语音
            hideRobotVoiceHint();
            doEmoticonBtn2Blur();
            // 软件盘的处理
            KPSwitchConflictUtil.showKeyboard(mPanelRoot, et_sendmessage);
            editModelToVoice(View.GONE, "123");// 编辑模式隐藏 ，语音模式显示
        }

        if (view == btn_model_voice) { // 从语音转换到编辑模式
            showRobotVoiceHint();
            doEmoticonBtn2Blur();
            permissionListener = new PermissionListenerImpl() {
                @Override
                public void onPermissionSuccessListener() {
                    showAudioRecorder();
                }
            };
            if (!isHasPermission(2, 3)) {
                return;
            }
            showAudioRecorder();
        }

        if (view == sobot_ll_switch_robot) {
            // 打开机器人切换页面
            if (!isSessionOver && (mRobotListDialog == null || !mRobotListDialog.isShowing())) {
                mRobotListDialog = ChatUtils.showRobotListDialog(getSobotActivity(), initModel, this);
            }
        }

        if (view == sobot_tv_right_second) {
            if (!TextUtils.isEmpty(SobotUIConfig.sobot_title_right_menu2_call_num)) {
                if (SobotOption.functionClickListener != null) {
                    SobotOption.functionClickListener.onClickFunction(getSobotActivity(), SobotFunctionType.ZC_PhoneCustomerService);
                }
                if (SobotOption.newHyperlinkListener != null) {
                    boolean isIntercept = SobotOption.newHyperlinkListener.onPhoneClick(getSobotActivity(), "tel:" + SobotUIConfig.sobot_title_right_menu2_call_num);
                    if (isIntercept) {
                        return;
                    }
                }
                ChatUtils.callUp(SobotUIConfig.sobot_title_right_menu2_call_num, getContext());
            } else {
                btnSatisfaction();
            }
        }

        if (view == sobot_tv_right_third) {
            if (!TextUtils.isEmpty(SobotUIConfig.sobot_title_right_menu3_call_num)) {
                if (SobotOption.functionClickListener != null) {
                    SobotOption.functionClickListener.onClickFunction(getSobotActivity(), SobotFunctionType.ZC_PhoneCustomerService);
                }
                if (SobotOption.newHyperlinkListener != null) {
                    boolean isIntercept = SobotOption.newHyperlinkListener.onPhoneClick(getSobotActivity(), "tel:" + SobotUIConfig.sobot_title_right_menu3_call_num);
                    if (isIntercept) {
                        return;
                    }
                }
                ChatUtils.callUp(SobotUIConfig.sobot_title_right_menu3_call_num, getContext());
            } else {
                LogUtils.e("电话号码不能为空");
            }
        }
    }

    //开始录音
    private void showAudioRecorder() {
        try {
            mFileName = SobotPathManager.getInstance().getVoiceDir() + "sobot_tmp.wav";
            String state = android.os.Environment.getExternalStorageState();
            if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
                LogUtils.i("SD Card is not mounted,It is  " + state + ".");
            }
            File directory = new File(mFileName).getParentFile();
            if (!directory.exists() && !directory.mkdirs()) {
                LogUtils.i("Path to file could not be created");
            }
            extAudioRecorder = ExtAudioRecorder.getInstanse(false);
            extAudioRecorder.setOutputFile(mFileName);
            extAudioRecorder.prepare();
            extAudioRecorder.start(new ExtAudioRecorder.AudioRecorderListener() {
                @Override
                public void onHasPermission() {
                    hidePanelAndKeyboard(mPanelRoot);
                    editModelToVoice(View.VISIBLE, "");// 编辑模式显示
                    if (btn_press_to_speak.getVisibility() == View.VISIBLE) {
                        btn_press_to_speak.setVisibility(View.VISIBLE);
                        btn_press_to_speak.setClickable(true);
                        btn_press_to_speak.setOnTouchListener(new PressToSpeakListen());
                        btn_press_to_speak.setEnabled(true);
                        txt_speak_content.setText(getResString("sobot_press_say"));
                        txt_speak_content.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNoPermission() {
                    ToastUtil.showToast(mAppContext, getResString("sobot_no_record_audio_permission"));
                }
            });
            stopVoice();
        } catch (Exception e) {
            LogUtils.i("prepare() failed");
        }
    }

    private void showRobotVoiceHint() {
        send_voice_robot_hint.setVisibility(current_client_model == ZhiChiConstant.client_model_robot ? View.VISIBLE : View.GONE);
    }

    private void hideRobotVoiceHint() {
        send_voice_robot_hint.setVisibility(View.GONE);
    }

    /**
     * 发送消息的方法
     *
     * @param content
     */
    @Override
    protected void sendMsg(String content) {
        if (initModel == null) {
            return;
        }

        String msgId = (initModel != null ? initModel.getCid() : "") + System.currentTimeMillis() + "";

        if (ZhiChiConstant.client_model_robot == current_client_model) {
            if (type == 4 && initModel.getInvalidSessionFlag() == 1 && customerState != CustomerState.Queuing && TextUtils.isEmpty(tempMsgContent)) {
                //人工优先,用户没有排队并且开启客户发送消息后分配客服,转人工发送该消息
                //如果排队，再发送的消息就是机器人的消息
                tempMsgContent = content;
                doClickTransferBtn();
                return;
            }
            if (type == 2) {
                if (initModel.getInvalidSessionFlag() == 1) {
                    //开启客户发送消息后分配客服,转人工发送该消息
                    tempMsgContent = content;
                }
                doClickTransferBtn();
                return;
            } else if ((type == 3 || type == 4) && info.getTransferKeyWord() != null) {
                //用户可以输入关键字 进行转人工
                HashSet<String> transferKeyWord = info.getTransferKeyWord();
                if (!TextUtils.isEmpty(content) && transferKeyWord.contains(content)) {
                    sendTextMessageToHandler(msgId, content, handler, 1, SEND_TEXT);
                    doClickTransferBtn();
                    return;
                }
            }
        }

        // 通知Handler更新 我的消息ui
        sendTextMessageToHandler(msgId, content, handler, 2, SEND_TEXT);

        LogUtils.i("当前发送消息模式：" + current_client_model);
        setTimeTaskMethod(handler);
        sendMessageWithLogic(msgId, content, initModel, handler, current_client_model, 0, "");
    }

    /**
     * 发送卡片消息
     *
     * @param consultingContent
     */
    protected void sendCardMsg(ConsultingContent consultingContent) {
        if (initModel == null || consultingContent == null) {
            return;
        }
        final String title = consultingContent.getSobotGoodsTitle();
        final String fromUrl = consultingContent.getSobotGoodsFromUrl();
        if (customerState == CustomerState.Online
                && current_client_model == ZhiChiConstant.client_model_customService
                && !TextUtils.isEmpty(fromUrl) && !TextUtils.isEmpty(title)) {
            String msgId = (initModel != null ? initModel.getCid() : "") + System.currentTimeMillis() + "";

            setTimeTaskMethod(handler);
            sendHttpCardMsg(initModel.getPartnerid(), initModel.getCid(), handler, msgId, consultingContent);
        }
    }

    /**
     * 发送订单卡片消息
     *
     * @param orderCardContent
     */
    protected void sendOrderCardMsg(OrderCardContentModel orderCardContent) {
        if (initModel == null || orderCardContent == null) {
            return;
        }
        final String title = orderCardContent.getOrderCode();
        if (customerState == CustomerState.Online
                && current_client_model == ZhiChiConstant.client_model_customService
                && !TextUtils.isEmpty(title)) {
            String msgId = (initModel != null ? initModel.getCid() : "") + System.currentTimeMillis() + "";
            setTimeTaskMethod(handler);
            sendHttpOrderCardMsg(initModel.getPartnerid(), initModel.getCid(), handler, msgId, orderCardContent);
        }
    }

    /**
     * 满意度评价
     * 首先判断是否评价过 评价过 弹您已完成提示 未评价 判断是否达到可评价标准
     *
     * @param isActive    是否是主动评价  true 主动  flase 邀请
     * @param score       几颗星
     * @param isSolve     是否已解决 0 是已解决  1 未解决
     * @param checklables 主动邀请选中的标签
     */
    public void submitEvaluation(boolean isActive, int score, int isSolve, String checklables) {
        if (isActive && isComment) {
            //主动评价 并且已经评价过,就不能再次弹出评价
            hidePanelAndKeyboard(mPanelRoot);
            showHint(getResString("sobot_completed_the_evaluation"));
            return;
        }
        if (isUserBlack()) {
            showHint(getResString("sobot_unable_to_evaluate"));
        } else if (isAboveZero) {
            if (isActive()) {
                Intent intent = new Intent(getSobotActivity(), SobotEvaluateActivity.class);
                intent.putExtra("score", score);
                intent.putExtra("isSessionOver", isSessionOver);
                intent.putExtra("isFinish", false);
                intent.putExtra("isExitSession", false);
                intent.putExtra("initModel", initModel);
                intent.putExtra("current_model", current_client_model);
                intent.putExtra("commentType", isActive ? 1 : 0);
                intent.putExtra("customName", currentUserName);
                intent.putExtra("isSolve", isSolve);
                intent.putExtra("checklables", checklables);
                intent.putExtra("isBackShowEvaluate", false);
                intent.putExtra("canBackWithNotEvaluation", false);
                startActivity(intent);
            }
        } else {
            showHint(getResString("sobot_after_consultation_to_evaluate_custome_service"));
        }
    }

    public void showVoiceBtn() {
        if (current_client_model == ZhiChiConstant.client_model_robot && type != 2) {
            btn_model_voice.setVisibility(info.isUseVoice() && info.isUseRobotVoice() ? View.VISIBLE : View.GONE);
            view_model_split.setVisibility(info.isUseVoice() && info.isUseRobotVoice() ? View.VISIBLE : View.GONE);
            if (type == 1) {
                //仅机器人模式，隐藏分割线
                view_model_split.setVisibility(View.GONE);
            }
        } else {
            view_model_split.setVisibility(View.GONE);
            btn_model_voice.setVisibility(info.isUseVoice() ? View.VISIBLE : View.GONE);
        }
    }

    private void sendMsgToRobot(ZhiChiMessageBase base, int sendType, int questionFlag, String docId) {
        sendMsgToRobot(base, sendType, questionFlag, docId, null);
    }

    private void sendMsgToRobot(ZhiChiMessageBase base, int sendType, int questionFlag, String docId, String multiRoundMsg) {
        if (!TextUtils.isEmpty(multiRoundMsg)) {
            sendTextMessageToHandler(base.getId(), multiRoundMsg, handler, 2, sendType);
        } else {
            sendTextMessageToHandler(base.getId(), base.getContent(), handler, 2, sendType);
        }
        ZhiChiReplyAnswer answer = new ZhiChiReplyAnswer();
        answer.setMsgType(ZhiChiConstant.message_type_text + "");
        answer.setMsg(base.getContent());
        base.setAnswer(answer);
        base.setSenderType(ZhiChiConstant.message_sender_type_customer + "");
        sendMessageWithLogic(base.getId(), base.getContent(), initModel, handler, current_client_model, questionFlag, docId);
    }

    /**
     * 更新 多轮会话的状态
     */
    private void restMultiMsg() {
        for (int i = 0; i < messageList.size(); i++) {
            ZhiChiMessageBase data = messageList.get(i);
            if (data.getAnswer() != null && data.getAnswer().getMultiDiaRespInfo() != null
                    && !data.getAnswer().getMultiDiaRespInfo().getEndFlag()) {
                data.setMultiDiaRespEnd(1);
            }
        }
        messageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            LogUtils.i("多媒体返回的结果：" + requestCode + "--" + resultCode + "--" + data);

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == ZhiChiConstant.REQUEST_CODE_picture) { // 发送本地图片
                    if (data != null && data.getData() != null) {
                        Uri selectedImage = data.getData();
                        if (selectedImage == null) {
                            selectedImage = ImageUtils.getUri(data, getSobotActivity());
                        }
                        String path = ImageUtils.getPath(getSobotActivity(), selectedImage);
                        if (MediaFileUtils.isVideoFileType(path)) {
                            try {
                                File selectedFile = new File(path);
                                if (selectedFile.exists()) {
                                    if (selectedFile.length() > 50 * 1024 * 1024) {
                                        ToastUtil.showToast(getContext(), getResString("sobot_file_upload_failed"));
                                        return;
                                    }
                                }
                                //SobotDialogUtils.startProgressDialog(getSobotActivity());
                                File videoFile = new File(path);
                                if (videoFile.exists()) {
                                    uploadVideo(videoFile, selectedImage, messageAdapter);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            ChatUtils.sendPicByUri(getSobotActivity(), handler, selectedImage, initModel, lv_message, messageAdapter, false);
                        }
                    } else {
                        ToastUtil.showLongToast(mAppContext, getResString("sobot_did_not_get_picture_path"));
                    }

                }
                hidePanelAndKeyboard(mPanelRoot);
            }
            if (data != null) {
                switch (requestCode) {
                    case ZhiChiConstant.REQUEST_COCE_TO_GRROUP:
                        boolean toLeaveMsg = data.getBooleanExtra("toLeaveMsg", false);
                        int groupIndex = data.getIntExtra("groupIndex", -1);
                        if (toLeaveMsg) {
                            SharedPreferencesUtil.saveStringData(getSobotActivity(), ZhiChiConstant.sobot_connect_group_id, list_group != null ? list_group.get(groupIndex).getGroupId() : "");
                            startToPostMsgActivty(false);
                            return;
                        }
                        int tmpTransferType = data.getIntExtra("transferType", 0);
                        LogUtils.i("groupIndex-->" + groupIndex);
                        if (groupIndex >= 0) {
                            SobotConnCusParam param = (SobotConnCusParam) data.getSerializableExtra(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_CONNCUSPARAM);
                            if (param != null) {
                                param.setGroupId(list_group.get(groupIndex).getGroupId());
                                param.setGroupName(list_group.get(groupIndex).getGroupName());
                                param.setTransferType(tmpTransferType);
                            }
                            requestQueryFrom(param, info.isCloseInquiryForm());
                        }
                        break;
                    case ZhiChiConstant.REQUEST_COCE_TO_QUERY_FROM:
                        //填完询前表单后的回调
                        if (resultCode == ZhiChiConstant.REQUEST_COCE_TO_QUERY_FROM) {
                            SobotConnCusParam param = (SobotConnCusParam) data.getSerializableExtra(ZhiChiConstant.SOBOT_INTENT_BUNDLE_DATA_CONNCUSPARAM);
                            connectCustomerService(param);
                        } else {
                            //询前表单取消
                            isHasRequestQueryFrom = false;
                            if (type == ZhiChiConstant.type_custom_only) {
                                //仅人工模式退出聊天
                                isSessionOver = true;
                                //清除会话信息
                                clearCache();
                                finish();
                            }
                        }
                        break;
                    case ZhiChiConstant.REQUEST_COCE_TO_CHOOSE_FILE:
                        Uri selectedFileUri = data.getData();
                        if (null == selectedFileUri) {
                            File selectedFile = (File) data.getSerializableExtra(ZhiChiConstant.SOBOT_INTENT_DATA_SELECTED_FILE);
                            uploadFile(selectedFile, handler, lv_message, messageAdapter, false);
                        } else {
                            String tmpMsgId = (initModel != null ? initModel.getCid() : "") + String.valueOf(System.currentTimeMillis());
                            if (selectedFileUri == null) {
                                selectedFileUri = ImageUtils.getUri(data, getSobotActivity());
                            }
                            String path = ImageUtils.getPath(getSobotActivity(), selectedFileUri);
                            if (TextUtils.isEmpty(path)) {
                                ToastUtil.showToast(getSobotActivity(), ResourceUtils.getResString(getSobotActivity(), "sobot_cannot_open_file"));
                                return;
                            }
                            File selectedFile = new File(path);
                            LogUtils.i("tmpMsgId:" + tmpMsgId);
                            uploadFile(selectedFile, handler, lv_message, messageAdapter, true);
                        }
                        break;
                    case REQUEST_CODE_CAMERA:
                        int actionType = SobotCameraActivity.getActionType(data);
                        if (actionType == SobotCameraActivity.ACTION_TYPE_VIDEO) {
                            File videoFile = new File(SobotCameraActivity.getSelectedVideo(data));
                            if (videoFile.exists()) {
                                String snapshotPath = SobotCameraActivity.getSelectedImage(data);
                                uploadVideo(videoFile, null, messageAdapter);
                            } else {
                                ToastUtil.showLongToast(mAppContext, getResString("sobot_pic_select_again"));
                            }
                        } else {
                            File tmpPic = new File(SobotCameraActivity.getSelectedImage(data));
                            if (tmpPic.exists()) {
                                ChatUtils.sendPicLimitBySize(tmpPic.getAbsolutePath(), initModel.getCid(),
                                        initModel.getPartnerid(), handler, getSobotActivity(), lv_message, messageAdapter, true);
                            } else {
                                ToastUtil.showLongToast(mAppContext, getResString("sobot_pic_select_again"));
                            }
                        }
                        break;
                    case SobotPostLeaveMsgActivity.EXTRA_MSG_LEAVE_REQUEST_CODE:
                        //离线留言
                        String content = SobotPostLeaveMsgActivity.getResultContent(data);
                        ZhiChiMessageBase tmpMsg = ChatUtils.getLeaveMsgTip(content);

                        ZhiChiMessageBase tmpMsg2 = ChatUtils.getTipByText(ResourceUtils.getResString(getSobotActivity(), "sobot_leavemsg_success_tip"));
                        messageAdapter.justAddData(tmpMsg);
                        messageAdapter.justAddData(tmpMsg2);
                        messageAdapter.notifyDataSetChanged();
                        customerServiceOffline(initModel, 99);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class PressToSpeakListen implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            isCutVoice = false;
            // 获取说话位置的点击事件
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    voiceMsgId = (initModel != null ? initModel.getCid() : "") + System.currentTimeMillis() + "";
                    // 在这个点击的位置
                    btn_upload_view.setClickable(false);
                    btn_model_edit.setClickable(false);
                    btn_upload_view.setEnabled(false);
                    btn_model_edit.setEnabled(false);
                    if (Build.VERSION.SDK_INT >= 11) {
                        btn_upload_view.setAlpha(0.4f);
                        btn_model_edit.setAlpha(0.4f);
                    }
                    stopVoiceTimeTask();
                    v.setPressed(true);
                    voice_time_long.setText("00" + "''");
                    voiceTimeLongStr = "00:00";
                    voiceTimerLong = 0;
                    currentVoiceLong = 0;
                    recording_container.setVisibility(View.VISIBLE);
                    voice_top_image.setVisibility(View.VISIBLE);
                    mic_image.setVisibility(View.VISIBLE);
                    mic_image_animate.setVisibility(View.VISIBLE);
                    voice_time_long.setVisibility(View.VISIBLE);
                    recording_timeshort.setVisibility(View.GONE);
                    image_endVoice.setVisibility(View.GONE);
                    txt_speak_content.setText(getResString("sobot_up_send"));
                    // 设置语音的定时任务
                    startVoice();
                    return true;
                // 第二根手指按下
                case MotionEvent.ACTION_POINTER_DOWN:
                    return true;
                case MotionEvent.ACTION_POINTER_UP:
                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (!is_startCustomTimerTask) {
                        noReplyTimeUserInfo = 0;
                    }

                    if (event.getY() < 10) {
                        // 取消界面的显示
                        voice_top_image.setVisibility(View.GONE);
                        image_endVoice.setVisibility(View.VISIBLE);
                        mic_image.setVisibility(View.GONE);
                        mic_image_animate.setVisibility(View.GONE);
                        recording_timeshort.setVisibility(View.GONE);
                        txt_speak_content.setText(getResString("sobot_release_to_cancel"));
                        recording_hint.setText(getResString("sobot_release_to_cancel"));
                        recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg"));
                    } else {
                        if (voiceTimerLong != 0) {
                            txt_speak_content.setText(getResString("sobot_up_send"));
                            voice_top_image.setVisibility(View.VISIBLE);
                            mic_image_animate.setVisibility(View.VISIBLE);
                            image_endVoice.setVisibility(View.GONE);
                            mic_image.setVisibility(View.VISIBLE);
                            recording_timeshort.setVisibility(View.GONE);
                            recording_hint.setText(getResString("sobot_move_up_to_cancel"));
                            recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg1"));
                        }
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    // 手指抬起的操作
                    int toLongOrShort = 0;
                    btn_upload_view.setClickable(true);
                    btn_model_edit.setClickable(true);
                    btn_upload_view.setEnabled(true);
                    btn_model_edit.setEnabled(true);
                    if (Build.VERSION.SDK_INT >= 11) {
                        btn_upload_view.setAlpha(1f);
                        btn_model_edit.setAlpha(1f);
                    }
                    v.setPressed(false);
                    txt_speak_content.setText(getResString("sobot_press_say"));
                    stopVoiceTimeTask();
                    stopVoice();
                    if (recording_container.getVisibility() == View.VISIBLE
                            && !isCutVoice) {
                        hidePanelAndKeyboard(mPanelRoot);
                        if (animationDrawable != null) {
                            animationDrawable.stop();
                        }
                        voice_time_long.setText("00" + "''");
                        voice_time_long.setVisibility(View.GONE);
                        if (event.getY() < 0) {
                            recording_container.setVisibility(View.GONE);
                            sendVoiceMap(2, voiceMsgId);
                            return true;
                            // 取消发送语音
                        } else {
                            // 发送语音
                            if (currentVoiceLong < 1 * 1000) {
                                voice_top_image.setVisibility(View.VISIBLE);
                                recording_hint.setText(getResString("sobot_voice_time_short"));
                                recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg"));
                                recording_timeshort.setVisibility(View.VISIBLE);
                                voice_time_long.setVisibility(View.VISIBLE);
                                voice_time_long.setText("00:00");
                                mic_image.setVisibility(View.GONE);
                                mic_image_animate.setVisibility(View.GONE);
                                toLongOrShort = 0;
                                sendVoiceMap(2, voiceMsgId);
                            } else if (currentVoiceLong < minRecordTime * 1000) {
                                recording_container.setVisibility(View.GONE);
                                sendVoiceMap(1, voiceMsgId);
                                return true;
                            } else if (currentVoiceLong > minRecordTime * 1000) {
                                toLongOrShort = 1;
                                voice_top_image.setVisibility(View.VISIBLE);
                                recording_hint.setText(getResString("sobot_voiceTooLong"));
                                recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg"));
                                recording_timeshort.setVisibility(View.VISIBLE);
                                mic_image.setVisibility(View.GONE);
                                mic_image_animate.setVisibility(View.GONE);
                            } else {
                                sendVoiceMap(2, voiceMsgId);
                            }
                        }
                        currentVoiceLong = 0;
                        closeVoiceWindows(toLongOrShort);
                    } else {
                        sendVoiceMap(2, voiceMsgId);
                    }
                    voiceTimerLong = 0;
                    restartMyTimeTask(handler);
                    // mFileName
                    return true;
                default:
                    sendVoiceMap(2, voiceMsgId);
                    closeVoiceWindows(2);
                    return true;
            }
        }
    }

    // 获取标题内容
    public String getActivityTitle() {
        return mTitleTextView.getText().toString();
    }

    /**
     * 返回键监听
     *
     * @return true 消费事件
     */
    public void onBackPress() {
        if (isActive()) {
            //按返回按钮的时候 如果面板显示就隐藏面板  如果面板已经隐藏那么就是用户想退出
            if (mPanelRoot.getVisibility() == View.VISIBLE) {
                hidePanelAndKeyboard(mPanelRoot);
                return;
            } else {
                if (info.isShowSatisfaction() || (info.getIsSetShowSatisfaction() == 0 && initModel != null && initModel.getCommentFlag() == 1)) {
                    if (isAboveZero && !isComment) {
                        // 退出时 之前没有评价过的话 才能 弹评价框
                        openEcaluate();
                        return;
                    }
                }
            }
            finish();
        }
    }

    protected String getSendMessageStr() {
        return et_sendmessage.getText().toString().trim();
    }

    private void sobotCustomMenu() {
        if (!initModel.isLableLinkFlag()) {
            return;
        }
        final int marginRight = (int) getDimens("sobot_layout_lable_margin_right");
        //自定义菜单获取接口
        zhiChiApi.getLableInfoList(SobotChatFSFragment.this, initModel.getPartnerid(), new StringResultCallBack<List<SobotLableInfoList>>() {
            @Override
            public void onSuccess(final List<SobotLableInfoList> infoLists) {
                if (!isActive()) {
                    return;
                }

                sobot_custom_menu_linearlayout.removeAllViews();
                if (infoLists != null && infoLists.size() > 0) {
                    for (int i = 0; i < infoLists.size(); i++) {
                        final TextView tv = (TextView) View.inflate(getContext(), getResLayoutId("sobot_layout_lable"), null);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0, 0, marginRight, 0);
                        tv.setLayoutParams(layoutParams);
                        tv.setText(infoLists.get(i).getLableName());
                        tv.setTag(infoLists.get(i).getLableLink());
                        sobot_custom_menu_linearlayout.addView(tv);
                        if (!TextUtils.isEmpty(tv.getTag() + "")) {
                            tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    hidePanelAndKeyboard(mPanelRoot);
                                    if (SobotOption.hyperlinkListener != null) {
                                        SobotOption.hyperlinkListener.onUrlClick(v.getTag() + "");
                                        return;
                                    }
                                    if (SobotOption.newHyperlinkListener != null) {
                                        //如果返回true,拦截;false 不拦截
                                        boolean isIntercept = SobotOption.newHyperlinkListener.onUrlClick(getSobotActivity(), v.getTag() + "");
                                        if (isIntercept) {
                                            return;
                                        }
                                    }

                                    Intent intent = new Intent(getContext(), WebViewActivity.class);
                                    intent.putExtra("url", v.getTag() + "");
                                    getSobotActivity().startActivity(intent);
                                }
                            });
                        }
                    }
                    sobot_custom_menu.setVisibility(View.VISIBLE);
                } else {
                    sobot_custom_menu.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                if (!isActive()) {
                    return;
                }
                sobot_custom_menu.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 显示切换机器人业务的按钮
     */
    private void showSwitchRobotBtn() {
        if (initModel != null && type != 2 && current_client_model == ZhiChiConstant.client_model_robot) {
            sobot_ll_switch_robot.setVisibility(initModel.isRobotSwitchFlag() ? View.VISIBLE : View.GONE);
        } else {
            sobot_ll_switch_robot.setVisibility(View.GONE);
        }
    }

    /**
     * 机器人切换列表的回调
     */
    @Override
    public void onSobotRobotListItemClick(SobotRobot sobotRobot) {
        if (initModel != null && sobotRobot != null) {
            initModel.setGuideFlag(sobotRobot.getGuideFlag());
            initModel.setRobotid(sobotRobot.getRobotFlag());
            initModel.setRobotLogo(sobotRobot.getRobotLogo());
            initModel.setRobotName(sobotRobot.getRobotName());
            initModel.setRobotHelloWord(sobotRobot.getRobotHelloWord());
            initModel.setAiStatus(sobotRobot.getAiStatus());
            showLogicTitle(initModel.getRobotName(), initModel.getRobotLogo(), false);
            List<ZhiChiMessageBase> datas = messageAdapter.getDatas();
            int count = 0;
            for (int i = datas.size() - 1; i >= 0; i--) {
                if ((ZhiChiConstant.message_sender_type_robot_welcome_msg + "").equals(datas.get(i).getSenderType())
                        || (ZhiChiConstant.message_sender_type_questionRecommend + "").equals(datas.get(i).getSenderType())
                        || (ZhiChiConstant.message_sender_type_robot_guide + "").equals(datas.get(i).getSenderType())) {
                    datas.remove(i);
                    count++;
                    if (count >= 3) {
                        break;
                    }
                }
            }
            messageAdapter.notifyDataSetChanged();
            //切换机器人后调整UI
            remindRobotMessageTimes = 0;
            remindRobotMessage(handler, initModel, info);
        }
    }

    private void applyUIConfig() {
        if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_serviceImgId) {
            btn_set_mode_rengong.setBackgroundResource(SobotUIConfig.sobot_serviceImgId);
        }

        if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_chat_bottom_bgColor) {
            sobot_ll_bottom.setBackgroundColor(getContext().getResources().getColor(SobotUIConfig.sobot_chat_bottom_bgColor));
        }

        if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_apicloud_titleBgColor) {
            relative.setBackgroundColor(SobotUIConfig.sobot_apicloud_titleBgColor);
        }

        if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_titleBgColor) {
            relative.setBackgroundColor(getContext().getResources().getColor(SobotUIConfig.sobot_titleBgColor));
        }

        if (SobotUIConfig.sobot_title_right_menu2_display) {
            sobot_tv_right_second.setVisibility(View.VISIBLE);
            if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_title_right_menu2_bg) {
                Drawable img = getResources().getDrawable(SobotUIConfig.sobot_title_right_menu2_bg);
                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                sobot_tv_right_second.setCompoundDrawables(null, null, img, null);
            }

        }

        if (SobotUIConfig.sobot_title_right_menu3_display) {
            sobot_tv_right_third.setVisibility(View.VISIBLE);
            if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_title_right_menu3_bg) {
                Drawable img = getResources().getDrawable(SobotUIConfig.sobot_title_right_menu3_bg);
                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                sobot_tv_right_third.setCompoundDrawables(null, null, img, null);
            }

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mPanelRoot != null) {
            hidePanelAndKeyboard(mPanelRoot);
        }
    }
}
