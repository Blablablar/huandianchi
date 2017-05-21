package com.haundianchi.huandianchi.ui.tickets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.haundianchi.huandianchi.Http.VolleyListenerInterface;
import com.haundianchi.huandianchi.Http.VolleyRequest;
import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.utils.ActivityBuilder;
import com.haundianchi.huandianchi.widget.TitleBar;
import com.haundianchi.huandianchi.widget.dialog.TicketConfirmDialog;
import com.haundianchi.huandianchi.widget.dialog.TicketTypeSelectDialog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateNewTicketActivity extends AppCompatActivity {

    @BindView(R.id.et_ticket_type)
    TextView mTicketType;
    @BindView(R.id.et_ticket_content)
    TextView mTicketContent;
    @BindView(R.id.et_ticket_money)
    TextView mTicketMoney;
    @BindView(R.id.et_ticket_property)
    EditText mTicketProperty;
    @BindView(R.id.et_ticket_receiver_name)
    EditText mTicketReceiverName;
    @BindView(R.id.et_ticket_phone)
    EditText mTicketPhone;
    @BindView(R.id.et_ticket_address)
    EditText mTicketAddress;
    @BindView(R.id.et_ticket_detail_address)
    EditText mTicketDetailAddress;
    @BindView(R.id.et_ticket_mail)
    EditText mTicketMail;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.vg_root)
    ViewGroup mRoot;
    @BindView(R.id.btn_submit_ticket)
    Button btnSubmitTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_ticket);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        mRoot.requestFocus();
        mTitleBar.bindActivity(this);

        mTicketMoney.setText(getIntent().getStringExtra("ticketMoney") + "元");
        mTicketType.setText("纸质发票");
    }

    @OnClick(R.id.et_ticket_type)
    public void onEtTicketTypeClicked() {
    }

    @OnClick(R.id.et_ticket_address)
    public void onEtTicketAddressClicked() {
    }

    @OnClick(R.id.btn_submit_ticket)
    public void onBtnSubmitTicketClicked() {
        if(!validate()){
            return ;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("type", mTicketType.getText().toString());
        params.put("title", mTicketProperty.getText().toString());
        params.put("receiver", mTicketReceiverName.getText().toString());
        params.put("mobile", mTicketPhone.getText().toString());
        params.put("address", mTicketDetailAddress.getText().toString());

        TicketConfirmDialog.Builder builder = new TicketConfirmDialog.Builder(this, params, new TicketConfirmDialog.OnConfirmClickedListener() {
            @Override
            public void onClick() {
                createOrder();
            }
        });
        builder.show();
    }

    /**
     * 验证发票信息
     * @return true／false
     */
    private boolean validate() {
        if (mTicketProperty.getText().toString().equals("")){
            Toast.makeText(this, "请输入发票抬头", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mTicketReceiverName.getText().toString().equals("")){
            Toast.makeText(this, "请输入收件人姓名", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mTicketPhone.getText().toString().equals("")){
            Toast.makeText(this, "请输入收件人手机号", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mTicketDetailAddress.getText().toString().equals("")){
            Toast.makeText(this, "请输入收件人详细地址", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @OnClick(R.id.et_ticket_type)
    public void onViewClicked() {
        TicketTypeSelectDialog.Builder builder = new TicketTypeSelectDialog.Builder(this, new TicketTypeSelectDialog.OnConfirmClickedListener() {
            @Override
            public void onClick(String type) {
                mTicketType.setText(type);
            }
        });
        builder.show();
    }

    public void createOrder() {
        Map<String, String> params = new HashMap<>();
        params.put("body", mTicketContent.getText().toString());
        params.put("title", mTicketProperty.getText().toString());
        params.put("type", Objects.equals(mTicketType.getText().toString(), "纸质发票") ? "0" : "1");
        params.put("mobile", mTicketPhone.getText().toString());
        params.put("address", mTicketDetailAddress.getText().toString());
        params.put("email", mTicketMail.getText().toString());
        params.put("orderNums", getIntent().getStringExtra("orderString"));

        VolleyRequest.RequestPost(getApplicationContext(), "/Invoice/create", "createOrder", params,
                new VolleyListenerInterface(getApplicationContext(), VolleyListenerInterface.mListener, VolleyListenerInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(String result) {
                        try {
                            JSONObject obj = new JSONObject(result);
                            System.out.println(result);
                            if (obj.getString("code").equals("200")){
                                Toast.makeText(CreateNewTicketActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CreateNewTicketActivity.this, HistoryTicketsActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }else{
                                Toast.makeText(CreateNewTicketActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        Toast.makeText(CreateNewTicketActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public static class Builder extends ActivityBuilder {
        private String orderString;
        private String ticketMoney;

        public Builder(Context context, String orderString, String ticketMoney) {
            super(context);
            this.orderString = orderString;
            this.ticketMoney = ticketMoney;
        }

        @Override
        public Intent create() {
            Intent intent = new Intent(getContext(), CreateNewTicketActivity.class);
            intent.putExtra("orderString", orderString);
            intent.putExtra("ticketMoney", ticketMoney);
            return intent;
        }
    }
}
