package snake;

import java.util.Vector;
import snake.data.*;
import snake.corso.Util;

/**
 * CollectableSpriteData manages the connection to the corospace for the collectables.
 * A notifier thread automatically updates the collectables when notifications occur.
 * Also manages the points of the player.
 * @author Thomas Scheller, Markus Karolus
 */
public class CollectableSpriteData
{
	private final int minBorder = 20;
	private final int minDistance = 50;
	private final double removingStep = 0.1;
	public boolean doublePoints = false;

	private CorsoVarOid collectableOidListOid = null;
	private CollectableOidList oids = null;
	private CollectablePos[] pos = null;
	private CorsoConnection conn = null;
	private boolean multiplayer = true;
	private CorsoStrategy strat = null;
	private Player myPlayer = null;
	private BackgroundManager gameMap = null;
	private java.util.Random rand;

	private Vector eatenPos = new Vector<CollectablePos> ();
	private Vector eatenStatus = new Vector<Double> ();

	private Integer points = 0; //Integer so it can be used for synchronization

	private DataNotifier notifier = null;

	/**
	 * DataNotifier class is run in an own thread. All changes that occur by notifications are automatically
	 * commited to the CollectableSpriteData object; if a notification for a new collectable is received,
	 * the old position is added to the eaten collectables so it will be drawn faded.
	 */
	private class DataNotifier implements Runnable
	{
		private boolean running = true;

		public DataNotifier()
		{
		}

		public void run()
		{
			try
			{
				Vector notifVec = new Vector();

				for (int i = 0; i < oids.oidList.length; i++)
				{
					// create notification items on this object ***************************
					CorsoNotificationItem notifItem =
						new CorsoNotificationItem(oids.oidList[i],
								0,
								CorsoNotificationItem.CURRENT_TIMESTAMP);

					// add the notification item to a vector ******************************
					notifVec.addElement(notifItem);
				}

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
					if (fired != null)
					{
						try
						{
							synchronized (pos)
							{
								synchronized (eatenPos)
								{
									//read new position
									CollectablePos newPos = new CollectablePos();
									newPos.read(data);

									//add old position to eaten positions
									eatenPos.addElement(pos[newPos.nr]);
									eatenStatus.addElement(1.0d);

									//save new positions
									pos[newPos.nr] = newPos;
								}
							}
						}
						catch (CorsoException ex)
						{
							System.out.println("CollectableSpriteData - DataNotifier: Corso Read-Error occured:");
							ex.printStackTrace(System.out);
						}
					}
				}
			}
			catch (CorsoException ex)
			{
				System.out.println("PlayerListNotifier: Corso Error occured:");
				ex.printStackTrace(System.out);
			}
		}

		public void stop()
		{
			running = false;
		}
	}

	/**
	 * Create a new CollectableSpriteData. The collectables are read from space, if
	 * they cannot be read they are created and saved to space. After that, the notifier
	 * thread is started.
	 * @param gameList GameListManager with the current game
	 * @param conn the connection to the corsospace
	 * @param myPlayer the own player
	 * @param aGameMap BackgroundManager for the current game
	 */
	public CollectableSpriteData(GameListManager gameList, CorsoConnection conn,
			Player myPlayer, BackgroundManager aGameMap)
	{
		gameMap = aGameMap;
		this.conn = conn;
		multiplayer = (conn != null);
		this.myPlayer = myPlayer;

		java.util.Date today = new java.util.Date();
		rand = new java.util.Random(today.getTime());

		oids = new CollectableOidList(gameMap.getCollectableCount());
		pos = new CollectablePos[oids.oidList.length];

		if (multiplayer)
		{
			try
			{
				//load collectable list from corsospace
				strat = new CorsoStrategy(Util.STRATEGY);
				if (gameList.getCurrentGame().getCollectableOID() != null)
				{
					collectableOidListOid = gameList.getCurrentGame().getCollectableOID();
				}
				else
				{
					collectableOidListOid = Util.createVarOid();
					gameList.setCollectableOID(collectableOidListOid);
				}

				try
				{
					//read oid list
					collectableOidListOid.readShareable(oids, null, CorsoConnection.NO_TIMEOUT);

					//read positions from the list
					for (int i = 0; i < oids.oidList.length; i++)
					{
						pos[i] = new CollectablePos();
						oids.oidList[i].readShareable(pos[i], null, CorsoConnection.NO_TIMEOUT);
					}
				}
				catch (CorsoException ex)
				{
					//collectables could not be read, create new
					int specials = 0;
					for (int i = 0; i < oids.oidList.length; i++)
					{
						if (specials < gameMap.getCollectableSpecialCount())
						{
							pos[i] = getNewPosition(i, true);
							specials++;
						}
						else
						{
							pos[i] = getNewPosition(i, false);
						}
						oids.oidList[i] = conn.createVarOid(strat);
						oids.oidList[i].writeShareable(pos[i], CorsoConnection.INFINITE_TIMEOUT);
					}
					collectableOidListOid.writeShareable(oids, CorsoConnection.INFINITE_TIMEOUT);
				}

				//Leader checks all positions once more to prevent having wrong collectables
				//that were generated with another background
				if (gameList.myPlayerIsLeader())
				{
					for (int i = 0; i < pos.length; i++)
					{
						if (!checkPosOk(pos[i].x, pos[i].y))
						{
							pos[i] = getNewPosition(i, (pos[i].type != CollectableType.normal));
							oids.oidList[i].writeShareable(pos[i], CorsoConnection.INFINITE_TIMEOUT);
						}
					}
				}

				//start DataNotifier thread
				notifier = new DataNotifier();
				Thread notifierThread = new Thread(notifier);
				notifierThread.start();
			}
			catch (CorsoException ex)
			{
				System.out.println("Corso Error occured:");
				ex.printStackTrace(System.out);
			}
		}
		else
		{
			//singleplayer variant: simply initialize all positions
			int specials = 0;
			for (int i = 0; i < pos.length; i++)
			{
				if (specials < gameMap.getCollectableSpecialCount())
				{
					pos[i] = getNewPosition(i, true);
					specials++;
				}
				else
				{
					pos[i] = getNewPosition(i, false);
				}
			}
		}
	}

	/**
	 * Randomly search for new collectable position until a valid position is found.
	 * @param nr nr of collectable (in the list of collectables)
	 * @param special true if collectbable should be a special collectable
	 * @return CollectablePos
	 */
	private CollectablePos getNewPosition(int nr, boolean special)
	{
		//search new position until a valid position is found
		int newX;
		int newY;
		do
		{
			newX = rand.nextInt(gameMap.getBackgroundWidth());
			newY = rand.nextInt(gameMap.getBackgroundHeight());
		}
		while (!checkPosOk(newX, newY));

		//determine type of new collectable
		CollectableType type = CollectableType.normal;
		if (special)
		{
			//CollectableType[] values = CollectableType.values();
			//type = values[rand.nextInt(values.length-1)+1];

			//alternating special collectables by nr
			if (nr % 2 == 0)
			{
				type = CollectableType.speedup;
			}
			else
			{
				type = CollectableType.doublepoints;
			}
		}

		return new CollectablePos(newX, newY, nr, type);
	}

	/**
	 * Check if the collectable position is valid. Position must not collide with a
	 * map obstacle and not be to near to a border or another collectable.
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return true if the position is valid
	 */
	private boolean checkPosOk(int x, int y)
	{
		//check if pos does not collide with obstacles
		if (!gameMap.freePlaceCollectable(x, y))
		{
			return false;
		}
		//check if pos too near to a border
		if (x < minBorder || x > (gameMap.getBackgroundWidth() - minBorder) ||
				y < minBorder || y > (gameMap.getBackgroundHeight() - minBorder))
		{
			return false;
		}
		//check if pos to near to other positions
		for (int i = 0; i < pos.length; i++)
		{
			if (pos[i] != null && (Math.abs(x - pos[i].x) < minDistance) &&
					(Math.abs(y - pos[i].y) < minDistance))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Create a new collectable at the given position, actualize the points of the player
	 * add old collectable to eaten collectables, and save new collectable and points to
	 * space.
	 * @param nr number of collectable
	 */
	public void eatCollectable(int nr)
	{
		synchronized (pos)
		{
			synchronized (eatenPos)
			{
				try
				{
					//add old collectable at this position to eaten collectables
					eatenPos.addElement(pos[nr]);
					eatenStatus.addElement(1.0d);

					if (pos[nr].type == CollectableType.normal)
					{
						//update points of the player
						points++;
						if (doublePoints)
						{
							points++; //again if double points powerup is active
						}

						//save points to space
						if (multiplayer)
						{
							myPlayer.setPoints(points);
							myPlayer.saveToSpace();
						}
					}

					//create new collectable and save to space
					pos[nr] = getNewPosition(nr, (pos[nr].type != CollectableType.normal));
					if (multiplayer)
					{
						oids.oidList[nr].writeShareable(pos[nr], CorsoConnection.INFINITE_TIMEOUT);
					}
				}
				catch (CorsoException ex)
				{
					System.out.println("Corso Error occured:");
					ex.printStackTrace(System.out);
				}
			}
		}
	}

	/**
	 * Update state of eaten collectables. Eaten collectables are faded increasing
	 * with every update. When fading of an eaten collectable is complet, remove it
	 * from the list.
	 */
	public void updateEatenCollectables()
	{
		synchronized (eatenPos)
		{
			for (int i = 0; i < eatenStatus.size(); i++)
			{
				double status = (Double) eatenStatus.elementAt(i);
				status -= removingStep;
				if (status <= 0)
				{
					eatenPos.removeElementAt(i);
					eatenStatus.removeElementAt(i);
				}
				else
				{
					eatenStatus.setElementAt(status, i);
				}
			}
		}
	}

	public CollectablePos[] getPositions()
	{
		return pos;
	}

	public Vector getEatenPositions()
	{
		return eatenPos;
	}

	public Vector getEatenStatus()
	{
		return eatenStatus;
	}

	public int getPoints()
	{
		return points;
	}

	/**
	 * Add the given value to the points of the player. Value can also be negative.
	 * If the points are negative after adding the value, the points are changed to 0.
	 * Additionaly the player and its points are saved to space.
	 * @param value int
	 */
	public void addPoints(int value)
	{
		synchronized (points)
		{
			points += value;
			if (points < 0)
			{
				points = 0;
			}
			if (multiplayer)
			{
				myPlayer.setPoints(points);
				myPlayer.saveToSpace();
			}
		}
	}

	/**
	 * Stop the notifier thread.
	 */
	public void stopNotifier()
	{
		if (notifier != null)
		{
			notifier.stop();
		}
	}
}
