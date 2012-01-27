package snake.data;

import java.io.Serializable;

/**
 * The position and direction of one snake part.
 * @author Thomas Scheller, Markus Karolus
 */
public class SnakePos implements Serializable
{
	private static final long serialVersionUID = 1L;
	public double x = 0;
	public double y = 0;
	public double direction = 0;

	public SnakePos()
	{
	}

	/**
	 * Create a new SnakePos.
	 * @param x x coordinate of the part
	 * @param y y coordinate of the part
	 * @param direction direction of the part in degrees
	 */
	public SnakePos(double x, double y, double direction)
	{
		this.x = x;
		this.y = y;
		this.direction = direction;
	}
}
