package cn.com.zhiwoo.activity.consult;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.base.BaseActivity;
import cn.com.zhiwoo.bean.main.Account;
import cn.com.zhiwoo.bean.react.Commend;
import cn.com.zhiwoo.bean.tutor.User;
import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.tool.ChatTool;
import cn.com.zhiwoo.tool.PayTool;
import cn.com.zhiwoo.utils.Api;
import cn.com.zhiwoo.utils.LogUtils;
import cn.com.zhiwoo.view.consult.PayTipSelectDialog;
import io.rong.imkit.RongIM;
import io.rong.imlib.MessageTag;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.TypingMessage.TypingStatus;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import okhttp3.Call;
import okhttp3.Response;

import static io.rong.imlib.model.Conversation.ConversationType;


public class ConsultChatActivity extends BaseActivity{
    private String mTargetId;
    private User user;
    private MyBroadCastReceiver broadcastReceiver;
    private static final int SET_TARGETID_TITLE = 3;
    private static final int SET_TEXT_TYPING_TITLE = 1;
    private static final int SET_VOICE_TYPING_TITLE = 2;
    private static final int SHOW_MOUDLE = 4;
    private Account account;
    private String expect;
    private int send;
    private boolean sendOrBack;
    private Map<String,String> params = new HashMap<>();
    private Handler resHandler = new Handler();
    private  Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sendNoRespond();
        }
    };


    private Handler mHandler = new Handler(new Handler.Callback()
    {
        public boolean handleMessage(android.os.Message paramMessage)
        {
            switch (paramMessage.what)
            {
                case SET_TEXT_TYPING_TITLE: {
                    ConsultChatActivity.this.titleView.setText("对方正在输入...");
                    ConsultChatActivity.this.titleImage.setVisibility(View.GONE);
                }
                break;
                case SET_VOICE_TYPING_TITLE: {
                    ConsultChatActivity.this.titleView.setText("对方正在讲话...");
                    ConsultChatActivity.this.titleImage.setVisibility(View.GONE);
                }
                break;
                case SET_TARGETID_TITLE: {
                    ConsultChatActivity.this.titleView.setText(ConsultChatActivity.this.user.getNickName());
                    ConsultChatActivity.this.titleImage.setVisibility(View.GONE);
                }
                break;
                case SHOW_MOUDLE: {
                    ConsultChatActivity.this.showMoudle();
                }
                break;
                default:
                    break;
            }
            return false;
        }
    });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RongIMClient.setTypingStatusListener(new RongIMClient.TypingStatusListener() {
            public void onTypingStatusChanged(ConversationType type, String targetId, Collection<TypingStatus> typingStatusSet) {
                //当输入状态的会话类型和targetID与当前会话一致时，才需要显示
                if (type.equals(Conversation.ConversationType.PRIVATE) && user.getId().equals(targetId)) {
                    //count表示当前会话中正在输入的用户数量，目前只支持单聊，所以判断大于0就可以给予显示了
                    int count = typingStatusSet.size();
                    if (count > 0) {
                        Iterator iterator = typingStatusSet.iterator();
                        TypingStatus status = (TypingStatus) iterator.next();
                        String objectName = status.getTypingContentType();
                        MessageTag textTag = TextMessage.class.getAnnotation(MessageTag.class);
                        MessageTag voiceTag = VoiceMessage.class.getAnnotation(MessageTag.class);
                        //匹配对方正在输入的是文本消息还是语音消息
                        if (objectName.equals(textTag.value())) {
                            //显示“对方正在输入”
                            mHandler.sendEmptyMessage(SET_TEXT_TYPING_TITLE);
                        } else if (objectName.equals(voiceTag.value())) {
                            //显示"对方正在讲话"
                            mHandler.sendEmptyMessage(SET_VOICE_TYPING_TITLE);
                        }
                    } else {
                        //当前会话没有用户正在输入，标题栏仍显示原来标题
                        mHandler.sendEmptyMessage(SET_TARGETID_TITLE);
                    }
                }

            }
        });
        RongIM.setConversationBehaviorListener(ChatTool.sharedTool().getMessageListener());
        RongIM.getInstance().setSendMessageListener(new RongIM.OnSendMessageListener() {
            @Override
            public Message onSend(Message message) {
                sendOrBack = false;
                sendAutoResponed(message.getTargetId());
                return message;
            }

            @Override
            public boolean onSent(Message message, RongIM.SentMessageErrorCode sentMessageErrorCode) {
                return false;
            }
        });
        RongIM.getInstance().getRongIMClient().setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
            @Override
            public boolean onReceived(Message message, int i) {
                sendOrBack = true;
                sendAutoResponed(message.getTargetId());
                resHandler.removeCallbacks(runnable);
                return false;
            }
        });

    }

    @Override
    public void initView() {
        super.initView();
        RelativeLayout relativeLayout = (RelativeLayout) View.inflate(this, R.layout.consult_chat_activity1,null);
        flContent.addView(relativeLayout);
        getIntentInfo(getIntent());
        rightImageView.setImageResource(R.drawable.nav_write);
        rightImageView.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    @Override
    public void initData() {
        titleView.setText(user.getNickName());
        // 动态注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("ask_tip");
        broadcastReceiver = new MyBroadCastReceiver();
        registerReceiver(broadcastReceiver, filter);
        account = AccountTool.getCurrentAccount(this);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void process(View v) {
    }
    private void paytip() {
        PayTipSelectDialog.showDialog(this, new PayTipSelectDialog.OnChosenPayAmount() {
            @Override
            public void didChosenPayAmount(final float amount) {
                PayTool payTool = new PayTool(ConsultChatActivity.this, new PayTool.OnPayResultListener() {
                    @Override
                    public void paySuccess() {
                        LogUtils.log("打赏成功");
                        Toast.makeText(getBaseContext(),"打赏成功",Toast.LENGTH_SHORT).show();
                        String message = AccountTool.getCurrentAccount(getBaseContext()).getNickName() + " 打赏了 " + user.getNickName() + " " + (int) amount + "块钱";
                        RongIM.getInstance().getRongIMClient().sendMessage(ConversationType.PRIVATE, mTargetId, new InformationNotificationMessage(message), null, null, null, null);
                    }

                    @Override
                    public void payFailure() {
                        LogUtils.log("打赏失败");
                        Toast.makeText(getBaseContext(),"打赏失败",Toast.LENGTH_SHORT).show();
                    }
                });
                //[支付]
                PayTool.Product product = new PayTool.Product("打赏导师", amount);
//                PayTool.Product product = new PayTool.Product("打赏导师", 0.01f);
                payTool.pay(product);
            }
        });
    }
    private void askTip() {
        InformationNotificationMessage message = new InformationNotificationMessage("可以为导师的回答进行打赏吗?");
        message.setExtra("ZWConsultBegToPayTipExtra");
        RongIM.getInstance().getRongIMClient().sendMessage(ConversationType.PRIVATE, mTargetId, message, null, null, null, null);
    }

    @Override
    public void titleBarRightBtnClick() {
        showMoudle();
    }

    private void getIntentInfo(Intent intent) {
        user = (User) intent.getSerializableExtra("user");
        if (user != null) {
            mTargetId = user.getId();
            enterFragment(mTargetId);
        }
    }
    private void enterFragment(String mTargetId)  {
        ChatFragment chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentById(R.id.chat_fragment);
        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath("private")
                .appendQueryParameter("targetId", mTargetId)
                .build();
        chatFragment.setUri(uri);
        chatFragment.setPayListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountTool.getCurrentAccount(getBaseContext()).isTour()) {
                    //请求打赏
                    LogUtils.log("请求打赏");
                    askTip();
                } else {
                    LogUtils.log("主动打赏");
                    paytip();
                }
            }
        });
        if (!ChatTool.sharedTool().isNewChat(mTargetId) || AccountTool.getCurrentAccount(getBaseContext()).isTour()) {
            return;
        }
        sendGreeter();//进入聊天界面的消息提示
        mHandler.sendEmptyMessageDelayed(SHOW_MOUDLE,500);
        ChatTool.sharedTool().targetToRead(mTargetId, user.getNickName(), user.getHeadImageUrl());
    }

//    为了方便分析，建议按以下格式
//    【你的诉求】
//            【你的情况】
//            【对方情况】
//            【相处经历】
    private void sendGreeter()
    {
        RongIM.getInstance().getRongIMClient().insertMessage(Conversation.ConversationType.PRIVATE,
                this.user.getId(), this.user.getId(), TextMessage.obtain("您好,如您是第一次咨询，请点击右上角编辑您的问题,方便导师分析,如导师未能及时回复,我们也会根据您的问题推荐相关文章预览以等待导师接入!"),
                new RongIMClient.ResultCallback<Message>() {
                    @Override
                    public void onSuccess(Message message) {
                        LogUtils.log(ConsultChatActivity.this.getBaseContext(), "消息发送成功");
                    }
                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        LogUtils.log(ConsultChatActivity.this.getBaseContext(), "消息发送失败");
                    }
                });
    }

    private void sendNoRespond(){
        final Random random = new Random();
        final String demand_infos= AccountTool.getCurrentAccount(this).getDemand_infos();
        OkGo.get(Api.TEXT_IMAGE)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        RichContentMessage richContentMessage ;
                        Commend commend = new Gson().fromJson(s,Commend.class);
                        List<Commend.DataBean.RedeemBean> default_list = commend.getData().getRedeem();
                        Commend.DataBean.RedeemBean default_bean = default_list.get(random.nextInt(default_list.size()));
                        richContentMessage = RichContentMessage.obtain(default_bean.getTitle(),default_bean.getContent(),default_bean.getPic_url(),default_bean.getContent_url());
                        if (!TextUtils.isEmpty(expect) || !TextUtils.isEmpty(demand_infos)){
                            if("关系推进".equals(expect) || "自我提升".equals(demand_infos)){
                                List<Commend.DataBean.BoostBean> list = commend.getData().getBoost();
                                Commend.DataBean.BoostBean bean = list.get(random.nextInt(list.size()));
                                richContentMessage = RichContentMessage.obtain(bean.getTitle(),bean.getContent(),bean.getPic_url(),bean.getContent_url());
                            }else if("婚姻维系".equals(expect) || "长期关系".equals(demand_infos)){
                                List<Commend.DataBean.KeepBean> list = commend.getData().getKeep();
                                Commend.DataBean.KeepBean bean = list.get(random.nextInt(list.size()));
                                richContentMessage = RichContentMessage.obtain(bean.getTitle(),bean.getContent(),bean.getPic_url(),bean.getContent_url());
                            }else if("摆脱单身".equals(expect) || "提升情商".equals(demand_infos)){
                                List<Commend.DataBean.AwayBean> list = commend.getData().getAway();
                                Commend.DataBean.AwayBean bean = list.get(random.nextInt(list.size()));
                                richContentMessage = RichContentMessage.obtain(bean.getTitle(),bean.getContent(),bean.getPic_url(),bean.getContent_url());
                            }
                        }
                        RongIM.getInstance().getRongIMClient().sendMessage(ConversationType.PRIVATE, mTargetId, new InformationNotificationMessage("咨询师好像正在忙喔，相关内容预览戳下面!"), null, null, null, null);
                        sendRichMessage(richContentMessage);
                    }
                });
    }

/**
 *  图文消息
 *
 */
    private void sendRichMessage(RichContentMessage message){
        RongIM.getInstance().getRongIMClient().insertMessage(Conversation.ConversationType.PRIVATE,
                mTargetId, user.getId(),message,
                new RongIMClient.ResultCallback<Message>() {
                    @Override
                    public void onSuccess(Message message) {

                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
    }

    /**
     * 提问模板
     */
    private void showMoudle()
    {
        ConsultMoudleDialog.showDialog(this, new ConsultMoudleDialog.OnMoudleClickSend()
        {
            public void moudleClickSend(String paramString1, String paramString2, String paramString3, String paramString4)
            {
                expect = paramString1;
                String str = "咨询师你好！";
                if (!TextUtils.isEmpty(paramString1))
                    str = str + "\n\n我的问题：" + paramString1;
                if (!TextUtils.isEmpty(paramString2))
                    str = str + "\n\n我的情况：" + paramString2;
                if (!TextUtils.isEmpty(paramString3))
                    str = str + "\n\n对方情况：" + paramString3;
                if (!TextUtils.isEmpty(paramString4))
                    str = str + "\n\n相处经历：" + paramString4;
                RongIM.getInstance().getRongIMClient().sendMessage(Conversation.ConversationType.PRIVATE, ConsultChatActivity.this.user.getId(), TextMessage.obtain(str), str, null, null, null);
                if (send <=0 ){
                    resHandler.postDelayed(runnable,60*1000);
                    send++;
                }
            }
        });

    }
    @Override
    protected void onDestroy() {
        ChatTool.sharedTool().targetToRead(mTargetId,user.getNickName(),user.getHeadImageUrl());
        unregisterReceiver(broadcastReceiver);
        LogUtils.log(getBaseContext(), "聊天界面销毁!");
        ChatTool.sharedTool().targetToRead(mTargetId, user.getNickName(), user.getHeadImageUrl());
        super.onDestroy();
    }
    private class MyBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            paytip();
        }
    }


    private void sendAutoResponed(String targetid){
        params.clear();
        if (sendOrBack){
            params.put("access_token",account.getAccessToken());
            params.put("sender_id",account.getId());
            params.put("target_id",targetid);
            params.put("is_user","1");
        }else {
            params.put("access_token",account.getAccessToken());
            params.put("sender_id",targetid);
            params.put("target_id",account.getId());
            params.put("is_user","0");
        }
        OkGo.post(Api.AUTORESPONED)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {

                    }
                });
    }
}
