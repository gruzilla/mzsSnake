package client.gui.menu;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.event.i.MenuEventListener;

public class MenuFrame extends JPanel /* implements NotificationListener */ {

	private static final long serialVersionUID = 1L;

	private MenuPanel menuPanel;
	private MPMenuPanel mpmenuPanel;
	private MPMenuNewGamePanel mpmenuNewPanel;
	private MenuSettingsPanel settingsPanel;

	private MenuEventListener listener;



	private static Logger log = LoggerFactory.getLogger(MenuFrame.class);
	

	
	public MenuFrame(MenuEventListener listener) {
		this.listener = listener;
		
		// init menu panels
		menuPanel = new MenuPanel(this.listener);
		mpmenuPanel = new MPMenuPanel(this.listener);
		mpmenuNewPanel = new MPMenuNewGamePanel(this.listener);
		settingsPanel = new MenuSettingsPanel(this.listener);
		
		this.showStartMenu();
		setVisible(true);
	}

	/**
	 * shows the start menu
	 */
	public void showStartMenu() {
		this.showMenu(menuPanel);
	}

	/**
	 * shows the multiplayer menu
	 */
	public void showMultiplayerMenu() {
		this.showMenu(mpmenuPanel);
	}
	
	/**
	 * shows the multiplayer newgame menu
	 */
	public void showMultiplayerNewGameMenu() {
		this.showMenu(mpmenuNewPanel);
	}

	/**
	 * shows the settings popup
	 */
	public void showSettingsMenu() {
		this.showMenu(settingsPanel);
	}
	
	/**
	 * shows the given menupanel
	 * @param mp
	 */
	private void showMenu(MenuPanel mp)	{
		this.removeAll();
		this.add(mp);
		this.repaint();
	}
	
	/**
	 * getter for mpmenupanel
	 * @return MPMenuPanel
	 */
	public MPMenuPanel getMPMenuPanel()	{
		return this.mpmenuPanel;
	}
}
