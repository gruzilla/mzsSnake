package mzs.event;

public class DataChangeEventData {

	private static final long serialVersionUID = 1L;

	DataChangeEventType changeType = null;

	public DataChangeEventData(DataChangeEventType changeType)
	{
		this.changeType = changeType;
	}

	public DataChangeEventType getType()
	{
		return changeType;
	}
}
