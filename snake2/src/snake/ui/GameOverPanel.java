package snake.ui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;

import snake.Snake;
import snake.GameListManager;
import snake.data.*;

/**
 * Panel that displays game points and time after a game is over, and calculates the
 * highscore.
 * @author Thomas Scheller, Markus Karolus
 */
public class GameOverPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private Snake snakeMain = null;
	private GameListManager gameList = null;
	private Player myPlayer = null;
	private int playTime = 0;
	private boolean multiplayer = false;

	JLabel laTitel = new JLabel();
	JButton btZurueck = new JButton();
	JTextField tfPlayer1 = new JTextField();
	JLabel laPlayer1 = new JLabel();
	JLabel laPlayer4 = new JLabel();
	JLabel laPlayer3 = new JLabel();
	JTextField tfPlayer2 = new JTextField();
	JLabel laPlayer2 = new JLabel();
	JTextField tfPlayer4 = new JTextField();
	JTextField tfPlayer3 = new JTextField();
	JTextField tfPlayer2Points = new JTextField();
	JTextField tfPlayer3Points = new JTextField();
	JTextField tfPlayer4Points = new JTextField();
	JTextField tfPlayer1Points = new JTextField();
	JLabel laPlayTime = new JLabel();
	JLabel laPunkte = new JLabel();
	JLabel laHighscore = new JLabel();
	JLabel laName = new JLabel();
	JTextField tfHighScorePoints2 = new JTextField();
	JTextField tfHighScorePoints3 = new JTextField();
	JTextField tfHighScorePoints4 = new JTextField();
	JTextField tfHighScorePoints1 = new JTextField();
	JButton btHighScore = new JButton();

	/**
	 * Default constructor, just to remove errors with JBuilder.
	 */
	public GameOverPanel()
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
	 * Create a new GameOverPanel, showing Information depending on the provided data.
	 * @param snakeMain snake main class
	 * @param gameList manager for the list of games
	 * @param myPlayer the own player
	 * @param playTime playtime of the ended game
	 * @param multiplayer true if the game was in multiplayer mode
	 */
	public GameOverPanel(Snake snakeMain, GameListManager gameList, Player myPlayer, int playTime,
			boolean multiplayer)
	{
		this.snakeMain = snakeMain;
		this.gameList = gameList;
		this.myPlayer = myPlayer;
		this.playTime = playTime;
		this.multiplayer = multiplayer;
		try
		{
			jbInit();
			fill();
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
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
		laTitel.setText("Game Over");
		laTitel.setBounds(new Rectangle(300, 131, 200, 44));
		btZurueck.setBounds(new Rectangle(330, 412, 150, 32));
		btZurueck.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		btZurueck.setText("Back");
		btZurueck.addActionListener(new GameOverPanel_btZurueck_actionAdapter(this));
		tfPlayer1.setEditable(false);
		tfPlayer1.setBounds(new Rectangle(265, 234, 140, 30));
		laPlayer1.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		laPlayer1.setText("1.");
		laPlayer1.setBounds(new Rectangle(247, 236, 25, 28));
		laPlayer4.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		laPlayer4.setText("4.");
		laPlayer4.setBounds(new Rectangle(248, 335, 25, 28));
		laPlayer3.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		laPlayer3.setText("3.");
		laPlayer3.setBounds(new Rectangle(248, 302, 25, 28));
		tfPlayer2.setEditable(false);
		tfPlayer2.setText("");
		tfPlayer2.setBounds(new Rectangle(265, 267, 140, 30));
		laPlayer2.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		laPlayer2.setText("2.");
		laPlayer2.setBounds(new Rectangle(248, 269, 25, 28));
		tfPlayer4.setEditable(false);
		tfPlayer4.setBounds(new Rectangle(265, 333, 140, 30));
		tfPlayer3.setEditable(false);
		tfPlayer3.setBounds(new Rectangle(265, 300, 140, 30));
		tfPlayer2Points.setEditable(false);
		tfPlayer2Points.setText("");
		tfPlayer2Points.setHorizontalAlignment(SwingConstants.RIGHT);
		tfPlayer2Points.setBounds(new Rectangle(408, 267, 73, 30));
		tfPlayer3Points.setEditable(false);
		tfPlayer3Points.setHorizontalAlignment(SwingConstants.RIGHT);
		tfPlayer3Points.setBounds(new Rectangle(408, 300, 73, 30));
		tfPlayer4Points.setEditable(false);
		tfPlayer4Points.setHorizontalAlignment(SwingConstants.RIGHT);
		tfPlayer4Points.setBounds(new Rectangle(408, 333, 73, 30));
		tfPlayer1Points.setEditable(false);
		tfPlayer1Points.setHorizontalAlignment(SwingConstants.RIGHT);
		tfPlayer1Points.setBounds(new Rectangle(408, 234, 73, 30));
		laPlayTime.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		laPlayTime.setHorizontalAlignment(SwingConstants.CENTER);
		laPlayTime.setText("Playtime...");
		laPlayTime.setBounds(new Rectangle(300, 176, 215, 28));
		laPunkte.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		laPunkte.setText("Points");
		laPunkte.setBounds(new Rectangle(410, 203, 71, 28));
		laHighscore.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		laHighscore.setText("Highscore");
		laHighscore.setBounds(new Rectangle(485, 203, 85, 28));
		laName.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		laName.setText("Name");
		laName.setBounds(new Rectangle(265, 203, 121, 28));
		tfHighScorePoints2.setEditable(false);
		tfHighScorePoints2.setText("");
		tfHighScorePoints2.setHorizontalAlignment(SwingConstants.RIGHT);
		tfHighScorePoints2.setBounds(new Rectangle(485, 267, 73, 30));
		tfHighScorePoints3.setEditable(false);
		tfHighScorePoints3.setText("");
		tfHighScorePoints3.setHorizontalAlignment(SwingConstants.RIGHT);
		tfHighScorePoints3.setBounds(new Rectangle(485, 300, 73, 30));
		tfHighScorePoints4.setEditable(false);
		tfHighScorePoints4.setText("");
		tfHighScorePoints4.setHorizontalAlignment(SwingConstants.RIGHT);
		tfHighScorePoints4.setBounds(new Rectangle(485, 333, 73, 30));
		tfHighScorePoints1.setEditable(false);
		tfHighScorePoints1.setText("");
		tfHighScorePoints1.setHorizontalAlignment(SwingConstants.RIGHT);
		tfHighScorePoints1.setBounds(new Rectangle(485, 234, 73, 30));
		btHighScore.setBounds(new Rectangle(329, 375, 150, 32));
		btHighScore.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
		btHighScore.setText("Highscore");
		btHighScore.addActionListener(new GameOverPanel_btHighScore_actionAdapter(this));
		this.add(laTitel);
		this.add(laPlayTime);
		this.add(laPlayer3);
		this.add(tfPlayer3);
		this.add(tfPlayer3Points);
		this.add(laPlayer4);
		this.add(tfPlayer4);
		this.add(tfPlayer4Points);
		this.add(laName);
		this.add(laPunkte);
		this.add(tfHighScorePoints1);
		this.add(tfHighScorePoints3);
		this.add(tfHighScorePoints2);
		this.add(tfHighScorePoints4);
		this.add(laHighscore);
		this.add(btHighScore);
		this.add(btZurueck);
		this.add(tfPlayer1);
		this.add(laPlayer1);
		this.add(tfPlayer1Points);
		this.add(laPlayer2);
		this.add(tfPlayer2);
		this.add(tfPlayer2Points);
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
	 * Fill the form with the points of all players and the playtime. Also, the highscore
	 * is calculated, and saved to space if the own player is the leader of the game. In
	 * singleplayer mode just the playtime is shown.
	 */
	private void fill()
	{
		//create arrays to make filling easier
		JLabel[] labels =
		{laPlayer1, laPlayer2, laPlayer3, laPlayer4};
		JTextField[] nameFields =
		{tfPlayer1, tfPlayer2, tfPlayer3, tfPlayer4};
		JTextField[] pointFields =
		{tfPlayer1Points, tfPlayer2Points, tfPlayer3Points, tfPlayer4Points};
		JTextField[] highScoreFiels =
		{tfHighScorePoints1, tfHighScorePoints2, tfHighScorePoints3, tfHighScorePoints4};

		//show playtime
		laPlayTime.setText("Playtime: " + playTime + " sec");

		int i = 0;
		if (multiplayer)
		{
			//if multiplayer mode, show points of all players
			int maxPoints = 0;
			UUID winnerNr = null;
			float highScoreData = (float) playTime / 60.0f;
			float highScorePoints = 0.0f;
			HighScore highScore = gameList.getHighScore();
			for (i = 0; i < gameList.getCurrentGame().getPlayerAnz(); i++)
			{
				Player player = gameList.getCurrentGame().getPlayer(i);
				nameFields[i].setText(player.getName());
				pointFields[i].setText(String.valueOf(player.getPoints()));
				highScorePoints = (float) player.getPoints() / highScoreData;
				if (highScorePoints > 0)
				{
					if (highScore.addHighScoreUser(player.getName(), highScorePoints))
					{
						highScoreFiels[i].setText(String.valueOf(highScorePoints));
					}
					else
					{
						highScoreFiels[i].setText("(" + String.valueOf(highScorePoints) + ")");
					}
				}
				else
				{
					highScoreFiels[i].setText("---");
				}

				if (maxPoints < player.getPoints())
				{
					maxPoints = player.getPoints();
					winnerNr = player.getNr();
				}
			}
			if (winnerNr.equals(myPlayer.getNr()))
			{
				//special text if player is winner
				laTitel.setText("WINNER");
			}
			if (gameList.getCurrentGame().getLeader().equals(myPlayer))
			{
				//leader writes the highscore list to corsospace
				gameList.writeHighScore(highScore);
			}

			if (gameList.isViewOnly())
			{
				laHighscore.setVisible(false);
				laPlayTime.setVisible(false);
				tfHighScorePoints1.setVisible(false);
				tfHighScorePoints2.setVisible(false);
				tfHighScorePoints3.setVisible(false);
				tfHighScorePoints4.setVisible(false);
				laTitel.setText("Game over");
			}
		}
		else
		{
			laHighscore.setVisible(false);
			btHighScore.setVisible(false);
			btZurueck.setBounds(btHighScore.getBounds());
			laName.setVisible(false);
			laPunkte.setVisible(false);
		}

		//make unused fields invisible
		for (; i < 4; i++)
		{
			labels[i].setVisible(false);
			nameFields[i].setVisible(false);
			pointFields[i].setVisible(false);
			highScoreFiels[i].setVisible(false);
		}
	}

	/**
	 * Leave the game and go back to main menu, or to multiplayer menu if game was a
	 * multiplayer game.
	 * @param e ActionEvent
	 */
	public void btZurueck_actionPerformed(ActionEvent e)
	{
		if (multiplayer)
		{
			gameList.leaveGame();
			snakeMain.openMPMenue();
		}
		else
		{
			snakeMain.openMainMenue();
		}
	}

	/**
	 * Leave the game and open highscore.
	 * @param e ActionEvent
	 */
	public void btHighScore_actionPerformed(ActionEvent e)
	{
		if (multiplayer)
		{
			gameList.leaveGame();
			snakeMain.openFHighScore();
		}
		else
		{
			snakeMain.openFHighScore();
		}
	}
}

class GameOverPanel_btHighScore_actionAdapter implements ActionListener
{
	private GameOverPanel adaptee;
	GameOverPanel_btHighScore_actionAdapter(GameOverPanel adaptee)
	{
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e)
	{
		adaptee.btHighScore_actionPerformed(e);
	}
}

class GameOverPanel_btZurueck_actionAdapter implements ActionListener
{
	private GameOverPanel adaptee;
	GameOverPanel_btZurueck_actionAdapter(GameOverPanel adaptee)
	{
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e)
	{
		adaptee.btZurueck_actionPerformed(e);
	}
}
