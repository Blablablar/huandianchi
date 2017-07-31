package com.moka.carsharing.wxapi;

/**
 * Created by blablabla on 2017/5/31.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.moka.carsharing.ActivityManagerApplication;
import com.moka.carsharing.R;
import com.moka.carsharing.cache.SystemConfig;
import com.moka.carsharing.ui.Indent.IndentActivity;
import com.moka.carsharing.ui.Indent.IndentDetailActivity;
import com.moka.carsharing.ui.Indent.PayMethodActivity;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, PayMethodActivity.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        System.out.println("onPayFinish, errCode = " + resp.errCode);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("title");
//            builder.setMessage(String.valueOf(resp.errCode));
//            builder.show();
            if(resp.errCode==0){
                //ActivityManagerApplication.destoryActivity("PayMethod");
                Toast.makeText(getApplicationContext(), "订单支付成功", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getApplicationContext(), IndentActivity.class);
//                intent.putExtra("fragment","payed");
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                //ActivityCollector
                Intent intent = new Intent(this, IndentDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("id", SystemConfig.orderNum);
                intent.putExtra("fragment", "pay_success");
                startActivity(intent);
                //finish();
            }else
                Toast.makeText(WXPayEntryActivity.this, "订单支付失败", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    protected void onResume() {
        super.onResume();
    }
    protected void onDestroy() {
        super.onDestroy();
    }
}