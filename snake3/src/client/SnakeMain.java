package client;

import com.esotericsoftware.minlog.Log;

import client.data.event.MenuEventData;
import client.data.event.i.MenuEventListener;
import client.gui.GameFrame;
import client.gui.menu.MenuFrame;

/**
 * @author Jakob Lahmer, Matthias Steinbšck
 *
 */
public class SnakeMain implements MenuEventListener {

	/**
	 * Main Function, starts a client
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new SnakeMain();
	}

	
	// game frame
	private GameFrame gameFrame;
	
	// menu panel
	private MenuFrame menuFrame;
	
	
	
	/**
	 * represents a client instance of a snake game
	 */
	public SnakeMain() {
		// init game frame
		
		menuFrame = new MenuFrame(this);
		menuFrame.setVisible(true);
		
//		gameFrame = new GameFrame();
//		gameFrame.getContentPane().add(menuFrame);
		
//		gameFrame.startGame(false, false);
	}



	/* (non-Javadoc)
	 * @see client.data.event.i.MenuEventListener#menuChanged(client.data.event.MenuEventData)
	 */
	@Override
	public void menuChanged(MenuEventData eventData) {
		Log.info("Menu clicked " + eventData.getMenuItem());
		
		switch (eventData.getMenuItem())	{
			/** MENU NAVIGATION **/
				case START_MENU:
					this.menuFrame.showStartMenu();
					break;
					
				case MULTIPLAYER_NEW:
					this.menuFrame.showMultiplayerNewGameMenu();
					break;
		
			/** MULTIPLAYER **/
				case MULTIPLAYER_MENU:
					menuFrame.showMultiplayerMenu();
					break;
				case MULTIPLAYER_START:
					this.startMultiplayer();
					break;
					
			/** SINGLEPLAYER **/
				case SINGLEPLAYER_MENU:
				break;
				case SINGLEPLAYER_START:
					this.startMultiplayer();
					break;

			/** UTIL **/
				case SETTINGS:
					this.menuFrame.showSettingsMenu();
					break;
				case EXIT:
					this.exitGame();
					break;
				
			default:
				break;
		}
	}



	/**
	 * starts a Multiplayer Game
	 */
	private void startMultiplayer() {
		// TODO Auto-generated method stub
		
	}



	/**
	 * exits the game properly, releases resources etc
	 */
	private void exitGame() {
		System.exit(0);
	}

}










