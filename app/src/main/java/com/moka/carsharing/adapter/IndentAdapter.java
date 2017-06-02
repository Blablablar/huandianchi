package com.moka.carsharing.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.moka.carsharing.model.IndentModel;
import com.moka.carsharing.ui.Indent.IndentConfirmActivity;
import com.moka.carsharing.R;

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
    private int time;
    private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局  /*构造函数*/

    public int getTime(){
        return time;
    }
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
        ViewHolder holder = null;
        if(convertView == null)
        {
            convertView = mInflater.inflate(R.layout.list_item_dingdan, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.stationName = (TextView)convertView.findViewById(R.id.tv_station_name);
        holder.status = (TextView)convertView.findViewById(R.id.tv_status);
        holder.dianliang=(TextView)convertView.findViewById(R.id.tv_dianliang);
        holder.dianliang.setTextColor(Color.rgb(51, 51, 51));
        holder.sumPrice=(TextView)convertView.findViewById(R.id.tv_sum);
        holder.tradeTime=(TextView)convertView.findViewById(R.id.tv_time);
        holder.button=(Button)convertView.findViewById(R.id.btn_pay);
        holder.button.setEnabled(false);
        holder.button.setVisibility(GONE);
        holder.stationName.setText(models.get(position).getStation());
        holder.status.setTextColor(Color.rgb(141, 141, 141));
        holder.countDown=(TextView)convertView.findViewById(R.id.tv_count_down);
        //重制UI
        holder.tradeTime.setText("");
        holder.countDown.setText("");
        holder.sumPrice.setText("");
        if(models.get(position).getStatus().equals("0")) {
            holder.sumPrice.setText("合计：¥"+models.get(position).getPrice()+"元");
            holder.dianliang.setText("距离预约失效还有");
            holder.status.setText("预约中");
            holder.status.setTextColor(Color.rgb(126, 195, 14));
            //倒计时
            time = Integer.parseInt(models.get(position).getPayTimeRemain());
            String hourStr;
            String minStr;
            if(time>0)
            {
                if(time/60<10)
                    hourStr="0"+time/60;
                else
                    hourStr=time/60+"";
                if(time%60<10)
                    minStr="0"+time%60;
                else
                    minStr=time%60+"";
                holder.countDown.setText(hourStr+":"+minStr);
            }
            holder.button.setVisibility(View.VISIBLE);
            holder.button.setBackgroundColor(Color.rgb(191, 191, 191));
        }
        else if(models.get(position).getStatus().equals("1"))
        {
            holder.dianliang.setText("电站已成功换电，请前去支付");
            holder.dianliang.setTextColor(Color.rgb(255, 0, 0));
            holder.status.setText("待支付");
            holder.status.setTextColor(Color.rgb(126, 195, 14));
            holder.button.setVisibility(View.VISIBLE);
            holder.button.setEnabled(true);
            //countDown.setText("");
            holder.sumPrice.setText("合计：¥"+models.get(position).getPrice()+"元");
        }
        else if(models.get(position).getStatus().equals("2")){
            holder.dianliang.setText("订单已取消，请重新预约");
            holder.status.setText("已取消");
            holder.button.setVisibility(GONE);
            //countDown.setText("");
            //订单取消时间
            long time = Long.parseLong(models.get(position).getTradeTime());
            Date date = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(date);
            holder.tradeTime.setText("取消时间："+dateStr);
            //tradeTime.setTextColor(Color.rgb(191, 191, 191));
        }
        else if(models.get(position).getStatus().equals("3")){
            holder.status.setText("已支付");
            holder.button.setVisibility(GONE);
            long time = Long.parseLong(models.get(position).getTradeTime());
            Date date = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(date);
            holder.tradeTime.setText(""+dateStr);
            holder.tradeTime.setTextColor(Color.rgb(191, 191, 191));
            //countDown.setText("");
        }
        else if(models.get(position).getStatus().equals("4")){
            long time = Long.parseLong(models.get(position).getTradeTime());
            Date date = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(date);
            holder.tradeTime.setText(dateStr);
            holder.tradeTime.setTextColor(Color.rgb(191, 191, 191));
            holder.button.setVisibility(GONE);
            holder.status.setTextColor(Color.rgb(126, 195, 14));
            holder.status.setText("已开票");
            //countDown.setText("");
        }
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, IndentConfirmActivity.class);
                intent.putExtra("id",models.get(position).getId());
                intent.putExtra("orderNum",models.get(position).getOrderNum());
                intent.putExtra("orderType","0");
                intent.putExtra("position",position);
                intent.putExtra("price",models.get(position).getPrice());
                intent.putExtra("stationName",models.get(position).getStation());
                intent.putExtra("batteryModel",models.get(position).getBatteryModel());
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

    public static class ViewHolder {
        public TextView stationName;
        public TextView status;
        public TextView dianliang;
        public TextView sumPrice;
        public TextView tradeTime;
        public TextView countDown;
        public Button button;
    }
}