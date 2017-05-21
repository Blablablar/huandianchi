package com.haundianchi.huandianchi.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps.model.Text;
import com.haundianchi.huandianchi.Http.VolleyRequest;
import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.model.IndentModel;
import com.haundianchi.huandianchi.ui.HomePageActivity;
import com.haundianchi.huandianchi.ui.Indent.IndentActivity;
import com.haundianchi.huandianchi.ui.Indent.IndentConfirmActivity;
import com.haundianchi.huandianchi.ui.Indent.IndentDetailActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.view.View.GONE;

/**
 * Created by blablabla on 2017/5/13.
 */

public class IndentAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<IndentModel> models;
    private int type = 0;//0不显示选择框

    private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局  /*构造函数*/

    public IndentAdapter(Context context,ArrayList<IndentModel> models) {
        this.context=context;
        this.mInflater = LayoutInflater.from(context);
        this.models = models;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null)
        {
            convertView = mInflater.inflate(R.layout.list_item_dingdan, null);
        }
        TextView stationName = (TextView)convertView.findViewById(R.id.tv_station_name);
        TextView status = (TextView)convertView.findViewById(R.id.tv_status);
        TextView dianliang=(TextView)convertView.findViewById(R.id.tv_dianliang);
        TextView sumPrice=(TextView)convertView.findViewById(R.id.tv_sum);
        TextView tradeTime=(TextView)convertView.findViewById(R.id.tv_time);
        final TextView countDown=(TextView)convertView.findViewById(R.id.tv_count_down);
        Button button=(Button)convertView.findViewById(R.id.btn_pay);
        button.setEnabled(false);
        button.setVisibility(GONE);
        stationName.setText(models.get(position).getStation());
        status.setTextColor(Color.rgb(141, 141, 141));
        if(models.get(position).getStatus().equals("0")) {
            dianliang.setText("距离预约失效还有");
            status.setText("预约中");
            status.setTextColor(Color.rgb(126, 195, 14));
            //倒计时
            int time = Integer.parseInt(models.get(position).getPayTimeRemain());
            final String hourStr;
            final String minStr;
            if(time/60<10)
                hourStr="0"+time/60;
            else
                hourStr=time/60+"";
            if(time%60<10)
                minStr="0"+time%60;
            else
                minStr=time%60+"";
            countDown.setText(hourStr+":"+minStr);
            button.setVisibility(View.VISIBLE);
            button.setBackgroundColor(Color.rgb(191, 191, 191));
        }
        else if(models.get(position).getStatus().equals("1"))
        {
            dianliang.setText("电站已成功换电，请前去支付");
            dianliang.setTextColor(Color.rgb(255, 0, 0));
            status.setText("待支付");
            status.setTextColor(Color.rgb(126, 195, 14));
            button.setVisibility(View.VISIBLE);
            button.setEnabled(true);
            countDown.setText("");
        }
        else if(models.get(position).getStatus().equals("2")){
            dianliang.setText("订单已取消，请重新预约");
            status.setText("已取消");
            button.setVisibility(GONE);
            countDown.setText("");
        }
        else if(models.get(position).getStatus().equals("3")){
            status.setText("已支付");
            button.setVisibility(GONE);
            long time = Long.parseLong(models.get(position).getTradeTime());
            Date date = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(date);
            tradeTime.setText(dateStr);
            tradeTime.setTextColor(Color.rgb(191, 191, 191));
            countDown.setText("");
        }
        else if(models.get(position).getStatus().equals("4")){
            long time = Long.parseLong(models.get(position).getTradeTime());
            Date date = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(date);
            tradeTime.setText(dateStr);
            tradeTime.setTextColor(Color.rgb(191, 191, 191));
            button.setVisibility(GONE);
            status.setText("已开票");
            countDown.setText("");
        }
        sumPrice.setText("合计：¥"+models.get(position).getPrice()+"元");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, IndentConfirmActivity.class);
                intent.putExtra("id",models.get(position).getId());
                intent.putExtra("orderNum",models.get(position).getOrderNum());
                intent.putExtra("orderType","0");
                intent.putExtra("price",models.get(position).getPrice());
                context.startActivity(intent);
            }
        });
        return convertView;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }


}