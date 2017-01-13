package cn.com.zhiwoo.bean.consult;

import java.util.List;

/**
 * Created by 25820 on 2016/12/28.
 */

public class Banner {

    /**
     * data : [{"id":1,"app_pic":"dfgdfgdf","app_url":"35535235","created_at":"2016-12-28 18:20:56"}]
     * Message : data available
     */

    private String Message;
    private List<DataBean> data;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1
         * app_pic : dfgdfgdf
         * app_url : 35535235
         * created_at : 2016-12-28 18:20:56
         */

        private int id;
        private String app_pic;
        private String app_url;
        private String created_at;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getApp_pic() {
            return app_pic;
        }

        public void setApp_pic(String app_pic) {
            this.app_pic = app_pic;
        }

        public String getApp_url() {
            return app_url;
        }

        public void setApp_url(String app_url) {
            this.app_url = app_url;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
    }
}
