package snake.ui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;
import snake.Snake;

/**
 * Panel that displays the main menu of the game.
 * @author Thomas Scheller, Markus Karolus
 */
public class MenuePanel extends JPanel
{
  private Snake snakeMain = null;

  JLabel laTitel = new JLabel();
  JButton btMultiplayer = new JButton();
  JButton btSingleplayer = new JButton();
  JButton btEinstellungen = new JButton();
  JButton btBeenden = new JButton();

  /**
   * Create a new menu panel.
   * @param snakeMain Snake main class
   */
  public MenuePanel(Snake snakeMain)
  {
    this.snakeMain = snakeMain;
    try
    {
      jbInit();
      setPreferredSize(new Dimension(Snake.PWIDTH, Snake.PHEIGHT));
    }
    catch (Exception exception)
    {
      exception.printStackTrace();
    }
  }

  /**
   * Default constructor, just to remove errors with JBuilder.
   */
  public MenuePanel()
  {
    try
    {
      jbInit();
    }
    catch (Exception exception)
    {
      exception.printStackTrace();
    }
  }

  /**
   * Standard method to initialize all gui components.
   * @throws Exception
   */
  private void jbInit() throws Exception
  {
    laTitel.setFont(new java.awt.Font("Dialog", Font.BOLD, 40));
    laTitel.setForeground(new Color(0, 118, 0));
    laTitel.setHorizontalAlignment(SwingConstants.CENTER);
    laTitel.setText("SNAKE");
    laTitel.setBounds(new Rectangle(300, 182, 200, 44));
    this.setLayout(null);
    btMultiplayer.setBounds(new Rectangle(325, 287, 150, 32));
    btMultiplayer.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    btMultiplayer.setText("Multiplayer");
    btMultiplayer.addActionListener(new MenuePanel_btMultiplayer_actionAdapter(this));
    btSingleplayer.setBounds(new Rectangle(325, 247, 150, 32));
    btSingleplayer.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    btSingleplayer.setText("Singleplayer");
    btSingleplayer.addActionListener(new MenuePanel_btSingleplayer_actionAdapter(this));
    btEinstellungen.setBounds(new Rectangle(325, 327, 150, 32));
    btEinstellungen.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    btEinstellungen.setToolTipText("");
    btEinstellungen.setText("Settings");
    btEinstellungen.addActionListener(new MenuePanel_btEinstellungen_actionAdapter(this));
    btBeenden.setBounds(new Rectangle(325, 367, 150, 32));
    btBeenden.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    btBeenden.setText("Exit");
    btBeenden.addActionListener(new MenuePanel_btBeenden_actionAdapter(this));
    this.setMinimumSize(new Dimension(1, 1));
    this.add(btSingleplayer);
    this.add(laTitel);
    this.add(btMultiplayer);
    this.add(btEinstellungen);
    this.add(btBeenden);
  }

  /**
   * Overwritten to paint a background picture, and a window around the components
   * of the panel.
   * @param g Graphics
   */
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    //background
    if (snakeMain.getBackGroundImage() != null)
    {
      g.drawImage(snakeMain.getBackGroundImage(), 0, 0, this);
    }
    //window
    snakeMain.drawWindow( (Graphics2D) g, 350, 300);
  }

  /**
   * Start singleplayer game.
   * @param e ActionEvent
   */
  public void btSingleplayer_actionPerformed(ActionEvent e)
  {
    snakeMain.startSingleplayerGame();
  }

  /**
   * Try to open a connection to corsospace; if the connection could be opened,
   * open multiplayer menu.
   * @param e ActionEvent
   */
  public void btMultiplayer_actionPerformed(ActionEvent e)
  {
    if (snakeMain.openCorsoConnection())
    {
      snakeMain.openMPMenue();
    }
  }

  /**
   * Open the settings dialog.
   * @param e ActionEvent
   */
  public void btEinstellungen_actionPerformed(ActionEvent e)
  {
    FSettings fsettings = new FSettings(snakeMain);
    fsettings.setVisible(true);
  }

  /**
   * Exit the game.
   * @param e ActionEvent
   */
  public void btBeenden_actionPerformed(ActionEvent e)
  {
    Snake.snakeLog.close();
    System.exit(0);
  }
}

class MenuePanel_btBeenden_actionAdapter implements ActionListener
{
  private MenuePanel adaptee;
  MenuePanel_btBeenden_actionAdapter(MenuePanel adaptee)
  {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e)
  {
    adaptee.btBeenden_actionPerformed(e);
  }
}

class MenuePanel_btEinstellungen_actionAdapter implements ActionListener
{
  private MenuePanel adaptee;
  MenuePanel_btEinstellungen_actionAdapter(MenuePanel adaptee)
  {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e)
  {
    adaptee.btEinstellungen_actionPerformed(e);
  }
}

class MenuePanel_btMultiplayer_actionAdapter implements ActionListener
{
  private MenuePanel adaptee;
  MenuePanel_btMultiplayer_actionAdapter(MenuePanel adaptee)
  {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e)
  {
    adaptee.btMultiplayer_actionPerformed(e);
  }
}

class MenuePanel_btSingleplayer_actionAdapter implements ActionListener
{
  private MenuePanel adaptee;
  MenuePanel_btSingleplayer_actionAdapter(MenuePanel adaptee)
  {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e)
  {
    adaptee.btSingleplayer_actionPerformed(e);
  }
}
