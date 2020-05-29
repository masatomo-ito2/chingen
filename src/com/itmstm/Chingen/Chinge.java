package com.itmstm.Chingen;

import java.util.Date;
import java.util.Random;

import com.itmstm.Chingen.R;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;

public class Chinge {

	
	private static final String TAG = "Chinge";
	private static final int NUM_ROUND = 1;
	private static final int NUM_INITIAL_ITERATION = 5;

	
	private PointF[] mPts;
	private Paint mChingePaint = new Paint();
	private Paint mDebugPaint = new Paint();
	private Paint mDebugTouchedBoxPaint = new Paint();
	private Paint mDebugBoxInMovePaint = new Paint();

	private boolean mDebug = false;
	private PointF mStartPoint = new PointF();
	private PointF mEndPoint = new PointF();
	private PointF mTouchedPoint;
	private int mTouchedPointIndex;
	private boolean mDragMode;
	
	private Vector2D mMotionVector = new Vector2D();
	private Vector2D mTouchedPointMotionVector = new Vector2D();
	private float mDistance;
	private int mViewHeight;
	private int mViewWidth;
	
	public Chinge(Resources res, boolean debug, int num_points, float distance, int viewWidth, int viewHeight) {
		
		// set view size
		mViewWidth = viewWidth;
		mViewHeight = viewHeight;
		
		// Paint class for Chinge
		mChingePaint.setColor( res.getColor( R.color.ChingeColor ));
    	mChingePaint.setStrokeWidth( 2 );	
    	
    	// Paint class for debug box
		mDebugPaint.setColor( res.getColor( R.color.DebugBoxColor ));
		mDebugPaint.setStyle(Style.STROKE);
    	mDebugPaint.setStrokeWidth( 2 );	
    	
    	// paint class for touched chinge debug box
    	mDebugTouchedBoxPaint.setColor( res.getColor(R.color.DebugTouchedBoxColor));
		mDebugTouchedBoxPaint.setStyle(Style.FILL);
    	mDebugTouchedBoxPaint.setStrokeWidth( 2 );	
    	
    	
    	// Paint class for debug box in move
    	mDebugBoxInMovePaint.setColor( res.getColor(R.color.DebugBoxInMoveColor));
		mDebugBoxInMovePaint.setStyle(Style.STROKE);
    	mDebugBoxInMovePaint.setStrokeWidth( 2 );	
    	
    	// initialize chinge points
    	mTouchedPointIndex = 0;
    	mPts = new PointF[num_points];
		for( int i=0; i<num_points; i++ ) {
			mPts[i] = new PointF();
		}
		
    	mDistance = PointF.length( distance, distance );
    	
    	Log.d( TAG, "ポイント間のディスタンスは：　" + mDistance );
    	
    	// 乱数にもとづいて初期ベクターを求め、そこからさらに乱数に基づくモーションベクターを数回に分け適用する
    	Random rng = new Random();
    	Date date = new Date();
    	
    	rng.setSeed(date.getTime());
    	
		float initial_w;
		float initial_h;
		
		for( int i=0; i<NUM_INITIAL_ITERATION; i++ ) {
	    	initial_w = (float) rng.nextInt( mViewWidth );
	    	initial_h = (float) rng.nextInt( mViewHeight );
	    	
			mMotionVector.set(
					mPts[mTouchedPointIndex].x, mPts[mTouchedPointIndex].y, 
					initial_w, initial_h );
			mPts[mTouchedPointIndex].set( initial_w, initial_h );
	    		
			calculateOtherChingePointPosition();
		}
		
		mTouchedPointIndex = num_points - 1;
		for( int i=0; i<NUM_INITIAL_ITERATION; i++ ) {
	    	initial_w = (float) rng.nextInt( mViewWidth );
	    	initial_h = (float) rng.nextInt( mViewHeight );
	    	
			mMotionVector.set(
					mPts[mTouchedPointIndex].x, mPts[mTouchedPointIndex].y, 
					initial_w, initial_h );
			mPts[mTouchedPointIndex].set( initial_w, initial_h );
	    		
			calculateOtherChingePointPosition();
		}
		
		mDragMode = false;
		this.mDebug = debug;
	}

	public void drawChinge(Canvas canvas) {
		
		if( mDebug ) {
			drawDebugBoxes( canvas );
		}
		for( int i=0; i < mPts.length -1; i++ ) {
			canvas.drawLine(mPts[i].x, mPts[i].y, mPts[i+1].x, mPts[i+1].y, mChingePaint );
		}
		
	}

	private void drawDebugBoxes( Canvas canvas ) {
		// For debug purpose
		
		Paint p = mDebugPaint;
		
		for( int i=0; i<mPts.length; i++ ) {
			
			if( mDragMode ) {
				p= mDebugBoxInMovePaint;
				
				if( i== mTouchedPointIndex )
					p = mDebugTouchedBoxPaint;
			}
			
			canvas.drawCircle( mPts[i].x, mPts[i].y, mDistance/2, p );
		}
	}

	public boolean isDebug() {
		return mDebug;
	}

	public void setDebug(boolean mDebug) {
		this.mDebug = mDebug;
	}

	public boolean action(MotionEvent event) {
		
		switch( event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mStartPoint.set(event.getX(), event.getY());
			return true;
		case MotionEvent.ACTION_UP:
			if( mDragMode )
				mDragMode = false;
			return true;
		case MotionEvent.ACTION_MOVE:
			mEndPoint.set( event.getX(), event.getY() );
			
			// これはドラッグモーションのベクター
			mMotionVector.set( 
					mStartPoint.x, 
					mStartPoint.y, 
					mEndPoint.x - mStartPoint.x, 
					mEndPoint.y - mStartPoint.y );
			
			if( mDragMode ) {   
				// 引っ張られるポイントに適用されるモーションのベクター
				mTouchedPointMotionVector.set( 
						mTouchedPoint.x, 
						mTouchedPoint.y,
						mEndPoint.x - mTouchedPoint.x,
						mEndPoint.y - mTouchedPoint.y );
				
				mTouchedPoint.offset( mTouchedPointMotionVector.v.x, mTouchedPointMotionVector.v.y );
				
				calculateOtherChingePointPosition();
			}
			else if( touchedChinge() ) {
				mTouchedPoint = getTouchedPoint();
				mDragMode = true;
			}
			
			// update start point
			mStartPoint.set( mEndPoint );
			return true;
		}
		return false;
	}

	private void calculateOtherChingePointPosition() {
	
		int round = NUM_ROUND;
		
		// ボックス下方向
		for( int i=mTouchedPointIndex -1; i>=0; i-- ) {
			if( round != 0 ) round--;
			calculateNewPointPosition(i, i+1, round );
		}

		round = NUM_ROUND;
		
		// ボックス上方向
		for( int i=mTouchedPointIndex +1; i<mPts.length; i++ ) {
			if( round != 0 ) round--;
			calculateNewPointPosition(i, i-1, round );
		}
	}
		
	private void calculateNewPointPosition(int currentIndex, int targetIndex, int round) {
		
		PointF current = mPts[ currentIndex ];
		PointF target = mPts[ targetIndex ];
		
		PointF c_to_t = new PointF();

		if( round > 0 ) {
			// もしRoundが設定されていたら、丸くなるように補正
			PointF a = getOrthgonalVector();
			Log.d( TAG, "Orthogonal: " + a.x + "," + a.y );
			
			normalize( a );
			Log.d( TAG, "Normalized Orthogonal: " + a.x + "," + a.y );
			
			// 交線への垂線を求める
			PointF p = getPerpendicular( current, target, a );
			Log.d( TAG, "Perpendicular: " + p.x + "," + p.y );
			
			
			// 補正値
			float dx = p.x * ( (float) round / (float) NUM_ROUND );
			float dy = p.y * ( (float) round / (float) NUM_ROUND );
					
			// currentの位置をアップデート
			current.offset( dx, dy );
		}
		
		// currentからtargetへのベクターを取得
		c_to_t.set( target.x - current.x, target.y - current.y );
	
		// c_to_tの大きさとmDistanceの比率を求める
		float ratio = mDistance / c_to_t.length();
		
		// c_to_tを適切な長さにする
		multiply( c_to_t, (1.f -ratio) );
		
		// 新しい場所へアップデート
		current.offset( c_to_t.x, c_to_t.y );
	}

	private PointF getPerpendicular(PointF current, PointF target, PointF a) {
		PointF ret = new PointF();
		ret.set( target.x - current.x, target.y - current.y );
		float dot_a_c = dot( a, ret );
		float dx = a.x * dot_a_c;
		float dy = a.y * dot_a_c;
		
		Log.d( TAG, "Perpendicular: dx=" + dx + ", dy=" + dy );
		ret.set( (target.x + dx) - current.x,  (target.y + dy) - current.y );
				
		return ret;
	}

	private float dot(PointF v1, PointF v2) {
		return v1.x * v2.x + v1.y * v2.y;
	}

	// モーションベクターに直交するベクトルを返す (モーション方向に対して左向きの直交ベクトルを返す）
	private PointF getOrthgonalVector() {
		PointF ret = new PointF();
			
		ret.set( -mMotionVector.v.y, mMotionVector.v.x );
		return ret;
	}

	private void multiply(PointF p, float m) {
		p.set( p.x * m, p.y * m );
	}

	private void normalize(PointF p) {
		p.set( p.x / p.length(), p.y / p.length() );
	}

	private PointF getTouchedPoint() {
		return mPts[mTouchedPointIndex];
	}

	private boolean touchedChinge() {
		
		Vector2D line = new Vector2D();
		
		for( int i=0; i<mPts.length - 1; i++ ) {
			
			// Moveイベントの間にｘとｙの移動感覚に隙間ができるので、点と点の間の線で衝突を判定すべき
			
			line.set( mPts[i].x, mPts[i].y, mPts[i+1].x - mPts[i].x, mPts[i+1].y - mPts[i].y );
			
			if( mMotionVector.collideTo( line )) 
			{
				Log.d(TAG, "Line " + i + " に触った！ Ratio=" + mMotionVector.mRatio );
				
				if( mMotionVector.mRatio < 0.5f ) 
					mTouchedPointIndex = i;
				else 
					mTouchedPointIndex = i + 1;
				
				return true;
			}
		}
		return false;
	}
}
