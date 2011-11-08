package snake.corso;

import java.net.URI;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.CapiUtil;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsCore;

import snake.*;

/**
 * Manages the connection to the xvsm space.
 * Reads the settings from the file
 * @author Jakob Lahmer, Matthias Steinb�ck based on work by Thomas Scheller, Markus Karolus
 */
public class Util
{
	private static final String spaceName = "snakeSpace"; //starting point name in the space
	private static Capi conn;
	private static MzsCore core;
	private static Settings settings;

	/**
	 * Store the settings from the propertyfile into the local variables
	 * @param newSettings Settings
	 */
	public static void init(Settings settings)
	{
		Util.settings = settings;
		core = DefaultMzsCore.newInstance();
	}

	/**
	 * Connect to "local" corso_coke with the parameters defined in this class.
	 * If an error occurs we throw an Exception.
	 *
	 * @return the connection to our corso_coke
	 */
	public static Capi getConnection() throws Exception
	{
		if (conn == null)
		{
			try
			{
				Snake.snakeLog.writeLogEntry("Trying to connect.. ");
				conn = new Capi(core);
				Snake.snakeLog.writeLogEntry("connected");
			}
			catch (Exception e)
			{
				Snake.snakeLog.writeLogEntry("Can't connect to space");
				throw new Exception("CorsoUtil: Error connecting to space " +
						e.getMessage());
			}
		}
		return conn;
	}

	/**
	 * Disconnect from the "local" corso_coke
	 */
	public static void Disconnect()
	{
		try
		{
			if (conn != null)
			{
				Snake.snakeLog.writeLogEntry("Disconnected from Local Space");
			}
		}
		catch (Exception e)
		{

			System.err.println("CorsoUtil: Disconnect from Local Space: " + e.getMessage());
		}
	}

	public static URI getSpaceUri() {
		return Util.settings.getUri();
	}

	public static ContainerReference getContainer(String containerName) {
	return CapiUtil.lookupOrCreateContainer(containerName, getSpaceUri(), ContainerCoordinatorMapper.getCoordinators(containerName), null, conn);
	}
}