package mzs.event;

/**
 * Different types of changed data at a DataChangeEvent.
 * @author Thomas Scheller, Markus Karolus
 */

public enum DataChangeEventType	{
	GAME,//data of a game has been changed
	PLAYER, //data of a player has been changed
	UNDEFINED //data has been changed, type of data is unknown
}
