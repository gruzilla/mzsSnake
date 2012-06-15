package client.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.UUID;

import javax.swing.JFrame;

import mzs.data.SnakeDataHolder;
import mzs.util.ContainerCoordinatorMapper;
import mzs.util.Util;

import client.data.Snake;

public class GameFrame extends JFrame implements Runnable, KeyListener {

	private static final long serialVersionUID = 1L;

	private GamePanel panel;
	private boolean running = true;
	
	private boolean isViewer = false;
	
	private Thread gameThread;
	private Snake snake;

	
	private Snake snake2;

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
			
	//		snake2 = new Snake();
	//		snake2.getParts().get(0).setX(500);
	//		snake2.getParts().get(0).setY(500);
	//		snake2.getParts().get(0).setDirection(90);
			
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
	 * 	for GamePanel
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
				
				if(first) first = false;
			}
			
			// testing
//			snake2.moveForward(15);

			panel.repaint();
			
			try {
				Thread.sleep(150);
			} catch (InterruptedException e) {
				e.printStackTrace();
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
