package com.moka.carsharing.ui.Indent;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.jwenfeng.library.pulltorefresh.ViewStatus;
import com.moka.carsharing.model.IndentModel;
import com.moka.carsharing.Http.VolleyListenerInterface;
import com.moka.carsharing.Http.VolleyRequest;
import com.moka.carsharing.R;
import com.moka.carsharing.adapter.IndentAdapter;
import com.moka.carsharing.cache.Order;
import com.moka.carsharing.utils.refreshView.MyPullToRefreshView;
import com.moka.carsharing.utils.refreshView.PullToRefreshView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.logging.Handler;

/**
 * Created by blablabla on 2017/4/26.
 */

public class PayedFragment extends Fragment implements View.OnClickListener
{
    private IndentAdapter adapter;
    private ArrayList<IndentModel> mIndentModels = new ArrayList<>();
    private ListView listView;
    View view;
    private MyPullToRefreshView mPullToRefreshView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view=inflater.inflate(R.layout.payed_fragment, container, false);
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
        //listView.setEmptyView(view.findViewById(R.id.scrollView_list));

        listView.setAdapter(adapter);
        //listView.setEmptyView(View.inflate(getActivity(),R.layout.payed_fragment_empty,null));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(), IndentDetailActivity.class);
                intent.putExtra("position",position);
                intent.putExtra("type",Order.getPayedList().get(position).getOrderType());
                getActivity().startActivity(intent);
            }
        });
        if(Order.getPayedList().size()!=0){
            adapter=new IndentAdapter(getActivity(),Order.getPayedList());
            listView.setAdapter(adapter);
        }
        mPullToRefreshView = (MyPullToRefreshView) view.findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                getData();
            }

            @Override
            public void loadMore() {
                mPullToRefreshView.finishLoadMore();
            }
        });
    }

    public void getData(){
        VolleyRequest.RequestGet(getActivity(), "/Order/list?type=1","2",
                new VolleyListenerInterface(getActivity(),VolleyListenerInterface.mListener,VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        try{
                            System.out.println("已完成 "+result);
                            JSONObject jsonObject = new JSONObject(result);
                            mIndentModels.clear();
                            Order.getPayedList().clear();
                            if(jsonObject.get("code").toString().equals("200")){
                                JSONArray resultArray  = new JSONArray(jsonObject.get("result").toString());
                                for(int i=0;i<resultArray.length();i++){
                                    JSONObject data=resultArray.getJSONObject(i);
                                    IndentModel indentModel=new IndentModel();
                                    indentModel.setPrice(data.get("price").toString());
                                    indentModel.setStation((new JSONObject(data.get("station").toString())).get("name").toString());
                                    indentModel.setStatus(data.get("status").toString());
                                    indentModel.setTradeTime(data.get("tradeTime").toString());
                                    indentModel.setOrderNum(data.get("orderNum").toString());
                                    indentModel.setOrderType(data.get("type").toString());
                                    indentModel.setElectricity(data.get("electricity").toString());
                                    indentModel.setElectricityOfBefore(data.get("electricityOfBefore").toString());
                                    indentModel.setPayType(data.get("payType").toString());
                                    //indentModel.setBatteryModel((new JSONObject(data.get("station").toString())).get("model").toString());
                                    if(!data.isNull("actualPrice"))
                                        indentModel.setActualPrice(data.getString("actualPrice"));
                                    if(!data.isNull("promotionCode")){
                                        JSONObject jsonObject1=new JSONObject(data.getString("promotionCode"));
                                        indentModel.setYouhuiPrice(jsonObject1.getString("discount"));
                                    }
                                    mIndentModels.add(indentModel);
                                    Order.getPayedList().add(indentModel);
                                }
                                adapter=new IndentAdapter(getActivity(),mIndentModels);
                                listView.setAdapter(adapter);
                                mPullToRefreshView.finishRefresh();
                                if(listView.getCount()==0){
                                    mPullToRefreshView.showView(ViewStatus.EMPTY_STATUS);
                                }
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
        System.out.println("onResume");
        getData();
    }

}