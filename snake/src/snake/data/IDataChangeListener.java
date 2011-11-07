package snake.data;

/**
 * The DataChangeListener Interface can be implemented by a class so it can be registered
 * at another class that trows these types of events.
 * @author Thomas Scheller, Markus Karolus
 */
public interface IDataChangeListener
{
    /**
     * Called by another class when certain data has changed. The DataChangedEvent
     * specifies what changes occured.
     * @param event DataChangeEvent
     */
    public void dataChanged(DataChangeEvent event);
}
