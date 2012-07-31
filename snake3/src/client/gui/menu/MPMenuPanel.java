/**
 * 
 */
package client.gui.menu;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Messages;

import mzs.event.DataChangeEventData;
import mzs.event.DataChangeEventGameListData;
import mzs.event.i.DataChangeEventListener;

import client.data.game.Game;
import client.event.MenuEventData;
import client.event.MenuEventMPNewData;
import client.event.MenuEventType;
import client.event.i.MenuEventListener;

/**
 * @author Jakob Lahmer, Matthias Steinbšck
 *
 */
public class MPMenuPanel extends MenuPanel implements DataChangeEventListener {

	private static final long serialVersionUID = 1L;

	private JButton btNew;
	private JButton btJoin;
	private JScrollPane gameListScrollPanel;
	private JList lbGames;
	private JTextField tfNew;
	private JButton btView;
	private JButton btHighScore;
	
	private Logger log = LoggerFactory.getLogger(MPMenuPanel.class);
	
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
		gameListScrollPanel = new JScrollPane();
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
				menuEventListener.menuChanged(new MenuEventMPNewData(MenuEventType.MULTIPLAYER_NEW, tfNew.getText()));
			}
		});
		btJoin.setBounds(new Rectangle(255, 383, 150, 32));
		btJoin.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		btJoin.setText("Join");
		btJoin.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// check if game was selected
				if (lbGames.getSelectedIndex() > -1)	{
					//check if game can be joined
					menuEventListener.menuChanged(new MenuEventMPNewData(MenuEventType.MULTIPLAYER_JOIN, ((Game)lbGames.getSelectedValue()).getName()));
					/*
					if (!gameListManager.isGameJoinable(lbGames.getSelectedIndex()))
					{
						Messages.infoMessage(snakeMain,"Can't join this game, because it's full or already started.");
					}
					else
					{
						//set player state back to not init when necessary
						if (snakeMain.getMyPlayer().getPlayerState() != PlayerState.notinit)
						{
							snakeMain.getMyPlayer().setPlayerState(PlayerState.notinit);
							//myPlayer.saveToSpace();
						}
						//join game
						gameListManager.joinGame(lbGames.getSelectedIndex());
						//open new game menu
						snakeMain.openMPNewGameMenue();
					}
					*/
				} else	{
					if (lbGames.getMaxSelectionIndex() > 0)	{
						// @TODO show message
//						Messages.infoMessage(, "Please choose a game from the list.");
					}
					else
					{
//						Messages.infoMessage(snakeMain, "Please create a new game.");
					}
				}
			}
		});
		btJoin.setEnabled(false);
		gameListScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		gameListScrollPanel.setBounds(new Rectangle(280, 224, 240, 148));
		btExit.setBounds(new Rectangle(325, 463, 150, 32));
		btExit.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		btExit.setText("Back");
		btExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				menuEventListener.menuChanged(new MenuEventData(MenuEventType.START_MENU));
			}
		});
		tfNew.setBounds(new Rectangle(405, 179, 140, 30));
		btView.setBounds(new Rectangle(410, 383, 150, 32));
		btView.setText("Watch");
		btView.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		btView.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				menuEventListener.menuChanged(new MenuEventData(MenuEventType.MULTIPLAYER_WATCH));
			}
		});
		btHighScore.setBounds(new Rectangle(325, 423, 150, 32));
		btHighScore.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		btHighScore.setText("HighScore");
		btHighScore.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				menuEventListener.menuChanged(new MenuEventData(MenuEventType.MULTIPLAYER_HIGHSCORE));
			}
		});
		gameListScrollPanel.getViewport().add(lbGames);
		this.add(laTitle);
		this.add(btJoin);
		this.add(btNew);
		this.add(gameListScrollPanel);
		this.add(btHighScore);
		this.add(btExit);
		this.add(tfNew);
		this.add(btView);
	}
	
	/**
	 * fill the Listbox with the list of games. Select the same game after filling
	 * that was selected before if possible.
	 */
	private void fillGameList(Vector<Game> withGames)
	{
		int oldIndex = lbGames.getSelectedIndex();
		//log.debug("list currently has "+lbGames.getModel().getSize()+" elements");

		Object temp = null;
		if (oldIndex >= 0)	{
			try	{
				temp = lbGames.getSelectedValue();
			}
			catch (Exception ex)	{}
		}
		for (int i = 0; i < withGames.size(); i++)	{
			lbGames.setListData(withGames);
		}
		btJoin.setEnabled( (withGames.size() > 0));
		btView.setEnabled( (withGames.size() > 0));
		if (oldIndex >= 0 && oldIndex < withGames.size())
		{
			lbGames.setSelectedIndex(oldIndex);
			if (temp != null && !lbGames.getSelectedValue().equals(temp))
			{
				lbGames.setSelectedIndex( -1);
			}
		}
		//log.debug("now it has "+lbGames.getModel().getSize()+" elements");
		this.updateUI();
	}
	
	/* (non-Javadoc)
	 * @see client.data.event.i.DataChangeEventListener#dataChanged(client.data.event.DataChangeEventData)
	 */
	@Override
	public void dataChanged(DataChangeEventData changeEvent) {
		if(changeEvent instanceof DataChangeEventGameListData)	{
			this.fillGameList(((DataChangeEventGameListData) changeEvent).getGameListData());
		} else {
			Messages.errorMessage(this, "Error reading Game Data! "+changeEvent.getClass());
		}
	}
	
	
	@Override
	protected Dimension getBoxDimensions()	{
		return new Dimension(400, 400);
	}

	
}
