package client.data.state;

/**
 * Different states of a player when a game is started.
 * @author Thomas Scheller, Markus Karolus
 */
public enum PlayerState
{
  NOTINIT, 	//no player variables are initialized (starting state if player enters a game)
  INIT, 	//player has initialized its own snake variables in space
  LOADED,	//player has loaded all snake variables of the other players from space
  STARTING 	//player is starting the game
}
