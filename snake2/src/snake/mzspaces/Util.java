package snake.mzspaces;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;

import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.capi3.CountNotMetException;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.KeyCoordinator;
import org.mozartspaces.capi3.KeyCoordinator.KeyData;
import org.mozartspaces.capi3.KeyCoordinator.KeySelector;
import org.mozartspaces.capi3.Selector;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.CapiUtil;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.TransactionReference;
import org.mozartspaces.core.MzsConstants.Container;
import org.mozartspaces.core.MzsConstants.RequestTimeout;
import org.mozartspaces.notifications.NotificationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import snake.*;
import snake.data.Game;

/**
 * Manages the connection to the xvsm space.
 * Reads the settings from the file
 * @author Jakob Lahmer, Matthias Steinboeck based on work by Thomas Scheller, Markus Karolus
 */
public class Util
{
	private static Util instance;
	private static boolean server;
	private final String spaceName = "snakeSpace"; //starting point name in the space
	private Capi conn;
	private MzsCore core;
	private Settings settings;
	private NotificationManager notificationManager;

	private Logger log = LoggerFactory.getLogger(Util.class);
	private URI space;
	
	private Util() {
		try
		{
			//read the properties from the config file
			settings = new Settings();
			settings.load();
			log.debug("Settings loaded ");
//			log.debug("		local site:	" + settings.getCokeSiteLocal());
			log.debug("		server site:	" + settings.getCokeSiteServer());
			log.debug("		port:			" + settings.getPort());
			log.debug("		playername:		" + settings.getPlayerName());
//			log.debug("		xvsm user:		" + settings.getUsername());
			log.debug("");

			// set space
			space = this.getSettings().getUri();
			if (server) {
				core = DefaultMzsCore.newInstance();
			} else {
				/*
				Configuration config = new Configuration();
				config.setSpaceUri(this.getSettings().getUri(server));
				config.setEmbeddedSpace(true);
				config.setXpThreadNumber(-1);
				*/
				core = DefaultMzsCore.newInstance();
			}
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
				log.debug("Trying to connect.. ");
				conn = new Capi(core);
				log.debug("connected");
			}
			catch (Exception e)
			{
				log.debug("Can't connect to space");
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
				log.debug("Disconnected from Local Space");
			}
		}
		catch (Exception e)
		{

			System.err.println("CorsoUtil: Disconnect from Local Space: " + e.getMessage());
		}
	}

	
	public URI getSpaceUri() {
		return space;
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
    	System.out.println("getting container "+containerName+" "+ this.getSpaceUri());
		try {
			return CapiUtil.lookupOrCreateContainer(
					containerName,
					this.getSpaceUri(),
					ContainerCoordinatorMapper.getCoordinators(containerName),
					null,
					getConnection());
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
	
	/**
	 * creates/returns a container for a certain game. in every game-container, the players and their current position is held 
	 * @param game
	 * @return
	 */
	public ContainerReference getGameContainer(Game game) {
		System.out.println("getting game container "+game.getNr()+" on "+this.getSpaceUri());
		String containerName = "game-"+game.getNr();
		try {
			return CapiUtil.lookupOrCreateContainer(
					containerName,
					this.getSpaceUri(),
					ContainerCoordinatorMapper.getCoordinators(ContainerCoordinatorMapper.GAME),
					null,
					getConnection());
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

    
    public ContainerReference forceCreateContainer(String containerName) throws MzsCoreException	{
    	System.out.println("force-creating container "+containerName+" "+ this.getSpaceUri()); //settings.getUri(server));
        ContainerReference cref = null;
    	
    	// make sure container does not exist (destroy if available)
    	try	{
    		getConnection().destroyContainer(getConnection().lookupContainer(containerName, this.getSpaceUri(), 0, null), null);
    	} catch(Exception s)	{
			// TODO Auto-generated catch block
//			s.printStackTrace();
    	} finally {
	    	try {
				cref = getConnection().createContainer(
						containerName, 
						this.getSpaceUri(),
						Container.UNBOUNDED, 
						ContainerCoordinatorMapper.getCoordinators(containerName),
						null,
						null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        System.out.println("Container " + containerName + " created");
    	}
		return cref;
    }

    public static Util getInstance() {
    	return Util.getInstance(false);
    }
    
	public static Util getInstance(boolean is_server) {
		if (instance == null) {
			server = is_server;
			instance = new Util();
		}
		return instance;
	}

	public Settings getSettings() {
		return settings;
	}

	public TransactionReference createTransaction() {
		try {
			return getConnection().createTransaction(RequestTimeout.INFINITE, getSpaceUri());
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			log.error("Could not create transaction (mzexception)");
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Could not create transaction (java exception)");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public TransactionReference createTransaction(int timeoutInMilliseconds) {
		try {
			return getConnection().createTransaction(timeoutInMilliseconds, getSpaceUri());
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			log.error("Could not create transaction (mzexception)");
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Could not create transaction (java exception)");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * deletes an object from the given container identified by its key
	 * 
	 * @param containerName the containers name found in the {@link ContainerCoordinatorMapper}
	 * @param key the key that is used in KeyCoordinator.newSelector
	 */
	public void delete(String containerName, String key) {
		ArrayList<Selector> selectors = new ArrayList<Selector>();
		selectors.add(KeyCoordinator.newSelector(
				key,
				MzsConstants.Selecting.COUNT_ALL)
		);
		delete(containerName, selectors, null);
	}

	/**
	 * use this delete-method if you have a list of selectors,
	 * BUT normally you'd use the delete-method that uses a key
	 *
	 * @param containerName the containers name found in the {@link ContainerCoordinatorMapper} 
	 * @param selectors a list of selectors that identify the element that should be deleted
	 */
	public void delete(String containerName, ArrayList<Selector> selectors) {
		delete(containerName, selectors, null);
	}

	/**
	 * @see Util.delete only with transaction
	 *
	 * @param containerName the containers name found in the {@link ContainerCoordinatorMapper}
	 * @param selectors a list of selectors that identify the element that should be deleted
	 * @param tx transaction
	 */
	public void delete(String containerName, ArrayList<Selector> selectors,
			TransactionReference tx) {
		ContainerReference container = getContainer(containerName);
		try {
			conn.delete(container, selectors, MzsConstants.RequestTimeout.ZERO, tx);
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			log.error("Could not delete from container "+container.getId()+" (mzexception)");
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Could not delete from container "+container.getId()+" (java exception)");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * updates an object in the given container using a key coordinator
	 *
	 * @param containerName the containers name found in the {@link ContainerCoordinatorMapper}
	 * @param data the data that should be updated
	 * @param key a key identifying the object in the container
	 */
	public void update(String containerName, Serializable data, String key) {
		update(
			containerName,
			data,
			KeyCoordinator.newSelector(key),
			KeyCoordinator.newCoordinationData(key)
		);
	}

	/**
	 * updates an object in the given container using the provided selector to take and key to write it.
	 * that all happens in a new transaction.
	 * BUT normally you'd use the update-method that uses a key
	 *
	 * @param containerName the containers name found in the {@link ContainerCoordinatorMapper}
	 * @param data the data that should be updated
	 * @param selector selector used for take
	 * @param key keydata used to write
	 */
	public void update(String containerName, Serializable data, KeySelector selector, KeyData key) {
		ContainerReference container = getContainer(containerName);
		TransactionReference tx = createTransaction();
		try {
			ArrayList<Selector> selectors = new ArrayList<Selector>();
			selectors.add(selector);

			// take it to force no ui update
			try {
			Util.getInstance().getConnection().take(
					container,
					selectors,
					MzsConstants.RequestTimeout.ZERO, 
					tx);
			} catch (CountNotMetException e) {
				// ignore
			}
			
			Util.getInstance().getConnection().write(
					container,
					MzsConstants.RequestTimeout.ZERO,
					tx,
					new Entry(data, key)
			);

			Util.getInstance().getConnection().commitTransaction(tx);
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				Util.getInstance().getConnection().rollbackTransaction(tx);
			} catch (MzsCoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				Util.getInstance().getConnection().rollbackTransaction(tx);
			} catch (MzsCoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}

