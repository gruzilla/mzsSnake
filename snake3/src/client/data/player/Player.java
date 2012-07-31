/**
 * 
 */
package client.data.player;

import java.io.Serializable;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.data.state.PlayerState;

/**
 * @author Jakob Lahmer, Matthias Steinbšck
 *
 */
public class Player implements Serializable {

	private static final long serialVersionUID = -6686013630813091200L;
	
	private UUID id = UUID.randomUUID();
	private String name;
	private String skin;
	private PlayerState state = PlayerState.NOTINIT;

	private static Logger log = LoggerFactory.getLogger(Player.class);
	
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
	
	public PlayerState getPlayerState()	{
		return state;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the skin
	 */
	public String getSkin() {
		return skin;
	}

	/**
	 * @param skin the skin to set
	 */
	public void setSkin(String skin) {
		this.skin = skin;
	}

	/**
	 * @return the id
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(UUID id) {
		this.id = id;
	}

	public void setPlayerState(PlayerState newState)	{
		log.debug("\n\nSETTING PLAYER STATE TO "+newState+"\n\n");
		state = newState;
	}

	public boolean isStateNotInit()	{
		return state == PlayerState.NOTINIT;
	}
	public boolean isStateLoaded()	{
		return state == PlayerState.LOADED;
	}
	public boolean isStateInit()	{
		return state == PlayerState.INIT;
	}
	public boolean isStateStarting()	{
		return state == PlayerState.STARTING;
	}

}
