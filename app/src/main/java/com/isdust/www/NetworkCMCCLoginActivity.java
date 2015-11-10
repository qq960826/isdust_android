package com.isdust.www;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.isdust.www.baseactivity.BaseSubPageActivity_new;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pw.isdust.isdust.function.Networklogin_CMCC;

/**
 * Created by wzq on 15/11/10.
 */
public class NetworkCMCCLoginActivity extends BaseSubPageActivity_new {
    EditText mEditText_CMCC_user,mEditText_CMCC_password;
    Button mButton_ok,mButton_dymaticpassword;
    CheckBox mCheckBox_savepassword;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mSharedPreferences_editor;
    Networklogin_CMCC mNetworklogin_CMCC;


    //线程池
    String xiancheng_user,xiancheng_password,xiancheng_status;
    ExecutorService mExecutorService= Executors.newCachedThreadPool();
    android.os.Handler mHandler=new android.os.Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if (msg.what==0){//获取验证码失败
                Toast.makeText(mContext,"验证码获取失败",Toast.LENGTH_SHORT);
                return;

            }
            if (msg.what==1){//获取验证码成功
                Toast.makeText(mContext,"动态密码已经发往手机号码",Toast.LENGTH_SHORT);
                return;


            }
            if (msg.what==10){//网络超时
                Toast.makeText(mContext, "网络访问超时，请重试", Toast.LENGTH_SHORT).show();
                return;
            }


        }
    };
    Runnable xiancheng_getdymaticpassword=new Runnable() {
        @Override
        public void run() {
            Message message=new Message();


            try {
                xiancheng_status=isdustapp.getNetworklogin_CMCC().cmcc_getyanzheng(xiancheng_user);
            } catch (IOException e) {
                message.what=10;
                mHandler.sendMessage(message);
                return;
            }
            if (xiancheng_status.equals("动态密码已经发往手机号码")){
                message.what=1;
                mHandler.sendMessage(message);
                return;
            }
            message.what=0;
            mHandler.sendMessage(message);

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        INIT(R.layout.activity_network_cmcc, "CMCC二层登录账号");
        mNetworklogin_CMCC=new Networklogin_CMCC();
        mSharedPreferences = mContext.getSharedPreferences("NetworkLogin", Activity.MODE_PRIVATE);
        mSharedPreferences_editor=mSharedPreferences.edit();
        getview();
        String name =mSharedPreferences.getString("network_cmcc_cmcc_user", "");
        String pwd =mSharedPreferences.getString("network_cmcc_cmcc_password", "");
        Boolean keeppwd = mSharedPreferences.getBoolean("network_cmcc_cmcc_keeppword", true);;
        mEditText_CMCC_user.setText(name);
        mEditText_CMCC_password.setText(pwd);
        mCheckBox_savepassword.setChecked(keeppwd);

    }
    public void getview(){
        mEditText_CMCC_user=(EditText)findViewById(R.id.EditText_network_cmcc_login_user);
        mEditText_CMCC_password=(EditText)findViewById(R.id.EditText_network_cmcc_login_password);
        mCheckBox_savepassword=(CheckBox)findViewById(R.id.checkbox_network_cmcc_savepassword);
        mButton_dymaticpassword=(Button)findViewById(R.id.btn_cmcc_dynamicpwd);
        mButton_ok=(Button)findViewById(R.id.button_network_cmcc_login);


        mButton_dymaticpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xiancheng_user=mEditText_CMCC_user.getText().toString();
                if (xiancheng_user.equals("")){
                    Toast.makeText(mContext, "请输入您的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                mExecutorService.execute(xiancheng_getdymaticpassword);


            }
        });
        mButton_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xiancheng_user=mEditText_CMCC_user.getText().toString();
                xiancheng_password=mEditText_CMCC_password.getText().toString();
                if (xiancheng_user==""||xiancheng_password==""){
                    Toast.makeText(mContext,"请输入您的账号和密码",Toast.LENGTH_SHORT);
                    return;
                }
                mSharedPreferences_editor.putString("network_cmcc_cmcc_user", xiancheng_user);

                    if (mCheckBox_savepassword.isChecked()) {    //记住密码
                        mSharedPreferences_editor.putBoolean("network_cmcc_cmcc_keeppword", true);
                        mSharedPreferences_editor.putString("network_cmcc_cmcc_password", xiancheng_password);
                    } else {    //不记住密码
                        mSharedPreferences_editor.putBoolean("network_cmcc_cmcc_keeppword", false);
                        mSharedPreferences_editor.putString("network_cmcc_cmcc_password", "");
                    }
                    //提交当前数据
                    mSharedPreferences_editor.commit();
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("cmcc_cmcc_user", xiancheng_user);
                    bundle.putString("cmcc_cmcc_password", xiancheng_password);
                    intent.putExtras(bundle);
                NetworkCMCCLoginActivity.this.setResult(RESULT_OK, intent);

                    finish();

            }
        });


    }
}
