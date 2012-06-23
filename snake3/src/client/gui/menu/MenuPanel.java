/**
 * 
 */
package client.gui.menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import client.data.Snake;
import client.data.event.MenuEventData;
import client.data.event.MenuEventEnum;
import client.data.event.i.MenuEventListener;
import client.gui.graphics.GraphicsHelper;
import client.gui.graphics.ImageLoader;

/**
 * represents the Main Menu
 * 	is parent for all other menu Panels
 * 
 * @author Jakob Lahmer, Matthias Steinbšck
 *
 */
public class MenuPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	protected JLabel laTitle = new JLabel();
	private JButton btMultiplayer = new JButton();
	private JButton btSingleplayer = new JButton();
	private JButton btSettings = new JButton();
	protected JButton btExit = new JButton();
	protected MenuEventListener menuEventListener;
	protected BufferedImage gameMap;
	protected Dimension gameMapSize;


	public MenuPanel(MenuEventListener menuChangeEventListener)	{
		this.menuEventListener = menuChangeEventListener;

		ImageLoader loader = new ImageLoader();
		gameMap = loader.loadImage("res/levels/Level1/back.jpg", false);

		gameMapSize = new Dimension(gameMap.getWidth(null), gameMap.getHeight(null));
		setPreferredSize(gameMapSize);
		setMinimumSize(gameMapSize);
		setMaximumSize(gameMapSize);
		setLayout(null);

		this.initVariables();
		this.initTitle();
		this.init();
	}

	/**
	 * inits the variables
	 */
	protected void initVariables() {}

	private void initTitle()	{
		laTitle.setFont(new java.awt.Font("Dialog", Font.BOLD, 40));
		laTitle.setForeground(new Color(0, 118, 0));
		laTitle.setHorizontalAlignment(SwingConstants.CENTER);
		laTitle.setBounds(new Rectangle(300, 182, 200, 44));
		laTitle.setText("SNAKE");
	}

	/**
	 * Standard method to initialize all gui components.
	 */
	protected void init() 
	{

		btMultiplayer.setBounds(new Rectangle(325, 287, 150, 32));
		btMultiplayer.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		btMultiplayer.setText("Multiplayer");
		btMultiplayer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				menuEventListener.menuChanged(new MenuEventData(MenuEventEnum.MULTIPLAYER_MENU));
			}
		});
		btSingleplayer.setBounds(new Rectangle(325, 247, 150, 32));
		btSingleplayer.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		btSingleplayer.setText("Singleplayer");
		btSingleplayer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				menuEventListener.menuChanged(new MenuEventData(MenuEventEnum.SINGLEPLAYER_MENU));

			}
		});
		btSettings.setBounds(new Rectangle(325, 327, 150, 32));
		btSettings.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		btSettings.setToolTipText("");
		btSettings.setText("Settings");
		btSettings.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				menuEventListener.menuChanged(new MenuEventData(MenuEventEnum.SETTINGS));
			}
		});
		btExit.setBounds(new Rectangle(325, 367, 150, 32));
		btExit.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		btExit.setText("Exit");
		btExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				menuEventListener.menuChanged(new MenuEventData(MenuEventEnum.EXIT));

			}
		});
		this.add(btSingleplayer);
		this.add(laTitle);
		this.add(btMultiplayer);
		this.add(btSettings);
		this.add(btExit);
	}

	/**
	 * sets the dimensions of the transparent background box for current panel
	 * @return Dimension
	 */
	protected Dimension getBoxDimensions()	{
		return new Dimension(350, 300);
	}

	/**
	 * Overwritten to paint a background picture, and a window around the components
	 * of the panel.
	 * @param g Graphics
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		//background
		g.drawImage(gameMap, 0, 0, this);
		//window
		GraphicsHelper.drawWindow( (Graphics2D) g, this.getBoxDimensions(), gameMapSize);
	}

}
