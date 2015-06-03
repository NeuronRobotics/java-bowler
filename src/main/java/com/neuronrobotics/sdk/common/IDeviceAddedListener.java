package com.neuronrobotics.sdk.common;


public interface IDeviceAddedListener {
	public void onNewDeviceAdded(BowlerAbstractDevice bad);
	public void onDeviceRemoved(BowlerAbstractDevice bad);
}
