package snake.data;

/**
 * Class representing the starting point of a snake with coordinates and direction.
 * @author Thomas Scheller, Markus Karolus
 */
public class SnakeStartPoint
{
  int x = 0;
  int y = 0;
  int degree = 0;

  /**
   * Create a Startpoint from a String that is read from a level property file.
   * @param propertyString startpoint string from property file
   */
  public SnakeStartPoint(String propertyString)
  {
    try
    {
      String[] parts = propertyString.split(">");
      degree = Integer.parseInt(parts[1]);
      parts = parts[0].split("x");
      x = Integer.parseInt(parts[0]);
      y = Integer.parseInt(parts[1]);
    }
    catch (Exception err)
    {
      System.out.println("Startpoint settings: Error occured: " + propertyString + " "+err.toString());
    }
  }

  /**
   * Create a new Startpoint by settings the values manually.
   * @param newX x-coordinate
   * @param newY y-coordinate
   * @param newDegree direction in degrees
   */
  public SnakeStartPoint(int newX, int newY, int newDegree)
  {
    x = newX;
    y = newY;
    degree = newDegree;
  }

  public int getX()
  {
    return x;
  }

  public int getY()
  {
    return y;
  }

  public int getDegree()
  {
    return degree;
  }

}
