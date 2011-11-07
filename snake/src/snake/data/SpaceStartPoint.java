package snake.data;

import corso.lang.*;
import snake.corso.Util;

/**
 * Connection Point that is created at every corso Site, so the game can get
 * information about the server name and the gamelist oid. This is the only
 * CorsoShareable that is saved as a named oid. This class is necessary because
 * named oids can only read at the site they are created.
 * @author Thomas Scheller, Markus Karolus
 */
public class SpaceStartPoint implements CorsoShareable
{
  private String serverName = "";
  private CorsoVarOid gameListOID = null;
  private CorsoVarOid highScoreOID = null;

  private final String structName = "snakeSpacePoint";
  private final int structSize = 3;

  public SpaceStartPoint()
  {
  }

  /**
   * Create a new start point with the given server name, and create new var oids
   * for gamelist and highscore.
   * @param aServerName name of the server
   */
  public SpaceStartPoint(String aServerName)
  {
    this.serverName = aServerName;
    this.gameListOID = Util.createVarOid();
    this.highScoreOID = Util.createVarOid();
  }

  /**
   * Create a new start point.
   * @param aServerName name of the server
   * @param newGameListOID gamelist oid
   * @param newHighScoreOID highscore oid
   */
  public SpaceStartPoint(String aServerName, CorsoVarOid newGameListOID, CorsoVarOid newHighScoreOID)
  {
    this.serverName = aServerName;
    this.gameListOID = newGameListOID;
    this.highScoreOID = newHighScoreOID;
  }

  /**
   * Read the the object from CorsoSpace.
   * @param data CorsoData
   * @throws CorsoDataException
   */
  public void read(CorsoData data) throws CorsoDataException
  {
    StringBuffer dataName = new StringBuffer("");

    //control expected struct size
    int arity = data.getStructTag(dataName);
    if (arity == 1)
    {
      //read data
      serverName = data.getString();
      gameListOID = Util.createVarOid();
      highScoreOID = Util.createVarOid();
    }
    else
    {
      if (arity != structSize)
      {
        throw new CorsoDataException();
      }
      //control expected struct name
      if (!dataName.toString().equals(structName))
      {
        throw new CorsoDataException();
      }

      //read data
      serverName = data.getString();
      gameListOID = new CorsoVarOid();
      data.getShareable(gameListOID);
      highScoreOID = new CorsoVarOid();
      data.getShareable(highScoreOID);
    }
  }

  public void write(CorsoData data) throws CorsoDataException
  {
    //create struct with name and size
    if (gameListOID != null)
    {
      data.putStructTag(structName, structSize);
      //write data
      data.putString(serverName);
      data.putShareable(gameListOID);
      data.putShareable(highScoreOID);
    }
    else
    {
      data.putStructTag(structName, 1);
      //write data
      data.putString(serverName);
    }
  }

  public CorsoVarOid getGameListOid()
  {
    return gameListOID;
  }

  public CorsoVarOid getHighScoreOid()
  {
    return highScoreOID;
  }

  public String getServerName()
  {
    return serverName;
  }
}
