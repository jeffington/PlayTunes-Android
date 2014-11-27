package com.ideabag.playtunes.service;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
import android.provider.MediaStore;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.media.MediaButtonHelper;
import com.ideabag.playtunes.media.MusicIntentReceiver;
import com.ideabag.playtunes.media.RemoteControlClientCompat;
import com.ideabag.playtunes.media.RemoteControlClientCompat.MetadataEditorCompat;
import com.ideabag.playtunes.media.RemoteControlHelper;

public class LockscreenManager {
	
	public static final String TAG = "LockscreenManager";
	
	private Context mContext;
	private String mAlbumArtUri = null;
	private String media_id = null;
	
	private boolean isPlaying = false;
	
	// our RemoteControlClient object, which will use remote control APIs available in
    // SDK level >= 14, if they're available.
    RemoteControlClientCompat mRemoteControlClientCompat = null;
    
    // The component name of MusicIntentReceiver, for use with media button and remote control
    // APIs
    ComponentName mMediaButtonReceiverComponent;
    
    private AudioManager mAudioManager;
    
    public LockscreenManager( Context context ) {
    	
		if ( android.os.Build.VERSION.SDK_INT >= 14 ) {
			
	        if ( mRemoteControlClientCompat == null ) {
	        	
	        	mContext = context;
	        	
	        	mAudioManager = ( AudioManager ) mContext.getSystemService( Context.AUDIO_SERVICE );
				mMediaButtonReceiverComponent = new ComponentName( mContext, MusicIntentReceiver.class );
				MediaButtonHelper.registerMediaButtonEventReceiverCompat( mAudioManager, mMediaButtonReceiverComponent );
	            final Intent intent = new Intent( Intent.ACTION_MEDIA_BUTTON );
	            //intent.setAction(  );
	            intent.setComponent( mMediaButtonReceiverComponent );
	            mRemoteControlClientCompat = new RemoteControlClientCompat(
	                    PendingIntent.getBroadcast(mContext /*context*/,
	                            0 /*requestCode, ignored*/, intent /*intent*/, 0 /*flags*/));
	            
	            RemoteControlHelper.registerRemoteControlClient( mAudioManager, mRemoteControlClientCompat );
	            
	        }
	        
		}
    	
    }
    
    
    @SuppressLint("InlinedApi")
	public void play() {
    	
    	if ( null != mRemoteControlClientCompat ) {
	        
			mRemoteControlClientCompat.setPlaybackState( RemoteControlClient.PLAYSTATE_PLAYING );
			
		}
    	
    	
    	
    	isPlaying = true;
    	
    }
    
    @SuppressLint("InlinedApi")
	public void pause() {
    	
		if ( null != mRemoteControlClientCompat ) {
			
			mRemoteControlClientCompat.setPlaybackState( RemoteControlClient.PLAYSTATE_PAUSED );
			
		}
		
		isPlaying = false;
    	
    }
    
    public void remove() {
    	
		if ( null != mRemoteControlClientCompat ) {
			
			RemoteControlHelper.unregisterRemoteControlClient( mAudioManager, mRemoteControlClientCompat );
			MediaButtonHelper.unregisterMediaButtonEventReceiverCompat( mAudioManager, mMediaButtonReceiverComponent );
			
			mRemoteControlClientCompat = null;
			
		}
    	
    }
    
    public boolean ready() {
    	
    	return ( null != mRemoteControlClientCompat );
    	
    }

	@SuppressLint("InlinedApi")
	public void setMediaID( String id, boolean prev, boolean next ) {
		
		if ( null != mRemoteControlClientCompat && id != null && !id.equals( media_id ) ) {
			
			media_id = id;
			
			MediaQuery mSongQuery = new MediaQuery(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					new String[] {
						
						MediaStore.Audio.Media.ALBUM,
						MediaStore.Audio.Media.ARTIST,
						MediaStore.Audio.Media.TITLE,
						MediaStore.Audio.Media.ALBUM_ID,
						
					},
					MediaStore.Audio.Media._ID + "=?",
					new String[] {
						
							media_id
						
					},
					null
				);
			
			MediaQuery.executeAsync( mContext, mSongQuery, mCurrentSongQueryListener );
			
	        int mFlags = RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE;
	        
	        if ( next ) {
	        	
	        	mFlags |= RemoteControlClient.FLAG_KEY_MEDIA_NEXT;
	        	
	        }
	        
	        if ( prev ) {
	        	
	        	mFlags |= RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS;
	        	
	        }
	        
	        mRemoteControlClientCompat.setTransportControlFlags( mFlags );
	
	        // Update the remote controls
	        
	        
		}
	        
	}
	
	MediaQuery.OnQueryCompletedListener mCurrentSongQueryListener = new MediaQuery.OnQueryCompletedListener() {
		
		@SuppressLint("InlinedApi")
		@Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {

			mResult.moveToFirst();
			
			String mSongTitle = mResult.getString( mResult.getColumnIndex(MediaStore.Audio.Media.TITLE ) );
			String mSongAlbum = mResult.getString( mResult.getColumnIndex(MediaStore.Audio.Media.ALBUM ) );
			String mSongArtist = mResult.getString( mResult.getColumnIndex(MediaStore.Audio.Media.ARTIST ) );
			String album_id = mResult.getString( mResult.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM_ID ) );
			
			mResult.close();
			
		    MetadataEditorCompat mMetadataEditor;
			mMetadataEditor = mRemoteControlClientCompat.editMetadata( true );
			
	        
			mMetadataEditor.putString( MediaMetadataRetriever.METADATA_KEY_ARTIST, mSongArtist );
			mMetadataEditor.putString( MediaMetadataRetriever.METADATA_KEY_ALBUM, mSongAlbum );
			mMetadataEditor.putString( MediaMetadataRetriever.METADATA_KEY_TITLE, mSongTitle );
	        
			mMetadataEditor.apply();
			
			// 
			// Don't judge me!
			// 
			// 
			
			mMetadataEditor = mRemoteControlClientCompat.editMetadata( true );
			
	        
			mMetadataEditor.putString( MediaMetadataRetriever.METADATA_KEY_ARTIST, mSongArtist );
			mMetadataEditor.putString( MediaMetadataRetriever.METADATA_KEY_ALBUM, mSongAlbum );
			mMetadataEditor.putString( MediaMetadataRetriever.METADATA_KEY_TITLE, mSongTitle );
	        
			mMetadataEditor.apply();
			
	        
			MediaQuery mAlbumArtQuery = new MediaQuery(
					MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
				    new String[] {
				    	
				    	MediaStore.Audio.Albums.ALBUM_ART,
				    	MediaStore.Audio.Albums._ID
				    	
				    },
				    MediaStore.Audio.Albums._ID + "=?",
					new String[] {
						
						album_id
						
					},
					null
				);
			
			MediaQuery.executeAsync( mContext, mAlbumArtQuery, new MediaQuery.OnQueryCompletedListener() {
				
				@Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {
					
					mResult.moveToFirst();
					
					String newAlbumUri = mResult.getString( mResult.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM_ART ) );
					mResult.close();
				    
					
					MetadataEditorCompat mMetadataEditor = mRemoteControlClientCompat.editMetadata( false );
					
					try {
						
						Bitmap mAlbumArtBitmap = null;
						
						if ( null == newAlbumUri ) {
							
							mAlbumArtBitmap = BitmapFactory.decodeResource( mContext.getResources(), R.drawable.no_album_art_full );
							mMetadataEditor.putBitmap( RemoteControlClientCompat.MetadataEditorCompat.METADATA_KEY_ARTWORK, mAlbumArtBitmap );
							
						} else if ( !newAlbumUri.equals( mAlbumArtUri ) ) {
							
							mAlbumArtBitmap = BitmapFactory.decodeFile( newAlbumUri );
							mMetadataEditor.putBitmap( RemoteControlClientCompat.MetadataEditorCompat.METADATA_KEY_ARTWORK, mAlbumArtBitmap );
							
						}
						
						mMetadataEditor.apply();
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				
			});
			
			
			if ( isPlaying ) {
				
				mRemoteControlClientCompat.setPlaybackState( RemoteControlClient.PLAYSTATE_PLAYING );
				
			} else {
				
				mRemoteControlClientCompat.setPlaybackState( RemoteControlClient.PLAYSTATE_PAUSED );
				
			}
			
		}
		
	};
    
}
