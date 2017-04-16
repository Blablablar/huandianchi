package com.haundianchi.huandianchi.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Burgess on 2017/4/16.
 */

public abstract class ActivityBuilder {
    private Context context;

    public ActivityBuilder(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    protected Intent createIntent(Class<?> cls) {
        return new Intent(getContext(), cls);
    }

    public abstract Intent create();

    public void start() {
        getContext().startActivity(create());
    }

    public void startForResult(Activity activity, int requestCode) {
        activity.startActivityForResult(create(), requestCode);
    }
}