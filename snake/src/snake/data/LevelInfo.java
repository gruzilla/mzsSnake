package snake.data;

import java.util.Properties;
import java.io.FileInputStream;

/**
 * The LevelInfo class represents the short information from one level
 * Reading the level name from the levelsettingsfile
 * @author Thomas Scheller, Markus Karolus
 */
public class LevelInfo
{
  private String name;
  private String dirName;

  /**
   * Default constructor for a newly created highscore list.
   *
   * @param dirName String
   */
  public LevelInfo(String dirName)
  {
    this.dirName = dirName;
  }

  /**
   * Loading the levelname from the settingsfile in the leveldirectory
   *
   * @param absolutePath String
   * @param startPosPropsFilename String
   */
  public void loadLevelName(String absolutePath, String startPosPropsFilename)
  {
    String propFile = absolutePath + "\\" + dirName + "\\" + startPosPropsFilename;
    try
    {
      Properties props = new Properties();
      FileInputStream in = new FileInputStream(propFile);
      if (in == null)
      {
        throw new Exception("Cannot find background config file: " + propFile);
      }
      props.load(in);

      name = props.getProperty("name");

      in.close();
    }
    catch (Exception ex)
    {
      System.out.println("Error occurred: Can't read level name." + propFile);
      ex.printStackTrace(System.out);
    }
    if (name == null)
    {
      name = dirName;
    }
  }

  public String getName()
  {
    return name;
  }

  public String getDirName()
  {
    return dirName;
  }
}
