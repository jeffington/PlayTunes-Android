package com.ideabag.playtunes;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;

public class PlayTunesActivity extends Activity {
	
	int[] commandHistory;
	
    @Override public boolean onCreateOptionsMenu( Menu menu ) {
    	
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.menu, menu );
        
        return true;
        
    }
    
    
    @Override public Object onRetainNonConfigurationInstance() {
    	
    	return commandHistory;
    	
    }
	
	protected void commandHistoryPush( int commandCode ) {
		
		if ( null == commandHistory ) {
			
			commandHistory = new int[ 1 ];
			commandHistory[ 0 ] = commandCode;
			
		} else {
			
			int len = commandHistory.length;
			int[] newArr = new int[ len + 1 ];
			System.arraycopy( commandHistory, 0, newArr, 0, len );
			commandHistory = newArr;
			commandHistory[ commandHistory.length - 1 ] = commandCode;
			
		}
		
	}
	
	protected int commandHistoryPeek() {
		
		if ( null == commandHistory) {
			
			return -2;
			
		}
		
		if ( commandHistory.length > 0 ) {
			
			return commandHistory[ commandHistory.length - 1 ];
			
		}
		
		return -2;
		
	}
	
	protected int commandHistoryPop() {
		
		int poppedCommandCode;
		if ( null == commandHistory) {
			
			return -2;
			
		}
		
		int len = commandHistory.length;
		
		if ( len >= 1 ) {
			
			int[] newArr = new int[ len - 1 ];
			poppedCommandCode = commandHistory[ len - 1 ];			
			System.arraycopy( commandHistory, 0, newArr, 0, len - 1 );
			commandHistory = newArr;
		
		} else {
			
			poppedCommandCode = commandHistory[ 0 ];
			commandHistory = null;
			
		}
		
		return poppedCommandCode;
		
	}
	
	protected boolean commandHistoryIsEmpty() {
		
		return (null == commandHistory || commandHistory.length <= 0);
		
	}
	
}
