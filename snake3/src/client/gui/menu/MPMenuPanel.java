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

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import client.data.event.MenuEventData;
import client.data.event.MenuEventEnum;
import client.data.event.i.MenuEventListener;

/**
 * @author Jakob Lahmer, Matthias Steinbšck
 *
 */
public class MPMenuPanel extends MenuPanel {

	private static final long serialVersionUID = 1L;

	private JButton btNew;
	private JButton btJoin;
	private JScrollPane jScrollPane1;
	private JList lbGames;
	private JTextField tfNew;
	private JButton btView;
	private JButton btHighScore;
	
	/**
	 * @param menuChangeEventListener
	 */
	public MPMenuPanel(MenuEventListener menuChangeEventListener) {
		super(menuChangeEventListener);
		
	}
	
	@Override
	protected void initVariables() {
		btNew = new JButton();
		btJoin = new JButton();
		jScrollPane1 = new JScrollPane();
		lbGames = new JList();
		tfNew = new JTextField();
		btView = new JButton();
		btHighScore = new JButton();
	}
	
	/**
	 * Standard method to initialize all gui components.
	 */
	@Override
	protected void init() 
	{
		laTitle.setFont(new java.awt.Font("Dialog", Font.BOLD, 24));
		laTitle.setHorizontalAlignment(SwingConstants.CENTER);
		laTitle.setText("Multiplayer Menu");
		laTitle.setBounds(new Rectangle(290, 131, 220, 44));
		btNew.setBounds(new Rectangle(255, 179, 140, 32));
		btNew.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		btNew.setToolTipText("");
		btNew.setText("New Game");
		btNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				menuEventListener.menuChanged(new MenuEventData(MenuEventEnum.MULTIPLAYER_NEW));
			}
		});
		btJoin.setBounds(new Rectangle(255, 383, 150, 32));
		btJoin.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		btJoin.setText("Join");
		btJoin.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				menuEventListener.menuChanged(new MenuEventData(MenuEventEnum.MULTIPLAYER_JOIN));
			}
		});
		jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jScrollPane1.setBounds(new Rectangle(280, 224, 240, 148));
		btExit.setBounds(new Rectangle(325, 463, 150, 32));
		btExit.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		btExit.setText("Back");
		btExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				menuEventListener.menuChanged(new MenuEventData(MenuEventEnum.START_MENU));
			}
		});
		tfNew.setBounds(new Rectangle(405, 179, 140, 30));
		btView.setBounds(new Rectangle(410, 383, 150, 32));
		btView.setText("Watch");
		btView.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		btView.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				menuEventListener.menuChanged(new MenuEventData(MenuEventEnum.MULTIPLAYER_WATCH));
			}
		});
		btHighScore.setBounds(new Rectangle(325, 423, 150, 32));
		btHighScore.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		btHighScore.setText("HighScore");
		btHighScore.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				menuEventListener.menuChanged(new MenuEventData(MenuEventEnum.MULTIPLAYER_HIGHSCORE));
			}
		});
		jScrollPane1.getViewport().add(lbGames);
		this.add(laTitle);
		this.add(btJoin);
		this.add(btNew);
		this.add(jScrollPane1);
		this.add(btHighScore);
		this.add(btExit);
		this.add(tfNew);
		this.add(btView);
	}
	
	@Override
	protected Dimension getBoxDimensions()	{
		return new Dimension(400, 400);
	}
	
}
