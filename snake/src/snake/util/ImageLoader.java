package snake.util;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

/**
 * Utility class to load, resize, brighten and fade images.
 * @author Thomas Scheller, Markus Karolus
 */

public class ImageLoader
{
  private GraphicsConfiguration gc;

  public ImageLoader()
  {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
  }

  /**
   * Load the image, returning it as a BufferedImage
   * which is compatible with the graphics device being used.
   * Uses ImageIO.
   * @param filePath the path of the image file
   * @param fromResource true if the image should be loaded from a resource file
   * @return the loaded image
   */
  public BufferedImage loadImage(String filePath, boolean fromResource)
  {
    try
    {
      BufferedImage im = null;
      if (fromResource)
        im = ImageIO.read(getClass().getResource(filePath));
      else
        im = ImageIO.read(new File(filePath));
      return im;
    }
    catch (IOException e)
    {
      System.out.println("Load Image error for " + filePath + ":\n" + e);
      return null;
    }
  }

  /**
   * Create a new BufferedImage which is the input image, rotated
   * angle degrees clockwise.
   * An issue is edge clipping. The simplest solution is to design the
   * image with plenty of (transparent) border.
   * @param src the source image
   * @param angle the angle to rotate the image in degrees
   * @return the rotated image as a new BufferedImage object
   */
  public BufferedImage getRotatedImage(BufferedImage src, int angle)
  {
    if (src == null)
    {
      System.out.println("getRotatedImage: input image is null");
      return null;
    }

    int transparency = src.getColorModel().getTransparency();
    BufferedImage dest = gc.createCompatibleImage(
        src.getWidth(), src.getHeight(), transparency);
    Graphics2D g2d = dest.createGraphics();

    AffineTransform origAT = g2d.getTransform(); // save original transform

    // rotate the coord. system of the dest. image around its center
    AffineTransform rot = new AffineTransform();
    rot.rotate(Math.toRadians(angle), src.getWidth() / 2, src.getHeight() / 2);
    g2d.transform(rot);

    g2d.drawImage(src, 0, 0, null); // copy in the image

    g2d.setTransform(origAT); // restore original transform
    g2d.dispose();

    return dest;
  }

  /**
   * Draw the image with changed brightness, by using a RescaleOp.
   * Any alpha channel is unaffected.
   * @param src the source image
   * @param brightness new brightness value for the image (1.0 = no change)
   * @return the brightened image as a new BufferImage object
   */
  public BufferedImage getBrighterImage(BufferedImage src, float brightness)
  {
    if (src == null)
    {
      System.out.println("drawBrighterImage: input image is null");
      return null;
    }

    if (brightness == 1.0f)
    {
      //no change in brightness, return source image
      return src;
    }
    if (brightness < 0.0f)
    {
      System.out.println("Brightness must be >= 0.0f; setting to 0.5f");
      brightness = 0.5f;
    }
    // brightness may be less than 1.0 to make the image dimmer

    RescaleOp brighterOp;
    if (hasAlpha(src))
    {
      float[] scaleFactors = {brightness, brightness, brightness, 1.0f};
      // don't change alpha
      // without the 1.0f the RescaleOp fails, which is a bug (?)
      float[] offsets = {0.0f, 0.0f, 0.0f, 0.0f};
      brighterOp = new RescaleOp(scaleFactors, offsets, null);
    }
    else //not transparent
    {
      brighterOp = new RescaleOp(brightness, 0, null);

    }

    int transparency = src.getColorModel().getTransparency();
    BufferedImage dest = gc.createCompatibleImage(
        src.getWidth(), src.getHeight(), transparency);
    Graphics2D g2d = dest.createGraphics();
    g2d.drawImage(src, brighterOp, 0, 0);
    g2d.dispose();

    return dest;
  }

  /**
   * Checks an image if it has an alpha channel.
   * @param im the image to check
   * @return true if the image has an alpha channel
   */
  public boolean hasAlpha(BufferedImage im)
  {
    if (im == null)
    {
      return false;
    }

    int transparency = im.getColorModel().getTransparency();

    if ( (transparency == Transparency.BITMASK) ||
        (transparency == Transparency.TRANSLUCENT))
    {
      return true;
    }
    else
    {
      return false;
    }
  }

  /**
   * Draw a resized image by multiplying by widthChange and heightChange.
   * @param g2d the graphics object where the image should be drawn
   * @param im the source image
   * @param x x coordinate to draw the image
   * @param y y coordinate to draw the image
   * @param widthChange multiplication factor for the width of the image
   * @param heightChange multiplication factor for the height of the image
   */
  public void drawResizedImage(Graphics2D g2d, BufferedImage im, int x, int y, double widthChange, double heightChange)
  {
    if (im == null)
    {
      System.out.println("drawResizedImage: input image is null");
      return;
    }

    if (widthChange <= 0)
    {
      System.out.println("width change cannot <= 0");
      widthChange = 1.0;
    }
    if (heightChange <= 0)
    {
      System.out.println("height change cannot <= 0");
      heightChange = 1.0;
    }

    int destWidth = (int) (im.getWidth() * widthChange);
    int destHeight = (int) (im.getHeight() * heightChange);

    // adjust top-left (x,y) coord of resized image so it remains centered
    int destX = x + im.getWidth() / 2 - destWidth / 2;
    int destY = y + im.getHeight() / 2 - destHeight / 2;

    g2d.drawImage(im, destX, destY, destWidth, destHeight, null);
  }

  /**
   * Create a resized Image, keeping the correct proportions (so the resulting image
   * may not have both of the given max width and height values, but at least one of them).
   * @param im the source image
   * @param maxWidth  the maximum width for the resized image
   * @param maxHeight the maximum height for the resized image
   * @return the resized image
   */
  public BufferedImage getResizedImage(BufferedImage im, int maxWidth, int maxHeight)
  {
    int height = im.getHeight();
    int width = im.getWidth();
    double verhaeltnis;
    if (height > width)
    {
      verhaeltnis = (double)height / (double)maxHeight ;
      height = maxHeight;
      width = (int)( width / verhaeltnis);
    }
    else
    {
      verhaeltnis = (double)width / (double)maxWidth ;
      height = (int)( height / verhaeltnis);
      width = maxWidth;
    }

    int transparency = im.getColorModel().getTransparency();
    BufferedImage dest = gc.createCompatibleImage(width, height, transparency);
    Graphics2D g2d = dest.createGraphics();

    g2d.drawImage(im, 0, 0, width, height, null);
    g2d.dispose();

    return dest;
  }

  /**
   * Draw a partialy transparent image.
   * @param g2d the graphics object where the image is drawn
   * @param im the source image
   * @param x x coordinate to draw the image
   * @param y y coordinate to draw the image
   * @param alpha specifies the degree of fading; alpha == 1 means fully visible, 0 mean invisible
   */
  public void drawFadedImage(Graphics2D g2d, BufferedImage im, int x, int y, float alpha)
  {
    if (im == null)
    {
      System.out.println("drawFadedImage: input image is null");
      return;
    }

    if (alpha < 0.0f)
    {
      System.out.println("Alpha must be >= 0.0f; setting to 0.0f");
      alpha = 0.0f;
    }
    else if (alpha > 1.0f)
    {
      System.out.println("Alpha must be <= 1.0f; setting to 1.0f");
      alpha = 1.0f;
    }

    Composite c = g2d.getComposite(); // backup the old composite

    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
    g2d.drawImage(im, x, y, null);

    g2d.setComposite(c); // restore the old composite so it doesn't mess up future rendering
  }

  /**
   * Draw a both resized an faded image.
   * @param g2d Graphics2D
   * @param g2d the graphics object where the image is drawn
   * @param im the source image
   * @param x x coordinate to draw the image
   * @param y y coordinate to draw the image
   * @param widthChange multiplication factor for the width of the image
   * @param heightChange multiplication factor for the height of the image
   * @param alpha specifies the degree of fading; alpha == 1 means fully visible, 0 mean invisible
   */
  public void drawResizedFadedImage(Graphics2D g2d, BufferedImage im, int x, int y, double widthChange, double heightChange, float alpha)
  {
    if (im == null)
    {
      System.out.println("drawResizedFadedImage: input image is null");
      return;
    }

    if (widthChange <= 0)
    {
      System.out.println("width change cannot <= 0");
      widthChange = 1.0;
    }
    if (heightChange <= 0)
    {
      System.out.println("height change cannot <= 0");
      heightChange = 1.0;
    }

    if (alpha < 0.0f)
    {
      System.out.println("Alpha must be >= 0.0f; setting to 0.0f");
      alpha = 0.0f;
    }
    else if (alpha > 1.0f)
    {
      System.out.println("Alpha must be <= 1.0f; setting to 1.0f");
      alpha = 1.0f;
    }

    int destWidth = (int) (im.getWidth() * widthChange);
    int destHeight = (int) (im.getHeight() * heightChange);

    // adjust top-left (x,y) coord of resized image so it remains centered
    int destX = x + im.getWidth() / 2 - destWidth / 2;
    int destY = y + im.getHeight() / 2 - destHeight / 2;

    Composite c = g2d.getComposite(); // backup the old composite

    g2d.setComposite(AlphaComposite.getInstance(
        AlphaComposite.SRC_OVER, alpha));

    g2d.drawImage(im, destX, destY, destWidth, destHeight, null);

    g2d.setComposite(c); // restore the old composite so it doesn't mess up future rendering
  }
}
