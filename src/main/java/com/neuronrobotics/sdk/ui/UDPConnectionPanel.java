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
import com.neuronrobotics.sdk.network.UDPBowlerConnection;
import com.neuronrobotics.sdk.util.IMonitorable;
import com.neuronrobotics.sdk.util.IProgressMonitorListener;
import com.neuronrobotics.sdk.util.ProcessMonitor;

// TODO: Auto-generated Javadoc
/**
 * The Class UDPConnectionPanel.
 */
public class UDPConnectionPanel extends AbstractConnectionPanel {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The Constant defaultPortNum. */
	private static final int defaultPortNum = 1865;
	
	/** The connection cbo. */
	private JComboBox connectionCbo = null;
	
	/** The refresh. */
	private JButton refresh;
	
	/** The port. */
	private JTextField port = new JTextField(8);
	
	/** The clnt. */
	UDPBowlerConnection clnt=null;
	
	
	/**
	 * Instantiates a new UDP connection panel.
	 *
	 * @param connectionDialog the connection dialog
	 */
	public UDPConnectionPanel(ConnectionDialog connectionDialog) {
		super("UDP",ConnectionImageIconFactory.getIcon("images/ethernet-icon.png"),connectionDialog);
		
		
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

	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.ui.AbstractConnectionPanel#getConnection()
	 */
	public BowlerAbstractConnection getConnection() {
		try {
			int baud = Integer.parseInt(port.getText());
			if(baud < 0) {
				throw new NumberFormatException();
			}
			String address =connectionCbo.getSelectedItem().toString().trim();
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

	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.ui.AbstractConnectionPanel#refresh()
	 */
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
		getConnectionDialog().pack();
	}
	
	/**
	 * The Class NetworkSearchProcess.
	 */
	private class NetworkSearchProcess extends Thread implements IMonitorable {
		
		/** The is running. */
		private boolean isRunning = false;
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			setName("Bowler Platform UDP searcher");
			isRunning = true;
			//System.out.println("Searching for UDP devices, please wait...");
			int prt;
			try {
				prt=new Integer(port.getText());
			}catch (NumberFormatException e) {
				prt=defaultPortNum;
				port.setText(new Integer(defaultPortNum).toString());
			}
			clnt=new UDPBowlerConnection(prt);
			ArrayList<InetAddress>  addrs = clnt.getAllAddresses();
//			if (addrs.size()>0)
//				System.out.println("Bowler servers: "+addrs);
			connectionCbo.removeAllItems();
			for (InetAddress i:addrs) {
				connectionCbo.addItem(i.getHostAddress());
			}
			if(addrs.size() ==0 )
				connectionCbo.addItem("No Servers Found");
			
			isRunning = false;
		}
		
		/* (non-Javadoc)
		 * @see com.neuronrobotics.sdk.util.IMonitorable#getPercentage()
		 */
		public double getPercentage() {
			return 0;
		}
		
		/* (non-Javadoc)
		 * @see com.neuronrobotics.sdk.util.IMonitorable#isComplete()
		 */
		public boolean isComplete() {
			return !isRunning;
		}
	}
}
