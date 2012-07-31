package mzs.event;

import client.data.game.Game;

public class DataChangeEventGameData extends DataChangeEventData {

	private Game game;
	
	public DataChangeEventGameData(DataChangeEventType changeType)	{
		super(changeType);
	}
	
	public DataChangeEventGameData(DataChangeEventType changeType, Game game)	{
		super(changeType);
		this.game = game;
	}

	/**
	 * @return the gameListData
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * @param game
	 */
	public void setGameListData(Game game) {
		this.game = game;
	}
	
}
