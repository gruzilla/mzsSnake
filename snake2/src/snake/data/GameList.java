package snake.data;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import snake.mzspaces.ContainerCoordinatorMapper;
import snake.mzspaces.DataChangeEvent;
import snake.mzspaces.DataChangeListener;
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

	public GameList(DataChangeListener listener, PlayerList playerList)
	{
		this.listener = listener;
		this.playerList = playerList;
		
		NotificationManager notifManager = Util.getNotificationManager();
		try {
			this.notification = notifManager.createNotification(
					Util.getContainer(ContainerCoordinatorMapper.GAME_LIST),
					this,
					Operation.WRITE
			);
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		//search first free number
		int nr = 1;
		while (true)
		{
			for (int i = 0; i < games.size(); i++)
			{
				if ( ( (Game) games.elementAt(i)).getNr() == nr)
				{
					nr++;
					continue;
				}
			}
			break;
		}
		//create the game
		Game game = new Game(nr, name, leader, playerList);
		game.setLevelData(initData);
		games.addElement(game);
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
			if ( ( (Game) games.elementAt(i)).getNr() == game.getNr())
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
	public void entryOperationFinished(Notification arg0, Operation arg1,
			List<? extends Serializable> arg2) {
		
		// TODO: update games-list
		
		listener.dataChanged(new DataChangeEvent());
	}
}
