package mzs.util;

/**
 * Different types of changed data at a DataChangeEvent.
 * @author Thomas Scheller, Markus Karolus
 */

public enum DataChangeType
{
  game,//data of a game has been changed
  player, //data of a player has been changed
  undefined //data has been changed, type of data is unknown
}
