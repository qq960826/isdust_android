package com.isdust.www;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.isdust.www.baseactivity.BaseMainActivity_new;

/**
 * Created by Administrator on 2015/10/17.
 */
public class JiaowuActivity extends BaseMainActivity_new {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        INIT(R.layout.helper_jiaowu, "教务查询",3);
    }
    public void onFormJiaowuClick(View v) {
        //设置传递方向
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.FormJiaowu_button_emptyroom:
                intent.setClass(this,EmptyRoomActivity.class);
                //启动activity
                this.startActivity(intent);
                break;
            case R.id.FormJiaowu_button_schedule:
                intent.setClass(this,ScheduleActivity.class);
                //启动activity
                this.startActivity(intent);
                break;



        }
    }
}
