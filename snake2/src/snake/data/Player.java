package snake.data;

import java.io.Serializable;

/**
 * Representation of a player in corsospace.
 * @author Thomas Scheller, Markus Karolus
 */
public class Player implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String name = null;
	private String skin = null;
	private int nr = 0;
	private boolean ready = false;
	private int points = 0;
	private PlayerState state = PlayerState.notinit;
	private int playerId;
	private SnakeState snakeState;
	private int headPos;
	private int tailPos;
	private SnakePos[] parts;

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
	 * /
	public void saveToSpace()
	{
		//save player to space
		//System.out.println("Player \"" + name + "\" saved to space.");
		try
		{
			if (playerId == 0)
			{
				playerId = 1;
			}
			
		}
		catch (CorsoException ex)
		{
			System.out.println("Player.save(): Corso Error occured!");
			ex.printStackTrace(System.out);
		}
	}

	/**
	 * Load player from space.
	 * /
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
	 * /
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
	 * Checks if object is another player an equal to this player. Two players
	 * are equal if their oid is the same.
	 * @param obj Object
	 * @return true if object is a player and players have the same oids
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof Player)
		{
			return playerId == (((Player)obj).getId());
		}
		else
		{
			return super.equals(obj);
		}
	}

	private int getId() {
		return playerId;
	}

	public void setSnakeState(SnakeState snakeState) {
		this.snakeState = snakeState;
	}

	public void setHeadPos(int headPos) {
		System.out.println(headPos);
		this.headPos = headPos;
	}

	public void setTailPos(int tailPos) {
		this.tailPos = tailPos;
		System.out.println("Tail: " + tailPos);
	}

	public void setParts(SnakePos[] parts) {
		this.parts = parts;
	}

	public int getHeadPos() {
		return headPos;
	}

	public int getTailPos() {
		return tailPos;
	}

	public SnakePos[] getParts() {
		return parts;
	}
	
	public void setPart(int i, SnakePos pos) {
		parts[i] = pos;
	}
	
	public SnakePos getPart(int i) {
		if (parts.length < i) return null;
		return parts[i];
	}
	
	public SnakePos getHeadPart() {
		return parts[headPos];
	}
	
	public SnakePos getTailPart() {
		return parts[tailPos];
	}

	public SnakeState getSnakeState() {
		return snakeState;
	}
}