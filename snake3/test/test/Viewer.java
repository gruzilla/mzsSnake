package test;

import java.io.Serializable;
import java.util.List;

import mzs.data.SnakeDataHolder;
import mzs.util.ContainerCoordinatorMapper;
import mzs.util.Util;

import org.mozartspaces.capi3.CoordinationData;
import org.mozartspaces.capi3.KeyCoordinator;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.data.Snake;
import client.gui.GameFrame;


/**
 * 
 * Viewer, that displays the drawn snake
 * 
 * @author Jakob Lahmer, Matthias Steinbšck
 *
 */
public class Viewer implements NotificationListener {

	private Notification notification;

	private GameFrame gameFrame;
	
	private static Logger log = LoggerFactory.getLogger(Viewer.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Viewer();
	}

	public Viewer()	{
		
		// create notification
		NotificationManager notifManager = Util.getInstance().getNotificationManager();
		try {
			this.notification = notifManager.createNotification(
					Util.getInstance().getContainer(ContainerCoordinatorMapper.GAME_LIST),
					this,
					Operation.WRITE);
			
		} catch (MzsCoreException e) {
			log.error("ERROR: could not create notification (mzsexception)");
			e.printStackTrace();
		} catch (InterruptedException e) {
			log.error("ERROR: could not create notification (interrupted)");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (notification == null) {
				log.error("ERROR: could not create notification (null after creation)");
			} else {
				log.debug("NOTFICATION successfully created");
			}
		}
		
		gameFrame = new GameFrame(true);
	}
	
	
	@Override
	public void entryOperationFinished(Notification notification, Operation operation,
			List<? extends Serializable> entries) {
		
		
		if (entries != null && operation == Operation.WRITE)	{
			for (Serializable entry : entries) {
				Entry e = ((Entry) entry);
				/* how to get the key of keycoordinator *
				KeyCoordinator.KeyData kdata = null;
				for(CoordinationData cdata : e.getCoordinationData())	{
					if(cdata instanceof KeyCoordinator.KeyData)	{
						kdata = ((KeyCoordinator.KeyData) cdata);
					}
				}
				log.debug(kdata.getName() + " " + kdata.getKey());
				 */
				
				Serializable obj = ((Entry) entry).getValue();
				
//				log.debug(e.getCoordinationData().toString());
				
				if (obj instanceof SnakeDataHolder) {
					SnakeDataHolder snakedataholder = (SnakeDataHolder) obj;
//					log.debug("received: " + snakedataholder);
					
					/**
					 * @TODO check if update comes from own snake
					 * 	=> if so, ignore it (can not be done in viewer)
					 */
					
					
					// how to get the key easier... lulz
					if(!gameFrame.hasSnake(snakedataholder.getId()))	{
						// create Snake (temporarly here - i think)
						gameFrame.addSnake(new Snake(snakedataholder.getId()));
					}
					// update snake
					this.gameFrame.updateSnake(snakedataholder);
				}
			}
		}
	}

}
