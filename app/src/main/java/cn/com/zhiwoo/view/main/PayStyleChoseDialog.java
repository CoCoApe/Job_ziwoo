package cn.com.zhiwoo.view.main;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import cn.com.zhiwoo.R;


public class PayStyleChoseDialog extends AlertDialog implements View.OnClickListener {
    public final static int PayStyleWXPay = 1;
    private final static int PayStyleALiPay = 2;
    private OnChosenPayStyle onChosenPayStyle;

    public static void showDialog(Context context,OnChosenPayStyle onChosenPayStyle) {
        PayStyleChoseDialog payStyleChoseDialog = new PayStyleChoseDialog(context);
        payStyleChoseDialog.setOnChosenPayStyle(onChosenPayStyle);
        payStyleChoseDialog.show();
    }

    private PayStyleChoseDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_paystylechose_dialog);
        findViewById(R.id.wxpay_imageview).setOnClickListener(this);
        findViewById(R.id.alipay_imageview).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wxpay_imageview : {
                if (onChosenPayStyle != null) {
                    onChosenPayStyle.didChosenPayStyle(PayStyleWXPay);
                }
            }
            break;
            case R.id.alipay_imageview : {
                if (onChosenPayStyle != null) {
                    onChosenPayStyle.didChosenPayStyle(PayStyleALiPay);
                }
            }
            break;
        }
        dismiss();
    }

    private void setOnChosenPayStyle(OnChosenPayStyle onChosenPayStyle) {
        this.onChosenPayStyle = onChosenPayStyle;
    }

    public interface OnChosenPayStyle {
        void didChosenPayStyle(int payStyle);
    }
}
