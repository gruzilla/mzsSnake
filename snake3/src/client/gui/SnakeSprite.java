package client.gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import client.data.Snake;
import client.data.SnakePart;
import client.gui.graphics.ImageLoader;

public class SnakeSprite {
	private Snake snake;

	public SnakeSprite(Snake snake) {
		this.snake = snake;
	}

	public void draw(Graphics g) {
		ImageLoader snakePartImageLoader = new ImageLoader();
		BufferedImage part = snakePartImageLoader.loadImage("res/skins/Snake/snakepart.png", false);
		BufferedImage head = snakePartImageLoader.loadImage("res/skins/Snake/snakehead.png", false);
		BufferedImage tail = snakePartImageLoader.loadImage("res/skins/Snake/snaketail.png", false);
		
		
		for (int i = 0; i < snake.getParts().size(); i++) {
			SnakePart p = snake.getParts().get(i);

			BufferedImage image = part;
			if (i == 0) image = head;
			if (i == snake.getParts().size()-1) image = tail;

			//System.out.println("drawing on "+p.getX()+" "+p.getY()+" "+p.getDirection());
			g.drawImage(
				snakePartImageLoader.getRotatedImage(image, (int) p.getDirection()),
				p.getX(),
				p.getY(),
				null
			);
		}
	}
}
