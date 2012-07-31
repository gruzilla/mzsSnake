/**
 * 
 */
package client.event;

/**
 * @author Jakob Lahmer, Matthias Steinbšck
 *
 */
public class MenuEventData {

	private MenuEventType menuItem;
	
	public MenuEventData()	{
		
	}
	
	public MenuEventData(MenuEventType menuItem)	{
		this.menuItem = menuItem;
	}
	
	/**
	 * @return the menuItem
	 */
	public MenuEventType getMenuItem() {
		return menuItem;
	}

	/**
	 * @param menuItem the menuItem to set
	 */
	public void setMenuItem(MenuEventType menuItem) {
		this.menuItem = menuItem;
	}
}
