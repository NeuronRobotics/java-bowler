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
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.usb.UsbDevice;
import javax.usb.UsbDisconnectedException;
import javax.usb.UsbException;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.javaxusb.UsbCDCSerialConnection;

/**
 * 
 */
public class UsbConnectionPanel extends AbstractConnectionPanel {

	private static final long serialVersionUID = 1L;
	

	private JComboBox<String> connectionCbo = null;
	private JButton refresh;
	
	private UsbCDCSerialConnection connection = null;
	
	/**
	 * 
	 */
	public UsbConnectionPanel() {
		super("USB", ConnectionImageIconFactory.getIcon("images/usb-icon.png"));
		

		connectionCbo = new JComboBox<String>();
		connectionCbo.setEditable(true);
		
		refresh = new JButton("Refresh");
		refresh.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				refresh();
			}
		});
		
		setLayout(new MigLayout("",	// Layout Constraints
				                "[right][left]", // Column constraints with default align
				                "[center][center]"	// Row constraints with default align
				               ));

		add(new JLabel("Connection:"), "cell 0 0");
		add(connectionCbo);
		add(refresh);
		
		refresh();
	}

	
	public BowlerAbstractConnection getConnection() {
		try {

			String port =connectionCbo.getSelectedItem().toString();
			connection = new UsbCDCSerialConnection(port);
			Log.info("Using port:"+port+"\n");
		} catch(NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Invalid baudrate given. Please review the list of valid baudrates.", "Invalid Baudrate", JOptionPane.ERROR_MESSAGE);
		} catch(Exception e) {
		} finally {
			setVisible(false);
		}
		return connection;
	}

	
	public void refresh() {		
		connectionCbo.removeAllItems();

		List<UsbDevice> prts;
		try {
			prts = UsbCDCSerialConnection.getAllUsbBowlerDevices();
			for(int i=0;i<prts.size();i++) {
				String s = prts.get(i).getProductString();
				connectionCbo.addItem(s);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UsbDisconnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UsbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
