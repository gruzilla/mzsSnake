package snake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import snake.data.*;
import snake.mzspaces.ContainerCoordinatorMapper;
import snake.mzspaces.DataChangeEvent;
import snake.mzspaces.DataChangeListener;
import snake.mzspaces.Util;

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
public class GameListManager implements DataChangeListener
{
	private GameList gameList = null;
	private PlayerList playerList = null;
	private Snake snakeMain = null; //needed to report back when game can be startet

	private Game currentGame = null;
	private boolean viewOnly = false;
	private boolean viewOnlyAutostart = false;
	private LevelsManager levelManager = null;
	private DataChangeListener listener;

	private Logger log = LoggerFactory.getLogger(GameListManager.class);

	/**
	 * Create a new GameListManager object.
	 * @param snakeMain Snake main class
	 * @param myPlayer the own player
	 * @param aLevelManager the levelmanager with infos about all available levels
	 */
	public GameListManager(Snake snakeMain, LevelsManager aLevelManager)
	{
		this.snakeMain = snakeMain;
		this.levelManager = aLevelManager;
	}


	/**
	 * Initialize the GameListManager: Create gamelist and playerlist, add the own player
	 * to the playerlist, read the gamelist from corsospace and start the gamelist notifier
	 * thread.
	 * @param conn connection to the space
	 */
	public void initialize()
	{
		playerList = new PlayerList();
		gameList = new GameList(this, playerList);
		playerList.addPlayer(snakeMain.getMyPlayer());
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

			currentGame = gameList.addGame(name, snakeMain.getMyPlayer(), initData);
			snakeMain.getMyPlayer().setCurrentGame(currentGame);
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
			currentGame = gameList.joinGame(index, snakeMain.getMyPlayer());
			snakeMain.getMyPlayer().setCurrentGame(currentGame);
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
			snakeMain.getMyPlayer().setCurrentGame(currentGame);
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
				gameList.leaveGame(currentGame, snakeMain.getMyPlayer());
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

			log.debug("\n\ncurrent state: "+snakeMain.getMyPlayer().getPlayerState());
			snakeMain.getMyPlayer().setPlayerState(newState);
			log.debug("\n\nnew state: "+snakeMain.getMyPlayer().getPlayerState());

			if (myPlayerIsLeader())
			{
				//leader of the game determines game status
				if (newState == PlayerState.notinit)
				{
					//set game opened if leader not initialized
					gameList.setGameState(currentGame, GameState.opened, false);
				}
				else if (newState == PlayerState.init)
				{
					//set game ready if player initialized
					gameList.setGameState(currentGame, GameState.ready, false);
				}
				else if (newState == PlayerState.loaded)
				{
					//set game running if player loaded
					gameList.setGameState(currentGame, GameState.running, false);
				}
			}

			for (int i = 0; i < currentGame.getPlayerAnz(); i++) {
				log.debug("\n\n P"+i+" state: "+currentGame.getPlayer(i).getPlayerState());
			}

			// updated player must be sent
			Util.getInstance().update(
					ContainerCoordinatorMapper.GAME_LIST,
					currentGame,
					String.valueOf(currentGame.getNr())
			);
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
			gameList.setGameLevel(gameList.getGame(currentGame.getNr()), levelData);
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
			gameList.setGameData(gameList.getGame(currentGame.getNr()), gameType, winValue, collisionWall, collisionOwn,
					collisionOther);
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
		// Pruefen ob Spiel und alle Spieler bereit sind (es muessen mindestens 2 Spieler sein)
		// currentGame = getCurrentGame();
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
		// Pruefen ob Spiel und alle Spieler bereit sind (es muessen mindestens 2 Spieler sein)
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
		//Pruefen ob Spiel beigetreten werden kann (nicht voll und State opened)
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
		return currentGame.getLeader().equals(snakeMain.getMyPlayer());
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
			return gameList.getGame(currentGame.getNr());
		}
	}

	/**
	 * Check if the current game is ready (everything loaded). Start the game if it is ready.
	 */
	public void checkCurrentGame()
	{
		currentGame = gameList.getGame(currentGame.getNr());
		if (currentGame != null)
		{
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
		return highScore;
	}

	public void writeHighScore(HighScore highScore) {
		// TODO Auto-generated method stub
	}

	public void setDataChangeListener(DataChangeListener listener) {
		this.listener = listener;
	}

	@Override
	public void dataChanged(DataChangeEvent changeEvent) {
		// TODO Auto-generated method stub
		synchronized (gameList) {
			log.debug("\n\noverwriting current game\n");
			currentGame = gameList.getGame(currentGame.getNr());

			for (int i = 0; i < currentGame.getPlayerAnz(); i++) {
				Player player = currentGame.getPlayer(i);
				if (player.getNr().equals(snakeMain.getMyPlayer().getNr())) {
					snakeMain.setMyPlayer(player);
					break;
				}
			}

			checkCurrentGame();

			if (listener != null) {
				listener.dataChanged(changeEvent);
			}
		}
	}
}
