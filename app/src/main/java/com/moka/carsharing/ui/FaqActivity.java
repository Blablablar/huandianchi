package com.moka.carsharing.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moka.carsharing.R;

/**
 * Created by blablabla on 2017/4/30.
 */

public class FaqActivity extends Activity implements View.OnClickListener{
    private ImageButton backBtn;
    private RelativeLayout mFaqRl1;
    private RelativeLayout mFaqRl2;
    private RelativeLayout mFaqRl3;
    private TextView mFaqTv1;
    private TextView mFaqTv2;
    private TextView mFaqTv3;
    private ImageView mFaqIv1;
    private ImageView mFaqIv2;
    private ImageView mFaqIv3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
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
            case R.id.rl_faq_1:
                if (getIndex()==1){
                    mFaqTv1.setVisibility(View.GONE);
                    mFaqIv1.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.mipmap.arrow_down));
                }else
                {
                    retrieve();
                    mFaqTv1.setVisibility(View.VISIBLE);
                    mFaqIv1.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.mipmap.arrow_up));
                }
                break;
            case R.id.rl_faq_2:
                if (getIndex()==2){
                    mFaqTv2.setVisibility(View.GONE);
                    mFaqIv2.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.mipmap.arrow_down));
                }else
                {
                    retrieve();
                    mFaqTv2.setVisibility(View.VISIBLE);
                    mFaqIv2.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.mipmap.arrow_up));
                }
                break;
            case R.id.rl_faq_3:
                if (getIndex()==3){
                    mFaqTv3.setVisibility(View.GONE);
                    mFaqIv3.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.mipmap.arrow_down));
                }else
                {
                    retrieve();
                    mFaqTv3.setVisibility(View.VISIBLE);
                    mFaqIv3.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.mipmap.arrow_up));
                }
                break;
            default:
                break;
        }
    }
    public void init(){
        ((TextView)findViewById(R.id.tv_title)).setText("常见问题");

        mFaqRl1=(RelativeLayout)findViewById(R.id.rl_faq_1);
        mFaqRl2=(RelativeLayout)findViewById(R.id.rl_faq_2);
        mFaqRl3=(RelativeLayout)findViewById(R.id.rl_faq_3);

        mFaqRl1.setOnClickListener(this);
        mFaqRl2.setOnClickListener(this);
        mFaqRl3.setOnClickListener(this);

        mFaqTv1=(TextView)findViewById(R.id.tv_faq_1);
        mFaqTv2=(TextView)findViewById(R.id.tv_faq_2);
        mFaqTv3=(TextView)findViewById(R.id.tv_faq_3);

        mFaqIv1=(ImageView)findViewById(R.id.iv_faq_1);
        mFaqIv2=(ImageView)findViewById(R.id.iv_faq_2);
        mFaqIv3=(ImageView)findViewById(R.id.iv_faq_3);
    }

    public void retrieve(){
        mFaqTv1.setVisibility(View.GONE);
        mFaqTv2.setVisibility(View.GONE);
        mFaqTv3.setVisibility(View.GONE);

        mFaqIv1.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.mipmap.arrow_down));
        mFaqIv2.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.mipmap.arrow_down));
        mFaqIv3.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.mipmap.arrow_down));
    }

    public int getIndex(){
        if(mFaqTv1.getVisibility()!=View.GONE){
            return 1;
        }else  if(mFaqTv2.getVisibility()!=View.GONE){
            return 2;
        }else  if(mFaqTv3.getVisibility()!=View.GONE){
            return 3;
        }else
            return -1;
    }
}
