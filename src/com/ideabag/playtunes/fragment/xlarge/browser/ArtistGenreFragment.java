package com.ideabag.playtunes.fragment.xlarge.browser;

import com.google.android.gms.analytics.HitBuilders;
import com.ideabag.playtunes.R;
import com.ideabag.playtunes.activity.MainActivity;
import com.ideabag.playtunes.adapter.ArtistsAllAdapter;
import com.ideabag.playtunes.database.MediaQuery;
import com.ideabag.playtunes.util.GAEvent.Categories;
import com.ideabag.playtunes.util.GAEvent.Playlist;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

public class ArtistGenreFragment extends ListFragment {
	
	ArtistsAllAdapter adapter;
	
	FilteredAlbumsFragment mAlbumsFragment;
	
	MainActivity mActivity;

	@Override public void onAttach( Activity activity ) {
		super.onAttach(activity);
		
		mActivity = ( MainActivity ) activity;
		
	}
	
	@Override public void onActivityCreated( Bundle savedInstanceState ) {
		super.onActivityCreated(savedInstanceState);
		
		adapter = new ArtistsAllAdapter( getActivity(), new MediaQuery.OnQueryCompletedListener() {
			
			@Override public void onQueryCompleted( MediaQuery mQuery, Cursor mResult ) {
				
				mActivity.setActionbarTitle( getString( R.string.all_songs ) );
				
			}
			
		});
		
		//MediaQuery.execute( getActivity(), query)
		
		getView().setBackgroundColor( getResources().getColor( android.R.color.white ) );
		getListView().setDivider( getResources().getDrawable( R.drawable.list_divider ) );
		getListView().setDividerHeight( 1 );
		getListView().setSelector( R.drawable.list_item_background );
		
		//getListView().addHeaderView( mActivity.AdContainer, null, true );
		
		setListAdapter( adapter );
		
		getActivity().getContentResolver().registerContentObserver(
				MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, true, mediaStoreChanged );
		
	}
	
	@Override public void onResume() {
		super.onResume();
		
		
		
	}
	
	@Override public void onListItemClick( ListView l, View v, int position, long id ) {
		
		String artistID = ( String ) v.getTag( R.id.tag_artist_id );
		//String genreID = ( String ) v.getTag( R.id.tag_genre_id );
		
		if ( mAlbumsFragment == null ) {
			
			mAlbumsFragment = ( FilteredAlbumsFragment ) getFragmentManager().findFragmentById( R.id.ArtistsAlbumsPickerFragment );
			
		}
		
		if ( mAlbumsFragment != null ) {
			
			if ( artistID != null ) {
				
				mAlbumsFragment.setArtistID( artistID );
				
				MediaQuery mGetArtistName = new MediaQuery(
						MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
						new String[] {
						    	
						    	MediaStore.Audio.Artists.ARTIST,
								MediaStore.Audio.Artists._ID
							
						},
						MediaStore.Audio.Artists._ID + " =?",
						new String[] {
							
								artistID
								
						},
						null
					);
				
				MediaQuery.executeAsync( getActivity(), mGetArtistName, new MediaQuery.OnQueryCompletedListener() {
					
					@Override public void onQueryCompleted(MediaQuery mQuery, Cursor mResult) {
						
						if ( mResult != null && mResult.getCount() > 0 ) {
							
							mResult.moveToFirst();
							
							try {
								
								mActivity.setActionbarTitle( mResult.getString( mResult.getColumnIndexOrThrow( MediaStore.Audio.Artists.ARTIST ) ) );
								//mActivity.setActionbarSubtitle( mResult.getCount() + " " + ( mResult.getCount() == 1 ? getString( R.string.song_singular) : getString( R.string.songs_plural) ) );
								
							} catch( Exception e ) {
								
								mActivity.setActionbarTitle( null );
								//mActivity.setActionbarSubtitle( null );
								
							}
							
							//restoreScrollPosition();
							
						}
						
						if ( mResult != null && !mResult.isClosed() ) {
							
							mResult.close();
							
						}
						
					}
					
				});
				
			}/* else if ( genreID != null ) {
				
				mAlbumsFragment.setGenreID( genreID );
				
			}*/
			
		}
		
	}
	
	ContentObserver mediaStoreChanged = new ContentObserver(new Handler()) {

        @Override public void onChange( boolean selfChange ) {
            /*
            mActivity.runOnUiThread( new Runnable() {

				@Override public void run() {
					
					saveScrollPosition();
					adapter.requery();
				
				}
            	
            });
            */
            super.onChange( selfChange );
            
        }

	};
	
	
	
}
