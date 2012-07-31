package util;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * Utility class to display Messages with JOptionPane.
 * @author Thomas Scheller, Markus Karolus
 */

public class Messages
{
  /**
   * Displays an error message with "Fehler" as title and error icon.
   * @param owner the owner of the message
   * @param message the errormessage
   */
  public static void errorMessage(Component owner, String message)
  {
    JOptionPane.showMessageDialog(owner, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Displays an info message with "Information" as title and information icon.
   * @param owner the owner of the message
   * @param message the info message
   */
  public static void infoMessage(Component owner, String message)
  {
    JOptionPane.showMessageDialog(owner, message, "Information", JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Displays a question message with yes/no buttons and "Frage" as title.
   * @param owner the owner of the message
   * @param message the displayed question
   * @return true if yes was chosen in the message box
   */
  public static boolean yesNoMessage(Component owner, String message)
  {
    int option = JOptionPane.showConfirmDialog(owner, message, "Question", JOptionPane.YES_NO_OPTION);
    return (option == JOptionPane.YES_OPTION);
  }
}
