package snake.data;

import corso.lang.*;

/**
 * List of all oids of the positions of a snake, helps making it easier to save
 * the oids to space in one CorsoShareable object.
 * @author Thomas Scheller, Markus Karolus
 */
public class SnakeOidList implements CorsoShareable
{
	private final String structName = "snakeOidListDataStruct";
	private final int defaultStructSize = snake.SnakeSpriteData.MAXPOINTS;

	private int structSize = defaultStructSize;
	public CorsoVarOid[] oidList = null;

	public SnakeOidList()
	{
		oidList = new CorsoVarOid[structSize];
	}

	/**
	 * Create new SnakeOidList for a number of part oids equal to the structsize.
	 * @param structSize size of the corsodata, and number of snake parts
	 */
	public SnakeOidList(int structSize)
	{
		this.structSize = structSize;
		oidList = new CorsoVarOid[structSize];
	}

	/**
	 * Read the the object from CorsoSpace.
	 * @param data CorsoData
	 * @throws CorsoDataException
	 */
	public void read(CorsoData data) throws CorsoDataException
	{
		StringBuffer name = new StringBuffer("");

		//control expected struct size
		int arity = data.getStructTag(name);
		if (arity != structSize)
		{
			throw new CorsoDataException();
		}
		//control expected struct name
		if (!name.toString().equals(structName))
		{
			throw new CorsoDataException();
		}

		//read data
		for (int i = 0; i < oidList.length; i++)
		{
			oidList[i] = new CorsoVarOid();
			data.getShareable(oidList[i]);
		}
	}

	/**
	 * Write the object to CorsoSpace.
	 * @param data CorsoData
	 * @throws CorsoDataException
	 */
	public void write(CorsoData data) throws CorsoDataException
	{
		//Struct anlegen, Namen und Größe angeben
		data.putStructTag(structName, structSize);

		//Daten schreiben
		for (int i = 0; i < oidList.length; i++)
		{
			data.putShareable(oidList[i]);
		}
	}

	/**
	 * Create a new array of oids from the Vector by copying all oids from the Vector
	 * to the array. The structsize is set to the size of the vector.
	 * @param oids Vector with snake part oids
	 */
	public void createOidArray(java.util.Vector<CorsoVarOid> oids)
	{
		this.structSize = oids.size();
		oidList = new CorsoVarOid[oids.size()];
		for (int i = 0; i < oids.size(); i++)
		{
			oidList[i] = (CorsoVarOid)oids.elementAt(i);
		}
	}
}
