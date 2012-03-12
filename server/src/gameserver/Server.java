/**
 * Server.java
 * created by Jakob Lahmer, Matthias Steinböck (11.03.2012)
 * 
 * All rights reserved.
 */
package gameserver;

import java.util.ArrayList;

import org.mozartspaces.capi3.AnyCoordinator;
import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsConstants.Container;
import org.mozartspaces.core.MzsCoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * server class
 */
public class Server {

	private static Logger log = LoggerFactory.getLogger(gameserver.Server.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Server();
	}


	private DefaultMzsCore core;
	private Capi capi;
	private ContainerReference gamesContainerRef;


	/**
	 * 
	 */
	public Server() {
		
		log.debug("starting Server...");
		
		this.initXVSMSpace();
		this.initXVSMContainers();
		this.initNotificationListeners();
		log.debug("started server successfully!");
	}


	/**
	 * create containers here
	 */
	private void initXVSMContainers() {
		log.debug("creating containers...");
		try {
			// force creation? (Util.force...)
			// gamesContainerRef = Util.getOrCreateNamedContainer(core.getConfig().getSpaceUri(), "snake.gamesContainer", capi);
			
			gamesContainerRef = Util.forceCreateContainer(
					"snake.gamescontainer", //use containercoordinatormapper ? 
					core.getConfig().getSpaceUri(), 
					capi, 
					Container.UNBOUNDED, 
        			new ArrayList<Coordinator>() {{ 
						add(new AnyCoordinator());
						add(new QueryCoordinator());
					}}, 
					null, null);
		} catch (MzsCoreException e) {
			log.debug("error creating containers");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.debug("containers created successfully!");
	}

	/**
	 * inits the needed notification listeners
	 */
	private void initNotificationListeners() {
		
	}

	/**
	 * init the space
	 */
	private void initXVSMSpace() {
		// TODO Auto-generated method stub
		core = DefaultMzsCore.newInstance();
		capi = new Capi(core);
	}
}






