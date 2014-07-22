package com.ideabag.playtunes;

import com.ideabag.playtunes.activity.MainActivity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;

public class PlaybackNotification {
	
	private static final char DASH_SYMBOL = 0x2013;
	
	private static final int PAUSE_ICON_RESOURCE = R.drawable.ic_action_playback_pause_white;
	private static final int TUNE_ICON_RESOURCE = R.drawable.ic_action_music_2_white;
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
		
		openIntent.putExtra( "now_playing", true );
		
		contentIntent = PendingIntent.getActivity( mContext, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT );
		
		playIntent = new Intent( MusicPlayerService.ACTION_PLAY_OR_PAUSE );
		nextTrackIntent = new Intent( MusicPlayerService.ACTION_NEXT );
		closeIntent = new Intent( MusicPlayerService.ACTION_CLOSE );
		
		
		playPendingIntent = PendingIntent.getBroadcast( mContext, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT );
		nextTrackPendingIntent = PendingIntent.getBroadcast( mContext, 0, nextTrackIntent, PendingIntent.FLAG_UPDATE_CURRENT );
		closePendingIntent = PendingIntent.getBroadcast( mContext, 0, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT );
		
		
		mRemoteViews = new RemoteViews( mContext.getPackageName(), R.layout.notification_layout ); 
		
		mRemoteViews.setOnClickPendingIntent( R.id.NotificationPlayPauseButton, playPendingIntent );
		
		mRemoteViews.setOnClickPendingIntent( R.id.NotificationNextButton, nextTrackPendingIntent );
		
		mRemoteViews.setOnClickPendingIntent( R.id.NotificationCloseButton, closePendingIntent );
		
	}
	
	private void buildAndShowNotification() {
		
		String tickerString = "";
		
		if ( null != mSongCursor ) {
			
			mSongCursor.moveToFirst();
			
			String title = mSongCursor.getString( mSongCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.TITLE ) );
			String artist = mSongCursor.getString( mSongCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) );
			
			tickerString = title + " " + Character.toString( DASH_SYMBOL ) + " " + artist;
			
		}
		
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder( mContext )
		        .setSmallIcon( TUNE_ICON_RESOURCE )
				.setContent( mRemoteViews )
                .setOngoing( true )
                .setTicker( tickerString )
		        .setContentIntent( contentIntent );
		
		mNotificationManager.notify( PLAY_NOTIFICATION_ID, mBuilder.build() );
		
	}
	
	public void showSong( String song_content_id ) {
		
		MEDIA_ID = song_content_id;
		
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
		
		String title = mSongCursor.getString( mSongCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.TITLE ) );
		String artist = mSongCursor.getString( mSongCursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) );
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
		albumCursor.moveToFirst();
		
		String newAlbumUri = albumCursor.getString( albumCursor.getColumnIndexOrThrow( MediaStore.Audio.Albums.ALBUM_ART ) );
		
		if ( !newAlbumUri.equals( lastAlbumUri ) ) {
			
			lastAlbumUri = newAlbumUri;
			
			Uri albumArtUri = Uri.parse( newAlbumUri );
			
			mRemoteViews.setImageViewUri( R.id.NotificationAlbumArt, albumArtUri );
			
		}
		
		mRemoteViews.setTextViewText( R.id.NotificationSongName, title );
		mRemoteViews.setTextViewText( R.id.NotificationArtistName, artist );
		
		buildAndShowNotification();
		
	}
	
	public void showPlaying() {
		
		mRemoteViews.setImageViewResource( R.id.NotificationPlayPauseButton, R.drawable.ic_action_playback_pause_white );
		
		buildAndShowNotification();
		
	}
	
	public void showPaused() {
		
		mRemoteViews.setImageViewResource( R.id.NotificationPlayPauseButton, R.drawable.ic_action_playback_play_white );
		
		buildAndShowNotification();
		
	}
	
	public void remove() {
		
		if ( null != mSongCursor ) {
			
			mSongCursor.close();
			
		}
		
		mNotificationManager.cancel( PLAY_NOTIFICATION_ID );
		
	}
	
	public void setMediaId( String media_id ) {
		
		MEDIA_ID = media_id;
		
	}
	
}
