package mzs.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import client.data.SnakePart;

public class SnakeDataHolder implements Serializable {

	private static final long serialVersionUID = 1L;

	private ArrayList<SnakePart> parts;

	// id equals snake id and should not be changed
	private UUID id;
	
	
	public SnakeDataHolder(UUID id) 	{
		this.id = id;
		this.parts = new ArrayList<SnakePart>();
	}

	public void reset()	{
		parts = new ArrayList<SnakePart>();
	}

	public void addPart(SnakePart part)	{
		parts.add(part);
	}
	
	public void setParts(ArrayList<SnakePart> parts) {
		this.parts = parts;
	}


	public ArrayList<SnakePart> getParts() {
		return parts;
	}

	@Override
	public String toString() {
		return "SnakeDataHolder [parts=" + parts + ", id=" + id + "]";
	}
	
}
