package com.formal.sdusthelper;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;

import com.formal.sdusthelper.baseactivity.BaseCMCCandChinaUnicom;

import pw.isdust.isdust.function.Networklogin_CMCC;
import pw.isdust.isdust.function.Networklogin_ChinaUnicom;

/**
 * Created by Administrator on 2015/10/31.
 */
public class GoNetChinaUnicomActivity  extends BaseCMCCandChinaUnicom {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        INIT(R.layout.gonet_chinaunicom, "ChinaUnicom");  //初始化祖基类
        sharedPreferences = mContext.getSharedPreferences("ChinaUnicomData", Activity.MODE_PRIVATE);    //读取数据的对象
        anct_cls = GoNetChinaUnicomAcntActivity.class; // ChinaUnicom 的账户页面类
        BaseCMCCandChinaUnicomInit();   //初始化页面及数据
        obj_gonet = new Networklogin_ChinaUnicom();    //实例化操作对象
    }
}