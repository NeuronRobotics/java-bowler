package com.neuronrobotics.sdk.javaxusb;

import javax.usb.UsbDevice;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving IUsbDeviceEvent events.
 * The class that is interested in processing a IUsbDeviceEvent
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's  addIUsbDeviceEventListener  method. When
 * the IUsbDeviceEvent event occurs, that object's appropriate
 * method is invoked.
 *
 * @see UsbDevice
 */
public interface IUsbDeviceEventListener {
	
	/**
	 * On device event.
	 *
	 * @param device the device
	 */
	public void onDeviceEvent(UsbDevice device);
}
