package snake.ui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import snake.mzspaces.Util;
import snake.util.Messages;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;
import snake.util.ImageLoader;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import snake.Snake;
import snake.SkinsManager;
import snake.Settings;

/**
 * Dialog that shows the settings for corso connection, player name and snake
 * skin.
 * 
 * @author Thomas Scheller, Markus Karolus
 */
public class FSettings extends JDialog {
	private static final long serialVersionUID = 1L;
	private SkinsManager skinsManager = null;
	private ImageLoader imgLoader = null;
	private BufferedImage imgPart = null;
	private BufferedImage imgHead = null;
	private BufferedImage imgTail = null;
	private BufferedImage imgCollectable = null;
	private Snake snakeMain = null;

	JPanel panel1 = new JPanel();
	JTextField tfCorsoSiteLocal = new javax.swing.JTextField();
	JLabel laCokeSiteLocal = new javax.swing.JLabel();
	JTextField tfPort = new javax.swing.JTextField();
	JLabel laPort = new javax.swing.JLabel();
	JPasswordField tfPassword = new javax.swing.JPasswordField();
	JTextField tfUser = new javax.swing.JTextField();
	JLabel laUser = new javax.swing.JLabel();
	JLabel laPassword = new javax.swing.JLabel();
	JLabel laPlayername = new javax.swing.JLabel();
	JTextField tfPlayername = new javax.swing.JTextField();
	JTextField tfCorsoSiteServer = new javax.swing.JTextField();
	JLabel laCokeSiteServer = new javax.swing.JLabel();
	JTextField tfDomain = new javax.swing.JTextField();
	JLabel laDomain = new javax.swing.JLabel();
	JButton btAbbrechen = new JButton();
	JButton btOK = new JButton();
	JPanel jPanel1 = new JPanel();
	Border border1 = BorderFactory.createEtchedBorder(Color.white, new Color(
			156, 156, 158));
	Border border2 = new TitledBorder(border1, "Corso Connection:");
	JPanel jPanel2 = new JPanel();
	Border border3 = BorderFactory.createEtchedBorder(Color.white, new Color(
			156, 156, 158));
	Border border4 = new TitledBorder(border3, "Game Settings:");
	JPanel jPanel3 = new JPanel();
	Border border5 = BorderFactory.createEtchedBorder(Color.white, new Color(
			156, 156, 158));
	Border border6 = new TitledBorder(border5, "Skin Selection:");

	// JPanel with overwritten paint method that draws a preview of the snake
	JPanel pSkinPreview = new JPanel() {
		public void paint(Graphics g) {
			// draw snake skin preview
			g.clearRect(0, 0, 400, 400);
			g.drawRect(0, 0, 51, 131);
			if (imgCollectable != null)
				g.drawImage(imgCollectable, 14, 10, null);
			if (imgTail != null)
				g.drawImage(imgTail, 14, 104, null);
			if (imgPart != null) {
				g.drawImage(imgPart, 14, 88, null);
				g.drawImage(imgPart, 14, 72, null);
				g.drawImage(imgPart, 14, 56, null);
			}
			if (imgHead != null)
				g.drawImage(imgHead, 14, 40, null);
		}
	};

	JScrollPane jScrollPane1 = new JScrollPane();
	JList lbSkins = new JList();

	/**
	 * Create a new FSettings dialog.
	 * 
	 * @param snakeMain
	 *            Snake main class
	 */
	public FSettings(Snake snakeMain) {
		super(snakeMain, "Snake Settings", true); // always modal
		this.snakeMain = snakeMain;
		try {
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			jbInit();
			pack();

			this.setSize(523, 310);
			this.setLocationRelativeTo(snakeMain);

			// fill form
			Settings settings = Util.getInstance().getSettings();
			tfCorsoSiteLocal.setText(settings.getCokeSiteLocal());
			tfCorsoSiteServer.setText(settings.getCokeSiteServer());
			tfPort.setText(String.valueOf(settings.getPort()));
			// tfUser.setText(settings.getUsername());
			// tfPassword.setText(settings.getPassword());
			tfDomain.setText(settings.getDomain());
			tfPlayername.setText(settings.getPlayerName());

			// skins
			imgLoader = new ImageLoader();
			skinsManager = new SkinsManager();
			String[] skins = skinsManager.getSkinList();
			lbSkins.setListData(skins);
			skinsManager.setCurrentSkin(settings.getSnakeSkin());
			lbSkins.setSelectedIndex(skinsManager.getCurrentSkinIndex());
			lbSkins_mousePressed(null);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Standard method to initialize all gui components.
	 * 
	 * @throws Exception
	 */
	private void jbInit() throws Exception {
		panel1.setLayout(null);
		tfCorsoSiteLocal.setFont(new java.awt.Font("Lucida Sans", Font.PLAIN,
				12));
		laCokeSiteLocal.setFont(new java.awt.Font("Arial", Font.PLAIN, 13));
		laCokeSiteLocal.setText("Corso Site Local:");
		laCokeSiteLocal.setBounds(new Rectangle(16, 32, 106, 15));
		tfPort.setFont(new java.awt.Font("Lucida Sans", Font.PLAIN, 12));
		tfPort.setPreferredSize(new Dimension(110, 21));
		tfPort.setText("");
		tfPort.setColumns(9);
		tfPort.setBounds(new Rectangle(125, 72, 116, 20));
		laPort.setFont(new java.awt.Font("Arial", Font.PLAIN, 13));
		laPort.setText("Port:");
		laPort.setBounds(new Rectangle(17, 73, 106, 20));
		tfPassword.setFont(new java.awt.Font("Lucida Sans", Font.PLAIN, 12));
		tfPassword.setText("");
		tfPassword.setBounds(new Rectangle(125, 116, 116, 20));
		tfUser.setFont(new java.awt.Font("Lucida Sans", Font.PLAIN, 12));
		tfUser.setToolTipText("");
		tfUser.setText("");
		tfUser.setBounds(new Rectangle(125, 94, 116, 20));
		laUser.setFont(new java.awt.Font("Arial", Font.PLAIN, 13));
		laUser.setText("User:");
		laUser.setVerticalAlignment(SwingConstants.BOTTOM);
		laUser.setBounds(new Rectangle(16, 95, 106, 18));
		laPassword.setFont(new java.awt.Font("Arial", Font.PLAIN, 13));
		laPassword.setText("Password:");
		laPassword.setBounds(new Rectangle(16, 116, 108, 20));
		laPlayername.setFont(new java.awt.Font("Arial", Font.PLAIN, 13));
		laPlayername.setToolTipText("");
		laPlayername.setText("Playername:");
		laPlayername.setBounds(new Rectangle(16, 27, 104, 15));
		tfPlayername.setFont(new java.awt.Font("Lucida Sans", Font.PLAIN, 12));
		tfPlayername.setText("");
		tfPlayername.setBounds(new Rectangle(124, 24, 116, 20));
		tfCorsoSiteServer.setFont(new java.awt.Font("Lucida Sans", Font.PLAIN,
				12));
		tfCorsoSiteServer.setText("");
		tfCorsoSiteServer.setBounds(new Rectangle(125, 50, 116, 20));
		laCokeSiteServer.setFont(new java.awt.Font("Arial", Font.PLAIN, 13));
		laCokeSiteServer.setText("Corso Site Server:");
		laCokeSiteServer.setBounds(new Rectangle(16, 54, 106, 15));
		tfDomain.setFont(new java.awt.Font("Lucida Sans", Font.PLAIN, 12));
		tfDomain.setBounds(new Rectangle(125, 138, 116, 20));
		laDomain.setFont(new java.awt.Font("Arial", Font.PLAIN, 13));
		laDomain.setText("Domain:");
		laDomain.setBounds(new Rectangle(16, 141, 106, 15));
		this.setTitle("Snake Settings");
		this.addComponentListener(new FSettings_this_componentAdapter(this));
		btAbbrechen.setBounds(new Rectangle(265, 275, 100, 23));
		btAbbrechen.setToolTipText("");
		btAbbrechen.setText("Cancel");
		btAbbrechen.addActionListener(new FSettings_btAbbrechen_actionAdapter(
				this));
		btOK.setBounds(new Rectangle(164, 275, 100, 23));
		btOK.setToolTipText("");
		btOK.setText("OK");
		btOK.addActionListener(new FSettings_btOK_actionAdapter(this));
		jPanel1.setBorder(border2);
		jPanel1.setBounds(new Rectangle(9, 6, 255, 172));
		jPanel1.setLayout(null);
		jPanel2.setBorder(border4);
		jPanel2.setBounds(new Rectangle(9, 178, 255, 87));
		jPanel2.setLayout(null);
		jPanel3.setBorder(border6);
		jPanel3.setBounds(new Rectangle(269, 6, 241, 172));
		jPanel3.setLayout(null);
		pSkinPreview.setBorder(BorderFactory.createEtchedBorder());
		pSkinPreview.setBounds(new Rectangle(174, 21, 52, 132));
		jScrollPane1.setBounds(new Rectangle(10, 22, 153, 132));
		lbSkins.addMouseListener(new FSettings_lbSkins_mouseAdapter(this));
		jPanel1.add(laCokeSiteLocal);
		jPanel1.add(tfCorsoSiteLocal);
		jPanel1.add(laPort);
		jPanel1.add(laDomain);
		jPanel1.add(laUser);
		jPanel1.add(laPassword);
		jPanel1.add(tfPassword);
		jPanel1.add(laCokeSiteServer);
		jPanel1.add(tfCorsoSiteServer);
		jPanel1.add(tfPort);
		jPanel1.add(tfDomain);
		jPanel1.add(tfUser);
		panel1.add(btAbbrechen);
		panel1.add(btOK);
		panel1.add(jPanel3);
		jPanel3.add(pSkinPreview);
		jPanel3.add(jScrollPane1);
		jScrollPane1.getViewport().add(lbSkins);
		panel1.add(jPanel2);
		jPanel2.add(tfPlayername);
		jPanel2.add(laPlayername);
		panel1.add(jPanel1);
		this.getContentPane().add(panel1, java.awt.BorderLayout.CENTER);
		tfCorsoSiteLocal.setText("");
		tfCorsoSiteLocal.setBounds(new Rectangle(125, 28, 116, 20));
	}

	/**
	 * check all data, save it if valid and close the dialog.
	 * 
	 * @param e
	 *            ActionEvent
	 */
	public void btOK_actionPerformed(ActionEvent e) {
		// check data
		if (tfCorsoSiteLocal.getText().length() == 0
				|| tfCorsoSiteServer.getText().length() == 0
				|| tfPort.getText().length() == 0
				|| tfUser.getText().length() == 0
				|| tfPassword.getPassword().length == 0
				|| tfDomain.getText().length() == 0) {
			Messages.errorMessage(this,
					"Please fill out all Corso settings fields.");
			return;
		}
		int port = 0;
		try {
			port = Integer.parseInt(tfPort.getText());
		} catch (Exception ex) {
			Messages.errorMessage(this, "Please define a correct port number.");
			return;
		}
		if (tfPlayername.getText().length() == 0) {
			Messages.errorMessage(this, "Please define a player name.");
			return;
		}

		// create new settings object
		Settings settings = new Settings();
		settings.setCokeSiteLocal(tfCorsoSiteLocal.getText());
		settings.setCokeSiteServer(tfCorsoSiteServer.getText());
		settings.setPort(port);
		// settings.setUsername(tfUser.getText());
		// settings.setPassword(new String(tfPassword.getPassword()));
		settings.setDomain(tfDomain.getText());
		settings.setPlayerName(tfPlayername.getText());
		settings.setSnakeSkin(skinsManager.getCurrentSkinName());

		// save settings, close dialog
		snakeMain.updateSettings(settings);
		this.dispose();
	}

	/**
	 * Close the dialog.
	 * 
	 * @param e
	 *            ActionEvent
	 */
	public void btAbbrechen_actionPerformed(ActionEvent e) {
		this.dispose();
	}

	/**
	 * Correct the size of the dialog when it is shown for the first time.
	 * 
	 * @param e
	 *            ComponentEvent
	 */
	public void this_componentShown(ComponentEvent e) {
		java.awt.Insets insets = getInsets();
		setSize(getWidth() + insets.left + insets.right, getHeight()
				+ insets.top + insets.bottom);
	}

	/**
	 * Show skin preview, when a skin is chosen.
	 * 
	 * @param e
	 *            MouseEvent
	 */
	public void lbSkins_mousePressed(MouseEvent e) {
		skinsManager.setCurrentSkin((String) lbSkins.getSelectedValue());
		imgCollectable = imgLoader.loadImage(skinsManager.getCollectablePath(),
				false);
		imgHead = imgLoader.loadImage(skinsManager.getSnakeHeadPath(), false);
		imgPart = imgLoader.loadImage(skinsManager.getSnakePartPath(), false);
		imgTail = imgLoader.loadImage(skinsManager.getSnakeTailPath(), false);
		pSkinPreview.updateUI();
	}
}

class FSettings_lbSkins_mouseAdapter extends MouseAdapter {
	private FSettings adaptee;

	FSettings_lbSkins_mouseAdapter(FSettings adaptee) {
		this.adaptee = adaptee;
	}

	public void mousePressed(MouseEvent e) {
		adaptee.lbSkins_mousePressed(e);
	}
}

class FSettings_this_componentAdapter extends ComponentAdapter {
	private FSettings adaptee;

	FSettings_this_componentAdapter(FSettings adaptee) {
		this.adaptee = adaptee;
	}

	public void componentShown(ComponentEvent e) {
		adaptee.this_componentShown(e);
	}
}

class FSettings_btAbbrechen_actionAdapter implements ActionListener {
	private FSettings adaptee;

	FSettings_btAbbrechen_actionAdapter(FSettings adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.btAbbrechen_actionPerformed(e);
	}
}

class FSettings_btOK_actionAdapter implements ActionListener {
	private FSettings adaptee;

	FSettings_btOK_actionAdapter(FSettings adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.btOK_actionPerformed(e);
	}
}
