/**
 * 
 */
package client.event.i;

import client.data.game.Game;

/**
 * @author Jakob Lahmer, Matthias Steinb�ck
 *
 */
public interface GameStateEventListener {

	public void gameStateChanged(Game game);
}
