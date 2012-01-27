package snake.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import snake.util.*;

/**
 * Content panel for the Game, that draws a border around the panel (the first
 * element of the content panel), so it looks better when the frame has a bigger
 * size than the panel.
 * @author Thomas Scheller, Markus Karolus
 */
public class BorderContentPanel extends JPanel
{
  BufferedImage borderCorner1 = null;
  BufferedImage borderEdge1 = null;
  BufferedImage borderCorner2 = null;
  BufferedImage borderEdge2 = null;
  BufferedImage borderCorner3 = null;
  BufferedImage borderEdge3 = null;
  BufferedImage borderCorner4 = null;
  BufferedImage borderEdge4 = null;

  /**
   * Create a new BorderPanel and load all images needed to draw the border.
   */
  public BorderContentPanel()
  {
    super();

    //load border images
    ImageLoader loader = new ImageLoader();
    borderCorner1 = loader.loadImage("images/borderCorner.png",false);
    borderEdge1 = loader.loadImage("images/borderEdge1.png",false);
    borderCorner2 = loader.getRotatedImage(borderCorner1,90);
    borderEdge2 = loader.loadImage("images/borderEdge2.png",false);
    borderCorner3 = loader.getRotatedImage(borderCorner1,180);
    borderEdge3 = loader.getRotatedImage(borderEdge1,180);
    borderCorner4 = loader.getRotatedImage(borderCorner1,270);
    borderEdge4 = loader.getRotatedImage(borderEdge2,180);
  }

  /**
   * Overwritten; draws the border after calling super.paintComponent(). The border
   * is only drawn when necessary (drawing and calculation of the border position is
   * skipped when border would not be visible).
   * @param g Graphics
   */
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    //only draw border when panel has elements
    if (this.getComponentCount() > 0)
    {
      JPanel panel = (JPanel)this.getComponent(0);

      if (panel.getHeight() < this.getHeight())
      {
        //top border
        int y = panel.getY() - borderEdge1.getHeight();
        for (int x = panel.getX(); x < panel.getX()+panel.getWidth(); x+= borderEdge1.getWidth())
        {
          if (panel.getX()+panel.getWidth()-x < borderEdge1.getWidth())
            g.drawImage(borderEdge1,x,y,panel.getX()+panel.getWidth()-x,borderEdge1.getHeight(),null);
          else
            g.drawImage(borderEdge1,x,y,null);
        }
        //bottom border
        y = panel.getY() + panel.getHeight();
        for (int x = panel.getX(); x < panel.getX()+panel.getWidth(); x+= borderEdge3.getWidth())
        {
          if (panel.getX()+panel.getWidth()-x < borderEdge3.getWidth())
            g.drawImage(borderEdge3,x,y,panel.getX()+panel.getWidth()-x,borderEdge3.getHeight(),null);
          else
            g.drawImage(borderEdge3,x,y,null);
        }
      }

      if (panel.getWidth() < this.getWidth())
      {
        //left border
        int x = panel.getX() - borderEdge4.getWidth();
        for (int y = panel.getY(); y < panel.getY()+panel.getHeight(); y+= borderEdge4.getHeight())
        {
          if (panel.getY()+panel.getHeight()-y < borderEdge4.getHeight())
            g.drawImage(borderEdge4,x,y,borderEdge4.getWidth(),panel.getY()+panel.getHeight()-y,null);
          else
            g.drawImage(borderEdge4,x,y,null);
        }
        //right border
        x = panel.getX() + panel.getWidth();
        for (int y = panel.getY(); y < panel.getY()+panel.getHeight(); y+= borderEdge2.getHeight())
        {
          if (panel.getY()+panel.getHeight()-y < borderEdge2.getHeight())
            g.drawImage(borderEdge2,x,y,borderEdge2.getWidth(),panel.getY()+panel.getHeight()-y,null);
          else
            g.drawImage(borderEdge2,x,y,null);
        }
      }

      if (panel.getHeight() < this.getHeight() && panel.getWidth() < this.getWidth())
      {
        //corner borders
        g.drawImage(borderCorner1,panel.getX()-borderCorner1.getWidth(),panel.getY()-borderCorner1.getHeight(),null);
        g.drawImage(borderCorner2,panel.getX()+panel.getWidth(),panel.getY()-borderCorner2.getHeight(),null);
        g.drawImage(borderCorner3,panel.getX()+panel.getWidth(),panel.getY()+panel.getHeight(),null);
        g.drawImage(borderCorner4,panel.getX()-borderCorner1.getWidth(),panel.getY()+panel.getHeight(),null);
      }
    }
  }
}
