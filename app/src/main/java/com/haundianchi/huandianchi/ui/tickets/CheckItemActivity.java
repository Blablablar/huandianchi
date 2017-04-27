package com.haundianchi.huandianchi.ui.tickets;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.ui.Indent.IndentConfirmActivity;

/**
 * Created by blablabla on 2017/4/24.
 */

public class CheckItemActivity extends Activity implements View.OnClickListener {
    private ImageButton backBtn;
    private Button confirmBtn;

//    private List<Item> itemList;
//    private DraftDailyAdapter adapter;
//    private Map<Integer, Boolean> isCheckedMap;
//    private CheckBox allCheckBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indent);
        backBtn = (ImageButton)findViewById(R.id.btn_back);
        confirmBtn=(Button)findViewById(R.id.btn_confirm);
        confirmBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        init();
    }
    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_confirm:
                Intent intent2 = new Intent(this.getApplicationContext(),IndentConfirmActivity.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }
    public void init(){
        ((TextView)findViewById(R.id.tv_title)).setText("订单查询");
    }

//    class OrderItemAdapter extends BaseAdapter {
//
//        public List<Item> list;
//        private Context context;
//        LayoutInflater inflater;
//
//        public OrderItemAdapter(Context context, List<Item> list) {
//            super();
//            this.list = list;
//            this.context = context;
//            inflater = LayoutInflater.from(this.context);
//        }
//        @Override
//        public int getCount() {
//            return list == null ? 0 : list.size();
//        }
//        @Override
//        public Object getItem(int location) {
//            return list.get(location);
//        }
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder holder = null;
//            Item item = list.get(position);
//            //Item的位置
//            final int listPosition = position;
//            //这个记录item的id用于操作isCheckedMap来更新CheckBox的状态
//            final int id = item.id;
//            if(convertView == null){
//                holder = new ViewHolder();
//                convertView = inflater.inflate(R.layout.item, null);
//                holder.tvName = (TextView)convertView.findViewById(R.id.dailyName);
//                holder.cBox = (CheckBox)convertView.findViewById(R.id.isCheakBox);
//                convertView.setTag(holder);
//            }else{
//                holder = (ViewHolder) convertView.getTag();
//            }
////            Log.d(TAG, "id="+id);
//            holder.cBox.setChecked(isCheckedMap.get(id));
//            holder.tvName.setText(item.name);
//
//            holder.cBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if(isChecked){
//                        isCheckedMap.put(id,true);
//                    }else{
//                        isCheckedMap.put(id,false);
//                    }
//                }
//            });
//            return convertView;
//        }
//        public final class ViewHolder {
//            public TextView tvName;
//            public ImageButton deleteButton;
//            public CheckBox cBox;
//        }
//    }
//
//    class Item {
//        private Integer id;
//        private String name;
//    }
//
//}
}

