package snake.data;

import java.io.Serializable;
import java.util.UUID;

public class SnakeSpriteDataHolder implements Serializable {
	public static final long serialVersionUID = 1L;
	public SnakeState snakeState;
	public SnakePos headPart;
	public int tailPos;
	public int headPos;
	public UUID id;
}
