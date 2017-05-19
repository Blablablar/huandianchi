package com.haundianchi.huandianchi.ui.Indent;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.haundianchi.huandianchi.Http.VolleyListenerInterface;
import com.haundianchi.huandianchi.Http.VolleyRequest;
import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.adapter.IndentAdapter;
import com.haundianchi.huandianchi.model.IndentModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by blablabla on 2017/4/26.
 */

public class PayedFragment extends Fragment implements View.OnClickListener
{
    private IndentAdapter adapter;
    private ArrayList<IndentModel> mIndentModels = new ArrayList<>();
    private ListView listView;
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
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(), IndentDetailActivity.class);
                intent.putExtra("orderNum",mIndentModels.get(position).getOrderNum());
                intent.putExtra("status",mIndentModels.get(position).getStatus());
                getActivity().startActivity(intent);
            }
        });
    }

    public void getData(){
        VolleyRequest.RequestGet(getActivity(), "/Order/list?type=1","2",
                new VolleyListenerInterface(getActivity(),VolleyListenerInterface.mListener,VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        try{
                            System.out.println(result);
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
                                    indentModel.setTradeTime(data.get("tradeTime").toString());
                                    indentModel.setOrderNum(data.get("orderNum").toString());
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
    }

}