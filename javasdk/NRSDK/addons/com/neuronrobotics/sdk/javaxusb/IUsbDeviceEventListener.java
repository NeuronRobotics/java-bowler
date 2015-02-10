package com.neuronrobotics.sdk.javaxusb;

import javax.usb.UsbDevice;

public interface IUsbDeviceEventListener {
	public void onDeviceEvent(UsbDevice device);
}
