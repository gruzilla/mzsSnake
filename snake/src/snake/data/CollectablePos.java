package snake.data;

import corso.lang.*;

/**
 * Represents one collectable by its coordinates, number and type.
 * @author Thomas Scheller
 */
public class CollectablePos implements CorsoShareable
{
  public int x = 0;
  public int y = 0;
  public int nr = 0;
  public CollectableType type = CollectableType.normal;

  private final String structName = "snakeCollectablePosDataStruct";
  private final int structSize = 4;

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
    x = data.getInt();
    y = data.getInt();
    nr = data.getInt();
    type = CollectableType.values()[data.getInt()];
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
    data.putInt(x);
    data.putInt(y);
    data.putInt(nr);
    data.putInt(type.ordinal());
  }
}
