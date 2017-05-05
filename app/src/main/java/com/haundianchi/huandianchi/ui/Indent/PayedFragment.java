package com.haundianchi.huandianchi.ui.Indent;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.haundianchi.huandianchi.R;

/**
 * Created by blablabla on 2017/4/26.
 */

public class PayedFragment extends Fragment implements View.OnClickListener
{
    ImageView ll_1;
    ImageView ll_2;
    ImageView ll_3;
    ImageView ll_4;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.payed_fragment, container, false);
        init(view);
        return view;
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.imageView1:
            case R.id.imageView2:
            case R.id.imageView3:
            case R.id.imageView4:
                Intent intent = new Intent(getActivity(),IndentDetailActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public void init(View view){
        ll_1=(ImageView)view.findViewById(R.id.imageView1);
        ll_2=(ImageView)view.findViewById(R.id.imageView2);
        ll_3=(ImageView)view.findViewById(R.id.imageView3);
        ll_4=(ImageView)view.findViewById(R.id.imageView4);

        ll_1.setOnClickListener(this);
        ll_2.setOnClickListener(this);
        ll_3.setOnClickListener(this);
        ll_4.setOnClickListener(this);
    }
}