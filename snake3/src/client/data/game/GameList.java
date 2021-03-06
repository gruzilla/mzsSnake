/**
 * 
 */
package client.data.game;

import java.io.Serializable;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import mzs.event.DataChangeEventGameData;
import mzs.event.DataChangeEventGameListData;
import mzs.event.DataChangeEventType;
import mzs.event.i.DataChangeEventListener;
import mzs.util.ContainerCoordinatorMapper;
import mzs.util.Util;

import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.FifoCoordinator.FifoSelector;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.data.player.Player;
import client.data.state.GameState;
import client.event.i.GameStateEventListener;

/**
 * @author Jakob Lahmer, Matthias Steinb�ck
 *
 * stores games of space locally, notified by xvsm notifications
 */
public class GameList implements Serializable, NotificationListener {

	private static final long serialVersionUID = -7243366780152664842L;

	private Vector<Game> games;
	private DataChangeEventListener listener;
	
	private Logger log = LoggerFactory.getLogger(GameList.class);

	private Notification notification;

	private Game currentGame = null;

	private GameStateEventListener stateListener;
	
	
	
	/**
	 * create a new game list.
	 *
	 * @param listener DataChangeListener
	 * @param playerList
	 */
	public GameList(GameStateEventListener stateListener)	{
		this.games = new Vector<Game>();
		this.stateListener = stateListener;
	}
	
	/**
	 * sets a new datachangelistener
	 * @param listener
	 */
	public void setDataChangeEventListener(DataChangeEventListener listener)	{
		this.listener = listener;
	}
	
	public boolean initGameList()	{
		games = new Vector<Game>();
		// read current games and add them to the local vector
		ContainerReference gamesContainer;
		try {
			gamesContainer = Util.getInstance().getContainer(ContainerCoordinatorMapper.GAME_LIST);
			//log.debug("trying to read games!");
			ArrayList<Serializable> spaceGames = Util.getInstance().getConnection().read(
					gamesContainer, 
					//AnyCoordinator.newSelector(),
					FifoCoordinator.newSelector(FifoSelector.COUNT_ALL),
					0,
					null);
			
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
				log.info("ERROR reading games!\n"+e1.getMessage());
			} else {
				log.info("ERROR connecting to space!");
			}
			return false;
		} catch (ConnectException e2) {
			return false;
		} catch (Exception e1) {
			return false;
		}
		
		// create a notification that updates the list whenever a game gets added
		NotificationManager notifManager;
		try {
			notifManager = Util.getInstance().getNotificationManager();
			this.notification = notifManager.createNotification(
					Util.getInstance().getContainer(ContainerCoordinatorMapper.GAME_LIST),
					this,
					Operation.WRITE, Operation.DELETE
			);
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			log.error("ERROR: could not create notification (mzsexception)");
			return false;
		} catch (InterruptedException e) {
			log.error("ERROR: could not create notification (interrupted)");
			// TODO Auto-generated catch block
			return false;
		} finally {
			if (notification == null) {
				log.error("ERROR: could not create notification (null after creation)");
				return false;
			} else {
				log.info("NOTFICATION successfully created");
			}
		}
		
		// inform listener about games
		listener.dataChanged(new DataChangeEventGameListData(DataChangeEventType.GAME, this.games));
		
		return true;
	}

	/**
	 * Check if the game name is already in use.
	 * @param name game name to check
	 * @return true if the a game with that name already exists
	 */
	public boolean gameNameExists(String name)	{
		for (int i = 0; i < games.size(); i++)	{
			if ( ( (Game) games.elementAt(i)).getName().equals(name))	{
				return true;
			}
		}
		return false;
	}

	/**
	 * returns a game identified by name
	 * @param gameName
	 * @return Game or null
	 */
	public Game getGamePerName(String gameName)	{
		for (int i = 0; i < games.size(); i++)	{
			if ((games.elementAt(i)).getName().equals(gameName))	{
				return games.elementAt(i);
			}
		}
		return null;
	}
	
	/**
	 * @param mpName
	 * @return
	 */
	public boolean joinGame(String mpName, Player player, DataChangeEventListener newListener) {
		if(!isGameJoinable(mpName))
			return false;
		
		Game game = this.getGamePerName(mpName);
		if(!game.joinGame(player))
			return false;

		this.currentGame = game;
		if(this.updateCurrentGame())	{
			this.listener = newListener;
			return true;
		}
		
		this.currentGame = null;
		return false;
	}
	
	public void leaveCurrentGame(Player player, DataChangeEventListener newListener)	{
		this.currentGame.leaveGame(player);
		this.listener = newListener;
		
		// check if game has players
		if(this.currentGame.getPlayerCount() < 1)	{
			this.removeCurrentGame();
		} else {
			// determine new leader
			if(this.currentGame.getLeader().equals(player))	{
				this.currentGame.updateLeader();
			}
			this.updateCurrentGame();
		}
		this.currentGame = null;
	}
	
	/**
	 * check if given game is joinable
	 * @param index
	 * @return
	 */
	private boolean isGameJoinable(String name)	{
		Game game;
		if((game = this.getGamePerName(name)) != null)	{
			return (game.getPlayerCount() < Game.MAXPLAYERS) && (game.getState() == GameState.OPENEND);
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
	public void createGame(String name, Player leader /*,LevelData initData */)
	{
		// create the game, initialize it with nr 0, because our IndexAspect creates teh number
		this.currentGame = new Game(0, name, leader);
//		game.setLevelData(initData);
		games.addElement(currentGame);
		
		// however we have to write it to the space. but first we have to delete this very game:
		this.updateCurrentGame();
	}
	
	/**
	 * @param player
	 * @return
	 */
	public boolean startGame(Player player) {
		if(this.currentGame == null)	{
			return false;
		}
		this.currentGame.setPlayerStarted(player);
		
		if(this.currentGame.isReadyToStart())	{
			this.currentGame.startGame();
		}
		
		log.info("GAME STATE: " + currentGame.getState());
		
		this.updateCurrentGame();
		
		return this.currentGame.isReadyToStart();
	}
	
	
	/**
	 * updates the current game in space
	 * @return
	 */
	private boolean updateCurrentGame()	{
		try {
			Util.getInstance().update(
					ContainerCoordinatorMapper.GAME_LIST,
					this.currentGame,
					String.valueOf(this.currentGame.getId())
			);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Remove the current game from the list.
	 * @param game Game
	 */
	public void removeCurrentGame()	{
		if(this.currentGame != null)	{
			games.remove(this.currentGame);
			Util.getInstance().delete(ContainerCoordinatorMapper.GAME_LIST, String.valueOf(this.currentGame.getId()));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.mozartspaces.notifications.NotificationListener#entryOperationFinished(org.mozartspaces.notifications.Notification, org.mozartspaces.notifications.Operation, java.util.List)
	 */
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
						if (games.get(i).getId().equals(game.getId())) {
							found = true;
//							games.get(i).syncWith(game);
							this.games.remove(i);
							this.games.add(i, game);
							
							if(this.currentGame == null)
								changed = true;
							else if(this.currentGame.equals(game))	{
								// update current game
								this.currentGame = this.games.get(i);
								changed = true;
							}
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
//			log.debug("\n\n\n GAME DELETED -> delete it from local game list");
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
			// no currentgame => user is in multiplayer menu
			if(this.currentGame == null)	{
				listener.dataChanged(new DataChangeEventGameListData(DataChangeEventType.GAME, this.games));
			} else {
				if(this.currentGame.getState() == GameState.READY)	{
					this.stateListener.gameStateChanged(this.currentGame);
				} else
					// user is in new multiplayer game menu
					listener.dataChanged(new DataChangeEventGameData(DataChangeEventType.GAME, this.currentGame));
			}
		}
	}
	
}
