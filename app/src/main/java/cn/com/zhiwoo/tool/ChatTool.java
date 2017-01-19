package cn.com.zhiwoo.tool;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.base.PhotoActivity;
import cn.com.zhiwoo.activity.main.LoginActivity;
import cn.com.zhiwoo.activity.main.MainActivity;
import cn.com.zhiwoo.activity.main.NotificationActivity;
import cn.com.zhiwoo.activity.react.ArticleDetailActivity;
import cn.com.zhiwoo.bean.home.Message;
import cn.com.zhiwoo.bean.main.Account;
import cn.com.zhiwoo.bean.react.ReactArticle;
import cn.com.zhiwoo.bean.tutor.User;
import cn.com.zhiwoo.utils.APPStatusUtils;
import cn.com.zhiwoo.utils.Api;
import cn.com.zhiwoo.utils.LogUtils;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongIMClientWrapper;
import io.rong.imkit.widget.provider.CameraInputProvider;
import io.rong.imkit.widget.provider.ImageInputProvider;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.location.message.RealTimeLocationStartMessage;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.ReadReceiptMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import io.rong.notification.PushNotificationMessage;
import okhttp3.Call;
import okhttp3.Response;


// 读取全部消息和未读消息的时候,要减少数据库的读取操作.在数据库数据发生改变的时候,更新一下成员变量的内容即可
public class ChatTool {
    private Context mContext;
    private HashMap<String,UserInfo> userInfos = new HashMap<>();
    private ArrayList<String> downloadingIds = new ArrayList<>();
    private static ChatTool instance;
    private MessageDataTool messageDataTool;

    private Handler connectStatusHandler = new Handler(new Handler.Callback()
    {
        public boolean handleMessage(android.os.Message paramMessage)
        {
            Toast.makeText(ChatTool.this.mContext, "您的账号已在其它设备登录!", Toast.LENGTH_SHORT).show();
            AccountTool.unregistCurrentAccount(ChatTool.this.mContext);
            new Handler().postDelayed(new Runnable()
            {
                public void run()
                {
                    Intent localIntent = new Intent(ChatTool.this.mContext, MainActivity.class);
                    ChatTool.this.mContext.startActivity(localIntent);
                }
            }, 500L);
            return false;
        }
    });

    public ChatTool() {

    }

    public ArrayList<Message> getTipMessages() {
        //在没有登录的时候,返回空数组
        if (!AccountTool.isLogined(mContext)) {
            return new ArrayList<>();
        }
        if (null != messageDataTool){
            return (ArrayList<Message>) messageDataTool.unreadMessageList();
        }
        return new ArrayList<>();
    }

    private ArrayList<Message> allMessages = new ArrayList<>();
    public ArrayList<Message> getAllMessages() {
        //在没有登录的时候,返回空数组
        if (!AccountTool.isLogined(mContext)) {
            return new ArrayList<>();
        }
        if (null != messageDataTool)
        allMessages = (ArrayList<Message>) messageDataTool.allMessageList();
        return allMessages;
    }
    public void targetToRead(String userId,String nickName,String icon) {
        int result = messageDataTool.tagToRead(userId,nickName,icon);
        if (result > 0) {
            LogUtils.log("标记为已读成功, ID : " + userId);
            Intent intent = new Intent("message_change");
            mContext.sendBroadcast(intent);
        }
    }
    public boolean isNewChat(String targetId) {
        return messageDataTool.isNewChat(targetId);
    }

    /**
     * 配置聊天工具,在APP启动时调用或者账号登录成功的时候调用
     */
    public static void config(Context context) {
        if (instance == null) {
            instance = new ChatTool(context);
        }
    }

    static void destoyChatTool() {
        LogUtils.log("销毁聊天工具");
        instance = null;
    }

    public static ChatTool sharedTool() {
        if (null == instance){
            return new ChatTool();
        }
        return instance;
    }

    private ChatTool(Context context) {
        if (context != null) {
            this.mContext = context;
        }
        //messagedatatool应该在每次登录成功的时候创建
        messageDataTool = new MessageDataTool(mContext);
    }
    public void login() {
        if (!AccountTool.isLogined(mContext)) {
            return;
        }
        Account account = AccountTool.getCurrentAccount(mContext);
        final String token =  account.getAccessToken();
        String userId = account.getId();
        LogUtils.log(mContext, "开始登录");
        HashMap<String,String> params = new HashMap<>();
        params.put("access_token", token);
        params.put("user_id", userId);
        OkGo.get(Api.FOR_TOKEN)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        String rmToken = null;
                        try {
                            JSONObject object = new JSONObject(s);
                            rmToken = object.getString("token");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (rmToken == null) {
                            return;
                        }
                        //配置好友信息提供者
                        RongIM.setUserInfoProvider(new ChatUserInfoProvider(), true);
                        RongIM.connect(rmToken, new RongIMClient.ConnectCallback() {
                            @Override
                            public void onTokenIncorrect() {
                                LogUtils.log("融云token无效");
                            }
                            @Override
                            public void onSuccess(String s) {
                                LogUtils.log("融云登录成功!");
                                if (RongIM.getInstance() != null) {
                                    //配置连接状态监听
                                    if (RongIM.getInstance().getRongIMClient() != null) {
                                        LogUtils.log(mContext, "设置融云监听");
                                        RongIMClientWrapper.setOnReceiveMessageListener(new MyReceiveMessageListener());
                                        RongIMClientWrapper.setConnectionStatusListener(new MyConnectionStatusListener());
                                        InputProvider.ExtendProvider[] provider = {
                                                new ImageInputProvider(RongContext.getInstance()),//图片
                                                new CameraInputProvider(RongContext.getInstance()),//相机
                                        };
                                        RongIM.resetInputExtensionProvider(Conversation.ConversationType.PRIVATE, provider);
                                        RongIM.setConversationBehaviorListener(new ClickMessageListener());

                                    }
                                }
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                                LogUtils.log("融云登录出错");
                                Toast.makeText(mContext,"登录出错",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        LogUtils.log("获取融云token出错： " + response.body());
                        String result = response.body().toString();
                        JsonParser parser = new JsonParser();
                        JsonObject jsonObj = parser.parse(result.replace(" ","")).getAsJsonObject();
                        if (jsonObj.get("error_code").getAsInt() == 1) {
                            // token 失效
                            AccountTool.unregistCurrentAccount(mContext);
                            Toast.makeText(mContext,"您的账号在其他设备登录过，请重新登录",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            mContext.startActivity(intent);
                        }
                    }
                });

    }

    private class ChatUserInfoProvider implements RongIM.UserInfoProvider {

        @Override
        public UserInfo getUserInfo(final String s) {
            if (s==null) {
                return null;
            }
            UserInfo userInfo = userInfos.get(s);
            if (userInfo != null) {
                return userInfo;
            } else if (!downloadingIds.contains(s)){
                GetUserInfoFromNet(s);
                downloadingIds.add(s);
            }
            return null;
        }
        private void GetUserInfoFromNet(final String s) {
            HashMap<String,String> params = new HashMap<>();
            params.put("access_token","Z:0:!:5");
            params.put("friend_id",s);
            OkGo.get(Api.GET_USERINFO)
                    .params(params)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            if (s != null) {
                                if (!(userInfos.containsKey(s))) {
                                    //把新的userinfo放进去
                                    Gson gson = new Gson();
                                    User user = gson.fromJson(s, User.class);
                                    UserInfo userInfo1 = new UserInfo(user.getId(),user.getNickName(), Uri.parse(user.getHeadImageUrl()));
                                    userInfos.put(s,userInfo1);
                                }
                            }
                        }
                    });
        }

    }
    public void getUserInfo(final String s, final loadUserInfoCallBack callBack) {
        LogUtils.log("tour: " + s);
        if (userInfos.containsKey(s)) {
            UserInfo userInfo = userInfos.get(s);
            if (userInfo != null && callBack != null) {
                callBack.onsuccess(userInfo);
                return;
            }
        }
        HashMap<String,String> params = new HashMap<>();
        params.put("access_token","Z:0:!:5");
        params.put("friend_id",s);
        OkGo.get(Api.GET_USERINFO)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (s != null) {
                            if (!(userInfos.containsKey(s))) {
                                //把新的userinfo放进去
                                Gson gson = new Gson();
                                User user = gson.fromJson(s, User.class);
                                UserInfo userInfo1 = new UserInfo(user.getId(), user.getNickName(), Uri.parse(user.getHeadImageUrl()));
                                userInfos.put(s,userInfo1);
                                if (callBack != null) {
                                    callBack.onsuccess(userInfo1);
                                }
                            }
                        }
                    }
                });
    }
    public interface loadUserInfoCallBack {
        void onsuccess(UserInfo userInfo);
    }

    private static class MessageDataTool {

        // 数据库名称
        private static String DB_NAME = "message.db";
        // 数据库版本
        private static int DB_VERSION = 1;
        private SQLiteDatabase db;
        private SqliteHelper dbHelper;

        MessageDataTool(Context context) {
            if (!AccountTool.isLogined(context)) {
                return;
            }
            dbHelper = new SqliteHelper(context, AccountTool.getCurrentAccount(context).getId() + DB_NAME, null, DB_VERSION );
            db = dbHelper.getWritableDatabase();
//            方便测试的时候,删除所有的数据
//            db.delete(SqliteHelper.TB_NAME,null,null);
        }

        //所有消息列表
        List<Message> allMessageList() {
            List<Message> allMessageList = new ArrayList<>();
            Cursor cursor = db.query(SqliteHelper.TB_NAME, null, null, null, null,
                    null, Message.LAST_TIEM + " DESC");
            LogUtils.log("全部消息条目 : " + cursor.getCount());
            cursor.moveToFirst();
            while (!cursor.isAfterLast() && (cursor.getString(0) != null )) {
                Message message = new Message();
                message.setUserId(cursor.getString(0));
                message.setUserName(cursor.getString(1));
                message.setUserIcon(cursor.getString(2));
                message.setUnreadCount(cursor.getInt(3));
                message.setLastTime(cursor.getLong(4));
                allMessageList.add(message);
                cursor.moveToNext();
            }
            cursor.close();
            return allMessageList;
        }
        //未读消息列表
        List<Message> unreadMessageList() {
            List<Message> unreadMessageList = new ArrayList<>();
            Cursor cursor = db.query(SqliteHelper.TB_NAME, null, Message.UNREAD_COUNT + " > 0" , null, null,
                    null, Message.LAST_TIEM + " DESC");
            int count = cursor.getCount();
            LogUtils.log("未读条目数: " + count);
            cursor.moveToFirst();
            while (!cursor.isAfterLast() && (cursor.getString(0) != null )) {
                Message message = new Message();
                message.setUserId(cursor.getString(0));
                LogUtils.log("查询到 userid : " + cursor.getString(0));
                message.setUserName(cursor.getString(1));
                message.setUserIcon(cursor.getString(2));
                message.setUnreadCount(cursor.getInt(3));
                message.setLastTime(cursor.getLong(4));
                unreadMessageList.add(message);
                cursor.moveToNext();
            }
            cursor.close();
            return unreadMessageList;
        }

        // 保存信息
        Long saveMessage(Message message) {
            Boolean b;
            Cursor cursor = db.query(SqliteHelper.TB_NAME, null, Message.USER_ID
                    + " = " + message.getUserId(), null, null, null, null );
            b = cursor.moveToFirst();
            Long uid;
            if (b) {
                LogUtils.log("找到了userid : " + message.getUserId());
                int unreadCount = cursor.getInt(3);
                ContentValues values = new ContentValues();
                values.put(Message.UNREAD_COUNT,unreadCount+1);
                values.put(Message.LAST_TIEM,System.currentTimeMillis());
                LogUtils.log("更新的 userid : " + message.getUserId());
                uid = (long)db.update(SqliteHelper.TB_NAME,values, Message.USER_ID + " = " + message.getUserId(),null);
            } else {
                LogUtils.log("没有找到 userid : " + message.getUserId());
                ContentValues values = new ContentValues();
                values.put(Message.USER_ID, message.getUserId());
                LogUtils.log("插入的 userid : " + message.getUserId());
                values.put(Message.USER_NAME, message.getUserName());
                values.put(Message.USER_ICON, message.getUserIcon());
                values.put(Message.UNREAD_COUNT, message.getUnreadCount());
                values.put(Message.LAST_TIEM, message.getLastTime());
                uid = db.insert(SqliteHelper.TB_NAME,null, values);
            }
            cursor.close();
            return uid;
        }
        boolean isNewChat(String targetId) {
            Cursor cursor = db.query(SqliteHelper.TB_NAME, null, Message.USER_ID + " = " + targetId , null, null,
                    null, null);
            boolean b = cursor.moveToFirst();
            if (!b) {
                cursor.close();
                LogUtils.log("没有找到会话 id = " + targetId + ", 所以是新聊天");
                return true;
            }
            long time = cursor.getLong(cursor.getColumnIndex(Message.LAST_TIEM));
            long now = System.currentTimeMillis();
            LogUtils.log("time : " + time + ", now : " + now + "now - time = " + (now - time));
            cursor.close();
            if ((now - time) < (10 * 60 * 1000)) {
                LogUtils.log("不是新聊天");
                return false;
            }
            LogUtils.log("是新聊天");
            return true;
        }

        // 标记为已读
        int tagToRead(String userId, String nickName, String icon) {
            Boolean b;
            Cursor cursor = db.query(SqliteHelper.TB_NAME, null, Message.USER_ID
                    + " = " + userId, null, null, null, null );
            b = cursor.moveToFirst();
            Long uid;
            if (b) {
                LogUtils.log("找到了userid : " + userId);
                ContentValues values = new ContentValues();
                values.put(Message.UNREAD_COUNT,0);
                values.put(Message.LAST_TIEM, System.currentTimeMillis());
                LogUtils.log("标记为已读的 userid : " + userId);
                uid = (long)db.update(SqliteHelper.TB_NAME,values, Message.USER_ID + " = " + userId,null);
            } else {
                LogUtils.log("没有找到 userid : " + userId);
                ContentValues values = new ContentValues();
                values.put(Message.USER_ID, userId);
                LogUtils.log("标记为已读的 userid : " + userId);
                values.put(Message.USER_NAME, nickName);
                values.put(Message.USER_ICON, icon);
                values.put(Message.UNREAD_COUNT, 0);
                values.put(Message.LAST_TIEM, System.currentTimeMillis());
                uid = db.insert(SqliteHelper.TB_NAME,null, values);
            }
            cursor.close();
            return uid.intValue();
        }

        private class SqliteHelper extends SQLiteOpenHelper {
            //用来保存UserID、Access Token、Access Secret的表名
            static final String TB_NAME = "messages";
            SqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
                super(context, name, factory, version);
            }

            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE IF NOT EXISTS "+ TB_NAME + "(" + Message.USER_ID + " varchar primary key," + Message.USER_NAME + " varchar," + Message.USER_ICON + " varchar," + Message.UNREAD_COUNT + " integer," + Message.LAST_TIEM + " integer)");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL( "DROP TABLE IF EXISTS " + TB_NAME );
                onCreate(db);
            }
            //更新列
            public void updateColumn(SQLiteDatabase db, String oldColumn, String newColumn, String typeColumn){
                try{
                    db.execSQL( "ALTER TABLE " + TB_NAME + " CHANGE " + oldColumn + " "+ newColumn + " " + typeColumn);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 接收消息监听器
     */
    private class MyReceiveMessageListener implements RongIMClient.OnReceiveMessageListener {
        @Override
        public boolean onReceived(final io.rong.imlib.model.Message message, int i) {
            if ((message.getMessageDirection() != io.rong.imlib.model.Message.MessageDirection.RECEIVE) || ((message.getContent() instanceof ReadReceiptMessage)))
            {
                LogUtils.log("收到消息,被拦截下来了");
                return false;
            }
            final String id = message.getSenderUserId();
            LogUtils.log("sender user id = " + id);

            //把消息保存起来.如果网速好，可以拉取到用户的头像最好；如果没有拉取到，不显示头像、昵称，但要将该条消息保存起来

            HashMap<String,String> params = new HashMap<>();
            params.put("access_token","Z:0:!:5");
            params.put("friend_id",id);
            OkGo.get(Api.GET_USERINFO)
                    .params(params)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            if (s != null) {
                                //把新的userinfo放进去
                                Gson gson = new Gson();
                                User user = gson.fromJson(s, User.class);
                                UserInfo userInfo1 = new UserInfo(user.getId(),user.getNickName(), Uri.parse(user.getHeadImageUrl()));
                                Message message1 = new Message(userInfo1.getUserId(), userInfo1.getPortraitUri().toString(), userInfo1.getName(), 1, System.currentTimeMillis());
                                long uid = messageDataTool.saveMessage(message1);
                                if (uid > 0) {
                                    LogUtils.log("保存消息成功!");
                                    Intent intent = new Intent("message_change");
                                    mContext.sendBroadcast(intent);
                                } else {
                                    LogUtils.log("保存消息失败!");
                                }
                            } else {
                                Message message1 = new Message(id, "", "", 1, System.currentTimeMillis());
                                long uid = messageDataTool.saveMessage(message1);
                                if (uid > 0) {
                                    LogUtils.log("保存消息成功!");
                                    Intent intent = new Intent("message_change");
                                    mContext.sendBroadcast(intent);
                                } else {
                                    LogUtils.log("保存消息失败!");
                                }
                            }
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            Message message1 = new Message(id, "", "", 1, System.currentTimeMillis());
                            long uid = messageDataTool.saveMessage(message1);
                            if (uid > 0) {
                                LogUtils.log("保存消息成功!");
                                Intent intent = new Intent("message_change");
                                mContext.sendBroadcast(intent);
                            } else {
                                LogUtils.log("保存消息失败!");
                            }
                        }
                    });
            if (!APPStatusUtils.isAppRunningOnTop(mContext, "cn.com.zhiwoo")) {
                LogUtils.log("不在前台收到了消息,即收到后台消息");
                //自己手动发出本地通知,再在点击通知后,唤醒APP
                getUserInfo(id, new loadUserInfoCallBack() {
                    @Override
                    public void onsuccess(UserInfo userInfo) {
                        String userName = userInfo.getName();
                        String content = null;
                        MessageContent messageContent = message.getContent();
                        if (messageContent instanceof TextMessage) {//文本消息
                            TextMessage textMessage = (TextMessage) messageContent;
                            content = textMessage.getContent();
                            LogUtils.log("onReceived-TextMessage:" + textMessage.getContent());
                        } else if (messageContent instanceof ImageMessage) {//图片消息
                            ImageMessage imageMessage = (ImageMessage) messageContent;
                            content = "图片";
                            LogUtils.log("onReceived-ImageMessage:" + imageMessage.getRemoteUri());
                        } else if (messageContent instanceof VoiceMessage) {//语音消息
                            VoiceMessage voiceMessage = (VoiceMessage) messageContent;
                            content = "语音";
                            LogUtils.log("onReceived-voiceMessage:" + voiceMessage.getUri().toString());
                        } else if (messageContent instanceof RichContentMessage) {//图文消息
                            RichContentMessage richContentMessage = (RichContentMessage) messageContent;
                            content = richContentMessage.getContent();
                            LogUtils.log("onReceived-RichContentMessage:" + richContentMessage.getContent());
                        } else if (messageContent instanceof InformationNotificationMessage) {//小灰条消息
                            InformationNotificationMessage informationNotificationMessage = (InformationNotificationMessage) messageContent;
                            if (informationNotificationMessage.getExtra().equals("ZWConsultBegToPayTipExtra")) {
                                LogUtils.log("导师请求打赏");
                                Intent intent = new Intent("ask_tip");
                                mContext.sendBroadcast(intent);
                                content = "能对我的回答进行打赏吗?";
                            }
                            LogUtils.log("onReceived-informationNotificationMessage:" + informationNotificationMessage.getMessage());
                        } else {
                            LogUtils.log("onReceived-其他消息，自己来判断处理");
                            content = "未知内容";
                        }
                        NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                        Intent intent = new Intent(mContext, NotificationActivity.class);
                        PendingIntent pi = PendingIntent.getActivity(mContext, 0, intent, 0);
                        Notification n = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            n = new Notification.Builder(mContext)
                                    .setContentTitle(userName)
                                    .setContentText(content)
                                    .setTicker(userName + " : " + content)
                                    .setSmallIcon(R.mipmap.icon)
                                    .setContentIntent(pi)
                                    .setAutoCancel(true)
                                    .setDefaults(Notification.DEFAULT_SOUND)
                                    .setWhen(System.currentTimeMillis())
                                    .build();
                        }
                        LogUtils.log("发送本地通知 username : " + userName + " ,content : " + content);
                        nm.notify(1, n);
                    }
                });
                return true;
            } else {
                MessageContent messageContent = message.getContent();
                if (messageContent instanceof TextMessage) {//文本消息
                    TextMessage textMessage = (TextMessage) messageContent;
                    LogUtils.log("onReceived-TextMessage:" + textMessage.getContent());
                } else if (messageContent instanceof ImageMessage) {//图片消息
                    ImageMessage imageMessage = (ImageMessage) messageContent;
                    LogUtils.log("onReceived-ImageMessage:" + imageMessage.getRemoteUri());
                } else if (messageContent instanceof VoiceMessage) {//语音消息
                    VoiceMessage voiceMessage = (VoiceMessage) messageContent;
                    LogUtils.log("onReceived-voiceMessage:" + voiceMessage.getUri().toString());
                } else if (messageContent instanceof RichContentMessage) {//图文消息
                    RichContentMessage richContentMessage = (RichContentMessage) messageContent;
                    LogUtils.log("onReceived-RichContentMessage:" + richContentMessage.getContent());
                } else if (messageContent instanceof InformationNotificationMessage) {//小灰条消息
                    InformationNotificationMessage informationNotificationMessage = (InformationNotificationMessage) messageContent;
                    if (informationNotificationMessage.getExtra().equals("ZWConsultBegToPayTipExtra")) {
                        LogUtils.log("导师请求打赏");
                        Intent intent = new Intent("ask_tip");
                        mContext.sendBroadcast(intent);
                    }
                    LogUtils.log("onReceived-informationNotificationMessage:" + informationNotificationMessage.getMessage());
                } else {
                    LogUtils.log("onReceived-其他消息，自己来判断处理");
                }
                LogUtils.log("在前台收到了消息,即收到前台消息");
            }
                return false;
        }
    }


    /**
     * 连接状态监听器
     */
    private class MyConnectionStatusListener implements RongIMClient.ConnectionStatusListener {

        @Override
        public void onChanged(ConnectionStatus connectionStatus) {

            switch (connectionStatus){

                case CONNECTED://连接成功。
                    LogUtils.log("融云连接成功");
                    break;
                case DISCONNECTED://断开连接。
                    LogUtils.log("融云断开连接");

                    break;
                case CONNECTING://连接中。
                    LogUtils.log("融云连接中");

                    break;
                case NETWORK_UNAVAILABLE://网络不可用。
                    LogUtils.log("融云网络不可用");

                    break;
                case KICKED_OFFLINE_BY_OTHER_CLIENT://用户账户在其他设备登录，本机会被踢掉线
                    LogUtils.log("融云被挤掉线");
                    ChatTool.this.connectStatusHandler.sendEmptyMessage(1);
            }
        }
    }
    private class MyReceivePushMessageListener implements RongIMClient.OnReceivePushMessageListener{

        /**
         * 收到 push 消息的处理。
         *
         * @param pushNotificationMessage push 消息实体。
         * @return true 自己来弹通知栏提示，false 融云 SDK 来弹通知栏提示。
         */
        @Override
        public boolean onReceivePushMessage(PushNotificationMessage pushNotificationMessage) {
            LogUtils.log("收到推送消息");
            return false;
        }

    }
    private static ClickMessageListener messageListener;
    public ClickMessageListener getMessageListener(){
        if (null == messageListener){
            messageListener = new ClickMessageListener();
        }
        return messageListener;
    }
    private class ClickMessageListener implements RongIM.ConversationBehaviorListener {

        @Override
        public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
            return false;
        }

        @Override
        public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
            String uid = userInfo.getUserId();
            Toast.makeText(context, "对方ID："+uid, Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public boolean onMessageClick(Context context, View view, io.rong.imlib.model.Message message) {
            if (message.getContent() instanceof RealTimeLocationStartMessage) {
                return false;
            }else if (message.getContent() instanceof ImageMessage) {
                ImageMessage imageMessage = (ImageMessage) message.getContent();
                Intent intent = new Intent(context, PhotoActivity.class);
                intent.putExtra("photo", imageMessage.getLocalUri() == null ? imageMessage.getRemoteUri() : imageMessage.getLocalUri());
                if (message.getMessageDirection() == io.rong.imlib.model.Message.MessageDirection.SEND) {
                    intent.putExtra("isSend",true);
                } else {
                    intent.putExtra("isSend",false);
                }
                context.startActivity(intent);
                return false;
            }
            else if (message.getContent() instanceof RichContentMessage){
                RichContentMessage richContentMessage = (RichContentMessage) message.getContent();
                ReactArticle.QBean article = new ReactArticle.QBean();
                article.setTitle(richContentMessage.getTitle());
                article.setContent_url(richContentMessage.getUrl());
                Intent intent = new Intent(context, ArticleDetailActivity.class);
                intent.putExtra("article",article);
                context.startActivity(intent);
            }
            return true;
        }

        @Override
        public boolean onMessageLinkClick(Context context, String s) {
            return false;
        }

        @Override
        public boolean onMessageLongClick(Context context, View view, io.rong.imlib.model.Message message) {
            return false;
        }
    }


}
