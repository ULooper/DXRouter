package com.dongxian.personal;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dongxian.dxrouter_api.DXRouter;

/**
 * @author DongXian
 * on 2022/7/26
 */
public class PersonalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        TextView tv = findViewById(R.id.tv);
        Button btn = findViewById(R.id.btn);

        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        tv.setText("get Bundle value:" + name);

        btn.setOnClickListener(v -> {
            DXRouter.getInstance().navigation("/app/activity1", PersonalActivity.this);
        });
    }
}