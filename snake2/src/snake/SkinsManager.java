package snake;

import java.util.Vector;
import java.io.*;

/**
 * Manages the skins for the snake: loads skins from disk, validates them and saves
 * them in a list, and provides methods to set the current skin and access the files
 * of the current skin. A skin consists of: images for snake head, snake tail, snake
 * part (all parts between head and tail), collectables, special collectables
 * (speedup and doublepoints), and sounds for eat, crash and die.
 * @author Thomas Scheller, Markus Karolus
 */
public class SkinsManager
{
	//filenames and folder for skins
	private final String skinPath = "skins\\";
	private final String snakeHeadFilename = "snakehead.png";
	private final String snakePartFilename = "snakepart.png";
	private final String snakeTailFilename = "snaketail.png";
	private final String collectableFilename = "collectable.png";
	private final String speedUpFilename = "speedup.png";
	private final String doublePointsFilename = "doublepoints.png";
	private final String eatSoundFilename = "eat.wav";
	private final String crashSoundFilename = "crash.wav";
	private final String dieSoundFilename = "die.wav";
	private final String defaultSkinname = "Snake";

	private String absolutePath = null;
	private String[] skinList;
	private String currentSkinName = null;
	private int currentSkinIndex = -1;

	/**
	 * Create a new SkinsManager: create the list of skins by reading all folders
	 * from the skins folder that contain all files needed for a valid skin.
	 */
	public SkinsManager()
	{
		//read all subfolders in skin folder
		File skinFolder = new File(skinPath);
		absolutePath = skinFolder.getAbsolutePath();
		File[] skinFiles = skinFolder.listFiles();
		Vector<String> skins = new Vector<String>();

		for (int i = 0; i < skinFiles.length; i++)
		{
			if (skinFiles[i].isDirectory() && skinIsCorrect(skinFiles[i]))
			{
				skins.addElement(skinFiles[i].getName());
			}
		}

		//save skin names in string array
		skinList = new String[skins.size()];
		for (int i = 0; i < skinList.length; i++)
		{
			skinList[i] = (String)skins.elementAt(i);
		}

		//set default skin as current skin
		currentSkinName = defaultSkinname;
		currentSkinIndex = getSkinIndex(defaultSkinname);
	}

	/**
	 * Check if a skin is valid, by checking if all needed files exist.
	 * @param skinFile folder of the skin
	 * @return true if the skin is valid
	 */
	private boolean skinIsCorrect(File skinFile)
	{
		//Pr�fen ob alle notwendigen Files f�r den Skin vorhanden sind
		String[] list = skinFile.list();
		boolean correct = fileExists(list,snakeHeadFilename) &&
		fileExists(list,snakePartFilename) &&
		fileExists(list,snakeTailFilename) &&
		fileExists(list,collectableFilename) &&
		fileExists(list,speedUpFilename) &&
		fileExists(list,doublePointsFilename) &&
		fileExists(list,eatSoundFilename) &&
		fileExists(list,crashSoundFilename) &&
		fileExists(list,dieSoundFilename);
		//if (!correct)
		//  System.out.println("Nicht korrekt angelegter Skin wurde gefunden: " + skinFile.getAbsoluteFile());
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
			if (files[i].equals(searchFile))
				return true;
		}
		return false;
	}

	/**
	 * Set the skin with the given name as the current level. Choose default skin,
	 * if no skin with this name is available.
	 * @param skinname name of the skin
	 */
	public void setCurrentSkin(String skinname)
	{
		int index = getSkinIndex(skinname);
		if (index > -1)
		{
			currentSkinName = skinname;
			currentSkinIndex = getSkinIndex(skinname);
		}
		else
		{
			//use default skin if skin not found
			System.out.println("SkinManager: Skin \"" + skinname + "\" can't be found. using default skin.");
			currentSkinIndex = getSkinIndex(defaultSkinname);
			currentSkinName = defaultSkinname;
		}
	}

	/**
	 * Get the index of the skin with the given name.
	 * @param skinname name of the skin
	 * @return index of the skin, or -1 if no skin is found
	 */
	private int getSkinIndex(String skinname)
	{
		for (int i = 0; i < skinList.length; i++)
		{
			if (skinList[i].equals(skinname))
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * Get the path of the snake head image of the current skin.
	 * @return snake head image path
	 */
	public String getSnakeHeadPath()
	{
		if (currentSkinIndex == -1)
			return null;
		else
			return absolutePath + "\\" + skinList[currentSkinIndex] + "\\" + snakeHeadFilename;
	}

	/**
	 * Get the path of the snake part image of the current skin.
	 * @return snake part image path
	 */
	public String getSnakePartPath()
	{
		if (currentSkinIndex == -1)
			return null;
		else
			return absolutePath + "\\" + skinList[currentSkinIndex] + "\\" + snakePartFilename;
	}

	/**
	 * Get the path of the snake tail image of the current skin.
	 * @return snake tail image path
	 */
	public String getSnakeTailPath()
	{
		if (currentSkinIndex == -1)
			return null;
		else
			return absolutePath + "\\" + skinList[currentSkinIndex] + "\\" + snakeTailFilename;
	}

	/**
	 * Get the path of the collectable image of the current skin.
	 * @return collectable image path
	 */
	public String getCollectablePath()
	{
		if (currentSkinIndex == -1)
			return null;
		else
			return absolutePath + "\\" + skinList[currentSkinIndex] + "\\" + collectableFilename;
	}

	/**
	 * Get the path of the speedup collectable image of the current skin.
	 * @return speedup collectable image path
	 */
	public String getSpeedUpPath()
	{
		if (currentSkinIndex == -1)
			return null;
		else
			return absolutePath + "\\" + skinList[currentSkinIndex] + "\\" + speedUpFilename;
	}

	/**
	 * Get the path of the doublepoints collectable image of the current skin.
	 * @return doublepoints collectable image path
	 */
	public String getDoublePointsPath()
	{
		if (currentSkinIndex == -1)
			return null;
		else
			return absolutePath + "\\" + skinList[currentSkinIndex] + "\\" + doublePointsFilename;
	}

	/**
	 * Get the path of the eat sound file of the current skin. (played when snake eats a
	 * collectable)
	 * @return eat sound file path
	 */
	public String getEatSoundPath()
	{
		if (currentSkinIndex == -1)
			return null;
		else
			return absolutePath + "\\" + skinList[currentSkinIndex] + "\\" + eatSoundFilename;
	}

	/**
	 * Get the path of the crash sound file of the current skin. (played when snake crashes
	 * with another snake)
	 * @return crash sound file path
	 */
	public String getCrashSoundPath()
	{
		if (currentSkinIndex == -1)
			return null;
		else
			return absolutePath + "\\" + skinList[currentSkinIndex] + "\\" + crashSoundFilename;
	}

	/**
	 * Get the path of the die sound file of the current skin. (played when snake dies because
	 * it crashed into a wall)
	 * @return die sound file path
	 */
	public String getDieSoundPath()
	{
		if (currentSkinIndex == -1)
			return null;
		else
			return absolutePath + "\\" + skinList[currentSkinIndex] + "\\" + dieSoundFilename;
	}


	public String[] getSkinList()
	{
		return skinList;
	}

	public String getCurrentSkinName()
	{
		return currentSkinName;
	}

	public int getCurrentSkinIndex()
	{
		return currentSkinIndex;
	}

	/**
	 * Get a ClipsLoader object that contains the eat, crash and die sounds of the current skin.
	 * @return ClipsLoader for the sounds of the current skin, or null if there is no current skin
	 */
	public snake.util.ClipsLoader getClipsLoader()
	{
		if (currentSkinIndex == -1)
			return null;

		snake.util.ClipsLoader clipsLoader = new snake.util.ClipsLoader();
		clipsLoader.load("eat",getEatSoundPath());
		clipsLoader.load("crash",getCrashSoundPath());
		clipsLoader.load("die",getDieSoundPath());
		return clipsLoader;
	}
}
