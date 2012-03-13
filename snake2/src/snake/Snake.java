package snake;

/**
 * The main class to start snake, also the main frame that holds the game and the menu.
 * @author Thomas Scheller, Markus Karolus
 * @version 1.0
 */

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import snake.data.*;
import snake.mzspaces.Util;
import snake.ui.*;
import snake.util.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Snake extends JFrame implements WindowListener
{
	private static final long serialVersionUID = 1L;
	public static int DEFAULT_FPS = 20;
	public static final int PWIDTH = 800; // size of panel
	public static final int PHEIGHT = 600;

	private int maxPanelWidth = 0;
	private int maxPanelHeight = 0;

	private SnakePanel snakePanel = null; // where the game is drawn
	private MenuePanel menuePanel = null;
	private MPMenuePanel mpMenuePanel = null;
	private MPNewGamePanel mpNewGamePanel = null;
	private GameOverPanel gameOverPanel = null;
	private HighScorePanel highScorePanel = null;

	private long period = 0;
	private GameListManager gameList = null;
	private Player myPlayer = null;
	private LevelsManager levelManager = null;
	private BufferedImage bgImage = null;
	private boolean multiplayer = false;
	private boolean exitAllowed = true; //is exit allowed with the X button of the frame
	private Integer multiStarted = 0; //object to synchronize game start and to prevent multiple starts

	public static SnakeLog snakeLog = null;
	/**
	 * Main method to start the program.
	 * @param args no arguments need to be given
	 */
	public static void main(String[] args)
	{
		long period = (long) 1000.0 / DEFAULT_FPS;
		// System.out.println("fps: " + DEFAULT_FPS + "; period: " + period + " ms");
		new Snake(period * 1000000L); // ms --> nanosecs
	}

	/**
	 * Main constructor of the class, loads settings from property file and opens the main menu.
	 * @param period the duration for one frame in ms
	 */
	public Snake(long period)
	{
		super("Snake v2.0");
		this.period = period;
		snakeLog = new SnakeLog();

		// load the background image
		levelManager = new LevelsManager();
		ImageLoader imgLoader = new ImageLoader();
		bgImage = imgLoader.loadImage(levelManager.getBackPicturePath(), false);

		//ContentPane with custom drawn border
		this.setContentPane(new BorderContentPanel());

		//open menue
		openMainMenue();

		//Frame settings (position, max. size, ...)
		this.addWindowListener(this);
		this.pack();
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		this.setLocation(env.getCenterPoint().x - this.getWidth() / 2,
				env.getCenterPoint().y - this.getHeight() / 2);
		this.setMaximizedBounds(env.getMaximumWindowBounds());
		//this.setExtendedState(this.getExtendedState() | this.MAXIMIZED_BOTH);
		this.addComponentListener(new Snake_this_componentAdapter(this));

		getContentPane().setLayout(null);
		getContentPane().setBackground(Color.BLACK);

		//calculate max panel size
		this.setVisible(true);
		calcMaxPanelSize();

		//create player, load game list
		myPlayer = new Player(Util.getInstance().getSettings().getPlayerName(), Util.getInstance().getSettings().getSnakeSkin());
		gameList = new GameListManager(this, myPlayer, levelManager);

		//show frame
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}

	/**
	 * Calculate maximum panel width and height in the frame.
	 */
	private void calcMaxPanelSize()
	{
		maxPanelWidth = this.getWidth() - (this.getInsets().left + this.getInsets().right);
		maxPanelHeight = this.getHeight() - (this.getInsets().bottom + this.getInsets().top);
	}

	public BufferedImage getBackGroundImage()
	{
		return bgImage;
	}

	/**
	 * Helper method to draw a background window. The window positions itself
	 * in the center and is half-transparent.
	 * @param g2d Graphics2D object where the Window should be drawn
	 * @param width the width of the window to draw
	 * @param height the height of the window to draw
	 */
	public void drawWindow(Graphics2D g2d, int width, int height)
	{
		//helper method to draw a background window
		int ypos = (Snake.PHEIGHT - height) / 2;
		int xpos = (Snake.PWIDTH - width) / 2;
		Composite c = g2d.getComposite(); // backup the old composite
		g2d.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 0.5f));
		g2d.setColor(Color.WHITE);
		g2d.fillRect(xpos, ypos, width, height);
		g2d.setColor(Color.darkGray);
		g2d.drawRect(xpos, ypos, width, height);
		g2d.setComposite(c); // restore the old composite so it doesn't mess up future rendering
	}

	/**
	 * Start the game in singleplayer mode.
	 */
	public void startSingleplayerGame()
	{
		//start singleplayer game
		getContentPane().removeAll();
		// TODO: get settings inside panel
		snakePanel = new SnakePanel(this, period, Util.getInstance().getSettings(), myPlayer,gameList, levelManager);
		snakePanel.setGameBounds(maxPanelWidth, maxPanelHeight);
		getContentPane().add(snakePanel);
		snakePanel.startSingleplayerGame();
		this.getContentPane().doLayout();
		snakePanel.requestFocus();
		multiplayer = false;
		exitAllowed = false;
	}

	/**
	 * The settings are updated with the new settings and saved to the property file.
	 * @param settings new settings
	 */
	public void updateSettings(Settings settings)
	{
		settings.save();
		myPlayer.setName(settings.getPlayerName());
		myPlayer.setSkin(settings.getSnakeSkin());
	}

	/**
	 * Start the game in multiplayer mode.
	 */
	public void startMultiplayerGame()
	{
		synchronized (multiStarted)
		{
			if (multiStarted == 0) //prevent from multiple game starts
			{
				multiStarted = 1;
				System.out.println("MultiplayerGame - Starting");
				getContentPane().removeAll();
				snakePanel.setGameBounds(maxPanelWidth, maxPanelHeight);
				getContentPane().add(snakePanel);

				snakePanel.startGame();
				this.getContentPane().doLayout();
				snakePanel.requestFocus();
				multiplayer = true;
				exitAllowed = false;
			}
			else
			{
				//System.out.println("Game already started.");
			}
		}
	}

	/**
	 * Create a new SnakePanel (the panel that shows the running game), initialize
	 * all game sprites the belong to the player, and set the playerstate to initialized.
	 * (only used when starting a multiplayer game)
	 */
	public void initMyGameSprites()
	{
		// System.out.println("Initializing MyGameSprites.");
		if (snakePanel == null)
		{
			// TODO: update panel to get settings itself
			snakePanel = new SnakePanel(this, period, Util.getInstance().getSettings(), myPlayer, gameList, levelManager);
		}
		snakePanel.initMyGameSprites();
		multiStarted = 0;
	}

	/**
	 * Initialize all other game sprites (the snake from other players) by loading them
	 * from the space. (only used when starting a multiplayer game)
	 */
	public void initOtherGameSprites()
	{
		if (snakePanel == null && gameList.isViewOnly())
		{
			//load some variables now when in viewing mode
			myPlayer.setPlayerState(snake.data.PlayerState.init);
			initMyGameSprites();
		}
		snakePanel.initOtherGameSprites();
	}

	/**
	 * Try to open a connection to the corso space.
	 * @return true if the connection could be opened
	 */
	public boolean openSpaceConnection()
	{
		//open corso connection
		try
		{
			snakeLog.flush();
			snakeLog.writeLogEntry("Successfully connected to 2. corso site");
			gameList.initialize(); //load game list
			return true;
		}
		catch (Exception ex)
		{
			snakeLog.writeLogEntry("Can't connect to XVSM");
			System.out.println("XVSM Error occured:");
			ex.printStackTrace();
			Messages.errorMessage(this, "Can't connect to XVSM Server.");
			return false;
		}
	}

	/**
	 * Close the corso connection.
	 */
	public void closeCorsoConnection()
	{
		//close corso connection
		try
		{
			gameList.leaveGame(); //leave game if running
			System.out.println("Corso connection closed.");
		}
		catch (Exception ex)
		{
			System.out.println("Corso Error occured");
		}
	}

	/**
	 * Open and display the main menu.
	 */
	public void openMainMenue()
	{
		getContentPane().removeAll();
		menuePanel = new MenuePanel(this);
		menuePanel.setBounds( (maxPanelWidth - PWIDTH) / 2, (maxPanelHeight - PHEIGHT) / 2, PWIDTH, PHEIGHT);
		getContentPane().add(menuePanel);
		this.getContentPane().doLayout();
		this.getContentPane().repaint();
		exitAllowed = true;
	}

	/**
	 * Open and display the multiplayer menu.
	 */
	public void openMPMenue()
	{
		getContentPane().removeAll();
		mpMenuePanel = new MPMenuePanel(this, gameList, myPlayer, levelManager);
		mpMenuePanel.setBounds( (maxPanelWidth - PWIDTH) / 2, (maxPanelHeight - PHEIGHT) / 2, PWIDTH, PHEIGHT);
		getContentPane().add(mpMenuePanel);
		mpMenuePanel.updateForm();
		this.getContentPane().doLayout();
		this.getContentPane().repaint();
		exitAllowed = true;
	}

	/**
	 * Open and display the new game menu.
	 */
	public void openMPNewGameMenue()
	{
		getContentPane().removeAll();
		mpNewGamePanel = new MPNewGamePanel(this, gameList, myPlayer, levelManager);
		mpNewGamePanel.setBounds( (maxPanelWidth - PWIDTH) / 2, (maxPanelHeight - PHEIGHT) / 2, PWIDTH, PHEIGHT);
		getContentPane().add(mpNewGamePanel);
		mpNewGamePanel.updateForm();
		this.getContentPane().doLayout();
		this.getContentPane().repaint();
		exitAllowed = false;
	}

	/**
	 * Open and display the highscore.
	 */
	public void openFHighScore()
	{
		getContentPane().removeAll();
		highScorePanel = new HighScorePanel(this, gameList);
		highScorePanel.setBounds( (maxPanelWidth - PWIDTH) / 2, (maxPanelHeight - PHEIGHT) / 2, PWIDTH, PHEIGHT);
		getContentPane().add(highScorePanel);
		highScorePanel.updateForm();
		this.getContentPane().doLayout();
		this.getContentPane().repaint();
		exitAllowed = false;
	}

	/**
	 * Open and display the game over panel.
	 */
	public void gameOver(int playTime)
	{
		System.out.println("GAME OVER");
		getContentPane().removeAll();
		gameOverPanel = new GameOverPanel(this, gameList, myPlayer, playTime, multiplayer);
		gameOverPanel.setBounds( (maxPanelWidth - PWIDTH) / 2, (maxPanelHeight - PHEIGHT) / 2, PWIDTH, PHEIGHT);
		getContentPane().add(gameOverPanel);
		this.getContentPane().doLayout();
		this.getContentPane().repaint();
		exitAllowed = true;
		multiStarted = 0;
	}

	// ----------------- window listener methods -------------

	public void windowActivated(WindowEvent e)
	{
		//snakePanel.resumeGame();
	}

	public void windowDeactivated(WindowEvent e)
	{
		//DEAKTIVIERT
		//snakePanel.pauseGame();
	}

	public void windowDeiconified(WindowEvent e)
	{
		//snakePanel.resumeGame();
	}

	public void windowIconified(WindowEvent e)
	{
		//snakePanel.pauseGame();
	}

	public void windowClosing(WindowEvent e)
	{
		//snakePanel.stopGame();
		//midisLoader.close(); // not really required
		if (exitAllowed)
		{
			snakeLog.close();
			System.exit(0);
		}
	}

	public void windowClosed(WindowEvent e)
	{}

	public void windowOpened(WindowEvent e)
	{}

	public void this_componentResized(ComponentEvent e)
	{
		//window size changed: recalculate position/size of the panel
		System.out.println("Frame Resized.");

		calcMaxPanelSize();
		JPanel panel = (JPanel) getContentPane().getComponent(0);
		if (panel == snakePanel)
		{
			snakePanel.setGameBounds(maxPanelWidth, maxPanelHeight);
		}
		else
		{
			panel.setBounds( (maxPanelWidth - PWIDTH) / 2, (maxPanelHeight - PHEIGHT) / 2, PWIDTH, PHEIGHT);
		}
	}
}

class Snake_this_componentAdapter extends ComponentAdapter
{
	private Snake adaptee;
	Snake_this_componentAdapter(Snake adaptee)
	{
		this.adaptee = adaptee;
	}

	public void componentResized(ComponentEvent e)
	{
		adaptee.this_componentResized(e);
	}
}
