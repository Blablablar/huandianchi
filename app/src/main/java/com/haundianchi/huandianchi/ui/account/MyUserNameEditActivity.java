package com.haundianchi.huandianchi.ui.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.utils.ActivityBuilder;
import com.haundianchi.huandianchi.widget.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyUserNameEditActivity extends AppCompatActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.edit_username)
    EditText mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_user_name_edit);
        ButterKnife.bind(this);
    }
}

class MyUserNameEditActivityBuilder extends ActivityBuilder {
    public MyUserNameEditActivityBuilder(Context context) {
        super(context);
    }

    @Override
    public Intent create() {
        return new Intent(getContext(), MyUserNameEditActivity.class);
    }
}
