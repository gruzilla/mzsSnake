package mzs.data;

/**
 * Different states of a game.
 * @author Thomas Scheller, Markus Karolus
 */
public enum GameState
{
	OPENEND, //das Spiel wurde geöffnet, Spieler können beitreten
	READY, //das Spiel wurde vom Leiter zum Start freigegeben, es wird gestartet sobald alle Spieler bereit sind
	RUNNING, //das Spiel läuft, es können keine Spieler mehr beitreten
	ACTIVE, //Spiel gestartet
	ENDED, //das Spiel wurde beendet
	UNKNOWN //unbekannter oder fehlerhafter Status
}
