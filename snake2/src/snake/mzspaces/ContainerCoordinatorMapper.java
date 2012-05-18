package snake.mzspaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.KeyCoordinator;
import org.mozartspaces.capi3.LindaCoordinator;

public class ContainerCoordinatorMapper {
	// list of all games
	public static final String GAME_LIST = "gameList";
	public static final String LEVEL_DATA = "levelData";
	
	// mMn nicht benoetigt?
	public static final String HIGH_SCORE = "highScore";
	
	// container fuer ein spiel (beinhaltet: player, gamestate etc)
	public static final String GAME = "game";
	public static final String PLAYER = "player";
	
	private static HashMap<String, List<Coordinator>> map = new HashMap<String, List<Coordinator>>();
	
	static {
		// GAME_LIST
		ArrayList<Coordinator> list = new ArrayList<Coordinator>();
		// list.add(new AnyCoordinator());
		list.add(new FifoCoordinator());
		list.add(new KeyCoordinator());
		//list.add(new LindaCoordinator());
		map.put(GAME_LIST, list);
		
		list = new ArrayList<Coordinator>();
		list.add(new FifoCoordinator());
		map.put(LEVEL_DATA, list);

		list = new ArrayList<Coordinator>();
		list.add(new FifoCoordinator());
		map.put(PLAYER, list);

		// GAME
		// each (network) game gets its own container to share playerinfo etc
		list = new ArrayList<Coordinator>();
		list.add(new FifoCoordinator());
		map.put(GAME, list);
	}

	public static List<Coordinator> getCoordinators(String containerName) {
		return map.get(containerName);
	}
}
