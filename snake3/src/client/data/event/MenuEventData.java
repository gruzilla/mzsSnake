/**
 * 
 */
package client.data.event;

/**
 * @author Jakob Lahmer, Matthias Steinbšck
 *
 */
public class MenuEventData {

	private MenuEventEnum menuItem;
	
	// here can be menuData set (for example Settings for multiplayer, etc)
	
	public MenuEventData()	{
		
	}
	
	public MenuEventData(MenuEventEnum menuItem)	{
		this.menuItem = menuItem;
	}
	
	
	/**
	 * @return the menuItem
	 */
	public MenuEventEnum getMenuItem() {
		return menuItem;
	}

	/**
	 * @param menuItem the menuItem to set
	 */
	public void setMenuItem(MenuEventEnum menuItem) {
		this.menuItem = menuItem;
	}
}
