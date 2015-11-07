package com.isdust.www;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.isdust.www.baseactivity.BaseSubPageActivity;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;

/**
 * Created by Administrator on 2015/10/16.
 */
public class CardChangePwdActivity extends BaseSubPageActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        INIT(R.layout.activity_cardchangepwd, "密码修改");
        MobclickAgent.onEvent(this, "schoolcard_changePwd");

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.FormCard_button_changeok:
                EditText textid = (EditText) findViewById(R.id.FormCard_editText_IDcard);
                EditText textoldpwd = (EditText) findViewById(R.id.FormCard_editText_oldpwd);
                EditText textnewpwd = (EditText) findViewById(R.id.FormCard_editText_newpwd);
                EditText textrenewpwd = (EditText) findViewById(R.id.FormCard_editText_renewpwd);
                String strid = textid.getText().toString();
                String stroldpwd = textoldpwd.getText().toString();
                String strnewpwd = textnewpwd.getText().toString();
                String strrenewpwd = textrenewpwd.getText().toString();
                if (strnewpwd.length()!=6){
                    Toast.makeText(this, "密码需要设置为6位纯数字", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (strnewpwd.equals(strrenewpwd)) {
                    String result;
                    try {
                        result = isdustapp.getUsercard().changepassword(stroldpwd,strnewpwd,strid);
                        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                        if (result.equals("修改密码成功"))
                            finish();
                    } catch (IOException e) {
                        Toast.makeText(this, "网络访问超时，请重试", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }
                else
                    Toast.makeText(this, "新密码前后不一致", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}