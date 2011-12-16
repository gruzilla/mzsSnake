package snake.corso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.LindaCoordinator;

public class ContainerCoordinatorMapper {
	public static final String HIGH_SCORE = "highScore";
	public static final String GAME_LIST = "gameList";
	public static final String LEVEL_DATA = "levelData";
	
	private static HashMap<String, List<Coordinator>> map = new HashMap<String, List<Coordinator>>();
	
	static {
		// define coordinators for
		// GAME_LIST
		ArrayList<Coordinator> list = new ArrayList<Coordinator>();
		list.add(new FifoCoordinator());
		list.add(new LindaCoordinator());
		map.put(GAME_LIST, list);
		
		ArrayList<Coordinator> list2 = new ArrayList<Coordinator>();
		list2.add(new FifoCoordinator());
		map.put(LEVEL_DATA, list2);
	}

	public static List<Coordinator> getCoordinators(String containerName) {
		return map.get(containerName);
	}
}
