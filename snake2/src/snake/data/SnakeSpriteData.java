package snake.data;

import snake.*;
import snake.util.*;
import snake.ui.SnakeSprite;
import snake.ui.CollectableSprite;
import java.awt.Rectangle;

/**
 * Manages all data of a snake, calculating the positions of the snake parts, and
 * managing the data in corsospace. A SnakeSpriteNotifier object listens for notifications
 * for passive snakes. Also responsable for creating the initial data of the snake,
 * and calculating all collisions.
 * @author Thomas Scheller, Markus Karolus
 */
public class SnakeSpriteData
{
	public static final int STARTPARTS = 4; //number of parts of the snake at start
	public static final int MAXPARTS = 20; //maximum number of parts
	public static final int POINTDIST = 4; //point distance between two (drawn) snake parts(in the array)
	public static final int MAXPOINTS = (MAXPARTS + 1) * POINTDIST; //maximum number of points of the snake
	public static final int PARTDIST = POINTDIST * 4; //total pixel distance between to snake parts

	public static final int UNVERWUNDBAR = (STARTPARTS * 2) * POINTDIST;
	private static final int STEP = 4; //step the position of the snake is increased every update, in other words the speed of the snake in pixels
	private int speedChange = 0; //current change of the speed in pixels

	private int partsAnz = STARTPARTS;
	private double direction = 90.0;
	private int starting;
	private int getroffen;

	// TODO: check if the initialisiations here are not forgotten
	//public int headPos = STARTPARTS * POINTDIST;
	//public int tailPos = 0;
	//private SnakeState snakeState = SnakeState.active;
	//public SnakePos[] parts = new SnakePos[MAXPOINTS]; //the snake parts (actually the points)

	private int pWidth;
	private int pHeight;
	private double tempX = 0.0;
	private double tempY = 0.0;

	//private SnakeOidList snakePosList = null;
	private BackgroundManager gameMap = null;
	private ClipsLoader clipsLoader = null;
	private SnakeSprite[] otherSnakes = null;
	private int snakePartHeight = 0;
	private int snakePartWidth = 0;
	//private CorsoStrategy strat = null;
	private CollectableSprite collectables = null;
	private Player myPlayer = null;
	//private SnakeSpriteNotifier notifier = null;
	//private CorsoConnection conn = null;
	private boolean multiplayer = false;
	private SnakeSprite sprite = null;

	//collision values
	public static final int COLLISION_NONE = 0;
	public static final int COLLISION_WALL = 1;
	public static final int COLLISION_OWN = 2;
	public static final int COLLISION_OTHER = 4;
	private int collision_mode = COLLISION_WALL | COLLISION_OWN | COLLISION_OTHER;

	//pixel positions along the line that is defined by the snake positions
	//size is set so the snake can have 1,5x speed at maximum length
	public java.awt.Point[] pixelPos = new java.awt.Point[MAXPOINTS * 6];
	public int[] pixelDirection = new int[MAXPOINTS * 6];
	public int pixelHeadPos = STARTPARTS * POINTDIST * STEP;


	/**
	 * SnakeSpriteData constructor for active (writing) snake. Get all oids from the player
	 * and set the initial data (positions) of the snake by calling resetSnake().
	 * @param aClipsLoader ClipsLoader with the eat, crash and die sounds of the snake
	 * @param conn connection to the corsospace
	 * @param aPlayer the own player
	 * @param aGameMap manager for the level background
	 * @param collisionMode collision mode of the game (collision with self, others, wall)
	 */
	public SnakeSpriteData(ClipsLoader aClipsLoader, boolean isMultiplayer, Player aPlayer,
												 BackgroundManager aGameMap, int collisionMode)
	{
		clipsLoader = aClipsLoader;
		myPlayer = aPlayer;
		gameMap = aGameMap;
		//this.conn = conn;
		// if (this.conn == null)
		if (isMultiplayer)
		{
			multiplayer = true;
		}
		collision_mode = collisionMode;

		this.pWidth = gameMap.getMapImage().getWidth();
		this.pHeight = gameMap.getMapImage().getHeight();

		//create initial data
		resetSnake();
	}

	/**
	 * SnakeSpriteData constructor for passive (reading) snake. Read the initial data of
	 * the snake from space and start the notifier thread to get notifications on data
	 * changes of the snake.
	 * @param conn connection to the corsospace
	 * @param aPlayer the own player
	 * @param aClipsLoader ClipsLoader with the eat, crash and die sounds of the snake
	 */
	public SnakeSpriteData(Player aPlayer, ClipsLoader aClipsLoader)
	{
		myPlayer = aPlayer;
		clipsLoader = aClipsLoader;
		
		//read initial data
		readInitData();

		//start notifier thread
		// TODO: USE player-notification instead
		//notifier = new SnakeSpriteNotifier();
		//Thread notifierThread = new Thread(notifier);
		//notifierThread.start();
	}

	/**
	 * Set the snakes of the other players.
	 * @param otherPlayer snakes of the other players
	 */
	public void setOtherPlayers(SnakeSprite[] otherPlayer)
	{
		otherSnakes = otherPlayer;
	}

	/**
	 * Set the size of one part of the snake (all parts must have the same size).
	 * @param heigth height of a snake part
	 * @param width width of a snake part
	 */
	public void setSnakePartSize(int heigth, int width)
	{
		snakePartHeight = heigth;
		snakePartWidth = width;
	}

	/**
	 * Set the SnakeSprite this data belongs to.
	 * @param sprite SnakeSprite
	 */
	public void setSprite(SnakeSprite sprite)
	{
		this.sprite = sprite;
	}

	/**
	 * Set the collectable sprite of the game.
	 * @param collectables CollectableSprite
	 */
	public void setCollectableSprite(CollectableSprite collectables)
	{
		this.collectables = collectables;
	}

	/**
	 * write player to space
	 */
	private void writePlayer() {
		// TODO: implement
	}

	/**
	 * Create the snake and its parts by setting the coordinates and degree of all parts
	 * to the given start point. Also initialize the pixel positions with the same coordinates.
	 * Save all data of the snake to space if in multiplayer mode.
	 * @param startX starting point x coordinate
	 * @param startY starting point y coordinate
	 * @param degree direction
	 */
	private void createData(double startX, double startY, int degree)
	{
		for (int i = myPlayer.getHeadPos(); i >= myPlayer.getTailPos(); i--)
		{
			myPlayer.setPart(i, new SnakePos(startX, startY, degree));
			//parts[i] = new SnakePos(startX, startY, degree);
		}
		for (int i = 0; i < MAXPOINTS; i++) //fill unused points with 0
		{
			if (myPlayer.getPart(i) == null) myPlayer.setPart(i, new SnakePos());
			/*
			if (parts[i] == null)
			{
				parts[i] = new SnakePos();
			}
			*/
		}

		//initialize pixel positions
		for (int i = 0; i <= pixelHeadPos; i++)
		{
			pixelPos[i] = new java.awt.Point( (int) startX, (int) startY);
			pixelDirection[i] = degree;
		}
		for (int i = 0; i < pixelPos.length; i++)
		{
			if (pixelPos[i] == null)
			{
				pixelPos[i] = new java.awt.Point();
				pixelDirection[i] = 0;
			}
		}

		if (multiplayer)
		{
			//write snake data to space
			//snakePosList = new SnakeOidList();
			//for (int i = 0; i < snakePosList.oidList.length; i++)
			//{
			//	snakePosList.oidList[i] = conn.createVarOid(strat);
			//	snakePosList.oidList[i].writeShareable(parts[i], tx);
			//}
			//myPlayer.setPlayerState(snakeState);
			//myPlayer.setHeadPos(headPos);
			//myPlayer.setTailPos(tailPos);
			//myPlayer.setParts(parts);
			
			//snakePosOid.writeShareable(snakePosList, tx);
			//snakeHeadOid.writeInt(headPos, tx);
			//snakeTailOid.writeInt(tailPos, tx);
			//snakeStateOid.writeInt(snakeState.ordinal(), tx);
			//tx.commit(CorsoConnection.INFINITE_TIMEOUT);

			writePlayer();
			
			//System.out.println("Variables created");
		}
	}

	/**
	 * Read the initial data of the snake from corsospace: Read state, head position,
	 * tail position, and the positions of all snake parts. Then initialize the pixel
	 * positions of the snake.
	 */
	private void readInitData()
	{
		//Initiale Daten für Schlange aus CorsoSpace lesen
		/*
		try
		{
			// CorsoStrategy strat = new CorsoStrategy(Util.STRATEGY);
			snakeHeadOid = myPlayer.getSnakeHeadOid();
			snakeTailOid = myPlayer.getSnakeTailOid();
			snakePosOid = myPlayer.getSnakePosOid();
			snakeStateOid = myPlayer.getSnakeStateOid();

			// CorsoTopTransaction tx = conn.createTopTransaction();
			headPos = snakeHeadOid.readInt(null, CorsoConnection.INFINITE_TIMEOUT);
			tailPos = snakeTailOid.readInt(null, CorsoConnection.INFINITE_TIMEOUT);
			snakePosList = new SnakeOidList();
			snakePosOid.readShareable(snakePosList, null, CorsoConnection.INFINITE_TIMEOUT);
			snakeState = SnakeState.values()[snakeStateOid.readInt(null, CorsoConnection.NO_TIMEOUT)];

			for (int i = 0; i < snakePosList.oidList.length; i++)
			{
				parts[i] = new SnakePos();
				snakePosList.oidList[i].readShareable(parts[i], null, CorsoConnection.INFINITE_TIMEOUT);
			}
		*/
			//Pixelpositionen initialisieren
			initPixelPositions(true);
			for (int i = 0; i < pixelPos.length; i++)
			{
				if (pixelPos[i] == null)
				{
					pixelPos[i] = new java.awt.Point();
					pixelDirection[i] = 0;
				}
			}
			// tx.commit(CorsoConnection.INFINITE_TIMEOUT);
		/*
		}
		catch (CorsoException ex)
		{
			System.out.println("Corso Error occured:");
			ex.printStackTrace(System.out);
		}
		*/
	}

	/**
	 * Initialize the pixel positions of the snake along the line that is defined by the
	 * snake parts. How many pixel positions are between two parts is decided by the
	 * STEP value of the snake (speed changed added if it should be included)
	 * @param includeSpeedChange true if the current speed change value should be included
	 */
	private void initPixelPositions(boolean includeSpeedChange)
	{
		pixelHeadPos = 0;
		int step = STEP + (includeSpeedChange ? speedChange : 0);
		for (int i = myPlayer.getTailPos(); i < myPlayer.getHeadPos(); i++)
		{
			SnakePos pos1 = myPlayer.getPart(i);
			SnakePos pos2 = myPlayer.getPart(i + 1);

			//calculate x and y increment per step from part 1 to part 2
			double xIncrement = (pos2.x - pos1.x) / (double) step;
			double yIncrement = (pos2.y - pos1.y) / (double) step;

			//calc pixel positions between the two snake parts, take the direction of part 1
			for (int j = 0; j < step; j++)
			{
				pixelPos[pixelHeadPos] = new java.awt.Point( (int) (pos1.x + xIncrement * j),
						(int) (pos1.y + yIncrement * j));
				pixelDirection[pixelHeadPos] = (int) pos1.direction;
				pixelHeadPos++;
			}
		}
		pixelPos[pixelHeadPos] = new java.awt.Point( (int) myPlayer.getHeadPart().x, (int) myPlayer.getHeadPart().y);
		pixelDirection[pixelHeadPos] = (int) myPlayer.getHeadPart().direction;
	}

	/**
	 * Check if the collision value includes the given type
	 * @param Collision_Typ collision type (self, other or wall)
	 * @return true if type collision value includes this type
	 */
	private boolean useCollision(int Collision_Typ)
	{
		return (collision_mode & Collision_Typ) == Collision_Typ;
	}

	/**
	 * Check if collision with wall is used.
	 * @return true if collision with wall is used
	 */
	private boolean useCollisionWALL()
	{
		return useCollision(COLLISION_WALL);
	}

	/**
	 * Check if collision with self is used.
	 * @return true if collision with self is used
	 */
	private boolean useCollisionOWN()
	{
		return useCollision(COLLISION_OWN);
	}

	/**
	 * Check if collision with others is used.
	 * @return true if collision with others is used
	 */
	private boolean useCollisionOTHER()
	{
		return useCollision(COLLISION_OTHER);
	}

	/**
	 * Reset/initialize the snake: Read the startpoint of the snake from the background
	 * manager, create the initial snake data by calling creatData(), then update the
	 * snake so that it spreads into the starting direction and not all parts are at
	 * the same point. Write the snake positions to space at the end, if in multiplayer
	 * mode.
	 */
	private void resetSnake()
	{
		SnakeStartPoint start = gameMap.getStartPoint(myPlayer.getNr());
		direction = start.getDegree();
		myPlayer.setHeadPos(STARTPARTS * POINTDIST);
		//headPos = STARTPARTS * POINTDIST;
		myPlayer.setTailPos(0);
		//tailPos = 0;
		starting = STARTPARTS * POINTDIST;

		myPlayer.setSnakeState(SnakeState.active);
		//snakeState = SnakeState.active;
		createData(start.getX(), start.getY(), start.getDegree());

		int collisionTemp = collision_mode;
		collision_mode = 0; //turn off collision while starting
		int end = myPlayer.getHeadPos();
		//int end = headPos;
		for (int i = 0; i < STARTPARTS * POINTDIST; i++)
		{
			updateData(0.0);
		}
		myPlayer.setTailPos(end);
		//tailPos = end;
		collision_mode = collisionTemp;

		if (multiplayer)
		{
			writePlayer();
			//writeSnakePositions();
		}
	}

	/**
	 * Restart the snake after it crashed. Set the state to unverwundbar, load the
	 * start point from the background manager, set the headposition to the start
	 * point, set the length of the snake back to the default start length, and update
	 * the snake so that it moves over the start position with all parts, having the correct
	 * start position in the end. In this way, as few data as possible is changed and has to
	 * be loaded from the space by the other players. The snake positions are written to
	 * space if in multiplayer mode. A restart effect for the snake is started.
	 */
	private void restartSnake()
	{
		myPlayer.setSnakeState(SnakeState.unverwundbar);
		//snakeState = SnakeState.unverwundbar;
		SnakeStartPoint newStartPoint = gameMap.getStartPoint(myPlayer.getNr());
		starting = STARTPARTS * POINTDIST;
		direction = newStartPoint.getDegree();

	//	System.out.println("New Start: (" + newStartPoint.getX() + "," + newStartPoint.getY() + "), Degree " +	newStartPoint.getDegree());

		int collisionTemp = collision_mode;
		collision_mode = 0; //turn off collision while starting
		updateData(0.0);
		myPlayer.getHeadPart().direction = direction;
		//parts[headPos].direction = direction;
		myPlayer.getHeadPart().x = newStartPoint.getX();
		//parts[headPos].x = newStartPoint.getX();
		myPlayer.getHeadPart().y = newStartPoint.getY();
		//parts[headPos].y = newStartPoint.getY();
		int end = myPlayer.getHeadPos();
		//int end = headPos;
		for (int i = 0; i < STARTPARTS * POINTDIST; i++)
		{
			updateData(0.0);
		}
		myPlayer.setTailPos(end);
		//tailPos = end;
		partsAnz = STARTPARTS;

		collision_mode = collisionTemp;

		if (multiplayer)
		{
			writePlayer();
			//writeSnakePositions();
		}

		sprite.restartSnake(); //restart effect
	}

	/**
	 * Get the length of the snake (the number of parts).
	 * @return the length of the snake
	 */
	public int getSnakeLength()
	{
		return partsAnz;
	}

	/**
	 * Check if a part of the snake crashes with the given control rectangle.
	 * @param controllRect the rectangle that is checked for crash
	 * @param abstandHead number of parts that are not checked, in direction from head to tail
	 * @param snakeLength length of the other snake, if crash with another snake is checked
	 * @return true if a part of the snake crashes with the given control rectangle
	 */
	private boolean checkCrash(Rectangle controllRect, int abstandHead, int snakeLength)
	{
		int start = myPlayer.getTailPos();
		int stop = myPlayer.getHeadPos() - abstandHead;
		if (stop < 0)
		{
			stop = MAXPOINTS + stop;
		}

		while (start != stop)
		{
			//create a rectangle for the current snake part
			Rectangle rect = new Rectangle( (int) myPlayer.getPart(start).x + 2, (int) myPlayer.getPart(start).y + 2,
																		 snakePartWidth - 4,
																		 snakePartHeight - 4);

			start++;
			if (start >= MAXPOINTS)
			{
				start = start - MAXPOINTS;
			}

			//check if the rectangle intersects with the control rectangle (-> crash)
			if (rect.intersects(controllRect))
			{
				if (start == myPlayer.getHeadPos())
				{
					if (getSnakeLength() > snakeLength)
					{ //own snake is longer and wins
						//System.out.println("Über Kopf gewonnen");
						return false;
					}
					else
					{
						//own snake is shorter and looses
						//System.out.println("Über Kopf verloren");
					}
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * If collision with self is used, check if the Snake crashes with itself at the
	 * given new snake head coordinates. Use the checkCrash() method for the control.
	 * @param controllPosX new snake head x coordinate
	 * @param controllPosY new snake head y coordinate
	 * @return true if snake crashes with itself
	 */
	private boolean checkCrashOwn(int controllPosX, int controllPosY)
	{
		if (useCollisionOWN())
		{
			if (getSnakeLength() <= STARTPARTS)
			{
				return false;
			}
			if (starting > 0)
			{
				return false;
			}
			Rectangle headRect = new Rectangle( (int) controllPosX + 2, (int) controllPosY + 2,
																				 snakePartWidth - 4, snakePartHeight - 4);
			return checkCrash(headRect, POINTDIST * 3, -1);
		}
		else
		{
			return false;
		}
	}

	/**
	 * If collision with others is used, check if the Snake crashes with other snakes, by
	 * checking if the body of the snake crashes with the head of another snake. Use the
	 * checkCrash() method for the control.
	 * @return true if snake crashes with another snake
	 */
	private boolean checkCrashOther()
	{
		if (useCollisionOTHER())
		{
			if (otherSnakes != null && otherSnakes.length > 0)
			{
				for (int j = 0; j < otherSnakes.length; j++)
				{
					SnakeSpriteData otherSnakeData = otherSnakes[j].getData();

					Rectangle headRectOther = new Rectangle( (int) otherSnakeData.myPlayer.getHeadPart().x + 2,
																									(int) otherSnakeData.myPlayer.getHeadPart().y + 2,
																									snakePartWidth - 4, snakePartHeight - 4);
					return checkCrash(headRectOther, 0, otherSnakeData.getSnakeLength());
				}
			}
		}
		return false;
	}

	/**
	 * Updates the position of the snake head and saves it in a temp object. Checks
	 * if the new coordinates are valid by checking if any crash with wall or itself
	 * occurs. If a crash occurs, crashAction() is called.
	 * @param turn direction change value for the current update
	 * @return true if the update of the coordinates is valid (no crash, ...)
	 */
	private boolean checkFreePlace(double turn)
	{
		direction += turn;
		if (direction < 0)
		{
			direction += 360;
		}
		else if (direction > 359)
		{
			direction -= 360;
		}

		//calculate new coordinates
		int step = STEP + speedChange;
		double changeX = (step * Math.cos(Math.toRadians(direction)));
		double changeY = (step * Math.sin(Math.toRadians(direction)));

		int controllPosX = (int) (myPlayer.getHeadPart().x - changeX);
		int controllPosY = (int) (myPlayer.getHeadPart().y - changeY);

		//check crash with self
		boolean end = false;
		while (checkCrashOwn(controllPosX, controllPosY))
		{
			crashAction(COLLISION_OWN);
			end = true;
		}
		if (end)
		{
			return false;
		}
		//check crash with wall
		if (!gameMap.freePlaceSnake(controllPosX, controllPosY))
		{
			if (useCollisionWALL())
			{
				crashAction(COLLISION_WALL);
			}

			return false;
		}
		else
		{
			//save new position in temp variables
			tempX = changeX;
			tempY = changeY;
			return true;
		}
	}

	/**
	 * Update the data of the snake: Increase head and tail position by 1, calculate
	 * the coordinates for the new head position, check if the they are valid, or if
	 * any crashes occur, and update the pixel positions (calculate the positions from
	 * the last head position to the new head position).
	 * @param turn direction change value for the current update
	 */
	public void updateData(double turn)
	{
		if (starting > 0)
		{
			starting--;
		}
		if (getroffen > 0)
		{
			getroffen--;
		}

		if (!multiplayer)
		{
			if (getroffen == 0 && myPlayer.getSnakeState() != SnakeState.active)
			{
				myPlayer.setSnakeState(SnakeState.active);
			}
		}

		double changeX = 0.0;
		double changeY = 0.0;

		if (checkFreePlace(turn)) //check if update is valid
		{
			changeX = tempX;
			changeY = tempY;
		}
		else
		{
			return; //crash occured, cancel update
		}

		int prevPos = myPlayer.getHeadPos(); // save old head pos while creating new one
		
		myPlayer.setHeadPos((myPlayer.getHeadPos() + 1) % MAXPOINTS);
		//headPos = (headPos + 1) % MAXPOINTS;
		myPlayer.setTailPos((myPlayer.getTailPos() + 1) % MAXPOINTS);
		//tailPos = (tailPos + 1) % MAXPOINTS;

		double newPosX = myPlayer.getPart(prevPos).x - changeX;
		double newPosY = myPlayer.getPart(prevPos).y - changeY;

		boolean setChanges = true;
		// modify newX/newY if < 0, or > pWidth/pHeight; use wraparound
		if (newPosX < -5) //head out of left border?
		{
			if (gameMap.freePlaceSnake( (int) (pWidth - newPosX), (int) (newPosY)))
			{
				newPosX += pWidth;
			}
			else
			{
				setChanges = false;
			}
		}
		else if (newPosX > pWidth) //head out of right border?
		{
			if (gameMap.freePlaceSnake( (int) (newPosX - pWidth), (int) (newPosY)))
			{
				newPosX -= pWidth;
			}
			else
			{
				setChanges = false;
			}
		}
		if (newPosY < -5) //head out of top border?
		{
			if (gameMap.freePlaceSnake( (int) (newPosX), (int) (pHeight - newPosY)))
			{
				newPosY += pHeight;
			}
			else
			{
				setChanges = false;
			}
		}
		else if (newPosY > pHeight) //head out of bottom border?
		{
			if (gameMap.freePlaceSnake( (int) (newPosX), (int) (newPosY - pHeight)))
			{
				newPosY -= pHeight;
			}
			else
			{
				setChanges = false;
			}
		}
		//set new head position if it is vaid
		if (!setChanges)
		{
			myPlayer.getHeadPart().x = myPlayer.getPart(prevPos).x;
			myPlayer.getHeadPart().y = myPlayer.getPart(prevPos).y;
		}
		else
		{
			myPlayer.getHeadPart().x = newPosX;
			myPlayer.getHeadPart().y = newPosY;
		}
		myPlayer.getHeadPart().direction = direction;

		//calculate pixel positions from last headpos to new headpos
		updatePixelPositions(myPlayer.getHeadPos(), prevPos, true);

		//check crash with other snakes
		if (checkCrashOther())
		{
			crashAction(COLLISION_OTHER);
		}
	}

	/**
	 * Calculate the pixel positions between head position and previous head position.
	 * Take into consideration that snake can cross the window border and the pixel positions
	 * have to be approximated in this case. If the snake is a passive snake (of another
	 * player), check the speed of the snake by calculating the distance between the two
	 * points.
	 * @param headPos snake head position
	 * @param prevHeadPos previous snake head position
	 * @param activeSnake true if the snake is an active (writing) snake
	 */
	private void updatePixelPositions(int headPos, int prevHeadPos, boolean activeSnake)
	{
		//Pixelpositionen zwischen letzter HeadPos und neuer HeadPos berechnen
		int step = STEP + speedChange;
		double xIncrement;
		double yIncrement;
		if (activeSnake)
		{
			//active (writing) snake: simply calculate x and y increment for pixel positions
			xIncrement = (myPlayer.getHeadPart().x - myPlayer.getPart(prevHeadPos).x) / (double) step;
			yIncrement = (myPlayer.getHeadPart().y - myPlayer.getPart(prevHeadPos).y) / (double) step;
		}
		else
		{
			//passive (reading) snake: control if speed has changed, by checking the distance between the two points
			double x = myPlayer.getHeadPart().x - myPlayer.getPart(prevHeadPos).x;
			double y = myPlayer.getHeadPart().y - myPlayer.getPart(prevHeadPos).y;
			//calc distance between points and round to new speed change value
			int distance = (int) Math.round(Math.sqrt(x * x + y * y));
			if (distance != step && distance < 20) //check if speed has changed; must not be seen as changed if snake crossed a window border
			{
				speedChange = distance - STEP;
				step = STEP + speedChange;
			}
			xIncrement = x / (double) step;
			yIncrement = y / (double) step;
		}
		if (Math.abs(xIncrement) > 1) //snake passed left or right window border: value is wrong and must be corrected (only approximation)
		{
			xIncrement = -0.5 * Math.signum(xIncrement);
		}
		if (Math.abs(yIncrement) > 1) //snake passed top or bottom window border: value is wrong and must be corrected (only approximation)
		{
			yIncrement = -0.5 * Math.signum(yIncrement);
		}
		//calculate the pixel positions by using the previously calculated increment values
		for (int i = 0; i < step; i++)
		{
			pixelPos[pixelHeadPos] = new java.awt.Point( (int) (myPlayer.getPart(prevHeadPos).x + xIncrement * i),
																									(int) (myPlayer.getPart(prevHeadPos).y + yIncrement * i));
			pixelDirection[pixelHeadPos] = (int) myPlayer.getPart(prevHeadPos).direction;
			pixelHeadPos++;
			if (pixelHeadPos >= pixelPos.length)
			{
				pixelHeadPos -= pixelPos.length;
			}
		}
		pixelPos[pixelHeadPos] = new java.awt.Point( (int) myPlayer.getHeadPart().x, (int) myPlayer.getHeadPart().y);
		pixelDirection[pixelHeadPos] = (int) myPlayer.getHeadPart().direction;
	}

	/**
	 * Do some action depending on crash type:
	 * - collision with self: play crash sound and remove a snake part.
	 * - collision with others: play crash sound and remove a snake part.
	 * - collision with wall: play die sound and restart the snake.
	 * Independent of the type, the state is saved to space, an effect is started and
	 * the CollectableSprite is informed so points of the player can be decreased
	 * depending on the type of collision.
	 * @param collisiontyp collision type (self, other or wall)
	 */
	private void crashAction(int collisiontyp)
	{
		switch (collisiontyp)
		{
			case COLLISION_OTHER:
			{
				if (getroffen == 0)
				{
					//collision with others: play crash sound and remove a snake part
					clipsLoader.playCrash();
					sprite.crashSnake();
					collectables.snakeCrashed(COLLISION_OTHER);
				 // System.out.println("Crash with other snake");
					removeSnakePart();
					getroffen = STARTPARTS * POINTDIST;
					myPlayer.setSnakeState(SnakeState.crashed);
					//snakeState = SnakeState.crashed;
					writePlayer();
					//writeState();
				}
				break;
			}
			case COLLISION_OWN:
			{
				//collision with self: play crash sound and remove a snake part
				clipsLoader.playCrash();
				sprite.crashSnake();
				collectables.snakeCrashed(COLLISION_OWN);
				//System.out.println("Crash with itself");
				removeSnakePart();
				getroffen = STARTPARTS * POINTDIST;
				myPlayer.setSnakeState(SnakeState.crashed);
				//snakeState = SnakeState.crashed;
				writePlayer();
				//writeState();
				break;
			}
			case COLLISION_WALL:
			{
				//collision with wall: play die sound and restart the snake
				collectables.snakeCrashed(COLLISION_WALL);
			 // System.out.println("crash with object");
				clipsLoader.playDie();
				restartSnake();
				getroffen = UNVERWUNDBAR;
				break;
			}
			default:
			{
				System.out.println("No action for typ :" + collisiontyp + " defined");
				break;
			}
		}
	}

	/**
	 * Decrease the number of snake parts by 1 and change the tailpos appropriate to
	 * the decrease, if the number of parts is larger than the minimum value.
	 */
	public void removeSnakePart()
	{
		if (STARTPARTS < partsAnz)
		{
			partsAnz--;
			int tailPos = myPlayer.getTailPos();
			tailPos += POINTDIST;
			if (tailPos >= MAXPOINTS)
			{
				tailPos = tailPos - MAXPOINTS;
			}
			myPlayer.setTailPos(tailPos);
		}
	}

	/**
	 * Increase the number of snake parts by 1 and change the tailpos appropriate to
	 * the increase, if the number of parts is smaller than the maximum value.
	 */
	public void addSnakePart()
	{
		if (partsAnz < MAXPARTS)
		{
			partsAnz++;
			int tailPos = myPlayer.getTailPos();
			tailPos -= POINTDIST;
			if (tailPos < 0)
			{
				tailPos += MAXPOINTS;
			}
			myPlayer.setTailPos(tailPos);
		}
	}

	/**
	 * Set a new speed change value.
	 * @param value new speed change
	 */
	public void setSpeedChange(int value)
	{
		speedChange = value;
	}

	/*
	public void writeData()
	{
		if (multiplayer)
		{
			//Daten in CorsoSpace aktualisieren
			int count = 10;
			while (count > 0)
			{
				try
				{

					CorsoTopTransaction tx = conn.createTopTransaction();

					if (getroffen == 0 && snakeState != SnakeState.active)
					{
						snakeState = SnakeState.active;
						snakeStateOid.writeInt(snakeState.ordinal(), tx);
					}
					if (snake.corso.Util.usingOneSpace)
					{
						snakeHeadOid.writeInt(headPos, tx);
						snakeTailOid.writeInt(tailPos, tx);
					}

					snakePosList.oidList[headPos].writeShareable(parts[headPos], tx);

					if (!snake.corso.Util.usingOneSpace)
					{
						snakeHeadOid.writeInt(headPos, tx);
						snakeTailOid.writeInt(tailPos, tx);
					}

					tx.commit(CorsoConnection.INFINITE_TIMEOUT);
					count = -1;

				}
				catch (CorsoException ex)
				{
					System.out.println("writeData: Corso Error occured:");
					ex.printStackTrace(System.out);
					System.exit(0); //Test-Fehlerlösung
					count--;
				}
			}
			if (count == 0)
			{
				System.out.println("SnakeSpriteData can't set writeData!");
			}

		}
	}

	/*
	//DEACTIVATED, reading is now solved by notifications
		public void readData()
		{
			//Daten aus CorsoSpace auslesen und in Array speichern
			try
			{

				int newHeadPos = 0;
				try
				{
					snakeHeadOid = new CorsoVarOid(snakeHeadOid);
					newHeadPos = snakeHeadOid.readInt(null, CorsoConnection.NO_TIMEOUT);
				}
				catch (CorsoException ex)
				{
					System.out.println("ReadData SnakeSpriteData, SnakeHead");
					ex.printStackTrace();
				}

				if (headPos != newHeadPos)
				{
					snakeTailOid = new CorsoVarOid(snakeTailOid);
					tailPos = snakeTailOid.readInt(null, CorsoConnection.NO_TIMEOUT);

					//Alle Positionen bis newHeadPos müssen aktualisiert werden
					while (headPos != newHeadPos)
					{
						headPos = (headPos + 1) % MAXPOINTS;

						CorsoVarOid readPos = new CorsoVarOid(snakePosList.oidList[headPos]);
						readPos.readShareable(parts[headPos], null, CorsoConnection.NO_TIMEOUT);
					}
				}
				//tx.commit(CorsoConnection.INFINITE_TIMEOUT);
			}
			catch (CorsoException ex)
			{
				System.out.println("ReadData SnakeSpriteData, Positionen");
				ex.printStackTrace();
			}
		 }*/


	/**
	 * Stop the data notifier thread.
	 * /
	public void stopNotifier()
	{
		if (notifier != null)
		{
			notifier.stop();
		}
	}
	//*/
}
