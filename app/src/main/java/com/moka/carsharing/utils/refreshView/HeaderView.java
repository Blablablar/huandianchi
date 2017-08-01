package com.moka.carsharing.utils.refreshView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jwenfeng.library.pulltorefresh.view.HeadView;
import com.moka.carsharing.R;

/**
 * Created by blablabla on 2017/8/1.
 */

public class HeaderView extends FrameLayout implements HeadView {

//    private TextView tv;
//    private ImageView arrow;
    private ProgressBar progressBar;

    public HeaderView(Context context) {
        this(context,null);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_header,null);
        addView(view);
//        tv = (TextView) view.findViewById(R.id.header_tv);
//        arrow = (ImageView) view.findViewById(R.id.header_arrow);
        progressBar = (ProgressBar) view.findViewById(R.id.header_progress);
    }

    @Override
    public void begin() {

    }

    @Override
    public void progress(float progress, float all) {

    }

    @Override
    public void finishing(float progress, float all) {

    }

    @Override
    public void loading() {
        progressBar.setVisibility(VISIBLE);
    }

    @Override
    public void normal() {
        progressBar.setVisibility(GONE);
    }

    @Override
    public View getView() {
        return this;
    }
}