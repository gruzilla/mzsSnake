package snake;

import javax.swing.*;

import org.mozartspaces.core.Capi;

import java.awt.image.*;
import java.awt.event.*;
import java.awt.*;
import snake.corso.Util;
import snake.util.*;
import snake.data.*;

/**
 * SnakePanel draws and updates the game, handling all game sprites, the level background and
 * message outputs while the game is running.
 * An animation thread is responsible for updating an rendering the game with a constant frame
 * rate, therefor active rendering is used. It also handles key presses that are important for
 * the game and reports them to the game sprites.
 * @author Thomas Scheller, Markus Karolus
 */

public class SnakePanel extends JPanel implements Runnable
{
	private int pWidth = 800; // size of panel
	private int pHeight = 600;

	private static final int NO_DELAYS_PER_YIELD = 16;
	/* Number of frames with a delay of 0 ms before the animation thread yields
     to other running threads. */

	private static int MAX_FRAME_SKIPS = 5;
	// no. of frames that can be skipped in any one animation loop
	// i.e the games state is updated but not rendered

	private Thread animator; // the thread that performs the animation

	//game modes
	private static final int MODE_STARTING = 1;
	private static final int MODE_RUNNING = 2;
	private static final int MODE_PAUSE = 3;
	private static final int MODE_QUIT = 4;
	private static final int MODE_FINISH = 5;
	private static final int MODE_CLOSE = 6;
	//private static final int MODE_GAMEOVER = 7;

	private int working_mode = MODE_STARTING;
	private int modeQuit = MODE_STARTING;

	private boolean multiplayer = true;

	private long period; // period between drawing in nanosecs

	private Snake snakeMain;
	private Settings settings = null;
	private GameListManager gameList = null;
	private Player myPlayer = null;
	private LevelsManager levelManager = null;
	private ImageLoader imgLoader;

	// the sprites
	private SnakeSprite snakeSprite;
	private SnakeSprite[] otherSnakeSprites; //other players
	private CollectableSprite collectables;

	private long gameStartTime; // when the game started
	private int timeSpentInGame;

	// for displaying messages
	private Font msgsFont;
	private FontMetrics metrics;

	// off-screen rendering
	private Graphics dbg;
	private Image dbImage = null;

	private BufferedImage bgImage = null;
	private BackgroundManager gameMap = null;
	private Point correctionPos = new Point(0, 0);
	private boolean panelResized = true;
	private int viewOnlySnakeIX = 0;

	private String lastBackGroundImagePath = null;

	/**
	 * Create a new SnakePanel. Create a BackgroundManager that calculates obstacles and
	 * level measurements, add a key listener to catch key events and request the focus for
	 * the panel, load the background and set up the fonts for messages.
	 * @param snakeMain Snake main class
	 * @param period duration of one frame in nanosecs
	 * @param settings settings of the game
	 * @param gameList manager for the list of games
	 * @param myPlayer the own player
	 * @param aLevelManager LevelsManager with the current level
	 */
	public SnakePanel(Snake snakeMain, long period, Settings settings, GameListManager gameList,
			Player myPlayer, LevelsManager aLevelManager)
	{
		this.snakeMain = snakeMain;
		this.period = period;
		this.settings = settings;
		this.gameList = gameList;
		this.myPlayer = myPlayer;
		this.levelManager = aLevelManager;

		setDoubleBuffered(false);
		setBackground(Color.white);
		setPreferredSize(new Dimension(pWidth, pHeight));

		//create backgroundmanager
		gameMap = new BackgroundManager(pWidth, pHeight, levelManager.getBackDefinitionPath(),
				levelManager.getStartPosPropsPath());

		setFocusable(true);
		requestFocus(); // the JPanel now has focus, so receives key events

		//add a key listener to the panel to process key events
		addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				processKey(e);
			}

			public void keyReleased(KeyEvent e)
			{
				processKeyReleased(e);
			}
		});

		// load the background image
		imgLoader = new ImageLoader();
		lastBackGroundImagePath = levelManager.getBackPicturePath();
		bgImage = imgLoader.loadImage(levelManager.getBackPicturePath(), false);

		// set up message font
		msgsFont = new Font("SansSerif", Font.BOLD, 24);
		metrics = this.getFontMetrics(msgsFont);
	}

	/**
	 * Set the game mode. The game mode decides if the game is running, paused or
	 * something else.
	 * @param newValue new game mode
	 */
	private void setMode(int newValue)
	{
		working_mode = newValue;
		//System.out.println("Neuer MODUS: " + newValue);
	}

	/**
	 * Handles the processing of key events for the game. Right cursor (turn snake
	 * right), left cursor (turn snake left), enter (pause) and esc (quit) are the
	 * main supported keys.
	 * @param e KeyEvent
	 */
	private void processKey(KeyEvent e)
	{
		int keyCode = e.getKeyCode();

		switch (working_mode)
		{
		case MODE_RUNNING:
		{
			switch (keyCode)
			{
			case KeyEvent.VK_LEFT:
			{
				if (snakeSprite != null)
				{
					snakeSprite.turnLeft();
				}
				break;
			}
			case KeyEvent.VK_RIGHT:
			{
				if (snakeSprite != null)
				{
					snakeSprite.turnRight();
				}
				break;
			}
			case KeyEvent.VK_ENTER:
			{
				setMode(MODE_PAUSE);
				break;
			}
			case KeyEvent.VK_1:
			{
				if (gameList.isViewOnly())
				{
					viewOnlySnakeIX = 0;
				}
				break;
			}
			case KeyEvent.VK_2:
			{
				if (gameList.isViewOnly())
				{
					viewOnlySnakeIX = 1;
				}
				break;
			}
			case KeyEvent.VK_3:
			{
				if (gameList.isViewOnly())
				{
					viewOnlySnakeIX = 2;
				}
				break;
			}
			case KeyEvent.VK_4:
			{
				if (gameList.isViewOnly())
				{
					viewOnlySnakeIX = 3;
				}
				break;
			}




			case KeyEvent.VK_C:
			{
				if (!e.isControlDown())
				{
					break;
				}
				//down to quit if ctrl+c pressed
			}
			case KeyEvent.VK_ESCAPE:
			case KeyEvent.VK_Q:
			case KeyEvent.VK_END:
			{
				modeQuit = working_mode;
				setMode(MODE_QUIT);
				break;
			}

			}
			break;
		}

		case MODE_PAUSE: //paused: back to game if enter pressed again
		{
			switch (keyCode)
			{
			case KeyEvent.VK_ENTER:
			{
				setMode(MODE_RUNNING);
				break;
			}
			}
			break;
		}

		case MODE_QUIT: //quit question is shown: quit game if J is pressed
		{
			switch (keyCode)
			{
			case KeyEvent.VK_Y:
			{
				//quit: leave game and open menu
				if (snakeSprite != null)
				{
					snakeSprite.stopTurning();
				}
				setMode(MODE_CLOSE);
				if (multiplayer)
				{
					gameList.leaveGame();
					snakeMain.openMPMenue();
				}
				else
				{
					snakeMain.openMainMenue();
				}

				break;
			}
			case KeyEvent.VK_N:
			{
				setMode(modeQuit);
				break;
			}
			}
			break;
		}
		}
	}

	/**
	 * Handles processing of key events for the game. Stop turning the snake if
	 * left or right cursor key is released.
	 * @param e KeyEvent
	 */
	private void processKeyReleased(KeyEvent e)
	{
		int keyCode = e.getKeyCode();

		if (working_mode == MODE_RUNNING)
		{
			if (keyCode == KeyEvent.VK_LEFT
					|| keyCode == KeyEvent.VK_RIGHT)
			{
				if (snakeSprite != null)
				{
					snakeSprite.stopTurning();
				}
			}
		}
	}

	/**
	 * Initialize the own game sprites and load the level data. Create a new BackgroundManager
	 * to manage the level background. Save the oids with the player, set the points of the
	 * player back to 0, and set the player status to init (ready to load all other snakesprites).
	 * No own game sprites are created if user is in view only mode.
	 */
	public void initMyGameSprites()
	{
		setMode(MODE_STARTING);
		modeQuit = MODE_STARTING;
		Capi conn = null;
		try
		{
			conn = Util.getConnection();
		}
		catch (Exception ex)
		{
			System.out.println("Error occured: Can't get corso connection");
			ex.printStackTrace(System.out);
		}
		//load level
		Game game = gameList.getCurrentGame();
		try
		{
			if (!lastBackGroundImagePath.equals(levelManager.getBackPicturePath()))
			{
				gameMap = null;
				gameMap = new BackgroundManager(pHeight, pWidth, levelManager.getBackDefinitionPath(),
						levelManager.getStartPosPropsPath());
				bgImage = imgLoader.loadImage(levelManager.getBackPicturePath(), false);
			}
		}
		catch (Exception err)
		{
			System.out.println("Error occured: Can't load gamegraphics");
		}

		if (!gameList.isViewOnly())
		{
			// create own game sprites
			myPlayer.setNr(game.indexOf(myPlayer));
			myPlayer.createSnakeOIDs();
			myPlayer.saveToSpace();

			snakeSprite = new SnakeSprite(imgLoader, /*clipsLoader,*/ conn, myPlayer, gameMap, this,
					game.getCollisionType());
		}
		else
		{
			snakeSprite = null;
		}
		collectables = new CollectableSprite(pWidth, pHeight, imgLoader, snakeSprite, this, /*clipsLoader,*/ conn,
				gameList, myPlayer, gameMap);
		//set player init
		myPlayer.setPoints(0);
		gameList.setMyPlayerReady(PlayerState.init);
	}

	/**
	 * Initialize the game sprites of all other players: Load the snakesprite of every
	 * other player and save the snakesprites in an array. Set the player state to loaded
	 * after that (ready to start the game).
	 */
	public void initOtherGameSprites()
	{
		synchronized (gameList)
		{
			if (myPlayer.getPlayerState() != PlayerState.init)
			{
				System.out.println("initOtherGameSprites: playerstate not init");
				return;
			}

			Capi conn = null;
			try
			{
				conn = Util.getConnection();
			}
			catch (Exception ex)
			{
				System.out.println("Error occured:  Can't get corso connection:");
				ex.printStackTrace(System.out);
			}

			//create game sprites for other players
			Game game = gameList.getCurrentGame();
			if (!gameList.isViewOnly())
			{
				otherSnakeSprites = new SnakeSprite[game.getPlayerAnz() - 1];
			}
			else
			{
				otherSnakeSprites = new SnakeSprite[game.getPlayerAnz()];
			}
			int counter = 0;
			//System.out.println("InitOtherSnakeSprites: PlayerAnzahl=" + game.getPlayerAnz());
			for (int i = 0; i < game.getPlayerAnz(); i++)
			{
				if (!game.getPlayer(i).equals(myPlayer))
				{
					//System.out.println("Füge SnakeSprite Hinzu: " + game.getPlayer(i));
					otherSnakeSprites[counter] = new SnakeSprite(imgLoader, conn, game.getPlayer(i), this);
					counter++;
				}
			}
			if (snakeSprite != null)
			{
				snakeSprite.setOtherPlayers(otherSnakeSprites);
			}
			//set player loaded
			gameList.setMyPlayerReady(PlayerState.loaded);
			//System.out.println("initOtherGameSprites stoped");
		}
	}

	/**
	 * Start the game in multiplayer mode: Set the playerstate to starting, set the
	 * game mode to running, and save the current time as the game start time.
	 */
	public void startGame()
	{
		gameList.setMyPlayerReady(PlayerState.starting);

		gameList.setDataChangeListener(null);

		multiplayer = true;
		//hide menue
		setMode(MODE_RUNNING);
		//set game start time
		gameStartTime = System.currentTimeMillis();
		timeSpentInGame = 0;
	}

	/**
	 * Start the game in singleplayer mode: Create the own player, the snakesprite and
	 * the collectable data (without writing to corsospace), and then start the game by
	 * setting the game mode to running.
	 */
	public void startSingleplayerGame()
	{
		// create game sprites
		Player player = new Player(settings.getPlayerName(), settings.getSnakeSkin());
		player.setNr(1);
		snakeSprite = new SnakeSprite(imgLoader, /*clipsLoader,*/ null, player, gameMap, this,
				SnakeSpriteData.COLLISION_OWN | SnakeSpriteData.COLLISION_WALL);
		collectables = new CollectableSprite(pWidth, pHeight, imgLoader, snakeSprite, this, /*clipsLoader,*/ null,
				gameList, myPlayer, gameMap);
		multiplayer = false;
		//hide menue
		setMode(MODE_RUNNING);
		//set game start time
		gameStartTime = System.currentTimeMillis();
	}

	/**
	 * Called by CollectableSprite to signal that the game is over. Calculate final play time,
	 * and signal the gameover to the snake main class, so the gameover menu is shown.
	 */
	public void gameOver()
	{
		int finalTime =
			(int) ( (System.currentTimeMillis() - gameStartTime) / 1000L); // ns --> secs
		if (multiplayer)
		{
			collectables.stopNotifier();
		}
		setMode(MODE_CLOSE);
		snakeMain.gameOver(finalTime);
	}

	/**
	 * Overwritten: Now additionally starts the thread for the animation loop, in this way
	 * waits for the JPanel to be added to the JFrame before starting the thread.
	 */
	public void addNotify()
	{
		super.addNotify(); // creates the peer
		startThread(); // start the thread
	}

	/**
	 * Starts the thread for the animation loop.
	 */
	private void startThread()
	// initialise and start the thread
	{
		if (animator == null || working_mode != MODE_RUNNING)
		{
			animator = new Thread(this);
			animator.start();
		}
	}

	/*public void resumeGame()
     // called when the JFrame is activated / deiconified
     {
    if (working_mode == MODE_PAUSE)
    {
      setMode(MODE_RUNNING);
    }
     }*/

	/*public void pauseGame()
     // called when the JFrame is deactivated / iconified
     {
    if (working_mode == MODE_RUNNING)
    {
      setMode(MODE_PAUSE);
    }
     }*/

	/**
	 * Run method for the animation thread. A special timer class is used to time the
	 * animation (com.vladium.utils.timing.ITimer), because java system time has problems
	 * with (simulated) dual core cpus (tested on pentium cpu with hyperthreading).
	 * The while loop consists of three main actions: updating the game sprites, drawing
	 * the game sprites offscreen, and painting the offscreen image to screen. Then the
	 * loop waits for the remaining amount of time until it continues. If the process
	 * takes longer then the length of one period, drawing is skipped sometimes, to keep
	 * the updates at a consistent rate.
	 */
	public void run()
	{
		long timeDiff, sleepTime;
		long overSleepTime = 0L;
		int noDelays = 0;
		long excess = 0L;

		com.vladium.utils.timing.ITimer specialTimer = com.vladium.utils.timing.TimerFactory.newTimer();
		specialTimer.start();

		java.util.Date date = new java.util.Date();
		//  System.out.println("start at: " + date.toString());
		//  System.out.println("SnakePanel Loop started");
		while (working_mode != MODE_CLOSE)
		{
			gameUpdate(); //update game sprites
			gameRender(); //draw game sprites offscreen
			paintScreen(); //paint the offscreen rendered image onscreen

			specialTimer.stop();
			timeDiff = (long) (specialTimer.getDuration() * 1000000);
			specialTimer.reset();
			specialTimer.start();
			sleepTime = (period - timeDiff) - overSleepTime;

			if (sleepTime > 0)
			{ // some time left in this cycle
				try
				{
					Thread.sleep(sleepTime / 1000000L); // nano -> ms
				}
				catch (InterruptedException ex)
				{}
				specialTimer.stop();
				overSleepTime = (long) (specialTimer.getDuration() * 1000000) - sleepTime;
				specialTimer.reset();
				specialTimer.start();
			}
			else
			{ // sleepTime <= 0; the frame took longer than the period
				excess -= sleepTime; // store excess time value
				overSleepTime = 0L;

				if (++noDelays >= NO_DELAYS_PER_YIELD)
				{
					Thread.yield(); // give another thread a chance to run
					noDelays = 0;
				}
			}

			/* If frame animation is taking too long, update the game state
         without rendering it, to get the updates/sec nearer to
         the required FPS. */
			int skips = 0;
			while ( (excess > period) && (skips < MAX_FRAME_SKIPS))
			{
				excess -= period;
				gameUpdate(); // update state but don't render
				skips++;
			}
		}
		if (collectables != null)
		{
			collectables.stopNotifier();
		}
		specialTimer.stop();
		//  System.out.println("SnakePanel Loop Stopped");
	}

	/**
	 * If game is in running mode, update all game sprites (SnakeSprites, CollectableSprite)
	 * by calling their updateSprite() methods, and calculate the new correction position
	 * of the own snake on the game map.
	 */
	private void gameUpdate()
	{
		try
		{
			switch (working_mode)
			{
			case MODE_RUNNING:
			{
				if (snakeSprite != null)
				{
					snakeSprite.updateSprite();
				}
				if (multiplayer)
				{
					for (int i = 0; i < otherSnakeSprites.length; i++)
					{
						otherSnakeSprites[i].updateSprite();
					}
				}
				collectables.updateSprite();

				if (!gameList.isViewOnly())
				{
					correctionPos = gameMap.calcCorrectionPos(snakeSprite.getHeadPos());
				}
				else
				{
					if (viewOnlySnakeIX >= otherSnakeSprites.length)
					{
						viewOnlySnakeIX = otherSnakeSprites.length - 1;

					}
					correctionPos = gameMap.calcCorrectionPos(otherSnakeSprites[viewOnlySnakeIX].getHeadPos());

				}
				break;

			}
			}
		}
		catch (Exception err)
		{
			System.err.println("gameUpdate: " + err.toString());
			err.printStackTrace();
		}
	}

	/**
	 * Render all game sprites (SnakeSprites, CollectableSprite), messages (status messages,
	 * and quit or paused message if needed), and the level background to a BufferedImage.
	 */
	private void gameRender()
	{
		if (dbImage == null || panelResized)
		{
			panelResized = false;
			dbImage = createImage(pWidth, pHeight);
			if (dbImage == null)
			{
				System.out.println("dbImage is null");
				return;
			}
			else
			{
				dbg = dbImage.getGraphics();
			}
		}

		// draw the background: use the image or a white colour
		if (bgImage == null)
		{
			dbg.setColor(Color.white);
			dbg.fillRect(0, 0, pWidth, pHeight);
		}
		else
		{
			dbg.drawImage(bgImage, -correctionPos.x, -correctionPos.y, this);
		}

		switch (working_mode)
		{
		case MODE_RUNNING:
		{
			// draw game elements
			if (snakeSprite != null)
			{
				snakeSprite.drawSprite(dbg, correctionPos);
			}
			if (multiplayer)
			{
				for (int i = 0; i < otherSnakeSprites.length; i++)
				{
					otherSnakeSprites[i].drawSprite(dbg, correctionPos);
				}
			}
			collectables.drawSprite(dbg, correctionPos);
			printStats(dbg); //print status messages (points, time)
			break;

		}
		case MODE_QUIT:
		{
			//show quit message
			String[] msg = new String[]
			                          {
					"Exit Game?", "    [Y]es / [N]o"};
			printMessage(dbg, msg);
			break;
		}
		case MODE_PAUSE:
		{
			//Print Ingame Messages
			String[] msg = new String[]
			                          {
			                          "Game Paused."};

			printMessage(dbg, msg);
			break;
		}
		}
	}

	/**
	 * Print the number of points and the play time to the given graphics object.
	 * @param g Graphics
	 */
	private void printStats(Graphics g)
	{
		timeSpentInGame =
			(int) ( (System.currentTimeMillis() - gameStartTime) / 1000L); // ns --> secs

		//}
		g.setColor(Color.black);
		g.setFont(msgsFont);

		g.drawString("Points: " + collectables.getPoints(), 15, 30);
		g.drawString("Time: " + timeSpentInGame + " sec", 15, 50);

		g.setColor(Color.black);
	}

	/**
	 * Print a message with a transparent white background window at the middle of
	 * the screen. Every part of the message array is printed as a separate line.
	 * @param g Graphics
	 * @param msg message separated in lines
	 */
	private void printMessage(Graphics g, String[] msg)
	{
		int width = 0;
		int height = 0;

		//calculate width and height of the background window
		for (int i = 0; i < msg.length; i++)
		{
			java.awt.geom.Rectangle2D rect = metrics.getStringBounds(msg[i], g);
			if ( (int) rect.getWidth() > width)
			{
				width = (int) rect.getWidth();
			}
			height += (int) rect.getHeight();

			height += metrics.getLeading();
		}

		height -= metrics.getLeading();

		int x = (pWidth - width) / 2;
		int y = (pHeight - height) / 2;

		//draw window
		Graphics2D g2d = (Graphics2D) g;
		Composite c = g2d.getComposite(); // backup the old composite
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		g2d.setColor(Color.WHITE);
		g2d.fillRect(x - 10, y - metrics.getHeight(), width + 20, height + metrics.getHeight() / 2);
		g2d.setColor(Color.darkGray);
		g2d.drawRect(x - 10, y - metrics.getHeight(), width + 20, height + metrics.getHeight() / 2);
		g2d.setComposite(c); // restore the old composite so it doesn't mess up future rendering

		//draw message
		g2d.setFont(msgsFont);
		g2d.setColor(Color.red);
		for (int i = 0; i < msg.length; i++)
		{
			g2d.drawString(msg[i], x, y);
			y += metrics.getHeight() + metrics.getLeading();
		}
	}

	/**
	 * Use active rendering to put the buffered image on-screen.
	 */
	private void paintScreen()
	{
		Graphics g;
		try
		{
			g = this.getGraphics();
			if (g != null)
			{
				if (dbImage != null)
				{
					g.drawImage(dbImage, 0, 0, null);
				}
				g.dispose();
			}
		}
		catch (Exception e)
		{
			System.out.println("Graphics context error: " + e);
		}
	}

	/**
	 * Get the main frame of the game.
	 * @return Frame
	 */
	public Frame getMainFrame()
	{
		return snakeMain;
	}

	/**
	 * Set the hight and width of the panel (which is the new maximum displayable height and
	 * width of the game). Report the size change to the BackgroundManager so the new
	 * correction position will be calculated correctly. If the size of the level is smaller
	 * than the new maximum size, the panel is set to the size of the level and positioned
	 * in the middle of the frame.
	 * @param maxWidth new width
	 * @param maxHeight new height
	 */
	public void setGameBounds(int maxWidth, int maxHeight)
	{
		int width = bgImage.getWidth();
		if (width > maxWidth)
		{
			width = maxWidth;
		}
		int height = bgImage.getHeight();
		if (height > maxHeight)
		{
			height = maxHeight;
		}

		pWidth = width;
		pHeight = height;
		this.setBounds( (maxWidth - width) / 2, (maxHeight - height) / 2, width, height);
		this.setPreferredSize(new Dimension(width, height));
		//System.out.println("SnakePanel: new width = " + getWidth() + ", height = " + getHeight());
		gameMap.setMaxSize(width, height);
		panelResized = true;
	}

	/**
	 * Get the time spent in game.
	 * @return time spent in game
	 */
	public int getGameTime()
	{
		return timeSpentInGame;
	}
}
