/**
 * 
 */
package client.event;

/**
 * @author Jakob Lahmer, Matthias Steinbšck
 *
 */
public class MenuEventMPNewData extends MenuEventData {

	private String mpName;
	
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
}
