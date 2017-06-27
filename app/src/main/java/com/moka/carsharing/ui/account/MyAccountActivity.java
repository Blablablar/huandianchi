package com.moka.carsharing.ui.account;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.moka.carsharing.data.UserInfo;
import com.moka.carsharing.utils.ActivityBuilder;
import com.moka.carsharing.widget.TitleBar;
import com.moka.carsharing.R;
import com.moka.carsharing.ui.LoginActivity;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.widget.Toast;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyAccountActivity extends AppCompatActivity {
    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.user_img)
    RoundedImageView userImg;
    @BindView(R.id.vg_edit_img)
    ViewGroup vgEditImg;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.vg_edit_username)
    ViewGroup vgEditUsername;
    @BindView(R.id.user_phone)
    TextView userPhone;
    @BindView(R.id.tv_car_id)
    TextView carId;
    @BindView(R.id.vg_edit_phone)
    ViewGroup vgEditPhone;
    @BindView(R.id.btn_logout)
    Button mLogOutBtn;

    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;

    private static final int MSG_USER_INFO = 3;
    protected static Uri tempUri;
    RequestQueue mQueue;
    String phoneNumberStr;
    String tokenStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        if(UserInfo.getHeadPortrait()!=null)
            userImg.setImageBitmap(UserInfo.getHeadPortrait());
        if(UserInfo.getNameStr()!=null)
            userName.setText(UserInfo.getNameStr());
        if(UserInfo.getMobilePhoneStr()!=null)
            userPhone.setText(UserInfo.getMobilePhoneStr());
        if(UserInfo.getMobilePhoneStr()!=null)
            carId.setText(UserInfo.getCarIdStr());
        titleBar.bindActivity(this);
        mQueue = Volley.newRequestQueue(this);
        mLogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.commit();
                UserInfo.clear();
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @OnClick({R.id.vg_edit_img, R.id.vg_edit_username, R.id.vg_edit_phone})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.vg_edit_img:
                showChoosePicDialog();
                break;
            case R.id.vg_edit_username:
                new MyUserNameEditActivity.Builder(this).start();
                break;
            case R.id.vg_edit_phone:
                break;
        }
    }

    public static class Builder extends ActivityBuilder {
        public Builder(Context context) {
            super(context);
        }
        @Override
        public Intent create() {
            return new Intent(getContext(), MyAccountActivity.class);
        }
    }

    /**
     * 显示修改头像的对话框
     */
    protected void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("上传头像");
        String[] items = { "选择本地照片", "拍照" };
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        Intent openAlbumIntent = new Intent(
                                Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE: // 拍照
                        Intent openCameraIntent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        tempUri = Uri.fromFile(new File(Environment
                                .getExternalStorageDirectory(), "image.jpg"));
                        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;
                }
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    /**
     * 保存裁剪之后的图片数据
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            //photo = Utils.toRoundBitmap(photo, tempUri); // 这个时候的图片已经被处理成圆形的了
            //userImg.setImageBitmap(photo);
            uploadPic(photo);
        }
    }

    private void uploadPic(Bitmap bitmap) {
        // 上传至服务器
        // ... 可以在这里把Bitmap转换成file，然后得到file的url，做文件上传操作
        // 注意这里得到的图片已经是圆形图片了
        // bitmap是没有做个圆形处理的，但已经被裁剪了
        if(bitmap!=null)
        {
            final String bitmapStr = bitmaptoString(bitmap,100);
            StringRequest stringRequest1 = new StringRequest(Request.Method.POST,
                    "http://116.62.56.64/test/Member/update", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("TAG", response);
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject userInfoJson;
                        if(jsonObject.get("code").toString().equals("200")){
                            //userInfoJson=new JSONObject(jsonObject.get("result").toString());
//                            Message msg=new Message();
//                            msg.obj=userInfoJson;
//                            msg.what=MSG_USER_INFO;
//                            mHandler.sendMessage(msg);
                            getUserInfo();
                        }else if(jsonObject.get("code").toString().equals("400"))
                            Toast.makeText(getApplicationContext(), jsonObject.get("error").toString(), Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("TAG", error.getMessage(), error);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("mobile", phoneNumberStr);
                    map.put("headPortrait", bitmapStr);
                    return map;
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("MK-AUTH", phoneNumberStr+"-"+tokenStr);

                    return headers;
                }
            };
            mQueue.add(stringRequest1);
        }

    }

    /**
     　　* 将bitmap转换成base64字符串
     　　*
     　　* @param bitmap
     　　* @return base64 字符串
     　　*/
    public String bitmaptoString(Bitmap bitmap, int bitmapQuality) {
        // 将Bitmap转换成字符串
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, bitmapQuality, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }

    public void getUserInfo(){
        if(!getToken())
            return;
        StringRequest stringRequest1 = new StringRequest(Request.Method.POST,
                "http://116.62.56.64/test/Member/info", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG", response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject userInfoJson;
                    if(jsonObject.get("code").toString().equals("200")){
                        userInfoJson=new JSONObject(jsonObject.get("result").toString());
                        Message msg=new Message();
                        msg.obj=userInfoJson;
                        msg.what=MSG_USER_INFO;
                        mHandler.sendMessage(msg);
                    }else if(jsonObject.get("code").toString().equals("400"))
                        Toast.makeText(getApplicationContext(), jsonObject.get("error").toString(), Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("MK-AUTH", phoneNumberStr+"-"+tokenStr);
                return headers;
            }
        };
        mQueue.add(stringRequest1);
    }

    public Boolean getToken(){
        SharedPreferences share=getSharedPreferences("user", Activity.MODE_PRIVATE);
        phoneNumberStr = share.getString("phoneNumber","");
        tokenStr = share.getString("token","");
        if (phoneNumberStr.equals(""))
            return false;
        return true;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage (Message msg) {//此方法在ui线程运行
            switch(msg.what) {
                case MSG_USER_INFO:
                    JSONObject jsonObject=(JSONObject)msg.obj;
                    try{
                        if(!jsonObject.isNull("name"))
                            userName.setText(jsonObject.get("name").toString());
                        if(!jsonObject.isNull("mobile"))
                            userPhone.setText(jsonObject.get("mobile").toString());
                        if(!jsonObject.isNull("carId"))
                            carId.setText(jsonObject.get("carId").toString());
                        if(!jsonObject.isNull("headPortrait"))
                            userImg.setImageBitmap(stringtoBitmap(jsonObject.get("headPortrait").toString()));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    public Bitmap stringtoBitmap(String string) {
        // 将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo();
    }
}
