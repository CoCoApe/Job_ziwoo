package cn.com.zhiwoo.activity.consult;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import cn.com.zhiwoo.R;


 class ConsultMoudleDialog extends Dialog implements View.OnClickListener {

     private EditText conditionEditText;
     private EditText experenceEditText;
     private OnMoudleClickSend onMoudleClickSend;
     private EditText otherConditonEditText;
     private Spinner spinner;
     private String expect;

     static void showDialog(Context paramContext, OnMoudleClickSend paramOnMoudleClickSend)
    {
        ConsultMoudleDialog localConsultMoudleDialog = new ConsultMoudleDialog(paramContext);
        localConsultMoudleDialog.setOnMoudleClickSend(paramOnMoudleClickSend);
        localConsultMoudleDialog.show();
    }

    public void onClick(View view)
    {
        this.onMoudleClickSend.moudleClickSend(this.expect, this.conditionEditText.getText().toString(), this.otherConditonEditText.getText().toString(), this.experenceEditText.getText().toString());
        dismiss();
    }

    protected void ConsultMoudleDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    private ConsultMoudleDialog(Context context) {
        super(context, R.style.PayTipSelectDialogTheme);
    }

    protected ConsultMoudleDialog(Context context, int theme) {
        super(context, theme);
    }

    protected ConsultMoudleDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consult_moudle_dialog);
//        this.problemEditText = ((EditText)findViewById(R.id.problem_edittext));
        this.conditionEditText = ((EditText)findViewById(R.id.condition_edittext));
        this.otherConditonEditText = ((EditText)findViewById(R.id.otherCondition_edittext));
        this.experenceEditText = ((EditText)findViewById(R.id.exprence_edittext));
        this.spinner = (Spinner) findViewById(R.id.come_for_edittext);
        findViewById(R.id.send_moudle_button).setOnClickListener(this);
        setSpinner();

    }

     private void setSpinner() {
         final String[] expects = getContext().getResources().getStringArray(R.array.expects);
         spinner.setSelection(0);
         spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 TextView tv = (TextView)view;
                 tv.setTextColor(R.color.black);
                 tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
                 expect = expects[position];
                 spinner.setSelection(position);
             }
             @Override
             public void onNothingSelected(AdapterView<?> parent) {

             }
         });
     }

     private void setOnMoudleClickSend(OnMoudleClickSend paramOnMoudleClickSend)
    {
        this.onMoudleClickSend = paramOnMoudleClickSend;
    }

    interface OnMoudleClickSend
    {
        void moudleClickSend(String paramString1, String paramString2, String paramString3, String paramString4);
    }
}
