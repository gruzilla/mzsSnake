package client.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.swing.JPanel;

import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mzs.data.SnakeDataHolder;
import mzs.util.ContainerCoordinatorMapper;
import mzs.util.Util;

import client.data.Snake;
import client.data.game.Game;

public class GameFrame extends JPanel implements Runnable, KeyListener, NotificationListener {

	private static final long serialVersionUID = 1L;

	private GamePanel panel;
	private boolean running = true;						// game is running
	
	private boolean isViewer = false;					// game is in view mode
	private boolean isMultiplayer = false;				// game is multiplayer game
	
	private Thread gameThread;
	
	// THIS IS MY SNAKE (no other instance should ever be created)
	private Snake snake;

	private Notification gamespaceWriteNotification;

	private boolean headRight = false;
	private boolean headLeft = false;

	private Game game = null;


	private static Logger log = LoggerFactory.getLogger(GameFrame.class);
	

	
	public GameFrame() {
		// init game panel
		panel = new GamePanel();
		this.removeAll();
		this.add(panel);
		this.setFocusable(true);
	}

	public void startGame(boolean isViewer, boolean isMultiplayer, Game mpGame)	{
		this.game = mpGame;
		this.startGame(isViewer, isMultiplayer);
	}
	
	
	public void startGame(boolean isViewer, boolean isMultiplayer)	{
		// the JPanel now has focus, so receives key events
		this.requestFocusInWindow();
		
		this.isMultiplayer = isMultiplayer;
		this.isViewer = isViewer;
		if(!this.isViewer)	{
			// create Snake if player is not a viewer
			snake = new Snake();
			addKeyListener(this);
			// add snake to panel
			panel.addSnake(snake);
		}
		
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
		/*
		 * @TODO check if this is the best solution, or create an MPGameFrame extending this one
		 */
		if(this.isMultiplayer)
			this.registerNotificationListener();
		boolean first = true;
		while (running ) {
			if(!isViewer)	{
				if (headRight) {
					snake.move(+20);
				} else if (headLeft) {
					snake.move(-20);
				}
				snake.moveForward();
				
				if(isMultiplayer)	{
					// update own snake in space
					// first update contains all snakeparts, further updates contain tail and headpart
					Util.getInstance().update(
							Util.getInstance().getGameContainer(this.game).getId(), 
							snake.getSnakeDataHolder(first),
							snake.getId().toString());
				}
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
					Util.getInstance().getGameContainer(this.game),
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
		int key = e.getKeyCode();

		switch (key) {
		case KeyEvent.VK_LEFT:
			headRight = false;
			headLeft = true;
			break;
		case KeyEvent.VK_RIGHT:
			headLeft = false;
			headRight = true;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();

		switch (key) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_RIGHT:
			headRight = false;
			headLeft = false;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

}
