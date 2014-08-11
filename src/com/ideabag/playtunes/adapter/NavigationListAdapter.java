package com.ideabag.playtunes.adapter;
import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff.Mode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationListAdapter extends BaseAdapter {
	
	private static final int ROW_COUNT = 6;
	
	private static final int[] badge_resources = {
		
		R.drawable.ic_action_star_10,
		R.drawable.ic_action_mic,
		R.drawable.ic_action_record,
		R.drawable.ic_action_guitar,
		R.drawable.ic_action_music_2,
		
		R.drawable.ic_action_list_2
		
	};
	
	private static final int[] label_string_resources = {
		
		R.string.playlist_name_starred,
		R.string.artists_plural,
		R.string.albums_plural,
		R.string.genres_plural,
		R.string.songs_plural,
		
		R.string.playlists_plural
		
	};
	
	public static final int ARTISTS = 4;
	public static final int ALBUMS = 1;
	public static final int GENRES = 2;
	public static final int SONGS = 3;
	public static final int STARRED = 0;
	public static final int PLAYLISTS = 5;
	
	
	private Context mContext;
	
	public NavigationListAdapter( Context context ) {
		
		mContext = context;
		
	}
	
	@Override public int getCount() {
		
		return ROW_COUNT;
		
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	@Override public View getView( int position, View convertView, ViewGroup parent ) {
		
		if ( null == convertView ) {
			
			LayoutInflater li = ( LayoutInflater ) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			
			convertView = li.inflate( R.layout.list_item_navigation_title, null );
			
		}
		
		String title = mContext.getResources().getString( label_string_resources[ position ] );
		int icon_resource = badge_resources[ position ];
		
		int badgeCount = 0;
		
		if ( position == ARTISTS ) {
			
			Cursor artists = mContext.getContentResolver().query(
					MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
					new String[] {
							
						MediaStore.Audio.Artists._ID	
							
					},
					null,
					null,
					null);
			
			badgeCount = artists.getCount();
			artists.close();
			
		} else if ( position == ALBUMS ) {
			
			Cursor albums = mContext.getContentResolver().query(
					MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
					new String[] {
							
						MediaStore.Audio.Albums._ID	
							
					},
					MediaStore.Audio.Media.ALBUM + "!=?",
					new String[] {
						
						mContext.getString( R.string.no_album_string )
						
					},
					null);
			
			badgeCount = albums.getCount();
			albums.close();
			
		} else if ( position == GENRES ) {
			
			Cursor genres = mContext.getContentResolver().query(
					MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
					new String[] {
							
						MediaStore.Audio.Genres._ID	
							
					},
					null,
					null,
					null);
			
			badgeCount = genres.getCount();
			genres.close();
			
		} else if ( position == SONGS ) {
			
			Cursor songs = mContext.getContentResolver().query(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					new String[] {
							
						MediaStore.Audio.Media._ID	
							
					},
					MediaStore.Audio.Media.IS_MUSIC + " != 0",
					null,
					null);
			
			badgeCount = songs.getCount();
			songs.close();
			
		} else if ( position == STARRED) {
			
			PlaylistManager mPlaylistManager = new PlaylistManager( mContext );
			Cursor starredPlaylist = mPlaylistManager.getStarredCursor();
			
			badgeCount = starredPlaylist.getCount();
			starredPlaylist.close();
			
		} else if ( position == PLAYLISTS ) {
			
			Cursor playlists = mContext.getContentResolver().query(
					MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
					new String[] {
							
						MediaStore.Audio.Playlists._ID	
							
					},
					null,
					null,
					null);
			
			badgeCount = playlists.getCount();
			playlists.close();
			
		}
		
		( ( TextView ) convertView.findViewById( R.id.Title )).setText( title );
		ImageView badgeIcon = ( ImageView ) convertView.findViewById( R.id.BadgeIcon );
		badgeIcon.setImageResource( icon_resource );
		
		int badgeColor = mContext.getResources().getColor( R.color.textColorPrimary );
		
		
		if ( position != STARRED ) {
			
			badgeIcon.setColorFilter( new LightingColorFilter( badgeColor, badgeColor ) );
			
		}
		
		( ( TextView ) convertView.findViewById( R.id.BadgeCount )).setText( "" + badgeCount );
		
		//.setColorFilter( Color.RED, Mode.MULTIPLY );
		
		return convertView;
		
	}

}
