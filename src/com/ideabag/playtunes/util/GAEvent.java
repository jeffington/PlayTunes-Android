package com.ideabag.playtunes.util;

public interface GAEvent {
	
	public static interface Categories {
		
		// Any music browsing ListFragment in PlayTunes
		String PLAYLIST = "playlist";
		
		// editable playlists
		String USER_PLAYLIST = "user playlist";
		
		// The starred playlist
		String STARRED_PLAYLIST = "starred playlist";
		
		// Lock screen controls
		String LOCKSCREEN = "lockscreen";
		
		// 
		String FOOTER_CONTROLS = "footer_controls";
		
		// Notification
		String NOTIFICATION = "notification button";
		
		// Dialogs
		String DIALOG = "dialog";
		
	}
	
	//
	// Defined constants for event actions
	//
	
	public interface Playlist {
		
		String ACTION_CLICK = "click";
		String ACTION_LONGCLICK = "longclick";
		String ACTION_SHOWLIST = "show";
		
		
		
	}
	
	public interface Notification {
		
		String ACTION_CLOSE = "close";
		
	}
	
	public interface AudioControls {
		
		String ACTION_PLAY = "play";
		String ACTION_PAUSE = "pause";
		String ACTION_NEXT = "next";
		String ACTION_PREV = "prev"; // Not implemented
		String ACTION_STOP = "stop"; // Not implemented
		String ACTION_REW = "rew"; // Ni
		String ACTION_FFWD = "ffwd"; // Ni
		
	}
	
	public interface UserPlaylist {
		
		String ACTION_CREATE = "create";
		String ACTION_DELETE = "delete";
		String ACTION_RENAME = "rename";
		String ACTION_ADD = "add";
		String ACTION_REMOVE = "remove";
		String ACTION_MOVE = "move";
		
		String ACTION_ADDSTAR = "star_add";
		String ACTION_REMOVESTAR = "star_remove";
		String ACTION_MOVESTAR = "star_move";
		
		
	}
	
	public interface FooterControls {
		
		String ACTION_NOW_PLAYING = "now_playing";
		
	}
	
}
