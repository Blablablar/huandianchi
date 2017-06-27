package com.moka.carsharing.ui.Indent;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.moka.carsharing.model.IndentModel;
import com.moka.carsharing.Http.VolleyListenerInterface;
import com.moka.carsharing.Http.VolleyRequest;
import com.moka.carsharing.R;
import com.moka.carsharing.adapter.IndentAdapter;
import com.moka.carsharing.cache.Order;
import com.moka.carsharing.utils.refreshView.PullToRefreshView;

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
    boolean isCountDown=false;
    View view;
    private PullToRefreshView mPullToRefreshView;
    public static final int REFRESH_DELAY = 5000;
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
                intent.putExtra("type",Order.getUnPayList().get(position).getOrderType());
                intent.putExtra("position",position);
                intent.putExtra("time",Order.getUnPayList().get(0).getPayTimeRemain());
                getActivity().startActivity(intent);
            }
        });
        timer = new CountDownTimer(30*60*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(Integer.valueOf(mIndentModels.get(0).getPayTimeRemain())>0){
                    //System.out.println(mIndentModels.get(0).getPayTimeRemain());
                    mIndentModels.get(0).setPayTimeRemain(Integer.valueOf(mIndentModels.get(0).getPayTimeRemain())-1+"");
                    adapter.notifyDataSetChanged();
                }else
                    timer.cancel();
            }
            @Override
            public void onFinish() {

            }
        };
        if(Order.getUnPayList().size()!=0){
            adapter=new IndentAdapter(getActivity(),Order.getUnPayList());
            listView.setAdapter(adapter);
        }
        mPullToRefreshView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(getActivity(), "当前网络较差，请稍后重试", Toast.LENGTH_SHORT).show();
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, REFRESH_DELAY);
                getData();
            }
        });
    }

    public void getData(){
        VolleyRequest.RequestGet(getActivity(), "/Order/list?type=0","1",
                new VolleyListenerInterface(getActivity(),VolleyListenerInterface.mListener,VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        try{
                            System.out.println("未支付 "+result);
                            JSONObject jsonObject = new JSONObject(result);
                            timer.cancel();
                            mIndentModels.clear();
                            Order.getUnPayList().clear();
                            isCountDown=false;
                            if(jsonObject.get("code").toString().equals("200")){
                                JSONArray resultArray  = new JSONArray(jsonObject.get("result").toString());
                                for(int i=0;i<resultArray.length();i++){
                                    JSONObject data=resultArray.getJSONObject(i);
                                    IndentModel indentModel=new IndentModel();
                                    indentModel.setPrice(data.get("price").toString());
                                    indentModel.setStation((new JSONObject(data.get("station").toString())).get("name").toString());
                                    indentModel.setBatteryModel((new JSONObject(data.get("station").toString())).get("model").toString());
                                    indentModel.setValidCount((new JSONObject(data.get("station").toString())).get("validCount").toString());
                                    indentModel.setStatus(data.get("status").toString());
                                    indentModel.setOrderNum(data.get("orderNum").toString());
                                    indentModel.setOrderType(data.get("type").toString());
                                    if(data.get("status").toString().equals("2"))
                                        indentModel.setTradeTime(data.get("ackTime").toString());
                                    else if(data.get("status").toString().equals("1")){
                                        indentModel.setElectricity(data.get("electricity").toString());
                                        indentModel.setElectricityOfBefore(data.get("electricityOfBefore").toString());
                                    }
                                    else if(data.get("status").toString().equals("0")){
                                        indentModel.setTradeTime(data.get("appointTime").toString());
                                        indentModel.setPayTimeRemain(data.get("payTimeRemain").toString());
                                        isCountDown=true;
                                    }
                                    indentModel.setId(data.get("id").toString());
                                    mIndentModels.add(indentModel);
                                    Order.getUnPayList().add(indentModel);
                                }
                                adapter=new IndentAdapter(getActivity(),mIndentModels);
                                listView.setAdapter(adapter);
                                if(isCountDown)
                                    timer.start();
                                System.out.println("收起");
                                mPullToRefreshView.setRefreshing(false);
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