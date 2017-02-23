package cn.com.zhiwoo.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.zhiwoo.activity.main.InfoPerfectActivity;

/**
 * Created by 25820 on 2016/12/12.
 */

public class MyUtils {
    public static boolean isPhoneNumberValid(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)){
            return false;
        }
        boolean isValid = false;
        String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]\\d-?\\d{8}$)|(^0[3-9] \\d{2}-?\\d{7,8}$)|(^0[1,2]\\d-?\\d{8}-(\\d{1,4})$)|(^0[3-9]\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(phoneNumber);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static void showInfoPerfectDialog(final Context context) {
        new AlertDialog.Builder(context)
                .setMessage("立即绑定手机，保障你的隐私权益。并且获得更多免费的情感服务。")
                .setPositiveButton("立即绑定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        context.startActivity(new Intent(context, InfoPerfectActivity.class));
                    }
                })
                .setNegativeButton("稍后绑定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
