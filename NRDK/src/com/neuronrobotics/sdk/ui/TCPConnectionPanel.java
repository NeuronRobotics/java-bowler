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

import java.io.IOException;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;



import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.Log;

import com.neuronrobotics.sdk.network.BowlerTCPClient;


/**
 * 
 */
public class TCPConnectionPanel extends AbstractConnectionPanel {

	private static final long serialVersionUID = 1L;
	private static int defaultPortNum = 1965;
	private static String defaultServer = "localhost";
	private JComboBox connectionCbo = null;
	private JTextField port = new JTextField(8);
	BowlerTCPClient clnt=null;
	
	
	/**
	 * 
	 */
	public TCPConnectionPanel() {
		super("TCP", ConnectionImageIconFactory.getIcon("images/ethernet-icon.png"));
		
		
		port.setText(new Integer(defaultPortNum).toString());
		
		setLayout(new MigLayout("",	// Layout Constraints
				                "[right][left]", // Column constraints with default align
				                "[center][center]"	// Row constraints with default align
				               ));

		add(new JLabel("Server:"), "cell 0 0");
		connectionCbo = new JComboBox();
		connectionCbo.setEditable(true);
		connectionCbo.addItem(defaultServer);
		add(connectionCbo, "cell 1 0");
		
		add(new JLabel("Port:"), "cell 0 1");
		add(port, "cell 1 1");
	}
	
	public static void setDefaultServer(String server){
		defaultServer = server;
	}

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
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Invalid address given.", "Invalid address", JOptionPane.ERROR_MESSAGE);
			} finally {
				setVisible(false);
			}
			return null;
		}
		return clnt;
	}

	@Override
	public void refresh() {
		
	}
}
