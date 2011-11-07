package snake;

import java.util.Vector;

import org.mozartspaces.core.Capi;

import snake.data.*;
import snake.corso.Util;

/**
 * GameListManager manages the list of Games in corsospace and a list of all known
 * players. A gamelist notifier thread updates the gamelist automatically if notifications
 * occur. GameListManager also keeps track of the current game, and provides methods to
 * create, join and leave a game, and set the state of the own player and the current game.
 * Wenn game data changes, the current game is checked if it can be started (when all
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

		public void run()
		{
			try
			{
				// create notification item on this object ****************************
				CorsoNotificationItem notifItem =
						new CorsoNotificationItem(gameListOid,
									0,
									CorsoNotificationItem.CURRENT_TIMESTAMP);

				// add the notification item to a vector ******************************
				Vector notifVec = new Vector();
				notifVec.addElement(notifItem);

				// create notification ************************************************
				CorsoNotification notification =
						conn.createNotification(notifVec, strat);

				CorsoData data = conn.createData();
				// start notification and wait until oid is written *******************
				while (running)
				{

					//System.out.println("waiting until oid is written");
					CorsoNotificationItem fired =
							notification.start(CorsoConnection.INFINITE_TIMEOUT, data);

					// reading out the value of the written oid *************************
					if (fired != null && running) //only read when still running
					{
						if (fired.varOid() != null && fired.varOid().equals(gameListOid))
						{ //right object found
							//try to read multiple times when reading fails
							int readOK = -5;
							while (readOK < 0)
							{
								try
								{
									synchronized (gameList)
									{
										fired.varOid().readShareable(gameList, null, CorsoConnection.NO_TIMEOUT);
									}

									//report change event to GameListManager
									dataChanged(new DataChangeEvent(gameList, DataChangeType.game));

									if (readOK == -5)
									{
										readOK = 2;
									}
									else
									{
										readOK = 1;
									}
								}
								catch (CorsoException ex)
								{
									System.out.println("GameListNotifier: Corso Read-Error occured (" + readOK + "):");
									ex.printStackTrace(System.out);
									readOK++;
								}
							}
							if (readOK == 1)
							{
								System.out.println("GameListNotifier: Read successful (" + readOK + ")");
							}
						}
					}
				}
			}
			catch (CorsoException ex)
			{
				System.out.println("GameListNotifier: Corso Error occured:");
				ex.printStackTrace(System.out);
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
	 * @param conn connection to the corsospace
	 * @param newGameListOID oid of the gamelist
	 * @param newHighScoreOID oid of the highscore
	 */
	public void initialise(Capi conn)
	{
		this.conn = conn;
		try
		{
			playerList = new PlayerList(this);
			gameList = new GameList(playerList);
			playerList.addPlayer(myPlayer);
			strat = new CorsoStrategy(Util.STRATEGY);

			gameListOid = newGameListOID;
			highScoreOID = newHighScoreOID;

			try
			{
				//read game list
				gameListOid.readShareable(gameList, null, CorsoConnection.NO_TIMEOUT);
			}
			catch (CorsoException ex)
			{
				System.out.println("Corso Read-Error occured: Game List can't be read, created new.");
				//ex.printStackTrace(System.out);

				//write game list when it could not be read
				gameListOid.writeShareable(gameList, CorsoConnection.INFINITE_TIMEOUT);
			}

			//start GameListNotifier thread
			notifier = new GameListNotifier();
			Thread notifierThread = new Thread(notifier);
			notifierThread.start();
		}
		catch (CorsoException ex)
		{
			System.out.println("Corso Error occured:");
			ex.printStackTrace(System.out);
		}
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
			try
			{
				LevelData initData = new LevelData();
				initData.LoadData(levelManager);

				currentGame = gameList.addGame(name, myPlayer, initData);
				isLeader = true;
				gameListOid.writeShareable(gameList, CorsoConnection.INFINITE_TIMEOUT);
			}
			catch (CorsoException ex)
			{
				System.out.println("createGame: Corso Error occured:");
				ex.printStackTrace(System.out);
			}
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
			try
			{
				currentGame = gameList.joinGame(index, myPlayer);
				isLeader = false;
				gameListOid.writeShareable(gameList, CorsoConnection.INFINITE_TIMEOUT);
			}
			catch (CorsoException ex)
			{
				System.out.println("joinGame: Corso Error occured:");
				ex.printStackTrace(System.out);
			}
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
				try
				{
					//Spiel verlassen
					gameList.leaveGame(currentGame, myPlayer);
					gameListOid.writeShareable(gameList, CorsoConnection.INFINITE_TIMEOUT);
					currentGame = null;
				}
				catch (CorsoException ex)
				{
					System.out.println("leaveGame: Corso Error occured:");
					ex.printStackTrace(System.out);
				}
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
			try
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
						gameListOid.writeShareable(gameList, CorsoConnection.INFINITE_TIMEOUT);
					}
					else if (newState == PlayerState.init)
					{
						//set game ready if player initialized
						gameList.setGameState(currentGame, GameState.ready);
					 // System.out.println("set gameState ready");
						gameListOid.writeShareable(gameList, CorsoConnection.INFINITE_TIMEOUT);
					}
					else if (newState == PlayerState.loaded)
					{
						//set game running if player loaded
						gameList.setGameState(currentGame, GameState.running);
						//System.out.println("set gameState running");
						gameListOid.writeShareable(gameList, CorsoConnection.INFINITE_TIMEOUT);
					}
				}
			}
			catch (CorsoException ex)
			{
				System.out.println("setMyPlayerReady: Corso Error occured:");
				ex.printStackTrace(System.out);
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
			try
			{
				gameList.setGameState(currentGame, state);
				gameListOid.writeShareable(gameList, CorsoConnection.INFINITE_TIMEOUT);
			}
			catch (CorsoException ex)
			{
				System.out.println("setGameState: Corso Error occured:");
				ex.printStackTrace(System.out);
			}
		}
	}

	/**
	 * Set the oid for the collectables for the current game, and save the game
	 * to corsospace.
	 * @param newOID CorsoVarOid
	 */
	public void setCollectableOID(CorsoVarOid newOID)
	{
		synchronized (gameList)
		{
			try
			{
				gameList.getGame(currentGame).setCollectableOID(newOID);
				gameListOid.writeShareable(gameList, CorsoConnection.INFINITE_TIMEOUT);
			}
			catch (CorsoException ex)
			{
				System.out.println("setCollectableOID: Corso Error occured:");
				ex.printStackTrace(System.out);
			}
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
			try
			{
				gameList.getGame(currentGame).setLevelData(levelData);
				gameListOid.writeShareable(gameList, CorsoConnection.INFINITE_TIMEOUT);
			}
			catch (CorsoException ex)
			{
				System.out.println("setGameLevel: Corso Error occured:");
				ex.printStackTrace(System.out);
			}
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
			try
			{
				gameList.getGame(currentGame).setGameData(gameType, winValue, collisionWall, collisionOwn,
																									collisionOther);
				gameListOid.writeShareable(gameList, CorsoConnection.INFINITE_TIMEOUT);
			}
			catch (CorsoException ex)
			{
				System.out.println("setGameData: Corso Error occured:");
				ex.printStackTrace(System.out);
			}
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
		//Pr�fen ob Spiel und alle Spieler bereit sind (es m�ssen mindestens 2 Spieler sein)
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
		HighScore highScore = new HighScore();

		try
		{
			//read highscore
			CorsoVarOid tempOID = new CorsoVarOid(highScoreOID);
			tempOID.readShareable(highScore, null, CorsoConnection.NO_TIMEOUT);
		}
		catch (CorsoException ex)
		{
			System.out.println(
					"Corso Read-Error occured: HighScore can't be read, created new.");
			//ex.printStackTrace(System.out);

			highScore.init();
			//write highscore if reading not possible
			try
			{
				highScoreOID.writeShareable(highScore, CorsoConnection.INFINITE_TIMEOUT);
			}
			catch (CorsoException err)
			{
				System.out.println( "Corso Error occured: HighScore can't be written.");
			}
		}
		return highScore;
	}

	/**
	 * Write the highscore object to corsospace.
	 * @param highScore HighScore
	 */
	public void writeHighScore(HighScore highScore)
	{
		try
		{
			highScoreOID.writeShareable(highScore, CorsoConnection.INFINITE_TIMEOUT);
		}
		catch (CorsoException ex)
		{
			System.out.println( "Corso Error occured: HighScore can't be written.");
			//ex.printStackTrace(System.out);
		}
	}
}
