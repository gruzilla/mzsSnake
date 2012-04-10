package snake.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.MzsConstants.RequestTimeout;
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
	private Vector<Game> games = new Vector<Game>();
	private PlayerList playerList = null;
	private DataChangeListener listener;
	private Notification notification;

	private Logger log = LoggerFactory.getLogger(GameList.class);
	
	public GameList(DataChangeListener listener, PlayerList playerList)
	{
		this.listener = listener;
		this.playerList = playerList;
		
		// read current games and add them to the local vector
		ContainerReference gamesContainer = Util.getInstance().getContainer(ContainerCoordinatorMapper.GAME_LIST);
		try {
			log.debug("trying to read games!");
			ArrayList<Serializable> spaceGames = Util.getInstance().getConnection().read(
					gamesContainer, 
					//AnyCoordinator.newSelector(),
					FifoCoordinator.newSelector(),
					RequestTimeout.TRY_ONCE, 
					null);
			
			log.debug("games: " + spaceGames.size());
			
			for (Serializable spaceGame : spaceGames) {
				if (spaceGame instanceof Game) {
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
					Operation.WRITE, Operation.DELETE, Operation.TAKE
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
		
		// however we have to write it to the space
		ContainerReference gamesContainer = Util.getInstance().getContainer(ContainerCoordinatorMapper.GAME_LIST);
		try {
			Util.getInstance().getConnection().write(gamesContainer, new Entry(game));
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return game;
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
			((Game)games.elementAt(index)).joinGame(player);
			return (Game)games.elementAt(index);
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
			else if (game.getLeader().equals(player))
			{
				//determine new leader, if leader has left the game
				game.updateLeader();
			}
		}
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

		// log.debug("NOTIFICATION: received a "+operation+" notification with "+entries.size()+" entries");

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
						if (games.get(i).getNr().equals(game.getNr())) {
							found = true;
							games.set(i, game);
							break;
						}
					}
					// if not, we have to add it
					if (!found) {
						// log.debug("game not found, adding it: "+games.size());
						games.add(game);
						// log.debug("game list size after add: "+games.size());
						changed = true;
					}
				}
			}
			break;

		case DELETE:
		case TAKE:
			// on the other hand, if the game gets removed, we have to remove it too
			if (entries != null)
			for (Serializable entry : entries) {
				Serializable obj = ((Entry) entry).getValue();
				if (obj instanceof Game) {
					Game game = (Game) obj;
					games.remove(game);
					changed = true;
				}
			}
			break;
		}

		if (changed && listener != null) {
			//log.debug("informing listener about changed data: "+games.size());
			listener.dataChanged(new DataChangeEvent(this, DataChangeType.game));
		}
	}

	public void setDataChangeListener(DataChangeListener listener2) {
		listener = listener2;
	}
}
