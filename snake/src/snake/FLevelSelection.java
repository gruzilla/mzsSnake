package snake;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import snake.util.ImageLoader;
import java.awt.image.BufferedImage;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import snake.data.LevelData;

/**
 * Dialog that shows a selection of all available levels.
 * @author Thomas Scheller, Markus Karolus
 */
public class FLevelSelection extends JDialog
{
  private LevelsManager levelsManager = null;
  private GameListManager gameList = null;

  JPanel panel1 = new JPanel();
  JButton btWaehlen = new JButton();
  JButton btAbbrechen = new JButton();
  JScrollPane jScrollPane1 = new JScrollPane();
  JList lbLevels = new JList();
  JLabel laPreview = new JLabel();

  /**
   * Create a new FLevelSelection Dialog.
   * @param owner Owner of the Dialog
   * @param aLevelsManager LevelsManager that provides the level data
   * @param gameList GameListManager with the list of all games that are currently available
   */
  public FLevelSelection(Frame owner, LevelsManager aLevelsManager, GameListManager gameList)
  {
    super(owner, "Choose level", true);
    this.levelsManager = aLevelsManager;
    this.gameList = gameList;
    try
    {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      jbInit();
      //pack();
      fillListBox();
      this.setSize(370, 300);
      this.setLocationRelativeTo(owner);
    }
    catch (Exception exception)
    {
      exception.printStackTrace();
    }
  }

  /**
   * Default constructor, just to remove errors with JBuilder.
   */
  public FLevelSelection()
  {

    try
    {
      jbInit();
      pack();
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
    panel1.setLayout(null);
    btWaehlen.setBounds(new Rectangle(24, 210, 154, 32));
    btWaehlen.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    btWaehlen.setText("Choose");
    btWaehlen.addActionListener(new FLevelSelection_btWaehlen_actionAdapter(this));
    btAbbrechen.setBounds(new Rectangle(190, 209, 154, 32));
    btAbbrechen.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    btAbbrechen.setText("Cancel");
    btAbbrechen.addActionListener(new FLevelSelection_btAbbrechen_actionAdapter(this));
    jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    jScrollPane1.setBounds(new Rectangle(13, 14, 160, 160));
    this.setName("Choose level");
    this.setResizable(false);
    laPreview.setBorder(BorderFactory.createEtchedBorder());
    laPreview.setText("");
    laPreview.setBounds(new Rectangle(183, 34, 160, 120));
    lbLevels.addListSelectionListener(new FLevelSelection_lbLevels_listSelectionAdapter(this));
    getContentPane().add(panel1);
    panel1.add(jScrollPane1);
    jScrollPane1.getViewport().add(lbLevels);
    panel1.add(laPreview);
    panel1.add(btAbbrechen);
    panel1.add(btWaehlen);
  }

  /**
   * Fill the listbox with the levels and choose the current level.
   */
  private void fillListBox()
  {
    //create list with levels
    lbLevels.setListData(levelsManager.getLevelList());
    //choose current level
    lbLevels.setSelectedIndex(levelsManager.getCurrentLevelIndex());
    lbLevels_valueChanged(null);
  }

  /**
   * Set the chosen level and close the dialog.
   * @param e ActionEvent
   */
  public void btWaehlen_actionPerformed(ActionEvent e)
  {
    if (lbLevels.getSelectedIndex() > -1)
    {
      //levelsManager.setCurrentLevel((String)lbLevels.getSelectedValue());
      LevelData levelData = new LevelData();
      levelData.LoadData(levelsManager);
      gameList.setGameLevel(levelData);

      this.dispose();
    }
  }

  /**
   * Close the dialog without choosing a level.
   * @param e ActionEvent
   */
  public void btAbbrechen_actionPerformed(ActionEvent e)
  {
    levelsManager.setCurrentLevel(gameList.getCurrentGame().getLevelDir()); //restore previous level settings
    this.dispose();
  }

  /**
   * Display the thumbnail image for the level wenn a new level is chosen.
   * @param e ListSelectionEvent
   */
  public void lbLevels_valueChanged(ListSelectionEvent e)
  {
    //Vorschau anzeigen
    levelsManager.setCurrentLevel(lbLevels.getSelectedIndex());
    ImageLoader loader = new ImageLoader();
    BufferedImage levelImage = loader.loadImage(levelsManager.getThumbnailPath(), false);
    if (levelImage.getWidth() > laPreview.getWidth() || levelImage.getHeight() > laPreview.getHeight())
    {
      levelImage = loader.getResizedImage(levelImage, laPreview.getWidth(), laPreview.getHeight());
    }
    laPreview.setIcon(new ImageIcon(levelImage));
  }
}

class FLevelSelection_lbLevels_listSelectionAdapter implements ListSelectionListener
{
  private FLevelSelection adaptee;
  FLevelSelection_lbLevels_listSelectionAdapter(FLevelSelection adaptee)
  {
    this.adaptee = adaptee;
  }

  public void valueChanged(ListSelectionEvent e)
  {
    adaptee.lbLevels_valueChanged(e);
  }
}

class FLevelSelection_btAbbrechen_actionAdapter implements ActionListener
{
  private FLevelSelection adaptee;
  FLevelSelection_btAbbrechen_actionAdapter(FLevelSelection adaptee)
  {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e)
  {
    adaptee.btAbbrechen_actionPerformed(e);
  }
}

class FLevelSelection_btWaehlen_actionAdapter implements ActionListener
{
  private FLevelSelection adaptee;
  FLevelSelection_btWaehlen_actionAdapter(FLevelSelection adaptee)
  {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e)
  {
    adaptee.btWaehlen_actionPerformed(e);
  }
}
