package com.haundianchi.huandianchi.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.haundianchi.huandianchi.R;

public class RegisterActivity extends Activity implements View.OnClickListener{
    private Button registerBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerBtn = (Button)findViewById(R.id.btn_register);
        registerBtn.setOnClickListener(this);

        init();
    }
    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btn_register:
                Intent intent = new Intent(this.getApplicationContext(),RegisterNextActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public void init(){
    }
}
