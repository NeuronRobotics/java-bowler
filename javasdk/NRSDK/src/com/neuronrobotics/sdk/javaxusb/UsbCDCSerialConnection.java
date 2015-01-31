package com.neuronrobotics.sdk.javaxusb;



import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.usb.UsbDevice;
import javax.usb.UsbDisconnectedException;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbServices;

import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.HotplugCallback;
import org.usb4java.HotplugCallbackHandle;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;


import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.ByteList;

public class UsbCDCSerialConnection extends BowlerAbstractConnection implements HotplugCallback{
	static UsbServices services=null;
	private HotplugCallbackHandle callbackHandle;
	private UsbDevice device;
	static{
		try {
			services = UsbHostManager.getUsbServices();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UsbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void dumpDevice(final UsbDevice device,ArrayList<UsbDevice> addrs) throws UnsupportedEncodingException, UsbDisconnectedException, UsbException
    {
		try{
	    	if(device.getUsbDeviceDescriptor().idVendor() == 0x04d8 ){// Neuron robotics devices
		        // Dump information about the device itself
		        //System.out.println("Device: "+device.getProductString());
		        addrs.add(device);
	
		        // Dump device descriptor
		        //System.out.println(device.getUsbDeviceDescriptor());
		    }
	
	        //System.out.println();
	
	        // Dump child devices if device is a hub
	        if (device.isUsbHub())
	        {
	            final UsbHub hub = (UsbHub) device;
	            for (UsbDevice child: (List<UsbDevice>) hub.getAttachedUsbDevices())
	            {
	                dumpDevice(child,addrs);
	            }
	        }
		}catch(Exception e){
			e.printStackTrace();
		}
    }
	
	public static ArrayList<UsbDevice>  getAllUsbBowlerDevices() throws UnsupportedEncodingException, UsbDisconnectedException, SecurityException, UsbException{
		ArrayList<UsbDevice> addrs=null;
		if(addrs== null){
			addrs=new ArrayList<UsbDevice>();
			dumpDevice(services.getRootUsbHub(),addrs);
			
		}
		return addrs;
	}


	public UsbCDCSerialConnection(String device) {
		
		ArrayList<UsbDevice> devices;
		try {
			devices = getAllUsbBowlerDevices();
			this.device = null;
			
			for(UsbDevice d:devices){
				if(d.getProductString().contains(device)){
					this.device = d;
				}
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

		if (device == null )
			throw new NullPointerException("A valid USB device is needed to regester this connection.");
		
	}
	public UsbCDCSerialConnection(UsbDevice device) {
		if (device == null )
			throw new NullPointerException("A valid USB device is needed to regester this connection.");
		this.device = device;
		
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#connect()
	 */
	@Override
	public boolean connect() {
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
        
        
        
        
		return isConnected();	
	}

	

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#disconnect()
	 */
	@Override
	public void disconnect() {
		LibUsb.hotplugDeregisterCallback(null, callbackHandle);
		
	}
	
	/**
	 * Write.
	 *
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	//private ByteList outgoing = new ByteList();
	public void write(byte[] data) throws IOException {
		waitForConnectioToBeReady();

		
	}
	
	@Override
	public boolean loadPacketFromPhy(ByteList bytesToPacketBuffer) throws NullPointerException, IOException{

	
		return false;
	}
	

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#reconnect()
	 */
	@Override
	public boolean reconnect() {
		if(!isConnected())
			return false;
		else
			return true;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#waitingForConnection()
	 */
	@Override
	public boolean waitingForConnection() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int processEvent(Context context, Device device, int event,
            Object userData) {
        DeviceDescriptor descriptor = new DeviceDescriptor();
        int result = LibUsb.getDeviceDescriptor(device, descriptor);
        if (result != LibUsb.SUCCESS)
            throw new LibUsbException("Unable to read device descriptor",
                result);
        if(this.device.getUsbDeviceDescriptor().idVendor() == descriptor.idVendor() &&
        		this.device.getUsbDeviceDescriptor().idProduct() == descriptor.idProduct() ){
//        	System.err.format("%s: %04x:%04x%n",
//                    event == LibUsb.HOTPLUG_EVENT_DEVICE_ARRIVED ? "Connected" :
//                        "Disconnected",
//                    descriptor.idVendor(), descriptor.idProduct());
        	
        }else{
//	        System.out.format("%s: %04x:%04x%n",
//	            event == LibUsb.HOTPLUG_EVENT_DEVICE_ARRIVED ? "Connected" :
//	                "Disconnected",
//	            descriptor.idVendor(), descriptor.idProduct());
        }
        
        return 0;
	}
}
