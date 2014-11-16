package com.ideabag.playtunes.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.util.TrackerSingleton;

public class FeedbackDialogFragment extends DialogFragment {
	
	private EditText mEditText;
	Tracker tracker;
	
	public FeedbackDialogFragment() {
		
		setStyle( STYLE_NORMAL, 0 );
		
	}
	
	@Override public void onAttach( Activity activity ) {
		super.onAttach( activity );
		
		tracker = TrackerSingleton.getDefaultTracker( activity );
		
	}
	
    @Override public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
        View view = inflater.inflate( R.layout.dialog_fragment_feedback, container);
        mEditText = ( EditText ) view.findViewById( R.id.NewPlaylistName );
        
        getDialog().requestWindowFeature( Window.FEATURE_NO_TITLE );
        
        //mEditText.setOnEditorActionListener( this );
        mEditText.setSelected( false );
        
        view.findViewById( R.id.DialogConfirm ).setOnClickListener( headerButtonClickListener );
        view.findViewById( R.id.DialogCancel ).setOnClickListener( headerButtonClickListener );
        
        return view;
        
    }
	
    @Override public void onStart() {
    	super.onStart();
    	
    	
    	mEditText.performClick();
    	
    }
    
    private View.OnClickListener headerButtonClickListener = new View.OnClickListener() {
		
		@Override public void onClick(View v) {
			
			int id = v.getId();
			
			if ( id == R.id.DialogCancel ) {
				
				dismiss();
				
			} else if ( id == R.id.DialogConfirm ) {
				
				tracker.send( new HitBuilders.EventBuilder()
		    	.setCategory( "button" )
		    	.setAction( "click" )
		    	.setLabel( "feedback" )
		    	.build());
				
				sendFeedback();
				
			}
			
		}
		
	};
    
    private void sendFeedback() {
    	
    	String mFeedbackString = mEditText.getEditableText().toString();
    	
    	if ( mFeedbackString.length() > 0 ) {
	    	
	    	//Tracker tracker = TrackerSingleton.getDefaultTracker( getActivity() );
	    	
	    	tracker.send( new HitBuilders.EventBuilder()
	    	.setCategory( "feedback" )
	    	.setAction( "send" )
	    	.setLabel( mFeedbackString )
	    	.build());
	    	
	    	Toast.makeText( getActivity(), getString( R.string.feedback_thanks ), Toast.LENGTH_LONG ).show();
	    	GoogleAnalytics.getInstance( getActivity().getBaseContext() ).dispatchLocalHits();
    		
    	}
    	
    	dismiss();
    	
    }
    
}
