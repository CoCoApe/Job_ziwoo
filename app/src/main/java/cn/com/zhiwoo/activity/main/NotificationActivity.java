package cn.com.zhiwoo.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class NotificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        finish();
        if (!MainActivity.isAlive) {
            Intent launchIntent = getPackageManager().
                    getLaunchIntentForPackage("cn.com.zhiwoo");
            startActivity(launchIntent);
        }
    }
}
