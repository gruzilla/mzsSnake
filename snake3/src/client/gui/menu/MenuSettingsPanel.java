/**
 * 
 */
package client.gui.menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import mzs.util.Util;

import client.data.event.MenuEventData;
import client.data.event.MenuEventEnum;
import client.data.event.i.MenuEventListener;

/**
 * 
 * Settings JDialog, opens in new window - mal schauen ob es nicht sinnvoller ist das auch in ein Overlay zu tun
 * 
 * @author Jakob Lahmer, Matthias Steinbšck
 *
 */
public class MenuSettingsPanel extends MenuPanel {

	private static final long serialVersionUID = 1L;

	
	private JPanel panel1;
	private JFormattedTextField tfPort;
	private JLabel laPort;
	private JPasswordField tfPassword;
	private JTextField tfUser;
	private JLabel laUser;
	private JLabel laPassword;
	private JLabel laPlayername;
	private JTextField tfPlayername;
	private JTextField tfXVSMServer;
	private JLabel laCokeSiteServer;
	private JButton btAbbrechen;
	private JButton btOK;
	private JPanel jPanel1;
	private Border border1;
	private Border border2;
	private JPanel jPanel2;
	private Border border3;
	private Border border4;
	private JPanel jPanel3;
	private Border border5;
	private Border border6;

	// JPanel with overwritten paint method that draws a preview of the snake
	JPanel pSkinPreview;

	JScrollPane jScrollPane1;
	JList lbSkins;


	/**
	 * @param menuChangeEventListener
	 */
	public MenuSettingsPanel(MenuEventListener menuChangeEventListener) {
		super(menuChangeEventListener);
		
		this.setValues();
	}
	
	/**
	 * set default values to textfields etc
	 */
	protected void setValues() {
		tfXVSMServer.setText(Util.getInstance().getSettings().getServer());
		tfPort.setValue(Util.getInstance().getSettings().getPort());
		tfUser.setText(Util.getInstance().getSettings().getUsername());
		tfPassword.setText(Util.getInstance().getSettings().getPassword());
		tfPlayername.setText(Util.getInstance().getSettings().getPlayerName());
	}

	/**
	 * inits variables
	 */
	@Override
	protected void initVariables() {
		panel1 = new JPanel();
		jPanel1 = new JPanel();
		jPanel2 = new JPanel();
		jPanel3 = new JPanel();
		tfPort = new JFormattedTextField();
		tfPort.setValue(new Integer(0));
		tfPort.setColumns(4);
		
		laPort = new javax.swing.JLabel();
		tfPassword = new javax.swing.JPasswordField();
		tfUser = new javax.swing.JTextField();
		laUser = new javax.swing.JLabel();
		laPassword = new javax.swing.JLabel();
		laPlayername = new javax.swing.JLabel();
		tfPlayername = new javax.swing.JTextField();
		tfXVSMServer = new javax.swing.JTextField();
		laCokeSiteServer = new javax.swing.JLabel();
		btAbbrechen = new JButton();
		btOK = new JButton();
		border1 = BorderFactory.createEtchedBorder(Color.white, new Color(
				156, 156, 158));
		border2 = new TitledBorder(border1, "XVSM Connection:");
		border3 = BorderFactory.createEtchedBorder(Color.white, new Color(
				156, 156, 158));
		border4 = new TitledBorder(border3, "Game Settings:");
		border5 = BorderFactory.createEtchedBorder(Color.white, new Color(
				156, 156, 158));
		border6 = new TitledBorder(border5, "Skin Selection:");

		// JPanel with overwritten paint method that draws a preview of the snake
		pSkinPreview = new JPanel() {
			public void paint(Graphics g) {
				// draw snake skin preview
				g.clearRect(0, 0, 400, 400);
				g.drawRect(0, 0, 51, 131);
				/*
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
				*/
			}
		};

		jScrollPane1 = new JScrollPane();
		lbSkins = new JList();
	}
	
	
	@Override
	protected void init()	{
		panel1.setLayout(null);
/*
		tfXVSMSiteLocal.setFont(new java.awt.Font("Lucida Sans", Font.PLAIN,
				12));
		laCokeSiteLocal.setFont(new java.awt.Font("Arial", Font.PLAIN, 13));
		laCokeSiteLocal.setText("XVSM Site Local:");
		laCokeSiteLocal.setBounds(new Rectangle(16, 32, 106, 15));
*/
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
		tfXVSMServer.setFont(new java.awt.Font("Lucida Sans", Font.PLAIN,
				12));
		tfXVSMServer.setText("");
		tfXVSMServer.setBounds(new Rectangle(125, 50, 116, 20));
		laCokeSiteServer.setFont(new java.awt.Font("Arial", Font.PLAIN, 13));
		laCokeSiteServer.setText("XVSM Site Server:");
		laCokeSiteServer.setBounds(new Rectangle(16, 54, 106, 15));
/*		tfDomain.setFont(new java.awt.Font("Lucida Sans", Font.PLAIN, 12));
		tfDomain.setBounds(new Rectangle(125, 138, 116, 20));
		laDomain.setFont(new java.awt.Font("Arial", Font.PLAIN, 13));
		laDomain.setText("Domain:");
		laDomain.setBounds(new Rectangle(16, 141, 106, 15));
		this.setTitle("Snake Settings");
*/
		this.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void componentResized(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void componentMoved(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void componentHidden(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		btAbbrechen.setBounds(new Rectangle(265, 275, 100, 23));
		btAbbrechen.setToolTipText("");
		btAbbrechen.setText("Back");
		btAbbrechen.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				menuEventListener.menuChanged(new MenuEventData(MenuEventEnum.START_MENU));
			}
		});
		btOK.setBounds(new Rectangle(164, 275, 100, 23));
		btOK.setToolTipText("");
		btOK.setText("OK");
		btOK.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// store settings
				storeSettings();
				menuEventListener.menuChanged(new MenuEventData(MenuEventEnum.START_MENU));
				
			}
		});
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
		lbSkins.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
//		jPanel1.add(laCokeSiteLocal);
//		jPanel1.add(tfXVSMSiteLocal);
		jPanel1.add(laPort);
//		jPanel1.add(laDomain);
		jPanel1.add(laUser);
		jPanel1.add(laPassword);
		jPanel1.add(tfPassword);
		jPanel1.add(laCokeSiteServer);
		jPanel1.add(tfXVSMServer);
		jPanel1.add(tfPort);
//		jPanel1.add(tfDomain);
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
//		this.getContentPane().add(panel1, java.awt.BorderLayout.CENTER);
//		tfXVSMSiteLocal.setText("");
//		tfXVSMSiteLocal.setBounds(new Rectangle(125, 28, 116, 20));
		panel1.setSize(523, 310);
		panel1.setBounds(new Rectangle((gameMapSize.width - 523) / 2, (gameMapSize.height - 310) / 2, 523, 310));
		this.add(panel1);
	}
	
	

	/**
	 * store the settings to the settings file
	 */
	private void storeSettings() {
		Util.getInstance().getSettings().setServer(tfXVSMServer.getText());
		Util.getInstance().getSettings().setPort(((Number)tfPort.getValue()).intValue());
		Util.getInstance().getSettings().setUsername(tfUser.getText());
		Util.getInstance().getSettings().setPassword(new String(tfPassword.getPassword()));
		Util.getInstance().getSettings().setPlayerName(tfPlayername.getText());
		Util.getInstance().getSettings().save();
	}
	

	/**
	 * sets the dimensions of the transparent background box for current panel
	 * @return Dimension
	 */
	protected Dimension getBoxDimensions()	{
		return new Dimension(523, 310);
	}

}
