package snake.data;

/**
 * Different states of a player when a game is started.
 * @author Thomas Scheller, Markus Karolus
 */
public enum PlayerState
{
  notinit, //no player variables are initialized (starting state if player enters a game)
  init, //player has initialized its own snake variables in space
  loaded, //player has loaded all snake variables of the other players from space
  starting //player is starting the game
}
