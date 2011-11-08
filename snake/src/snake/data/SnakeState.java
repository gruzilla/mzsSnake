package snake.data;

/**
 * Different states of a snake while in a game.
 * @author Thomas Scheller, Markus Karolus
 */
public enum SnakeState
{
	unknown, //not yet loaded
	active, //snake is active in game
	unverwundbar, //snake is invulnerable and cannot be crashed
	unsichtbar, //snake invisible for other players (not used)
	crashed //snake crashed (against itself, other snake or wall)
}
