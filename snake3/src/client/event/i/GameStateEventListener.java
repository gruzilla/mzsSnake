/**
 * 
 */
package client.event.i;

import client.data.game.Game;

/**
 * @author Jakob Lahmer, Matthias Steinbšck
 *
 */
public interface GameStateEventListener {

	public void gameStateChanged(Game game);
}
