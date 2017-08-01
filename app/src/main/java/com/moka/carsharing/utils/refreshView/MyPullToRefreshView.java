package com.moka.carsharing.utils.refreshView;

import android.content.Context;
import android.util.AttributeSet;

import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;

/**
 * Created by blablabla on 2017/8/1.
 */

public class MyPullToRefreshView extends PullToRefreshLayout {

    public MyPullToRefreshView(Context context) {
        super(context);
        initView(context);
    }

    public MyPullToRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MyPullToRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        setHeaderView(new HeaderView(context));
        //setHeaderView(new NormalHeadView(context));
    }
}
