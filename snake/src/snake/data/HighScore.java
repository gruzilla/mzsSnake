package snake.data;

import java.util.Vector;
import corso.lang.*;

/**
 * The HighScore class represents the highscore in corso space.
 * @author Thomas Scheller, Markus Karolus
 */
public class HighScore implements CorsoShareable
{
  private Vector highscores = new Vector<HighScoreData> ();
  public final int highScoreCount = 10;
  private final String structName = "snakeHighScore";

  /**
   * Default constructor for a newly created highscore list.
   */
  public HighScore()
  {
  }

  /**
   * Create inital empty list
   */
  public void init()
  {
    highscores.clear();
    for (int i = 0;i<highScoreCount;i++)
    {
      highscores.addElement(new HighScoreData());
    }
  }

  /**
   * Read the the object from CorsoSpace.
   * @param data CorsoData
   * @throws CorsoDataException
   */
  public void read(CorsoData data) throws CorsoDataException
  {
    StringBuffer dataName = new StringBuffer("");

    int arity = data.getStructTag(dataName);
     //control expected struct name
    if (!dataName.toString().equals(structName))
    {
      throw new CorsoDataException();
    }
     //control expected struct size
    int size = data.getInt();
    if ( (arity - 1) != size*2)
    {
      throw new CorsoDataException();
    }

    //Read data from the space
    highscores.clear();
    for (int i = 0; i < size; i++)
    {
      HighScoreData hData = new HighScoreData(data.getString(), data.getFloat());
      highscores.addElement(hData);
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
    data.putStructTag(structName, highscores.size() * 2 + 1);

    //Write the data to the space
    data.putInt(highscores.size());
    for (int i = 0; i < highscores.size(); i++)
    {
      HighScoreData hData = (HighScoreData) highscores.elementAt(i);
      data.putString(hData.getName());
      data.putFloat(hData.getPoints());
    }
  }

  /**
   * Add new user into the highscore list.
   *
   * @param name String Name of the player
   * @param points float Reached points
   * @return boolean true if no errors
   */
  public boolean addHighScoreUser(String name, float points)
  {

    boolean added = false;

  /* //no duplicates
    for (int i = 0; i < highscores.size(); i++)
    {
      HighScoreData highScoreData = (HighScoreData) highscores.elementAt(i);
      if (highScoreData.getName().equals(name))
      {
        if (highScoreData.getPoints() < points)
        {
          highscores.removeElementAt(i);
          highscores.addElement(new HighScore());
        }
        else
        {
          return false;
        }
        break;
      }
    }
   */

    for (int i = 0; i < highscores.size(); i++)
    {
      HighScoreData highScoreData = (HighScoreData) highscores.elementAt(i);
      if (highScoreData.getPoints() == 0 || highScoreData.getPoints() < points)
      {
        highscores.removeElementAt(highscores.size() - 1);
        highscores.insertElementAt(new HighScoreData(name, points),i);
        added = true;
        break;
      }
    }
    return added;
  }

  public int size()
  {
    return highscores.size();
  }

  public Vector getVector()
  {
    return highscores;
  }

}
