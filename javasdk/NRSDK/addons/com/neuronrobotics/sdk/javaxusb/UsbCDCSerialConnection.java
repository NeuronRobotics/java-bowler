package com.neuronrobotics.sdk.javaxusb;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.usb.event.UsbDeviceDataEvent;
import javax.usb.event.UsbDeviceErrorEvent;
import javax.usb.event.UsbDeviceEvent;
import javax.usb.event.UsbDeviceListener;
import javax.usb.util.DefaultUsbIrp;

import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.HotplugCallback;
import org.usb4java.HotplugCallbackHandle;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerDatagramFactory;
import com.neuronrobotics.sdk.common.BowlerRuntimeException;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.util.OsInfoUtil;
//import com.neuronrobotics.sdk.util.OsInfoUtil;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class UsbCDCSerialConnection extends BowlerAbstractConnection implements
		IUsbDeviceEventListener, UsbDeviceListener {
	static UsbServices services = null;

	private UsbDevice mDevice;
	private String MyDeviceString="";
	// private UsbInterface controlInterface;
	private UsbInterface dataInterface;

	private UsbEndpoint dataInEndpoint;
	private UsbEndpoint dataOutEndpoint;
	// private byte [] data = new byte[64];
	private DeviceHandle deviceHandle;
	private int interfaceNumber;

	private UsbPipe camInpipe;
	private UsbIrp read = new DefaultUsbIrp();

	private UsbPipe camOutpipe;
	private UsbIrp write = new DefaultUsbIrp();

	private static HotplugCallbackHandle callbackHandle;
	private static ArrayList<IUsbDeviceEventListener> usbDeviceEventListeners = new ArrayList<IUsbDeviceEventListener>();
	private static EventHandlingThread thread;
	
	public UsbCDCSerialConnection(String deviceString) {
		MyDeviceString=deviceString;
		setup();

	}
	public UsbCDCSerialConnection(UsbDevice device) {
		if (device == null)
			throw new NullPointerException(
					"A valid USB device is needed to regester this connection.");
		
		try {
			MyDeviceString=getUniqueID(device);
			
		} catch (UnsupportedEncodingException | 
				UsbDisconnectedException
				| UsbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setup();

	}

	/**
	 * This is the event handling thread. libusb doesn't start threads by its
	 * own so it is our own responsibility to give libusb time to handle the
	 * events in our own thread.
	 */
	static class EventHandlingThread extends Thread {
		/** If thread should abort. */
		private volatile boolean abort;

		/**
		 * Aborts the event handling thread.
		 */
		public void abort() {
			this.abort = true;
		}

		@Override
		public void run() {
			//if(!OsInfoUtil.isWindows()){
				setName("Bowler Platform USB Events thread");
				while (!this.abort) {
					// Let libusb handle pending events. This blocks until events
					// have been handled, a hotplug callback has been deregistered
					// or the specified time of .1 second (Specified in
					// Microseconds) has passed.
					try {
						int result = LibUsb.handleEventsTimeoutCompleted(null, 0,
								null);
					} catch (Exception e) {
						e.printStackTrace();
					}
					ThreadUtil.wait(100);
				}
			//}
		}
	}

	static {
		resetUsbSystem();
	}
	
	private static void resetUsbSystem(){
		try {
			
			services = UsbHostManager.getUsbServices();
			if(!OsInfoUtil.isWindows()){
				callbackHandle = new HotplugCallbackHandle();
				int result = LibUsb.hotplugRegisterCallback(null,
						LibUsb.HOTPLUG_EVENT_DEVICE_ARRIVED
								| LibUsb.HOTPLUG_EVENT_DEVICE_LEFT,
						LibUsb.HOTPLUG_ENUMERATE, LibUsb.HOTPLUG_MATCH_ANY,
						LibUsb.HOTPLUG_MATCH_ANY, LibUsb.HOTPLUG_MATCH_ANY,
						new HotplugCallback() {
	
							@Override
							public int processEvent(Context arg0, Device arg1,
									int arg2, Object arg3) {
								DeviceDescriptor descriptor = new DeviceDescriptor();
								int result = LibUsb.getDeviceDescriptor(arg1,
										descriptor);
								if (result != LibUsb.SUCCESS)
									throw new LibUsbException(
											"Unable to read device descriptor",
											result);
								if (0x04d8 == descriptor.idVendor()) {
									for (IUsbDeviceEventListener d : usbDeviceEventListeners) {
										d.onDeviceEvent(mapLibUsbDevicetoJavaxDevice(arg1));
									}
								}
								return 0;
							}
						}, null, callbackHandle);
				if (result != LibUsb.SUCCESS) {
					throw new LibUsbException("Unable to register hotplug callback", result);
				}
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UsbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(thread!=null)
			thread.abort();

		// Start the event handling thread
		thread = new EventHandlingThread();
		thread.start();
	}

	static public void addUsbDeviceEventListener(IUsbDeviceEventListener l) {
		if (!usbDeviceEventListeners.contains(l))
			usbDeviceEventListeners.add(l);
	}

	static public void removeUsbDeviceEventListener(IUsbDeviceEventListener l) {
		if (usbDeviceEventListeners.contains(l))
			usbDeviceEventListeners.remove(l);
	}

	public static UsbDevice mapLibUsbDevicetoJavaxDevice(Device device) {
		try {
			DeviceDescriptor descriptor = new DeviceDescriptor();
			LibUsb.getDeviceDescriptor(device, descriptor);
			ArrayList<UsbDevice> javaxDev = getAllUsbBowlerDevices();
			for (UsbDevice d : javaxDev) {
				if (descriptor.iSerialNumber() == d.getUsbDeviceDescriptor()
						.iSerialNumber()
						&& descriptor.idProduct() == d.getUsbDeviceDescriptor()
								.iProduct()
						&& descriptor.idVendor() == d.getUsbDeviceDescriptor()
								.idVendor()) {
					return d;
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
		return null;

	}

	@SuppressWarnings("unchecked")
	private static void dumpDevice(final UsbDevice device,
			ArrayList<UsbDevice> addrs) throws UnsupportedEncodingException,
			UsbDisconnectedException, UsbException {
		try {
			if (device.getUsbDeviceDescriptor().idVendor() == 0x04d8) {// Neuron
																		// robotics
																		// devices
				// Dump information about the device itself
				// System.out.println("Device: "+device.getProductString());
				addrs.add(device);

				// Dump device descriptor
				// System.out.println(device.getUsbDeviceDescriptor());
			}

			// System.out.println();

			// Dump child devices if device is a hub
			if (device.isUsbHub()) {
				final UsbHub hub = (UsbHub) device;
				for (UsbDevice child : (List<UsbDevice>) hub
						.getAttachedUsbDevices()) {
					dumpDevice(child, addrs);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<UsbDevice> getAllUsbBowlerDevices()
			throws UnsupportedEncodingException, UsbDisconnectedException,
			SecurityException, UsbException {
		ArrayList<UsbDevice> addrs = null;
		if (addrs == null) {
			addrs = new ArrayList<UsbDevice>();
			dumpDevice(services.getRootUsbHub(), addrs);

		}
		return addrs;
	}

	public static String getUniqueID(UsbDevice d)
			throws UnsupportedEncodingException, UsbDisconnectedException,
			UsbException {
		return d.getProductString().trim() + " "
				+ d.getSerialNumberString().trim();
	}


	
	private void setup(){
		
		ArrayList<UsbDevice> devices;
		try {
			devices = getAllUsbBowlerDevices();
			this.mDevice = null;

			for (UsbDevice d : devices) {
				if (getUniqueID(d).contains(MyDeviceString)) {
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

		if (MyDeviceString == null)
			throw new NullPointerException(
					"A valid USB device is needed to regester this connection.");
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#connect()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean connect() {
		try{
			localDisconnect();
			resetUsbSystem();
			setup();
		}catch(Exception e){
			e.printStackTrace();
		}

		// System.out.println(mDevice);
		// Dump device descriptor
		// System.out.println(mDevice.getUsbDeviceDescriptor());

		// Process all configurations
		for (UsbConfiguration configuration : (List<UsbConfiguration>) mDevice
				.getUsbConfigurations()) {
			// Process all interfaces
			for (UsbInterface iface : (List<UsbInterface>) configuration
					.getUsbInterfaces()) {
				// Dump the interface descriptor
				// System.out.println(iface.getUsbInterfaceDescriptor());

				if (iface.getUsbInterfaceDescriptor().bInterfaceClass() == 2) {
					// controlInterface = iface;
					// controlEndpoint = (UsbEndpoint)
					// controlInterface.getUsbEndpoints().get(0);
				}
				if (iface.getUsbInterfaceDescriptor().bInterfaceClass() == 10) {
					dataInterface = iface;
					try {
						kernelDetatch(mDevice);

					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						return false;
					}
					if (!dataInterface.isClaimed()) {
						try {
							dataInterface.claim();
							// Process all endpoints
							for (UsbEndpoint endpoint : (List<UsbEndpoint>) dataInterface
									.getUsbEndpoints()) {
								if (endpoint.getUsbEndpointDescriptor()
										.bEndpointAddress() == 0x03) {
									// System.out.println("Data out Endpipe");
									dataOutEndpoint = endpoint;

								} else {
									// System.out.println("Data in Endpipe");
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
					} else {
						Log.error("Interface is already climed");
					}

				}

			}
		}

		if (dataInEndpoint != null && dataOutEndpoint != null) {

			setConnected(true);
			mDevice.addUsbDeviceListener(this);
		}

		

		return isConnected();
	}

	public Device findDevice(String seriualNumber) {
		//if(!OsInfoUtil.isWindows()){
			// Read the USB device list
			DeviceList list = new DeviceList();
			int result = LibUsb.getDeviceList(null, list);
			if (result < 0)
				throw new LibUsbException("Unable to get device list", result);
	
			try {
				// Iterate over all devices and scan for the right one
				for (Device device : list) {
	
					DeviceDescriptor descriptor = new DeviceDescriptor();
					result = LibUsb.getDeviceDescriptor(device, descriptor);
					if (result != LibUsb.SUCCESS)
						throw new LibUsbException(
								"Unable to read device descriptor", result);
					DeviceHandle handle = new DeviceHandle();
					result = LibUsb.open(device, handle);
					if (result == LibUsb.SUCCESS) {
						String sn = LibUsb.getStringDescriptor(handle,
								descriptor.iSerialNumber()).trim();
						LibUsb.close(handle);
						if (sn.contains(seriualNumber.trim())) {
	
							return device;
						}
					}
				}
			} finally {
				// Ensure the allocated device list is freed
				LibUsb.freeDeviceList(list, true);
			}
		//}
		// Device not found
		return null;
	}

	private void kernelDetatch(UsbDevice mDevice){
		//if(!OsInfoUtil.isWindows()){
			Device kDev=null;
			try {
				kDev = findDevice(mDevice.getSerialNumberString());
			} catch (UnsupportedEncodingException | 
					UsbDisconnectedException
					| UsbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (kDev == null)
				return;
	
			deviceHandle = new DeviceHandle();
			interfaceNumber = dataInterface.getUsbInterfaceDescriptor()
					.bInterfaceNumber();
	
			int result = LibUsb.open(kDev, deviceHandle);
			if (result != LibUsb.SUCCESS)
				throw new LibUsbException("Unable to open USB device", result);
	
			int r = LibUsb.detachKernelDriver(deviceHandle, interfaceNumber);
			if (r != LibUsb.SUCCESS && r != LibUsb.ERROR_NOT_SUPPORTED
					&& r != LibUsb.ERROR_NOT_FOUND)
				throw new LibUsbException("Unable to detach kernel     driver", r);
			// System.out.println("Kernel detatched for device "+mDevice);
		//}
	}
	
	private void localDisconnect(){
		mDevice.removeUsbDeviceListener(this);
		try {
			if(camInpipe!=null)
				camInpipe.close();
			camInpipe=null;
			if(camOutpipe!=null)
				camOutpipe.close();
			camOutpipe=null;
		} catch (UsbNotActiveException | UsbNotOpenException
				| UsbDisconnectedException | UsbException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if(dataInterface!=null){
			if (dataInterface.isClaimed()){
				try {
					dataInterface.release();
					dataInterface=null;
				} catch (UsbNotActiveException | 
						UsbDisconnectedException
						| UsbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		//if(!OsInfoUtil.isWindows()){
		if (deviceHandle != null) {
				//LibUsb.attachKernelDriver(deviceHandle, interfaceNumber);
			try{
				LibUsb.close(deviceHandle);
				deviceHandle=null;
			}catch(IllegalStateException e){
				e.printStackTrace();
			}
		}
		//}


	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#disconnect()
	 */
	@Override
	public void disconnect() {
		super.disconnect();
		localDisconnect();

	}

	private void prepIrp(UsbIrp irp, byte[] data) {
		irp.complete();
		irp.setData(data);
		irp.setLength(data.length);
		irp.setOffset(0);
		irp.setAcceptShortPacket(true);
		irp.setComplete(false);
		irp.setUsbException(null);
	}

	/**
	 * Write.
	 *
	 * @param data
	 *            the data
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	// private ByteList outgoing = new ByteList();
	public void write(byte[] src) throws IOException {
		if (!isConnected())
			return;
		waitForConnectioToBeReady();
		setLastWrite(System.currentTimeMillis());
		try {
			if (camOutpipe == null) {
				camOutpipe = dataOutEndpoint.getUsbPipe();

			}
			if (!camOutpipe.isOpen())
				camOutpipe.open();
			// if(write == null){
			//
			// write = camOutpipe.createUsbIrp();
			// System.out.println("Write is a "+write.getClass());
			// System.out.println("camOutpipe is a "+camOutpipe.getClass());
			// }
			// write = new DefaultUsbIrp();
			prepIrp(write, src);

			camOutpipe.asyncSubmit(write);
			write.waitUntilComplete();

			while (!write.isComplete()) {
				ThreadUtil.wait(0, 1);
			}

		} catch (Exception e) {// TODO Auto-generated catch block
			e.printStackTrace();
			connect();
			throw new BowlerRuntimeException(
					"Connection is no longer availible "
							+ e.getLocalizedMessage());
		}

		return;
	}

	@Override
	public boolean loadPacketFromPhy(ByteList bytesToPacketBuffer)
			throws NullPointerException, IOException {

		if (dataInEndpoint == null)
			return false;
		int got = 0;
		byte[] data = new byte[64];
		try {
			if (camInpipe == null) {
				camInpipe = dataInEndpoint.getUsbPipe();

			}
			if (!camInpipe.isOpen())
				camInpipe.open();
			// if(read == null)
			// read = camInpipe.createUsbIrp();

			// read = new DefaultUsbIrp();
			prepIrp(read, data);
	
			camInpipe.asyncSubmit(read);
		

			read.waitUntilComplete();

			got = read.getActualLength();

			while (!read.isComplete()) {
				ThreadUtil.wait(0, 1);
			}

		} catch ( IllegalArgumentException 
				| UsbNotActiveException 
				| UsbNotOpenException
				| UsbDisconnectedException 
				| UsbException e) {
			e.printStackTrace();
			connect();
			return false;
		}
		if (got > 0) {
			bytesToPacketBuffer.add(Arrays.copyOfRange(data, 0, got));
			BowlerDatagram bd = BowlerDatagramFactory
					.build(bytesToPacketBuffer);
			if (bd != null) {
				// Log.info("\nR<<"+bd);
				onDataReceived(bd);

				// Packet found, break the loop and deal with it
				return true;
			}
		}

		return false;
	}

	// /* (non-Javadoc)
	// * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#reconnect()
	// */
	// @Override
	// public boolean reconnect() {
	// if(!isConnected())
	// return false;
	// else
	// return true;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.neuronrobotics.sdk.common.BowlerAbstractConnection#waitingForConnection
	 * ()
	 */
	@Override
	public boolean waitingForConnection() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dataEventOccurred(UsbDeviceDataEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void errorEventOccurred(UsbDeviceErrorEvent arg0) {
		if(arg0.getUsbDevice() == mDevice){
			new RuntimeException("Disconnect in USB called").printStackTrace();
			//disconnect();
			connect() ;
		}
	}

	@Override
	public void usbDeviceDetached(UsbDeviceEvent arg0) {
		
		if(arg0.getUsbDevice() == mDevice){
			new RuntimeException("Disconnect in USB called").printStackTrace();
			//disconnect();
			connect() ;
		}
	}

	@Override
	public void onDeviceEvent(UsbDevice device) {
		// TODO Auto-generated method stub

	}

}
