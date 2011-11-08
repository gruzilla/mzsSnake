package snake.corso;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.FifoCoordinator.FifoSelector;
import org.mozartspaces.core.MzsConstants.Selecting;

public class ContainerCoordinatorMapper {
	public static String GAME_LIST = "gameList";
	
	private static HashMap<String, List<? extends Coordinator>> map = new HashMap<String, List<? extends Coordinator>>();
	
	static {
		
		ArrayList<FifoSelector> list = new ArrayList<FifoCoordinator.FifoSelector>();
		list.add(FifoCoordinator.newSelector(Selecting.COUNT_ALL));
		map.put(GAME_LIST, list);
	}

	public static List<?> getCoordinators(String containerName) {
		return map.get(containerName);
	}
}
