package snake.data;

import corso.lang.*;
import snake.LevelsManager;
import java.io.*;

/**
 * The LevelData class represents one game level in corso space.
 * @author Thomas Scheller, Markus Karolus
 */
public class LevelData implements CorsoShareable
{
  private String levelDir = "";
  private String levelName = "";
  private byte[] backgroundImg;
  private byte[] backgroundMapImg;
  private byte[] thumbnailFilenameImg;
  private byte[] startPosPropsFilename;
  private byte[] checkSum;

  private final String structName = "snakeLevelDataStruct";
  private final int structSize = 7;

  /**
   * Default constructor for a newly created level data object.
   */
  public LevelData()
  {
  }

  /**
   * CheckSum from all data
   *
   * @return byte[]
   */
  public byte[] getCheckSum()
  {
    return checkSum;
  }

  /**
     * Returns name of the datadirectory where all
     * files are stored
     * @return String
   */
  public String getLevelDir()
  {
    return levelDir;
  }

  /**
   * Returns name of the level
   * @return String
  */
  public String getLevelName()
  {
    return levelName;
  }

  /**
   * Default constructor for a newly created level data object
   * Read the object direct from CorsoSpace.
   * @param dataOID CorsoVarOid
   */
  public LevelData(CorsoVarOid dataOID)
  {
    try
    {
      if (dataOID != null)
      {
        dataOID.readShareable(this, null, CorsoConnection.NO_TIMEOUT);
      }
      else
      {
        System.out.println("Can't read object from space, no OID!!");
      }
    }
    catch (CorsoException ex)
    {
      System.out.println("Corso Error: Reading LevelData object from space:");
      ex.printStackTrace(System.out);
    }

  }
  /**
    * Default constructor for a newly created level data object
    * Read the object information from the LevelsManager
    * @param levelManager LevelsManager
   */
  public void LoadData(LevelsManager levelManager)
  {
    levelDir = levelManager.getCurrentLevelDir();
    checkSum = levelManager.getLevelHash();
    levelName = levelManager.getCurrentLevelName();
    backgroundImg = ReadFile(levelManager.getBackPicturePath());
    backgroundMapImg = ReadFile(levelManager.getBackDefinitionPath());
    thumbnailFilenameImg = ReadFile(levelManager.getThumbnailPath());
    startPosPropsFilename = ReadFile(levelManager.getStartPosPropsPath());

  }

  /**
   * Store the data to the local filesystem
   *
   * @param levelManager LevelsManager
   */
  public void SaveData(LevelsManager levelManager)
  {
    levelManager.addLevelFromSpace(levelDir);
    SaveFile(backgroundImg, levelManager.getBackPicturePath());
    SaveFile(backgroundMapImg, levelManager.getBackDefinitionPath());
    SaveFile(thumbnailFilenameImg, levelManager.getThumbnailPath());
    SaveFile(startPosPropsFilename, levelManager.getStartPosPropsPath());
  }

  /**
   * Read file data to byte array
   *
   * @param fileName String
   * @return byte[]
   */
  private byte[] ReadFile(String fileName)
  {
    byte[] buffer = null;
    try
    {
      java.io.File file = new java.io.File(fileName);
      FileInputStream reader = new FileInputStream(fileName);
      buffer = new byte[ (int) file.length()];
      reader.read(buffer, 0, (int) file.length());
      reader.close();

    }
    catch (Exception err)
    {
      System.out.println(fileName + " can't read");
    }
    return buffer;
  }

  /**
   * Save byte array as file
   *
   * @param binaryData byte[]
   * @param fileName String
   */
  private void SaveFile(byte[] binaryData, String fileName)
  {
    try
    {
      FileOutputStream writer = new FileOutputStream(fileName);
      writer.write(binaryData, 0, binaryData.length);
      writer.flush();
      writer.close();

    }
    catch (Exception err)
    {
      System.out.println(fileName + " can't read");
    }
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
    levelDir = data.getString();
    levelName = data.getString();
    checkSum = data.getBinary();
    backgroundImg = data.getBinary();
    backgroundMapImg = data.getBinary();
    thumbnailFilenameImg = data.getBinary();
    startPosPropsFilename = data.getBinary();

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
    data.putString(levelDir);
    data.putString(levelName);
    data.putBinary(checkSum);
    data.putBinary(backgroundImg);
    data.putBinary(backgroundMapImg);
    data.putBinary(thumbnailFilenameImg);
    data.putBinary(startPosPropsFilename);

  }
}
