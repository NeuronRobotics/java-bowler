package com.neuronrobotics.sdk.ui;

import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.miginfocom.swing.MigLayout;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.ConfigManager;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIOCommunicationException;
import com.neuronrobotics.sdk.serial.SerialConnection;

public class ConnectionDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private SerialConnection connection = null;
	private boolean isCancled = true;
	private JPanel panel;
	private JButton connectBtn;
	private JButton refresh;
	private JButton cancelBtn;
	private JTabbedPane connectionPanels;
	private static Packer fudge;
	private  LookAndFeel laf;
	public ConnectionDialog() {
		setIconImage( ConnectionImageIconFactory.getIcon("images/hat.png").getImage());
	       
		
		setModal(true);	
		
		connectionPanels = new JTabbedPane();
		
		loadDefaultConnections();
		
		connectBtn = new JButton("Connect");
		connectBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				try {
					Log.info("Using connection" + getConnection() + "\n");
					isCancled = false;
				} catch(Exception e) {
					JOptionPane.showMessageDialog(null, "Error connecting with the given connection.", "Connection Error", JOptionPane.ERROR_MESSAGE);
				} finally {
					setVisible(false);
				}
			}
		});
		
		cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				isCancled = true;
				setVisible(false);
			}
		});
		
		refresh = new JButton("Refresh");
		refresh.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				for(int i = 0; i < connectionPanels.getTabCount(); i++) {
					((AbstractConnectionPanel) connectionPanels.getComponentAt(i)).refresh();
				}
			}
		});
		panel = new JPanel(new MigLayout("",	// Layout Constraints
				 "[right][left]", // Column constraints with default align
				 "[center][center]"	// Row constraints with default align
				));
		
		panel.add(connectionPanels);
		panel.add(connectBtn, "cell 0 2 2 1");
		panel.add(cancelBtn, "cell 0 2 2 2");
		
		add(panel);
		setResizable(false);
		setTitle("Connection Information");
		pack();
		
		if (connection != null) {
			connection.disconnect();
		}
		connection = null;
		
		addWindowFocusListener(new WindowAdapter() {
		    public void windowGainedFocus(WindowEvent e) {
		        connectBtn.requestFocusInWindow();
		    }
		});
		fudge = new Packer();
		fudge.start();
	}
	
	private void loadDefaultConnections() {
		addConnectionPanel(new SerialConnectionPanel());
		addConnectionPanel(new BluetoothConnectionPanel());
		addConnectionPanel(new UDPConnectionPanel());
		addConnectionPanel(new TCPConnectionPanel());
	}

	public void addConnectionPanel(AbstractConnectionPanel panel) {
		connectionPanels.addTab(panel.getTitle(), panel.getIcon(), panel, panel.getToolTipText());
		connectionPanels.invalidate();
		connectionPanels.repaint();
	}
	
	/**
	 * Displays the dialog and blocks until the user has chosen 'Set', 'Cancel',
	 * or 'No Connection' Returns true if the user set a connection, returns
	 * false otherwise.
	 * 
	 * @return - Did the user cancel
	 */
	public boolean showDialog() {
		setLocationRelativeTo(null); 
	    setVisible(true);
	    return !isCancled;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public BowlerAbstractConnection getConnection() {
		
		BowlerAbstractConnection c = ((AbstractConnectionPanel) connectionPanels.getSelectedComponent()).getConnection();
		if(c == null) {
			JOptionPane.showMessageDialog(null, "Unable to create connection.", "Invalid Connection", JOptionPane.ERROR_MESSAGE);
		}
		
		return c;
	}
	
	public static boolean getBowlerDevice(BowlerAbstractDevice dev) {
		return getBowlerDevice(dev, null);
	}
	
	/**
	 * 
	 * 
	 * @param dev
	 * @return Returns if the device has been found
	 */
	public static boolean getBowlerDevice(BowlerAbstractDevice dev, AbstractConnectionPanel panel){
		if (dev == null) {
			return false;
		}
		BowlerAbstractConnection connection = null;
		while(connection == null) {
			Log.info("Select connection:");
			connection = ConnectionDialog.promptConnection(panel);
			if (connection == null) {
				Log.info("No connection selected...");
				return false;
			}
			Log.info("setting connection");
			try {
				dev.setConnection(connection);
				dev.connect();
				Log.info("Connected");
			} catch(DyIOCommunicationException e1) {
				String m = "The DyIO has not reported back to the library. \nCheck your connection and ensure you are attempting to talk to a DyIO, not another Bowler Device\nThis program will now exit.";
				JOptionPane.showMessageDialog(null, m, "DyIO Not Responding"+e1.getMessage(), JOptionPane.ERROR_MESSAGE);
				continue;
			} catch(Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, e.getMessage(), "DyIO Connection problem ", JOptionPane.ERROR_MESSAGE);
				fudge=null;
				return false;
			}
			Log.info("Attempting to ping");
			if(dev.ping() != null){
				Log.info("Ping OK!");
				break;
			}else{
				connection = null;
				JOptionPane.showMessageDialog(null, "No device on that port", "", JOptionPane.ERROR_MESSAGE);
			}
		}
		fudge=null;
		return true;
	}

	
	/**
	 * Displays a serial connection dialog to the user and returns the connection or null
	 * 
	 * @return the connection if one is selected, null if canceled or no connection is selected.
	 */
	public static BowlerAbstractConnection promptConnection() {
		if(!GraphicsEnvironment.isHeadless()) {
			ConnectionDialog cd = new ConnectionDialog();
			cd.showDialog();
		
			return cd.isCancled?null:cd.getConnection();
		}
		if(System.getProperty("nrdk.config.file") == null) {
			return null;
		}
		return getHeadlessConnection(System.getProperty("nrdk.config.file"));
	}
	
	public static BowlerAbstractConnection getHeadlessConnection(String config){
		return ConfigManager.loadDefaultConnection(config);
	}
	
	/**
	 * Displays a serial connection dialog to the user and returns the connection or null
	 * 
	 * @return the connection if one is selected, null if canceled or no connection is selected.
	 */
	public static BowlerAbstractConnection promptConnection(AbstractConnectionPanel panel) {
		if(System.getProperty("nrdk.config.file") == null) {
			ConnectionDialog cd = new ConnectionDialog();
			if(panel != null) {
				cd.addConnectionPanel(panel);
			}
			cd.showDialog();
		
			return cd.isCancled?null:cd.getConnection();
		}
		if(System.getProperty("nrdk.config.file") == null) {
			return null;
		}
		return getHeadlessConnection(System.getProperty("nrdk.config.file"));
	}
	
	private class Packer extends Thread{
		public void run() {
			while(true) {
				try {Thread.sleep(500);} catch (InterruptedException e) {}
				try {
					pack();
				}catch(Exception e) {
					return;
				}
			}
		}
	}
}
