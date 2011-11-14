package snake.data;

import snake.corso.Util;

import java.io.Serializable;
import java.util.Vector;

/**
 * Representation of a player in corsospace.
 * @author Thomas Scheller, Markus Karolus
 */
public class Player implements Serializable
{
	private String name = null;
	private String skin = null;
	private int nr = 0;
	private boolean ready = false;
	private int points = 0;
	private PlayerState state = PlayerState.notinit;

	private CorsoVarOid playerOid = null; //to save the oid of itself

	private final String structName = "snakePlayerDataStruct";
	private final int structSizeInit = 10;
	private final int structSizeNotInit = 6;

	private CorsoVarOid snakeHeadOid = null;
	private CorsoVarOid snakeTailOid = null;
	private CorsoVarOid snakePosOid = null;
	private CorsoVarOid snakeStateOid = null;

	private PlayerNotifier notifier = null;
	private IDataChangeListener changeListener = null;

	public Player()
	{
	}

	/**
	 * Default constructor for a newly created player.
	 * @param name name of the player
	 * @param skin name of the snake skin used by the player
	 */
	public Player(String name, String skin)
	{
		this.name = name;
		this.skin = skin;
	}

	/**
	 * Default constructor for other players that are loaded from space. The player
	 * is automatically loaded from space, so a correct oid must be provided.
	 * @param playerOid oid to load the player from space
	 */
	public Player(CorsoVarOid playerOid)
	{
		//load player from CorsoSpace
		this.playerOid = playerOid;
		loadFromSpace();
		//start notifier thread so player will updated itself automatically
		startNotifier();
	}

	/**
	 * PlayerNotifier class is run in an own thread, a notification item for the player
	 * is generated and all changes that occur by notifications are automatically
	 * commited to the player object, a DataChangeEvent is generated if a DataChangeListener
	 * is set.
	 */
	private class PlayerNotifier implements Runnable
	{
		private boolean running = true;
		Player myPlayer = null;
		CorsoNotification notification = null;

		public PlayerNotifier(Player newMyPlayer)
		{
			myPlayer = newMyPlayer;
		}

		public void run()
		{
			try
			{
				// create notification item on this object ****************************
				CorsoNotificationItem notifItem =
					new CorsoNotificationItem(playerOid,
							0,
							CorsoNotificationItem.CURRENT_TIMESTAMP);

				// add the notification item to a vector ******************************
				Vector notifVec = new Vector();
				notifVec.addElement(notifItem);

				// create notification ************************************************
				notification =
					playerOid.getConnection().createNotification(notifVec, playerOid.getStrategy());

				CorsoData data = playerOid.getConnection().createData();
				// start notification and wait until oid is written *******************
				while (running)
				{
					//System.out.println("waiting until oid is written");
					CorsoNotificationItem fired =
						notification.start(CorsoConnection.INFINITE_TIMEOUT, data);

					// reading out the value of the written oid *************************
					if (fired != null && running) //only read when still running
					{
						if (fired.varOid() != null && fired.varOid().equals(playerOid))
						{
							int readOK = -5; //try to read the player multiple times if reading fails
							while (readOK < 0)
							{
								try
								{
									synchronized (myPlayer)
									{
										fired.varOid().readShareable(myPlayer, null, CorsoConnection.NO_TIMEOUT);
									}
									if (changeListener != null)
									{
										//create data change event
										// System.out.println("Player-Notify-DataChanged: " + myPlayer.getPlayerState() + ", PlayerObj: " + myPlayer);
										changeListener.dataChanged(new DataChangeEvent(myPlayer,DataChangeType.player));
									}

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
								System.out.println("GameListNotifier: Read ok (" + readOK + ")");
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
			if (notification != null)
			{
				try
				{
					notification.reset();
				}
				catch (CorsoException ex)
				{
				}
			}
		}

		public boolean isRunning()
		{
			return running;
		}
	}

	/**
	 * Read the the object from CorsoSpace.
	 * @param data CorsoData
	 * @throws CorsoDataException
	 */
	public void read(CorsoData data) throws CorsoDataException
	{
		StringBuffer dataName = new StringBuffer("");

		//control expected struct size
		int arity = data.getStructTag(dataName);
		if (arity != structSizeInit && arity != structSizeNotInit)
		{
			throw new CorsoDataException();
		}
		//control expected struct name
		if (!dataName.toString().equals(structName))
		{
			throw new CorsoDataException();
		}

		//read data
		name = data.getString();
		skin = data.getString();
		nr = data.getInt();
		ready = data.getBoolean();
		points = data.getInt();
		state = PlayerState.values()[data.getInt()];

		if (arity == structSizeInit)
		{
			snakeHeadOid = new CorsoVarOid();
			snakePosOid = new CorsoVarOid();
			snakeTailOid = new CorsoVarOid();
			snakeStateOid = new CorsoVarOid();

			data.getShareable(snakeHeadOid);
			data.getShareable(snakePosOid);
			data.getShareable(snakeTailOid);
			data.getShareable(snakeStateOid);
		}
	}

	/**
	 * Write the object to CorsoSpace.
	 * @param data CorsoData
	 * @throws CorsoDataException
	 */
	public void write(CorsoData data) throws CorsoDataException
	{
		//create struct with name and size
		if (snakeHeadOid != null)
		{
			data.putStructTag(structName, structSizeInit);
		}
		else
		{
			data.putStructTag(structName, structSizeNotInit);
		}
		//write data
		data.putString(name);
		data.putString(skin);
		data.putInt(nr);
		data.putBoolean(ready);
		data.putInt(points);
		data.putInt(state.ordinal());

		if (snakeHeadOid != null)
		{
			data.putShareable(snakeHeadOid);
			data.putShareable(snakePosOid);
			data.putShareable(snakeTailOid);
			data.putShareable(snakeStateOid);
		}
	}

	/**
	 * Set a DataChangeListener. Player will report any changes to this listener
	 * that occur by notifications.
	 * @param changeListener Class implementing IDataChangeListener interface
	 */
	public void setDataChangeListener(IDataChangeListener changeListener)
	{
		this.changeListener = changeListener;
	}

	public CorsoVarOid getSnakeHeadOid()
	{
		return snakeHeadOid;
	}

	public CorsoVarOid getSnakePosOid()
	{
		return snakePosOid;
	}

	public CorsoVarOid getSnakeTailOid()
	{
		return snakeTailOid;
	}

	public CorsoVarOid getSnakeStateOid()
	{
		return snakeStateOid;
	}

	/**
	 * Create all oids needed to save the snake of the player to space (head oid,
	 * tail oid, pos oid, state oid)
	 */
	public void createSnakeOIDs()
	{
		snakeHeadOid = Util.createVarOid();
		snakeTailOid = Util.createVarOid();
		snakePosOid = Util.createVarOid();
		snakeStateOid = Util.createVarOid();
	}

	public String getName()
	{
		return name;
	}

	public PlayerState getPlayerState()
	{
		return state;
	}

	public String getSkin()
	{
		return skin;
	}

	public int getNr()
	{
		return nr;
	}

	public boolean isReady()
	{
		return ready;
	}

	public void setReady(boolean value)
	{
		ready = value;
	}

	public int getPoints()
	{
		return points;
	}

	public void setPoints(int points)
	{
		this.points = points;
	}

	public void setName(String value)
	{
		name = value;
	}

	public void setSkin(String value)
	{
		skin = value;
	}

	public void setNr(int value)
	{
		nr = value;
	}

	public void setPlayerState(PlayerState newState)
	{
		state = newState;
	}

	public boolean isStateNotInit()
	{
		return state == PlayerState.notinit;
	}
	public boolean isStateLoaded()
	{
		return state == PlayerState.loaded;
	}
	public boolean isStateInit()
	{
		return state == PlayerState.init;
	}
	public boolean isStateStarting()
	{
		return state == PlayerState.starting;
	}

	/**
	 * Save player to space. A new CorsoVarOid is created, if the player does not
	 * have an oid yet.
	 */
	public void saveToSpace()
	{
		//save player to space
		//System.out.println("Player \"" + name + "\" saved to space.");
		try
		{
			if (playerOid == null)
			{
				playerOid = Util.createVarOid();
			}
			playerOid.writeShareable(this, CorsoConnection.INFINITE_TIMEOUT);
		}
		catch (CorsoException ex)
		{
			System.out.println("Player.save(): Corso Error occured!");
			ex.printStackTrace(System.out);
		}
	}

	/**
	 * Load player from space.
	 */
	public void loadFromSpace()
	{
		try
		{
			if (playerOid != null)
			{
				playerOid.readShareable(this, null, CorsoConnection.NO_TIMEOUT);
			}
			else
			{
				System.out.println("Can't load player, no OID!!");
			}
		}
		catch (CorsoException ex)
		{
			System.out.println("Player.loadFromSpace(): Corso Error occured:!");
			ex.printStackTrace(System.out);
		}
	}

	/**
	 * Delete player from space and all snake oids belonging to the player.
	 */
	public void deleteFromSpace()
	{
		try
		{
			if (playerOid != null)
			{
				playerOid.free(false);
				playerOid = null;
			}
			if (snakeHeadOid != null)
			{
				snakeHeadOid.free(false);
				snakeHeadOid = null;
			}
			if (snakePosOid != null)
			{
				snakePosOid.free(false);
				snakePosOid = null;
			}
			if (snakeTailOid != null)
			{
				snakeTailOid.free(false);
				snakeTailOid = null;
			}
			if (snakeStateOid != null)
			{
				snakeStateOid.free(false);
				snakeStateOid = null;
			}
		}
		catch (CorsoException ex)
		{
			System.out.println("Player.deleteFromSpace(): Corso Error occured:");
			ex.printStackTrace(System.out);
		}
	}

	public CorsoVarOid getOid()
	{
		return playerOid;
	}

	/**
	 * Start player notifier in new thread, if not already started.
	 */
	public void startNotifier()
	{
		if (notifier == null || !notifier.isRunning())
		{
			notifier = new PlayerNotifier(this);
			Thread notifierThread = new Thread(notifier);
			notifierThread.start();
		}
	}

	/**
	 * Stop player notifier, if running.
	 */
	public void stopNotifier()
	{
		if (notifier != null)
		{
			//System.out.println("Stop Player Notify: " + name);
			notifier.stop();
		}
	}

	/**
	 * Checks if object is another player an equal to this player. Two players
	 * are equal if their oid is the same.
	 * @param obj Object
	 * @return true if object is a player and players have the same oids
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof Player)
		{
			return playerOid.equals(((Player)obj).getOid());
		}
		else
		{
			return super.equals(obj);
		}
	}
}
