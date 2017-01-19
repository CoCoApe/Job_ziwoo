package cn.com.zhiwoo.bean.react;


import java.io.Serializable;
import java.util.List;

public class LessonEvent implements Serializable{

    /**
     * data : [{"course_id":1,"course_href":"http://cctv3.qiniudn.com/zuixingfuderen.mp3","pay":"0.01","course_name":"第一个课程","is_cost":false}]
     * message : OK!
     */

    private String message;
    private List<DataBean> data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable{
        /**
         * course_id : 1
         * course_href : http://cctv3.qiniudn.com/zuixingfuderen.mp3
         * pay : 0.01
         * course_name : 第一个课程
         * is_cost : false
         */

        private String course_id;
        private String course_href;
        private String pay;
        private String course_name;
        private boolean is_cost;

        public String getCourse_id() {
            return course_id;
        }

        public void setCourse_id(String course_id) {
            this.course_id = course_id;
        }

        public String getCourse_href() {
            return course_href;
        }

        public void setCourse_href(String course_href) {
            this.course_href = course_href;
        }

        public String getPay() {
            return pay;
        }

        public void setPay(String pay) {
            this.pay = pay;
        }

        public String getCourse_name() {
            return course_name;
        }

        public void setCourse_name(String course_name) {
            this.course_name = course_name;
        }

        public boolean isIs_cost() {
            return is_cost;
        }

        public void setIs_cost(boolean is_cost) {
            this.is_cost = is_cost;
        }
    }
}
