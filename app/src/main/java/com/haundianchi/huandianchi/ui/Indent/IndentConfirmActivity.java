package com.haundianchi.huandianchi.ui.Indent;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.ui.MyPopupWindow.CodeWindow;
import com.haundianchi.huandianchi.ui.MyPopupWindow.MyPopupWindow;

/**
 * Created by blablabla on 2017/4/24.
 */

public class IndentConfirmActivity extends Activity implements View.OnClickListener{
    private ImageButton backBtn;
    private RelativeLayout mCodeRl;
    CodeWindow codeWindow;
    private Activity mActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indent_confirm);
        backBtn = (ImageButton)findViewById(R.id.btn_back);
        backBtn.setOnClickListener(this);
        init();
    }
    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.rl_code:
                codeWindow = new CodeWindow(this,itemsOnClick);
                codeWindow.showAtLocation(findViewById(R.id.rl_indent_confirm),
                        Gravity.CENTER, 0, 0);
                break;
            default:
                break;
        }
    }
    public void init(){
        ((TextView)findViewById(R.id.tv_title)).setText("确认订单");
        mCodeRl=(RelativeLayout) findViewById(R.id.rl_code);
        mCodeRl.setOnClickListener(this);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener(){
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_quit:
                    codeWindow.dismiss();
                    break;
                case R.id.btn_submit:
                    codeWindow.dismiss();
                    break;
                default:
                    break;
            }
        }

    };
}
