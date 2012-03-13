package snake.mzspaces;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.CapiUtil;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.TransactionReference;
import org.mozartspaces.core.MzsConstants.Container;
import org.mozartspaces.core.MzsConstants.RequestTimeout;
import org.mozartspaces.core.config.Configuration;
import org.mozartspaces.notifications.NotificationManager;

import snake.*;
import snake.util.SnakeLog;

/**
 * Manages the connection to the xvsm space.
 * Reads the settings from the file
 * @author Jakob Lahmer, Matthias Steinboeck based on work by Thomas Scheller, Markus Karolus
 */
public class Util
{
	private static Util instance;
	private final String spaceName = "snakeSpace"; //starting point name in the space
	private Capi conn;
	private MzsCore core;
	private Settings settings;
	private NotificationManager notificationManager;
	private SnakeLog snakeLog;

	private Util() {
		try
		{
			//read the properties from the config file
			snakeLog = new SnakeLog();
			settings = new Settings();
			settings.load();
			snakeLog.writeLogEntry("Settings loaded ");
			snakeLog.writeLogEntry("		 local site:	" + settings.getCokeSiteLocal());
			snakeLog.writeLogEntry("		 server site: " + settings.getCokeSiteServer());
			snakeLog.writeLogEntry("		 port:			 " + settings.getPort());
//			snakeLog.writeLogEntry("		 xvsm user:			 " + settings.getUsername());
			snakeLog.writeLogEntry("		 domain:		 " + settings.getDomain());
			snakeLog.writeLogEntry("");

			Configuration config = new Configuration();
			config.setSpaceUri(settings.getUri());
			core = DefaultMzsCore.newInstance(config);
		}
		catch (Exception ex)
		{
			System.out.println("Error occured: " + ex.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Connect to "local" XVSM Server with the parameters defined in this class.
	 * If an error occurs we throw an Exception.
	 *
	 * @return the connection to our XVSM Server
	 */
	public Capi getConnection() throws Exception
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
	public void disconnect()
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

	
	public URI getSpaceUri() {
		return settings.getUri();
	}

	
	/**
	 * Loads a ContainerReference for a container name. The Container will be created, if it
	 * is not available
	 * 
	 * @see ContainerCoordinatorMapper use the container-coordinator-mapper for the keys of each container
	 * @param containerName name of the container in space
	 * @return ContainerReference or null
	 */
	public ContainerReference getContainer(String containerName) {
		try {
			return CapiUtil.lookupOrCreateContainer(containerName, getSpaceUri(), ContainerCoordinatorMapper.getCoordinators(containerName), null, getConnection());
		} catch (MzsCoreException e) {
			
			e.printStackTrace();
			System.err.println("Util: Could not load Container (" + containerName + "): " + e.getMessage());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Util: Could not connect to XVSM: " + e.getMessage());
		}
		return null;
	}

	public NotificationManager getNotificationManager() {
		if (notificationManager == null) {
			notificationManager = new NotificationManager(core);
		}
		return notificationManager;
	}
	
	
    /**
     * Gets or creates a named container (unbounded, FIFO coordination).
     *
     * @param space
     *            the space to use
     * @param containerName
     *            the name of the container
     * @param capi
     *            the interface to access the space (Core API)
     * @return the reference of the container
     * @throws MzsCoreException
     *             if getting or creating the container failed
     */
    public ContainerReference getOrCreateNamedContainer(final URI space, final String containerName, final Capi capi) throws MzsCoreException {

        ContainerReference cref;
        try {
            // Get the Container
            System.out.println("Lookup container");
            cref = capi.lookupContainer(containerName, space, RequestTimeout.TRY_ONCE, null);
            System.out.println("Container found");
            // If it is unknown, create it
        } catch (MzsCoreException e) {
            System.out.println("Container not found, creating it ...");
            // Create the Container
            ArrayList<Coordinator> obligatoryCoords = new ArrayList<Coordinator>();
            obligatoryCoords.add(new FifoCoordinator());
            cref = capi.createContainer(containerName, space, Container.UNBOUNDED, obligatoryCoords, null, null);
            System.out.println("Container created");
        }
        return cref;
    }

    
    public ContainerReference forceCreateContainer(String name, final URI space, final Capi capi, int containerSize, List<Coordinator> obligatoryCoords, List<Coordinator> optionalCoords, TransactionReference tx) throws MzsCoreException	{
        ContainerReference cref = null;
    	
    	// make sure container does not exist (destroy if available)
    	try	{
    		capi.destroyContainer(capi.lookupContainer(name, space, 0, null), null);
    	} catch(MzsCoreException s)	{
    	}
    	cref = capi.createContainer(name, 
    			space,
    			containerSize, 
    			obligatoryCoords,
    			optionalCoords,
    			tx);
        System.out.println("Container " + name + " created");
		return cref;
    }

	public static Util getInstance() {
		if (instance == null) {
			instance = new Util();
		}
		return instance;
	}

	public Settings getSettings() {
		return settings;
	}
}

