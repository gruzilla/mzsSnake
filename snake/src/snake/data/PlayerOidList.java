package snake.data;

import corso.lang.*;

/**
 * List of player Oids for a game, helps to make it easier saving the players
 * of a game to space.
 * @author Thomas Scheller, Markus Karolus
 */
public class PlayerOidList implements CorsoShareable
{
  private final String structName = "playerOidListDataStruct";
  private int structSize = 1;
  public CorsoVarOid[] oidList = null;

  public PlayerOidList()
  {
    oidList = new CorsoVarOid[structSize];
  }

  /**
   * Create new PlayerOidList for a number of player oids equal to the structsize.
   * @param structSize size of the corsodata, and number of players
   */
  public PlayerOidList(int structSize)
  {
    this.structSize = structSize;
    oidList = new CorsoVarOid[structSize];
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
    for (int i = 0; i < oidList.length; i++)
    {
      oidList[i] = new CorsoVarOid();
      data.getShareable(oidList[i]);
    }
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
    for (int i = 0; i < oidList.length; i++)
    {
      data.putShareable(oidList[i]);
    }
  }

  /**
   * Create a new array of oids from the Vector by copying all oids from the Vector
   * to the array. The structsize is set to the size of the vector.
   * @param oids Vector with player oids
   */
  public void createOidArray(java.util.Vector<CorsoVarOid> oids)
  {
    this.structSize = oids.size();
    oidList = new CorsoVarOid[oids.size()];
    for (int i = 0; i < oids.size(); i++)
    {
      oidList[i] = (CorsoVarOid)oids.elementAt(i);
    }
  }
}
