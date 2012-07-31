/**
 * 
 */
package client.event;

/**
 * @author Jakob Lahmer, Matthias Steinbšck
 *
 */
public enum MenuEventType {
	
	// menu navigation
		START_MENU,
		MULTIPLAYER_NEW,
	// multiplayer
		MULTIPLAYER_MENU,
		MULTIPLAYER_HIGHSCORE,
		MULTIPLAYER_JOIN,
		MULTIPLAYER_LEAVE,
		MULTIPLAYER_WATCH,
		MULTIPLAYER_START,
	// singleplayer
		SINGLEPLAYER_MENU, 
		SINGLEPLAYER_START, 
	// utils
		SETTINGS, 
		EXIT
	
}
