package com.ideabag.playtunes.fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ideabag.playtunes.PlaylistManager;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.DragNDrop.DragListener;
import com.ideabag.playtunes.DragNDrop.DragNDropListView;
import com.ideabag.playtunes.DragNDrop.DropListener;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.PlaylistsOneAdapter;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.dialog.SongMenuDialogFragment;
import com.ideabag.playtunes.util.IMusicBrowser;
import com.ideabag.playtunes.util.TrackerSingleton;
import com.ideabag.playtunes.util.GAEvent.Categories;
import com.ideabag.playtunes.util.GAEvent.Playlist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class PlaylistsOneFragment extends Fragment implements IMusicBrowser, AdapterView.OnItemClickListener {
	
	public static final String TAG = "One Playlist Fragment";
	public static final String STARRED_TAG = "Starred Playlist Fragment";
	
	MainActivity mActivity;
	private Tracker mTracker;
	PlaylistsOneAdapter adapter;
	
	private String PLAYLIST_ID = "";
	
	private MenuItem menuItemEdit, menuItemDoneEditing;
	
	private String mTitle, mSubtitle;
	
	private DragNDropListView mListView;
	
	private boolean isEditing = false;
	
	private boolean isStarred = false;
	
	private static final String KEY_POSTION = "position_y";
	private static final String KEY_OFFSET = "offset_y";
	
	protected int mSavedScrollOffset = 0;
	protected int mSavedScrollListPosition = 0;
	
	
	@Override public void setMediaID( String media_id ) {
		
		PLAYLIST_ID = media_id;
		
	}
	
	@Override public String getMediaID() { return PLAYLIST_ID; }
	
	@Override public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );
		outState.putString( getString( R.string.key_state_media_id ), PLAYLIST_ID );
		outState.putBoolean( getString( R.string.key_state_playlist_editing ), isEditing );
		outState.putInt( KEY_POSTION, mSavedScrollListPosition );
		outState.putInt( KEY_OFFSET, mSavedScrollOffset );
		
	}
	
	@Override public void onAttach( Activity activity ) {
			
		super.onAttach( activity );
		
		mActivity = ( MainActivity ) activity;
		mTracker = TrackerSingleton.getDefaultTracker( mActivity );
		
		// Set screen name.
		mTracker.setScreenName( TAG );
				
		mActivity.setActionbarTitle( null );
    	mActivity.setActionbarSubtitle( null );
    	
    	
    	
		MediaQuery mGetPlaylistName = new MediaQuery(
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
				new String[] {
				    	
				    	MediaStore.Audio.Playlists.NAME,
						MediaStore.Audio.Playlists._ID
					
				},
				MediaStore.Audio.Playlists._ID + " =?",
				new String[] {
					
						PLAYLIST_ID
						
				},
				null
			);
    	
		MediaQuery.executeAsync( getActivity(), mGetPlaylistName, new MediaQuery.OnQueryCompletedListener() {
			
			@Override public void onQueryCompleted(MediaQuery mQuery, Cursor mResult) {
				
				if ( mResult != null && mResult.getCount() > 0 ) {
					
					mResult.moveToFirst();
					
					try {
						
						mActivity.setActionbarTitle( mResult.getString( mResult.getColumnIndexOrThrow( MediaStore.Audio.Playlists.NAME ) ) );
								
					} catch( Exception e ) {
						
						mActivity.setActionbarTitle( null );
						
					}
					
				}
				
				mListView.setSelectionFromTop( mSavedScrollListPosition, mSavedScrollOffset );
				
				if ( mResult != null && !mResult.isClosed() ) {
					
					mResult.close();
					
				}
				
			}
			
		});
    	
	}
    
	@SuppressLint("NewApi")
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated( savedInstanceState );
		
		if ( null != savedInstanceState ) {
			
			PLAYLIST_ID = savedInstanceState.getString( getString( R.string.key_state_media_id ) );
			isEditing = savedInstanceState.getBoolean( getString( R.string.key_state_playlist_editing ) );
			mSavedScrollListPosition = savedInstanceState.getInt( KEY_POSTION );
			mSavedScrollOffset = savedInstanceState.getInt( KEY_OFFSET );
			
		}
		
		setRetainInstance( true );
		setHasOptionsMenu( true );
		
		// Adapter is asyncronous
    	adapter = new PlaylistsOneAdapter( mActivity, PLAYLIST_ID, songMenuClickListener, new MediaQuery.OnQueryCompletedListener() {
			
			@Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {
				
				mActivity.setActionbarSubtitle( mResult.getCount() + " " + ( mResult.getCount() == 1 ? getString( R.string.song_singular) : getString( R.string.songs_plural) ) );
				mListView.setSelectionFromTop( mSavedScrollListPosition, mSavedScrollOffset );
				
				mTracker.send( new HitBuilders.EventBuilder()
		    	.setCategory( isStarred ? Categories.STARRED_PLAYLIST : Categories.PLAYLIST )
		    	.setAction( Playlist.ACTION_SHOWLIST )
		    	.setValue( adapter.getCount() )
		    	.build());
				
			}
			
		} );
    	
    	// Configure for editing (or not)
    	adapter.setEditing( isEditing );
    	mListView.setDraggingEnabled( isEditing );
    	
    	
    	if ( android.os.Build.VERSION.SDK_INT < 11 && android.os.Build.VERSION.SDK_INT > 8) {
    		
    		mListView.setOverScrollMode( AbsListView.OVER_SCROLL_NEVER );
    		
    	}
    	
    	mListView.setDivider( getResources().getDrawable( R.drawable.list_divider ) );
    	mListView.setDividerHeight( 1 );
    	mListView.setSelector( R.drawable.list_item_background );
    	
		
		// Dumb thing to have a bottom divider shown
    	mListView.setFooterDividersEnabled( true );
    	mListView.addFooterView( new View( getActivity() ), null, true);
		//
    	
    	
    	mListView.setAdapter( adapter );
		
    	mListView.setOnItemClickListener( this );
    	mListView.setOnItemLongClickListener( mSongMenuLongClickListener );
    	
		getActivity().getContentResolver().registerContentObserver(
				MediaStore.Audio.Playlists.Members.getContentUri( "external", Long.parseLong( PLAYLIST_ID ) ), true, mediaStoreChanged );
		getActivity().getContentResolver().registerContentObserver(
				MediaStore.Audio.Playlists.Members.getContentUri( "external", Long.parseLong( new PlaylistManager( getActivity() ).createStarredIfNotExist() ) ), true, mediaStoreChanged );
		
		mListView.setDropListener( mSongDropListener );
		
		mListView.setDragListener( mSongDragListener );
		
		
	}
	
	@Override public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		
		mListView = new DragNDropListView( getActivity(), null );
		
		return mListView;
		
	}
	
	@Override public void onPause() {
		super.onPause();
		
		saveScrollPosition();
		
	}
	
	@Override public void onResume() {
		super.onResume();
		
		mTracker.send( new HitBuilders.AppViewBuilder().build() );
		
	}
	
	@Override public void onDestroy() {
		super.onDestroy();
		
		setHasOptionsMenu( false );
		getActivity().getContentResolver().unregisterContentObserver( mediaStoreChanged );
		
	}
	
	@Override public void onItemClick( AdapterView<?> adapterView, View v, int position, long id ) {
		
		if ( adapter.isEditing ) {
			
			Toast.makeText( getActivity(), getString( R.string.playlist_cant_play_when_editing ), Toast.LENGTH_SHORT ).show();
			
		} else {
			
			String playlistName = mActivity.getSupportActionBar().getTitle().toString();
			
			mActivity.mBoundService.setPlaylist( adapter.getQuery(), playlistName, PlaylistsOneFragment.class, PLAYLIST_ID );
			
			mActivity.mBoundService.setPlaylistPosition( position );
			
			mActivity.mBoundService.play();
			
		}
		
		mTracker.send( new HitBuilders.EventBuilder()
    	.setCategory( isStarred ? Categories.STARRED_PLAYLIST : Categories.PLAYLIST )
    	.setAction( Playlist.ACTION_CLICK )
    	.setValue( position )
    	.build());
		
	}
	
	// if ( adapter.isEditing ) {
	
	protected AdapterView.OnItemLongClickListener mSongMenuLongClickListener = new AdapterView.OnItemLongClickListener() {
		
		@Override public boolean onItemLongClick( AdapterView<?> arg0, View v, int position, long id ) {
			
			if ( adapter.isEditing ) {
				
				Toast.makeText( getActivity(), getString( R.string.playlist_cant_play_when_editing ), Toast.LENGTH_SHORT ).show();
				
			} else {
				
				showSongMenuDialog( "" + id );
				
			}
			
			mTracker.send( new HitBuilders.EventBuilder()
	    	.setCategory( isStarred ? Categories.STARRED_PLAYLIST : Categories.PLAYLIST )
	    	.setAction( Playlist.ACTION_LONGCLICK )
	    	.setValue( position )
	    	.build());
			
			return true;
			
		}
		
	};
	
	private DropListener mSongDropListener = new DropListener() {

		@Override public void onDrop( int from, int to ) {
			
			mActivity.PlaylistManager.moveTrack( PLAYLIST_ID, from, to );
			
		}
		
	};
	
	private DragListener mSongDragListener = new DragListener() {

		@Override public void onStartDrag( int itemIndex, View itemView ) {
			
			//android.util.Log.i( TAG, "" + itemView.getId() );
			//itemView.requestFocus();
			//itemView.requestFocusFromTouch();
			
			
		}

		@SuppressLint("NewApi")
		@Override public void onDrag( int x, int y, ListView listView ) {
			
			int mListHeight = listView.getHeight();
			
			int mSafeYMin = mListHeight / 3;
			int mSafeYMax = ( 2 * mListHeight ) / 3;
			
			int mAmountToScrollY = 0;
			
			if ( y < mSafeYMin ) {
				
				int mYDiff = Math.abs( y - mSafeYMin );
				// Scrolling up means a negative distance
				mAmountToScrollY = -1 * calculateScrollYDistance( mYDiff, mSafeYMin );
				
				//android.util.Log.i( TAG, "Scroll up: " + mAmountToScrollY );
				
			} else if ( y > mSafeYMax ) {
				
				int mYDiff = y - mSafeYMax;
				
				mAmountToScrollY = calculateScrollYDistance( mYDiff, mSafeYMin );
				//android.util.Log.i( TAG, "");
				//android.util.Log.i( TAG, "Scroll down: " + mAmountToScrollY );
			}
			
			if ( mAmountToScrollY != 0 ) {
				
				if ( android.os.Build.VERSION.SDK_INT >= 19 ) {
					
					listView.scrollListBy( mAmountToScrollY );
					
				} else {
					
					//http://stackoverflow.com/questions/3014089/maintain-save-restore-scroll-position-when-returning-to-a-listview
					int index = listView.getFirstVisiblePosition();
					View v = listView.getChildAt(0);
					int top = (v == null) ? 0 : v.getTop();
					
					listView.setSelectionFromTop(index, ( top - mAmountToScrollY ) );
					
				}
				
			}
			
		}

		@Override public void onStopDrag( View itemView ) {
			
			
			
		}
		
		
	};
	
	@Override public void onCreateOptionsMenu( Menu menu, MenuInflater inflater) {
		
	   inflater.inflate( R.menu.menu_playlist_one, menu );
	   
	   menuItemEdit = menu.findItem( R.id.MenuPlaylistEdit );
	   
	   menuItemDoneEditing = menu.findItem( R.id.MenuPlaylistDone );
	   
	   menuItemDoneEditing.setVisible( isEditing );
	   
	   menuItemEdit.setVisible( !isEditing );
	   
	}
	
	@Override public void onPrepareOptionsMenu( Menu menu ) {
		
		super.onPrepareOptionsMenu( menu );
		
		if ( !mActivity.mShouldHideActionItems ) {
			
			menuItemDoneEditing.setVisible( isEditing );
			
			menuItemEdit.setVisible( !isEditing );
	    	
		}
	    
	}
	
	
	@Override public boolean onOptionsItemSelected( MenuItem item ) {
		
	   switch ( item.getItemId() ) {
	   
	      case R.id.MenuPlaylistEdit:
	         
	    	  isEditing = true;
	    	  
	    	  menuItemDoneEditing.setVisible( isEditing );
	    	  menuItemEdit.setVisible( !isEditing );
	    	  
	    	  adapter.setEditing( isEditing );
	    	  mListView.setDraggingEnabled( isEditing );
	    	  
	    	  mTitle = (String) mActivity.getSupportActionBar().getTitle();
	    	  mSubtitle = (String) mActivity.getSupportActionBar().getSubtitle();
	    	  mActivity.setActionbarSubtitle( mTitle );
	    	  mActivity.setActionbarTitle( getActivity().getString( R.string.playlist_editing ) );
	    	  
	         return true;
	         
	      case R.id.MenuPlaylistDone:
	    	  
	    	  isEditing = false;
	    	  
	    	  menuItemDoneEditing.setVisible( isEditing );
	    	  menuItemEdit.setVisible( !isEditing );
	    	  adapter.setEditing( isEditing );
	    	  mListView.setDraggingEnabled( isEditing );
	    	  
	    	  mActivity.setActionbarTitle( mTitle );
	    	  mActivity.setActionbarSubtitle( mSubtitle );
	    	  
	    	  
	    	  return true;
	    	  
	      default:
	         return super.onOptionsItemSelected(item);
	         
	   }
	   
	}
	
	View.OnClickListener songMenuClickListener = new View.OnClickListener() {
		
		@Override public void onClick( View v ) {
			
			int viewID = v.getId();
			String songID = "" + v.getTag( R.id.tag_song_id );
			
			if ( viewID == R.id.StarButton ) {
				
				ToggleButton starButton = ( ToggleButton ) v;
				
				if ( starButton.isChecked() ) {
					
					mActivity.PlaylistManager.addFavorite( songID );
					//android.util.Log.i( "starred", songID );
					
				} else {
					
					mActivity.PlaylistManager.removeFavorite( songID );
					//android.util.Log.i( "unstarred", songID );
					
				}
				
			} else if ( viewID == R.id.MenuButton ) {
				
				showSongMenuDialog( songID );
				
			} else if ( viewID == R.id.RemoveButton ) {
				
				mActivity.PlaylistManager.removeSong( PLAYLIST_ID, songID );
				
				Toast.makeText(getActivity(), getString( R.string.playlist_removed ), Toast.LENGTH_SHORT ).show();
				
			}
			
		}
		
	};
	
	protected void showSongMenuDialog( String songID ) {
		
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
    	
		SongMenuDialogFragment newFragment = new SongMenuDialogFragment();
		newFragment.setMediaID( songID );
    	
        newFragment.show( ft, "dialog" );
		
	}
	
	ContentObserver mediaStoreChanged = new ContentObserver( new Handler() ) {
		
        @Override public void onChange( boolean selfChange ) {
            
            mActivity.runOnUiThread( new Runnable() {

				@Override public void run() {
					
					saveScrollPosition();
					adapter.requery();
					adapter.notifyDataSetChanged();
					
				}
            	
            });
            
            super.onChange( selfChange );
            
        }

	};
	
	
	// 
	// The list is intended to scroll faster the closer the dragged item gets to the edge.
	// 
	// The amount you scroll is calculated with f(x) = e^(b * x)
	// Where b is the percentage inside of the scrolling zone you are times 5
	// 
	// 
	private int calculateScrollYDistance( int diff, int listHeight ) {
		
		//double mCoefficient = 0.00003 * listHeight;
		double mCoefficient = (double) diff / listHeight;
		
		
		
		double scrollAmount = Math.pow( Math.E, ( 5 * mCoefficient ) ) - 1;
		
		//android.util.Log.i( TAG, "Coefficient: " + mCoefficient +" amount to scroll: " + scrollAmount );
		
		return (int) scrollAmount;
		
	}
	
	private void saveScrollPosition() {
		
		try {
			
			if ( null != mListView ) {
				
				mSavedScrollListPosition = mListView.getFirstVisiblePosition();
				
				View v = mListView.getChildAt( 0 );
				mSavedScrollOffset = ( v == null ? 0 : v.getTop() );
				
			}
			
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
	}
	
}