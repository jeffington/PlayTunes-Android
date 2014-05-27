package com.ideabag.playtunes;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;

public class PlayTunesActivity extends Activity {
	int[] commandHistory;
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    
    @Override
    public Object onRetainNonConfigurationInstance() {
    	return commandHistory;
    }
	
	protected void commandHistoryPush(int x) {
		if (commandHistory == null) {
			commandHistory = new int[1];
			commandHistory[0] = x;
		}
		else {
			int len = commandHistory.length;
			int[] newArr = new int[len + 1];
			System.arraycopy(commandHistory, 0, newArr, 0, len);
			commandHistory = newArr;
			commandHistory[commandHistory.length - 1] = x;
		}
	}
	
	protected int commandHistoryPeek() {
		if (commandHistory == null)
			return -2;
		
		if (commandHistory.length > 0)
			return commandHistory[commandHistory.length - 1];
		return -2;
	}
	
	protected int commandHistoryPop() {
		int x;
		if (commandHistory == null)
			return -2;
		
		int len = commandHistory.length;
		
		if (len >= 1) {
			int[] newArr = new int[len - 1];
			x = commandHistory[len - 1];			
			System.arraycopy(commandHistory, 0, newArr, 0, len - 1);
			commandHistory = newArr;
		}
		else {
			x = commandHistory[0];
			commandHistory = null;
		}
		return x;
	}
	
	protected boolean commandHistoryIsEmpty() {
		return (commandHistory == null || commandHistory.length <= 0);
	}
	
}
