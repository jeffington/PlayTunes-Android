package com.ideabag.playtunes.util;

import com.ideabag.playtunes.R;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

public class CommandUtils {
	
	public enum Commands {
		ALL_ARTISTS,
		ONE_ARTIST,
		ALL_GENRES,
		ONE_GENRE,
		ALL_ALBUMS,
		ONE_ALBUM,
		
		
	};
	
	public static final int COMMAND_ARTISTS_CODE = 0x800;
	public static final int COMMAND_ARTIST_CODE = 0x1000;
	public static final int COMMAND_GENRES_CODE = 0x1800;
	public static final int COMMAND_GENRE_CODE = 0x2000;
	public static final int COMMAND_NOW_PLAYING_CODE = 0x2800;
	public static final int COMMAND_ALBUMS_CODE = 0x3000;
	public static final int COMMAND_ALBUM_CODE = 0x3800;
	public static final int COMMAND_PLAYLISTS_CODE = 0x4000;
	public static final int COMMAND_PLAYLIST_CODE = 0x4800;
	public static final int COMMAND_SONGS_CODE = 0x5000;
	public static final int COMMAND_ARTIST_SINGLES_CODE = 0x5800;
	public static final int COMMAND_ARTIST_ALL_CODE = 0x6000;
	
	public static final int ID_KEY = 0x7FF;
	public static final int COMMAND_KEY = 0xF800;
	
	private static final String[] singleAlbumSelection = new String[] { // This is the same as...
		
		MediaStore.Audio.Media.TITLE,
		MediaStore.Audio.Media.ARTIST,
		MediaStore.Audio.Media.TRACK,
		MediaStore.Audio.Media.ALBUM,
		MediaStore.Audio.Media.DATA,
		MediaStore.Audio.Media.ALBUM_ID,
		MediaStore.Audio.Media._ID
		
	};
	
	private static final String[] artistsSelection = new String[] {
		
    	MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists._ID 
	
	};
	
	private static final String[] artistsAllSelection = new String[] {
		
		MediaStore.Audio.Artists.Albums.ALBUM,
		MediaStore.Audio.Artists.Albums.ARTIST,
		MediaStore.Audio.Albums._ID
		
	};
	
    private static final String[] allSongsSelection = new String[] { // This...
    	
    	MediaStore.Audio.Media.TITLE,
    	MediaStore.Audio.Media.ARTIST,
    	MediaStore.Audio.Media.TRACK,
    	MediaStore.Audio.Media.ALBUM,
    	MediaStore.Audio.Media.DATA,
    	MediaStore.Audio.Media.ALBUM_ID,
    	MediaStore.Audio.Media.ARTIST_ID,
    	MediaStore.Audio.Media._ID
    	
    };
    
    private static final String[] allAlbumsSelection = new String[] {
    	
    	MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums._ID
    	
    };
    
    private static final String[] allPlaylistsSelection = new String[] {
    	
    	MediaStore.Audio.Playlists.NAME,
		MediaStore.Audio.Playlists._ID
	
    };
    
    private static final String[] singlePlaylistSelection = new String[] { // And this...
    	
		MediaStore.Audio.Media.TITLE,
		MediaStore.Audio.Media.ARTIST,
		MediaStore.Audio.Media.TRACK,
		MediaStore.Audio.Media.ALBUM,
		MediaStore.Audio.Media.DATA,
		MediaStore.Audio.Media.ALBUM_ID,
		MediaStore.Audio.Playlists._ID,
		
	};
    
    private static final String[] allGenresSelection = new String[] {
    	
		MediaStore.Audio.Genres.NAME,
		MediaStore.Audio.Genres._ID
		
    };
    private static final String[] singleGenreSelection = new String[] {
    	
		MediaStore.Audio.Genres.Members.TITLE,
		MediaStore.Audio.Genres.Members.ARTIST,
		MediaStore.Audio.Genres.Members.ALBUM,
		MediaStore.Audio.Genres.Members.DATA,
		MediaStore.Audio.Media.ALBUM_ID,
		//MediaStore.Audio.Genres.Members.
		MediaStore.Audio.Genres.Members._ID,
		
	};
/*
return new PlayTunesListAdapter(context, R.layout.tabletext, c, singleGenreSelection, new int[] { R.id.toptext, R.id.bottomtext, R.id.trackAlbum });
 */
	public static Cursor getCursor( Context context, int command ) {
		
    	int id = ID_KEY & command;
    	int cmd = COMMAND_KEY & command;
		
    	switch( cmd ) {
    	
    		case COMMAND_ALBUM_CODE:
    			
    		    return context.getContentResolver().query(
    		    		MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    		    		singleAlbumSelection,
    		    		MediaStore.Audio.Media.ALBUM_ID + "=" + id,
    		    		null,
    		    		MediaStore.Audio.Media.TRACK
    		    	);
    		
    		case COMMAND_ALBUMS_CODE:
    			
    			return context.getContentResolver().query(
    					MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
    					allAlbumsSelection,
    					null,
    					null,
    					MediaStore.Audio.Albums.ALBUM
    				);
    		
    		case COMMAND_ARTISTS_CODE:
    			
    			return context.getContentResolver().query(
    					MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
    					artistsSelection,
    					null,
    					null,
    					MediaStore.Audio.Artists.ARTIST
    				);
    		
    		case COMMAND_ARTIST_CODE:
    			
    			return context.getContentResolver().query(
    					MediaStore.Audio.Artists.Albums.getContentUri("external", id),
    					artistsAllSelection,
    					MediaStore.Audio.Artists.Albums.ALBUM + "!='Music'",
    					null,
    					MediaStore.Audio.Albums.ALBUM
    				);
    		
    		case COMMAND_PLAYLISTS_CODE:
    			
    			return context.getContentResolver().query(
    					MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
    					allPlaylistsSelection,
    					null,
    					null,
    					MediaStore.Audio.Playlists.NAME
    				);
    		
    		case COMMAND_PLAYLIST_CODE:
    		
    			return context.getContentResolver().query(
    					MediaStore.Audio.Playlists.Members.getContentUri("external", id),
    					singlePlaylistSelection,
    					null,
    					null,
    					MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER
    				);
    		
    		case COMMAND_SONGS_CODE:
    			
    			return context.getContentResolver().query(
    					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    					allSongsSelection,
    					null,
    					null,
    					MediaStore.Audio.Media.TITLE
    				);
    		
    		case COMMAND_GENRES_CODE:
    			
    			return context.getContentResolver().query(
    					MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
    					allGenresSelection,
    					null,
    					null,
    					MediaStore.Audio.Genres.NAME
    				);
    		
    		case COMMAND_GENRE_CODE:
    			
    			return context.getContentResolver().query(
    					MediaStore.Audio.Genres.Members.getContentUri("external", id), 
    					singleGenreSelection, 
    					null, 
    					null, 
    					MediaStore.Audio.Genres.Members.TITLE
    				);
    		
    		case COMMAND_ARTIST_SINGLES_CODE:
    			
    			return context.getContentResolver().query(
    					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    					allSongsSelection,
    					MediaStore.Audio.Media.ARTIST_ID + "=" + id + " AND " + MediaStore.Audio.Media.ALBUM + "='Music'",
    					null,
    					MediaStore.Audio.Media.TITLE
    				);
    		
    		case COMMAND_ARTIST_ALL_CODE:
    			
    			return context.getContentResolver().query(
    					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    					allSongsSelection,
    					MediaStore.Audio.Media.ARTIST_ID + "=" + id,
    					null,
    					MediaStore.Audio.Media.TITLE
    				);
    			
    	}
    	
    	return null;
    	
	}
	/*
	public static PlayTunesListAdapter getAdapter( Context context, Cursor c, int command ) {
		
		int cmd = COMMAND_KEY & command;
		
		switch ( cmd ) {
		
		case COMMAND_ALBUM_CODE:
			
			return new PlayTunesListAdapter(context, R.layout.basicrow, c, singleAlbumSelection, new int[] { R.id.basicRowText });
		
		case COMMAND_ALBUMS_CODE:
			
			return new PlayTunesListAdapter(context, R.layout.basicrow, c, allAlbumsSelection, new int[] { R.id.basicRowText });
		
		case COMMAND_ARTISTS_CODE:
			
			return new PlayTunesListAdapter(context, R.layout.basicrow, c, artistsSelection, new int[] { R.id.basicRowText });
		
		case COMMAND_ARTIST_CODE:
			
			return new PlayTunesListAdapter(context, R.layout.basicrow, c, artistsAllSelection, new int[] { R.id.basicRowText });
		
		case COMMAND_PLAYLISTS_CODE:
			
			return new PlayTunesListAdapter(context, R.layout.basicrow, c, allPlaylistsSelection, new int[] { R.id.basicRowText });
		
		case COMMAND_PLAYLIST_CODE:
			
			return new PlayTunesListAdapter(context, R.layout.tabletext, c, singlePlaylistSelection, new int[] { R.id.toptext, R.id.trackAlbum });
		
		case COMMAND_SONGS_CODE:
	
			return new PlayTunesListAdapter(context, R.layout.tabletext, c, allSongsSelection, new int[] { R.id.toptext, R.id.bottomtext, R.id.trackNumber, R.id.trackAlbum });
		
		case COMMAND_GENRES_CODE:
	
			return new PlayTunesListAdapter(context, R.layout.basicrow, c, allGenresSelection, new int[] { R.id.basicRowText });
		
		case COMMAND_GENRE_CODE:
	
			return new PlayTunesListAdapter(context, R.layout.tabletext, c, singleGenreSelection, new int[] { R.id.toptext, R.id.bottomtext, R.id.trackAlbum });
		
		case COMMAND_ARTIST_SINGLES_CODE:
	
			return new PlayTunesListAdapter(context, R.layout.tabletext, c, allSongsSelection, new int[] { R.id.toptext, R.id.bottomtext, R.id.trackNumber, R.id.trackAlbum });
		
		case COMMAND_ARTIST_ALL_CODE:
	
			return new PlayTunesListAdapter(context, R.layout.tabletext, c, allSongsSelection, new int[] { R.id.toptext, R.id.bottomtext, R.id.trackNumber, R.id.trackAlbum });
		
		}
	
		return null;
	
	}
	*/
	
	public static String getCommandText( Context context, int command ) {

		Cursor c;
		String str;
		int cmd = COMMAND_KEY & command;
		int id = ID_KEY & command;
		
		switch ( cmd ) {
			
		case COMMAND_ALBUM_CODE:
				c = context.getContentResolver().query( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, singleAlbumSelection, MediaStore.Audio.Media.ALBUM_ID + "=" + id, null, null );
				
				if ( !c.moveToFirst() ) {
					
					str = "";
					
				} else {
					
					str = c.getString( c.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM ) );
					
				}
				c.close();
				
				return str;
				
			case COMMAND_ALBUMS_CODE:
				
				return context.getString( R.string.albums );
			
			case COMMAND_ARTISTS_CODE:
				
				return context.getString( R.string.artists );
			
			case COMMAND_ARTIST_SINGLES_CODE:
			case COMMAND_ARTIST_ALL_CODE:
			case COMMAND_ARTIST_CODE: //MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists._ID 
				
				c = context.getContentResolver().query(
						MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
						artistsSelection,
						MediaStore.Audio.Artists._ID + "=" + id,
						null,
						MediaStore.Audio.Artists.ARTIST
					);
				
				if ( !c.moveToFirst() ) {
					
					str = "";
					
				} else {
					
					str = c.getString( c.getColumnIndexOrThrow( MediaStore.Audio.Artists.ARTIST ) );
					
				}
				
				return str;
			
			case COMMAND_PLAYLISTS_CODE:
				
				return context.getString( R.string.playlists );
				
			case COMMAND_PLAYLIST_CODE:
				
    			c = context.getContentResolver().query(
    					MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
    					allPlaylistsSelection,
    					MediaStore.Audio.Playlists._ID + "=" + id,
    					null,
    					MediaStore.Audio.Playlists.NAME
    				);
    			
				if ( !c.moveToFirst() ) {
					
					str = "";
					
				} else {
					
					str = c.getString( c.getColumnIndexOrThrow( MediaStore.Audio.Playlists.NAME ) );
					
				}
				
				c.close();
				return str;
			
			case COMMAND_SONGS_CODE:
				
				return context.getString( R.string.songs );
			
			case COMMAND_GENRES_CODE:
				return context.getString( R.string.genres );
			
			case COMMAND_GENRE_CODE:
				
				c = context.getContentResolver().query(
						MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
						allGenresSelection,
						MediaStore.Audio.Genres._ID + "=" + id,
						null,
						MediaStore.Audio.Genres.NAME
					);
				
    			if ( !c.moveToFirst() ) {
    				
    				str = "";
    				
    			} else {
					
    				str = c.getString( c.getColumnIndexOrThrow( MediaStore.Audio.Playlists.NAME ) );
					
    			}
    			
				c.close();
				return str;
				
		}
		
		return "";
	}
	
	public static String getSubtitleText( Context context, int command ) {
		
		int cmd = COMMAND_KEY & command;
		
		switch ( cmd ) {
		
		case COMMAND_SONGS_CODE:
			return context.getString(R.string.songs);
			
		case COMMAND_GENRES_CODE:
			return context.getString(R.string.genres);
			
		case COMMAND_ALBUMS_CODE:
			return context.getString(R.string.albums);
			
		case COMMAND_PLAYLISTS_CODE:
			return context.getString(R.string.playlists);
			
		case COMMAND_ARTISTS_CODE:
			return context.getString(R.string.artists);
			
		case COMMAND_ARTIST_SINGLES_CODE:
			return "singles";
			
		default:
			return "tracks";
			
		}
		
	}
	
}
