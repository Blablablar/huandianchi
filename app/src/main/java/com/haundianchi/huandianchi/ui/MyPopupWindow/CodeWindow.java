package com.haundianchi.huandianchi.ui.MyPopupWindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.haundianchi.huandianchi.R;

/**
 * Created by blablabla on 2017/4/30.
 */

public class CodeWindow extends PopupWindow {
    private View mView;
    public CodeWindow(Activity context, View.OnClickListener itemsOnClick){
        super(context);
        initView(context,itemsOnClick);
    }

    private void initView(final Activity context,View.OnClickListener itemsOnClick) {
        // TODO Auto-generated method stub
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = mInflater.inflate(R.layout.window_code,null);
        ImageView mQuitIv = (ImageView) mView.findViewById(R.id.iv_quit);
        Button mSubmitBtn = (Button) mView.findViewById(R.id.btn_submit);

//        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//                //销毁弹出框
//                //dismiss();
//                backgroundAlpha(context, 1f);
//            }
//        });
        //设置按钮监听
        mQuitIv.setOnClickListener(itemsOnClick);
        mSubmitBtn.setOnClickListener(itemsOnClick);
        //设置SelectPicPopupWindow的View
        this.setContentView(mView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(WindowManager.LayoutParams.FILL_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置PopupWindow可触摸
        this.setTouchable(true);
        //设置非PopupWindow区域是否可触摸
//        this.setOutsideTouchable(false);
        //设置SelectPicPopupWindow弹出窗体动画效果
        //this.setAnimationStyle(R.style.CallWindow);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        backgroundAlpha(context,0.5f);//0.0-1.0
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                backgroundAlpha(context, 1f);
            }
        });
    }
    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(Activity context, float bgAlpha)
    {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    public String getCode() {
        EditText mCodeEt = (EditText) mView.findViewById(R.id.et_code);
        return mCodeEt.getText().toString();
    }
}
