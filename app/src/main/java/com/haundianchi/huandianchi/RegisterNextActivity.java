package com.haundianchi.huandianchi;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

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
