package com.ideabag.playtunes;

import java.util.ArrayList;

import com.google.ads.*;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PlayTunes extends PlayTunesActivity {
	Resources res;
	
	public static final String PLAYTUNES_UI_UPDATE = "com.ideabag.playtunes.action.UI_UPDATE";
	boolean shuffle;
	int repeat;
	
	Handler handle;
	Cursor cursor;
	PlayTunesListAdapter adapter;
	Animation fadeInAnimation;
	boolean isPlaying = false;
	boolean scrolling = false;
	
	private final BroadcastReceiver MusicPlayerServiceReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			if (action.equals(Intent.ACTION_MEDIA_EJECT) || action.equals(Intent.ACTION_MEDIA_UNMOUNTED) || action.equals(Intent.ACTION_MEDIA_REMOVED) || action.equals(Intent.ACTION_MEDIA_SHARED)) {
		    	findViewById(R.id.ListViewSongs).setVisibility(View.GONE);
		    	findViewById(R.id.Header).setVisibility(View.GONE);
		    	findViewById(R.id.SubHeader).setVisibility(View.GONE);
		    	if (cursor != null && !cursor.isClosed())
		    		cursor.close();
			} 
			else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {// || action.equals(Intent.ACTION_MEDIA_MOUNTED_READ_ONLY)) {
				findViewById(R.id.ListViewSongs).setVisibility(View.VISIBLE);
		    	findViewById(R.id.Header).setVisibility(View.VISIBLE);
		    	findViewById(R.id.SubHeader).setVisibility(View.VISIBLE);
		    	// Load up the last command (if any)
		    	if (!commandHistoryIsEmpty())
		    		executeCommand(commandHistoryPeek(), false);
		    	
			} else {
		        Bundle extras = intent.getExtras();
		        String albumArt = extras.getString("songAlbumArt");
		        ((TextView)findViewById(R.id.SongName)).setText(extras.getString("songName"));
	    		((TextView)findViewById(R.id.SongArtist)).setText(extras.getString("songArtist"));
	    		((TextView)findViewById(R.id.SongAlbum)).setText(extras.getString("songAlbum"));
	    		isPlaying = extras.getBoolean("isPlaying");
	    		if (extras.getInt("trackNumber") >= 0 && albumArt != null && !albumArt.equals(""))
	    			((ImageView)findViewById(R.id.AlbumCover)).setImageURI(android.net.Uri.parse(albumArt)); //Update with album art too
	    		else
	    			((ImageView)findViewById(R.id.AlbumCover)).setImageResource(R.drawable.albumart_unknown);
	    		
	    		shuffle = extras.getBoolean("shuffle");
	    		repeat = extras.getInt("loop");
	    		
				if (shuffle)
					((ImageView)findViewById(R.id.Shuffle)).setImageResource(R.drawable.ic_shuffle_on);
				else
					((ImageView)findViewById(R.id.Shuffle)).setImageResource(R.drawable.ic_shuffle_off);
				
				if (repeat == res.getInteger(R.integer.looping_no))
					((ImageView)findViewById(R.id.Repeat)).setImageResource(R.drawable.ic_repeat_off);
				else if (repeat == res.getInteger(R.integer.looping_all))
					((ImageView)findViewById(R.id.Repeat)).setImageResource(R.drawable.ic_repeat);
				else
					((ImageView)findViewById(R.id.Repeat)).setImageResource(R.drawable.ic_repeat_once);
	    		
				if (!isPlaying)
					hideSongInfo();
				else
					if (findViewById(R.id.SongInformation).getVisibility() != View.VISIBLE)
						resetTimer();
			}
			
	    }
	};
	
	AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
		public void onScrollStateChanged(AbsListView arg0, int scrollState) {
			if (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
				hideSongInfo();
				scrolling = true;
			} else {
				resetTimer();
				scrolling = false;
			}
		}
		public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		}
	};
	
	View.OnClickListener cornerClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			int id = v.getId();
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			switch(id) {
				case R.id.TopLeft:
					executeCommand(sp.getInt(getString(R.string.preference_topleft), res.getInteger(R.integer.albums_code)), true);
					break;
				case R.id.TopRight:
					executeCommand(sp.getInt(getString(R.string.preference_topright), res.getInteger(R.integer.artists_code)), true);
					break;
				case R.id.BottomLeft:
					executeCommand(sp.getInt(getString(R.string.preference_bottomleft), res.getInteger(R.integer.songs_code)), true);
					break;
				case R.id.BottomRight:
					executeCommand(sp.getInt(getString(R.string.preference_bottomright), res.getInteger(R.integer.playlists_code)), true);
					break;
			}
		}
	};
	
	ListView.OnItemClickListener listClickListener = new ListView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
			int pos = position - adapter.getHeaderCount();
			//cursor.moveToPosition(pos);
			
			int cmd, cmd_id;
			cmd = commandHistoryPeek() & res.getInteger(R.integer.command_key);
			cmd_id  = commandHistoryPeek() & res.getInteger(R.integer.id_key);
			
			if (adapter.getHeaderCount() > 0 && position < adapter.getHeaderCount()) { // A header was clicked
				cursor.moveToFirst();
				//if (cmd == res.getInteger(R.integer.playlists_code) && position == 0)
				//	android.widget.Toast.makeText(getBaseContext(), "Add new playlist clicked!", android.widget.Toast.LENGTH_SHORT).show();
				//else
				if (cmd == res.getInteger(R.integer.artist_code)) {
					if (position == 0)
						executeCommand(res.getInteger(R.integer.artist_all_code)+cmd_id, true);
					else if (position == 1)
						executeCommand(res.getInteger(R.integer.artist_singles_code)+cmd_id, true);
						//android.widget.Toast.makeText(getBaseContext(), "Singles clicked!", android.widget.Toast.LENGTH_SHORT).show(); //artist_singles_code
				}
				
			} // TODO: 
			else {
				cursor.moveToPosition(pos);
				if (cmd == res.getInteger(R.integer.artists_code))
					executeCommand(res.getInteger(R.integer.artist_code)+cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID )), true);
				else if (cmd == res.getInteger(R.integer.albums_code))
					executeCommand(res.getInteger(R.integer.album_code)+cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)), true);
				else if (cmd == res.getInteger(R.integer.artist_code))
					executeCommand(res.getInteger(R.integer.album_code)+cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)), true);
				else if (cmd == res.getInteger(R.integer.playlists_code))
					executeCommand(res.getInteger(R.integer.playlist_code)+cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists._ID)), true);
				else if (cmd == res.getInteger(R.integer.genres_code))
					executeCommand(res.getInteger(R.integer.genre_code)+cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID)), true);
				else
					play(pos);
			}
		}
	};
	
	
	View.OnClickListener songControlsClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			int id = v.getId();
			switch(id) {
			case R.id.PauseButton:
				sendBroadcast(new Intent(getString(R.string.action_pause)));
				break;
			case R.id.PlayButton:
				play(-1);
				break;
			case R.id.BackButton:
				sendBroadcast(new Intent(getString(R.string.action_prev)));
				break;
			case R.id.ForwardButton:
				sendBroadcast(new Intent(getString(R.string.action_next)));
				break;
			case R.id.Repeat:
				if (repeat == res.getInteger(R.integer.looping_no)) {
					repeat = res.getInteger(R.integer.looping_all);
					((ImageView)findViewById(R.id.Repeat)).setImageResource(R.drawable.ic_repeat);
					Toast.makeText(getBaseContext(), "Repeat all turned on.", Toast.LENGTH_SHORT).show();
				}
				else if (repeat == res.getInteger(R.integer.looping_all)) {
					repeat = res.getInteger(R.integer.looping_one);
					((ImageView)findViewById(R.id.Repeat)).setImageResource(R.drawable.ic_repeat_once);
					Toast.makeText(getBaseContext(), "Repeat current turned on.", Toast.LENGTH_SHORT).show();
				}
				else {
					repeat = res.getInteger(R.integer.looping_no);
					((ImageView)findViewById(R.id.Repeat)).setImageResource(R.drawable.ic_repeat_off);
					Toast.makeText(getBaseContext(), "Repeat turned off.", Toast.LENGTH_SHORT).show();
				}
				sendBroadcast(new Intent(getString(R.string.action_repeat)).putExtra("repeat", repeat));
				break;
			case R.id.Shuffle:
				if (shuffle) {
					((ImageView)findViewById(R.id.Shuffle)).setImageResource(R.drawable.ic_shuffle_off);
					shuffle = false;
					Toast.makeText(getBaseContext(), getString(R.string.shuffle_off), Toast.LENGTH_SHORT).show();
				}
				else {
					((ImageView)findViewById(R.id.Shuffle)).setImageResource(R.drawable.ic_shuffle_on);
					shuffle = true;
					Toast.makeText(getBaseContext(), getString(R.string.shuffle_on), Toast.LENGTH_SHORT).show();
				}
				sendBroadcast(new Intent(getString(R.string.action_shuffle)).putExtra("shuffle", shuffle));
				break;
			}
			
		}
	};
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        res = getResources();
        findViewById(R.id.TopLeft).setOnClickListener(cornerClickListener);
        findViewById(R.id.TopRight).setOnClickListener(cornerClickListener);
        findViewById(R.id.BottomLeft).setOnClickListener(cornerClickListener);
        findViewById(R.id.BottomRight).setOnClickListener(cornerClickListener);
        
        findViewById(R.id.PlayButton).setOnClickListener(songControlsClickListener);
        findViewById(R.id.PauseButton).setOnClickListener(songControlsClickListener);
        findViewById(R.id.ForwardButton).setOnClickListener(songControlsClickListener);
        findViewById(R.id.BackButton).setOnClickListener(songControlsClickListener);
        findViewById(R.id.Repeat).setOnClickListener(songControlsClickListener);
        findViewById(R.id.Shuffle).setOnClickListener(songControlsClickListener);
        findViewById(R.id.SongInformation).setOnClickListener(songInfoClickListener);
        
       fadeInAnimation = (Animation)AnimationUtils.loadAnimation(this, R.anim.fadein);
       fadeInAnimation.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				showSongInfo();
			}
			public void onAnimationRepeat(Animation animation) { }
			public void onAnimationStart(Animation animation) {
			}
        });
        ListView list = (ListView)findViewById(R.id.ListViewSongs);
        list.setOnScrollListener(scrollListener);
        list.setAdapter(adapter);
        list.setOnItemClickListener(listClickListener);
        
        handle = new Handler();
        
        commandHistory = (int[]) getLastNonConfigurationInstance();
        if (getIntent().hasExtra("command"))
        	executeCommand(getIntent().getIntExtra("command", -1), false);
        
     // Look up the AdView as a resource and load a request.
        //AdRequest request = new AdRequest();
        //request.setTesting(true);
        AdView adView = (AdView)findViewById(R.id.ad);
        adView.loadAd(new AdRequest());
    }
    
    @Override
    public void onStart() {
    	super.onStart();
        if (!commandHistoryIsEmpty())
        	executeCommand(commandHistoryPeek(), false);
        configureCornerButtonUI();
    }
    
    @Override
    protected void onPause() {
    	super.onStop();
    	unregisterReceiver(MusicPlayerServiceReceiver);
    	//handle.removeCallbacks(songInfoFadeIn);
        sendBroadcast(new Intent("com.ideabag.playtunes.RELEASE"));
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	IntentFilter intfil = new IntentFilter();
		intfil.addAction(Intent.ACTION_MEDIA_EJECT);
		intfil.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intfil.addAction(Intent.ACTION_MEDIA_REMOVED);
		intfil.addAction(PLAYTUNES_UI_UPDATE);
		
        registerReceiver(MusicPlayerServiceReceiver, intfil);
    	String state = Environment.getExternalStorageState();
    	if (Environment.MEDIA_REMOVED.equals(state) || Environment.MEDIA_NOFS.equals(state) || Environment.MEDIA_UNMOUNTED.equals(state) || Environment.MEDIA_SHARED.equals(state)) {
    		findViewById(R.id.ListViewSongs).setVisibility(View.GONE);
    		findViewById(R.id.Header).setVisibility(View.GONE);
	    	findViewById(R.id.SubHeader).setVisibility(View.GONE);
    	}
    	else {
    		findViewById(R.id.ListViewSongs).setVisibility(View.VISIBLE);
    		findViewById(R.id.Header).setVisibility(View.VISIBLE);
	    	findViewById(R.id.SubHeader).setVisibility(View.VISIBLE);
    	}
    	showSongInfo();
        startService(new Intent(getBaseContext(), MusicPlayerService.class));
        //sendBroadcast(new Intent(getString(R.string.action_update)));
    	sendBroadcast(new Intent(getString(R.string.action_update)));
        sendBroadcast(new Intent("com.ideabag.playtunes.CONSUME"));
        Log.i("PlayTunes", "Sent a CONSUME broadcast.");
    }
    
    private void executeCommand(int command, boolean historyFlag) {
    	if (historyFlag && !commandHistoryIsEmpty() && command == commandHistoryPeek())
    		return;
    	
    	String state = Environment.getExternalStorageState();
    	//Log.i("PlayTunes", "External storage state: "+state);
    	if (Environment.MEDIA_REMOVED.equals(state) || Environment.MEDIA_NOFS.equals(state) || Environment.MEDIA_UNMOUNTED.equals(state) || Environment.MEDIA_SHARED.equals(state))
    		return;
    	
    	if (cursor != null && !cursor.isClosed())
    		cursor.close();
    	
    	cursor = MusicUtils.getCursor(getBaseContext(), command);
    	adapter = MusicUtils.getAdapter(getBaseContext(), cursor, command);
    	ArrayList<View> list = MusicUtils.getHeaders(getBaseContext(), command);
    	if (list.size() > 0)
    		adapter.addHeaders(list);
    	((ListView)findViewById(R.id.ListViewSongs)).setAdapter(adapter);
    	
    	((TextView)findViewById(R.id.Header)).setText(MusicUtils.getCommandText(getBaseContext(), command));
    	((TextView)findViewById(R.id.SubHeader)).setText(cursor.getCount()+" "+MusicUtils.getSubtitleText(getBaseContext(), command));
    	
    	// TODO: 
    	
    	hideSongInfo();
    	resetTimer();
    	if (historyFlag)
    		commandHistoryPush(command);
    }
    
    private void play(int trackNumber) {
    	Intent playIntent = new Intent("com.ideabag.playtunes.ACTION_PLAY");
    	playIntent.putExtra("command", commandHistoryPeek());
    	if (trackNumber != -1)
    		playIntent.putExtra("track", trackNumber);
    	sendBroadcast(playIntent);
    }
    
    // This is to handle the back button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && !commandHistoryIsEmpty()) {
        	commandHistoryPop();
        	if (commandHistoryIsEmpty())
        		return super.onKeyDown(keyCode, event);
        	executeCommand(commandHistoryPeek(), false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	int id = item.getItemId();
    	switch(id) {
	    	case R.id.MenuItemAlbums:
	    		executeCommand(res.getInteger(R.integer.albums_code), true);
	    		return true;
	    	case R.id.MenuItemGenres:
	    		executeCommand(res.getInteger(R.integer.genres_code), true);
	    		return true;
	    	case R.id.MenuItemArtists:
	    		executeCommand(res.getInteger(R.integer.artists_code), true);
	    		return true;
	    	case R.id.MenuItemPlaylists:
	    		executeCommand(res.getInteger(R.integer.playlists_code), true);
	    		return true;
	    	case R.id.MenuItemSongs:
	    		executeCommand(res.getInteger(R.integer.songs_code), true);
	    		return true;
	    	case R.id.MenuItemSettings:
	    		startActivity(new Intent(getBaseContext(), SettingsActivity.class));
	    		return true;
    	}
    	return false;
    }
    
	private void configureCornerButtonUI() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		Button b;
		int current = sp.getInt(getString(R.string.preference_topleft), res.getInteger(R.integer.albums_code));
		b = (Button)findViewById(R.id.TopLeft);
		b.setText(MusicUtils.getCommandText(getBaseContext(), current));
		b.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, MusicUtils.getCommandIcon(current));
		//b.setImageResource(MusicUtils.getCommandIcon(current));
		
		current = sp.getInt(getString(R.string.preference_bottomleft), res.getInteger(R.integer.songs_code));
		b = (Button)findViewById(R.id.BottomLeft);
		b.setText(MusicUtils.getCommandText(getBaseContext(), current));
		b.setCompoundDrawablesWithIntrinsicBounds(0, MusicUtils.getCommandIcon(current), 0, 0);
		
		current = sp.getInt(getString(R.string.preference_topright), res.getInteger(R.integer.artists_code));
		b = (Button)findViewById(R.id.TopRight);
		b.setText(MusicUtils.getCommandText(getBaseContext(), current));
		b.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, MusicUtils.getCommandIcon(current));
		
		current = sp.getInt(getString(R.string.preference_bottomright), res.getInteger(R.integer.playlists_code));
		b = (Button)findViewById(R.id.BottomRight);
		b.setText(MusicUtils.getCommandText(getBaseContext(), current));
		b.setCompoundDrawablesWithIntrinsicBounds(0, MusicUtils.getCommandIcon(current), 0, 0);
	}
	
	private Runnable fadeIn = new Runnable() {
		public void run() {
			if (!scrolling)
				beginAnimation();
		}
	};
	
	private void resetTimer() {
		handle.removeCallbacks(fadeIn);
		if (isPlaying)
			handle.postDelayed(fadeIn, 1200);
	}
	
	View.OnClickListener songInfoClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			hideSongInfo();
			resetTimer();
		}
	};
	
	void beginAnimation() {
		fadeInAnimation.reset();
		findViewById(R.id.SongInformation).startAnimation(fadeInAnimation);
		findViewById(R.id.SongInformation).requestLayout();
	}
	
	void showSongInfo() {
		findViewById(R.id.SongInformation).setVisibility(View.VISIBLE);
		findViewById(R.id.SongInformation).setClickable(true);
	}
	
	void hideSongInfo() {
		findViewById(R.id.SongInformation).setVisibility(View.INVISIBLE);
		findViewById(R.id.SongInformation).setClickable(false);
	}
}
