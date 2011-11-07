package snake.corso;

import java.io.*;
import java.util.*;
import java.text.*;
/**
 * log class for simpler error detection
 * @author Thomas Scheller, Markus Karolus
 */
public class SnakeLog
{
  private SimpleDateFormat df;
  private String fileName = "snake.log";
  private FileWriter writer = null;

  /**
  * Default constructor for a newly created snake log object
  */
  public SnakeLog()
  {

    df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
    try
     {
       writer = new FileWriter(fileName,true);
       writeLogEntry("----- Start Logging -----");
     }
     catch (IOException ex)
    {
      System.err.println("Error: Can't create log file");
      writer = null;
    }
  }

  protected void finalize() throws Throwable
  {
    close();
  }

  /**
   * Write log entry with timestamp to log
   *
   * @param info String
   */
  public void writeLogEntry(String info)
  {
    if (writer != null)
    {
      java.util.Calendar cal = java.util.Calendar.getInstance();
      try
      {
        writer.write(df.format(cal.getTime()) + ": " + info + "\r\n");
        writer.flush();
      }
      catch (IOException ex)
     {
       System.err.println("Error: Can't write to log file");
     }
    }
  }

  /**
   * Close the log file
   */
  public void close()
  {
    if (writer != null)
    {
      try
      {
        writeLogEntry("----- Stop Logging -----");
        writer.flush();

        writer.close();
      }
      catch (IOException ex)
      {
        System.err.println("Error: Can't close log file");
      }
      writer = null;
    }
  }

  /**
  * Write all buffed data into to file
  */
 public void flush()
 {
   if (writer != null)
   {
     try
     {
       writer.flush();
     }
     catch (IOException ex)
     {
       System.err.println("Error: Can't flush log file");
     }
   }
 }

}
