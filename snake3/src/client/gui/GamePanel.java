package client.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.UUID;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mzs.data.SnakeDataHolder;

import client.data.Snake;
import client.gui.graphics.ImageLoader;

/**
 * represents the GamePanel, where everything is drawn
 * 
 * @author Jakob Lahmer, Matthias Steinbšck
 *
 */
public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private BufferedImage gameMap;
	private Dimension gameMapSize;
	private ArrayList<Snake> snakes;

	Logger log = LoggerFactory.getLogger(GamePanel.class);
	
	
	public GamePanel() {
		this.snakes = new ArrayList<Snake>();
		
		ImageLoader loader = new ImageLoader();
		gameMap = loader.loadImage("res/levels/Level1/back.jpg", false);

		gameMapSize = new Dimension(gameMap.getWidth(null), gameMap.getHeight(null));
		setPreferredSize(gameMapSize);
		setMinimumSize(gameMapSize);
		setMaximumSize(gameMapSize);
		setLayout(null);
		
	}

	public void addSnake(Snake s)	{
		snakes.add(s);
		s.setGamePanelDimensions(gameMapSize);
	}
	
	public boolean hasSnake(UUID id)	{
		for(Snake s : snakes)	{
			if(s.equals(new Snake(id)))
				return true;
		}
		return false;
	}
	
	public void updateSnake(SnakeDataHolder sdh) {
		for(Snake s : snakes)	{
			if(s.equals(new Snake(sdh.getId())))
				s.updateSnakeParts(sdh);
		}
	}
	
	
	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(gameMap, 0, 0, null);
		for(Snake s : this.snakes)	{
			SnakeSprite sprite = new SnakeSprite(s);
			sprite.draw(g);
		}
	}
}
