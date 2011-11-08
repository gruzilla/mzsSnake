package snake;

import java.util.Properties;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Settings for corso connection, player name, snake skin and standard playtime or points.
 * Also methods are provided to save the settings to and load the settings from a properties file.
 * @author Thomas Scheller, Markus Karolus
 */
public class Settings
{
	//corso connection settings
	private String cokeSiteLocal = "localhost";
	private String cokeSiteServer = "localhost";
	private String domain = "localhost";
	private int port = 5006;
	private String username = "corsouser";
	private String password = "corsopass";

	private String playerName = "Player"; //name of the player in game
	private int playTime = 60; //playtime for time-playmode
	private int playPoints = 20; //points for points-playmode
	private String snakeSkin = "Snake"; //name of the used snake skin

	private Properties props = new Properties();
	private final String filePath = "snake.properties"; //name of properties file

	public Settings()
	{
	}

	/**
	 * Load settings from properties file.
	 */
	public void load()
	{
		try
		{
			props = new Properties();
			//InputStream in = ClassLoader.getSystemResourceAsStream(filePath);
			FileInputStream in = new FileInputStream(filePath);
			if (in == null)
			{
				throw new Exception("Cannot find config file: snake.properties");
			}
			props.load(in);

			cokeSiteLocal = props.getProperty("coke_site1");
			cokeSiteServer = props.getProperty("coke_site2");
			username = props.getProperty("coke_user");
			password = props.getProperty("coke_pass");
			domain = props.getProperty("coke_domain");
			port = Integer.parseInt(props.getProperty("coke_port"));
			playerName = props.getProperty("playername");
			snakeSkin = props.getProperty("snakeskin");

			in.close();
		}
		catch (Exception ex)
		{
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
			props.setProperty("coke_site1",cokeSiteLocal);
			props.setProperty("coke_site2",cokeSiteServer);
			props.setProperty("coke_user",username);
			props.setProperty("coke_pass",password);
			props.setProperty("coke_domain",domain);
			props.setProperty("coke_port",String.valueOf(port));
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
		cokeSiteLocal = "localhost";
		cokeSiteServer = "localhost";
		domain = "localhost";
		port = 5006;
		username = "corsouser";
		password = "corsopass";
		playerName = "Player";
		playTime = 60;
		playPoints = 20;
		snakeSkin = "Snake";
	}

	public void setCokeSiteLocal(String value)
	{
		cokeSiteLocal = value;
	}

	public String getCokeSiteLocal()
	{
		return cokeSiteLocal;
	}

	public void setCokeSiteServer(String value)
	{
		cokeSiteServer = value;
	}

	public String getCokeSiteServer()
	{
		return cokeSiteServer;
	}

	public void setDomain(String value)
	{
		domain = value;
	}

	public String getDomain()
	{
		return domain;
	}

	public void setPort(int value)
	{
		port = value;
	}

	public int getPort()
	{
		return port;
	}

	public void setUsername(String value)
	{
		username = value;
	}

	public String getUsername()
	{
		return username;
	}

	public void setPassword(String value)
	{
		password = value;
	}

	public String getPassword()
	{
		return password;
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
			return new URI("xvsm://"+getDomain()+":"+getPort());
		} catch (URISyntaxException e) {
			try {
				return new URI("xvsm://localhost:4321");
			} catch (URISyntaxException e1) {
				return null;
			}
		}
	}
}
