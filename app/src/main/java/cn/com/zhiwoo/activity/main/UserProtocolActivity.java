package cn.com.zhiwoo.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cn.com.zhiwoo.R;



public class UserProtocolActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textView;
    private TextView titleView;
    private ImageView leftImageView;
    private ImageView rightImageView;
    private Button agreeButton;
    private Button disagreeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_userprotocol_activity);
        initView();
        initData();
    }

    public void initView() {
        titleView = (TextView) findViewById(R.id.tv_title);
        leftImageView = (ImageView) findViewById(R.id.topBar_left_image);
        rightImageView = (ImageView) findViewById(R.id.topBar_right_image);
        agreeButton = (Button) findViewById(R.id.agree_button);
        disagreeButton = (Button) findViewById(R.id.disagree_button);
        rightImageView.setVisibility(View.GONE);
        leftImageView.setImageResource(R.drawable.nav_fanhui);
        agreeButton.setOnClickListener(this);
        disagreeButton.setOnClickListener(this);
        leftImageView.setOnClickListener(this);
        textView = (TextView) findViewById(R.id.userprotocol_textview);

    }

    public void initData() {
        titleView.setText("咨我用户协议");
        InputStream inputStream = null;
        try {
             inputStream = getAssets().open("userprotocol.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String content = getString(inputStream);
            textView.setText(content);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("agree",true);
        setResult(1000,intent);
        super.onBackPressed();
    }
    private String getString(InputStream inputStream) {
        InputStreamReader inputStreamReader;
        StringBuilder sb = new StringBuilder("");
        try {
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.agree_button : {
                intent.putExtra("agree",true);
            }
            break;
            case R.id.disagree_button : {
                intent.putExtra("agree",false);
            }
            break;
            case R.id.topBar_left_image : {
                intent.putExtra("agree",true);
            }
        }
        setResult(1000, intent);
        finish();
    }
}
