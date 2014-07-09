package com.ideabag.playtunes;

import android.app.AlertDialog;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

public class PlaylistManager {
	
	public static final String TAG = "PlaylistManager";
	
	public final static String STARRED_PLAYLIST_NAME = "Starred";
	
	private Context mContext;
	private ContentResolver mResolver;
	private Uri playlistsUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
	
	public String STARRED_PLAYLIST_ID = null;
	
	private SharedPreferences prefs;
	
	public PlaylistManager( Context context ) {
		
		mContext = context;
		mResolver = mContext.getContentResolver();
		
		prefs = context.getSharedPreferences( STARRED_PLAYLIST_NAME, Context.MODE_PRIVATE );
		
		//mResolver.update(uri, values, where, selectionArgs)
		
	}
	
	// Add Song to Playlist
	public boolean addSong( String playlist_id, String song_id ) {
		
		Cursor songCursor = mResolver.query(
			MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
        	new String[] {
    	    	"*"
    		},
            MediaStore.Audio.Media._ID + "=?",
            new String[] {
				song_id
			},
            null);
		
		addSongsInCursorToPlaylist( songCursor, playlist_id);
		
		return true;
		
	}
	
    private void addSongsInCursorToPlaylist( Cursor c, String mPlaylistId ) {
    	
	    int mIdCol;
	    int mCount;
	    
	    ContentProviderClient mCRC = null;
	    
	    try {
	        
	    	mCount = c.getCount();
	        mIdCol = c.getColumnIndex( MediaStore.Audio.Media._ID );
	        
	        ContentValues[] mInsertList = new ContentValues[1];
	        mInsertList[0] = new ContentValues();
	        
	        //int mPlaylistId  = mPrefs.getInt(AM.PLAYLIST_NOWPLAYING_ID, AM.PLAYLIST_ID_DEFAULT);
	        Uri mUri = MediaStore.Audio.Playlists.Members.getContentUri( "external", Long.parseLong( mPlaylistId ) );
	        Cursor c2 = mResolver.query(mUri, 
	        		new String[] {
	        	    	MediaStore.Audio.Playlists.Members._ID,
	        	    	MediaStore.Audio.Playlists.Members.PLAY_ORDER
	        		},
	                null,
	                null,
	                MediaStore.Audio.Playlists.Members.PLAY_ORDER + " DESC ");
	        
	        int mPlayOrder = 1;
	        
	        if ( c2 != null ) {
	        	
	            if ( c2.moveToFirst() ) {
	            	
	                mPlayOrder = ( c2.getInt( c2.getColumnIndex( MediaStore.Audio.Playlists.Members.PLAY_ORDER ) ) ) + 1;
	                
	            }
	            
	            c2.close();
	            
	        }
	        
	        //mCRC = mResolver.acquireContentProviderClient( mUri );
	        
	        //mCRC.
	        
	        for ( int i = 0; i < mCount; i++ ) {
	        	
	            if ( c.moveToPosition( i ) ) {
	            	
	                mInsertList[ 0 ].put( MediaStore.Audio.Playlists.Members.AUDIO_ID, c.getLong( mIdCol ) );
	                mInsertList[ 0 ].put( MediaStore.Audio.Playlists.Members.PLAY_ORDER, mPlayOrder++ );
	                mResolver.insert( mUri, mInsertList[ 0 ] );
	                
	            }
	            
	            //mCRC.release();
	            
	        }
	        
	    } catch ( Throwable t ) {
	        
	    	t.printStackTrace();
	        
	    }
	
	}
	    // Projection to get high water mark of PLAY_ORDER in a particular playlist

	// Projection to get the list of song IDs to be added to a playlist
	/*
    public static final String[] PROJECTION_SONGS_ADDTOPLAYLIST = new String[] {
	    MediaStore.Audio.Media._ID,
	};
	*/
	
	// Remove Song from playlist
	public boolean removeSong( String playlist_id, String song_id ) {
		
		
		mResolver.delete( MediaStore.Audio.Playlists.Members.getContentUri( "external", Long.parseLong( playlist_id ) ),
				MediaStore.Audio.Playlists.Members.AUDIO_ID + "=?",
				new String[] {
					song_id
				}
				);
		
		return true;
		
	}
	
	public void createPlaylistDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder( mContext );
		builder.setTitle("New Playlist Name");
		
		// Set up the input
		final EditText input = new EditText( mContext );
		//input.setTransformationMethod( SingleLineTransformationMethod.getInstance() );
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setInputType( InputType.TYPE_CLASS_TEXT );
		input.setFocusable( true );
		input.setFocusableInTouchMode( true );
		//input.clearFocus();
		builder.setView( input );

		// Set up the buttons
		builder.setPositiveButton( "Ok", new DialogInterface.OnClickListener() {
			
		    @Override public void onClick( DialogInterface dialog, int which ) {
		        // Create playlist
		    	String playlistName = input.getText().toString();
		    	createPlaylist( playlistName );
		        Toast.makeText(mContext, "Created playlist " + playlistName, Toast.LENGTH_SHORT).show();
		        
		    }
		    
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    
			@Override public void onClick( DialogInterface dialog, int which ) {
				
		        dialog.cancel();
		        
		    }
		    
		});

		
		
		builder.show();
		
		// TODO: Get the software keyboard to pop up so the user can immediately start writing the name
		
		input.performClick();
		//InputMethodManager mgr = ( InputMethodManager ) mContext.getSystemService( Context.INPUT_METHOD_SERVICE );
		// only will trigger it if no physical keyboard is open
		//mgr.showSoftInput( input, InputMethodManager.SHOW_IMPLICIT );
		
	}
	
	// Create new playlist
	// Returns the playlist_id
	//
	public int createPlaylist( String playlistName ) {
		
		String[] PROJECTION_PLAYLIST = new String[] {
			MediaStore.Audio.Playlists._ID,
			MediaStore.Audio.Playlists.NAME,
			MediaStore.Audio.Playlists.DATA
		};
		
		int playlist_id = -1;
		
		ContentValues values = new ContentValues();
        
		values.put( MediaStore.Audio.Playlists.NAME, playlistName );
		values.put( MediaStore.Audio.Playlists.DATE_ADDED, System.currentTimeMillis() );
		values.put( MediaStore.Audio.Playlists.DATE_MODIFIED, System.currentTimeMillis() );
        Uri mUri = mResolver.insert( MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values );
        
        
        if ( null != mUri ) {
        	
        	//playlist_id = -1;
            //mResult = FM.SUCCESS;
            Cursor c = mResolver.query( mUri, PROJECTION_PLAYLIST, null, null, null );
            
            if ( c != null ) {
            	c.moveToFirst();
                // Save the newly created ID so it can be selected.  Names are allowed to be duplicated,
                // but IDs can never be.
            	playlist_id = c.getInt( c.getColumnIndex( MediaStore.Audio.Playlists._ID ) );
                c.close();
                
            }
            
            
        }
		
		return playlist_id;
		
	}
	
	// 
	// 
	// Delete Playlist
	// 
	// 
	public boolean deletePlaylist( String playlist_id ) {
		
		mResolver.delete( playlistsUri,
				MediaStore.Audio.Playlists._ID + "=?",
				new String[] {
					
				playlist_id
					
				}
			);
		
		return true;
		
	}
	
	
	public boolean renamePlaylist( String playlist_id, String new_name ) {
		
		ContentValues updateValues = new ContentValues();
		
		updateValues.put( MediaStore.Audio.Playlists.NAME, new_name );
		
		mResolver.update( playlistsUri,
				updateValues,
				MediaStore.Audio.Playlists._ID + "=?",
				new String[] {
					
				playlist_id
					
				});
		
		return true;
		
	}
	
	// 
	// Starred Songs / Favorites
	// 
	
	public boolean addFavorite( String song_id ) {
		
		// check if favorite playlist exists, create if it doesn't
		String playlist_id = createStarredIfNotExist();
		
		addSong( playlist_id, song_id );
		
		return true;
		
	}
	
	public boolean removeFavorite( String song_id ) {
		
		// check if favorite playlist exists, create if it doesn't
		String playlist_id = createStarredIfNotExist();
		
		removeSong( playlist_id, song_id );
		
		return true;
		
	}
	
	private static final String[] starredPlaylistSelection = new String[] {
		MediaStore.Audio.Playlists.Members.PLAYLIST_ID,
		MediaStore.Audio.Playlists.Members.AUDIO_ID,
		MediaStore.Audio.Playlists.Members._ID
    };
	
	public boolean isStarred( String song_id ) {
		
		boolean isStarred = false;
		String starred_playlist_id = createStarredIfNotExist();
		
		Cursor starredQueryCursor = mResolver.query(
				
				MediaStore.Audio.Playlists.Members.getContentUri( "external", Long.parseLong( starred_playlist_id ) ),
			    
				starredPlaylistSelection,
			    
			    MediaStore.Audio.Playlists.Members.AUDIO_ID + "=? AND " + MediaStore.Audio.Playlists.Members.PLAYLIST_ID + "=?",
			    
			    new String[] {
					
					song_id,
					starred_playlist_id
					
				},
				
				null
			);
		
		
		isStarred = ( null != starredQueryCursor && starredQueryCursor.getCount() > 0 );
		/*
		if ( starredQueryCursor.getCount() > 1 ) {
			
			android.util.Log.i( "Weird Case", song_id + " : " + starredQueryCursor.getCount() );
			
		} else if ( starredQueryCursor.getCount() == 1 ) {
			
			//android.util.Log.i( "Correct Case", song_id + " : " + starredQueryCursor.getCount() );
			
		} else {
			
			//android.util.Log.i( "Not starred", song_id + " : " + starredQueryCursor.getCount() );
			
		}
		*/
		starredQueryCursor.close();
		
		
		return isStarred;
		
	}
	
	public Cursor getStarredCursor() {
		
		String starred_playlist_id = createStarredIfNotExist();
		
		Cursor starredQueryCursor = mResolver.query(
				
				MediaStore.Audio.Playlists.Members.getContentUri( "external", Long.parseLong( starred_playlist_id ) ),
			    
				new String[] {
					
					MediaStore.Audio.Playlists.Members.AUDIO_ID
					
			    },
			    null,
			    null,
				null
			);
		
		return starredQueryCursor;
		
	}
	
	
	public String createStarredIfNotExist() {
		
		if ( null == STARRED_PLAYLIST_ID ) {
			
			STARRED_PLAYLIST_ID = prefs.getString( STARRED_PLAYLIST_NAME, null );
			
		}
		
		if ( null == STARRED_PLAYLIST_ID) {
			
			Cursor starredQueryCursor = mResolver.query(
					
					playlistsUri,
				    
					new String[] {
				    	
				    	MediaStore.Audio.Playlists.NAME,
						MediaStore.Audio.Playlists._ID
					
				    },
				    
				    MediaStore.Audio.Playlists.NAME + "=?",
			
			new String[] {
				
					"Starred"
						
					},
					
					null
				);
			
			if ( 0 == starredQueryCursor.getCount()) {
				
				createPlaylist( "Starred" );
				
				starredQueryCursor = mResolver.query(
						playlistsUri,
					    new String[] {
					    	
					    	MediaStore.Audio.Playlists.NAME,
							MediaStore.Audio.Playlists._ID
							
					    },
						null,
						null,
						null
					);
				
				starredQueryCursor.moveToFirst();
				STARRED_PLAYLIST_ID = starredQueryCursor.getString( starredQueryCursor.getColumnIndex( MediaStore.Audio.Playlists._ID ) );
				
			} else {
				
				starredQueryCursor.moveToFirst();
				STARRED_PLAYLIST_ID = starredQueryCursor.getString( starredQueryCursor.getColumnIndex( MediaStore.Audio.Playlists._ID ) );
				
			}
			
			SharedPreferences.Editor editor = prefs.edit();
			
			editor.putString( STARRED_PLAYLIST_NAME, STARRED_PLAYLIST_ID );
			
			editor.commit();
			
			
		}
		
		//android.util.Log.i("starred playlist id", STARRED_PLAYLIST_ID );
		
		return STARRED_PLAYLIST_ID;
		
	}
	
	// Reorder songs in a playlist
	//moveItem(ContentResolver res, long playlistId, int from, int to)
}
