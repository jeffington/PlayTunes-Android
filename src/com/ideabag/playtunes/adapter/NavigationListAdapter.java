package com.ideabag.playtunes.adapter;
import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.util.QueryCountTask;

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
		
		R.drawable.ic_action_search,
		R.drawable.ic_action_star_10,
		R.drawable.ic_action_mic,
		R.drawable.ic_action_record,
		
		R.drawable.ic_action_music_2,
		R.drawable.ic_action_guitar,
		R.drawable.ic_action_list_2,
		
		
	};
	
	private static final int[] label_string_resources = {
		
		R.string.search,
		R.string.playlist_name_starred,
		R.string.artists_plural,
		R.string.albums_plural,
		
		R.string.songs_plural,
		R.string.genres_plural,
		R.string.playlists_plural,
		
		
	};
	
	public static final int SEARCH = 0;
	public static final int STARRED = 1;
	
	public static final int ARTISTS = 2;
	public static final int ALBUMS = 3;
	public static final int SONGS = 4;
	public static final int GENRES = 5;
	public static final int PLAYLISTS = 6;
	
	
	
	private Context mContext;
	
	MediaQuery mAllArtistQuery;
	MediaQuery mAllSongsQuery;
	MediaQuery mAllAlbumsQuery;
	MediaQuery mAllGenresQuery;
	MediaQuery mAllPlaylistsQuery;
	MediaQuery mStarredQuery;
	
	
	public NavigationListAdapter( Context context ) {
		
		mContext = context;
		
		mAllArtistQuery = new MediaQuery(
				MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
				new String[] {
						
					MediaStore.Audio.Artists._ID	
						
				},
				null,
				null,
				null);
		
		mAllAlbumsQuery = new MediaQuery(
				MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
		new String[] {
				
			MediaStore.Audio.Albums._ID	
				
		},
		MediaStore.Audio.Media.ALBUM + "!=?",
		new String[] {
			
			mContext.getString( R.string.no_album_string )
			
		},
		null);
		
		mAllGenresQuery = new MediaQuery(
				MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
				new String[] {
						
					MediaStore.Audio.Genres._ID	
						
				},
				null,
				null,
				null);
		mAllSongsQuery = new MediaQuery(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] {
						
					MediaStore.Audio.Media._ID	
						
				},
				MediaStore.Audio.Media.IS_MUSIC + " != 0",
				null,
				null);
		
		mAllPlaylistsQuery = new MediaQuery(
						MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
						new String[] {
								
							MediaStore.Audio.Playlists._ID	
								
						},
						null,
						null,
						null);
		
		String starred_playlist_id = new PlaylistManager( mContext ).createStarredIfNotExist();
		
		mStarredQuery = new MediaQuery(
				
				MediaStore.Audio.Playlists.Members.getContentUri( "external", Long.parseLong( starred_playlist_id ) ),
			    
				new String[] {
					
					MediaStore.Audio.Playlists.Members.AUDIO_ID
					
			    },
			    null,
			    null,
				null
			);
		
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
		
		ViewHolder holder;
		
		if ( null == convertView ) {
			
			holder = new ViewHolder();
			
			LayoutInflater li = ( LayoutInflater ) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			convertView = li.inflate( R.layout.list_item_navigation_title, null );
			
			holder.title = ( TextView ) convertView.findViewById( R.id.Title );
			holder.badgeCount =  ( TextView ) convertView.findViewById( R.id.BadgeCount );
			holder.badgeIcon = ( ImageView ) convertView.findViewById( R.id.BadgeIcon );
			
			convertView.setTag( holder );
			
		} else {
			
			holder = ( ViewHolder ) convertView.getTag();
			
		}
		
		if ( position == ARTISTS ) {
			
			new QueryCountTask( holder.badgeCount ).execute( mAllArtistQuery );
			
		} else if ( position == ALBUMS ) {
			
			new QueryCountTask( holder.badgeCount ).execute( mAllAlbumsQuery );
			
		} else if ( position == GENRES ) {
			
			new QueryCountTask( holder.badgeCount ).execute( mAllGenresQuery );
			
		} else if ( position == SONGS ) {
			
			new QueryCountTask( holder.badgeCount ).execute( mAllSongsQuery );
			
		} else if ( position == STARRED) {
			
			new QueryCountTask( holder.badgeCount ).execute( mStarredQuery );
			
		} else if ( position == PLAYLISTS ) {
			
			new QueryCountTask( holder.badgeCount ).execute( mAllPlaylistsQuery );
			
		}

		String title = mContext.getResources().getString( label_string_resources[ position ] );
		int icon_resource = badge_resources[ position ];
		
		holder.title.setText( title );
		
		holder.badgeIcon.setImageResource( icon_resource );
		
		int badgeColor = mContext.getResources().getColor( R.color.textColorPrimary );
		
		
		if ( position != STARRED ) {
			
			holder.badgeIcon.setColorFilter( new LightingColorFilter( badgeColor, badgeColor ) );
			
		}
		
		//.setColorFilter( Color.RED, Mode.MULTIPLY );
		
		return convertView;
		
	}
	
	static class ViewHolder {
		
		TextView badgeCount;
		ImageView badgeIcon;
		TextView title;
		
	}

}
