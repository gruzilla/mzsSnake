/**
 * 
 */
package client.event;

import client.data.game.Game;

/**
 * @author Jakob Lahmer, Matthias Steinbšck
 *
 */
public class MenuEventMPNewData extends MenuEventData {

	private String mpName;
	private Game game;
	public MenuEventMPNewData()	{
		super();
	}
	
	public MenuEventMPNewData(MenuEventType menuItem)	{
		super(menuItem);
	}
	
	public MenuEventMPNewData(MenuEventType menuItem, String mpName)	{
		super(menuItem);
		this.mpName = mpName;
	}
	
	public MenuEventMPNewData(MenuEventType menuItem, Game game)	{
		super(menuItem);
		this.game = game;;
	}
	
	/**
	 * @return the mpName
	 */
	public String getMpName() {
		return mpName;
	}

	/**
	 * @param mpName the mpName to set
	 */
	public void setMpName(String mpName) {
		this.mpName = mpName;
	}
	
	public Game getGame()	{
		return this.game;
	}
}
