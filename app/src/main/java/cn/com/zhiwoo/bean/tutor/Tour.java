package cn.com.zhiwoo.bean.tutor;

import java.io.Serializable;


public class Tour extends User implements Serializable {
    private String short_intro;
    private String long_intro;
    private int likeRate;
    private Points points;
    private String englishname;

    public String getEnglishname() {
        return englishname;
    }

    public void setEnglishname(String englishname) {
        this.englishname = englishname;
    }

    public Points getPoints() {
        return points;
    }

    public void setPoints(Points points) {
        this.points = points;
    }

    public int getLikeRate() {
        return likeRate;
    }

    public void setLikeRate(int likeRate) {
        this.likeRate = likeRate;
    }

    public String getLong_intro() {
        return long_intro;
    }

    public void setLong_intro(String long_intro) {
        this.long_intro = long_intro;
    }

    public String getShort_intro() {
        return short_intro;
    }

    public void setShort_intro(String short_intro) {
        this.short_intro = short_intro;
    }

    public static class Points implements Serializable {
        public int relationImpel;
        public int retrieveLover;
        public int matrimonyHold;
        public int dispenseSingle;

        public int getDispenseSingle() {
            return dispenseSingle;
        }

        public void setDispenseSingle(int dispenseSingle) {
            this.dispenseSingle = dispenseSingle;
        }

        public int getMatrimonyHold() {
            return matrimonyHold;
        }

        public void setMatrimonyHold(int matrimonyHold) {
            this.matrimonyHold = matrimonyHold;
        }

        public int getRetrieveLover() {
            return retrieveLover;
        }

        public void setRetrieveLover(int retrieveLover) {
            this.retrieveLover = retrieveLover;
        }

        public int getRelationImpel() {
            return relationImpel;
        }

        public void setRelationImpel(int relationImpel) {
            this.relationImpel = relationImpel;
        }




        @Override
        public String toString() {
            return "relationImpel:"+getRelationImpel() + " retrieveLover:"+getRetrieveLover()+" matrimonyHold:"+getMatrimonyHold()+" dispenseSingle:"+getDispenseSingle();
        }
    }

    @Override
    public String toString() {
        return "nickname:"+nickName+"   headimgurl:"+headImageUrl+"points:"+points.toString();
    }
}
