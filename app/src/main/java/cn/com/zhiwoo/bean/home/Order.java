package cn.com.zhiwoo.bean.home;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.util.Date;

import cn.com.zhiwoo.tool.ChatTool;
import cn.com.zhiwoo.utils.DateUtils;
import cn.com.zhiwoo.utils.LogUtils;
import io.rong.imlib.model.UserInfo;


public class Order {
    @SerializedName("tutor_id")
    private String tourId;
    private String tourName;
    @SerializedName("user_id")
    private String userId;
    private String userName;
    private String created_at;
    private String date;
    private String problem;

    public Order() {
    }

    //由于返回的信息不完整,不得不加载更多的信息
    public void loadMoreInfo() {
        if (tourId != null) {
            ChatTool.sharedTool().getUserInfo(tourId, new ChatTool.loadUserInfoCallBack() {
                @Override
                public void onsuccess(UserInfo userInfo) {
                    tourName = userInfo.getName();
                }
            });
        }
        if (userId != null) {
            ChatTool.sharedTool().getUserInfo(userId, new ChatTool.loadUserInfoCallBack() {
                @Override
                public void onsuccess(UserInfo userInfo) {
                    userName = userInfo.getName();
                }
            });
        }
        //2016-05-26T07:44:34.911Z
        //2016-05-26T07:44:34.911Z
        LogUtils.log("created_at" + created_at);
        DateTime dateTime = new DateTime(created_at);
        Date createdDate = dateTime.toDate();
        if (createdDate != null) {
            date = DateUtils.relativeDate(createdDate);
        }
    }

    public String getTourName() {
        return tourName;
    }

    public String getUserName() {
        return userName;
    }

    public String getDate() {
        return date;
    }

    public String getProblem() {
        return problem;
    }
}
