package snake;

import java.awt.*;
import java.awt.image.*;
import corso.lang.*;
import snake.util.*;
import snake.data.*;

/**
 * Sprite that draws and updates all collectables of the game, including visual effects
 * and checking for eaten collectables. The class also checks the winning conditions
 * (another player set game state to over, or winning condition is reached, or all other
 * players have left the game), and therefore also calculates the points of the player.
 * @author Thomas Scheller, Markus Karolus
 */

public class CollectableSprite
{
  //values for changing brightness
  private final float minBrightness = 1.0f;
  private final float maxBrightness = 3.5f;
  private final float stepBrightness = 0.04f;
  private boolean brightnessInc = true;

  //subtraction points for crashes
  private final int crashPointsWall = -4;
  private final int crashPointsOwn = -1;
  private final int crashPointsOther = -1;
  private final int winPoints = 25; //for singleplayer only

  //values for doublepoints powerup
  private final int doublePointsDuration = 180; //duration of doublepoints as number of frames (= 9 sec)
  private int doublePointsState = 0;
  private Font specialFont;

  //variables for audio clips and images. brightened images are stored in an array,
  //so the brighten operation has to be performed only once
  private ClipsLoader clipsLoader = null;
  private ImageLoader imgLoader = null;
  private BufferedImage imgCollectable = null;
  private BufferedImage[] imgCollectableBright = null;
  private BufferedImage imgSpeedUp = null;
  private BufferedImage[] imgSpeedUpBright = null;
  private BufferedImage imgDoublePoints = null;
  private BufferedImage[] imgDoublePointsBright = null;
  private int imgPos = 0;
  private int imgWidth = 10;
  private int imgHeight = 10;

  private CollectableSpriteData data = null;
  private GameListManager gameList = null;
  private Player myPlayer = null;
  private boolean multiplayer = true;

  private SnakeSprite snake = null;
  private SnakePanel snakePanel = null;

  /**
   * Create a new CollectableSprite. All graphics and sounds are loaded, and a new
   * CollectableSpriteData object is created that manages the collectables in corsospace.
   * @param pWidth frame width
   * @param pHeight frame height
   * @param imgLoader ImageLoader
   * @param snake the player's SnakeSprite
   * @param snakePanel the game's SnakePanel
   * @param conn the connection object to connect to the corsospace
   * @param gameList the GameListManager that holds the information about the current game
   * @param myPlayer the own player
   * @param gameMap the BackGroundManager that is used in the Game
   */
  public CollectableSprite(int pWidth, int pHeight, ImageLoader imgLoader, SnakeSprite snake,
                           SnakePanel snakePanel, CorsoConnection conn, GameListManager gameList,
                           Player myPlayer, BackgroundManager gameMap)
  {
    this.imgLoader = imgLoader;
    this.snake = snake;
    if (snake != null)
    {//not ViewOnly mode
      this.snake.setCollectableSprite(this);
    }
    this.snakePanel = snakePanel;
    this.gameList = gameList;
    this.myPlayer = myPlayer;
    multiplayer = (conn != null);

    //load graphics
    SkinsManager skinsManager = new SkinsManager();
    skinsManager.setCurrentSkin(myPlayer.getSkin());
    clipsLoader = skinsManager.getClipsLoader(); //get a clipsloader holding all sound of the current skin
    imgCollectable = imgLoader.loadImage(skinsManager.getCollectablePath(), false);
    imgSpeedUp = imgLoader.loadImage(skinsManager.getSpeedUpPath(), false);
    imgDoublePoints = imgLoader.loadImage(skinsManager.getDoublePointsPath(), false);
    specialFont = new Font("SansSerif", Font.BOLD, 20);
    createImages();
    imgWidth = imgCollectable.getWidth();
    imgHeight = imgCollectable.getHeight();

    //Positionen der Collectables initialisieren
    data = new CollectableSpriteData(gameList, conn, myPlayer, gameMap);
  }

  /**
   * Create all brightened images and store them in arrays, so they don't need to
   * be created every time at drawing.
   */
  private void createImages()
  {
    float brightness = minBrightness;
    int anz = (int) ( (maxBrightness - minBrightness) / stepBrightness);
    imgCollectableBright = new BufferedImage[anz];
    imgSpeedUpBright = new BufferedImage[anz];
    imgDoublePointsBright = new BufferedImage[anz];

    for (int i = 0; i < anz; i++)
    {
      imgCollectableBright[i] = imgLoader.getBrighterImage(imgCollectable, brightness);
      imgSpeedUpBright[i] = imgLoader.getBrighterImage(imgSpeedUp, brightness);
      imgDoublePointsBright[i] = imgLoader.getBrighterImage(imgDoublePoints, brightness);
      brightness += stepBrightness;
    }
  }

  /**
   * Tasks when updating the collectable sprite:
   * - check if snake eats a collectable
   * - check if other players are still there (end game if all other players left)
   * - check if game has been ended by another player
   * - check if max point or time has been reached
   * - update double points mode if active
   * - update brightness value
   * - update status of removing elements
   */
  public void updateSprite()
  {
    if (snake != null)
    {
      //Prüfen ob Schlange ein Collectable frisst
      for (int i = 0; i < data.getPositions().length; i++)
      {
        Rectangle rect = new Rectangle(data.getPositions()[i].x, data.getPositions()[i].y, imgWidth,
                                       imgHeight);
        if (rect.intersects(snake.getHeadRectangle()))
        {
          //update data depending on type of collectable
          switch (data.getPositions()[i].type)
          {
            case normal:
              snake.addSnakePart();
              if (data.doublePoints)
                snake.addSnakePart();
              break;

            case speedup:
              snake.startSpeedUp();
              break;

            case doublepoints:
              doublePointsState = doublePointsDuration;
              data.doublePoints = true;
              //System.out.println("Double Points on");
              break;
          }
          data.eatCollectable(data.getPositions()[i].nr);
          //play sound
          clipsLoader.playEat();
        }
      }
    }
    //update double points mode if active
    if (data.doublePoints)
    {
      doublePointsState--;
      if (doublePointsState <= 0)
      {
        data.doublePoints = false;
        //System.out.println("Double Points off");
      }
    }


    //check if other players are still there (end game if all other players left)
    if (multiplayer && gameList.getCurrentGame().getPlayerAnz() < 2)
    {
      gameList.setGameState(GameState.ended);
      snakePanel.gameOver();
      return;
    }

    if ((multiplayer && gameList.getCurrentGame().getState() == GameState.ended))
    {//game already ended (by another player)
      snakePanel.gameOver();
    }

    //check if max points or time reached (game over)
    if (!multiplayer || gameList.getCurrentGame().getGameType() == GameType.points) //gamtype points
    {
      if (data.getPoints() >= ((multiplayer) ? gameList.getCurrentGame().getWinValue() : winPoints))
      {
        if (multiplayer)
        {
          gameList.setGameState(GameState.ended);
        }
        snakePanel.gameOver();
      }
    }
    else //gametype time
    {
      if (snakePanel.getGameTime() >= gameList.getCurrentGame().getWinValue())
      {
        if (multiplayer)
        {
          gameList.setGameState(GameState.ended);
        }
        snakePanel.gameOver();
      }
    }

    //update brightness value
    if (imgPos >= imgCollectableBright.length - 1)
    {
      brightnessInc = false;
    }
    else if (imgPos <= 0)
    {
      brightnessInc = true;
    }
    if (brightnessInc)
    {
      imgPos++;
    }
    else
    {
      imgPos--;
    }

    //update status of removing elements (collectables that have been eaten and are shown as fading)
    data.updateEatenCollectables();
  }

  /**
   * Draw all current collectables, and fading for eaten collectables. Also draw information for
   * double points powerup (icon and remaining time in seconds), if it is currently active.
   * @param g Graphics
   * @param correctionPos correction of the drawing positions  because of scrolling map
   */
  public void drawSprite(Graphics g, java.awt.Point correctionPos)
  {
    Graphics2D g2d = (Graphics2D) g;
    //draw collectables (image depending on type)
    for (int i = 0; i < data.getPositions().length; i++)
    {
      switch (data.getPositions()[i].type)
      {
        case normal:
          g.drawImage(imgCollectableBright[imgPos], data.getPositions()[i].x - correctionPos.x, data.getPositions()[i].y - correctionPos.y, null);
          break;
        case speedup:
          g.drawImage(imgSpeedUpBright[imgPos], data.getPositions()[i].x - correctionPos.x, data.getPositions()[i].y - correctionPos.y, null);
          break;
        case doublepoints:
          g.drawImage(imgDoublePointsBright[imgPos], data.getPositions()[i].x - correctionPos.x, data.getPositions()[i].y - correctionPos.y, null);
          break;
      }
    }

    //draw any removing elements
    for (int i = 0; i < data.getEatenPositions().size(); i++)
    {
      CollectablePos pos = (CollectablePos) data.getEatenPositions().elementAt(i);
      double state = (Double) data.getEatenStatus().elementAt(i);
      double sizeChange = 3.0 - 2.0 * state;

      switch (pos.type)
      {
        case normal:
          imgLoader.drawResizedFadedImage(g2d, imgCollectable, pos.x - correctionPos.x, pos.y - correctionPos.y, sizeChange, sizeChange, (float) state);
          break;
        case speedup:
          imgLoader.drawResizedFadedImage(g2d, imgSpeedUp, pos.x - correctionPos.x, pos.y - correctionPos.y, sizeChange, sizeChange, (float) state);
          break;
        case doublepoints:
          imgLoader.drawResizedFadedImage(g2d, imgDoublePoints, pos.x - correctionPos.x, pos.y - correctionPos.y, sizeChange, sizeChange, (float) state);
          break;
      }
    }

    //draw double points time and icon, if active
    if (data.doublePoints)
    {
      g2d.setColor(Color.black);
      Composite c = g2d.getComposite();
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
      g2d.setFont(specialFont);
      g2d.drawImage(imgDoublePoints,15,85,null);
      g2d.drawString(String.valueOf (doublePointsState/Snake.DEFAULT_FPS),45,103);
      g2d.setComposite(c);
    }
  }

  public int getPoints()
  {
    return data.getPoints();
  }

  /**
   * Subtract points from the current points of the player depending on type
   * of crash.
   * @param crashType type of crash (self, other or wall)
   */
  public void snakeCrashed(int crashType)
  {
    switch (crashType)
    {
      case SnakeSpriteData.COLLISION_WALL:
        data.addPoints(crashPointsWall);
        break;
      case SnakeSpriteData.COLLISION_OWN:
        data.addPoints(crashPointsOwn);
        break;
      case SnakeSpriteData.COLLISION_OTHER:
        data.addPoints(crashPointsOther);
        break;
    }
  }

  /**
   * Stop the notifier thread from the CollectableSpriteData object.
   */
  public void stopNotifier()
  {
    data.stopNotifier();
  }
}
