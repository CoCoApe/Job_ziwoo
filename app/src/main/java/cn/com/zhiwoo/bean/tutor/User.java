package cn.com.zhiwoo.bean.tutor;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class User implements Serializable {
    protected String id;
    @SerializedName("nickname")
    String nickName;
    @SerializedName("headimgurl")
    String headImageUrl;
    @SerializedName("is_tutor")
    private boolean isTour;
    private String city;
    private String province;
    private String mobile;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    private String country;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isTour() {
        return isTour;
    }

    public void setIsTour(boolean isTour) {
        this.isTour = isTour;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadImageUrl() {
        return headImageUrl;
    }

    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
