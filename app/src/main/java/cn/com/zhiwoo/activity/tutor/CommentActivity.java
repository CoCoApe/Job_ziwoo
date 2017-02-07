package cn.com.zhiwoo.activity.tutor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.bean.main.Account;
import cn.com.zhiwoo.bean.tutor.Tour;
import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.utils.Api;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by 25820 on 2017/2/5.
 */

public class CommentActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView close_btn;
    private TextView tutor_name;
    private TextView commit_btn;
    private EditText comment_content;
    private Tour tutor;
    private String content;
    private Account account;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initView();
        initListener();
    }

    private void initView() {
        close_btn = (ImageView) findViewById(R.id.comment_bar_close);
        tutor_name = (TextView) findViewById(R.id.comment_bar_tutorName);
        commit_btn = (TextView) findViewById(R.id.comment_bar_commit);
        comment_content = (EditText) findViewById(R.id.comment_content_et);
        getDataFromIntent(getIntent());
    }

    private void initListener() {
        close_btn.setOnClickListener(this);
        commit_btn.setOnClickListener(this);
        comment_content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                comment_content.setCursorVisible(hasFocus);
                comment_content.setHint(hasFocus ? "" : "请写下你的评论...");
            }
        });
    }

    @Override
    public void onClick(View v) {
        content = comment_content.getText().toString();
        switch (v.getId()){
            case R.id.comment_bar_close:
                closeClick();
                break;
            case R.id.comment_bar_commit:
                commitClick();
                break;
        }
    }

    private void closeClick(){
        if (!TextUtils.isEmpty(content)){
            showExitDialog();
        }else {
            finish();
        }
    }

    private void commitClick(){
        if (!TextUtils.isEmpty(content)){
            postComment();
            showCommitDialog();
        }else {
            showToInputDialog();
        }
    }

    private void postComment() {
        Map<String,String> params = new HashMap<>();
        params.put("tutorId",tutor.getId());
        params.put("userId",account.getId());
        params.put("plContent",content);
        OkGo.post(Api.COMMENT_COMMIT)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {

                    }
                });
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setMessage("本次编辑的内容尚未发布，确定退出吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
    private void showCommitDialog() {
        new AlertDialog.Builder(this)
                .setMessage("评论成功！审核通过后将显示在评论列表。")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .show();
    }
    private void showToInputDialog() {
        new AlertDialog.Builder(this)
                .setMessage("评论不能为空，请评论后再发表！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


    private void getDataFromIntent(Intent intent) {
        tutor = (Tour) intent.getSerializableExtra("tutor");
        account = AccountTool.getCurrentAccount(this);
        if (tutor != null) {
            tutor_name.setText(tutor.getNickName()+"的评论");
        }
    }
}
