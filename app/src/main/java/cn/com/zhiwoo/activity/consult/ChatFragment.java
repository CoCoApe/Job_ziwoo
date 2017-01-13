package cn.com.zhiwoo.activity.consult;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.com.zhiwoo.R;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongNotificationManager;
import io.rong.imkit.fragment.DispatchResultFragment;
import io.rong.imkit.fragment.MessageInputFragment;
import io.rong.imkit.fragment.MessageListFragment;
import io.rong.imkit.model.ConversationInfo;
import io.rong.imkit.widget.InputView;
import io.rong.imlib.CustomServiceConfig;
import io.rong.imlib.ICustomServiceListener;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.CustomServiceMode;
import io.rong.imlib.model.PublicServiceMenu;
import io.rong.message.PublicServiceCommandMessage;


public class ChatFragment  extends DispatchResultFragment {
    MessageListFragment mListFragment;
    MessageInputFragment mInputFragment;
    Conversation.ConversationType mConversationType;
    private String mTargetId;
    ConversationInfo mCurrentConversationInfo;
    private View payButton;
    private View.OnClickListener payListener;
    private InputView.OnInfoButtonClick onInfoButtonClick;
    private boolean robotType = true;
    private int source = 0;
    private boolean resolved = true;
    private boolean committing = false;
    private long enterTime;
    private boolean evaluate = true;
    ICustomServiceListener customServiceListener = new ICustomServiceListener() {
        public void onSuccess(CustomServiceConfig config) {
            if(config.isBlack) {
                String msg = config.msg;
                ChatFragment.this.csWarning(msg, false, true);
            }

        }

        public void onError(int code, String msg) {
            ChatFragment.this.csWarning(msg, false, false);
        }

        public void onModeChanged(CustomServiceMode mode) {
            ChatFragment.this.mInputFragment.setInputProviderType(mode);
            ChatFragment.this.evaluate = true;
            if(mode.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_HUMAN)) {
                ChatFragment.this.robotType = false;
            } else if(mode.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_NO_SERVICE)) {
                ChatFragment.this.evaluate = false;
            }

        }

        public void onQuit(String msg) {
            if(!ChatFragment.this.committing) {
                ChatFragment.this.csWarning(msg, true, false);
            }

        }
    };

    public ChatFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.consult_chat_activity, container, false);
        this.payButton = view.findViewById(R.id.pay_button);
        return view;
    }

    public void onResume() {
        RongNotificationManager.getInstance().onRemoveNotification();
        super.onResume();
    }

    public void setOnInfoButtonClick(InputView.OnInfoButtonClick onInfoButtonClick) {
        this.onInfoButtonClick = onInfoButtonClick;
        if(this.mInputFragment != null) {
            this.mInputFragment.setOnInfoButtonClick(onInfoButtonClick);
        }

    }

    protected void initFragment(Uri uri) {
        if(uri != null) {
            List paths = uri.getPathSegments();
            String typeStr = uri.getLastPathSegment().toUpperCase();
            this.mConversationType = Conversation.ConversationType.valueOf(typeStr);
            this.mTargetId = uri.getQueryParameter("targetId");
            this.mCurrentConversationInfo = ConversationInfo.obtain(this.mConversationType, this.mTargetId);
            RongContext.getInstance().registerConversationInfo(this.mCurrentConversationInfo);
            this.mListFragment = (MessageListFragment)this.getChildFragmentManager().findFragmentById(R.id.chat_messagelist_fragment);
            this.mInputFragment = (MessageInputFragment)this.getChildFragmentManager().findFragmentById(R.id.chat_input_fragment);
            if(this.mListFragment == null) {
                this.mListFragment = new MessageListFragment();
            }

            if(this.mInputFragment == null) {
                this.mInputFragment = new MessageInputFragment();
            }

            if(this.mListFragment != null && (this.mListFragment.getUri() == null || !this.mListFragment.getUri().equals(uri))) {
                this.mListFragment.setUri(uri);
            }

            if(this.mInputFragment != null && (this.mInputFragment.getUri() == null || !this.mInputFragment.getUri().equals(uri))) {
                this.mInputFragment.setUri(uri);
            }

            if(((String)paths.get(1)).toLowerCase().equals("chatroom")) {
                final String targetId = uri.getQueryParameter("targetId");
                if(TextUtils.isEmpty(targetId)) {
                    return;
                }

                this.getHandler().post(new Runnable() {
                    public void run() {
                        int pullCount = ChatFragment.this.getResources().getInteger(io.rong.imkit.R.integer.rc_chatroom_first_pull_message_count);
                        RongIM.getInstance().getRongIMClient().joinChatRoom(targetId, pullCount, new RongIMClient.OperationCallback() {
                            public void onSuccess() {
                            }

                            public void onError(RongIMClient.ErrorCode errorCode) {
                            }
                        });
                    }
                });
            }

            if(this.mConversationType != Conversation.ConversationType.APP_PUBLIC_SERVICE && this.mConversationType != Conversation.ConversationType.PUBLIC_SERVICE) {
                if(this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
                    this.enterTime = System.currentTimeMillis();
                    RongIMClient.getInstance().startCustomService(this.mTargetId, this.customServiceListener);
                    this.mInputFragment.setOnRobotSwitcherListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            RongIMClient.getInstance().switchToHumanMode(ChatFragment.this.mTargetId);
                        }
                    });
                }
            } else {
                RongContext.getInstance().executorBackground(new Runnable() {
                    public void run() {
                        PublicServiceCommandMessage msg = new PublicServiceCommandMessage();
                        msg.setCommand(PublicServiceMenu.PublicServiceMenuItemType.Entry.getMessage());
                        if(RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null) {
//                            RongIM.getInstance().getRongIMClient().sendMessage(ChatFragment.this.mConversationType, ChatFragment.this.mTargetId, msg, (String)null, (String)null, (RongIMClient.SendMessageCallback)null);
                            RongIMClient.getInstance().sendMessage(ChatFragment.this.mConversationType, ChatFragment.this.mTargetId, msg, null, null, null,null);
                        }

                    }
                });
            }

        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mInputFragment = (MessageInputFragment)this.getChildFragmentManager().findFragmentById(R.id.chat_input_fragment);
        if(this.mInputFragment != null) {
            this.mInputFragment.setOnInfoButtonClick(this.onInfoButtonClick);
        }

    }

    public void onDestroyView() {
        RongContext.getInstance().unregisterConversationInfo(this.mCurrentConversationInfo);
        super.onDestroyView();
    }

    public void onDestroy() {
        RongContext.getInstance().getEventBus().unregister(this);
        if(this.mConversationType != null) {
            if(this.mConversationType.equals(Conversation.ConversationType.CHATROOM)) {
                RongContext.getInstance().executorBackground(new Runnable() {
                    public void run() {
                        RongIM.getInstance().getRongIMClient().quitChatRoom(ChatFragment.this.mTargetId, new RongIMClient.OperationCallback() {
                            public void onSuccess() {
                            }

                            public void onError(RongIMClient.ErrorCode errorCode) {
                            }
                        });
                    }
                });
            }

            if(this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
                RongIMClient.getInstance().stopCustomService(this.mTargetId);
            }
        }

        super.onDestroy();
    }

    public boolean onBackPressed() {
        return this.submitComment(true);
    }

    public boolean handleMessage(Message msg) {
        return false;
    }

    private void csWarning(String msg, final boolean evaluate, final boolean suspend) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(io.rong.imkit.R.layout.rc_cs_alert_warning);
        TextView tv = (TextView)window.findViewById(io.rong.imkit.R.id.rc_cs_msg);
        tv.setText(msg);
        window.findViewById(io.rong.imkit.R.id.rc_btn_ok).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alertDialog.dismiss();
                if(evaluate) {
                    ChatFragment.this.submitComment(suspend);
                } else {
                    FragmentManager fm = ChatFragment.this.getChildFragmentManager();
                    if(fm.getBackStackEntryCount() > 0) {
                        fm.popBackStack();
                    } else {
                        ChatFragment.this.getActivity().finish();
                    }
                }

            }
        });
    }

    private boolean submitComment(boolean suspend) {
        if(!this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
            return false;
        } else if(!this.evaluate) {
            return false;
        } else {
            long currentTime = System.currentTimeMillis();
            int interval = 60;

            try {
                interval = RongContext.getInstance().getResources().getInteger(io.rong.imkit.R.integer.rc_custom_service_evaluation_interval);
            } catch (Resources.NotFoundException var11) {
                var11.printStackTrace();
            }

            if(currentTime - this.enterTime < (long)(interval * 1000)) {
                return false;
            } else {
                this.committing = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
                builder.setCancelable(false);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                Window window = alertDialog.getWindow();
                final LinearLayout linearLayout;
                int i;
                View child;
                if(this.robotType) {
                    window.setContentView(io.rong.imkit.R.layout.rc_cs_alert_robot_evaluation);
                    linearLayout = (LinearLayout)window.findViewById(io.rong.imkit.R.id.rc_cs_yes_no);
                    if(this.resolved) {
                        linearLayout.getChildAt(0).setSelected(true);
                        linearLayout.getChildAt(1).setSelected(false);
                    } else {
                        linearLayout.getChildAt(0).setSelected(false);
                        linearLayout.getChildAt(1).setSelected(true);
                    }

                    for(i = 0; i < linearLayout.getChildCount(); ++i) {
                        child = linearLayout.getChildAt(i);
                        child.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                v.setSelected(true);
                                int index = linearLayout.indexOfChild(v);
                                if(index == 0) {
                                    linearLayout.getChildAt(1).setSelected(false);
                                    ChatFragment.this.resolved = true;
                                } else {
                                    ChatFragment.this.resolved = false;
                                    linearLayout.getChildAt(0).setSelected(false);
                                }

                            }
                        });
                    }
                } else {
                    window.setContentView(io.rong.imkit.R.layout.rc_cs_alert_human_evaluation);
                    linearLayout = (LinearLayout)window.findViewById(io.rong.imkit.R.id.rc_cs_stars);

                    for(i = 0; i < linearLayout.getChildCount(); ++i) {
                        child = linearLayout.getChildAt(i);
                        if(i < this.source) {
                            child.setSelected(true);
                        }

                        child.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                int index = linearLayout.indexOfChild(v);
                                int count = linearLayout.getChildCount();
                                ChatFragment.this.source = index + 1;
                                if(!v.isSelected()) {
                                    while(index >= 0) {
                                        linearLayout.getChildAt(index).setSelected(true);
                                        --index;
                                    }
                                } else {
                                    ++index;

                                    while(index < count) {
                                        linearLayout.getChildAt(index).setSelected(false);
                                        ++index;
                                    }
                                }

                            }
                        });
                    }
                }

                window.findViewById(io.rong.imkit.R.id.rc_btn_cancel).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        ChatFragment.this.committing = false;
                        alertDialog.dismiss();
                        FragmentManager fm = ChatFragment.this.getChildFragmentManager();
                        if(fm.getBackStackEntryCount() > 0) {
                            fm.popBackStack();
                        } else {
                            ChatFragment.this.getActivity().finish();
                        }

                    }
                });
                window.findViewById(io.rong.imkit.R.id.rc_btn_ok).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if(ChatFragment.this.robotType) {
                            RongIMClient.getInstance().evaluateCustomService(ChatFragment.this.mTargetId, ChatFragment.this.resolved);
                        } else if(ChatFragment.this.source > 0) {
                            RongIMClient.getInstance().evaluateCustomService(ChatFragment.this.mTargetId, ChatFragment.this.source, null);
                        }

                        alertDialog.dismiss();
                        ChatFragment.this.committing = false;
                        FragmentManager fm = ChatFragment.this.getChildFragmentManager();
                        if(fm.getBackStackEntryCount() > 0) {
                            fm.popBackStack();
                        } else {
                            ChatFragment.this.getActivity().finish();
                        }

                    }
                });
                return true;
            }
        }
    }

    public void setPayListener(View.OnClickListener payListener) {
        this.payListener = payListener;
        payButton.setOnClickListener(payListener);
    }
}
