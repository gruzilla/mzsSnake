package test;

import java.io.Serializable;
import java.util.List;

import mzs.data.SnakeDataHolder;
import mzs.util.ContainerCoordinatorMapper;
import mzs.util.Util;

import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * Viewer, that displays the drawn snake
 * 
 * @author Jakob Lahmer, Matthias Steinbšck
 *
 */
public class Viewer implements NotificationListener {

	private Notification notification;
	
	private static Logger log = LoggerFactory.getLogger(Viewer.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Viewer();
	}

	public Viewer()	{
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
	}
	
	
	@Override
	public void entryOperationFinished(Notification notification, Operation operation,
			List<? extends Serializable> entries) {
		log.debug("received notification");
		
		switch (operation) {
		case WRITE:
			
			if (entries != null)	{
				for (Serializable entry : entries) {
					Serializable obj = ((Entry) entry).getValue();
					if (obj instanceof SnakeDataHolder) {
						SnakeDataHolder snake = (SnakeDataHolder) obj;
						log.debug("received: " + snake);
					}
				}
			}
			break;
		}
	}

}
