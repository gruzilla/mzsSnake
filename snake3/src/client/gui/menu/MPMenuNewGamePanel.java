/**
 * 
 */
package client.gui.menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mzs.event.DataChangeEventData;
import mzs.event.DataChangeEventGameData;
import mzs.event.i.DataChangeEventListener;

import client.SnakeMain;
import client.data.game.Game;
import client.data.player.Player;
import client.event.MenuEventData;
import client.event.MenuEventMPNewData;
import client.event.MenuEventType;
import client.event.i.MenuEventListener;

/**
 * @author Jakob Lahmer, Matthias Steinbšck
 *
 */
public class MPMenuNewGamePanel extends MenuPanel implements DataChangeEventListener {

	private static final long serialVersionUID = 1L;


	private JButton btLevel;
	private JLabel laLevelPreview;
	private JLabel laLevelName;
	private JButton btStart;
	private ArrayList<JTextField> tfPlayer;
	private ArrayList<JLabel> laPlayerReady;
	private JLabel laPlayer1;
	private JLabel laPlayer2;
	private JLabel laPlayer3;
	private JLabel laPlayer4;
	private JLabel laCollision;
	private JCheckBox chCollisionOwn;
	private JCheckBox chCollisionEnemy;
	private JCheckBox chCollisionWall;
	private JLabel laGameMode;
	private JRadioButton rbGameTime;
	private JRadioButton rbGamePoints;
	private JSpinner spGamePoints;
	private JLabel laPlayer;
	private JSpinner spGameTime;
	private JTextField tfGamePoints;
	private JTextField tfGameTime;
	private JLabel laGameName;


	private Game currentGame;

	private static Logger log = LoggerFactory.getLogger(MPMenuNewGamePanel.class);

	/**
	 * @param menuChangeEventListener
	 */
	public MPMenuNewGamePanel(MenuEventListener menuChangeEventListener) {
		super(menuChangeEventListener);
	}


	@Override
	protected void initVariables() {
		tfPlayer = new ArrayList<JTextField>();
		tfPlayer.add(new JTextField());
		tfPlayer.add(new JTextField());
		tfPlayer.add(new JTextField());
		tfPlayer.add(new JTextField());
		laPlayerReady = new ArrayList<JLabel>();
		laPlayerReady.add(new JLabel());
		laPlayerReady.add(new JLabel());
		laPlayerReady.add(new JLabel());
		laPlayerReady.add(new JLabel());
		
		btLevel = new JButton();
		btExit = new JButton();
		laLevelPreview = new JLabel();
		btStart = new JButton();
		laPlayer2 = new JLabel();
		laLevelName = new JLabel();
		laPlayer3 = new JLabel();
		laPlayer4 = new JLabel();
		laPlayer1 = new JLabel();
		laCollision = new JLabel();
		chCollisionOwn = new JCheckBox();
		chCollisionEnemy = new JCheckBox();
		chCollisionWall = new JCheckBox();
		laGameMode = new JLabel();
		rbGameTime = new JRadioButton();
		rbGamePoints = new JRadioButton();
		spGamePoints = new JSpinner(new SpinnerNumberModel(10, 1, 50, 1));
		laPlayer = new JLabel();
		spGameTime = new JSpinner(new SpinnerNumberModel(100, 20, 300, 1));
		tfGamePoints = new JTextField();
		tfGameTime = new JTextField();
		laGameName = new JLabel();
	}

	/**
	 * init method
	 */
	@Override
	protected void init() {
		laTitle.setFont(new java.awt.Font("Dialog", Font.BOLD, 24));
		laTitle.setHorizontalAlignment(SwingConstants.CENTER);
		laTitle.setText("Multiplayer Menu - New Game");
		laTitle.setBounds(new Rectangle(194, 103, 407, 44));
		btLevel.setBounds(new Rectangle(410, 318, 154, 32));
		btLevel.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		btLevel.setText("Choose Level");
		btLevel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		btExit.setBounds(new Rectangle(402, 457, 150, 32));
		btExit.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		btExit.setText("Back");
		btExit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// @ DELETE GAME
				menuEventListener.menuChanged(new MenuEventData(MenuEventType.MULTIPLAYER_LEAVE));
			}
		});
		tfPlayer.get(1).setEditable(false);
		tfPlayer.get(1).setBounds(new Rectangle(243, 239, 140, 30));
		laLevelPreview.setBorder(BorderFactory.createEtchedBorder());
		laLevelPreview.setText("Level Preview...");
		laLevelPreview.setBounds(new Rectangle(411, 172, 160, 120));
		btStart.setBounds(new Rectangle(243, 457, 150, 32));
		btStart.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		btStart.setText("Start");
		btStart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// start the game
				menuEventListener.menuChanged(new MenuEventMPNewData(MenuEventType.MULTIPLAYER_START, currentGame));
			}
		});
		tfPlayer.get(2).setEditable(false);
		tfPlayer.get(2).setText("");
		tfPlayer.get(2).setBounds(new Rectangle(243, 272, 140, 30));
		tfPlayer.get(3).setEditable(false);
		tfPlayer.get(3).setText("");
		tfPlayer.get(3).setBounds(new Rectangle(243, 305, 140, 30));
		tfPlayer.get(0).setEditable(false);
		tfPlayer.get(0).setText("");
		tfPlayer.get(0).setBounds(new Rectangle(243, 206, 140, 30));
		laPlayer2.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		laPlayer2.setText("2.");
		laPlayer2.setBounds(new Rectangle(226, 241, 25, 28));
		laLevelName.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		laLevelName.setText("Player:");
		laLevelName.setBounds(new Rectangle(410, 289, 190, 28));
		laPlayer3.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		laPlayer3.setText("3.");
		laPlayer3.setBounds(new Rectangle(226, 274, 25, 28));
		laPlayer4.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		laPlayer4.setText("4.");
		laPlayer4.setBounds(new Rectangle(226, 307, 25, 28));
		laPlayer1.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		laPlayer1.setText("1.");
		laPlayer1.setBounds(new Rectangle(225, 208, 25, 28));
		laPlayerReady.get(2).setBackground(Color.red);
		laPlayerReady.get(2).setBorder(BorderFactory.createLineBorder(Color.black));
		laPlayerReady.get(2).setOpaque(true);
		laPlayerReady.get(2).setText("");
		laPlayerReady.get(2).setBounds(new Rectangle(387, 279, 16, 16));
		laPlayerReady.get(1).setBackground(Color.red);
		laPlayerReady.get(1).setBorder(BorderFactory.createLineBorder(Color.black));
		laPlayerReady.get(1).setOpaque(true);
		laPlayerReady.get(1).setBounds(new Rectangle(387, 246, 16, 16));
		laPlayerReady.get(3).setBackground(Color.red);
		laPlayerReady.get(3).setBorder(BorderFactory.createLineBorder(Color.black));
		laPlayerReady.get(3).setOpaque(true);
		laPlayerReady.get(3).setBounds(new Rectangle(387, 311, 16, 16));
		laPlayerReady.get(0).setBackground(Color.red);
		laPlayerReady.get(0).setBorder(BorderFactory.createLineBorder(Color.black));
		laPlayerReady.get(0).setOpaque(true);
		laPlayerReady.get(0).setBounds(new Rectangle(387, 214, 16, 16));
		laCollision.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		laCollision.setToolTipText("");
		laCollision.setText("Collision with:");
		laCollision.setBounds(new Rectangle(240, 348, 140, 28));
		chCollisionOwn.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		chCollisionOwn.setOpaque(false);
		chCollisionOwn.setText("itself");
		chCollisionOwn.setBounds(new Rectangle(238, 400, 136, 23));
		chCollisionOwn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		chCollisionEnemy.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		chCollisionEnemy.setOpaque(false);
		chCollisionEnemy.setText("other player");
		chCollisionEnemy.setBounds(new Rectangle(238, 424, 158, 23));
		chCollisionEnemy.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		chCollisionWall.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		chCollisionWall.setOpaque(false);
		chCollisionWall.setText("environment");
		chCollisionWall.setBounds(new Rectangle(238, 376, 136, 23));
		chCollisionWall.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		laGameMode.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		laGameMode.setToolTipText("");
		laGameMode.setText("Playtype:");
		laGameMode.setBounds(new Rectangle(409, 348, 140, 28));
		rbGameTime.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		rbGameTime.setOpaque(false);
		rbGameTime.setText("Time");
		rbGameTime.setBounds(new Rectangle(407, 400, 102, 23));
		rbGameTime.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		rbGamePoints.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		rbGamePoints.setOpaque(false);
		rbGamePoints.setText("Points");
		rbGamePoints.setBounds(new Rectangle(407, 376, 101, 23));
		rbGamePoints.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		spGamePoints.setBounds(new Rectangle(508, 376, 53, 23));
		spGamePoints.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		laPlayer.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		laPlayer.setText("Player:");
		laPlayer.setBounds(new Rectangle(225, 176, 140, 28));
		spGameTime.setBounds(new Rectangle(508, 400, 53, 23));
		spGameTime.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		tfGamePoints.setEditable(false);
		tfGamePoints.setBounds(new Rectangle(508, 376, 56, 23));
		tfGameTime.setEditable(false);
		tfGameTime.setText("jTextField1");
		tfGameTime.setBounds(new Rectangle(508, 400, 56, 23));
		laGameName.setFont(new java.awt.Font("Dialog", Font.BOLD, 20));
		laGameName.setHorizontalAlignment(SwingConstants.CENTER);
		laGameName.setText("New Game");
		laGameName.setBounds(new Rectangle(209, 141, 377, 35));
		this.add(tfPlayer.get(0));
		this.add(tfPlayer.get(1));
		this.add(tfPlayer.get(2));
		this.add(tfPlayer.get(3));
		this.add(laPlayer1);
		this.add(laPlayer4);
		this.add(laPlayer3);
		this.add(laPlayer2);
		this.add(laPlayerReady.get(3));
		this.add(laPlayerReady.get(2));
		this.add(laPlayerReady.get(1));
		this.add(laPlayerReady.get(0));
		this.add(btExit);
		this.add(btStart);
		this.add(chCollisionWall);
		this.add(chCollisionOwn);
		this.add(chCollisionEnemy);
		this.add(laCollision);
		this.add(laGameMode);
		this.add(rbGamePoints);
		this.add(rbGameTime);
		this.add(laLevelPreview);
		this.add(laPlayer);
		this.add(laLevelName);
		this.add(btLevel);
		this.add(spGameTime);
		this.add(spGamePoints);
		this.add(tfGamePoints);
		this.add(tfGameTime);
		this.add(laTitle);
		this.add(laGameName);
	}
	
	protected Dimension getBoxDimensions()	{
		return new Dimension(400, 400);
	}

	/**
	 * updates the values for the given game
	 * @param game
	 */
	public void updateValues(Game game)	{
		this.currentGame = game;
		
		// update playerfields
		for(int i=0; i < Game.MAXPLAYERS; i++)	{
			this.updatePlayer(i, tfPlayer.get(i), laPlayerReady.get(i));
		}
		
	}
	

	/**
	 * @param players
	 */
	private void updatePlayer(int index, JTextField tfPlayer, JLabel laPlayerReady) {
		
		if (this.currentGame.getPlayerCount() > index)
		{
			Player activePlayer = this.currentGame.getPlayer(index);
			synchronized (tfPlayer)
			{
				//display the playername in the textfield, set the colour to blue if the player is leader
				tfPlayer.setText(activePlayer.getName());
				if (activePlayer == this.currentGame.getLeader())
				{
					tfPlayer.setForeground(Color.BLUE);
				}
				else
				{
					tfPlayer.setForeground(Color.BLACK);
				}
			}
			//display ready information in the label, green if player is ready, otherwise red
			if (activePlayer.isStateInit() || activePlayer.isStateLoaded()) {
				laPlayerReady.setBackground(Color.GREEN);
			} else {
				laPlayerReady.setBackground(Color.RED);
			}
		}
		else
		{
			tfPlayer.setText("");
			laPlayerReady.setBackground(Color.GRAY);
		}
		tfPlayer.repaint(); //repaint because automatic refresh does not work reliable
		laPlayerReady.repaint(); //repaint because automatic refresh does not work reliable
	}


	/* (non-Javadoc)
	 * @see mzs.event.i.DataChangeEventListener#dataChanged(mzs.event.DataChangeEventData)
	 */
	@Override
	public void dataChanged(DataChangeEventData changeEvent) {
		if(changeEvent instanceof DataChangeEventGameData)	{
			this.updateValues(((DataChangeEventGameData) changeEvent).getGame());
		}
	}
}
