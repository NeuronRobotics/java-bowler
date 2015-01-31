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
import com.neuronrobotics.sdk.javaxusb.UsbCDCSerialConnection;
import com.neuronrobotics.sdk.util.ThreadUtil;

/**
 * 
 */
public class UsbConnectionPanel extends AbstractConnectionPanel  implements HotplugCallback{

	private static final long serialVersionUID = 1L;
	

	private JComboBox<String> connectionCbo = null;
	private JButton refresh;
	
	private UsbCDCSerialConnection connection = null;

	private HotplugCallbackHandle callbackHandle;
	private EventHandlingThread thread;
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
		
		callbackHandle = new HotplugCallbackHandle();
        int result = LibUsb.hotplugRegisterCallback(null,
            LibUsb.HOTPLUG_EVENT_DEVICE_ARRIVED
                | LibUsb.HOTPLUG_EVENT_DEVICE_LEFT,
            LibUsb.HOTPLUG_ENUMERATE,
            LibUsb.HOTPLUG_MATCH_ANY,
            LibUsb.HOTPLUG_MATCH_ANY,
            LibUsb.HOTPLUG_MATCH_ANY,
            this, null, callbackHandle);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to register hotplug callback",
                result);
        }
     // Start the event handling thread
        thread = new EventHandlingThread();
        thread.start();
	}

	
	public BowlerAbstractConnection getConnection() {

		String port =connectionCbo.getSelectedItem().toString();
		connection = new UsbCDCSerialConnection(port);
		Log.info("Using port:"+port+"\n");

		 // Unregister the hotplug callback and stop the event handling thread
        thread.abort();
        //LibUsb.hotplugDeregisterCallback(null, callbackHandle);
        try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
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
    /**
     * This is the event handling thread. libusb doesn't start threads by its
     * own so it is our own responsibility to give libusb time to handle the
     * events in our own thread.
     */
    static class EventHandlingThread extends Thread
    {
        /** If thread should abort. */
        private volatile boolean abort;

        /**
         * Aborts the event handling thread.
         */
        public void abort()
        {
            this.abort = true;
        }

        @Override
        public void run()
        {
            while (!this.abort)
            {
                // Let libusb handle pending events. This blocks until events
                // have been handled, a hotplug callback has been deregistered
                // or the specified time of .1 second (Specified in
                // Microseconds) has passed.
            	try{
            		int result = LibUsb.handleEventsTimeout(null, 1000000);
            	}catch (Exception e){
            		
            	}
            }
        }
    }

	@Override
	public int processEvent(Context context, Device device, int event,
            Object userData) {
        DeviceDescriptor descriptor = new DeviceDescriptor();
        int result = LibUsb.getDeviceDescriptor(device, descriptor);
        if (result != LibUsb.SUCCESS)
            throw new LibUsbException("Unable to read device descriptor",
                result);
        if(0x04d8 == descriptor.idVendor() ){
        	System.err.format("%s: %04x:%04x%n",
                    event == LibUsb.HOTPLUG_EVENT_DEVICE_ARRIVED ? "Connected " :
                        "Disconnected",
                    descriptor.idVendor(), descriptor.idProduct());
        	SwingUtilities.invokeLater(new Runnable() {
        	    public void run() {
        	    	ThreadUtil.wait(1000);
        	    	refresh();
        	    }
        	});
        	
        	
        }
        
        return 0;
	}
}
