package cn.com.zhiwoo.view.consult;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.utils.DensityUtil;
import cn.com.zhiwoo.utils.LogUtils;


public class PayTipSelectDialog extends AlertDialog implements View.OnClickListener {

    private OnChosenPayAmount onChosenPayAmount;
    public static void showDialog(Context context,OnChosenPayAmount onChosenPayAmount) {
        PayTipSelectDialog payTipSelectDialog = new PayTipSelectDialog(context);
        payTipSelectDialog.setOnChosenPayAmount(onChosenPayAmount);
        payTipSelectDialog.show();
        // 设置对话框大小
        Point size = new Point();
        payTipSelectDialog.getWindow().getWindowManager().getDefaultDisplay().getSize(size);
        WindowManager.LayoutParams layoutParams = payTipSelectDialog.getWindow().getAttributes();
        layoutParams.width = size.x - DensityUtil.dip2px(payTipSelectDialog.getContext(),60);
        layoutParams.height = (int)(layoutParams.width * 1327.0f / 1072.0f);
        payTipSelectDialog.getWindow().setAttributes(layoutParams);
    }

    private PayTipSelectDialog(Context context) {
        super(context, R.style.PayTipSelectDialogTheme);
    }

    protected PayTipSelectDialog(Context context, int theme) {
        super(context, theme);
    }

    protected PayTipSelectDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consult_paytip_select_dialog);
        initView();
    }

    private void initView() {
        ImageView pay3 = (ImageView) findViewById(R.id.pay3_imageview);
        ImageView pay6 = (ImageView) findViewById(R.id.pay6_imageview);
        ImageView pay16 = (ImageView) findViewById(R.id.pay16_imageview);
        ImageView pay29 = (ImageView) findViewById(R.id.pay29_imageview);
        ImageView pay55 = (ImageView) findViewById(R.id.pay55_imageview);
        pay3.setOnClickListener(this);
        pay6.setOnClickListener(this);
        pay16.setOnClickListener(this);
        pay29.setOnClickListener(this);
        pay55.setOnClickListener(this);
    }

    public OnChosenPayAmount getOnChosenPayAmount() {
        return onChosenPayAmount;
    }

    private void setOnChosenPayAmount(OnChosenPayAmount onChosenPayAmount) {
        this.onChosenPayAmount = onChosenPayAmount;
    }

    public void onClick(View view) {
        LogUtils.log("点击了打赏选择按钮");
        if (onChosenPayAmount != null) {
            float amount = 0.0f;
            switch (view.getId()) {
                case R.id.pay3_imageview : {
                    amount = 3.0f;
                }
                break;
                case R.id.pay6_imageview : {
                    amount = 6.0f;
                }
                break;
                case R.id.pay16_imageview : {
                    amount = 16.0f;
                }
                break;
                case R.id.pay29_imageview : {
                    amount = 29.0f;
                }
                break;
                case R.id.pay55_imageview : {
                    amount = 55.0f;
                }
                break;
            }
            LogUtils.log("选择了打赏金额 " + amount + " 元");
            onChosenPayAmount.didChosenPayAmount(amount);
        }
        dismiss();
    }

    public interface OnChosenPayAmount {
        void didChosenPayAmount(float amount);
    }
}
