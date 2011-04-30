package com.neuronrobotics.sdk.genericdevice;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.InvalidConnectionException;
import com.neuronrobotics.sdk.common.MACAddress;

/**
 * This is a basic device with only bcs.core
 * @author hephaestus
 *
 */
public class GenericDevice extends BowlerAbstractDevice {
	/**
	 * Builds a DyIO with the given connection and the broadcast address.
	 * @param connection
	 */
	public GenericDevice(BowlerAbstractConnection connection) {
		setAddress(new MACAddress(MACAddress.BROADCAST));
		setConnection(connection);
	}

	
	public boolean isAvailable() throws InvalidConnectionException {
		return getConnection().isConnected();
	}

	
	public void onAllResponse(BowlerDatagram data) {
		// TODO Auto-generated method stub

	}

	
	public void onAsyncResponse(BowlerDatagram data) {
		// TODO Auto-generated method stub

	}

}
