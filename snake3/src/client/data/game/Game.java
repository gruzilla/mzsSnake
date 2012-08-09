/**
 * 
 */
package client.data.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.data.player.Player;
import client.data.state.GameState;
import client.data.state.PlayerState;

/**
 * @author Jakob Lahmer, Matthias Steinbšck
 *
 */
public class Game implements Serializable
{
	private static final long serialVersionUID = -213203048176262335L;

	public static final int MAXPLAYERS = 4; //up to 4 players

	private ArrayList<Player> players = new ArrayList<Player>();
	private UUID id = UUID.randomUUID();
	private String name = null;
//	private String levelDir = snake.LevelsManager.DEFAULTLEVELDIR;
	private byte[] levelCheckSum;
	private GameState state = GameState.OPENEND;
//	private GameType gameType = GameType.points; //Game type
	private int winValue = 10; //Point or time value for determining game end (depenting on type of game)
//	private int collisionType = SnakeSpriteData.COLLISION_WALL | SnakeSpriteData.COLLISION_OTHER | SnakeSpriteData.COLLISION_OWN; //collision behaviour
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

	public ArrayList<Player> getPlayers()	{
		return players;
	}

	public UUID getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public int getPlayerCount()
	{
		return players.size();
	}

	public byte[] getLevelCheckSum()
	{
		return levelCheckSum;
	}

	public GameState getState()
	{
		return state;
	}

	public void setLevelCheckSum(byte[] newCheckSum)
	{
		levelCheckSum = newCheckSum;
	}

	public int getWinValue()
	{
		return winValue;
	}

	public boolean hasId() {
		return (id != null);
	}
	
	/**
	 * Get index of that player in the playerlist of the game.
	 * @param player Player
	 * @return index of the player, -1 if player does not belong to the game
	 */
	public int indexOf(Player player)
	{
		return this.players.indexOf(player);
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
		}
	}

	/**
	 * @param player
	 */
	public boolean joinGame(Player player) {
		if (getPlayerCount() < MAXPLAYERS && indexOf(player) == -1)	{
			players.add(player);
			if (leader == null) {
				leader = player;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Remove the player from the game.
	 * @param player Player
	 */
	public void leaveGame(Player player)	{
		int index = indexOf(player);
		if (index > -1)	{
			players.remove(index);
		}
	}
	
	/**
	 * @param player
	 */
	public void setPlayerStarted(Player player) {
		for(Player p : this.players)	{
			if(p.equals(player))	{
				p.setPlayerState(PlayerState.INIT);
				break;
			}
		}
	}
	
	/**
	 * checks if the game can be started
	 * @return
	 */
	public boolean isReadyToStart() {
		boolean ready = true;
		for(Player p : this.players)	{
			if(!p.isStateInit())	{
				ready = false;
				break;
			}
		}
		return ready;
	}
	
	
	public void startGame()	{
		this.state = GameState.READY;
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
			case OPENEND:
			{
				return name + " (" + players.size() + "/" + MAXPLAYERS + " open)";
			}
			case READY:
			{
				return name + " (" + players.size() + "/" + MAXPLAYERS + " started)";
			}
			case RUNNING:
			{
				return name + " (" + players.size() + "/" + MAXPLAYERS + " running)";
			}
			case ACTIVE:
			{
				return name + " (" + players.size() + "/" + MAXPLAYERS + " running)";
			}
			case ENDED:
			{
				return name + " (" + players.size() + "/" + MAXPLAYERS + " game over)";
			}
			case UNKNOWN:
			default:
			{
				return name;
			}
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
			return (this.getId().equals(( (Game) obj).getId()));
		}
		else
		{
			return super.equals(obj);
		}
	}

}
