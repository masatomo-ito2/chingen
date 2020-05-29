package com.itmstm.Chingen;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;


public class ChingeLayerService extends Service {
	View mView;
	WindowManager mWM;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		// Remove View
		mWM.removeView(mView);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		// 重ねあわせするViewの設定を行う
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.FILL_PARENT,
				WindowManager.LayoutParams.FILL_PARENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				PixelFormat.TRANSLUCENT );
		
		// WindowManagerを取得
		mWM = (WindowManager) getSystemService( Context.WINDOW_SERVICE);
		
		// 重ねあわせするViewを作成
		mView = new Chinge2DView( getApplicationContext() );
		
		mWM.addView( mView, params );
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
