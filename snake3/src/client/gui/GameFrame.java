package client.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import mzs.util.ContainerCoordinatorMapper;
import mzs.util.Util;

import client.data.Snake;

public class GameFrame extends JFrame implements Runnable, KeyListener {

	private static final long serialVersionUID = 1L;

	private GamePanel panel;
	private boolean running = true;
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
		snake = new Snake();
		
		snake2 = new Snake();
		snake2.getParts().get(0).setX(500);
		snake2.getParts().get(0).setY(500);
		snake2.getParts().get(0).setDirection(90);
		
		panel = new GamePanel(new Snake[]{snake, snake2});
		getContentPane().add(panel);
		pack();
		setVisible(true);
		setResizable(false);
		
		gameThread = new Thread(this);
		gameThread.start();
		
		addKeyListener(this);
	}

	@Override
	public void run() {
		boolean first = true;
		while (running ) {
			snake.moveForward(15);
			snake2.moveForward(15);
			panel.repaint();
			
			Util.getInstance().update(
					ContainerCoordinatorMapper.GAME_LIST, 
					snake.getSnakeDataHolder(first),
					snake.getId().toString());
			
			if(first) first = false;
			
			try {
				Thread.sleep(5000);
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
