/**
 * Server.java
 * created by Jakob Lahmer, Matthias Steinböck (11.03.2012)
 * 
 * All rights reserved.
 */
package server;


import mzs.util.ContainerCoordinatorMapper;
import mzs.util.Util;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.MzsCoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * server class
 */
public class Server {

	private static Logger log = LoggerFactory.getLogger(server.Server.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Server();
	}


	private Capi conn;

	/**
	 * 
	 */
	public Server() {
		
		log.debug("starting Server...");
		
		try {
			// init util to use server vars
			conn = Util.getInstance(true).getConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			
			Util.getInstance().forceCreateContainer(ContainerCoordinatorMapper.GAME_LIST);
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
}






