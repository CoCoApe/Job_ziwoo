package cn.com.zhiwoo.activity.home;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.base.BaseActivity;
import cn.com.zhiwoo.bean.tutor.User;
import cn.com.zhiwoo.tool.ChatTool;
import cn.com.zhiwoo.utils.LogUtils;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;


public class APPServiceActivity extends BaseActivity {
    private String mTargetId;
    private User user;
    @Override
    public void initView() {
        super.initView();
        LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.home_appservice_activity,null);
        flContent.addView(linearLayout);
        getIntentInfo(getIntent());
        rightImageView.setVisibility(View.GONE);
    }

    @Override
    public void initData() {
        titleView.setText(user.getNickName());
    }

    @Override
    public void initListener() {

    }

    @Override
    public void process(View v) {

    }
    private void getIntentInfo(Intent intent) {
        user = (User) intent.getSerializableExtra("user");
        if (user != null) {
            mTargetId = user.getId();
            enterFragment(mTargetId);
        }
    }
    private void enterFragment(String mTargetId)  {
        ConversationFragment conversationFragment = (ConversationFragment) getSupportFragmentManager().findFragmentById(R.id.chatconversation_fragment);
        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath("private")
                .appendQueryParameter("targetId", mTargetId)
                .build();
        conversationFragment.setUri(uri);
        if (!ChatTool.sharedTool().isNewChat(mTargetId)) {
            return;
        }
        RongIM.getInstance().getRongIMClient().insertMessage(Conversation.ConversationType.PRIVATE,
                user.getId(),user.getId(),
                TextMessage.obtain("欢迎使用咨我,我是咨我在线客服,请问有什么可以帮您的呢?"),
                new RongIMClient.ResultCallback<Message>() {
            @Override
            public void onSuccess(Message message) {
                LogUtils.log(getBaseContext(), "消息发送成功");
            }
            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LogUtils.log(getBaseContext(), "消息发送失败");
            }
        });
        ChatTool.sharedTool().targetToRead(mTargetId,user.getNickName(),user.getHeadImageUrl());
    }

    @Override
    protected void onDestroy() {
        ChatTool.sharedTool().targetToRead(mTargetId,user.getNickName(),user.getHeadImageUrl());
        super.onDestroy();
    }
}
