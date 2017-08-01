package com.moka.carsharing.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.TextViewCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.iflytek.cloud.thirdparty.L;
import com.moka.carsharing.R;
import com.moka.carsharing.data.UserInfo;
import com.moka.carsharing.ui.CarInfoActivity;
import com.moka.carsharing.ui.FaqActivity;
import com.moka.carsharing.ui.HomePageActivity;
import com.moka.carsharing.ui.Indent.IndentActivity;
import com.moka.carsharing.ui.InstructionsActivity;
import com.moka.carsharing.ui.LoginActivity;
import com.moka.carsharing.ui.PhoneActivity;
import com.moka.carsharing.ui.position.CarPositionActivity;
import com.moka.carsharing.ui.tickets.HistoryTicketsActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by blablabla on 2017/7/24.
 */

public class MenuItemAdapter extends BaseAdapter
{
    private final int mIconSize;
    private LayoutInflater mInflater;
    private Context mContext;

    public MenuItemAdapter(Context context)
    {
        mInflater = LayoutInflater.from(context);
        mContext = context;

        mIconSize = context.getResources().getDimensionPixelSize(R.dimen.drawer_icon_size);//24dp
    }

    private List<LvMenuItem> mItems = new ArrayList<LvMenuItem>(
            Arrays.asList(
                    new LvMenuItem(R.mipmap.dianzhan, "电站预约"),
                    new LvMenuItem(R.mipmap.dingdan, "订单查询"),
                    new LvMenuItem(R.mipmap.fapiao, "我的发票"),
                    new LvMenuItem(R.mipmap.car_info, "车辆信息"),
                    new LvMenuItem(),
                    new LvMenuItem(R.mipmap.instruction,"使用说明"),
                    new LvMenuItem(R.mipmap.problem, "常见问题"),
                    new LvMenuItem(R.mipmap.phone, "客服电话")
                    //,
                    //new LvMenuItem(0)
            ));


    @Override
    public int getCount()
    {
        return mItems.size();
    }


    @Override
    public Object getItem(int position)
    {
        return mItems.get(position);
    }


    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getViewTypeCount()
    {
        return 3;
    }

    @Override
    public int getItemViewType(int position)
    {
        return mItems.get(position).type;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LvMenuItem item = mItems.get(position);
        switch (item.type)
        {
            case LvMenuItem.TYPE_NORMAL:
                if (convertView == null)
                {
                    convertView = mInflater.inflate(R.layout.design_drawer_item, parent,
                            false);
                }
                TextView itemView = (TextView) convertView.findViewById(R.id.tv_menu_item);
                itemView.setText(item.name);
                Drawable icon = mContext.getResources().getDrawable(item.icon);
                setIconColor(icon);
                if (icon != null)
                {
                    icon.setBounds(0, 0, mIconSize, mIconSize);
                    TextViewCompat.setCompoundDrawablesRelative(itemView, icon, null, null, null);
                }

                break;
            case LvMenuItem.TYPE_NO_ICON:
                if (convertView == null)
                {
                    convertView = mInflater.inflate(R.layout.design_drawer_item_subheader,
                            parent, false);
                }
                TextView subHeader = (TextView) convertView;
                subHeader.setText(item.name);
                break;
            case LvMenuItem.TYPE_SEPARATOR:
                if (convertView == null)
                {
                    convertView = mInflater.inflate(R.layout.design_drawer_item_separator,
                            parent, false);
                }
                break;
            case LvMenuItem.TYPE_BUTTON:
                if (convertView == null)
                {
                    convertView = mInflater.inflate(R.layout.navigation_button,
                            parent, false);
                }
                break;
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position){
                    case 0:
                        Intent intent1=new Intent(mContext,CarPositionActivity.class);
                        mContext.startActivity(intent1);
                        break;
                    case 1:
                        Intent intent2=new Intent(mContext,IndentActivity.class);
                        intent2.putExtra("fragment","unpay");
                        mContext.startActivity(intent2);
                        break;
                    case 2:
                        Intent intent3=new Intent(mContext,HistoryTicketsActivity.class);
                        mContext.startActivity(intent3);
                        break;
                    case 3:
                        Intent intent4=new Intent(mContext,CarInfoActivity.class);
                        mContext.startActivity(intent4);
                        break;
                    case 5:
                        Intent intent5=new Intent(mContext,InstructionsActivity.class);
                        mContext.startActivity(intent5);
                        break;
                    case 6:
                        Intent intent6=new Intent(mContext,FaqActivity.class);
                        mContext.startActivity(intent6);
                        break;
                    case 7:
                        Intent intent7=new Intent(mContext,PhoneActivity.class);
                        mContext.startActivity(intent7);
                        break;
                    default:
                        break;
                }
            }
        });
        return convertView;
    }

    public void setIconColor(Drawable icon)
    {
        int textColorSecondary = android.R.attr.textColorSecondary;
        TypedValue value = new TypedValue();
        if (!mContext.getTheme().resolveAttribute(textColorSecondary, value, true))
        {
            return;
        }
        int baseColor = mContext.getResources().getColor(value.resourceId);
        icon.setColorFilter(baseColor, PorterDuff.Mode.MULTIPLY);
    }
}