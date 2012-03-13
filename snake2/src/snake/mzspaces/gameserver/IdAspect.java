package snake.mzspaces.gameserver;

import java.util.concurrent.atomic.AtomicInteger;

import org.mozartspaces.capi3.Capi3AspectPort;
import org.mozartspaces.capi3.SubTransaction;
import org.mozartspaces.capi3.Transaction;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.aspects.AbstractContainerAspect;
import org.mozartspaces.core.aspects.AspectResult;
import org.mozartspaces.core.requests.WriteEntriesRequest;

import snake.data.Game;

/**
 * Container aspect, which adds ids to the entries
 *
 */
public class IdAspect extends AbstractContainerAspect {

	private static final long serialVersionUID = 2125907508047200299L;

	/***
	 * static variables?
	 * 	sauberer => eine instanz pro needed unique id
	 */
	private static AtomicInteger gameID = new AtomicInteger(0);
	
	public IdAspect()	{}
	
	@Override
	public AspectResult preWrite(WriteEntriesRequest request, Transaction tx,
			SubTransaction stx, Capi3AspectPort capi3, int executionCount) {
		
		for (Entry e : request.getEntries()) {
			if (e.getValue() instanceof Game) {
				Game g = (Game) e.getValue();
				if (!g.hasNr()) {
					g.setNr(gameID.incrementAndGet());
//					System.out.println("set game id: " + eggID.get());
				}
			}
		}
		return AspectResult.OK;
	}
	
}
