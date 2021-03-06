package snake.data;

import java.io.Serializable;
import java.util.ArrayList;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsCoreException;

import snake.corso.ContainerCoordinatorMapper;
import snake.corso.Util;

import snake.data.*;
import snake.SnakeSpriteData;

/**
 * The Game class represents one game and its settings in corso space. The players
 * are not directly saved with the game, they are referenced by their oids.
 * @author Thomas Scheller, Markus Karolus
 */
public class Game implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int MAXPLAYERS = 4; //up to 4 players

	private ArrayList<Player> players = new ArrayList<Player>();
	//private Vector playerOids = new Vector<CorsoVarOid> ();
	private int nr = 0;
	private String name = null;
	private String levelDir = snake.LevelsManager.DEFAULTLEVELDIR;
	private byte[] levelCheckSum;
	private GameState state = GameState.opened;
	private GameType gameType = GameType.points; //Game type
	private int winValue = 10; //Point or time value for determining game end (depenting on type of game)
	private int collisionType = SnakeSpriteData.COLLISION_WALL | SnakeSpriteData.COLLISION_OTHER |
	SnakeSpriteData.COLLISION_OWN; //collision behaviour
	//private CorsoVarOid leaderOID = null;
	//private CorsoVarOid levelOID = null;
	private Player leader = null;
	//private CorsoVarOid collectableOID = null;
	private PlayerList playerList = null;

	//private final String structName = "snakeGameDataStruct";
	//private final int structSizeMin = 12;
	private Capi conn;

	public Game(PlayerList playerList)
	{
		this.playerList = playerList;
	}

	/**
	 * Create a new game with the given player as leader.
	 * @param nr number of the game, should be unique in the gamelist
	 * @param name name of the game
	 * @param leader the leader of the game (the player who opend the game)
	 * @param playerList PlayerList
	 */
	public Game(int nr, String name, Player leader, PlayerList playerList)
	{
		try {
			this.conn = Util.getConnection();
		} catch (Exception e) {
			System.err.println("Could not create connection");
			e.printStackTrace();
		}
		this.nr = nr;
		this.name = name;
		this.playerList = playerList;
		//this.collectableOID = snake.corso.Util.createVarOid();
		//this.levelOID = snake.corso.Util.createVarOid();
		joinGame(leader);
	}

	/**
	 * Read the the object from CorsoSpace.
	 * @param data CorsoData
	 * @throws CorsoDataException
	 * /
	public void read(CorsoData data) throws CorsoDataException
	{
		StringBuffer dataName = new StringBuffer("");

		//control expected struct size
		int arity = data.getStructTag(dataName);
		if (arity < structSizeMin)
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
		nr = data.getInt();
		state = GameState.valueOf(data.getString());
		levelDir = data.getString();
		levelCheckSum = data.getBinary();
		//levelOID = new CorsoVarOid();
		//data.getShareable(levelOID);
		gameType = GameType.valueOf(data.getString());
		winValue = data.getInt();
		collisionType = data.getInt();
		leaderOID = new CorsoVarOid();
		data.getShareable(leaderOID);
		collectableOID = new CorsoVarOid();
		data.getShareable(collectableOID);

		int anzPlayers = data.getInt();

		if (anzPlayers > 0)
		{
			Vector newPlayerOids = new Vector<CorsoVarOid> ();

			for (int i = 0; i < anzPlayers; i++)
			{
				CorsoVarOid oid = new CorsoVarOid();
				data.getShareable(oid);
				newPlayerOids.addElement(oid);
			}

			//read player data
			for (int i = 0; i < newPlayerOids.size(); i++)
			{
				Player player = playerList.getPlayer(new CorsoVarOid( (CorsoVarOid) newPlayerOids.elementAt(i)));
				
				//if (player.getOid().equals(leaderOID))
				if (player.equals(leader))
				{
					leader = player;
				}

				players.add(player);
				//playerOids.addElement(player.getOid());
			}
		}
		else
		{
			//playerOids = new Vector<CorsoVarOid> ();
			players = new ArrayList<Player>();
		}
	}

	/**
	 * Write the object to CorsoSpace.
	 * @param data CorsoData
	 * @throws CorsoDataException
	 * /
	public void write(CorsoData data) throws CorsoDataException
	{
		data.putStructTag(structName, structSizeMin + (playerOids.size()));
		//write data
		data.putString(name);
		data.putInt(nr);
		data.putString(state.toString());
		data.putString(levelDir);
		data.putBinary(levelCheckSum);
		data.putShareable(levelOID);
		data.putString(gameType.toString());
		data.putInt(winValue);
		data.putInt(collisionType);
		data.putShareable(leaderOID);
		data.putShareable(collectableOID);
		data.putInt(playerOids.size());

		for (int i = 0; i < playerOids.size(); i++)
		{
			data.putShareable( (CorsoVarOid) playerOids.elementAt(i));
		}
	}

	public CorsoVarOid getCollectableOID()
	{
		return collectableOID;
	}

	public void setCollectableOID(CorsoVarOid newOID)
	{
		collectableOID = newOID;
	}
	*/

	public Player getLeader()
	{
		return leader;
	}

	public Player getPlayer(int index)
	{
		return players.get(index);
	}

	public int getNr()
	{
		return nr;
	}

	public String getName()
	{
		return name;
	}

	public int getPlayerAnz()
	{
		return players.size();
	}

	public GameState getState()
	{
		return state;
	}

	public void setState(GameState value)
	{
		state = value;
	}

	public String getLevelDir()
	{
		return levelDir;
	}

	public byte[] getLevelCheckSum()
	{
		return levelCheckSum;
	}

	public LevelData getLevelData()
	{
		LevelData levelData = new LevelData();
		return levelData;
	}

	/**
	 * Change the level for the game. The new level is written to the space, so the
	 * other players can download it if they don't have it.
	 * @param newData LevelData
	 */
	public void setLevelData(LevelData newData)
	{
		if (!java.util.Arrays.equals(newData.getCheckSum(), levelCheckSum) ||
				!newData.getLevelDir().equals(levelDir))
		{
			//System.out.println("setLevelData: Daten in Space speichern");
			levelDir = newData.getLevelDir();
			levelCheckSum = newData.getCheckSum();
			ContainerReference container = Util.getContainer(ContainerCoordinatorMapper.LEVEL_DATA);
			try {
				this.conn.write(container, new Entry(newData));
			} catch (MzsCoreException e) {
				// TODO Auto-generated catch block
				System.out.println("setLevelData: MzsError occured:");
				e.printStackTrace(System.out);
			}
		}
	}

	public void setLevelDir(String value)
	{
		levelDir = value;
	}

	public void setLevelCheckSum(byte[] newCheckSum)
	{
		levelCheckSum = newCheckSum;
	}

	public GameType getGameType()
	{
		return gameType;
	}

	public int getWinValue()
	{
		return winValue;
	}

	public int getCollisionType()
	{
		return collisionType;
	}

	public boolean getCollisionTypeWall()
	{
		return (collisionType & SnakeSpriteData.COLLISION_WALL) == SnakeSpriteData.COLLISION_WALL;
	}

	public boolean getCollisionTypeOwn()
	{
		return (collisionType & SnakeSpriteData.COLLISION_OWN) == SnakeSpriteData.COLLISION_OWN;
	}

	public boolean getCollisionTypeOther()
	{
		return (collisionType & SnakeSpriteData.COLLISION_OTHER) == SnakeSpriteData.COLLISION_OTHER;
	}

	/**
	 * Save the new settings.
	 * @param gameType new GameType
	 * @param winValue new WinValue
	 * @param collisionWall collision with walls
	 * @param collisionOwn collision with self
	 * @param collisionOther collision with other players
	 */
	public void setGameData(GameType gameType, int winValue, boolean collisionWall, boolean collisionOwn,
			boolean collisionOther)
	{
		//Einstellungen zu Spieltyp und Kollisionsverhalten setzen
		this.gameType = gameType;
		this.winValue = winValue;
		collisionType = SnakeSpriteData.COLLISION_NONE;
		if (collisionWall)
		{
			collisionType |= SnakeSpriteData.COLLISION_WALL;
		}
		if (collisionOwn)
		{
			collisionType |= SnakeSpriteData.COLLISION_OWN;
		}
		if (collisionOther)
		{
			collisionType |= SnakeSpriteData.COLLISION_OTHER;
		}
	}

	/**
	 * Get index of that player in the playerlist of the game.
	 * @param player Player
	 * @return index of the player, -1 if player does not belong to the game
	 */
	public int indexOf(Player player)
	{
		return players.indexOf(player);
		//return playerOids.indexOf(player.getOid());
	}

	/**
	 * Add the player to the game. If the game has no leader yet, make the new player
	 * the leader.
	 * @param player Player
	 */
	public void joinGame(Player player)
	{
		//Spieler zu Spiel hinzufuegen
		if (getPlayerAnz() < MAXPLAYERS && indexOf(player) == -1)
		{
			players.add(player);
			//playerOids.addElement(player.getOid());
			//Spieler zum Anfuehrer machen wenn noch keiner vorhanden
			/*if (leaderOID == null)
			{
				leaderOID = player.getOid();
			}*/
			if (leader == null) {
				leader = player;
			}
		}
	}

	/**
	 * Remove the player from the game.
	 * @param player Player
	 */
	public void leaveGame(Player player)
	{
		//Spieler aus Spiel entfernen
		int index = indexOf(player);
		if (index > -1)
		{
			players.remove(index);
			//playerOids.removeElementAt(index);
		}
	}

	/**
	 * Check if all players have at least state "init" (own variables created in space),
	 * so players can proceed to load all variables from the other players.
	 * @return true if all players are at state
	 */
	public boolean allPlayersReadyCreated()
	{
		for (int i = 0; i < players.size(); i++)
		{
			if ( ( (Player) players.get(i)).isStateNotInit())
			{
				return false; //found a player with state notinit
			}
		}

		return true;
	}

	/**
	 * Check if all players have at least state "loaded" (all variables loaded from space),
	 * so the game can be startet.
	 * @return true if all players are at state loaded
	 */
	public boolean allPlayersReadyLoaded()
	{
		for (int i = 0; i < players.size(); i++)
		{
			if ( ( ( (Player) players.get(i)).isStateInit()) ||
					( ( (Player) players.get(i)).isStateNotInit()))
			{
				return false; //found a player with state less than loaded
			}
		}

		return true;
	}

	/**
	 * Set first player as leader. This is necessary when the previous leader left the game.
	 */
	public void updateLeader()
	{
		if (players.size() > 0)
		{
			Player newLeader = players.get(0);
			leader = newLeader;
			//leaderOID = newLeader.getOid();
		}
	}

	/**
	 * Special comparison when comparing two games: same if nr is equal
	 * @param obj Object to compare
	 * @return true if object is a game and numbers are equal
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof Game)
		{
			return (this.getNr() == ( (Game) obj).getNr());
		}
		else
		{
			return super.equals(obj);
		}
	}

	/**
	 * Check if the settings of the game are equal.
	 * @param game Game
	 * @return true if the game settings are equal
	 */
	public boolean settingsEqual(Game game)
	{
		return (game != null &&
				getPlayerAnz() == game.getPlayerAnz() &&
				getLevelDir().equals(game.getLevelDir()) &&
				getGameType() == game.getGameType() &&
				getCollisionType() == game.getCollisionType());
	}

	/**
	 * Return a String representation of the game, giving information about the current
	 * number of players and the game state. This is shown in the multiplayer game list.
	 * @return String representation of the game
	 */
	public String toString()
	{
		switch (state)
		{
		case opened:
		{
			return name + " (" + players.size() + "/" + MAXPLAYERS + " open)";
		}
		case ready:
		{
			return name + " (" + players.size() + "/" + MAXPLAYERS + " started)";
		}
		case running:
		{
			return name + " (" + players.size() + "/" + MAXPLAYERS + " running)";
		}
		case aktiv:
		{
			return name + " (" + players.size() + "/" + MAXPLAYERS + " running)";
		}
		case ended:
		{
			return name + " (" + players.size() + "/" + MAXPLAYERS + " game over)";
		}
		case unknown:
		{
			return name;
		}
		default:
		{
			return name;

		}
		}
	}
}
