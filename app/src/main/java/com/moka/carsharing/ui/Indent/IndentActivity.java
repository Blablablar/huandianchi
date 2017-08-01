package com.moka.carsharing.ui.Indent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.model.Text;
import com.moka.carsharing.ui.HomePageActivity;
import com.moka.carsharing.R;

/**
 * Created by blablabla on 2017/4/20.
 */

public class IndentActivity extends Activity implements View.OnClickListener{
    private ImageButton backBtn;
    private UnPayFragment mTab01;
    private PayedFragment mTab02;
    private LinearLayout mUnPayll;
    private LinearLayout mPayedll;
    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indent);
        fragmentManager=getFragmentManager();
        backBtn = (ImageButton)findViewById(R.id.btn_back);
        backBtn.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btn_back:
                Intent intent=new Intent(this, HomePageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Activity","Indent");
                startActivity(intent);
                break;
            case R.id.btn_confirm:
                Intent intent2 = new Intent(this.getApplicationContext(),IndentConfirmActivity.class);
                startActivity(intent2);
                break;
            case R.id.ll_unpay:
                setTabSelection(0);
                break;
            case R.id.ll_payed:
                setTabSelection(1);
                break;
            default:
                break;
        }
    }

    public void init(){
        mUnPayll = (LinearLayout) findViewById(R.id.ll_unpay);
        mPayedll = (LinearLayout) findViewById(R.id.ll_payed);
        mUnPayll.setOnClickListener(this);
        mPayedll.setOnClickListener(this);
        mUnPayll.callOnClick();
        ((TextView)findViewById(R.id.tv_title)).setText("订单查询");
        if(getIntent().getStringExtra("fragment").equals("unpay"))
            setTabSelection(0);
        else
            setTabSelection(1);
    }
    private void setTabSelection(int index)
    {
        // 重置按钮
        resetBtn();
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index)
        {
            case 0:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                ((ImageView) mUnPayll.findViewById(R.id.iv_unpay))
                        .setImageResource(R.mipmap.weizhifu_touch);
                if (mTab01 == null)
                {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    mTab01 = new UnPayFragment();
                    transaction.add(R.id.id_content, mTab01);
                } else
                {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(mTab01);
                }
                break;
            case 1:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                ((ImageView) mPayedll.findViewById(R.id.iv_payed))
                        .setImageResource(R.mipmap.yiwancheng_touch);
                if (mTab02 == null)
                {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    mTab02 = new PayedFragment();
                    transaction.add(R.id.id_content, mTab02);
                } else
                {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(mTab02);
                }
                break;
            default:
                break;
        }
        transaction.commit();
    }

    /**
     * 清除掉所有的选中状态。
     */
    private void resetBtn()
    {
        ((ImageView) mUnPayll.findViewById(R.id.iv_unpay))
                .setImageResource(R.mipmap.weizhifu_untouch);
        ((ImageView) mPayedll.findViewById(R.id.iv_payed))
                .setImageResource(R.mipmap.yiwancheng_untouch);
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction
     *            用于对Fragment执行操作的事务
     */
    @SuppressLint("NewApi")
    private void hideFragments(FragmentTransaction transaction)
    {
        if (mTab01 != null)
        {
            transaction.hide(mTab01);
        }
        if (mTab02 != null)
        {
            transaction.hide(mTab02);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent=new Intent(this, HomePageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("Activity","Indent");
            startActivity(intent);
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }


//    public void setEmptyHintVisible(boolean visible,String content) {
//        LinearLayout linearLayout=(LinearLayout)findViewById(R.id.ll_order_hint);
//        if(visible){
//            linearLayout.setVisibility(View.VISIBLE);
//            TextView textView=(TextView) linearLayout.findViewById(R.id.tv_hint);
//            textView.setText(content);
//        }
//        else
//            linearLayout.setVisibility(View.INVISIBLE);
//    }
}
