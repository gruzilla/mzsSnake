package client.data;

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

	private UUID id = UUID.randomUUID();
	// represents snake in space
	private SnakeDataHolder dataHolder;
	
	private static Logger log = LoggerFactory.getLogger(Snake.class);
	
	/**
	 * constructor should only be used for comparison (equals)
	 * 
	 * 	Snake can not be used as Instance if created using this constructor
	 * 
	 * @TODO bessere lšsung als equals immer snake.getId().equals zu machen?
	 * 
	 * @param id
	 */
	public Snake(UUID id)	{
		this.id = id;
	}
	
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
	/** END GETTER / SETTER **/
	
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
	
	
	/**
	 * called for remote snakes, to adopt position
	 * 
	 * @TODO make sure, that own player snake does not get updated (MUST be done in notification - before update call)
	 * 
	 * @param sdh
	 */
	public void updateSnakeParts(SnakeDataHolder sdh) {
		
		log.info("UPDATE SNAKE PARTS");
		// check for 2 parts (head and tail
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
