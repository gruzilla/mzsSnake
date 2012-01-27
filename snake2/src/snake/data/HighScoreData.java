package snake.data;

/**
 * The HighScoreData class represents one highscoreobject in the highscore list.
 * @author Thomas Scheller, Markus Karolus
 */
public class HighScoreData
{
  private String name;
  private float points;

  /**
   * Default constructor for a newly created highscorlist entry.
   * @param name String Name of the player
   * @param points float Reached points
   */
  public HighScoreData(String name, float points)
  {
    this.name = name;
    this.points = points;
  }

  /**
   * Default constructor for a newly created highscorlist entry.
   */
  public HighScoreData()
  {
    this.name = "";
    this.points = 0;
  }

  public String getName()
  {
    return name;
  }

  public float getPoints()
  {
    return points;
  }

}
