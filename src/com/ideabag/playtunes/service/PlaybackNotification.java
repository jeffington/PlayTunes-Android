package com.ideabag.playtunes.service;

import com.ideabag.playtunes.R;
import com.ideabag.playtunes.R.drawable;
import com.ideabag.playtunes.R.id;
import com.ideabag.playtunes.R.layout;
import com.ideabag.playtunes.activity.MainActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.StaleDataException;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

public class PlaybackNotification {
	
	private static final char DASH_SYMBOL = 0x2013;
	
	public static final String NOW_PLAYING_EXTRA = "now_playing";
	
	//private static final int PAUSE_ICON_RESOURCE = R.drawable.ic_action_playback_pause_white;
	//private static final int TUNE_ICON_RESOURCE = R.drawable.ic_action_music_2_white;
	private static final int PLAY_NOTIFICATION_ID = 1;
	
	private Context mContext;
	
	private Cursor mSongCursor = null;
	
	private String lastAlbumUri = null;
	
	private NotificationManager mNotificationManager;
	private RemoteViews mRemoteViews;
	
	PendingIntent playPendingIntent, nextTrackPendingIntent, closePendingIntent, contentIntent;
	Intent playIntent, closeIntent, nextTrackIntent, openIntent;
	
	private String MEDIA_ID;
	
	public PlaybackNotification( Context context ) {
		
		mContext = context;
		
		mNotificationManager = ( NotificationManager ) mContext.getSystemService( Context.NOTIFICATION_SERVICE );
		
		openIntent = new Intent( mContext, MainActivity.class );
		
		openIntent.putExtra( NOW_PLAYING_EXTRA, true );
		
		contentIntent = PendingIntent.getActivity( mContext, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT );
		
		// 
		// Android 2.3 (and below) doesn't support clickable buttons in the notification. Ancient history!
		// 
		
		if ( android.os.Build.VERSION.SDK_INT >= 11 ) {
				
			playIntent = new Intent( MusicPlayerService.ACTION_PLAY_OR_PAUSE );
			nextTrackIntent = new Intent( MusicPlayerService.ACTION_NEXT );
			closeIntent = new Intent( MusicPlayerService.ACTION_CLOSE );
			
			
			playPendingIntent = PendingIntent.getBroadcast( mContext, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT );
			nextTrackPendingIntent = PendingIntent.getBroadcast( mContext, 0, nextTrackIntent, PendingIntent.FLAG_UPDATE_CURRENT );
			closePendingIntent = PendingIntent.getBroadcast( mContext, 0, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT );
			
		}
		
		mRemoteViews = new RemoteViews( mContext.getPackageName(), R.layout.notification_layout ); 
		
		if ( android.os.Build.VERSION.SDK_INT >= 11 ) {
			
			mRemoteViews.setOnClickPendingIntent( R.id.NotificationPlayPauseButton, playPendingIntent );
			mRemoteViews.setOnClickPendingIntent( R.id.NotificationNextButton, nextTrackPendingIntent );
			mRemoteViews.setOnClickPendingIntent( R.id.NotificationCloseButton, closePendingIntent );
			
		}
		
	}
	
	private void requery() {
		
		if ( null != mSongCursor && !mSongCursor.isClosed() ) {
			
			mSongCursor.close();
			
		}
		
		mSongCursor = mContext.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] {
					
					MediaStore.Audio.Media.ALBUM,
					MediaStore.Audio.Media.ARTIST,
					MediaStore.Audio.Media.TITLE,
					MediaStore.Audio.Media.ALBUM_ID,
					MediaStore.Audio.Media._ID
					
				},
				MediaStore.Audio.Media._ID + "=?",
				new String[] {
					
					MEDIA_ID
					
				},
				null
			);
		
		mSongCursor.moveToFirst();
		
	}
	
	private void buildAndShowNotification() {
		
		String tickerString = "";
		
		if ( null != mSongCursor ) {
			
			try {
				
				mSongCursor.moveToFirst();
				
			} catch ( StaleDataException e ) {
				
				requery();
				
			}
			
			String title = mSongCursor.getString( mSongCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.TITLE ) );
			String artist = mSongCursor.getString( mSongCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) );
			
			tickerString = title + " " + Character.toString( DASH_SYMBOL ) + " " + artist;
			
		}
		
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder( mContext )
				.setContent( mRemoteViews )
                .setOngoing( true )
                .setTicker( tickerString )
		        .setContentIntent( contentIntent );
		
		if ( android.os.Build.VERSION.SDK_INT < 9 ) {
			
	        mBuilder.setSmallIcon( R.drawable.ic_action_music_2 );
			
		} else {
			
			mBuilder.setSmallIcon( R.drawable.ic_action_music_2_white );
			
		}
		
		Notification mBuiltNotification = mBuilder.build();
		
		// Weird bug in compatibility library
		if ( android.os.Build.VERSION.SDK_INT < 11 ) {
			
			mBuiltNotification.contentView = mRemoteViews;
			
		}
		
		mNotificationManager.notify( PLAY_NOTIFICATION_ID, mBuiltNotification );
		
	}
	
	public void setMediaID( String song_content_id ) {
		
		if ( null != song_content_id ) {
			
			MEDIA_ID = song_content_id;
			
			requery();
			
			mSongCursor.moveToFirst();
			
			String title = mSongCursor.getString( mSongCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.TITLE ) );
			String artist = mSongCursor.getString( mSongCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) );
			
			if ( android.os.Build.VERSION.SDK_INT >= 11 ) {
				
				String album_id = mSongCursor.getString( mSongCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM_ID ) );
				
				Cursor albumCursor = mContext.getContentResolver().query(
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
				//android.util.Log.i( "album_id", album_id );
				//android.util.Log.i( "album count" , "" + albumCursor.getCount() );
				
				if ( null != albumCursor && albumCursor.getCount() > 0 ) {
					
					albumCursor.moveToFirst();
					
					String newAlbumUri = albumCursor.getString( albumCursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM_ART ) );
					albumCursor.close();
					
					lastAlbumUri = newAlbumUri;
					
					if ( newAlbumUri == null ) {
						
						mRemoteViews.setImageViewResource( R.id.NotificationAlbumArt, R.drawable.no_album_art_thumb );
						
					} else {
						
						
						
						Uri albumArtUri = Uri.parse( newAlbumUri );
						
						mRemoteViews.setImageViewUri( R.id.NotificationAlbumArt, albumArtUri );
						
						
						
					}
					
				}
				
			}
			
			
			
			mRemoteViews.setTextViewText( R.id.NotificationSongName, title );
			mRemoteViews.setTextViewText( R.id.NotificationArtistName, artist );
			
			buildAndShowNotification();
			
		} else { // media_id is null
			
			remove();
			
		}
		
	}
	
	public void showPlaying() {
		
		if ( android.os.Build.VERSION.SDK_INT >= 11 ) {
			
			mRemoteViews.setImageViewResource( R.id.NotificationPlayPauseButton, R.drawable.ic_action_playback_pause_white );
			
		}
		
		buildAndShowNotification();
		
	}
	
	public void showPaused() {
		
		if ( android.os.Build.VERSION.SDK_INT >= 11 ) {
			
			mRemoteViews.setImageViewResource( R.id.NotificationPlayPauseButton, R.drawable.ic_action_playback_play_white );
			
		}
		
		buildAndShowNotification();
		
	}
	
	public void remove() {
		
		if ( null != mSongCursor && !mSongCursor.isClosed() ) {
			
			mSongCursor.close();
			
		}
		
		mNotificationManager.cancel( PLAY_NOTIFICATION_ID );
		
	}
	/*
	public void setMediaId( String media_id ) {
		
		MEDIA_ID = media_id;
		
	}
	*/
}
