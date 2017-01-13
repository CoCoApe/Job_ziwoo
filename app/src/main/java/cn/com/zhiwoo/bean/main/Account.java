package cn.com.zhiwoo.bean.main;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Account implements Serializable{

    protected String id;
    @SerializedName("nickname")
    private String nickName;
    @SerializedName("headimgurl")
    private String headImageUrl;
    @SerializedName("is_tutor")
    private boolean isTour;
    private String mobile;
    @SerializedName("access_token")
    private String accessToken;
    private int sex;
    private int age;
    private String country;
    private String province;
    private String city;
    private String demand_infos;
    private String birth;

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDemand_infos() {
        return demand_infos;
    }

    public void setDemand_infos(String demand_infos) {
        this.demand_infos = demand_infos;
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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }
}
