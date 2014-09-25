/*   
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ideabag.playtunes.media;

import com.ideabag.playtunes.MusicPlayerService;
import com.ideabag.playtunes.util.GAEvent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * Receives broadcasted intents. In particular, we are interested in the
 * android.media.AUDIO_BECOMING_NOISY and android.intent.action.MEDIA_BUTTON intents, which is
 * broadcast, for example, when the user disconnects the headphones. This class works because we are
 * declaring it in a &lt;receiver&gt; tag in AndroidManifest.xml.
 */
public class MusicIntentReceiver extends BroadcastReceiver {
	
    @Override public void onReceive( Context context, Intent intent ) {
    	
    	String action = intent.getAction();
    	
    	Intent mIntent = new Intent();
        
    	
        if ( action.equals( android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY ) ) {
        	
        	context.sendBroadcast( new Intent( MusicPlayerService.ACTION_PAUSE ) );

        } else if ( action.equals( Intent.ACTION_MEDIA_BUTTON ) ) {
	        	
	        if ( intent.hasExtra( Intent.EXTRA_KEY_EVENT ) ) {
	        	
	            KeyEvent keyEvent = ( KeyEvent ) intent.getExtras().get( Intent.EXTRA_KEY_EVENT );
	            
	            if ( keyEvent.getAction() != KeyEvent.ACTION_DOWN )
	                return;
	            
	            
	            mIntent.addCategory( GAEvent.Categories.LOCKSCREEN );
	            
	            switch ( keyEvent.getKeyCode() ) {
	            	
	                case KeyEvent.KEYCODE_HEADSETHOOK:
	                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
	                    
	                	mIntent.setAction( MusicPlayerService.ACTION_PLAY_OR_PAUSE );
	                	
	                	context.sendBroadcast( mIntent );
	                    
	                    break;
	                    
	                case KeyEvent.KEYCODE_MEDIA_PLAY:
	                	
	                	mIntent.setAction( MusicPlayerService.ACTION_PLAY );
	                	
	                    context.sendBroadcast( mIntent );
	                    
	                    break;
	                    
	                case KeyEvent.KEYCODE_MEDIA_PAUSE:
	                    
	                	mIntent.setAction( MusicPlayerService.ACTION_PAUSE );
	                	
	                	context.sendBroadcast( mIntent );
	                    
	                    break;
	                /*
	                case KeyEvent.KEYCODE_MEDIA_STOP:
	                	
	                    context.startService( new Intent( MusicPlayerService.ACTION_STOP ) );
	                    
	                    break;
	                 */
	                case KeyEvent.KEYCODE_MEDIA_NEXT:
	                	
	                	mIntent.setAction( MusicPlayerService.ACTION_NEXT );
	                	
	                    context.sendBroadcast( mIntent );
	                    
	                    break;
	                    
	                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
	                    
	                	mIntent.setAction( MusicPlayerService.ACTION_BACK );
	                	
	                    context.sendBroadcast( mIntent );
	                    
	                    break;
	                    
	            }
	            
	        }
        	
        }
        
    }
    
}
