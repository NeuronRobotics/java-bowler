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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;



import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;

import com.neuronrobotics.sdk.network.BowlerUDPClient;
import com.neuronrobotics.sdk.util.IMonitorable;
import com.neuronrobotics.sdk.util.IProgressMonitorListener;
import com.neuronrobotics.sdk.util.ProcessMonitor;

/**
 * 
 */
public class UDPConnectionPanel extends AbstractConnectionPanel {

	private static final long serialVersionUID = 1L;
	private static final int defaultPortNum = 1865;
	private JComboBox connectionCbo = null;
	private JButton refresh;
	private JTextField port = new JTextField(8);
	BowlerUDPClient clnt=null;
	
	
	/**
	 * 
	 */
	public UDPConnectionPanel() {
		super("UDP",ConnectionImageIconFactory.getIcon("images/ethernet-icon.png"));
		
		
		port.setText(new Integer(defaultPortNum).toString());
		
		setLayout(new MigLayout("",	// Layout Constraints
				                "[right][left]", // Column constraints with default align
				                "[center][center]"	// Row constraints with default align
				               ));

		add(new JLabel("Server:"), "cell 0 0");
		connectionCbo = new JComboBox();
		connectionCbo.setEditable(true);
		connectionCbo.addItem("none");
		add(connectionCbo);
		refresh = new JButton("Refresh");
		refresh.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				refresh();
			}
		});
		add(refresh);
		
		add(new JLabel("Port:"), "cell 0 1");
		add(port, "cell 1 1");
		
		//refresh();
	}

	
	public BowlerAbstractConnection getConnection() {
		try {
			int baud = Integer.parseInt(port.getText());
			if(baud < 0) {
				throw new NumberFormatException();
			}
			String address =connectionCbo.getSelectedItem().toString();
			clnt.setAddress(address);
			setVisible(false);
			return clnt;
		} catch(NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Invalid port given.", "Invalid port", JOptionPane.ERROR_MESSAGE);
		} catch(RuntimeException e) {
			JOptionPane.showMessageDialog(null, "Invalid address given.", "Invalid address", JOptionPane.ERROR_MESSAGE);
		} finally {
			setVisible(false);
		}
		return null;
	}

	
	public void refresh() {
		connectionCbo.removeAllItems();
		connectionCbo.addItem("Searching...");
		connectionCbo.setEnabled(false);
		
		port.setEnabled(false);
		
		refresh.setEnabled(false);
		
		NetworkSearchProcess nsp = new NetworkSearchProcess();
		
		ProcessMonitor pm = new ProcessMonitor(nsp);
		pm.addProcessMonitorListener(new IProgressMonitorListener() {
			
			
			public void onUpdate(double value) {
				// TODO Auto-generated method stub
				
			}
			
			
			public void onComplete() {
				connectionCbo.setEnabled(true);
				port.setEnabled(true);
				refresh.setEnabled(true);
			}
		});
		
		pm.start();
		nsp.start();		
	}
	
	private class NetworkSearchProcess extends Thread implements IMonitorable {
		private boolean isRunning = false;
		public void run() {
			isRunning = true;
			System.out.println("Searching for UDP devices, please wait...");
			int prt;
			try {
				prt=new Integer(port.getText());
			}catch (NumberFormatException e) {
				prt=defaultPortNum;
				port.setText(new Integer(defaultPortNum).toString());
			}
			clnt=new BowlerUDPClient(prt);
			ArrayList<InetAddress>  addrs = clnt.getAllAddresses();
			if (addrs.size()>0)
				System.out.println("Bowler servers: "+addrs);
			connectionCbo.removeAllItems();
			for (InetAddress i:addrs) {
				connectionCbo.addItem(i.getHostAddress());
			}
			if(addrs.size() ==0 )
				connectionCbo.addItem("No Servers Found");
			
			isRunning = false;
		}
		
		public double getPercentage() {
			return 0;
		}
		
		public boolean isComplete() {
			return !isRunning;
		}
	}
}
