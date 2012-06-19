/**
 * 
 */
package client.gui.graphics;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;

/**
 * @author Jakob Lahmer, Matthias Steinbšck
 *
 */
public class GraphicsHelper {

	
	/**
	 * Helper method to draw a background window. The window positions itself
	 * in the center and is half-transparent.
	 * @param g2d Graphics2D object where the Window should be drawn
	 * @param boxDimensions - the dimensions of the box to be shown
	 * @param frameDimension - the dimension of the parent frame
	 */
	public static void drawWindow(Graphics2D g2d, Dimension boxDimensions, Dimension frameDimensions)
	{
		//helper method to draw a background window
		int ypos = (frameDimensions.height - boxDimensions.height) / 2;
		int xpos = (frameDimensions.width - boxDimensions.width) / 2;
		Composite c = g2d.getComposite(); // backup the old composite
		g2d.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 0.5f));
		g2d.setColor(Color.WHITE);
		g2d.fillRect(xpos, ypos, boxDimensions.width, boxDimensions.height);
		g2d.setColor(Color.darkGray);
		g2d.drawRect(xpos, ypos, boxDimensions.width, boxDimensions.height);
		g2d.setComposite(c); // restore the old composite so it doesn't mess up future rendering
	}
	
}
