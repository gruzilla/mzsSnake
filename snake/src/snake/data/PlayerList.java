package snake.data;

import java.util.Vector;
import corso.lang.*;

/**
 * A list of players in a game, that helps managing the DataChangeListener for
 * the players, and having the notifier threads of the players started and stopped
 * correctly.
 * @author Thomas Scheller, Markus Karolus
 */
public class PlayerList
{
  private Vector players = new Vector<Player> ();
  private IDataChangeListener playerChangeListener = null;

  /**
   * Constructor for player list.
   * @param playerChangeListener ChangeListener that is set for the players
   */
  public PlayerList(IDataChangeListener playerChangeListener)
  {
    this.playerChangeListener = playerChangeListener;
  }

  /**
   * Finds a player in the list by its oid. If the player is not found in the list,
   * a new player object is created and automatically added to the list, and its
   * changeListener set to the one that was given to the playerlist.
   * @param playerOid the players oid
   * @return the player belonging to the given oid
   */
  public Player getPlayer(CorsoVarOid playerOid)
  {
    //find player by oid
    int index = indexOfPlayer(playerOid);
    if (index == -1)
    {
      //player not found, create new player object
      Player player = new Player(playerOid);
      player.setDataChangeListener(playerChangeListener);
      players.addElement(player);
      return player;
    }
    return (Player)players.elementAt(index);
  }

  /**
   * Add a new Player to the list, if it doesn't exist. Set the players changeListener
   * to the one that was given to the playerlist, and start its notifier if not already
   * startet.
   * @param player the new player
   */
  public void addPlayer(Player player)
  {
    if (player.getOid() != null && indexOfPlayer(player.getOid()) == -1)
    {
      player.setDataChangeListener(playerChangeListener);
      player.startNotifier();
      players.addElement(player);
    }
  }

  /**
   * Get the index of the player in the list.
   * @param playerOid oid of the player
   * @return index of the player in the list, -1 if player is not found.
   */
  private int indexOfPlayer(CorsoVarOid playerOid)
  {
    for (int i = 0; i < players.size(); i++)
    {
      if ( ( (Player) players.elementAt(i)).getOid().equals(playerOid))
      {
        return i;
      }
    }
    return -1;
  }

  /**
   * Remove all players from the list. Also remove all changeListeners from the players
   * and stop their notifiers.
   */
  public void removeAllPlayers()
  {
    for (int i = 0; i < players.size(); i++)
    {
      ((Player)players.elementAt(i)).setDataChangeListener(null);
      ((Player)players.elementAt(i)).stopNotifier();
    }
    players.clear();
  }
}
