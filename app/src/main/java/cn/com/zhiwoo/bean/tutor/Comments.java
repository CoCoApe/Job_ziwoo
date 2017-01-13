package cn.com.zhiwoo.bean.tutor;

import java.util.List;

/**
 * Created by 25820 on 2017/1/5.
 */

public class Comments {


    /**
     * code : [{"user_nickname":"新的用户","user_headimg":"","id":21,"user_id":2,"user_pl":"第二个评论","dz_sum":56,"pl_hot":62,"is_dz":true},{"user_nickname":"Nick","user_headimg":"http://wx.qlogo.cn/mmopen/UOlZKghBxaYvtzT5yJkL8jDlj9jkibRdjHnI5nUxoa4C1Alb4ibyAknt6MILliaZnibpOMLZGKRI2XIkVCxIZIRYFAfFswN6UrU2vPmKQO3Lbp4/0","id":9,"user_id":1,"user_pl":"第一个评论","dz_sum":41,"pl_hot":29,"is_dz":true}]
     * tutors : 9
     */

    private String tutors;
    private List<CodeBean> code;

    public String getTutors() {
        return tutors;
    }

    public void setTutors(String tutors) {
        this.tutors = tutors;
    }

    public List<CodeBean> getCode() {
        return code;
    }

    public void setCode(List<CodeBean> code) {
        this.code = code;
    }

    public static class CodeBean {
        /**
         * user_nickname : 新的用户
         * user_headimg :
         * id : 21
         * user_id : 2
         * user_pl : 第二个评论
         * dz_sum : 56
         * pl_hot : 62
         * is_dz : true
         */

        private String user_nickname;
        private String user_headimg;
        private int id;
        private int user_id;
        private String user_pl;
        private int dz_sum;
        private int pl_hot;
        private boolean is_dz;

        public String getUser_nickname() {
            return user_nickname;
        }

        public void setUser_nickname(String user_nickname) {
            this.user_nickname = user_nickname;
        }

        public String getUser_headimg() {
            return user_headimg;
        }

        public void setUser_headimg(String user_headimg) {
            this.user_headimg = user_headimg;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getUser_pl() {
            return user_pl;
        }

        public void setUser_pl(String user_pl) {
            this.user_pl = user_pl;
        }

        public int getDz_sum() {
            return dz_sum;
        }

        public void setDz_sum(int dz_sum) {
            this.dz_sum = dz_sum;
        }

        public int getPl_hot() {
            return pl_hot;
        }

        public void setPl_hot(int pl_hot) {
            this.pl_hot = pl_hot;
        }

        public boolean isIs_dz() {
            return is_dz;
        }

        public void setIs_dz(boolean is_dz) {
            this.is_dz = is_dz;
        }
    }
}
