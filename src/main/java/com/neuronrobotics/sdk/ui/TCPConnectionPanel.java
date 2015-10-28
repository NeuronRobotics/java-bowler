/*******************************************************************************
 * Copyright 2010 Neuron Robotics, LLC
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.neuronrobotics.sdk.ui;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.network.BowlerTCPClient;


// TODO: Auto-generated Javadoc
/**
 * The Class TCPConnectionPanel.
 */
public class TCPConnectionPanel extends AbstractConnectionPanel {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The default port num. */
	private static int defaultPortNum = 1866;
	
	/** The default server. */
	private static String defaultServer = "localhost";
	
	/** The connection cbo. */
	private JComboBox connectionCbo = null;
	
	/** The port. */
	private JTextField port = new JTextField(8);
	
	/** The clnt. */
	BowlerTCPClient clnt=null;
	
	
	/**
	 * Instantiates a new TCP connection panel.
	 *
	 * @param connectionDialog the connection dialog
	 */
	public TCPConnectionPanel(ConnectionDialog connectionDialog) {
		super("TCP", ConnectionImageIconFactory.getIcon("images/ethernet-icon.png"),connectionDialog);
		
		
		port.setText(new Integer(defaultPortNum).toString());
		
		setLayout(new MigLayout("",	// Layout Constraints
				                "[right][left]", // Column constraints with default align
				                "[center][center]"	// Row constraints with default align
				               ));

		add(new JLabel("Server:"), "cell 0 0");
		connectionCbo = new JComboBox();
		connectionCbo.setEditable(true);
		
//		Socket s;
//		try {
//			s = new Socket("google.com", 80);
//			connectionCbo.addItem(s.getLocalAddress().getHostAddress());
//			//System.out.println(s.getLocalAddress().getHostAddress());
//			s.close();
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//		}
	
		add(connectionCbo, "cell 1 0");
		
		add(new JLabel("Port:"), "cell 0 1");
		add(port, "cell 1 1");
		
	}
	
	/**
	 * Sets the default server.
	 *
	 * @param server the new default server
	 */
	public static void setDefaultServer(String server){
		defaultServer = server;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.ui.AbstractConnectionPanel#getConnection()
	 */
	@Override
	public BowlerAbstractConnection getConnection() {
		if(clnt == null){
			try {
				int thePort = Integer.parseInt(port.getText());
				if(thePort < 0) {
					throw new NumberFormatException();
				}
				String address =connectionCbo.getSelectedItem().toString();
				Log.info("Connecting on: "+address+":"+thePort);
				clnt = new BowlerTCPClient(address,thePort);
				setVisible(false);
				return clnt;
			} catch(NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid port given.", "Invalid port", JOptionPane.ERROR_MESSAGE);
			} catch(RuntimeException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Invalid address given.", "Invalid address", JOptionPane.ERROR_MESSAGE);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Invalid address given.", "Invalid address", JOptionPane.ERROR_MESSAGE);
			} finally {
				setVisible(false);
			}
			return null;
		}
		return clnt;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.ui.AbstractConnectionPanel#refresh()
	 */
	@Override
	public void refresh() {
		getConnectionDialog().pack();
	}
}
