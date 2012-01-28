package snake.ui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import snake.GameListManager;
import snake.LevelsManager;
import snake.Snake;
import snake.data.*;
import snake.util.Messages;
import snake.util.ImageLoader;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Panel that shows all settings for a multiplayer game. The MPNewGamePanel listens
 * to changes from the game list, and updates itself if changes in the game occur.
 * Only the leader of the game is allowed to change the settings.
 * @author Thomas Scheller, Markus Karolus
 */
public class MPNewGamePanel extends JPanel //implements IDataChangeListener
{
  private Snake snakeMain = null;
  private GameListManager gameList = null;
  private LevelsManager levels = null;
  private Player myPlayer = null;
  private boolean ready = false;
  private BufferedImage levelImage = null;
  private String currentLevelDir = null;
  private Game currentGame = null;

  JLabel laTitel = new JLabel();
  JButton btLevel = new JButton();
  JButton btZurueck = new JButton();
  JTextField tfPlayer2 = new JTextField();
  JLabel laLevelPreview = new JLabel();
  JButton btStart = new JButton();
  JTextField tfPlayer3 = new JTextField();
  JTextField tfPlayer4 = new JTextField();
  JTextField tfPlayer1 = new JTextField();
  JLabel laSpieler2 = new JLabel();
  JLabel laLevelName = new JLabel();
  JLabel laSpieler3 = new JLabel();
  JLabel laSpieler4 = new JLabel();
  JLabel laSpieler1 = new JLabel();
  JLabel laPlayer3Ready = new JLabel();
  JLabel laPlayer2Ready = new JLabel();
  JLabel laPlayer4Ready = new JLabel();
  JLabel laPlayer1Ready = new JLabel();
  JLabel laKollision = new JLabel();
  JCheckBox chKollisionSelbst = new JCheckBox();
  JCheckBox chKollisionAndere = new JCheckBox();
  JCheckBox chKollisionWand = new JCheckBox();
  JLabel laSpielart = new JLabel();
  JRadioButton rbSpielZeit = new JRadioButton();
  JRadioButton rbSpielPunkte = new JRadioButton();
  JSpinner spSpielPunkte = new JSpinner(new SpinnerNumberModel(10, 1, 50, 1));
  JLabel laSpieler = new JLabel();
  JSpinner spSpielZeit = new JSpinner(new SpinnerNumberModel(100, 20, 300, 1));
  JTextField tfSpielPunkte = new JTextField();
  JTextField tfSpielZeit = new JTextField();
  JLabel laGameName = new JLabel();

  /**
   * Default constructor, just to remove errors with JBuilder.
   */
  public MPNewGamePanel()
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
   * Create a new MPNewGamePanel
   * @param snakeMain Snake main class
   * @param gameList manager for the list of games
   * @param myPlayer the own player
   * @param aLevelManager LevelsManager
   */
  public MPNewGamePanel(Snake snakeMain, GameListManager gameList, Player myPlayer,
                        LevelsManager aLevelManager)
  {
    this.snakeMain = snakeMain;
    this.gameList = gameList;
    this.myPlayer = myPlayer;
    this.levels = aLevelManager;

//    gameList.setDataChangeListener(this); //set itself as DataChangeListener for the game list

    try
    {
      jbInit();
    }
    catch (Exception exception)
    {
      exception.printStackTrace();
    }

    if (!gameList.isViewOnly())
    {
      laTitel.setText("Multiplayer Menu - New Game");
    }
    else
    {
       laTitel.setText("Multiplayer Menu - Watcher");
    }
    Game currentGame = gameList.getCurrentGame();
    if (currentGame != null)
    {
      laGameName.setText("Title: \""+currentGame.getName()+"\"");
    }
  }

  /**
   * Overwritten from IDataChangeListener interface. If a change event occurs, the
   * form is updated depending on the type of change.
   * @param changeEvent DataChangeEvent
   */
/*  public void dataChanged(DataChangeEvent changeEvent)
  {
    updateForm(changeEvent.getType());
  }
/**/
  /**
   * Standard method to initialize all gui components.
   * @throws Exception
   */
  private void jbInit() throws Exception
  {
    this.setLayout(null);
    this.setMinimumSize(new Dimension(0, 0));
    this.setOpaque(true);
    this.setPreferredSize(new Dimension(0, 0));
    this.setSize(new Dimension(800, 600));
    laTitel.setFont(new java.awt.Font("Dialog", Font.BOLD, 24));
    laTitel.setHorizontalAlignment(SwingConstants.CENTER);
    laTitel.setText("Multiplayer Menu - New Game");
    laTitel.setBounds(new Rectangle(194, 103, 407, 44));
    btLevel.setBounds(new Rectangle(410, 318, 154, 32));
    btLevel.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    btLevel.setText("Choose Level");
    btLevel.addActionListener(new MPNewGamePanel_btLevel_actionAdapter(this));
    btZurueck.setBounds(new Rectangle(402, 457, 150, 32));
    btZurueck.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    btZurueck.setText("Back");
    btZurueck.addActionListener(new MPNewGamePanel_btZurueck_actionAdapter(this));
    tfPlayer2.setEditable(false);
    tfPlayer2.setBounds(new Rectangle(243, 239, 140, 30));
    laLevelPreview.setBorder(BorderFactory.createEtchedBorder());
    laLevelPreview.setText("Level Preview...");
    laLevelPreview.setBounds(new Rectangle(411, 172, 160, 120));
    btStart.setBounds(new Rectangle(243, 457, 150, 32));
    btStart.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    btStart.setText("Start");
    btStart.addActionListener(new MPNewGamePanel_btStart_actionAdapter(this));
    tfPlayer3.setEditable(false);
    tfPlayer3.setText("");
    tfPlayer3.setBounds(new Rectangle(243, 272, 140, 30));
    tfPlayer4.setEditable(false);
    tfPlayer4.setText("");
    tfPlayer4.setBounds(new Rectangle(243, 305, 140, 30));
    tfPlayer1.setEditable(false);
    tfPlayer1.setText("");
    tfPlayer1.setBounds(new Rectangle(243, 206, 140, 30));
    laSpieler2.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    laSpieler2.setText("2.");
    laSpieler2.setBounds(new Rectangle(226, 241, 25, 28));
    laLevelName.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    laLevelName.setText("Player:");
    laLevelName.setBounds(new Rectangle(410, 289, 190, 28));
    laSpieler3.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    laSpieler3.setText("3.");
    laSpieler3.setBounds(new Rectangle(226, 274, 25, 28));
    laSpieler4.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    laSpieler4.setText("4.");
    laSpieler4.setBounds(new Rectangle(226, 307, 25, 28));
    laSpieler1.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    laSpieler1.setText("1.");
    laSpieler1.setBounds(new Rectangle(225, 208, 25, 28));
    laPlayer3Ready.setBackground(Color.red);
    laPlayer3Ready.setBorder(BorderFactory.createLineBorder(Color.black));
    laPlayer3Ready.setOpaque(true);
    laPlayer3Ready.setText("");
    laPlayer3Ready.setBounds(new Rectangle(387, 279, 16, 16));
    laPlayer2Ready.setBackground(Color.red);
    laPlayer2Ready.setBorder(BorderFactory.createLineBorder(Color.black));
    laPlayer2Ready.setOpaque(true);
    laPlayer2Ready.setBounds(new Rectangle(387, 246, 16, 16));
    laPlayer4Ready.setBackground(Color.red);
    laPlayer4Ready.setBorder(BorderFactory.createLineBorder(Color.black));
    laPlayer4Ready.setOpaque(true);
    laPlayer4Ready.setBounds(new Rectangle(387, 311, 16, 16));
    laPlayer1Ready.setBackground(Color.red);
    laPlayer1Ready.setBorder(BorderFactory.createLineBorder(Color.black));
    laPlayer1Ready.setOpaque(true);
    laPlayer1Ready.setBounds(new Rectangle(387, 214, 16, 16));
    laKollision.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    laKollision.setToolTipText("");
    laKollision.setText("Collision with:");
    laKollision.setBounds(new Rectangle(240, 348, 140, 28));
    chKollisionSelbst.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    chKollisionSelbst.setOpaque(false);
    chKollisionSelbst.setText("itself");
    chKollisionSelbst.setBounds(new Rectangle(238, 400, 136, 23));
    chKollisionSelbst.addActionListener(new MPNewGamePanel_chKollisionSelbst_actionAdapter(this));
    chKollisionAndere.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    chKollisionAndere.setOpaque(false);
    chKollisionAndere.setText("other player");
    chKollisionAndere.setBounds(new Rectangle(238, 424, 158, 23));
    chKollisionAndere.addActionListener(new MPNewGamePanel_chKollisionAndere_actionAdapter(this));
    chKollisionWand.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    chKollisionWand.setOpaque(false);
    chKollisionWand.setText("environment");
    chKollisionWand.setBounds(new Rectangle(238, 376, 136, 23));
    chKollisionWand.addActionListener(new MPNewGamePanel_chKollisionWand_actionAdapter(this));
    laSpielart.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    laSpielart.setToolTipText("");
    laSpielart.setText("Playtype:");
    laSpielart.setBounds(new Rectangle(409, 348, 140, 28));
    rbSpielZeit.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    rbSpielZeit.setOpaque(false);
    rbSpielZeit.setText("Time");
    rbSpielZeit.setBounds(new Rectangle(407, 400, 102, 23));
    rbSpielZeit.addActionListener(new MPNewGamePanel_rbSpielZeit_actionAdapter(this));
    rbSpielPunkte.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    rbSpielPunkte.setOpaque(false);
    rbSpielPunkte.setText("Points");
    rbSpielPunkte.setBounds(new Rectangle(407, 376, 101, 23));
    rbSpielPunkte.addActionListener(new MPNewGamePanel_rbSpielPunkte_actionAdapter(this));
    spSpielPunkte.setBounds(new Rectangle(508, 376, 53, 23));
    spSpielPunkte.addChangeListener(new MPNewGamePanel_spSpielPunkte_changeAdapter(this));
    laSpieler.setFont(new java.awt.Font("Dialog", Font.BOLD, 16));
    laSpieler.setText("Player:");
    laSpieler.setBounds(new Rectangle(225, 176, 140, 28));
    spSpielZeit.setBounds(new Rectangle(508, 400, 53, 23));
    spSpielZeit.addChangeListener(new MPNewGamePanel_spSpielZeit_changeAdapter(this));
    tfSpielPunkte.setEditable(false);
    tfSpielPunkte.setBounds(new Rectangle(508, 376, 56, 23));
    tfSpielZeit.setEditable(false);
    tfSpielZeit.setText("jTextField1");
    tfSpielZeit.setBounds(new Rectangle(508, 400, 56, 23));
    laGameName.setFont(new java.awt.Font("Dialog", Font.BOLD, 20));
    laGameName.setHorizontalAlignment(SwingConstants.CENTER);
    laGameName.setText("New Game");
    laGameName.setBounds(new Rectangle(209, 141, 377, 35));
    this.add(tfPlayer1);
    this.add(tfPlayer2);
    this.add(tfPlayer3);
    this.add(tfPlayer4);
    this.add(laSpieler1);
    this.add(laSpieler4);
    this.add(laSpieler3);
    this.add(laSpieler2);
    this.add(laPlayer4Ready);
    this.add(laPlayer3Ready);
    this.add(laPlayer2Ready);
    this.add(laPlayer1Ready);
    this.add(btZurueck);
    this.add(btStart);
    this.add(chKollisionWand);
    this.add(chKollisionSelbst);
    this.add(chKollisionAndere);
    this.add(laKollision);
    this.add(laSpielart);
    this.add(rbSpielPunkte);
    this.add(rbSpielZeit);
    this.add(laLevelPreview);
    this.add(laSpieler);
    this.add(laLevelName);
    this.add(btLevel);
    this.add(spSpielZeit);
    this.add(spSpielPunkte);
    this.add(tfSpielPunkte);
    this.add(tfSpielZeit);
    this.add(laTitel);
    this.add(laGameName);
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
    snakeMain.drawWindow( (Graphics2D) g, 400, 400);
  }

  /**
   * Fill the form.
   */
  public void updateForm()
  {
//    updateForm(DataChangeType.undefined);
  }

  /**
   * Update the form depending on the type of change. If change type is player, then only
   * player data is updated.
   * @param type type of change from the DataChangeEvent
   */
/*  private void updateForm(DataChangeType type)
  {
    if (type == DataChangeType.game || type == DataChangeType.undefined)
    {
      //get current game
      Game newGame = gameList.getCurrentGame();
      if (newGame == null)
      {
        //back to multplayer menu if current game could not be found
        gameList.setDataChangeListener(null);
        System.out.println("Joining game failed.");
        Messages.errorMessage(snakeMain, "Joining game failed!");
        snakeMain.openMPMenue();
        return;
      }

      //check if game settings have been changed. set player state back to not init if settings
      //have changed, and game state to opened if player is leader
      if (myPlayer.isStateInit() && currentGame != null && !currentGame.settingsEqual(newGame))
      {
        //System.out.println("Game settings changed, set playerstate back to notinit.");
        myPlayer.setPlayerState(PlayerState.notinit);
        myPlayer.saveToSpace();
        if (gameList.myPlayerIsLeader() && gameList.getCurrentGame().getState() != GameState.opened)
        {
          //System.out.println("set gamestate back to open.");
          gameList.setGameState(GameState.opened);
        }
      }
      currentGame = newGame;

      //update game settings
      rbSpielPunkte.setSelected(currentGame.getGameType() == GameType.points);
      spSpielPunkte.setEnabled(rbSpielPunkte.isSelected());
      spSpielPunkte.setVisible(gameList.myPlayerIsLeader());
      if (rbSpielPunkte.isSelected() && !gameList.myPlayerIsLeader())
      {
        tfSpielPunkte.setVisible(true);
        tfSpielPunkte.setText(String.valueOf(currentGame.getWinValue()));
      }
      else
      {
        tfSpielPunkte.setVisible(false);
      }
      rbSpielZeit.setSelected(currentGame.getGameType() == GameType.time);
      spSpielZeit.setEnabled(rbSpielZeit.isSelected());
      spSpielZeit.setVisible(gameList.myPlayerIsLeader());
      if (rbSpielZeit.isSelected() && !gameList.myPlayerIsLeader())
      {
        tfSpielZeit.setVisible(true);
        tfSpielZeit.setText(String.valueOf(currentGame.getWinValue()));
      }
      else
      {
        tfSpielZeit.setVisible(false);
      }
      chKollisionWand.setSelected(currentGame.getCollisionTypeWall());
      chKollisionSelbst.setSelected(currentGame.getCollisionTypeOwn());
      chKollisionAndere.setSelected(currentGame.getCollisionTypeOther());

      //update level info if it has changed
      if (!currentGame.getLevelDir().equals(currentLevelDir) || levelImage == null)
      {
        if (!levels.levelExists(currentGame.getLevelDir()))
        {
          LevelData levelData = gameList.getCurrentGame().getLevelData();
          levelData.SaveData(levels);
        }

        //cancel if level does not exist
        if (!levels.levelExists(currentGame.getLevelDir()))
        {
          gameList.setDataChangeListener(null);
          Messages.errorMessage(snakeMain,
                                "Error occured: the level \"" + currentGame.getLevelDir() +
                                "\" can't be found!");
          snakeMain.openMPMenue();
          return;
        }
        else
        {
          //check if level is the same as level of the leader
          levels.setCurrentLevel(currentGame.getLevelDir());
          byte[] levelHash = levels.getLevelHash();
          if (!java.util.Arrays.equals(levelHash, currentGame.getLevelCheckSum()))
          {
            LevelData levelData = gameList.getCurrentGame().getLevelData();
            levelData.SaveData(levels);
          }

        }

        currentLevelDir = currentGame.getLevelDir();
        laLevelName.setText(levels.getCurrentLevelName());

        try
        {
          //level has changed, load new thumbnail image and display it
          ImageLoader loader = new ImageLoader();
          levelImage = loader.loadImage(levels.getThumbnailPath(), false);
          if (levelImage.getWidth() > laLevelPreview.getWidth() ||
              levelImage.getHeight() > laLevelPreview.getHeight())
          {
            levelImage = loader.getResizedImage(levelImage, laLevelPreview.getWidth(),
                                                laLevelPreview.getHeight());
          }
          laLevelPreview.setIcon(new ImageIcon(levelImage));
        }
        catch (Exception ex)
        {
          System.out.println("Error occured during displaying level preview:");
          ex.printStackTrace(System.out);
        }
      }
    }

    //update player information
    refreshPlayers(currentGame);
  }
*/
  /**
   * Update the player information.
   * @param gameData the current game, if null the game is read from the gamelist
   */
  private void refreshPlayers(Game gameData)
  {
    //get current game
    Game game;
    if (gameData == null)
    {
      game = gameList.getCurrentGame();
    }
    else
    {
      game = gameData;
    }

    if (game != null)
    {
      //update player information
      updatePlayerInfo(game, 0, tfPlayer1, laPlayer1Ready);
      updatePlayerInfo(game, 1, tfPlayer2, laPlayer2Ready);
      updatePlayerInfo(game, 2, tfPlayer3, laPlayer3Ready);
      updatePlayerInfo(game, 3, tfPlayer4, laPlayer4Ready);

      //check if player is ready (when state is not notinit)
      ready = myPlayer.getPlayerState() != PlayerState.notinit;

      if (gameList.isViewOnly())
      {
        btStart.setEnabled(false);
      }
      else
      {
        btStart.setEnabled(!ready);
      }
      //settings can only be edited if player is leader and not ready
      btLevel.setVisible(gameList.myPlayerIsLeader());
      btLevel.setEnabled(!ready);
      if (ready)
      {
        spSpielPunkte.setEnabled(false);
        spSpielZeit.setEnabled(false);
      }
    }
  }

  /**
   * Update the information of a single player.
   * @param game the current game
   * @param index index of the player in the current game
   * @param tfPlayer textfield to show the name of the player
   * @param laPlayerReady label to show the ready information of the player
   */
  private void updatePlayerInfo(Game game, int index, JTextField tfPlayer, JLabel laPlayerReady)
  {
    if (game.getPlayerAnz() > index)
    {
      Player activePlayer = game.getPlayer(index);
      synchronized (tfPlayer)
      {
        //display the playername in the textfield, set the colour to blue if the player is leader
        tfPlayer.setText(activePlayer.getName());
        if (activePlayer == game.getLeader())
        {
          tfPlayer.setForeground(Color.BLUE);
        }
        else
        {
          tfPlayer.setForeground(Color.BLACK);
        }
      }
      //display ready information in the label, green if player is ready, otherwise red
      if (activePlayer.isStateInit() || activePlayer.isStateLoaded())
      {
        laPlayerReady.setBackground(Color.GREEN);
      }
      else
      {
        laPlayerReady.setBackground(Color.RED);
      }
    }
    else
    {
      tfPlayer.setText("");
      laPlayerReady.setBackground(Color.GRAY);
    }
    tfPlayer.repaint(); //repaint because automatic refresh does not work reliable
    laPlayerReady.repaint(); //repaint because automatic refresh does not work reliable

  }

  /**
   * If player is ready, set back to not ready. If player is not ready, leave the game
   * and open the multiplayer menu.
   * @param e ActionEvent
   */
  public void btZurueck_actionPerformed(ActionEvent e)
  {
    if (ready)
    {
      //set player back to not init (= not ready)
      gameList.setMyPlayerReady(PlayerState.notinit);
    }
    else
    {
      //leave game
//      gameList.setDataChangeListener(null);
      gameList.leaveGame();

      //open multiplayer menu
      snakeMain.openMPMenue();
    }
  }

  /**
   * Show the level selection dialog
   * @param e ActionEvent
   */
  public void btLevel_actionPerformed(ActionEvent e)
  {
    //Levelauswahl anzeigen
    FLevelSelection form = new FLevelSelection(snakeMain, levels, gameList);
    form.setVisible(true);
  }

  /**
   * Initialize the own game sprites (creates own game data in corsospace and
   * sets the player state to init (= ready)).
   * @param e ActionEvent
   */
  public void btStart_actionPerformed(ActionEvent e)
  {
    if (gameList.getCurrentGame().getPlayerAnz() > 1)
    {
      snakeMain.initMyGameSprites();
      updateForm();
    }
  }

  /**
   * Save the game data to corso space (game type, win value, collision values).
   */
  private void saveGameData()
  {
    GameType gameType = rbSpielPunkte.isSelected() ? GameType.points : GameType.time;
    Integer winValue = rbSpielPunkte.isSelected() ? (Integer) spSpielPunkte.getValue() :
        (Integer) spSpielZeit.getValue();
    gameList.setGameData(gameType, winValue.intValue(), chKollisionWand.isSelected(),
                         chKollisionSelbst.isSelected(),
                         chKollisionAndere.isSelected());
  }

  /**
   * Select points as game type and enable the points field. Allow changes only if
   * player is leader.
   * @param e ActionEvent
   */
  public void rbSpielPunkte_actionPerformed(ActionEvent e)
  {
    if (!ready && gameList.myPlayerIsLeader())
    {
      rbSpielPunkte.setSelected(true);
      spSpielPunkte.setEnabled(true);
      rbSpielZeit.setSelected(false);
      spSpielZeit.setEnabled(false);
      saveGameData();
    }
    else
    {
      rbSpielPunkte.setSelected(gameList.getCurrentGame().getGameType() == GameType.points);
    }
  }

  /**
   * Select time as game type and enable the time field. Allow changes only if
   * player is leader. (Save game data to space)
   * @param e ActionEvent
   */
  public void rbSpielZeit_actionPerformed(ActionEvent e)
  {
    if (!ready && gameList.myPlayerIsLeader())
    {
      rbSpielPunkte.setSelected(false);
      spSpielPunkte.setEnabled(false);
      rbSpielZeit.setSelected(true);
      spSpielZeit.setEnabled(true);
      saveGameData();
    }
    else
    {
      rbSpielZeit.setSelected(gameList.getCurrentGame().getGameType() == GameType.time);
    }
  }

  /**
   * Change the wall collision type if player is leader. (Save game data to space)
   * @param e ActionEvent
   */
  public void chKollisionWand_actionPerformed(ActionEvent e)
  {
    if (!ready && gameList.myPlayerIsLeader())
    {
      saveGameData();
    }
    else
    {
      chKollisionWand.setSelected(gameList.getCurrentGame().getCollisionTypeWall());
    }
  }

  /**
   * Change the self collision type if player is leader. (Save game data to space)
   * @param e ActionEvent
   */
  public void chKollisionSelbst_actionPerformed(ActionEvent e)
  {
    if (!ready && gameList.myPlayerIsLeader())
    {
      saveGameData();
    }
    else
    {
      chKollisionSelbst.setSelected(gameList.getCurrentGame().getCollisionTypeOwn());
    }
  }

  /**
   * Change the others collision type if player is leader. (Save game data to space)
   * @param e ActionEvent
   */
  public void chKollisionAndere_actionPerformed(ActionEvent e)
  {
    if (!ready && gameList.myPlayerIsLeader())
    {
      saveGameData();
    }
    else
    {
      chKollisionAndere.setSelected(gameList.getCurrentGame().getCollisionTypeOther());
    }
  }

  /**
   * Points field state changed: Save game data to space if player is leader.
   */
  public void spSpielPunkte_stateChanged(ChangeEvent e)
  {
    if (gameList.myPlayerIsLeader() && spSpielPunkte.isEnabled())
    {
      saveGameData();
    }
  }

  /**
   * Time field state changed: Save game data to space if player is leader.
   * @param e ChangeEvent
   */
  public void spSpielZeit_stateChanged(ChangeEvent e)
  {
    if (gameList.myPlayerIsLeader() && spSpielZeit.isEnabled())
    {
      saveGameData();
    }
  }
}

class MPNewGamePanel_spSpielPunkte_changeAdapter implements ChangeListener
{
  private MPNewGamePanel adaptee;
  MPNewGamePanel_spSpielPunkte_changeAdapter(MPNewGamePanel adaptee)
  {
    this.adaptee = adaptee;
  }

  public void stateChanged(ChangeEvent e)
  {
    adaptee.spSpielPunkte_stateChanged(e);
  }
}

class MPNewGamePanel_spSpielZeit_changeAdapter implements ChangeListener
{
  private MPNewGamePanel adaptee;
  MPNewGamePanel_spSpielZeit_changeAdapter(MPNewGamePanel adaptee)
  {
    this.adaptee = adaptee;
  }

  public void stateChanged(ChangeEvent e)
  {
    adaptee.spSpielZeit_stateChanged(e);
  }
}

class MPNewGamePanel_chKollisionAndere_actionAdapter implements ActionListener
{
  private MPNewGamePanel adaptee;
  MPNewGamePanel_chKollisionAndere_actionAdapter(MPNewGamePanel adaptee)
  {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e)
  {
    adaptee.chKollisionAndere_actionPerformed(e);
  }
}

class MPNewGamePanel_chKollisionSelbst_actionAdapter implements ActionListener
{
  private MPNewGamePanel adaptee;
  MPNewGamePanel_chKollisionSelbst_actionAdapter(MPNewGamePanel adaptee)
  {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e)
  {
    adaptee.chKollisionSelbst_actionPerformed(e);
  }
}

class MPNewGamePanel_chKollisionWand_actionAdapter implements ActionListener
{
  private MPNewGamePanel adaptee;
  MPNewGamePanel_chKollisionWand_actionAdapter(MPNewGamePanel adaptee)
  {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e)
  {
    adaptee.chKollisionWand_actionPerformed(e);
  }
}

class MPNewGamePanel_rbSpielZeit_actionAdapter implements ActionListener
{
  private MPNewGamePanel adaptee;
  MPNewGamePanel_rbSpielZeit_actionAdapter(MPNewGamePanel adaptee)
  {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e)
  {
    adaptee.rbSpielZeit_actionPerformed(e);
  }
}

class MPNewGamePanel_rbSpielPunkte_actionAdapter implements ActionListener
{
  private MPNewGamePanel adaptee;
  MPNewGamePanel_rbSpielPunkte_actionAdapter(MPNewGamePanel adaptee)
  {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e)
  {
    adaptee.rbSpielPunkte_actionPerformed(e);
  }
}

class MPNewGamePanel_btLevel_actionAdapter implements ActionListener
{
  private MPNewGamePanel adaptee;
  MPNewGamePanel_btLevel_actionAdapter(MPNewGamePanel adaptee)
  {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e)
  {
    adaptee.btLevel_actionPerformed(e);
  }
}

class MPNewGamePanel_btZurueck_actionAdapter implements ActionListener
{
  private MPNewGamePanel adaptee;
  MPNewGamePanel_btZurueck_actionAdapter(MPNewGamePanel adaptee)
  {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e)
  {
    adaptee.btZurueck_actionPerformed(e);
  }
}

class MPNewGamePanel_btStart_actionAdapter implements ActionListener
{
  private MPNewGamePanel adaptee;
  MPNewGamePanel_btStart_actionAdapter(MPNewGamePanel adaptee)
  {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e)
  {
    adaptee.btStart_actionPerformed(e);
  }
}
