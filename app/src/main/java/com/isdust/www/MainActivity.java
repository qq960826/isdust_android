package com.isdust.www;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;

import com.isdust.www.baseactivity.BaseMainActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends BaseMainActivity {
	static boolean ishadopended = false;
	private Timer timer_wel = null;
	private boolean bool_wel = false;
	private View form_welcome;
	private MyApplication isdustapp1;
	Context mContext;
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		Http b=new Http();
//		String c=b.get_string("http://kzxs.isdust.com/chaxun.php?method=4&building=J1&zhoushu=9&xingqi=5&jieci=3");
//		Network_Kuaitong a=new Network_Kuaitong(this);
//		a.loginSmartCard("1501060238", "147147");
//		a.gaitaocan("1");
//		a.gaitaocan("2");
//		a.gaitaocan("3");
//		a.gaitaocan("4");
//		a.gaitaocan("5");
//		a.gaitaocan("6");
//		a.gaitaocan("7");
//		a.gaitaocan("8");
//		a.gaitaocan("9");
		isdustapp1=(MyApplication)getApplication();
		mContext=this;
		MobclickAgent.updateOnlineConfig(mContext);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);

//		Intent intent = new Intent();
//		intent.setClass(this, Card_login.class);
//		startActivityForResult(intent, 1);
//		Network_Kuaitong a=new Network_Kuaitong(this);
//		try {
//			a.loginSmartCard("1501060225","960826");
//			a.gaitaocan("11");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		if (ishadopended == true) {    //程序已经启动
			INIT(R.layout.activity_main, "首页");


		}else {
			ishadopended = true;
			LayoutInflater inflate = LayoutInflater.from(this);
			form_welcome = inflate.inflate(R.layout.welcome,null);
			setContentView(form_welcome);		//Show welcome page
			//next add some load event
			timer_wel = new Timer();
			timer_wel.schedule(task_wel, 2000, 2);		// start a 5s's timer after 2s

		}


//		Library a=new Library();
//		String b=a.login("1501060225","960826wang");

	}

	TimerTask task_wel = new TimerTask(){
        public void run(){
                 Message message = new Message();
                 if (bool_wel) 
                	 message.what = 1 ;
                 else
                	 message.what = 2 ;		// Change Transparency's command
                 handler_wel.sendMessage(message);
        }
	};
	
	final Handler handler_wel = new Handler(){
        public void handleMessage(Message msg){
                switch(msg.what){
                   case 1:
                	   bool_wel = true;
                   break;
                   case 2:
                	   float alp = form_welcome.getAlpha();
                	   //System.out.println(alp);
                	   if (alp < 0.015) {
                		   INIT(R.layout.activity_main,"首页");
                		   timer_wel.cancel();		//销毁 timer_wel
                	   }
                	   else {
                		   form_welcome.setAlpha((float) (alp - 0.01));	//修改欢迎页面的透明度
                	   }
                   break;
                }
                super.handleMessage(msg);
        }
	};

}