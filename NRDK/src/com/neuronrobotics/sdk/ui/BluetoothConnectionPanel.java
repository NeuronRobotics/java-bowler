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

import javax.bluetooth.RemoteDevice;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.util.IMonitorable;
import com.neuronrobotics.sdk.util.IProgressMonitorListener;
import com.neuronrobotics.sdk.util.ProcessMonitor;
import com.neuronrobotics.sdk.wireless.bluetooth.BlueCoveManager;
import com.neuronrobotics.sdk.wireless.bluetooth.BluetoothSerialConnection;

/**
 * 
 */
public class BluetoothConnectionPanel extends AbstractConnectionPanel {

	private static final long serialVersionUID = 1L;

	private BluetoothSerialConnection connection;
	private BlueCoveManager blue = null;
	private boolean displayWarning = false;
	
	private JComboBox connectionCbo;
	private JButton search;
	private JProgressBar progress = new JProgressBar();
	private JLabel message = new JLabel();
	
	/**
	 * 
	 */
	public BluetoothConnectionPanel() {
		super("Bluetooth", ConnectionImageIconFactory.getIcon("images/bluetooth-icon.png"));

		if(displayWarning) {
			return;
		}
		
		search = new JButton("Search for Devices");
		search.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refresh();
			}
		});
		
		setLayout(new MigLayout("",	// Layout Constraints
				 "[right][left]", // Column constraints with default align
				 "[center][center]"	// Row constraints with default align
				));
		
		connectionCbo = new JComboBox();
				
		add(new JLabel("Connection:"), "cell 0 0");
		add(connectionCbo);
		add(search, "wrap");
		add(progress, "spanx, growx");
		add(message, "spanx, growx");
	}

	@Override
	public BluetoothSerialConnection getConnection() {
		try {
			String port = connectionCbo.getSelectedItem().toString();
			RemoteDevice dev = blue.getDevice(port);
			connection = new BluetoothSerialConnection(blue,dev.getBluetoothAddress());
			Log.info("Using device:"+port+"\n");
		} catch(Exception e) {
			Log.warning("Unable to connect with bluetooth connection");
		}
		return connection;
	}
	
	@Override
	public void refresh() {
		Log.info("Searching for devices over bluetooth...");
		
		search.setEnabled(false);
		
		connectionCbo.removeAllItems();
		connectionCbo.setEnabled(false);

		progress.setIndeterminate(true);
		
		message.setText("Searching...");
		
		BluetoothSearchProcess bsp = new BluetoothSearchProcess();
		
		ProcessMonitor pm = new ProcessMonitor(bsp);
		pm.addProcessMonitorListener(new IProgressMonitorListener() {
			
			@Override
			public void onUpdate(double value) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onComplete() {
				progress.setIndeterminate(false);
				search.setEnabled(true);
				connectionCbo.setEnabled(true);
			}
		});
		pm.start();
		bsp.start();
	}
	
	private class BluetoothSearchProcess extends Thread implements IMonitorable {
		private boolean isRunning = false;
		public void run() {
			isRunning = true;
			try {
				if (blue == null)
					blue = new BlueCoveManager();
				message.setText("Searching for bluetooth devices, please wait...");
				String [] devices = blue.getAvailableSerialDevices(true);
				connectionCbo.removeAllItems();
				for(String s: devices) {
					Log.info("Adding: "+s);
					message.setText("Adding " + s);
					connectionCbo.addItem(s);
				}
				if(devices.length == 0) {
					message.setText("No devices found");
				}
			} catch(Exception e) {
				e.printStackTrace();
				displayWarning = true;
				String m = "BlueCove not installed properly, native library not found or missing dependancy\n\n";		
				JTextArea tx = new JTextArea();
				tx.setBorder(null);
				tx.setLineWrap(true);
				tx.setWrapStyleWord(true);
				tx.setText(m);
				tx.setColumns(20);
				removeAll();
				add(new JLabel(ConnectionImageIconFactory.getIcon("images/dialog-error.png")), "cell 0 0,ax center, ay center");
				add(tx, "cell 1 0");
			} finally {
				if (connection!=null) {
					connection.disconnect();
				}
				
				connection = null;
				isRunning = false;
			}
		}
		
		@Override
		public double getPercentage() {
			return 0;
		}

		@Override
		public boolean isComplete() {
			return !isRunning;
		}
		
	}
}
