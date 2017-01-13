package cn.com.zhiwoo.bean.home;


public class Message {
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_ICON = "user_icon";
    public static final String UNREAD_COUNT = "unread_count";
    public static final String LAST_TIEM = "last_time";


    private int unreadCount;
    private String userId;
    private String userName;
    private long lastTime;
    private String userIcon;

    public Message() {}
    public Message(String userId,String userIcon,String userName,int unreadCount,long lastTime) {
        this.userIcon = userIcon;
        this.userName = userName;
        this.unreadCount = unreadCount;
        this.userId = userId;
        this.lastTime = lastTime;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
