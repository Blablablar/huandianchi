package com.haundianchi.huandianchi.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.haundianchi.huandianchi.R;

/**
 * Created by blablabla on 2017/4/11.
 */

public class LoginActivity extends Activity implements View.OnClickListener{
    private Button loginBtn;
    private TextView registerTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginBtn = (Button)findViewById(R.id.btn_login);
        registerTv=(TextView)findViewById(R.id.tv_register);
        loginBtn.setOnClickListener(this);
        registerTv.setOnClickListener(this);
        init();
    }
    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btn_login:
                Intent intent1 = new Intent(this.getApplicationContext(),HomePageActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_register:
                Intent intent2 = new Intent(this.getApplicationContext(),RegisterActivity.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }

    public void init(){
    }
}