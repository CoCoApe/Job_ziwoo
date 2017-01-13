package cn.com.zhiwoo.bean.react;

import java.io.Serializable;
import java.util.ArrayList;


public class Lesson implements Serializable {


        private String imageUrl;
    private String title;
    private String tourId;
    private String introUrl;

    public static ArrayList<Lesson> allLessons() {
        ArrayList<Lesson> lessons = new ArrayList<>();
//        Lesson lesson1 = new Lesson("http://7xkilk.com2.z0.glb.qiniucdn.com/zwapp_react_lessonreact_lesson_recover.png","挽回课","1769","http://mp.weixin.qq.com/s?__biz=MzAwNzYzMTkwNQ==&mid=400618862&idx=1&sn=2bf48684fb6acd96d546ca181e13da95#rd",100.0f);
//        lessons.add(lesson1);
//        Lesson lesson2 = new Lesson("http://7xkilk.com2.z0.glb.qiniucdn.com/zwapp_react_lessonreact_lesson_plan.png","恋爱计划","1769","http://mp.weixin.qq.com/s?__biz=MzAwNzYzMTkwNQ==&mid=403498428&idx=1&sn=1664beb98cb54453726505e552b3f873#rd",100.0f);
//        lessons.add(lesson2);
        Lesson lesson3 = new Lesson("http://7xkilk.com2.z0.glb.qiniucdn.com/zwapp_react_article_20160714.png","免费情感分析",null,"https://www.wenjuan.com/s/Mf6vqme/",0.0f);
        lessons.add(lesson3);
        return lessons;
    }
    public Lesson() {
        super();
    }
    private Lesson(String imageUrl, String title, String tourId, String introUrl, float prePrice) {
        super();
        this.imageUrl = imageUrl;
        this.title = title;
        this.tourId = tourId;
        this.introUrl = introUrl;
        this.prePrice = prePrice;
    }
    public float getPrePrice() {
        return prePrice;
    }

    public void setPrePrice(float prePrice) {
        this.prePrice = prePrice;
    }

    public String getIntroUrl() {
        return introUrl;
    }

    public void setIntroUrl(String introUrl) {
        this.introUrl = introUrl;
    }

    public String getTourId() {
        return tourId;
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private float prePrice;
}
