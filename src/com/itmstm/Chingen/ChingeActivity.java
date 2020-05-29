package com.itmstm.Chingen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ChingeActivity extends Activity {
	
	private Chinge2DView mChingeView;
	private static final int MENU_START_SERVICE = Menu.FIRST+ 8;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		// main view s
        mChingeView = new Chinge2DView( (Context) this );
        setContentView( mChingeView );
        
        mChingeView.requestFocus();
    }
    
    @Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		super.onMenuItemSelected(featureId, item);
		
		/*
		if( item.getItemId() == MENU_START_SERVICE ) {
			// Start service
			startService( new Intent( this, ChingeLayerService.class )); 
		}
		*/
		
		return mChingeView.onMenuItemClick(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
    	// Make option menu from Activity
		//menu.add( 0, MENU_START_SERVICE, Menu.NONE, R.string.menu_start_service);
		
		// rest of menu options are created by view
    	return mChingeView.onCreateOptionsMenu( menu );
	}
}
