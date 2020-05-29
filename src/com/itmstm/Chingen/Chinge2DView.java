package com.itmstm.Chingen;

import com.itmstm.Chingen.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class Chinge2DView extends View implements OnTouchListener  {
	
	private static final String TAG = "Chinge2DView";
	private DebugLine mDebugLine;
	private Chinge mChinge;
	private boolean mDebug = false;
	
	private static final int MENU_TOGGLE_DEBUG = Menu.FIRST;
	private static final int MENU_SET_YUTAKA = Menu.FIRST+1;
	private static final int MENU_SET_MASA = Menu.FIRST+2;
	private static final int MENU_SET_NO_BG = Menu.FIRST+3;
	private static final int MENU_HIGH_RESOLUTION = Menu.FIRST+4;
	private static final int MENU_MIDDLE_RESOLUTION = Menu.FIRST+5;
	private static final int MENU_LOW_RESOLUTION = Menu.FIRST+6;
	private static final int MENU_VERY_LOW_RESOLUTION = Menu.FIRST+7;

	
	enum BG {
		BG_NONE,
		BG_YUTAKA,
		BG_MASA,
	}

	private DebugMotionEvent mDebugMotionEvent;
	private BG mBG = BG.BG_NONE;
	
	private Chinge mChingeHigh;
	private Chinge mChingeMiddle;
	private Chinge mChingeLow;
	private Chinge mChingeVeryLow;
	private Resources mRes;
	private int mViewHeight;
	private int mViewWidth;
	
	public Chinge2DView(Context context) {
		super( context );
		
		Log.d(TAG,  "Ching2DView constructor!");
		
		
    }	
	
	public void setDebug(boolean mDebug) {
		this.mDebug = mDebug;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		
		mViewWidth = w;
		mViewHeight = h;
		
		initChinge();
	}

	private void initChinge() {
		// enable this view to receive touch event
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		this.setOnTouchListener(this);
		
		
		// background
		mRes = getResources();
		this.setBackgroundColor( mRes.getColor( R.color.BackGroundColor ));
		mDebugLine = new DebugLine( mRes );
		
		// Chinge instance
		//mChingeMiddle	= new Chinge( mRes,  mDebug, 100, 10.f, mViewWidth, mViewHeight );
		mChingeHigh 	= new Chinge( mRes,  mDebug, 200, 5.f, mViewWidth, mViewHeight );

		
		// default
		mChinge = mChingeHigh;
		
        // for touch event debug
        mDebugMotionEvent = new DebugMotionEvent();
        mDebugMotionEvent.setDebugEnable(mDebug);
	}

	@Override
    protected void onDraw( Canvas canvas ) {
    	// begin drawing
    
		if( mDebug )
	    	mDebugLine.drawGrid( canvas );
		
		switch( mBG ) {
		case BG_NONE:
			setBackgroundResource( R.color.BackGroundColor );
			break;
		case BG_YUTAKA:
			setBackgroundResource( R.drawable.yutaka_l );
			break;
			/*
		case BG_MASA:
			setBackgroundResource( R.drawable.masa_l );
			break;
			*/
		}
		
    	mChinge.drawChinge( canvas );
    	
    }

	public boolean onTouch(View v, MotionEvent event) {
		mDebugMotionEvent.dumpEvent( event );
		boolean ret = mChinge.action( event );
		
		invalidate();
		
		return ret;
	}

	public void toggleDebug() {
		if( mDebug ) {
			mDebug = false;
			mChinge.setDebug(false);
		}
		else {
			mDebug = true;
			mChinge.setDebug(true);
		}
		invalidate();
	}

	public boolean onMenuItemClick(MenuItem item) {
		
		switch( item.getItemId()) {
		case MENU_TOGGLE_DEBUG:
			this.toggleDebug();
			break;
		case MENU_SET_YUTAKA:
			mBG = BG.BG_YUTAKA;
			break;
		case MENU_SET_MASA:
			mBG = BG.BG_MASA;
			break;
		case MENU_SET_NO_BG:
			mBG = BG.BG_NONE;
			break;
		case MENU_HIGH_RESOLUTION:
			if( mChingeHigh == null ) 
				mChingeHigh 	= new Chinge( mRes,  mDebug, 200, 5.f, mViewWidth, mViewHeight );
			mChinge = mChingeHigh;
			break;
		case MENU_MIDDLE_RESOLUTION:
			if( mChingeMiddle == null ) 
				mChingeMiddle 	= new Chinge( mRes,  mDebug, 100, 10.f, mViewWidth, mViewHeight );
			mChinge = mChingeMiddle;
			break;
		case MENU_LOW_RESOLUTION:
			if( mChingeLow == null ) 
				mChingeLow 		= new Chinge( mRes,  mDebug,  50, 20.f, mViewWidth, mViewHeight );
			mChinge = mChingeLow;
			break;
		case MENU_VERY_LOW_RESOLUTION:
			if( mChingeVeryLow == null )
				mChingeVeryLow 	= new Chinge( mRes,  mDebug,  20, 50.f, mViewWidth, mViewHeight );
			mChinge = mChingeVeryLow;
			break;
		}
		invalidate();
		return false;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add( 0, MENU_SET_NO_BG, Menu.NONE, R.string.menu_set_no_bg);
		menu.add( 0, MENU_SET_YUTAKA, Menu.NONE, R.string.menu_set_yutaka).setIcon( R.drawable.yutaka_l);
		//menu.add( 0, MENU_TOGGLE_DEBUG, Menu.NONE, R.string.menu_toggle_debug).setIcon( R.drawable.ic_launcher);
		//menu.add( 0, MENU_HIGH_RESOLUTION, Menu.NONE, R.string.menu_high_resolution);
		//menu.add( 0, MENU_MIDDLE_RESOLUTION, Menu.NONE, R.string.menu_middle_resolution);
		//menu.add( 0, MENU_LOW_RESOLUTION, Menu.NONE, R.string.menu_low_resolution);
		//menu.add( 0, MENU_VERY_LOW_RESOLUTION, Menu.NONE, R.string.menu_very_low_resolution);
		//menu.add( 0, MENU_SET_MASA, Menu.NONE, R.string.menu_set_masa).setIcon( R.drawable.masa_l);
		return true;
	}
}
