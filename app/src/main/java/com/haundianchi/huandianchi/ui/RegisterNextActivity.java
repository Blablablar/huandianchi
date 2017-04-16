package com.haundianchi.huandianchi.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.haundianchi.huandianchi.R;

public class RegisterNextActivity extends Activity implements View.OnClickListener{
    private ImageButton backBtn;
    private Button submitBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_next);
        backBtn=(ImageButton)findViewById(R.id.btn_back);
        submitBtn=(Button)findViewById(R.id.btn_submit);
        backBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_submit:
                Intent intent = new Intent(this.getApplicationContext(),HomePageActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
