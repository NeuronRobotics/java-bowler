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
import javax.swing.SwingUtilities;
import javax.usb.UsbDevice;
import javax.usb.UsbDisconnectedException;
import javax.usb.UsbException;

import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.HotplugCallback;
import org.usb4java.HotplugCallbackHandle;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.javaxusb.IUsbDeviceEventListener;
import com.neuronrobotics.sdk.javaxusb.UsbCDCSerialConnection;
import com.neuronrobotics.sdk.util.ThreadUtil;

/**
 * 
 */
public class UsbConnectionPanel extends AbstractConnectionPanel implements IUsbDeviceEventListener{

	private static final long serialVersionUID = 1L;
	

	private JComboBox connectionCbo = null;
	private JButton refresh;
	
	private UsbCDCSerialConnection connection = null;

	/**
	 * 
	 */
	public UsbConnectionPanel() {
		super("USB", ConnectionImageIconFactory.getIcon("images/usb-icon.png"));
		

		connectionCbo = new JComboBox();
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
		
		UsbCDCSerialConnection.addUsbDeviceEventListener(this);


	}

	
	public BowlerAbstractConnection getConnection() {

		String port =connectionCbo.getSelectedItem().toString();
		connection = new UsbCDCSerialConnection(port);
		Log.info("Using port:"+port+"\n");


        
		setVisible(false);
		
		return connection;
	}

	
	public void refresh() {	
		System.err.println("Refreshing USB");
		connectionCbo.removeAllItems();

		List<UsbDevice> prts;
		try {
			prts = UsbCDCSerialConnection.getAllUsbBowlerDevices();
			for(int i=0;i<prts.size();i++) {
				String s = prts.get(i).getProductString();
				connectionCbo.addItem(s.trim());
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

	@Override
	public void onDeviceEvent(UsbDevice device) {
		SwingUtilities.invokeLater(new Runnable() {
    	    public void run() {
    	    	ThreadUtil.wait(500);
    	    	refresh();
    	    }
    	});
	}


}
