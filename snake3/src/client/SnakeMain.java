package client;

import client.gui.GameFrame;

/**
 * @author Jakob Lahmer, Matthias Steinbšck
 *
 */
public class SnakeMain {

	private GameFrame frame;
	
	/**
	 * represents a client instance of a snake game
	 */
	public SnakeMain() {
		// init game frame
		frame = new GameFrame();
	}

	/**
	 * Main Function, starts a client
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new SnakeMain();
	}

}
