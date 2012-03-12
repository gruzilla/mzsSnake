/**
 * Server.java
 * created by Jakob Lahmer, Matthias Steinböck (11.03.2012)
 * 
 * All rights reserved.
 */
package snakeserver;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsCoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class Server {

	private static Logger log = LoggerFactory.getLogger(snakeserver.Server.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Server();
	}


	private DefaultMzsCore core;
	private Capi capi;


	/**
	 * 
	 */
	public Server() {
		
		log.debug("starting Server...");
		
		this.initXVSMSpace();
		this.initXVSMContainers();
	}


	/**
	 * create containers here
	 */
	private void initXVSMContainers() {

		try {
			// force creation?
			ContainerReference gamesContainerRef = Util.getOrCreateNamedContainer(core.getConfig().getSpaceUri(), "snake.gamesContainer", capi);
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	/**
	 * 
	 */
	private void initXVSMSpace() {
		// TODO Auto-generated method stub
		core = DefaultMzsCore.newInstance();
		capi = new Capi(core);
	}
}






