package client.data;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Snake {
	private ArrayList<SnakePart> snakeParts = new ArrayList<SnakePart>();
	private float direction = 45;
	private int distance = 18;

	public Snake() {
		SnakePart head = new SnakePart();
		head.setX(100);
		head.setY(200);
		head.setDirection(getDirection());
		snakeParts.add(head);
		snakeParts.add(new SnakePart());
		snakeParts.add(new SnakePart());
		snakeParts.add(new SnakePart());
		snakeParts.add(new SnakePart());

		calculateParts();
	}
	
	public float getDirection() {
		return direction ;
	}
	
	public void setDirection(float d) {
		direction = d;
	}
	
	public ArrayList<SnakePart> getParts() {
		return snakeParts;
	}

	/**
	 * calculate X and Y values of the other snake parts depending on the head-part
	 */
	protected void calculateParts() {
		SnakePart head = snakeParts.get(0);

		int startX = head.getX();
		int startY = head.getY();

		head.setDirection(getDirection());

		for (int i = 1; i < snakeParts.size(); i++) {
			SnakePart part = snakeParts.get(i);
			
			double xOffset = Math.sin((360 - getDirection()) * Math.PI / 180) * distance;
			double yOffset = Math.cos((360 - getDirection()) * Math.PI / 180) * distance;
			
			part.setX((int)(startX + xOffset * i));
			part.setY((int)(startY + yOffset * i));
			part.setDirection(getDirection());
		}
	}

	public void moveForward(int forward) {
		double xOffset = Math.sin((360 - getDirection()) * Math.PI / 180) * distance;
		double yOffset = Math.cos((360 - getDirection()) * Math.PI / 180) * distance;
		
		SnakePart head = snakeParts.get(0);

		// pop
		SnakePart tail = snakeParts.remove(snakeParts.size()-1);

		// use tail as new head, lulz
		tail.setX((int)(head.getX() - xOffset));
		tail.setY((int)(head.getY() - yOffset));
		tail.setDirection(getDirection());

		// unshift
		snakeParts.add(0, tail);
	}

	public void move(KeyEvent e) {
		int key = e.getKeyCode();

		switch (key) {
		case KeyEvent.VK_LEFT:
			direction -= 15;
			break;
		case KeyEvent.VK_RIGHT:
			direction += 15;
			break;
		}

		direction %= 360;
	}
}
