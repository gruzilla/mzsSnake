package snake.ui;

import java.awt.*;
import java.awt.image.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import snake.*;
import snake.util.ImageLoader;
import snake.data.*;

/**
 * Represents a single snake in the game, providing methods to draw the snake and
 * update its data. The data itself and the representation in corsospace is managed
 * by a SnakeSpriteData object. Also manages the effects of the snake using the
 * SnakeEffect class, and applies all active effects when drawing.
 * @author Thomas Scheller, Markus Karolus
 */
public class SnakeSprite
{
	private static final double TURNSTEP = 10.0; //degrees the snake can turn in one update;
								 //should not be changed because arraysize of turned images depends on it!

	private int headSize;
	private double turn = 0.0;

	private SnakeSpriteData data = null;
	private boolean writingSnake = true;

	//fonts and metrics for playername and speedup information
	private Font msgsFont;
	private Font specialFont;
	private FontMetrics metrics;

	private SkinsManager skinsManager = new SkinsManager();
	private ImageLoader imgLoader = null;

	//snake images
	private BufferedImage imgPart = null;
	private BufferedImage[] imgPartsTurned = new BufferedImage[36]; //rotated images (one every 10�)
	private BufferedImage imgHead = null;
	private BufferedImage imgTail = null;
	private BufferedImage imgSpeedUp = null;
	private Logger log = LoggerFactory.getLogger(SnakeSprite.class);

	//values for snake effects
	private SnakeEffect effect = new SnakeEffect(); //general effects (fading, brightness, ...)
	private boolean speedUp = false;
	private int speedUpState = 0;
	private final int speedUpDuration = 120; //frame duration of speedup (= 6 sec)
	private final int speedUpStandardValue = 2; //value the speed is increased
	private Snake snakeMain;
	private Player otherPlayer;

	/**
	 * SnakeSprite constructor for the own snake. The snake is set to active so data is written to
	 * space (if the game is in multiplayer mode). All graphics needed to draw the snake are initialized,
	 * a new SnakeSpriteData object is created.
	 * @param imgLoader ImageLoader used to load the snake images and apply effects on them
	 * @param conn connection to the corsospace
	 * @param aPlayer the own player
	 * @param gameMap manager for the level background
	 * @param snakePanel SnakePanel where the game is drawn
	 * @param collisionMode collision mode for the game (collision with self, others, wall)
	 */
	public SnakeSprite(ImageLoader imgLoader,
		 Snake snakeMain,
		 GameListManager gameListManager,
		 BackgroundManager gameMap,
		 SnakePanel snakePanel,
		 int collisionMode)
	{
		this.snakeMain = snakeMain;
		this.imgLoader = imgLoader;
		skinsManager.setCurrentSkin(getPlayer().getSkin());

		msgsFont = new Font("SansSerif", Font.BOLD, 16);
		specialFont = new Font("SansSerif", Font.BOLD, 20);
		metrics = snakePanel.getFontMetrics(msgsFont);

		data = new SnakeSpriteData(skinsManager.getClipsLoader(), gameListManager, snakeMain, gameMap, collisionMode); // TODO: multiplayer
		data.setSprite(this);
		writingSnake = true;
		loadGraphics();
		restartSnake(); //start effect for the snake
	}

	/**
	 * SnakeSprite constructor for the snake of another snakeMain.getMyPlayer(). The snake is set to passive so data is
	 * read from space and collision detection is turned off. All graphics needed to draw the snake are
	 * initialized, a new SnakeSpriteData object is created.
	 * @param imgLoader ImageLoader used to load the snake images and apply effects on them
	 * @param conn connection to the corsospace
	 * @param aPlayer the own player
	 * @param snakePanel SnakePanel where the game is drawn
	 */
	public SnakeSprite(ImageLoader imgLoader,
			 Snake snakeMain,
			 GameListManager gameListManager,
			SnakePanel snakePanel, Player player, String skin) {
		this.imgLoader = imgLoader;
		skinsManager.setCurrentSkin(skin);
		this.snakeMain = snakeMain;
		otherPlayer = player;

		msgsFont = new Font("SansSerif", Font.BOLD, 16);
		metrics = snakePanel.getFontMetrics(msgsFont);
		data = new SnakeSpriteData(skinsManager.getClipsLoader(), gameListManager, snakeMain, player, snakePanel);
		data.setSprite(this);
		writingSnake = false;
		loadGraphics();
		restartSnake(); //start effect for the snake
	}
	
	private Player getPlayer() {
		return (otherPlayer != null) ? otherPlayer : snakeMain.getMyPlayer();
	}

	/**
	 * Set the CollectableSprite object of the current game.
	 * @param collectables CollectableSprite object
	 */
	public void setCollectableSprite(CollectableSprite collectables)
	{
		data.setCollectableSprite(collectables);
	}

	/**
	 * Get the SnakeSpriteData object of the snake.
	 * @return SnakeSpriteData
	 */
	public SnakeSpriteData getData()
	{
		return data;
	}

	/**
	 * Load the graphics for the snake, getting the paths of the images from the
	 * skinsmanager. Head, part, tail and speedup image are loaded, the part image
	 * is then rotated and saved in an array for every direction needed, so the rotation
	 * does not have to be calculated everytime when drawing.
	 */
	private void loadGraphics()
	{
		imgPart = imgLoader.loadImage(skinsManager.getSnakePartPath(), false);
		imgHead = imgLoader.loadImage(skinsManager.getSnakeHeadPath(), false);
		imgTail = imgLoader.loadImage(skinsManager.getSnakeTailPath(), false);
		imgSpeedUp = imgLoader.loadImage(skinsManager.getSpeedUpPath(), false);
		headSize = imgHead.getWidth();

		for (int i = 0; i < imgPartsTurned.length; i++)
		{
			imgPartsTurned[i] = imgLoader.getRotatedImage(imgPart, (i * (int) TURNSTEP - 90));
		}

		data.setSnakePartSize(imgHead.getHeight(), imgHead.getWidth());
	}

	/**
	 * Set the current turn value of the snake to -turnstep, so the snake will turn
	 * left at the next update.
	 */
	public void turnLeft()
	{
		turn = -TURNSTEP;
	}

	/**
	 * Set the current turn value of the snake to turnstep, so the snake will turn
	 * right at the next update.
	 */
	public void turnRight()
	{
		turn = TURNSTEP;
	}

	/**
	 * Set the current turn value of the snake to 0, so the snake will not turn
	 * anywhere at the next update.
	 */
	public void stopTurning()
	{
		turn = 0.0;
	}

	/**
	 * If the snake is a writing snake (the snake of the own player), call updateData()
	 * from the SnakeSpriteData object to move the snake and update its coordinates
	 * and write the data so space if in multiplayer mode, as long as the snake does
	 * not have an immovable effect. Also decrease the frame duration of the speedup
	 * effect, if it is active.
	 */
	public void updateSprite()
	{
		if (writingSnake)
		{
			//update data if snake is not immovable
			if (!effect.hasNoMoveEffect())
			{
				data.updateData(turn);
				data.writeData();
			}
			// update speedup effect
			if (speedUp)
			{
				speedUpState--;
				if (speedUpState <= 0)
				{
					data.setSpeedChange(0);
					speedUp = false;
				}
			}
		}
	}

	/**
	 * Draw the snake and apply all running effects to the images before drawing, also
	 * draw the name of the player above the head of the snake, and an information about
	 * the speedup effect if it is running. Also update effects by calling the update()
	 * method of the SnakeEffect object. The positions of the snake are read from the
	 * SnakeSpriteData object, the correction coordinates are subtracted from them when
	 * drawing.
	 * @param g Graphics
	 * @param correctionPos correction coordinates for the onscreen position of the snake
	 */
	public void drawSprite(Graphics g, java.awt.Point correctionPos)
	{
		try {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(Color.black);
	
			effect.update(); //called here because the updateSprite method concerns only active snakes
											 //and the effects are needed for passive snakes also
			Composite c = g2d.getComposite(); // backup the old composite
			if (effect.hasFadeEffect())
			{
				//when snake has a fade effect, change the alpha composite of the graphics object
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, effect.getFadeValue()));
			}
	
			//calculate positions that must be drawn
			int headPos = getPlayer().getHeadPos();
			int tailPos = getPlayer().getTailPos();
			int maxPos = headPos;
			if (maxPos < tailPos)
			{
				maxPos += SnakeSpriteData.MAXPOINTS;
			}
			if (headPos > 0)
			{
				headPos -= 1;
			}
			else
			{
				headPos = SnakeSpriteData.MAXPOINTS - 1;
			}
	
			int tempPos = data.pixelHeadPos-((maxPos-tailPos)*(SnakeSpriteData.PARTDIST/SnakeSpriteData.POINTDIST));
			if (tempPos < 0)
				tempPos += data.pixelPos.length;
	
			//draw the snake: rotation an brighten effects are applied to the image when needed,
			//the map position of the snake is corrected by the correction position to get
			//screen coordinates

			//draw tail
			g2d.drawImage(imgLoader.getBrighterImage(imgLoader.getRotatedImage(imgTail, (int) (data.pixelDirection[tempPos] - 90)),effect.hasBrightenEffect() ? effect.getBrightenValue() : 1.0f),
										(int) data.pixelPos[tempPos].x - correctionPos.x,
										(int) data.pixelPos[tempPos].y - correctionPos.y, null);
			tempPos+=SnakeSpriteData.PARTDIST;

			//draw parts: pixelpositions are read from the SnakeSpriteData object, the array
			//position is increased by a constant value that is the pixel distance between
			//two parts.
			//The rotated images are read from the previously created array, so they dont have
			//to be recreated every time they are drawn.
			for (int ix = tailPos; ix < maxPos-3; ix += SnakeSpriteData.POINTDIST) //the -3 decrease prevents a passive snake to have flickering parts because of inconsistent arrival of notifications from space
			{
				tempPos = tempPos % data.pixelPos.length;
				g2d.drawImage(imgLoader.getBrighterImage(imgPartsTurned[ ( data.pixelDirection[tempPos] / (int) TURNSTEP)],effect.hasBrightenEffect() ? effect.getBrightenValue() : 1.0f),
											(int) data.pixelPos[tempPos].x - correctionPos.x, (int) data.pixelPos[tempPos].y - correctionPos.y, null);
				tempPos+=SnakeSpriteData.PARTDIST;
			}

			//draw head
			if (getPlayer() == null) log.error("\n\nplayer is null\n\n");
			if (getPlayer().getHeadPart() == null) log.error("\n\nhead part is null\n\n");
			g2d.drawImage(imgLoader.getBrighterImage(imgLoader.getRotatedImage(imgHead, (int) (getPlayer().getHeadPart().direction - 90)),effect.hasBrightenEffect() ? effect.getBrightenValue() : 1.0f),
										(int) data.pixelPos[data.pixelHeadPos].x - correctionPos.x,
										(int) data.pixelPos[data.pixelHeadPos].y - correctionPos.y, null);

			//draw name (half transparent)
			g2d.setColor(Color.darkGray);
			g2d.setFont(msgsFont);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
			g2d.drawString(getPlayer().getName(),
										 (int) getPlayer().getHeadPart().x + (imgHead.getWidth() / 2) -
										 (metrics.stringWidth(getPlayer().getName()) / 2) - correctionPos.x,
										 (int) getPlayer().getHeadPart().y - 10 - correctionPos.y);
			g2d.setComposite(c); // restore the old composite so it doesn't mess up future rendering
	
			//show remaing time and symbol of speedup effect, if active
			if (speedUp)
			{
				g2d.setColor(Color.black);
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
				g2d.setFont(specialFont);
				g2d.drawImage(imgSpeedUp,15,60,null);
				g2d.drawString(String.valueOf (speedUpState/Snake.DEFAULT_FPS),45,78);
				g2d.setComposite(c);
			}
		}
		catch (Exception ex)
		{
			System.out.println("drawSprite: Exception occured:");
			ex.printStackTrace(System.out);
			System.exit(0); //Test-Fehlerloesung
		}
	}

	/**
	 * Get the position of the snake head as a rectangle.
	 * @return Rectangle
	 */
	public Rectangle getHeadRectangle()
	{
		return new Rectangle( (int) getPlayer().getHeadPart().x + 2, (int) getPlayer().getHeadPart().y + 2,
												 headSize - 4, headSize - 4);
	}

	/**
	 * Get the position of the snake head.
	 * @return SnakePos
	 */
	public snake.data.SnakePos getHeadPos()
	{
		return getPlayer().getHeadPart();
	}

	/**
	 * Add a snake part, increasing the length of the snake.
	 */
	public void addSnakePart()
	{
		data.addSnakePart();
	}

	/**
	 * Activate a restart effect (managed by SnakeEffect object).
	 */
	public void restartSnake() {
		restartSnake(false);
	}
	public void restartSnake(boolean playSound)
	{
		if (playSound) skinsManager.getClipsLoader().playDie();
		effect.showStandardRestartEffect();
	}

	/**
	 * Activate a crash effect (managed by SnakeEffect object).
	 */
	public void crashSnake()
	{
		crashSnake(false);
	}
	public void crashSnake(boolean playSound)
	{
		if (playSound) skinsManager.getClipsLoader().playCrash();
		effect.showStandardCrashEffect();
	}

	/**
	 * Start the speedup effect.
	 */
	public void startSpeedUp()
	{
		speedUp = true;
		speedUpState = speedUpDuration;
		data.setSpeedChange(speedUpStandardValue);
	}

	public void setHeadPos(int headPos) {
		while (getPlayer().getHeadPos() != headPos)
		{
			int prevPos = getPlayer().getHeadPos();
			getPlayer().setHeadPos((headPos + 1) % SnakeSpriteData.MAXPOINTS);
			data.updatePixelPositions(getPlayer().getHeadPos(), prevPos, false);
		}
	}
}
