package com.neuronrobotics.sdk.javaxusb;



import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.usb.UsbClaimException;
import javax.usb.UsbConfiguration;
import javax.usb.UsbConst;
import javax.usb.UsbControlIrp;
import javax.usb.UsbDevice;
import javax.usb.UsbDisconnectedException;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbInterface;
import javax.usb.UsbInterfaceDescriptor;
import javax.usb.UsbIrp;
import javax.usb.UsbNotActiveException;
import javax.usb.UsbNotOpenException;
import javax.usb.UsbPipe;
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
import com.neuronrobotics.sdk.common.Log;

public class UsbCDCSerialConnection extends BowlerAbstractConnection implements HotplugCallback{
	static UsbServices services=null;
	private HotplugCallbackHandle callbackHandle;
	private UsbDevice mDevice;
	private UsbInterface controlInterface;
	private UsbEndpoint  controlEndpoint;
	private UsbInterface dataInterface;
	private UsbEndpoint dataInEndpoint;
	private UsbEndpoint dataOutEndpoint;
	private byte [] data = new byte[64];
	
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
			this.mDevice = null;
			
			for(UsbDevice d:devices){
				if(d.getProductString().contains(device)){
					this.mDevice = d;
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
		this.mDevice = device;
		
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#connect()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean connect(){
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
        
        //System.out.println(mDevice);
        // Dump device descriptor
        //System.out.println(mDevice.getUsbDeviceDescriptor());

        // Process all configurations
        for (UsbConfiguration configuration: (List<UsbConfiguration>) mDevice
            .getUsbConfigurations())
        {
            // Process all interfaces
            for (UsbInterface iface: (List<UsbInterface>) configuration
                .getUsbInterfaces())
            {
                // Dump the interface descriptor
                //System.out.println(iface.getUsbInterfaceDescriptor());
                
                if(iface.getUsbInterfaceDescriptor().bInterfaceClass() == 2){
                	controlInterface = iface;
                	controlEndpoint =  (UsbEndpoint) controlInterface.getUsbEndpoints().get(0);
                }
                if(iface.getUsbInterfaceDescriptor().bInterfaceClass() == 10){
                	dataInterface = iface;
                	if(!dataInterface.isClaimed()){
	                	try {
							dataInterface.claim();
		                	// Process all endpoints
		                    for (UsbEndpoint endpoint: (List<UsbEndpoint>) dataInterface.getUsbEndpoints())
		                    {
		                        if(endpoint.getUsbEndpointDescriptor().bEndpointAddress()==0x03){
		                        	//System.out.println("Data out Endpipe");
		                        	dataOutEndpoint = endpoint;
	
		                        }else {
		                        	//System.out.println("Data in Endpipe");
		                        	dataInEndpoint = endpoint;
		                        }
		                    }
						} catch (UsbClaimException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UsbNotActiveException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UsbDisconnectedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UsbException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}else{
                		Log.error("Interface is already climed");
                	}

                }
                
            }
        }
        
        if(dataInEndpoint != null && dataOutEndpoint != null )
        	setConnected(true);
        
        
		return isConnected();	
	}

	

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#disconnect()
	 */
	@Override
	public void disconnect() {
		LibUsb.hotplugDeregisterCallback(null, callbackHandle);
		try {
			if(dataInterface.isClaimed())
				dataInterface.release();
		} catch (UsbNotActiveException
				| UsbDisconnectedException | UsbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Write.
	 *
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	//private ByteList outgoing = new ByteList();
	public void write(byte[] src) throws IOException {
		waitForConnectioToBeReady();
		
		try {
			UsbPipe camOutpipe = dataOutEndpoint.getUsbPipe();
			camOutpipe.open();
			UsbIrp write = camOutpipe.createUsbIrp();
            write.setData(src);
            write.setLength(src.length);
            write.setOffset(0);
            write.setAcceptShortPacket(true);

            camOutpipe.syncSubmit(write);
            write.waitUntilComplete(getSleepTime());
            camOutpipe.close();
			
		} catch (UsbNotActiveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UsbNotOpenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UsbDisconnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UsbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return ;
	}
	
	@Override
	public boolean loadPacketFromPhy(ByteList bytesToPacketBuffer) throws NullPointerException, IOException{
		
		if(dataInEndpoint == null)
			return false;
		int got=0;
		try {
			UsbPipe camInpipe = dataInEndpoint.getUsbPipe();
			
			camInpipe.open();
			
			 UsbIrp read = camInpipe.createUsbIrp();
	        read.setData(data);
	        read.setLength(data.length);
	        read.setOffset(0);
	        read.setAcceptShortPacket(true);
	        
	        camInpipe.asyncSubmit(read);
	 
	        read.waitUntilComplete(getSleepTime()); 
	        
	        got=read.getActualLength();
	        
			camInpipe.close();
			
		} catch (UsbNotActiveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UsbNotOpenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UsbDisconnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UsbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(got>0){
			System.err.println("Got bytes! "+ got);
			for(int i=0;i<got;i++){
				System.err.print(", "+ data[i]);
				bytesToPacketBuffer.add(data[i]);
			}
			return true;
		}
	
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
        if(this.mDevice.getUsbDeviceDescriptor().idVendor() == descriptor.idVendor() &&
        		this.mDevice.getUsbDeviceDescriptor().idProduct() == descriptor.idProduct() ){
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
