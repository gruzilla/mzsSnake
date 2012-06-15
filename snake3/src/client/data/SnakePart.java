package client.data;

import java.io.Serializable;

public class SnakePart implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int x;
	private int y;
	private float d;

	public void setX(int i) {
		x = i;
	}

	public void setY(int i) {
		y = i;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public float getDirection() {
		return d;
	}
	
	public void setDirection(float d) {
		this.d = d;
	}
}
