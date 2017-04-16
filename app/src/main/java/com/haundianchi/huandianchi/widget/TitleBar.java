package com.haundianchi.huandianchi.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.haundianchi.huandianchi.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Burgess on 2017/4/11.
 */

public class TitleBar extends FrameLayout {
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.btn_back)
    ImageButton mBack;

    private boolean showBack;

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.header, this);
        if (view.isInEditMode()) { return; }
        ButterKnife.bind(this, view);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TitleBar, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.TitleBar_titleText:
                    mTitle.setText(a.getString(attr));
                    break;
                case R.styleable.TitleBar_isShowBack:
                    showBack = a.getBoolean(attr, false);
                    mBack.setVisibility(showBack ? VISIBLE : GONE);
                    break;
            }

        }
        a.recycle();
    }

    private void showBack(final Activity activity, OnClickListener listener) {
        if (listener == null) {
            listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                }
            };
        }
    }

    public void bindActivity(final Activity activity) {
        bindActivity(activity, null);
    }

    public void bindActivity(final Activity activity, OnClickListener listener) {
        //setTitle(activity.getTitle());
        if (showBack) {
            showBack(activity, listener);
        }
    }

    public void setTitle(CharSequence title) {
        mTitle.setText(title);
    }
}
