package client.data;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mzs.data.SnakeDataHolder;

public class Snake {
	private ArrayList<SnakePart> snakeParts = new ArrayList<SnakePart>();

	private float direction = 45;
	private int distance = 18;

	private UUID id;
	// represents snake in space
	private SnakeDataHolder dataHolder;
	private Dimension gamePanelDimension;
	
	private static Logger log = LoggerFactory.getLogger(Snake.class);
	
	/**
	 * constructor should only be used for:
	 * 	- comparison (equals)
	 * 	- to create remote snakes loacally
	 * 
	 * 	Snake can not be used as "instance" if created using this constructor
	 * 
	 * @TODO ist das eine bessere lšsung als bei equals immer snake.getId().equals zu machen? i think so!
	 * 
	 * @param id
	 */
	public Snake(UUID id)	{
		this.id = id;
	}
	
	public Snake() {
		id = UUID.randomUUID();
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

	/** GETTER / SETTER **/
	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getId() {
		return id;
	}
	
	public float getDirection() {
		return direction;
	}
	
	public void setDirection(float d) {
		direction = d;
	}
	
	public ArrayList<SnakePart> getSnakeParts() {
		return snakeParts;
	}

	public void setSnakeParts(ArrayList<SnakePart> snakeParts) {
		this.snakeParts = snakeParts;
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
	
	/**
	 * sets the dimensions of the gamepanel, set in GamePanel for each Snake
	 * 	snake needs this information to calculate collision
	 * 
	 * @param Dimension size
	 */
	public void setGamePanelDimensions(Dimension size) {
		this.gamePanelDimension = size;
	}
	
	/** END GETTER / SETTER **/
	
	/**
	 * stores all snake data to snakeDataHolder, which can be shared over a space
	 * 
	 * @return SnakeDataHolder
	 */
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
		int newPosX = (int)(head.getX() - xOffset),
			newPosY = (int)(head.getY() - yOffset);
		
//		log.info("move to: " + newPosX + "/" + newPosY);
		
		/** @credit: Thomas Scheller, Markus Karolus */
		
		//head out of left border?
		if (newPosX < -5)	{
			
			/*** @ÊTODO check if new position is free (collision detection)
			if (gameMap.freePlaceSnake( (int) (pWidth - newPosX), (int) (newPosY)))
			*/
			newPosX += gamePanelDimension.getWidth();
		}
		//head out of right border?
		else if (newPosX > gamePanelDimension.getWidth())	{
			/*** @ÊTODO check if new position is free (collision detection)
			if (gameMap.freePlaceSnake( (int) (newPosX - pWidth), (int) (newPosY)))
			*/
			newPosX -= gamePanelDimension.getWidth();
		}
		//head out of top border?
		if (newPosY < -5)	{
			/*** @ÊTODO check if new position is free (collision detection)
			if (gameMap.freePlaceSnake( (int) (newPosX), (int) (pHeight - newPosY)))
			*/
			newPosY += gamePanelDimension.getHeight();
		}
		//head out of bottom border?
		else if (newPosY > gamePanelDimension.getHeight())	{
			/*** @ÊTODO check if new position is free (collision detection)
			if (gameMap.freePlaceSnake( (int) (newPosX), (int) (newPosY - pHeight)))
			*/
			newPosY -= gamePanelDimension.getHeight();
		}
		
//		log.info("	move to: " + newPosX + "/" + newPosY);
		
		//set new head position
		tail.setX(newPosX);
		tail.setY(newPosY);
		tail.setDirection(getDirection());

		// unshift
		snakeParts.add(0, tail);
	}

	/**
	 * move snake depending on keyEvent
	 * @param e
	 */
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
	
	
	/**
	 * called for remote snakes, to adopt position
	 * 
	 * @TODO make sure, that own player snake does not get updated (MUST be done in notification - before update call)
	 * 
	 * @param SnakeDataHolder sdh
	 */
	public void updateSnakeParts(SnakeDataHolder sdh) {
		
		// check for 2 parts (head and tail)
		if(sdh.getParts().size() > 2)	{
			this.setSnakeParts(sdh.getParts());
		} else	{
			// set new head and tail
			
			/** 
			 * @TODO what if snake gets longer? at least the length must be provided in snakedataholder
			 * 	=> or all snake parts are written to space each time
			 */
			
			// remove tail
			SnakePart tail = snakeParts.remove(snakeParts.size()-1);
			
			// use tail as new head, lulz
			// unshift
			snakeParts.add(0, sdh.getParts().get(0));
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Snake other = (Snake) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
