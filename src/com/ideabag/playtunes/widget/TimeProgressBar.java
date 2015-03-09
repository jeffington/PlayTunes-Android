package com.ideabag.playtunes.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class TimeProgressBar extends ProgressBar {
	
	private static final int MILLISECOND_TIMER = 500;
	
	private Handler handle;
	
	private boolean isPlaying = false;
	
	public TimeProgressBar( Context context ) {
		super( context );
		
		init( context );
		
	}
	
	public TimeProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs );
		
		init( context );
		
	}
	
	public TimeProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
		init( context );
		
	}

	
	private void init( Context context ) {
		
		handle = new Handler();
		
	}
	
	
	
	public interface TimeChangeListener {
		
		
	}
	
	private final Runnable mUpdateTimer = new Runnable() {

		@Override public void run() {
			
			setProgress( getProgress() + MILLISECOND_TIMER );
			
			handle.postDelayed( mUpdateTimer, MILLISECOND_TIMER );
			
		}
		
		
		
	};
	
	public void start() {
		
		stop();
		isPlaying = true;
		handle.postDelayed( mUpdateTimer, MILLISECOND_TIMER );
		
		
	}
	
	public void stop() {
		
		isPlaying = false;
		handle.removeCallbacks( mUpdateTimer );
		
	}
	
}
