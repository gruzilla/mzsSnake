package snake.data;

/**
 * Event information that is given to a class implementing die IDataChangeListener.
 * @author Thomas Scheller, Markus Karolus
 */
public class DataChangeEvent
{
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
