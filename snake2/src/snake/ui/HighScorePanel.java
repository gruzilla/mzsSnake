package snake.ui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import snake.data.*;
import snake.Snake;
import snake.GameListManager;

/**
 * Panel that displays the highscore
 * @author Thomas Scheller, Markus Karolus
 */
public class HighScorePanel extends JPanel
{
	private static final long serialVersionUID = 1L;
  private Snake snakeMain = null;
  //private GameListManager gameList = null;
  private HighScore highScore = null;

  JLabel laTitel = new JLabel();
  JButton btZurueck = new JButton();
  JTextField tfPlayer3 = new JTextField();
  JTextField tfPlayer4 = new JTextField();
  JTextField tfPlayer1 = new JTextField();
  JLabel laSpieler3 = new JLabel();
  JLabel laSpieler2 = new JLabel();
  JLabel laSpieler5 = new JLabel();
  JLabel laSpieler1 = new JLabel();
  JLabel laSpieler = new JLabel();
  JTextField tfPlayer5 = new JTextField();
  JLabel laSpieler4 = new JLabel();
  JLabel laSpieler8 = new JLabel();
  JLabel laSpieler6 = new JLabel();
  JLabel laSpieler7 = new JLabel();
  JTextField tfPlayer6 = new JTextField();
  JTextField tfPlayer8 = new JTextField();
  JTextField tfPlayer7 = new JTextField();
  JTextField tfPlayer2 = new JTextField();

  /**
   * Default constructor, just to remove errors with JBuilder.
   */
  public HighScorePanel()
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
   * Create a new HighScorePanel, and get the highscore from corsospace.
   * @param snakeMain Snake main class
   * @param gameList manager for the list of games
   */
  public HighScorePanel(Snake snakeMain, GameListManager gameList)
  {
    this.snakeMain = snakeMain;
    //this.gameList = gameList;
    highScore = gameList.getHighScore();

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
    laTitel.setText("HighScore");
    laTitel.setBounds(new Rectangle(180, 103, 407, 44));
    btZurueck.setBounds(new Rectangle(354, 449, 150, 32));
    btZurueck.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    btZurueck.setText("Back");
    btZurueck.addActionListener(new FHighScore_btZurueck_actionAdapter(this));

    tfPlayer3.setEditable(false);
    tfPlayer3.setText("");
    tfPlayer3.setBounds(new Rectangle(342, 244, 180, 30));
    tfPlayer4.setEditable(false);
    tfPlayer4.setText("");
    tfPlayer4.setBounds(new Rectangle(342, 276, 180, 30));
    tfPlayer1.setEditable(false);
    tfPlayer1.setText("");
    tfPlayer1.setBounds(new Rectangle(342, 180, 180, 30));
    laSpieler3.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    laSpieler3.setText("3.");
    laSpieler3.setBounds(new Rectangle(301, 246, 25, 28));
    laSpieler2.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    laSpieler2.setText("2.");
    laSpieler2.setBounds(new Rectangle(301, 214, 25, 28));
    laSpieler5.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    laSpieler5.setText("5.");
    laSpieler5.setBounds(new Rectangle(301, 310, 25, 28));
    laSpieler1.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    laSpieler1.setText("1.");
    laSpieler1.setBounds(new Rectangle(301, 182, 25, 28));
    laSpieler.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    laSpieler.setText("Player:");
    laSpieler.setBounds(new Rectangle(234, 154, 140, 28));
    tfPlayer5.setEditable(false);
    tfPlayer5.setBounds(new Rectangle(342, 308, 180, 30));
    laSpieler4.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    laSpieler4.setText("4.");
    laSpieler4.setBounds(new Rectangle(301, 278, 25, 28));
    laSpieler8.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    laSpieler8.setText("8.");
    laSpieler8.setBounds(new Rectangle(301, 406, 25, 28));
    laSpieler6.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    laSpieler6.setText("6.");
    laSpieler6.setBounds(new Rectangle(301, 342, 25, 28));
    laSpieler7.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    laSpieler7.setText("7.");
    laSpieler7.setBounds(new Rectangle(301, 374, 25, 28));
    tfPlayer6.setEditable(false);
    tfPlayer6.setText("");
    tfPlayer6.setBounds(new Rectangle(342, 340, 180, 30));
    tfPlayer8.setEditable(false);
    tfPlayer8.setBounds(new Rectangle(342, 404, 180, 30));
    tfPlayer7.setEditable(false);
    tfPlayer7.setBounds(new Rectangle(342, 372, 180, 30));
    tfPlayer2.setEditable(false);
    tfPlayer2.setBounds(new Rectangle(342, 212, 180, 30));
    this.add(laTitel);
    this.add(laSpieler);
    this.add(tfPlayer1);
    this.add(laSpieler1);
    this.add(laSpieler2);
    this.add(tfPlayer4);
    this.add(tfPlayer3);
    this.add(tfPlayer5);
    this.add(tfPlayer6);
    this.add(tfPlayer7);
    this.add(tfPlayer8);
    this.add(laSpieler3);
    this.add(laSpieler4);
    this.add(laSpieler5);
    this.add(laSpieler6);
    this.add(laSpieler7);
    this.add(laSpieler8);
    this.add(tfPlayer2);
    this.add(btZurueck);
  }

  /**
   * Fill the form with the highscores read from corsospace.
   */
  public void updateForm()
  {
    if (highScore.size() > 8)
    {
      Vector<HighScoreData> vhighScore = highScore.getVector();
      showHighScore(tfPlayer1, (HighScoreData) vhighScore.elementAt(0));
      showHighScore(tfPlayer2, (HighScoreData) vhighScore.elementAt(1));
      showHighScore(tfPlayer3, (HighScoreData) vhighScore.elementAt(2));
      showHighScore(tfPlayer4, (HighScoreData) vhighScore.elementAt(3));
      showHighScore(tfPlayer5, (HighScoreData) vhighScore.elementAt(4));
      showHighScore(tfPlayer6, (HighScoreData) vhighScore.elementAt(5));
      showHighScore(tfPlayer7, (HighScoreData) vhighScore.elementAt(6));
      showHighScore(tfPlayer8, (HighScoreData) vhighScore.elementAt(7));
    }
    this.updateUI();
  }

  /**
   * Display the highscore in the textfield.
   * @param text textfield for the highscore
   * @param data highscore to display
   */
  private void showHighScore(JTextField text, HighScoreData data)
  {
    if (data.getName().length() > 0)
    {
      text.setText(data.getName() + " / " + data.getPoints());
    }
    else
    {
      text.setText("---");
    }
  }

  /**
   *
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
   * Open multiplayer menu
   * @param e ActionEvent
   */
  public void btZurueck_actionPerformed(ActionEvent e)
  {
    snakeMain.openMPMenue();
  }
}

class FHighScore_btZurueck_actionAdapter implements ActionListener
{
  private HighScorePanel adaptee;
  FHighScore_btZurueck_actionAdapter(HighScorePanel adaptee)
  {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e)
  {
    adaptee.btZurueck_actionPerformed(e);
  }
}
