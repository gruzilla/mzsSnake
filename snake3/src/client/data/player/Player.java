/**
 * 
 */
package client.data.player;

import java.io.Serializable;

/**
 * @author Jakob Lahmer, Matthias Steinbšck
 *
 */
public class Player implements Serializable {

	private static final long serialVersionUID = -6686013630813091200L;
	private String name;
	private String skin;

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

}
