package com.neuronrobotics.sdk.genericdevice;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.InvalidConnectionException;
import com.neuronrobotics.sdk.common.MACAddress;

// TODO: Auto-generated Javadoc
/**
 * This is a basic device with only bcs.core
 * @author hephaestus
 *
 */
public class GenericDevice extends BowlerAbstractDevice {
	
	/**
	 * Builds a DyIO with the given connection and the broadcast address.
	 *
	 * @param connection the connection
	 */
	public GenericDevice(BowlerAbstractConnection connection) {
		setAddress(new MACAddress(MACAddress.BROADCAST));
		setConnection(connection);
	}

	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractDevice#isAvailable()
	 */
	public boolean isAvailable() throws InvalidConnectionException {
		return getConnection().isConnected();
	}

	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractDevice#onAllResponse(com.neuronrobotics.sdk.common.BowlerDatagram)
	 */
	public void onAllResponse(BowlerDatagram data) {
		// TODO Auto-generated method stub

	}

	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.IBowlerDatagramListener#onAsyncResponse(com.neuronrobotics.sdk.common.BowlerDatagram)
	 */
	public void onAsyncResponse(BowlerDatagram data) {
		// TODO Auto-generated method stub

	}



}
