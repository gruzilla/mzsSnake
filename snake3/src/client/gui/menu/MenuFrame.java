package client.gui.menu;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.data.event.i.MenuEventListener;

public class MenuFrame extends JFrame /* implements NotificationListener */ {

	private static final long serialVersionUID = 1L;

	private MenuPanel menuPanel;
	private MPMenuPanel mpmenuPanel;
	private MPMenuNewGamePanel mpmenuNewPanel;

	private MenuEventListener listener;


	private static Logger log = LoggerFactory.getLogger(MenuFrame.class);
	

	
	public MenuFrame(MenuEventListener listener) {
		this.listener = listener;
		
		// init menu panels
		menuPanel = new MenuPanel(this.listener);
		mpmenuPanel = new MPMenuPanel(this.listener);
		mpmenuNewPanel = new MPMenuNewGamePanel(this.listener);
		
		this.showStartMenu();
		setVisible(true);
		setResizable(false);
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
	 * shows the given menupanel
	 * @param mp
	 */
	private void showMenu(MenuPanel mp)	{
		this.getContentPane().removeAll();
		this.getContentPane().add(mp);
		this.pack();
		this.repaint();
	}
}
