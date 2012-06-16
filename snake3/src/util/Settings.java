package util;

import java.util.Properties;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

/**
 * Settings for corso connection, player name, snake skin and standard playtime or points.
 * Also methods are provided to save the settings to and load the settings from a properties file.
 * @author Thomas Scheller, Markus Karolus
 */
public class Settings
{
	//corso connection settings
	private String server = "localhost";
	private int port = 4242;

	private String playerName = "Player"; //name of the player in game
	private int playTime = 60; //playtime for time-playmode
	private int playPoints = 20; //points for points-playmode
	private String snakeSkin = "Snake"; //name of the used snake skin

	private Properties props = new Properties();
	private final String filePath = "res/snake.properties"; //name of properties file
	private String connectionUsername;
	private String connectionPassword;

	public Settings()
	{
	}

	/**
	 * Load settings from properties file.
	 */
	public void load()
	{
		FileInputStream in = null;
		try
		{
			props = new Properties();
			//InputStream in = ClassLoader.getSystemResourceAsStream(filePath);
			in = new FileInputStream(filePath);
			props.load(in);

//			local = props.getProperty("local");
//			local_port = Integer.parseInt(props.getProperty("local_port"));
			server = props.getProperty("server");
			port = Integer.parseInt(props.getProperty("port"));
			playerName = props.getProperty("playername");
			snakeSkin = props.getProperty("snakeskin");

			in.close();
		}
		catch (Exception ex)
		{
			if (in == null)
			{
				System.out.println("Cannot find config file: snake.properties");
			}
			System.out.println(
					"Error occured: settings can't be loaded! set to default settings.");
			ex.printStackTrace(System.out);
			reset();
			save();
		}
	}

	/**
	 * Save settings to propierties file.
	 */
	public void save()
	{
		try
		{

			FileOutputStream out = new FileOutputStream(filePath);
			//Properties props = new Properties();
//			props.setProperty("local",local);
//			props.setProperty("local_port",String.valueOf(local_port));
			props.setProperty("server",server);
			props.setProperty("port",String.valueOf(port));
//			props.setProperty("coke_user",username);
//			props.setProperty("coke_pass",password);
			props.setProperty("playername",playerName);
			props.setProperty("snakeskin",snakeSkin);

			props.store(out, "Snake Properties");
			out.flush();
			out.close();
		}
		catch (Exception ex)
		{
			System.out.println("Error occured: Can't save settings!");
			ex.printStackTrace(System.out);
		}
	}

	/**
	 * Reset all settings to default.
	 */
	private void reset()
	{
//		local = "localhost";
//		local_port = 8282;
		server = "localhost";
		port = 4242;
//		username = "corsouser";
//		password = "corsopass";
		connectionUsername = "";
		connectionPassword = "";
		playerName = "Player";
		playTime = 60;
		playPoints = 20;
		snakeSkin = "Snake";
	}

	/*
	public void setCokeSiteLocal(String value)
	{
		local = value;
	}

	public String getCokeSiteLocal()
	{
		return local;
	}
*/
	public void setServer(String value)
	{
		server = value;
	}

	public String getCokeSiteServer()
	{
		return server;
	}


	public int getPort() {
		return port;
	}


	public void setUsername(String value)
	{
		connectionUsername = value;
	}

	public String getUsername()
	{
		return connectionUsername;
	}

	public void setPassword(String value)
	{
		connectionPassword = value;
	}

	public String getPassword()
	{
		return connectionPassword;
	}

	public void setPlayerName(String value)
	{
		playerName = value;
	}

	public String getPlayerName()
	{
		return playerName;
	}

	public void setPlayTime(int value)
	{
		playTime = value;
	}

	public int getPlayTime()
	{
		return playTime;
	}

	public void setPlayPoints(int value)
	{
		playPoints = value;
	}

	public int getPlayPoints()
	{
		return playPoints;
	}

	public void setSnakeSkin(String value)
	{
		snakeSkin = value;
	}

	public String getSnakeSkin()
	{
		return snakeSkin;
	}

	public URI getUri() {
		try {
			return new URI("xvsm://"+ server +":" + getPort());
		} catch (URISyntaxException e) {
			try {
				return new URI("xvsm://localhost:4242");
			} catch (URISyntaxException e1) {
				return null;
			}
		}
	}
}
