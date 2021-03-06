package snake.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import snake.mzspaces.ContainerCoordinatorMapper;
import snake.mzspaces.Util;

/**
 * The Game class represents one game and its settings in corso space. The players
 * are not directly saved with the game, they are referenced by their oids.
 * @author Thomas Scheller, Markus Karolus
 */
public class Game implements Serializable
{
	private static final long serialVersionUID = 1L;
	public static final int MAXPLAYERS = 4; //up to 4 players

	private ArrayList<Player> players = new ArrayList<Player>();
	//private Vector playerOids = new Vector<CorsoVarOid> ();
	private UUID nr = UUID.randomUUID();
	private String name = null;
	private String levelDir = snake.LevelsManager.DEFAULTLEVELDIR;
	private byte[] levelCheckSum;
	private GameState state = GameState.opened;
	private GameType gameType = GameType.points; //Game type
	private int winValue = 10; //Point or time value for determining game end (depenting on type of game)
	private int collisionType = SnakeSpriteData.COLLISION_WALL | SnakeSpriteData.COLLISION_OTHER |
	SnakeSpriteData.COLLISION_OWN; //collision behaviour
	private Player leader = null;

	private Logger log = LoggerFactory.getLogger(Game.class);

	public Game() { }

	/**
	 * Create a new game with the given player as leader.
	 * @param nr number of the game, should be unique in the gamelist
	 * @param name name of the game
	 * @param leader the leader of the game (the player who opend the game)
	 * @param playerList PlayerList
	 */
	public Game(int nr, String name, Player leader) {
		this.name = name;
		joinGame(leader);
	}

	public Player getLeader()
	{
		return leader;
	}
	
	public Player getPlayer(int index)
	{
		return players.get(index);
	}

	public ArrayList<Player> getPlayers()
	{
		return players;
	}

	public UUID getNr()
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
			levelDir = newData.getLevelDir();
			levelCheckSum = newData.getCheckSum();
		}
		System.out.println("setLevelData: Daten in Space speichern " + levelDir + " " + levelCheckSum);
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

		// send changed game to space
		Util.getInstance().update(ContainerCoordinatorMapper.GAME_LIST, this, String.valueOf(getNr()));
	}

	/*
	public void setNr(UUID nr) { // former int
		this.nr = nr;
	}
	*/

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
		log.debug("current anz: "+ player + " :" +getPlayerAnz()+" index of player is "+indexOf(player)+" and max is "+MAXPLAYERS);
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
		log.debug("CCCCcurrent anz: "+ player + " :" +getPlayerAnz()+" index of player is "+indexOf(player)+" and max is "+MAXPLAYERS);
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
		log.debug("\n\n\ncurrent anz: "+getPlayerAnz()+" index of player is "+indexOf(player)+" and max is "+MAXPLAYERS);
	}

	/**
	 * Check if all players have at least state "init" (own variables created in space),
	 * so players can proceed to load all variables from the other players.
	 * @return true if all players are at state
	 */
	public boolean allPlayersReadyCreated()
	{
		for (Player player : players) {
			if ( player.isStateNotInit() ) {
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
	public boolean allPlayersReadyLoaded() {
		for (Player player : players) {
			if ( player.isStateInit() || player.isStateNotInit() ) {
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
			return (this.getNr().equals(( (Game) obj).getNr()));
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

	public boolean hasNr() {
		return (nr != null);
	}

	public void syncWith(Game game) {
		setLevelDir(game.levelDir);
		gameType = game.gameType;
		winValue = game.winValue;
		collisionType = game.collisionType;
		setState(game.state);
		for (Player p : players) {
			int i = game.players.indexOf(p);
			if (i < 0) {
				players.remove(i);
			}
		}
		for (Player p : game.players) {
			int i = players.indexOf(p);
			if (i < 0) {
				players.add(p);
			} else {
				players.get(i).syncWith(p);
			}
		}
	}
}
