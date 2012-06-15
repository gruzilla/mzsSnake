package client.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import client.data.Snake;

public class GameFrame extends JFrame implements Runnable, KeyListener {

	private static final long serialVersionUID = 1L;

	private GamePanel panel;
	private boolean running = true;
	private Thread gameThread;
	private Snake snake;

	public GameFrame() {
		snake = new Snake();

		panel = new GamePanel(snake);
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
		while (running ) {
			snake.moveForward(15);
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
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

}
