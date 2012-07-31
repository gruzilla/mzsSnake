/**
 * 
 */
package mzs.event.i;

import mzs.event.DataChangeEventData;

/**
 * @author Jakob Lahmer, Matthias Steinbšck
 *
 */
public interface DataChangeEventListener {
	public void dataChanged(DataChangeEventData changeEvent);
}
