package mzs.util;

public class DataChangeEvent extends Exception {

	private static final long serialVersionUID = 1L;

	Object changedObj = null;
	DataChangeType changeType = null;

	public DataChangeEvent(Object changedObj, DataChangeType changeType)
	{
		this.changedObj = changedObj;
		this.changeType = changeType;
	}

	public Object getObject()
	{
		return changedObj;
	}

	public DataChangeType getType()
	{
		return changeType;
	}
}
