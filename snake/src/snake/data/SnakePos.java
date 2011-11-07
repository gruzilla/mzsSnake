package snake.data;

import corso.lang.*;

/**
 * The position and direction of one snake part.
 * @author Thomas Scheller, Markus Karolus
 */
public class SnakePos implements CorsoShareable
{
  public double x = 0;
  public double y = 0;
  public double direction = 0;

  private final String structName = "snakePosDataStruct";
  private final int structSize = 3;

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

  /**
   * Read the the object from CorsoSpace.
   * @param data CorsoData
   * @throws CorsoDataException
   */
  public void read(CorsoData data) throws CorsoDataException
  {
    StringBuffer name = new StringBuffer("");

    //control expected struct size
    int arity = data.getStructTag(name);
    if (arity != structSize)
    {
      throw new CorsoDataException();
    }
    //control expected struct name
    if (!name.toString().equals(structName))
    {
      throw new CorsoDataException();
    }

    //read data
    x = data.getDouble();
    y = data.getDouble();
    direction = data.getDouble();
  }

  /**
   * Write the object to CorsoSpace.
   * @param data CorsoData
   * @throws CorsoDataException
   */
  public void write(CorsoData data) throws CorsoDataException
  {
    //create struct with name and size
    data.putStructTag(structName, structSize);

    //write data
    data.putDouble(x);
    data.putDouble(y);
    data.putDouble(direction);
  }
}
