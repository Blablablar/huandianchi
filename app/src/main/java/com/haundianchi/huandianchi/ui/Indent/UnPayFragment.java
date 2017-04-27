package com.haundianchi.huandianchi.ui.Indent;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.haundianchi.huandianchi.R;

/**
 * Created by blablabla on 2017/4/26.
 */

public class UnPayFragment extends Fragment implements View.OnClickListener
{
    private Button confirmBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.unpay_fragment, container, false);
        confirmBtn=(Button)view.findViewById(R.id.btn_confirm);
        confirmBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btn_confirm:
                Intent intent2 = new Intent(getActivity(),IndentConfirmActivity.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }
}