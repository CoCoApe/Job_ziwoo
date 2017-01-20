package cn.com.zhiwoo.bean.consult;

import java.util.ArrayList;

import cn.com.zhiwoo.R;


public class ConsultTypeMode {
    private static final int MODE_baituo = 1;
    private static final int MODE_wanhui = 2;
    private static final int MODE_tuijin = 3;
    private static final int MODE_weixi = 4;

    private int imageName;
    private String title;
    private int mode;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageName() {
        return imageName;
    }

    private int num;

    public int getMode() {
        return mode;
    }

    private ConsultTypeMode(int imageName, String title, int num, int mode) {
        super();
        this.imageName = imageName;
        this.title = title;
        this.num = num;
        this.mode = mode;
    }

    public ConsultTypeMode(int imageName) {
        this.imageName = imageName;
    }

    public static ArrayList<ConsultTypeMode> allConsultTypeModes() {
        ArrayList<ConsultTypeMode> modes = new ArrayList<>();
//        ConsultTypeMode mode1 = new ConsultTypeMode(R.drawable.consult_icon_tuijin,"关系推进",56642,MODE_tuijin);
//        ConsultTypeMode mode2 = new ConsultTypeMode(R.drawable.consult_icon_weixi,"婚姻维系",26889,MODE_weixi);
//        ConsultTypeMode mode3 = new ConsultTypeMode(R.drawable.consult_icon_baituo,"摆脱单身",52521,MODE_baituo);
//        ConsultTypeMode mode4 = new ConsultTypeMode(R.drawable.consult_icon_wanhui,"失恋挽回",36965,MODE_wanhui);

        ConsultTypeMode mode1 = new ConsultTypeMode(R.drawable.consult_wanhui);
        ConsultTypeMode mode2 = new ConsultTypeMode(R.drawable.consult_weixi);
        ConsultTypeMode mode3 = new ConsultTypeMode(R.drawable.consult_baituo);
        ConsultTypeMode mode4 = new ConsultTypeMode(R.drawable.consult_tuijin);


        modes.add(mode1);
        modes.add(mode2);
        modes.add(mode3);
        modes.add(mode4);
        return modes;
    }
}
