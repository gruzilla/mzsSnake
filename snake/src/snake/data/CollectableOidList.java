package snake.data;

import corso.lang.*;

/**
 * List of oids of the positions of the collectables, that can be saved to
 * space as a CorsoShareable.
 * @author Thomas Scheller, Markus Karolus
 */
public class CollectableOidList implements CorsoShareable
{
	private final String structName = "snakeCollectableOidListDataStruct";
	private int collectableCount = 0;

	public CorsoVarOid[] oidList;

	/**
	 * Create a new list with the given number of collectables.
	 * @param aCollectableCount number of collectables (depends on the level that is played)
	 */
	public CollectableOidList(int aCollectableCount)
	{
		collectableCount = aCollectableCount;
		oidList = new CorsoVarOid[collectableCount];
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
		if (arity != collectableCount)
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
		//create struct with name and size
		data.putStructTag(structName, collectableCount);

		//write data
		for (int i = 0; i < oidList.length; i++)
		{
			data.putShareable(oidList[i]);
		}
	}
}
