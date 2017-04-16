package com.haundianchi.huandianchi.ui.tickets;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.haundianchi.huandianchi.R;
import com.haundianchi.huandianchi.widget.dialog.TicketConfirmDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateNewTicketActivity extends AppCompatActivity {

    @BindView(R.id.et_ticket_type)
    EditText mTicketType;
    @BindView(R.id.et_ticket_content)
    EditText mTicketContent;
    @BindView(R.id.et_ticket_money)
    EditText mTicketMoney;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_ticket);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.et_ticket_type)
    public void onEtTicketTypeClicked() {
    }

    @OnClick(R.id.et_ticket_address)
    public void onEtTicketAddressClicked() {
    }

    @OnClick(R.id.btn_submit_ticket)
    public void onBtnSubmitTicketClicked() {
        TicketConfirmDialog.Builder builder = new TicketConfirmDialog.Builder(this);
        builder.show();
    }
}