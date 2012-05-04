package snake.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.FifoCoordinator.FifoSelector;
import org.mozartspaces.capi3.KeyCoordinator;
import org.mozartspaces.capi3.Selector;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.MzsConstants.RequestTimeout;
import org.mozartspaces.core.TransactionReference;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import snake.mzspaces.ContainerCoordinatorMapper;
import snake.mzspaces.DataChangeEvent;
import snake.mzspaces.DataChangeListener;
import snake.mzspaces.DataChangeType;
import snake.mzspaces.Util;

/**
 * List of all games saved in corsospace.
 * @author Thomas Scheller, Markus Karolus
 */
public class GameList implements Serializable, NotificationListener
{
	private static final long serialVersionUID = 1L;
	private Vector<Game> games;
	private PlayerList playerList = null;
	private DataChangeListener listener;
	private Notification notification;

	private Logger log = LoggerFactory.getLogger(GameList.class);
	
	public GameList(DataChangeListener listener, PlayerList playerList)
	{
		this.games = new Vector<Game>();
		this.listener = listener;
		this.playerList = playerList;
		
		// read current games and add them to the local vector
		ContainerReference gamesContainer = Util.getInstance().getContainer(ContainerCoordinatorMapper.GAME_LIST);
		try {
			//log.debug("trying to read games!");
			ArrayList<Serializable> spaceGames = Util.getInstance().getConnection().read(
					gamesContainer, 
					//AnyCoordinator.newSelector(),
					FifoCoordinator.newSelector(FifoSelector.COUNT_ALL),
					0,
					null);
			
			log.debug("WTF games: " + spaceGames.size());
			
			for (Serializable spaceGame : spaceGames) {
				if (spaceGame instanceof Game) {
					//log.debug("player of game "+((Game)spaceGame).getNr()+" "+((Game)spaceGame).getPlayerAnz());
					games.add((Game) spaceGame);
				}
			}
		} catch (MzsCoreException e1) {
			// this exception is ok, normally it should be:
			// The Count(1) of Selector 'AnyCoordinator' was not met. (0 Entries available)
			if (e1.getMessage().indexOf("0 Entries available") < 0) {
				// but if its not this exception, we do NOT like it.
				log.debug("ERROR reading games!\n"+e1.getMessage());
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// create a notification that updates the list whenever a game gets added
		NotificationManager notifManager = Util.getInstance().getNotificationManager();
		try {
			this.notification = notifManager.createNotification(
					Util.getInstance().getContainer(ContainerCoordinatorMapper.GAME_LIST),
					this,
					Operation.WRITE, Operation.DELETE
			);
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			log.error("ERROR: could not create notification (mzsexception)");
			e.printStackTrace();
		} catch (InterruptedException e) {
			log.error("ERROR: could not create notification (interrupted)");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (notification == null) {
				log.error("ERROR: could not create notification (null after creation)");
			} else {
				log.debug("NOTFICATION successfully created");
			}
		}

	}

	/**
	 * Check if the game name is already in use.
	 * @param name game name to check
	 * @return true if the a game with that name already exists
	 */
	public boolean gameNameExists(String name)
	{
		for (int i = 0; i < games.size(); i++)
		{
			if ( ( (Game) games.elementAt(i)).getName().equals(name))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Add a new game to the list.
	 * @param name name of the game
	 * @param leader leader and creator of the game
	 * @param initData LevelData
	 * @return Game
	 */
	public Game addGame(String name, Player leader,LevelData initData)
	{
		// create the game, initialize it with nr 0, because our IndexAspect creates teh number
		Game game = new Game(0, name, leader, playerList);
		game.setLevelData(initData);
		games.addElement(game);
		
		// however we have to write it to the space. but first we have to delete this very game:
		writeGameToSpace(game);
		
		return game;
	}

	private void writeGameToSpace(Game game) {
		TransactionReference tx = Util.getInstance().createTransaction();
		ContainerReference gamesContainer = Util.getInstance().getContainer(ContainerCoordinatorMapper.GAME_LIST);
		try {
			ArrayList<Selector> selectors = new ArrayList<Selector>();
			selectors.add(KeyCoordinator.newSelector(
					String.valueOf(game.getNr()),
					MzsConstants.Selecting.COUNT_ALL)
			);

			// take it to force no ui update
			Util.getInstance().getConnection().take(
					gamesContainer,
					selectors,
					MzsConstants.RequestTimeout.ZERO, 
					tx);

			Util.getInstance().getConnection().write(
					gamesContainer,
					MzsConstants.RequestTimeout.ZERO,
					tx,
					new Entry(game, KeyCoordinator.newCoordinationData(String.valueOf(game.getNr())))
			);

			Util.getInstance().getConnection().commitTransaction(tx);
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void deleteGameFromSpace(Game game) {
		TransactionReference tx = Util.getInstance().createTransaction();
		ContainerReference gamesContainer = Util.getInstance().getContainer(ContainerCoordinatorMapper.GAME_LIST);
		
		try {
			ArrayList<Selector> selectors = new ArrayList<Selector>();
			selectors.add(KeyCoordinator.newSelector(
					String.valueOf(game.getNr()),
					MzsConstants.Selecting.COUNT_ALL)
			);
	
			Util.getInstance().delete(gamesContainer, selectors, tx);
			Util.getInstance().getConnection().commitTransaction(tx);
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Remove the game from the list.
	 * @param game Game
	 */
	public void removeGame(Game game)
	{
		for (int i = 0; i < games.size(); i++)
		{
			if ( ( (Game) games.elementAt(i)).getNr().equals(game.getNr()))
			{
				games.removeElementAt(i);
				deleteGameFromSpace(game);
			}
		}
	}

	/**
	 * Ad the player to the game with the given index.
	 * @param index index of the game in list
	 * @param player player that wants to join
	 * @return the game that has been joined
	 */
	public Game joinGame(int index, Player player)
	{
		if (games.size() > index)
		{
			Game game = (Game)games.elementAt(index);
			//log.debug("currently has: "+game.getPlayerAnz());
			game.joinGame(player);
			//log.debug("after join has: "+game.getPlayerAnz());

			// however we have to write it to the space
			// log.debug("\n\nwriting game to space again\n\n");
			writeGameToSpace(game);

			return game;
		}
		return null;
	}

	/**
	 * Get the game with the given index. (Method only used with view mode)
	 * @param index index of the game
	 * @return Game
	 */
	public Game getGameViewOnly(int index)
	{
		if (games.size() > index)
		{
			return (Game)games.elementAt(index);
		}
		return null;
	}

	/**
	 * Remove the player from the game. Delete the game if it has no more players.
	 * @param game Game
	 * @param player the player that should be removed
	 */
	public void leaveGame(Game game, Player player)
	{
		int index = games.indexOf(game);
		if (index > -1)
		{
			game = ((Game)games.elementAt(index));
			game.leaveGame(player);

			if (game.getPlayerAnz() == 0)
			{
				//delete game if no more players
				removeGame(game);
			}
			else {
				if (game.getLeader().equals(player))	{
					//determine new leader, if leader has left the game
					game.updateLeader();
				}
				writeGameToSpace(game);
			}
		}
	}

	/**
	 * set the game data
	 * @param game
	 * @param gameType
	 * @param winValue
	 * @param collisionWall
	 * @param collisionOwn
	 * @param collisionOther
	 */
	public void setGameData(Game game, GameType gameType, int winValue,
			boolean collisionWall, boolean collisionOwn, boolean collisionOther) {
		
		// set gamedata on game object
		game.setGameData(gameType, winValue, collisionWall, collisionOwn,
				collisionOther);
		
		// write game to space
		writeGameToSpace(game);
	}
	
	public void setGameLevel(Game game, LevelData levelData) {
		game.setLevelData(levelData);
		
		writeGameToSpace(game);
	}
	
	/**
	 * Change the state of the game.
	 * @param game Game
	 * @param state GameState
	 */
	public void setGameState(Game game, GameState state)
	{
			int index = games.indexOf(game);
			if (index > -1)
			{
				((Game)games.elementAt(index)).setState(state);
			}
	}

	/**
	 * Get the state of the game.
	 * @param index index of the game
	 * @return GameState
	 */
	public GameState getGameState(int index)
	{
			//Spielstatus auslesen
			if (games.size() > index)
			{
				return ((Game)games.elementAt(index)).getState();
			}
			return GameState.unknown;
	}

	
	/**
	 * Get the game (with equal number), if it is in the game list.
	 * @param game game that is searched in the game list
	 * @return original game from the game list, null if the game is not found
	 */
	public Game getGame(Game game)
	{
		//Spiel auslesen
		int index = games.indexOf(game);
		if (index > -1)
			return (Game)games.elementAt(index);
		else
			return null;
	}

	public int size()
	{
		return games.size();
	}

	public Vector<Game> getVector()
	{
		return games;
	}

	@Override
	public void entryOperationFinished(Notification notification, Operation operation,
			List<? extends Serializable> entries) {

		log.debug("\n\nNOTIFICATION: received a "+operation+" notification with "+entries.size()+" entries\ncurrent gamesize:"+games.size()+"\n\n");

		boolean changed = false;

		switch (operation) {
		case WRITE:
			// if a game is written to the container add it to the local vector
			if (entries != null)
			for (Serializable entry : entries) {
				// log.debug("entry has type "+entry.getClass());
				Serializable obj = ((Entry) entry).getValue();
				// log.debug("value of entry has type "+obj.getClass());
				if (obj instanceof Game) {
					Game game = (Game) obj;
					boolean found = false;
					// if we have this game already replace it.
					// log.debug("trying to find game with nr "+game.getNr());
					for (int i = 0; i < games.size(); i++) {
						//log.debug("comparing "+games.get(i).getNr()+" against "+game.getNr());
						if (games.get(i).getNr().equals(game.getNr())) {
							found = true;
							games.set(i, game);
							//log.debug("\n\nwe have "+game.getPlayerAnz()+" player\n\n");
							changed = true;
							break;
						}
					}
					// if not, we have to add it
					if (!found) {
						// log.debug("game not found, adding it: "+games.size());
						games.add(game);
						//log.debug("\n\nADD we have "+game.getPlayerAnz()+" player\n\n");
						// log.debug("game list size after add: "+games.size());
						changed = true;
					}
				}
			}
			break;

		/* we do not listen to take any more 
		case TAKE:
			// nothing on take. we only take to update.
			log.debug("\n\n\n GAME TAKEN -> do nothing");
			break;
		*/
		case DELETE:
			log.debug("\n\n\n GAME DELETED -> delete it from local game list");
			// on the other hand, if the game gets removed, we have to remove it too
			if (entries != null)
			for (Serializable entry : entries) {
				Game game = null;
				if (entry instanceof Game) {
					game = (Game)entry;
				}
				if (entry instanceof Entry) { 
					Serializable obj = ((Entry) entry).getValue();
					if (obj instanceof Game) {
						game = (Game) obj;
					}
				}
				if (game != null) {
					games.remove(game);
					changed = true;
				}
			}
			break;
		}

		// trigger data changed event
		if (changed && listener != null) {
			//log.debug("informing listener about changed data: "+games.size());
			listener.dataChanged(new DataChangeEvent(this, DataChangeType.game));
		}
	}

	public void setDataChangeListener(DataChangeListener listener2) {
		listener = listener2;
	}

}
