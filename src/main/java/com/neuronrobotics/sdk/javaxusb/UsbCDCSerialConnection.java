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

// TODO: Auto-generated Javadoc
/**
 * The Class UsbCDCSerialConnection.
 */
public class UsbCDCSerialConnection extends BowlerAbstractConnection implements
		IUsbDeviceEventListener, UsbDeviceListener {
	
	/** The services. */
	static UsbServices services = null;

	/** The m device. */
	private UsbDevice mDevice;
	
	/** The My device string. */
	private String MyDeviceString="";
	
	/** The data interface. */
	// private UsbInterface controlInterface;
	private UsbInterface dataInterface;

	/** The data in endpoint. */
	private UsbEndpoint dataInEndpoint;
	
	/** The data out endpoint. */
	private UsbEndpoint dataOutEndpoint;
	
	/** The device handle. */
	// private byte [] data = new byte[64];
	private DeviceHandle deviceHandle;
	
	/** The interface number. */
	private int interfaceNumber;

	/** The cam inpipe. */
	private UsbPipe camInpipe;
	
	/** The read. */
	private UsbIrp read = new DefaultUsbIrp();

	/** The cam outpipe. */
	private UsbPipe camOutpipe;
	
	/** The write. */
	private UsbIrp write = new DefaultUsbIrp();

	/** The callback handle. */
	private static HotplugCallbackHandle callbackHandle;
	
	/** The usb device event listeners. */
	private static ArrayList<IUsbDeviceEventListener> usbDeviceEventListeners = new ArrayList<IUsbDeviceEventListener>();
	
	/** The thread. */
	private static EventHandlingThread thread;
	
	/** The data. */
	byte[] data = new byte[64];
	
	/**
	 * Instantiates a new usb cdc serial connection.
	 *
	 * @param deviceString the device string
	 */
	public UsbCDCSerialConnection(String deviceString) {
		MyDeviceString=deviceString;
		setup();

	}
	
	/**
	 * Instantiates a new usb cdc serial connection.
	 *
	 * @param device the device
	 */
	public UsbCDCSerialConnection(UsbDevice device) {
		if (device == null)
			throw new NullPointerException(
					"A valid USB device is needed to regester this connection.");
		
		try {
			MyDeviceString=getUniqueID(device);
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UsbDisconnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UsbException e) {
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

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
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
	
	/**
	 * Reset usb system.
	 */
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

	/**
	 * Adds the usb device event listener.
	 *
	 * @param l the l
	 */
	static public void addUsbDeviceEventListener(IUsbDeviceEventListener l) {
		if (!usbDeviceEventListeners.contains(l))
			usbDeviceEventListeners.add(l);
	}

	/**
	 * Removes the usb device event listener.
	 *
	 * @param l the l
	 */
	static public void removeUsbDeviceEventListener(IUsbDeviceEventListener l) {
		if (usbDeviceEventListeners.contains(l))
			usbDeviceEventListeners.remove(l);
	}

	/**
	 * Map lib usb deviceto javax device.
	 *
	 * @param device the device
	 * @return the usb device
	 */
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

	/**
	 * Dump device.
	 *
	 * @param device the device
	 * @param addrs the addrs
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 * @throws UsbDisconnectedException the usb disconnected exception
	 * @throws UsbException the usb exception
	 */
	@SuppressWarnings("unchecked")
	private static void dumpDevice(final UsbDevice device,
			ArrayList<UsbDevice> addrs) throws UnsupportedEncodingException,
			UsbDisconnectedException, UsbException {
		try {
			if (device.getUsbDeviceDescriptor().idVendor() == 0x04d8 &&
					(	device.getUsbDeviceDescriptor().idProduct() == 0x0001||
						device.getUsbDeviceDescriptor().idProduct() == 0x3742
					)
				) {// Neuron
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

	/**
	 * Gets the all usb bowler devices.
	 *
	 * @return the all usb bowler devices
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 * @throws UsbDisconnectedException the usb disconnected exception
	 * @throws SecurityException the security exception
	 * @throws UsbException the usb exception
	 */
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

	/**
	 * Gets the unique id.
	 *
	 * @param d the d
	 * @return the unique id
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 * @throws UsbDisconnectedException the usb disconnected exception
	 * @throws UsbException the usb exception
	 */
	public static String getUniqueID(UsbDevice d)
			throws UnsupportedEncodingException, UsbDisconnectedException,
			UsbException {
		return d.getProductString().trim() + " "
				+ d.getSerialNumberString().trim();
	}


	
	/**
	 * Setup.
	 */
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

	/**
	 * Find device.
	 *
	 * @param seriualNumber the seriual number
	 * @return the device
	 */
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

	/**
	 * Kernel detatch.
	 *
	 * @param mDevice the m device
	 */
	private void kernelDetatch(UsbDevice mDevice){
		//if(!OsInfoUtil.isWindows()){
			Device kDev=null;
			try {
				kDev = findDevice(mDevice.getSerialNumberString());
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UsbDisconnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UsbException e) {
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
	
	/**
	 * Local disconnect.
	 */
	private void localDisconnect(){
		mDevice.removeUsbDeviceListener(this);
		try {
			if(camInpipe!=null)
				camInpipe.close();
			camInpipe=null;
			if(camOutpipe!=null)
				camOutpipe.close();
			camOutpipe=null;
		}  catch (UsbDisconnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UsbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(dataInterface!=null){
			if (dataInterface.isClaimed()){
				try {
					dataInterface.release();
					dataInterface=null;
				}  catch (UsbDisconnectedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UsbException e) {
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

	/**
	 * Prep irp.
	 *
	 * @param irp the irp
	 * @param data the data
	 */
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
	 * @param src the src
	 * @throws IOException             Signals that an I/O exception has occurred.
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

			prepIrp(write, src);

			camOutpipe.asyncSubmit(write);
			write.waitUntilComplete();

			while (!write.isComplete()) {
				ThreadUtil.wait(1);
			}

		} catch (Exception e) {// TODO Auto-generated catch block
			//e.printStackTrace();
			disconnect();
			throw new BowlerRuntimeException(
					"Connection is no longer availible "
							+ e.getLocalizedMessage());
		}

		return;
	}
	
	/**
	 * The Enum usbControlState.
	 */
	enum usbControlState{
		
		/** The init. */
		init,
		
		/** The submitted. */
		submitted,
		
		/** The done. */
		done
	} ;
	
	/** The usb read state. */
	usbControlState usbReadState = usbControlState.init;

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#loadPacketFromPhy(com.neuronrobotics.sdk.common.ByteList)
	 */
	@Override
	public BowlerDatagram loadPacketFromPhy(ByteList bytesToPacketBuffer)
			throws NullPointerException, IOException {

		if (dataInEndpoint == null)
			return null;
		int got = 0;
		
			
		switch (usbReadState){

		case init:
			try {
				if (camInpipe == null) {
					camInpipe = dataInEndpoint.getUsbPipe();
	
				}
				if (!camInpipe.isOpen())
					camInpipe.open();

				prepIrp(read, data);
		
				camInpipe.asyncSubmit(read);
			
	
				read.waitUntilComplete();
	
				usbReadState = usbControlState.submitted;
	
			} catch ( IllegalArgumentException 
					e) {
				//e.printStackTrace();
				disconnect();
				return null;
			} catch (UsbNotActiveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UsbNotOpenException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UsbDisconnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UsbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case submitted:
			if(read.isComplete()){
				got = read.getActualLength();
				if (got > 0) {
					bytesToPacketBuffer.add(Arrays.copyOfRange(data, 0, got));
				}
				usbReadState = usbControlState.init;
			}
		default:
			break;	
		}


		return BowlerDatagramFactory
				.build(bytesToPacketBuffer);
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

	/* (non-Javadoc)
	 * @see javax.usb.event.UsbDeviceListener#dataEventOccurred(javax.usb.event.UsbDeviceDataEvent)
	 */
	@Override
	public void dataEventOccurred(UsbDeviceDataEvent arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.usb.event.UsbDeviceListener#errorEventOccurred(javax.usb.event.UsbDeviceErrorEvent)
	 */
	@Override
	public void errorEventOccurred(UsbDeviceErrorEvent arg0) {
		if(arg0.getUsbDevice() == mDevice){
			new RuntimeException("Disconnect in USB called").printStackTrace();
			disconnect();
			//connect() ;
		}
	}

	/* (non-Javadoc)
	 * @see javax.usb.event.UsbDeviceListener#usbDeviceDetached(javax.usb.event.UsbDeviceEvent)
	 */
	@Override
	public void usbDeviceDetached(UsbDeviceEvent arg0) {
		
		if(arg0.getUsbDevice() == mDevice){
			//new RuntimeException("Disconnect in USB called").printStackTrace();
			disconnect();
			//connect() ;
		}
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.javaxusb.IUsbDeviceEventListener#onDeviceEvent(javax.usb.UsbDevice)
	 */
	@Override
	public void onDeviceEvent(UsbDevice device) {
		// TODO Auto-generated method stub

	}

}
