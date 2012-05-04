package snake;

import java.util.Vector;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import snake.data.LevelInfo;
import snake.ui.FLevelSelection;

/**
 * Manages the list of all available levels: Loads levels from disk and checks if
 * they are valid, provides methods to get the paths to all necessary files for a
 * level, a current level can be set.
 * @author Thomas Scheller, Markus Karolus
 */
public class LevelsManager
{
	private final String levelPath = "levels" + File.separator;
	private final String backPictureFilename = "back.jpg"; //background picture file
	private final String backDefinitionFilename = "back.gif"; //background obstacles definition file
	private final String thumbnailFilename = "thumb.jpg"; //thumbnail file
	private final String startPosPropsFilename = "back.properties"; //level settings file (name, player start points, ...)

	public static final String DEFAULTLEVELDIR = "Level1"; //directory of the def

	private String absolutePath = null;
	private LevelInfo[] levelList;
	private int currentLevelIndex = -1;


	private Logger log = LoggerFactory.getLogger(LevelsManager.class);

	public LevelsManager()
	{

		//read all subfolders in the levels folder, save all that contain correct leveldata
		File levelFolder = new File(levelPath);
		absolutePath = levelFolder.getAbsolutePath();
		File[] levelFiles = levelFolder.listFiles();
		Vector<String> levelDirs = new Vector<String>();
		for (int i = 0; i < levelFiles.length; i++)
		{
			if (levelFiles[i].isDirectory() && levelIsCorrect(levelFiles[i]))
			{
				levelDirs.addElement(levelFiles[i].getName());
			}
		}

		//save levels in array as LevelInfo objects
		levelList = new LevelInfo[levelDirs.size()];
		for (int i = 0; i < levelList.length; i++)
		{
			levelList[i] = new LevelInfo( (String) levelDirs.elementAt(i));
			levelList[i].loadLevelName(absolutePath, startPosPropsFilename);
		}

		//choose default level as currently set level
		currentLevelIndex = getLevelIndex(DEFAULTLEVELDIR);
	}

	/**
	 * Get the name of the current level.
	 * @return name of the current level
	 */
	public String getCurrentLevelName()
	{
		if (currentLevelIndex >= 0 && currentLevelIndex < levelList.length)
		{
			return levelList[currentLevelIndex].getName();
		}
		return "<unknown>";
	}

	/**
	 * Get the directory of the current level.
	 * @return directory of the current level
	 */
	public String getCurrentLevelDir()
	{
		if (currentLevelIndex >= 0 && currentLevelIndex < levelList.length)
		{
			return levelList[currentLevelIndex].getDirName();
		}
		return "<unknown>";
	}

	/**
	 * Add a new level (that has been read from corsospace) to the list of levels,
	 * create the folder for the new level and choose it as the current level.
	 * @param newLevelName name of the new level
	 */
	public void addLevelFromSpace(String newLevelName)
	{
		//In Array speichern
		LevelInfo[] newLevelList = new LevelInfo[levelList.length + 1];
		for (int i = 0; i < levelList.length; i++)
		{
			newLevelList[i] = levelList[i];
		}
		try
		{
			File newLevelFolder = new File(absolutePath + File.separator + newLevelName);
			newLevelFolder.mkdir();
		}
		catch (Exception err)
		{
			System.out.println("Directory can't be created");
		}
		newLevelList[newLevelList.length - 1] = new LevelInfo(newLevelName);
		newLevelList[newLevelList.length - 1].loadLevelName(absolutePath, startPosPropsFilename);
		levelList = newLevelList;
		currentLevelIndex = levelList.length - 1;
	}

	/**
	 * Check if all files exist in the folder that are needed for a valid level.
	 * @param levelFile folder to check
	 * @return true if folder contains a valid level
	 */
	private boolean levelIsCorrect(File levelFile)
	{
		String[] list = levelFile.list();
		boolean correct = fileExists(list, backPictureFilename) &&
		fileExists(list, backDefinitionFilename) &&
		fileExists(list, thumbnailFilename) &&
		fileExists(list, startPosPropsFilename);
		//if (!correct)
			//  System.out.println("Nicht korrekt angelegter Level wurde gefunden: " + skinFile.getAbsoluteFile());
		return correct;
	}

	/**
	 * Check if a file exists in a list of files, ignoring the case.
	 * @param files list if filenames
	 * @param searchFile file to search
	 * @return true if filename exists in the list
	 */
	private boolean fileExists(String[] files, String searchFile)
	{
		for (int i = 0; i < files.length; i++)
		{
			if (files[i].equalsIgnoreCase(searchFile))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Set the level with the given name as the current level. Choose default level,
	 * if no level with this name is available.
	 * @param levelname name of the level
	 */
	public void setCurrentLevel(String levelname)
	{
		String dirname = getLevelDir(levelname);
		int index = getLevelIndex(dirname);
		if (index > -1)
		{
			setCurrentLevel(index);
		}
		else
		{
			//level not found, choose default level
			System.out.println("LevelManager: Level \"" + levelname +
			"\" can't be found. Using Default Level.");
			currentLevelIndex = getLevelIndex(DEFAULTLEVELDIR);
		}
	}

	/**
	 * Set the level with the given index as the current level.
	 * @param newIndex index of the level in the list
	 */
	public void setCurrentLevel(int newIndex)
	{
		if (newIndex >= 0 && newIndex < levelList.length)
		{
			currentLevelIndex = newIndex;
		}
	}

	/**
	 * Get the index of the level with the given foldername.
	 * @param levelDir foldername of the level
	 * @return index of the level
	 */
	private int getLevelIndex(String levelDir)
	{
		//Index des Levels in der Levelliste suchen
		for (int i = 0; i < levelList.length; i++)
		{
			if (levelList[i].getDirName().equals(levelDir))
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * converts levelname to leveldir
	 * @param levelname
	 * @return
	 */
	private String getLevelDir(String levelname) {

		for (int i = 0; i < levelList.length; i++){
			if (levelList[i].getName().equals(levelname))
			{
				return levelList[i].getDirName();
			}
		}
		return null;
	}

	/**
	 * Check if a level with the given name exists.
	 * @param levelname name to check
	 * @return true if name exists
	 */
	public boolean levelExists(String levelname)
	{
		return (getLevelIndex(levelname) > -1);
	}

	/**
	 * Get the background picture path for the current level.
	 * @return path of current background picture
	 */
	public String getBackPicturePath()
	{
		if (currentLevelIndex == -1)
		{
			return null;
		}
		else
		{
			return absolutePath + File.separator + levelList[currentLevelIndex].getDirName() + File.separator + backPictureFilename;
		}
	}

	/**
	 * Get the background definition path for the current level (a gif image in black and white defining the
	 * obstacles of the level).
	 * @return path of current background definition
	 */
	public String getBackDefinitionPath()
	{
		if (currentLevelIndex == -1)
		{
			return null;
		}
		else
		{
			return absolutePath + File.separator + levelList[currentLevelIndex].getDirName() + File.separator + backDefinitionFilename;
		}
	}

	/**
	 * Get the thumbnail path for the current level.
	 * @return path of current thumbnail
	 */
	public String getThumbnailPath()
	{
		if (currentLevelIndex == -1)
		{
			return null;
		}
		else
		{
			return absolutePath + File.separator + levelList[currentLevelIndex].getDirName() + File.separator + thumbnailFilename;
		}
	}

	/**
	 * Get the properties path for the current level.
	 * @return path of current properties file
	 */
	public String getStartPosPropsPath()
	{
		if (currentLevelIndex == -1)
		{
			return null;
		}
		else
		{
			return absolutePath + File.separator + levelList[currentLevelIndex].getDirName() + File.separator + startPosPropsFilename;
		}
	}

	/**
	 * Get a list of all level names.
	 * @return String Array containing names of all levels
	 */
	public String[] getLevelList()
	{
		String[] levelNames = new String[levelList.length];
		for (int i = 0; i < levelList.length; i++)
		{
			levelNames[i] = levelList[i].getName();
		}
		return levelNames;

	}

	/**
	 * Get the index of the current level.
	 * @return index of the current level
	 */
	public int getCurrentLevelIndex()
	{
		return currentLevelIndex;
	}

	/**
	 * Get a Hash value for the current level, by hashing all files of the level. This
	 * is to compare a level on different clients is really the same.
	 * @return hash value as byte array
	 */
	public byte[] getLevelHash()
	{
		try
		{
			String[] fileHashList = new String[4];
			fileHashList[0] = getStartPosPropsPath();
			fileHashList[1] = getBackPicturePath();
			fileHashList[2] = getThumbnailPath();
			fileHashList[3] = getBackDefinitionPath();

			return snake.util.Hash.getDigest(fileHashList);
		}
		catch (Exception ex)
		{
			return null;
		}
	}
}
