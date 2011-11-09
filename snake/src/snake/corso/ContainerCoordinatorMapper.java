package snake.corso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.LindaCoordinator;

public class ContainerCoordinatorMapper {
	public static String GAME_LIST = "gameList";
	
	private static HashMap<String, List<Coordinator>> map = new HashMap<String, List<Coordinator>>();
	
	static {
		
		ArrayList<Coordinator> list = new ArrayList<Coordinator>();
		list.add(new FifoCoordinator());
		list.add(new LindaCoordinator());
		map.put(GAME_LIST, list);
	}

	public static List<Coordinator> getCoordinators(String containerName) {
		return map.get(containerName);
	}
}
