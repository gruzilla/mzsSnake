package snake;

import java.awt.image.*;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.io.FileInputStream;
import java.awt.Point;
import snake.data.SnakeStartPoint;
import snake.util.ImageLoader;

/**
 * Manager for the background of the game, loads level background obstacles image
 * and level properties, calculates the position correction for large maps, and
 * checks new positions for collectables.
 * @author Thomas Scheller, Markus Karolus
 */
public class BackgroundManager
{
	private BufferedImage gameMap = null; //gif in black an white showing the obstacles on the map (areas the snake cannot enter)
	private int rgb_Border = java.awt.Color.BLACK.getRGB(); //color of the obstacles on the gameMap

	private int maxHeight;
	private int maxWidth;
	private int imageHeight;
	private int imageWidth;
	private boolean levelBiggerThanScreen = false;
	private SnakeStartPoint[] startpunkte = null;
	private java.util.Random rand = new Random(); //previously used to get random positions for the snakes (now chosen by playerNr), now again random, because player nr is now an uuid

	private int collectableCount = 6;
	private int collectableSpecialCount = 2;

	//values for position correction on large maps (when map must be scrolled)
	private final int minCorrectionX = 0;
	private final int minCorrectionY = 0;
	private int maxCorrectionX = 0;
	private int maxCorrectionY = 0;
	private Point standardCorrectionPoint = new Point(0, 0);

	/**
	 * Create a new Background Manager.
	 * @param maxHeight max displayable height (= current height of the frame)
	 * @param maxWidth max displayable width (= current width of the frame)
	 * @param dataImagePath path of the background image file with info on obstacles for the current level
	 * @param settingsPath path of the properties file for the current level
	 */
	public BackgroundManager(int maxHeight, int maxWidth, String dataImagePath, String settingsPath)
	{
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
		try
		{
			ImageLoader loader = new ImageLoader();
			gameMap = loader.loadImage(dataImagePath, false);
			//System.out.println("Backgroundfile loaded: " + dataImagePath);
			calcCorrection();
			loadSettings(settingsPath);
		}
		catch (Exception err)
		{
			System.out.println("Error occured during background loading:" + err.getMessage());
		}
	}

	public BufferedImage getMapImage()
	{
		return gameMap;
	}

	public int getCollectableCount()
	{
		return collectableCount;
	}

	public int getCollectableSpecialCount()
	{
		return collectableSpecialCount;
	}

	/**
	 * Gets the StartPoint for the given Player. If there are more Players than
	 * StartPoints, the position of the startpoint is calculated by playerNr modulo
	 * number of startpoints.
	 * @param playerNr number of the player in the game
	 * @return startpoint for the snake
	 */
	public SnakeStartPoint getStartPoint(UUID playerNr)
	{
		int pos = rand.nextInt(startpunkte.length);
		//int pos = playerNr % startpunkte.length;
		return startpunkte[pos];
	}

	/**
	 * Load all settings for the snake level from the given properties file (startpoints,
	 * number of collectables).
	 * @param settingsPath path of the properties file for the current level
	 */
	private void loadSettings(String settingsPath)
	{
		FileInputStream in = null;
		try
		{
			//java.util.Date today = new java.util.Date();
			//rand = new java.util.Random(today.getTime());
			Properties props = new Properties();
			java.io.File info = new java.io.File(settingsPath);
			System.out.println(info.getAbsolutePath() + " " + info.exists());
			in = new FileInputStream(settingsPath);
			props.load(in);

			collectableCount = Integer.parseInt(props.getProperty("items"));
			collectableSpecialCount = Integer.parseInt(props.getProperty("specialItems"));

			int startPunktAnz = Integer.parseInt(props.getProperty("startpunkte"));
			startpunkte = new SnakeStartPoint[startPunktAnz];
			for (int i = 1; i <= startPunktAnz; i++)
			{
				startpunkte[i - 1] = new SnakeStartPoint(props.getProperty("start" + i));
			}
			in.close();
			System.out.println("Loaded startpoints successfully.");

		}
		catch (Exception ex)
		{
			if (in == null)
			{
				System.out.println("Cannot find background config file: " + settingsPath);
			}
			System.out.println("Error occured: Can't read backgroundfile settings.");
			ex.printStackTrace(System.out);
		}
	}

	/**
	 * Check if Snake at its current position crashes with an obstacle.
	 * @param x x coordinate of the snake (head)
	 * @param y y coordinate of the snake (head)
	 * @return false if snake crashes with an obstacle
	 */
	public boolean freePlaceSnake(int x, int y)
	{
		return freePlace(x + 7, y + 7, 8); //+7 correction to get the coordinate to the center of the snake head
	}

	/**
	 * Check if the position is valid for placing a collectable.
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return false if there is an obstacle at this position
	 */
	public boolean freePlaceCollectable(int x, int y)
	{
		return freePlace(x + 9, y + 9, 10); //+9 correction to get the coordinate to the center of the collectable
	}

	/**
	 * Checks an area around the given coordinate position for any obstacles.
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param controllBereich pixel width and height of the square area that is checked
	 * @return true if there is no obstacle anywhere in the area
	 */
	private boolean freePlace(int x, int y, int controllBereich)
	{

		if (gameMap != null)
		{
			int[] temp = new int[controllBereich * controllBereich];

			int controllx = x - controllBereich / 2;
			int controlly = y - controllBereich / 2;
			if (controllx < 0)
			{
				controllx = 0;
			}
			if (controlly < 0)
			{
				controlly = 0;
			}

			if (controllx > (imageWidth - controllBereich))
			{
				controllx = imageWidth - controllBereich;
			}
			if (controlly > (imageHeight - controllBereich))
			{
				controlly = imageHeight - controllBereich;
			}

			gameMap.getRGB(controllx, controlly, controllBereich, controllBereich, temp, 0, controllBereich); //get all rgb values in the control area

			boolean erg = true;
			for (int i = 0; i < temp.length; i++)
			{
				if (temp[i] == rgb_Border)
				{
					erg = false; //obstacle found
					break;
				}
			}
			return erg;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Calculate the correction position, if level is bigger than the fram size. (Level
	 * has to be moved together with the snake, so the snake is always visible on screen.)
	 * The correction position can be subtracted from the snake positions to make the map
	 * coordinates to screen coordinates.
	 * @param snakeHeadPos position of the snake head
	 * @return Point
	 */
	public Point calcCorrectionPos(snake.data.SnakePos snakeHeadPos)
	{
		if (levelBiggerThanScreen)
		{
			int correctionPosX = (int) snakeHeadPos.x - (maxWidth / 2);
			int correctionPosY = (int) snakeHeadPos.y - (maxHeight / 2);

			if (correctionPosX < minCorrectionX)
			{
				correctionPosX = minCorrectionX;
			}
			else if (correctionPosX > maxCorrectionX)
			{
				correctionPosX = maxCorrectionX;
			}

			if (correctionPosY < minCorrectionY)
			{
				correctionPosY = minCorrectionY;
			}
			else if (correctionPosY > maxCorrectionY)
			{
				correctionPosY = maxCorrectionY;
			}

			return new Point(correctionPosX, correctionPosY);
		}
		return standardCorrectionPoint;
	}

	public int getBackgroundWidth()
	{
		return imageWidth;
	}

	public int getBackgroundHeight()
	{
		return imageHeight;
	}

	/**
	 * Set new maximum width and height (=width and height of the frame) and recalculate
	 * the maximum correction values. This method must be called after the frame size
	 * has been changed.
	 * @param width new width
	 * @param height new width
	 */
	public void setMaxSize(int width, int height)
	{
		maxWidth = width;
		maxHeight = height;
		calcCorrection();
	}

	/**
	 * Calculate the maximum correction values (the maximum position of the screen
	 * when showing the lower right corner of the map). Values are 0 when position
	 * does not need to be corrected (when map is not larger than frame size).
	 */
	private void calcCorrection()
	{
		//Korrekturwerte berechnen für Levels die größer sind als der Bildschirm
		this.imageHeight = gameMap.getHeight();
		this.imageWidth = gameMap.getWidth();
		if (imageHeight > maxHeight || imageWidth > maxWidth)
		{
			levelBiggerThanScreen = true;
			maxCorrectionX = imageWidth - maxWidth;
			maxCorrectionY = imageHeight - maxHeight;
		}
		else
		{
			maxCorrectionX = 0;
			maxCorrectionY = 0;
		}
	}
}
