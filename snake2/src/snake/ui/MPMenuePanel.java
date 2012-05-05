package snake.ui;

import java.awt.*;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import snake.*;
import snake.data.*;
import snake.mzspaces.DataChangeEvent;
import snake.mzspaces.DataChangeListener;
import snake.util.Messages;

/**
 * Panel that displays the multiplayer menu. It contains a list of existing games, and
 * options to create a new game or join an existing game. The Panel listens to changes
 * of the game list and updates itself if changes occur.
 * @author Thomas Scheller, Markus Karolus
 */
public class MPMenuePanel extends JPanel implements DataChangeListener
{
	private static final long serialVersionUID = 1L;
	private Snake snakeMain = null;
	private GameListManager gameListManager = null;
	private LevelsManager levels = null;
	private Logger log = LoggerFactory.getLogger(MPMenuePanel.class);

	JLabel laTitel = new JLabel();
	JButton btNeu = new JButton();
	JButton btBeitreten = new JButton();
	JScrollPane jScrollPane1 = new JScrollPane();
	JList lbGames = new JList();
	JButton btZurueck = new JButton();
	JTextField tfNeu = new JTextField();
	JButton btView = new JButton();
	JButton btHighScore = new JButton();

	/**
	 * Default constructor, just to remove errors with JBuilder.
	 */
	public MPMenuePanel()
	{
		try
		{
			jbInit();
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}

	/**
	 * Create a new MPMenuePanel.
	 * @param snakeMain Snake main class
	 * @param gameListManager manager of the game list
	 * @param myPlayer the own player
	 * @param levels LevelsManager
	 */
	public MPMenuePanel(Snake snakeMain, GameListManager gameListManager, LevelsManager levels)
	{
		this.snakeMain = snakeMain;
		this.gameListManager = gameListManager;
		this.levels = levels;
		gameListManager.setDataChangeListener(this); //set itself as DataChangeListener of the game list

		try
		{
			jbInit();
			tfNeu.setText("New Game");
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}

	/**
	 * Overwritten from IDataChangeListener interface. Update form if Data has been changed.
	 * @param changeEvent DataChangeEvent
	 */
	public void dataChanged(DataChangeEvent changeEvent)
	{
		//log.debug("data has changed. we now have "+gameList.getListSize()+" games in the list");
		updateForm();
	}

	/**
	 * Standard method to initialize all gui components.
	 * @throws Exception
	 */
	private void jbInit() throws Exception
	{
		this.setLayout(null);
		this.setMinimumSize(new Dimension(0, 0));
		this.setOpaque(true);
		this.setPreferredSize(new Dimension(0, 0));
		this.setSize(new Dimension(800, 600));
		laTitel.setFont(new java.awt.Font("Dialog", Font.BOLD, 24));
		laTitel.setHorizontalAlignment(SwingConstants.CENTER);
		laTitel.setText("Multiplayer Menu");
		laTitel.setBounds(new Rectangle(300, 131, 200, 44));
		btNeu.setBounds(new Rectangle(255, 179, 140, 32));
		btNeu.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		btNeu.setToolTipText("");
		btNeu.setText("New Game");
		btNeu.addActionListener(new MPMenuePanel_btNeu_actionAdapter(this));
		btBeitreten.setBounds(new Rectangle(255, 383, 150, 32));
		btBeitreten.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		btBeitreten.setText("Join");
		btBeitreten.addActionListener(new MPMenuePanel_btBeitreten_actionAdapter(this));
		jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jScrollPane1.setBounds(new Rectangle(280, 224, 240, 148));
		btZurueck.setBounds(new Rectangle(325, 463, 150, 32));
		btZurueck.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		btZurueck.setText("Back");
		btZurueck.addActionListener(new MPMenuePanel_btZurueck_actionAdapter(this));
		tfNeu.setBounds(new Rectangle(405, 179, 140, 30));
		btView.setBounds(new Rectangle(410, 383, 150, 32));
		btView.setText("Watch");
		btView.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		btView.addActionListener(new MPMenuePanel_btView_actionAdapter(this));
		btHighScore.setBounds(new Rectangle(325, 423, 150, 32));
		btHighScore.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		btHighScore.setText("HighScore");
		btHighScore.addActionListener(new MPMenuePanel_btHighScore_actionAdapter(this));
		jScrollPane1.getViewport().add(lbGames);
		this.add(laTitel);
		this.add(btBeitreten);
		this.add(btNeu);
		this.add(jScrollPane1);
		this.add(btHighScore);
		this.add(btZurueck);
		this.add(tfNeu);
		this.add(btView);
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
		if (snakeMain.getBackGroundImage() != null)
		{
			g.drawImage(snakeMain.getBackGroundImage(), 0, 0, this);
		}
		//window
		snakeMain.drawWindow( (Graphics2D) g, 400, 400);
	}

	/**
	 * Fill the game list.
	 */
	public void updateForm()
	{
		fillGameList();
	}

	/**
	 * File the Listbox with the list of games. Select the same game after filling
	 * that was selected before if possible.
	 */
	private void fillGameList()
	{
		int oldIndex = lbGames.getSelectedIndex();
		//log.debug("list currently has "+lbGames.getModel().getSize()+" elements");

		Object temp = null;
		if (oldIndex >= 0)
		{
			try
			{
				temp = lbGames.getSelectedValue();
			}
			catch (Exception ex)
			{
			}
		}
		for (int i = 0; i < gameListManager.getListSize(); i++)
		{
			lbGames.setListData(gameListManager.getList().getVector());
		}
		btBeitreten.setEnabled( (gameListManager.getListSize() > 0));
		btView.setEnabled( (gameListManager.getListSize() > 0));
		if (oldIndex >= 0 && oldIndex < gameListManager.getListSize())
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

	/**
	 * Close the corso connection, open main menu, and remove the MPMenuePanel as
	 * DataChangeListener from the gamelist.
	 * @param e ActionEvent
	 */
	public void btZurueck_actionPerformed(ActionEvent e)
	{
//		@TODO datachangelistener
//		gameList.setDataChangeListener(null);
		snakeMain.closeCorsoConnection();
		snakeMain.openMainMenue();
	}

	/**
	 * Create a new game if the name does not exist, set the player state to not init
	 * and open the new game menu.
	 * @param e ActionEvent
	 */
	public void btNeu_actionPerformed(ActionEvent e)
	{
		gameListManager.setViewOnly(false, false);
		String gameName = tfNeu.getText();
		//check if name exists
		if (gameListManager.gameNameExists(gameName))
		{
			Messages.errorMessage(snakeMain,"A game with this name already exists,\nplease choose another name.");
			return;
		}

		//set player state back to not init when necessary
		if (snakeMain.getMyPlayer().getPlayerState() != PlayerState.notinit)
		{
			snakeMain.getMyPlayer().setPlayerState(PlayerState.notinit);
			//myPlayer.saveToSpace();
		}

		gameListManager.createGame(gameName);

		//open new game menu
		snakeMain.openMPNewGameMenue();
	}

	/**
	 * Join the selected game if it can be joined, set the player state to not init
	 * and open the new game menu.
	 * @param e ActionEvent
	 */
	public void btBeitreten_actionPerformed(ActionEvent e)
	{
		if (lbGames.getSelectedIndex() > -1)
		{
			gameListManager.setViewOnly(false, false);
			//check if game can be joined
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
		}
		else
		{
			if (gameListManager.getListSize() > 0)
			{
				Messages.infoMessage(snakeMain, "Please choose a game from the list.");
			}
			else
			{
				Messages.infoMessage(snakeMain, "Please create a new game.");
			}
		}
	}

	/**
	 * Join the selected game as spectator. If a game is joined that has not been started
	 * yet, the new game menu is opened and the game is automatically displayed when
	 * started. An already started game can be viewed immediately.
	 * @param e ActionEvent
	 */
	public void btView_actionPerformed(ActionEvent e)
	{
		int selectedIndex = lbGames.getSelectedIndex();
		if (selectedIndex > -1)
		{
			//set player state back to not init when necessary
			if (snakeMain.getMyPlayer().getPlayerState() != PlayerState.notinit)
			{
				snakeMain.getMyPlayer().setPlayerState(PlayerState.notinit);
				//myPlayer.saveToSpace();
			}

			GameState gameState = gameListManager.getList().getGameState(selectedIndex);
			//check if game can be joined
			if (gameState == GameState.aktiv || gameState == GameState.running || gameState == GameState.ended)
			{
				gameListManager.setViewOnly(true, false);
				gameListManager.joinGameViewOnly(selectedIndex);

				Game currentGame = gameListManager.getCurrentGame();

				if (!levels.levelExists(currentGame.getLevelDir()))
				{
					LevelData levelData = gameListManager.getCurrentGame().getLevelData();
					levelData.SaveData(levels);
				}

				//cancel if level does not exist
				if (!levels.levelExists(currentGame.getLevelDir()))
				{
					Messages.errorMessage(snakeMain,
																"Error occured: Level \"" + currentGame.getLevelDir() +
																"\" can't be found!");
					return;
				}
				else
				{
					//check if level is the same as level of leader
					levels.setCurrentLevel(currentGame.getLevelDir());
					byte[] levelHash = levels.getLevelHash();
					if (!java.util.Arrays.equals(levelHash, currentGame.getLevelCheckSum()))
					{
						LevelData levelData = gameListManager.getCurrentGame().getLevelData();
						levelData.SaveData(levels);
					}
				}

				//init all game sprites and start the game
				snakeMain.initMyGameSprites();
				snakeMain.initOtherGameSprites();
				gameListManager.checkCurrentGame();
				snakeMain.startMultiplayerGame();
			}
			else
			{
				//game not started yet
				gameListManager.setViewOnly(true, true);
				gameListManager.joinGameViewOnly(selectedIndex);
				//open new game menu
				snakeMain.openMPNewGameMenue();
			}
		}

	}

	/**
	 * Open the highscore.
	 * @param e ActionEvent
	 */
	public void btHighScore_actionPerformed(ActionEvent e)
	{
		snakeMain.openFHighScore();
	}
}

class MPMenuePanel_btHighScore_actionAdapter implements ActionListener
{
	private MPMenuePanel adaptee;
	MPMenuePanel_btHighScore_actionAdapter(MPMenuePanel adaptee)
	{
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e)
	{
		adaptee.btHighScore_actionPerformed(e);
	}
}

class MPMenuePanel_btView_actionAdapter implements ActionListener
{
	private MPMenuePanel adaptee;
	MPMenuePanel_btView_actionAdapter(MPMenuePanel adaptee)
	{
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e)
	{
		adaptee.btView_actionPerformed(e);
	}
}

class MPMenuePanel_btNeu_actionAdapter implements ActionListener
{
	private MPMenuePanel adaptee;
	MPMenuePanel_btNeu_actionAdapter(MPMenuePanel adaptee)
	{
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e)
	{
		adaptee.btNeu_actionPerformed(e);
	}
}

class MPMenuePanel_btBeitreten_actionAdapter implements ActionListener
{
	private MPMenuePanel adaptee;
	MPMenuePanel_btBeitreten_actionAdapter(MPMenuePanel adaptee)
	{
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e)
	{
		adaptee.btBeitreten_actionPerformed(e);
	}
}

class MPMenuePanel_btZurueck_actionAdapter implements ActionListener
{
	private MPMenuePanel adaptee;
	MPMenuePanel_btZurueck_actionAdapter(MPMenuePanel adaptee)
	{
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e)
	{
		adaptee.btZurueck_actionPerformed(e);
	}
}
