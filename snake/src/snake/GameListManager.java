package snake;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.CapiUtil;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.Operation;

import snake.data.*;
import snake.corso.ContainerCoordinatorMapper;
import snake.corso.Util;

/**
 * GameListManager manages the list of Games in corsospace and a list of all known
 * players. A gamelist notifier thread updates the gamelist automatically if notifications
 * occur. GameListManager also keeps track of the current game, and provides methods to
 * create, join and leave a game, and set the state of the own player and the current game.
 * When game data changes, the current game is checked if it can be started (when all
 * players are ready). The GameListManager can also listen to changes, and give changes
 * on to a set DataChangeListener.
 * @author Thomas Scheller, Markus Karolus
 */
public class GameListManager implements IDataChangeListener
{
	private Capi conn = null;

	private GameList gameList = null;
	private GameListNotifier notifier = null;
	private Player myPlayer = null;
	private PlayerList playerList = null;
	private Snake snakeMain = null; //needed to report back when game can be startet

	private Game currentGame = null;
	private boolean viewOnly = false;
	private boolean viewOnlyAutostart = false;
	private LevelsManager levelManager = null;
	private boolean isLeader = false;

	private IDataChangeListener changeListener = null;

	/**
	 * Sets a change listener that gets DataChangeEvents from the GameListManager when
	 * they occur.
	 * @param changeListener IDataChangeListener
	 */
	public void setDataChangeListener(IDataChangeListener changeListener)
	{
		this.changeListener = changeListener;
	}

	/**
	 * GameListNotifier class is run in an own thread. All changes that occur to the gamelist
	 * by notifications are automatically commited to the GameListManager object.
	 */
	private class GameListNotifier implements Runnable
	{
		private boolean running = true;

		public GameListNotifier()
		{
		}

		public void run() {
			NotificationListener notifListener = new NotificationListener() {
				@Override
				public void entryOperationFinished(final Notification notification, final Operation operation, final List<? extends Serializable> entries) {
					Entry entry = (Entry) CapiUtil.getSingleEntry(entries);
					synchronized (gameList) {
						gameList = (GameList) entry.getValue();
					}
					
					//report change event to GameListManager
					dataChanged(new DataChangeEvent(gameList, DataChangeType.game));
				}
			};
			
			try {
				Util.getNotificationManager().createNotification(
						Util.getContainer(ContainerCoordinatorMapper.GAME_LIST),
						notifListener,
						Operation.WRITE);
			} catch (MzsCoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void stop()
		{
			running = false;
		}

		public boolean isRunning()
		{
			return running;
		}
	}

	/**
	 * Create a new GameListManager object.
	 * @param snakeMain Snake main class
	 * @param myPlayer the own player
	 * @param aLevelManager the levelmanager with infos about all available levels
	 */
	public GameListManager(Snake snakeMain, Player myPlayer, LevelsManager aLevelManager)
	{
		this.snakeMain = snakeMain;
		this.myPlayer = myPlayer;
		this.levelManager = aLevelManager;
	}

	/**
	 * Overwritten from IDataChangeListener interface. When a change event occurs,
	 * the current game is checked if it can be started. If the GameListManager
	 * has a ChangeListener, the change event is passed on to it.
	 * @param changeEvent DataChangeEvent
	 */
	public void dataChanged(DataChangeEvent changeEvent)
	{
		synchronized (gameList)
		{
			// System.out.println("GameListManager: DataChanged " + changeEvent.getType());
			checkCurrentGame();
			if (changeListener != null)
			{
				changeListener.dataChanged(changeEvent);
			}
		}
	}

	/**
	 * Initialize the GameListManager: Create gamelist and playerlist, add the own player
	 * to the playerlist, read the gamelist from corsospace and start the gamelist notifier
	 * thread.
	 * @param conn connection to the space
	 */
	public void initialise(Capi conn)
	{
		this.conn = conn;
			playerList = new PlayerList(this);
			gameList = new GameList(playerList);
			playerList.addPlayer(myPlayer);

			ContainerReference gameListContainer = Util.getContainer(ContainerCoordinatorMapper.GAME_LIST);
			
			try {
				ArrayList<Serializable> list = conn.take(gameListContainer);
				if (list.size() == 1) {
					gameList = (GameList)list.get(0);
				} else {
					//write game list when it could not be read
					System.out.println("Corso Read-Error occured: Game List can't be read, created new.");
					conn.write(gameListContainer, new Entry(gameList));
				}
			} catch (MzsCoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//start GameListNotifier thread
			notifier = new GameListNotifier();
			Thread notifierThread = new Thread(notifier);
			notifierThread.start();
	}

	/**
	 * Return the list of games.
	 * @return GameList
	 */
	public GameList getList()
	{
		synchronized (gameList)
		{
			return gameList;
		}
	}

	/**
	 * Return the number of games in the list.
	 * @return int
	 */
	public int getListSize()
	{
		synchronized (gameList)
		{
			return gameList.size();
		}
	}

	/**
	 * Set view parameters, used when entering a game as spectator.
	 * @param active boolean
	 * @param useAutoStart boolean
	 */
	public void setViewOnly(boolean active, boolean useAutoStart)
	{
		viewOnly = active;
		viewOnlyAutostart = useAutoStart;
	}

	/**
	 * Check if Player is only a spectator for the current game.
	 * @return true if player is spectator only
	 */
	public boolean isViewOnly()
	{
		return viewOnly;
	}

	/**
	 * Create a new game with the given name and the own player as leader, add the
	 * game to the game list an save it to corsospace.
	 * @param name name of the new game
	 */
	public void createGame(String name)
	{
		
		synchronized (gameList)
		{
			LevelData initData = new LevelData();
			initData.LoadData(levelManager);

			currentGame = gameList.addGame(name, myPlayer, initData);
			isLeader = true;
			writeGameList();
		}
	}

	private void writeGameList() {
		ContainerReference gameListContainer = Util.getContainer(ContainerCoordinatorMapper.GAME_LIST);
		try {
			this.conn.write(gameListContainer, new Entry(gameList));
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			System.out.println("joinGame: MzsError occured:");
			e.printStackTrace(System.out);
		}
	}

	/**
	 * Join the game with the given index: Add the own player to the list of players
	 * of this game, and save the gamelist to space.
	 * @param index index of the game in the list
	 */
	public void joinGame(int index)
	{
		
		synchronized (gameList)
		{
			currentGame = gameList.joinGame(index, myPlayer);
			isLeader = false;
			writeGameList();
		}
	}

	/**
	 * Join the game with the given index as spectator. The game is just set as the
	 * current game, no data is written to space. (Spectators are not known to other
	 * players.)
	 * @param index index of the game in the list
	 */
	public void joinGameViewOnly(int index)
	{
		synchronized (gameList)
		{
			currentGame = gameList.getGameViewOnly(index);
			isLeader = false;
		}
	}

	/**
	 * Leave the game the own player has currently joined. Remove the own player from
	 * the list of players of the game and write the gamelist to corsospace.
	 */
	public void leaveGame()
	{
		synchronized (gameList)
		{
			if (currentGame != null)
			{
				gameList.leaveGame(currentGame, myPlayer);
				writeGameList();
			}
		}
	}

	/**
	 * Set the state of the own player and save the player to space. If the player is the
	 * leader of the current game, the game status is set depending on the new playerstatus:
	 * open if player is not initialized, ready if player is initialized, running if player
	 * is loaded.
	 * @param newState new state of the player
	 */
	public void setMyPlayerReady(PlayerState newState)
	{
		synchronized (gameList)
		{
			if (currentGame == null)
			{
				System.out.println("Playerstate can't be set because player not in a game!");
				return;
			}

			myPlayer.setPlayerState(newState);
			myPlayer.saveToSpace();

			//gameList.setGamePlayerReady(currentGame, myPlayer, ready);
			// System.out.println("Set my playerstate: " + newState);

			if (isLeader)
			{
				//leader of the game determines game status
				if (newState == PlayerState.notinit)
				{
					//set game opened if leader not initialized
					gameList.setGameState(currentGame, GameState.opened);
				}
				else if (newState == PlayerState.init)
				{
					//set game ready if player initialized
					gameList.setGameState(currentGame, GameState.ready);
				}
				else if (newState == PlayerState.loaded)
				{
					//set game running if player loaded
					gameList.setGameState(currentGame, GameState.running);
				}
				writeGameList();
			}
		}
	}

	/**
	 * Set the state of the game and save the gamelist corsospace.
	 * @param state new GameState
	 */
	public void setGameState(GameState state)
	{
		synchronized (gameList)
		{
			gameList.setGameState(currentGame, state);
			writeGameList();
		}
	}

	/**
	 * Set the oid for the collectables for the current game, and save the game
	 * to corsospace.
	 * @param newOID CorsoVarOid
	 * /
	public void setCollectableOID(CorsoVarOid newOID)
	{
		synchronized (gameList)
		{
			gameList.getGame(currentGame).setCollectableOID(newOID);
			writeGameList();
		}
	}

	/**
	 * Set the level of the current game, and save the gamelist to corsospace.
	 * @param levelData data of the chosen level
	 */
	public void setGameLevel(LevelData levelData)
	{
		synchronized (gameList)
		{
			gameList.getGame(currentGame).setLevelData(levelData);
			writeGameList();
		}
	}

	/**
	 * Adjust settings for the current game, and save the gamelist to corsospace.
	 * @param gameType type of the game
	 * @param winValue points needed to win the game / time until the game ends
	 * @param collisionWall true if collision with walls is on
	 * @param collisionOwn true if collision with itself is on
	 * @param collisionOther true if collision with other snakes is on
	 */
	public void setGameData(GameType gameType, int winValue, boolean collisionWall, boolean collisionOwn,
			boolean collisionOther)
	{
		synchronized (gameList)
		{
			gameList.getGame(currentGame).setGameData(gameType, winValue, collisionWall, collisionOwn,
					collisionOther);
			writeGameList();
		}
	}

	/**
	 * Check if game is ready (created), that is if the game has state ready or running,
	 * and all players have at least state init, and there are at least two players in
	 * the game.
	 * This state means that all players have created their own data in corsospace and
	 * are ready to load the data of all other players from corsospace.
	 * @return true if the game is ready (created)
	 */
	public boolean isGameReadyCreated()
	{
		//Pruefen ob Spiel und alle Spieler bereit sind (es m�ssen mindestens 2 Spieler sein)
		currentGame = getCurrentGame();
		if (currentGame != null)
		{
			if (currentGame.getPlayerAnz() > 1)
			{
				if (currentGame.getState() == GameState.ready || currentGame.getState() == GameState.running)
				{
					if (currentGame.allPlayersReadyCreated())
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Check if game is ready (loaded), that is if the game has running, and all players
	 * have at least state loaded, and there are at least two players in the game.
	 * This state means that all players have loaded all needed data from corsospace and
	 * the game can be started.
	 * @return true if the game is ready (loaded)
	 */
	public boolean isGameReadyLoaded()
	{
		//Pr�fen ob Spiel und alle Spieler bereit sind (es m�ssen mindestens 2 Spieler sein)
		// currentGame = getCurrentGame();
		if (currentGame != null)
		{
			if (currentGame.getPlayerAnz() > 1)
			{
				if (currentGame.getState() == GameState.running)
				{
					if (currentGame.allPlayersReadyLoaded())
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Check if a game is joinable, that is if the game is in opened state and the maximum
	 * number of players is not reached.
	 * @param index index of the game in the list
	 * @return true if the game is joinable
	 */
	public boolean isGameJoinable(int index)
	{
		//Pr�fen ob Spiel beigetreten werden kann (nicht voll und State opened)
		synchronized (gameList)
		{
			Game game = (Game) gameList.getVector().elementAt(index);
			return (game.getPlayerAnz() < Game.MAXPLAYERS) && (game.getState() == GameState.opened);
		}
	}

	/**
	 * Check if the own player is leader of the current game.
	 * @return true if the own player is leader
	 */
	public boolean myPlayerIsLeader()
	{
		return isLeader;
	}

	/**
	 * Check if a game with the given name allready exists.
	 * @param name game name to check
	 * @return true if game name exists
	 */
	public boolean gameNameExists(String name)
	{
		return gameList.gameNameExists(name);
	}

	/**
	 * Return the current game the own player has joined, or null if the player
	 * is currently not in a game.
	 * @return the current game of the player
	 */
	public Game getCurrentGame()
	{
		synchronized (gameList)
		{
			//aktuelles Spiel auslesen
			return gameList.getGame(currentGame);
		}
	}

	/**
	 * Stop the gamelist notifier thread and remove all players from the playerlist.
	 */
	public void stopNotifier()
	{
		if (notifier != null)
		{
			notifier.stop();
			playerList.removeAllPlayers();
		}
	}

	/**
	 * Check if the current game is ready (everything loaded). Start the game if it is ready.
	 */
	public void checkCurrentGame()
	{
		currentGame = gameList.getGame(currentGame);
		if (currentGame != null)
		{
			//check if own player is leader
			isLeader = myPlayer.equals(currentGame.getLeader());

			GameState state = currentGame.getState();
			//start game if game state is active
			if (state == GameState.aktiv)
			{
				if (!isViewOnly() || viewOnlyAutostart)
				{
					//System.out.println("Spiel geladen -> starte Spiel");
					snakeMain.startMultiplayerGame();
				}
			}
			else
			{
				/*boolean allPlayersLoaded = currentGame.allPlayersReadyLoaded();
				System.out.println("Game check: state=" + state + ", allPlayersReadyLoaded=" + allPlayersLoaded);
				for (int i = 0; i < currentGame.getPlayerAnz(); i++)
				{
					System.out.println("	Player ready: " + currentGame.getPlayer(i).getPlayerState());
				}
				System.out.println("	myPlayer Leader: " + myPlayerIsLeader());
				 */

				//set game state active if game is ready (loaded) and player is leader
				if (state == GameState.running && myPlayerIsLeader())
				{
					if (isGameReadyLoaded())
					{
						synchronized (currentGame)
						{
							setGameState(GameState.aktiv);
						}
					}
				}
				//init game sprites of other players if game is ready (created)
				if (state == GameState.ready || state == GameState.running)
				{
					if (isGameReadyCreated())
					{
						snakeMain.initOtherGameSprites();
					}
				}
			}
		}
	}

	/**
	 * Get the highscore data from corsospace. Write new highscore to corsospace if
	 * it could not be read.
	 * @return HighScore object containing highscore ranking
	 */
	public HighScore getHighScore()
	{
		ContainerReference container = Util.getContainer(ContainerCoordinatorMapper.HIGH_SCORE);
		ArrayList<Serializable> list;
		HighScore highScore = null;
		try {
			list = conn.read(container);
			if (list.size() == 0) {
				highScore = new HighScore();
				System.out.println(
						"Corso Read-Error occured: HighScore can't be read, created new.");
				conn.write(container, new Entry(highScore));
			}
			highScore = (HighScore) list.get(0);
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return highScore;
	}

	/**
	 * Write the highscore object to corsospace.
	 * @param highScore HighScore
	 */
	public void writeHighScore(HighScore highScore)
	{
		ContainerReference container = Util.getContainer(ContainerCoordinatorMapper.HIGH_SCORE);
		try {
			conn.write(container, new Entry(highScore));
		} catch (MzsCoreException e) {
			System.out.println( "Corso Error occured: HighScore can't be written.");
			e.printStackTrace();
		}
	}
}
