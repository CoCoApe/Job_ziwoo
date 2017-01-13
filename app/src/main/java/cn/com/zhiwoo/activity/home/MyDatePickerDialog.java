package cn.com.zhiwoo.activity.home;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.DatePicker;




public class MyDatePickerDialog extends AlertDialog implements DatePicker.OnDateChangedListener {


    protected MyDatePickerDialog(Context context) {
        super(context);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

    }
}
