package com.neuronrobotics.sdk.common;

import java.util.ArrayList;

import com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace;

public abstract class  NonBowlerDevice extends BowlerAbstractDevice {
	/**
	 * This method tells the connection object to disconnect its pipes and close out the connection. Once this is called, it is safe to remove your device.
	 */
	
	public abstract void disconnectDeviceImp();
	
	public abstract  boolean connectDeviceImp();
	public abstract  ArrayList<String>  getNamespacesImp();
	
	@Override
	public boolean connect(){
		fireConnectEvent();
		return connectDeviceImp();
	}
	
	/**
	 * Determines if the device is available.
	 *
	 * @return true if the device is avaiable, false if it is not
	 * @throws InvalidConnectionException the invalid connection exception
	 */
	@Override
	public boolean isAvailable() throws InvalidConnectionException{
		return true;
	}
	
	/**
	 * This method tells the connection object to disconnect its pipes and close out the connection. Once this is called, it is safe to remove your device.
	 */
	@Override
	public void disconnect(){
		fireDisconnectEvent();
		disconnectDeviceImp();
		
	}
	
	@Override
	public void onAsyncResponse(BowlerDatagram data) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Get all the namespaces.
	 *
	 * @return the namespaces
	 */
	@Override
	public ArrayList<String>  getNamespaces(){
		return getNamespacesImp();	
	}
}
