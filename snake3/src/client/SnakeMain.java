package client;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.minlog.Log;

import client.data.event.MenuEventData;
import client.data.event.i.MenuEventListener;
import client.gui.GameFrame;
import client.gui.graphics.BorderContentPanel;
import client.gui.menu.MenuFrame;

/**
 * @author Jakob Lahmer, Matthias Steinbšck
 *
 */
public class SnakeMain extends JFrame implements MenuEventListener {

	private static final long serialVersionUID = 1L;

	
	private static Logger log = LoggerFactory.getLogger(SnakeMain.class);


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
		
		// set the main layout with borders
		this.setContentPane(new BorderContentPanel());
		
		// init menu frame
		menuFrame = new MenuFrame(this);
		menuFrame.setVisible(true);
		this.getContentPane().add(menuFrame);
		this.pack();
		this.setVisible(true);
		this.setResizable(false);
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
					
			/** MULTIPLAYER **/
				case MULTIPLAYER_MENU:
					menuFrame.showMultiplayerMenu();
					break;
				case MULTIPLAYER_NEW:
					this.menuFrame.showMultiplayerNewGameMenu();
					break;
				case MULTIPLAYER_START:
					this.startMultiplayer();
					break;
					
			/** SINGLEPLAYER **/
				// in case there is an singleplayer menu (not existing atm)
				case SINGLEPLAYER_MENU:
				case SINGLEPLAYER_START:
					this.startSinglePlayer();
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
		this.pack();
	}



	/**
	 * starts a Singleplayer game
	 */
	private void startSinglePlayer() {
		// init game
		this.initGame();
		gameFrame.startGame(false, false);
	}

	
	/**
	 * starts a Multiplayer Game
	 */
	private void startMultiplayer() {
		this.initGame();
		gameFrame.startGame(false, true);
	}


	/**
	 * inits a new game instance
	 */
	private void initGame() {
		gameFrame = new GameFrame();
		gameFrame.setVisible(true);
		// add game
		this.getContentPane().removeAll();
		this.getContentPane().add(gameFrame);
		this.pack();
		this.setVisible(true);
	}



	/**
	 * exits the game properly, releases resources etc
	 */
	private void exitGame() {
		System.exit(0);
	}

}


