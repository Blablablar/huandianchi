package com.haundianchi.huandianchi;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
        View view=findViewById(R.id.btn_back);
        view.setVisibility(View.INVISIBLE);
    }
}
