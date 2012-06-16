package client.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.swing.JFrame;

import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mzs.data.SnakeDataHolder;
import mzs.util.ContainerCoordinatorMapper;
import mzs.util.Util;

import client.data.Snake;

public class GameFrame extends JFrame implements Runnable, KeyListener, NotificationListener {

	private static final long serialVersionUID = 1L;

	private GamePanel panel;
	private boolean running = true;
	
	private boolean isViewer = false;
	
	private Thread gameThread;
	private Snake snake;

	private Notification gamespaceWriteNotification;

	private static Logger log = LoggerFactory.getLogger(GameFrame.class);
	
	/**
	 * constructor setting isViewer to false
	 */
	public GameFrame() {
		this(false);
	}
	
	public GameFrame(boolean isViewer) {
		this.isViewer = isViewer;
		if(!this.isViewer)	{
			snake = new Snake();
			
			panel = new GamePanel(snake);
			
			addKeyListener(this);
			
		} else {
			panel = new GamePanel();
		}
		getContentPane().add(panel);
		pack();
		setVisible(true);
		setResizable(false);
		
		// start thread
		gameThread = new Thread(this);
		gameThread.start();
	}

	/**
	 * BEGIN WRAPPER
	 * 	for GamePanel  **/
	/*
	 * @TODO check if wrapper is needed
	 * @TODO check if this is best practice... i dunno
	 */
	public void addSnake(Snake s)	{
		panel.addSnake(s);
	}
	
	public boolean hasSnake(UUID id)	{
		return panel.hasSnake(id);
	}
	
	/**
	 * update snake with Data from SnakeDataHolder
	 * @param sdh
	 */
	public void updateSnake(SnakeDataHolder sdh)	{
		panel.updateSnake(sdh);
	}
	/** END WRAPPER **/
	
	
	@Override
	public void run() {
		
		this.registerNotificationListener();
		
		boolean first = true;
		while (running ) {
			if(!isViewer)	{
				snake.moveForward(15);
				
				// update own snake in space
				// first update contains all snakeparts, further updates contain tail and headpart
				Util.getInstance().update(
						ContainerCoordinatorMapper.GAME_LIST, 
						snake.getSnakeDataHolder(first),
						snake.getId().toString());
				
				// first only in first iteration true => all snakeparts are written to space, not just head an last
				// => force all parts being transported to make it possible, that snake is drawn completely in second window later
//				if(first) first = false;
			}
			
			panel.repaint();
			
			try {
				Thread.sleep(150);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * registers the notificationListener for all written Data to Game Container
	 */
	private void registerNotificationListener() {
		
		// create notification
		try {
			this.gamespaceWriteNotification = Util.getInstance().getNotificationManager().createNotification(
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
			if (gamespaceWriteNotification == null) {
				log.error("ERROR: could not create notification (null after creation)");
			} else {
				log.debug("NOTFICATION successfully created");
			}
		}
	}
	
	/** (non-Javadoc)
	 * @see org.mozartspaces.notifications.NotificationListener#entryOperationFinished(org.mozartspaces.notifications.Notification, org.mozartspaces.notifications.Operation, java.util.List)
	 * 
	 * notification Listener
	 */
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
				
				if (obj instanceof SnakeDataHolder) {
					SnakeDataHolder snakedataholder = (SnakeDataHolder) obj;
					
//					log.info("received: " + snakedataholder);
					
					/**
					 * @TODO is there a better way to exclude notifications from my own snake?!?!?!?!?
					 * 
					 */
					if(this.snake.equals(new Snake(snakedataholder.getId())))	{
						return;
					}
					
					// how to get the key easier... lulz
					if(!this.hasSnake(snakedataholder.getId()))	{
						// @TODO check it
						// create Snake (this is here temporarly - i think)
						this.addSnake(new Snake(snakedataholder.getId()));
					}
					// update snake
					this.updateSnake(snakedataholder);
				}
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		snake.move(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

}
