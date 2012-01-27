package snake.data;

import java.util.Vector;
import snake.*;

/**
 * CollectableSpriteData manages the connection to the corospace for the collectables.
 * A notifier thread automatically updates the collectables when notifications occur.
 * Also manages the points of the player.
 * @author Thomas Scheller, Markus Karolus
 */
public class CollectableSpriteData
{
	private final int minBorder = 20;
	private final int minDistance = 50;
	private final double removingStep = 0.1;
	public boolean doublePoints = false;

	private CollectablePos[] pos = null;
	private boolean multiplayer = true;
	private Player myPlayer = null;
	private BackgroundManager gameMap = null;
	private java.util.Random rand;

	private Vector<CollectablePos> eatenPos = new Vector<CollectablePos> ();
	private Vector<Double> eatenStatus = new Vector<Double> ();

	private Integer points = 0; //Integer so it can be used for synchronization

	/**
	 * Create a new CollectableSpriteData. The collectables are read from space, if
	 * they cannot be read they are created and saved to space. After that, the notifier
	 * thread is started.
	 * @param gameList GameListManager with the current game
	 * @param conn the connection to the corsospace
	 * @param myPlayer the own player
	 * @param aGameMap BackgroundManager for the current game
	 */
	public CollectableSpriteData(GameListManager gameList,
			Player myPlayer, BackgroundManager aGameMap)
	{
		gameMap = aGameMap;
		multiplayer = false; // TODO: multiplayer
		this.myPlayer = myPlayer;

		java.util.Date today = new java.util.Date();
		rand = new java.util.Random(today.getTime());

		pos = new CollectablePos[gameMap.getCollectableCount()];

		//singleplayer variant: simply initialize all positions
		int specials = 0;
		for (int i = 0; i < pos.length; i++)
		{
			if (specials < gameMap.getCollectableSpecialCount())
			{
				pos[i] = getNewPosition(i, true);
				specials++;
			}
			else
			{
				pos[i] = getNewPosition(i, false);
			}
		}
	}

	/**
	 * Randomly search for new collectable position until a valid position is found.
	 * @param nr nr of collectable (in the list of collectables)
	 * @param special true if collectbable should be a special collectable
	 * @return CollectablePos
	 */
	private CollectablePos getNewPosition(int nr, boolean special)
	{
		//search new position until a valid position is found
		int newX;
		int newY;
		do
		{
			newX = rand.nextInt(gameMap.getBackgroundWidth());
			newY = rand.nextInt(gameMap.getBackgroundHeight());
		}
		while (!checkPosOk(newX, newY));

		//determine type of new collectable
		CollectableType type = CollectableType.normal;
		if (special)
		{
			//CollectableType[] values = CollectableType.values();
			//type = values[rand.nextInt(values.length-1)+1];

			//alternating special collectables by nr
			if (nr % 2 == 0)
			{
				type = CollectableType.speedup;
			}
			else
			{
				type = CollectableType.doublepoints;
			}
		}

		return new CollectablePos(newX, newY, nr, type);
	}

	/**
	 * Check if the collectable position is valid. Position must not collide with a
	 * map obstacle and not be to near to a border or another collectable.
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return true if the position is valid
	 */
	private boolean checkPosOk(int x, int y)
	{
		//check if pos does not collide with obstacles
		if (!gameMap.freePlaceCollectable(x, y))
		{
			return false;
		}
		//check if pos too near to a border
		if (x < minBorder || x > (gameMap.getBackgroundWidth() - minBorder) ||
				y < minBorder || y > (gameMap.getBackgroundHeight() - minBorder))
		{
			return false;
		}
		//check if pos to near to other positions
		for (int i = 0; i < pos.length; i++)
		{
			if (pos[i] != null && (Math.abs(x - pos[i].x) < minDistance) &&
					(Math.abs(y - pos[i].y) < minDistance))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Create a new collectable at the given position, actualize the points of the player
	 * add old collectable to eaten collectables, and save new collectable and points to
	 * space.
	 * @param nr number of collectable
	 */
	public void eatCollectable(int nr)
	{
		synchronized (pos)
		{
			synchronized (eatenPos)
			{
				//add old collectable at this position to eaten collectables
				eatenPos.addElement(pos[nr]);
				eatenStatus.addElement(1.0d);

				if (pos[nr].type == CollectableType.normal)
				{
					//update points of the player
					points++;
					if (doublePoints)
					{
						points++; //again if double points powerup is active
					}

					//save points to space
					if (multiplayer)
					{
						myPlayer.setPoints(points);
					}
				}

				//create new collectable and save to space
				pos[nr] = getNewPosition(nr, (pos[nr].type != CollectableType.normal));
			}
		}
	}

	/**
	 * Update state of eaten collectables. Eaten collectables are faded increasing
	 * with every update. When fading of an eaten collectable is complet, remove it
	 * from the list.
	 */
	public void updateEatenCollectables()
	{
		synchronized (eatenPos)
		{
			for (int i = 0; i < eatenStatus.size(); i++)
			{
				double status = (Double) eatenStatus.elementAt(i);
				status -= removingStep;
				if (status <= 0)
				{
					eatenPos.removeElementAt(i);
					eatenStatus.removeElementAt(i);
				}
				else
				{
					eatenStatus.setElementAt(status, i);
				}
			}
		}
	}

	public CollectablePos[] getPositions()
	{
		return pos;
	}

	public Vector<CollectablePos> getEatenPositions()
	{
		return eatenPos;
	}

	public Vector<Double> getEatenStatus()
	{
		return eatenStatus;
	}

	public int getPoints()
	{
		return points;
	}

	/**
	 * Add the given value to the points of the player. Value can also be negative.
	 * If the points are negative after adding the value, the points are changed to 0.
	 * Additionaly the player and its points are saved to space.
	 * @param value int
	 */
	public void addPoints(int value)
	{
		synchronized (points)
		{
			points += value;
			if (points < 0)
			{
				points = 0;
			}
			if (multiplayer)
			{
				myPlayer.setPoints(points);
			}
		}
	}

	public void stopNotifier() {
		// TODO Auto-generated method stub
		
	}
}