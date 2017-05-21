package com.haundianchi.huandianchi.ui.Indent;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.haundianchi.huandianchi.Http.VolleyListenerInterface;
import com.haundianchi.huandianchi.Http.VolleyRequest;
import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.adapter.IndentAdapter;
import com.haundianchi.huandianchi.adapter.TicketAdapter;
import com.haundianchi.huandianchi.model.IndentModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by blablabla on 2017/4/26.
 */

public class UnPayFragment extends Fragment implements View.OnClickListener
{
    private IndentAdapter adapter;
    private ArrayList<IndentModel> mIndentModels = new ArrayList<>();
    private ListView listView;
    CountDownTimer timer;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view=inflater.inflate(R.layout.unpay_fragment, container, false);
        init(view);
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

    public void init(View view){
        listView=(ListView) view.findViewById(R.id.lv_content);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(), IndentDetailActivity.class);
                intent.putExtra("orderNum",mIndentModels.get(position).getOrderNum());
                intent.putExtra("time",mIndentModels.get(position).getPayTimeRemain());
                intent.putExtra("status",mIndentModels.get(position).getStatus());
                getActivity().startActivity(intent);
            }
        });
        timer = new CountDownTimer(30*60*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                getData();
            }
            @Override
            public void onFinish() {

            }
        };
        //timer.start();
    }

    public void getData(){
        VolleyRequest.RequestGet(getActivity(), "/Order/list?type=0","1",
                new VolleyListenerInterface(getActivity(),VolleyListenerInterface.mListener,VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        try{
                            System.out.println("未支付 "+result);
                            JSONObject jsonObject = new JSONObject(result);
                            mIndentModels.clear();
                            if(jsonObject.get("code").toString().equals("200")){
                                JSONArray resultArray  = new JSONArray(jsonObject.get("result").toString());
                                for(int i=0;i<resultArray.length();i++){
                                    JSONObject data=resultArray.getJSONObject(i);
                                    IndentModel indentModel=new IndentModel();
                                    indentModel.setPrice(data.get("price").toString());
                                    indentModel.setStation((new JSONObject(data.get("station").toString())).get("address").toString());
                                    indentModel.setStatus(data.get("status").toString());
                                    indentModel.setOrderNum(data.get("orderNum").toString());
                                    if(data.get("status").toString().equals("0"))
                                        indentModel.setPayTimeRemain(data.get("payTimeRemain").toString());
                                    indentModel.setId(data.get("id").toString());
                                    mIndentModels.add(indentModel);
                                }
                                adapter=new IndentAdapter(getActivity(),mIndentModels);
                                listView.setAdapter(adapter);
                            }else if(jsonObject.get("code").toString().equals("400"))
                                Toast.makeText(getActivity(), jsonObject.get("error").toString(), Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onMyError(VolleyError error) {

                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
        System.out.println("onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}