package mzs.event;

import java.util.Vector;

import client.data.game.Game;

public class DataChangeEventGameListData extends DataChangeEventData {

	private Vector<Game> gameListData;
	
	public DataChangeEventGameListData(DataChangeEventType changeType)	{
		super(changeType);
	}
	
	public DataChangeEventGameListData(DataChangeEventType changeType, Vector<Game> gameListData)	{
		super(changeType);
		this.gameListData = gameListData;
	}

	/**
	 * @return the gameListData
	 */
	public Vector<Game> getGameListData() {
		return gameListData;
	}

	/**
	 * @param gameListData the gameListData to set
	 */
	public void setGameListData(Vector<Game> gameListData) {
		this.gameListData = gameListData;
	}
	
}
