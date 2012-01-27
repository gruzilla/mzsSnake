package snake.data;

import java.util.Vector;

/**
 * A list of players in a game, that helps managing the DataChangeListener for
 * the players, and having the notifier threads of the players started and stopped
 * correctly.
 * @author Jakob Lahmer, Matthias Steinboeck based on work by Thomas Scheller, Markus Karolus
 */
public class PlayerList
{
	private Vector<Player> players = new Vector<Player> ();

	/**
	 * Constructor for player list.
	 * @param playerChangeListener ChangeListener that is set for the players
	 */
	public PlayerList()
	{
	}

	/**
	 * Add a new Player to the list, if it doesn't exist. Set the players changeListener
	 * to the one that was given to the playerlist, and start its notifier if not already
	 * startet.
	 * @param player the new player
	 */
	public void addPlayer(Player player)
	{
		players.addElement(player);
	}

	/**
	 * Get the index of the player in the list.
	 * @param playerOid oid of the player
	 * @return index of the player in the list, -1 if player is not found.
	 */
	public int indexOfPlayer(Player player)
	{
		return players.indexOf(player);
	}

	/**
	 * Remove all players from the list. Also remove all changeListeners from the players
	 * and stop their notifiers.
	 */
	public void removeAllPlayers()
	{
		players.clear();
	}
}
