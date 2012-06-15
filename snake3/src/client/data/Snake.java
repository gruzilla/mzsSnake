package client.data;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.UUID;

import mzs.data.SnakeDataHolder;

public class Snake {
	private ArrayList<SnakePart> snakeParts = new ArrayList<SnakePart>();
	private float direction = 45;
	private int distance = 18;

	private UUID id = UUID.randomUUID();
	// represents snake in space
	private SnakeDataHolder dataHolder;
	
	public Snake() {
		
		// dataholder has same id as Snake (SnakeDataHolder represents snake in space)
		dataHolder = new SnakeDataHolder(getId());
		
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
		return direction;
	}
	
	public void setDirection(float d) {
		direction = d;
	}
	
	public ArrayList<SnakePart> getParts() {
		return snakeParts;
	}
	
	public SnakePart getHeadPart()	{
		return this.snakeParts.get(0);
	}
	
	public SnakePart getTailPart()	{
		return this.snakeParts.get(this.snakeParts.size() - 1);
	}
	
	public SnakeDataHolder getSnakeDataHolder()	{
		return getSnakeDataHolder(false);
	}
	
	public SnakeDataHolder getSnakeDataHolder(boolean full)	{
		
		dataHolder.reset();
		if(full)	{
			dataHolder.setParts(this.snakeParts);
		} else	{
			dataHolder.addPart(this.getHeadPart());
			dataHolder.addPart(this.getTailPart());
		}
		return this.dataHolder;
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

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getId() {
		return id;
	}
}
