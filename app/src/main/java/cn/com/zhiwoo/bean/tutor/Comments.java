package cn.com.zhiwoo.bean.tutor;

import java.util.List;

/**
 * Created by 25820 on 2017/1/5.
 */

public class Comments {

    /**
     * code : [{"nickname":"💜大何ariel💓","headimgurl":"http://wx.qlogo.cn/mmopen/DBmAcyhE3e8HibuLQZc3F87qPZEfp5WcdiboqXtkjHib7ick7oflPcDtNoQ9ppjs5g5sicpuJxxEibCWGfC4W557sCOA/0","id":67,"user_id":9016,"user_pl":"小暖男一枚，很负责任的分析师！大爱！","dz_sum":0,"pl_hot":0},{"nickname":"13989337938","headimgurl":"http://zhiwoo.com.cn/zero/images/user.png","id":66,"user_id":9979,"user_pl":"总是那么一针见血 非常有耐心的好导师！","dz_sum":0,"pl_hot":0},{"nickname":"Nickつ 暮光","headimgurl":"http://wx.qlogo.cn/mmopen/UOlZKghBxaYvtzT5yJkL8jDlj9jkibRdjHnI5nUxoa4C1Alb4ibyAknt6MILliaZnibpOMLZGKRI2XIkVCxIZIRYFAfFswN6UrU2vPmKQO3Lbp4/0","id":52,"user_id":1,"user_pl":"很感激老师这段时间对我的耐心教导","dz_sum":51,"pl_hot":0,"is_dz":true},{"nickname":"忘了呔爱","headimgurl":"http://wx.qlogo.cn/mmopen/DBmAcyhE3e8cicvkyFZCRczrIbzjPyK0g7hsw3ApPfYiatCxManNjMcKkb6licePe5WocOZs9Aa6LuI1WpRXicicibfRXkk43qiaYqK/0","id":39,"user_id":5,"user_pl":"声音那么好听，那么温柔，讲课那么有条理，爱你么么哒！","dz_sum":142,"pl_hot":30,"is_dz":true},{"nickname":"没有感觉了","headimgurl":"","id":1,"user_id":2,"user_pl":"老师的开导与指导，帮助我走出困局\u2026跟男朋友分手非常绝望，可在老师的指导明白先要重拾自我才能挽回感情，现在跟男朋友情况好转，以后也需要老师教我更多如何提升自己的技巧！","dz_sum":79,"pl_hot":21,"is_dz":true}]
     * tutors : 3775
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
         * nickname : 💜大何ariel💓
         * headimgurl : http://wx.qlogo.cn/mmopen/DBmAcyhE3e8HibuLQZc3F87qPZEfp5WcdiboqXtkjHib7ick7oflPcDtNoQ9ppjs5g5sicpuJxxEibCWGfC4W557sCOA/0
         * id : 67
         * user_id : 9016
         * user_pl : 小暖男一枚，很负责任的分析师！大爱！
         * dz_sum : 0
         * pl_hot : 0
         * is_dz : true
         */

        private String nickname;
        private String headimgurl;
        private int id;
        private int user_id;
        private String user_pl;
        private int dz_sum;
        private int pl_hot;
        private boolean is_dz;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getHeadimgurl() {
            return headimgurl;
        }

        public void setHeadimgurl(String headimgurl) {
            this.headimgurl = headimgurl;
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
