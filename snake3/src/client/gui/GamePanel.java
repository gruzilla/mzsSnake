package client.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import client.data.Snake;
import client.gui.graphics.ImageLoader;

public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private BufferedImage gameMap;
	private Snake[] snakes;

	public GamePanel(Snake[] snakes) {
		this.snakes = snakes;

		ImageLoader loader = new ImageLoader();
		gameMap = loader.loadImage("res/levels/Level1/back.jpg", false);

		Dimension size = new Dimension(gameMap.getWidth(null), gameMap.getHeight(null));
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setLayout(null);
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
