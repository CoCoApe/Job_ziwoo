package cn.com.zhiwoo.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtils {
    public static String relativeDate(Date date) {
        String result;
        Calendar calendar = Calendar.getInstance();
        //传入的时间的 年 日 时 分
        calendar.setTime(date);
        int year1 = calendar.get(Calendar.YEAR);
        int day1 = calendar.get(Calendar.DAY_OF_YEAR);
        int minute1 = calendar.get(Calendar.MINUTE);

        //当前时间的 年 日 时 分
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        int minute = calendar.get(Calendar.MINUTE);

        calendar.setTime(date);

        if (year == year1) {//在同一年
            if (day == day1) {//在今天
                if (minute == minute1) {//刚刚
                    result = "刚刚";
                } else { //前一个小时以内
                    result = (new SimpleDateFormat("HH:mm")).format(calendar.getTime());
                }

            } else if (day - day1 <= 1) {//在昨天
                String str = (new SimpleDateFormat("HH:mm")).format(calendar.getTime());
                result = "昨天 " + str;
            } else {//很多天以前
                result = (new SimpleDateFormat("MM-dd")).format(calendar.getTime());
            }
        } else {//不在同一年
            result = (new SimpleDateFormat("yyyy-MM-dd")).format(calendar.getTime());
        }
//        String str = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(calendar.getTime());
//        LogUtils.log("时间处理 日期: " + str + " ,相对时间: " + result);
        return result;
    }
}
