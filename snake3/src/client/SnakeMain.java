package client;

import javax.swing.JFrame;

import mzs.event.DataChangeEventData;
import mzs.event.i.DataChangeEventListener;
import mzs.util.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Messages;

import com.esotericsoftware.minlog.Log;

import client.data.game.Game;
import client.data.game.GameList;
import client.data.player.Player;
import client.event.MenuEventData;
import client.event.MenuEventMPNewData;
import client.event.i.GameStateEventListener;
import client.event.i.MenuEventListener;
import client.gui.GameFrame;
import client.gui.graphics.BorderContentPanel;
import client.gui.menu.MenuFrame;

/**
 * @author Jakob Lahmer, Matthias Steinbšck
 *
 */
public class SnakeMain extends JFrame implements MenuEventListener, GameStateEventListener {

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

	// mp gamelist
	private GameList gameList;
	// my player
	private Player player;
	
	
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
		
		// create Player
		this.player = new Player(Util.getInstance().getSettings().getPlayerName(), Util.getInstance().getSettings().getSnakeSkin());
		
		// init game list
		this.gameList = new GameList(this);
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
					if(this.initMultiplayer())	{
						menuFrame.showMultiplayerMenu();
					}
					break;
				case MULTIPLAYER_NEW:
					if(this.createNewMultiplayerGame(eventData)) {
						this.menuFrame.showMultiplayerNewGameMenu();
					}
					break;
				case MULTIPLAYER_JOIN:
					if(this.joinMultiplayerGame(eventData))	{
						this.menuFrame.showMultiplayerNewGameMenu();
					}
					break;
				case MULTIPLAYER_LEAVE:
					this.leaveMultiplayerGame();
					this.menuFrame.showMultiplayerMenu();
					
					break;
				case MULTIPLAYER_START:
					// set player state to started
					if(this.setPlayerReady())	{
						// if all players started, start the game
//						this.startMultiplayer();
					}
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


	/** MP **/
	/**
	 * initialises the multiplayer game (creates the gamelist and loads games from space)
	 */
	private boolean initMultiplayer()	{
		this.gameList.setDataChangeEventListener(this.menuFrame.getMPMenuPanel());
		if(!this.gameList.initGameList()) {
			Messages.errorMessage(this, "Can't connect to XVSM Server.");
			return false;
		}
		return true;
	}
	/**
	 * creates a new multiplayer game
	 * @param menuData
	 */
	private boolean createNewMultiplayerGame(MenuEventData eventData) {
		// check instance
		if(eventData instanceof MenuEventMPNewData)	{
			eventData = (MenuEventMPNewData) eventData;
			gameList.setDataChangeEventListener(this.menuFrame.getMPMenuNewGamePanel());
//			gameListManager.setViewOnly(false, false);
//			String gameName = tfNeu.getText();
			//check if name exists
			if (gameList.gameNameExists(((MenuEventMPNewData) eventData).getMpName()))	{
				Messages.errorMessage(this,"A game with this name already exists,\nplease choose another name.");
				gameList.setDataChangeEventListener(this.menuFrame.getMPMenuPanel());
				return false;
			}

			//set player state back to not init when necessary
//			if (snakeMain.getMyPlayer().getPlayerState() != PlayerState.notinit)	{
//				snakeMain.getMyPlayer().setPlayerState(PlayerState.notinit);
//				//myPlayer.saveToSpace();
//			}

			gameList.createGame(((MenuEventMPNewData) eventData).getMpName(), this.player);
			// change data change listener
			return true;
		}
		Messages.errorMessage(this, "No valid Event Object given");
		return false;
	}
	
	/**
	 * 
	 * join an existing multiplayer game
	 * @param eventData
	 * @return
	 */
	private boolean joinMultiplayerGame(MenuEventData eventData) {
		if(eventData instanceof MenuEventMPNewData)	{
			if (!gameList.joinGame(((MenuEventMPNewData) eventData).getMpName(), this.player, this.menuFrame.getMPMenuNewGamePanel()))	{
				Messages.infoMessage(this,"Can't join game, because it's full or already started.");
				return false;
			}
			//set player state back to not init when necessary
//			if (this.player.getPlayerState() != PlayerState.notinit)	{
//				snakeMain.getMyPlayer().setPlayerState(PlayerState.notinit);
//				//myPlayer.saveToSpace();
//			}
			
			return true;
		}
		Messages.errorMessage(this, "No valid Event Object given");
		return false;
	}
	
	private void leaveMultiplayerGame()	{
		this.gameList.leaveCurrentGame(this.player, this.menuFrame.getMPMenuPanel());
	}
	
	/**
	 * sets the player to ready (pressed start button)
	 * @return
	 */
	private boolean setPlayerReady()	{
		if(this.gameList.startGame(this.player))
			return true;
		return false;
	}
	
/** END MP **/


/** GAME START **/
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
	 * @param game 
	 */
	private void startMultiplayer(Game game) {
		this.initGame();
		gameFrame.startGame(false, true, game);
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
/** END GAME START **/


/** GAME EXIT **/
	/**
	 * exits the game properly, releases resources etc
	 */
	private void exitGame() {
		System.exit(0);
	}



	/* (non-Javadoc)
	 * @see client.event.i.GameStateEventListener#gameStateChanged(client.data.game.Game)
	 */
	@Override
	public void gameStateChanged(Game game) {
		log.info("I AM STARTED");
		switch(game.getState())	{
		case ACTIVE:
			break;
		case READY:
			this.startMultiplayer(game);
			break;
		case OPENEND:
			break;
		case RUNNING:
			break;
		case ENDED:
		case UNKNOWN:
		default:
			break;
		}
	}

}


