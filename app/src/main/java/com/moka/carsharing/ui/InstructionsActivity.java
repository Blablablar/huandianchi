package com.moka.carsharing.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.moka.carsharing.R;

/**
 * Created by blablabla on 2017/4/30.
 */

public class InstructionsActivity extends Activity implements View.OnClickListener{
    private ImageButton backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_instruction);
        backBtn = (ImageButton)findViewById(R.id.btn_back);
        backBtn.setOnClickListener(this);
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }
    }
    public void init(){
        ((TextView)findViewById(R.id.tv_title)).setText("使用说明");
    }
}
