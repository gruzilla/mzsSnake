package snake.data;

/**
 * Different states of a game.
 * @author Thomas Scheller, Markus Karolus
 */
public enum GameState
{
	opened, //das Spiel wurde geöffnet, Spieler können beitreten
	ready, //das Spiel wurde vom Leiter zum Start freigegeben, es wird gestartet sobald alle Spieler bereit sind
	running, //das Spiel läuft, es können keine Spieler mehr beitreten
	aktiv, //Spiel gestartet
	ended, //das Spiel wurde beendet
	unknown //unbekannter oder fehlerhafter Status
}
