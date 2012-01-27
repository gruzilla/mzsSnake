package snake.data;

import java.io.Serializable;

/**
 * Represents one collectable by its coordinates, number and type.
 * @author Thomas Scheller
 */
public class CollectablePos implements Serializable
{
	private static final long serialVersionUID = 1L;
	public int x = 0;
	public int y = 0;
	public int nr = 0;
	public CollectableType type = CollectableType.normal;

	public CollectablePos()
	{
	}

	/**
	 * Constructor to create a new Collectable with all information.
	 * @param x x-coordinate of the collectable
	 * @param y y-coordinate of the collectable
	 * @param nr number of the collectable, must be unique in the list of collectables
	 * @param type collectable type (normal or special like speedup)
	 */
	public CollectablePos(int x, int y, int nr, CollectableType type)
	{
		this.x = x;
		this.y = y;
		this.nr = nr;
		this.type = type;
	}
}
