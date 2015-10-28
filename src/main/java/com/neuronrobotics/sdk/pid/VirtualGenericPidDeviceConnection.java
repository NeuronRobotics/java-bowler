package com.neuronrobotics.sdk.pid;

import java.io.IOException;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;

// TODO: Auto-generated Javadoc
/**
 * The Class VirtualGenericPidDeviceConnection.
 */
public class VirtualGenericPidDeviceConnection extends BowlerAbstractConnection {

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#connect()
	 */
	@Override
	public boolean connect() {
		// TODO Auto-generated method stub
		return true;
	}

//	@Override
//	public boolean reconnect() throws IOException {
//		// TODO Auto-generated method stub
//		return false;
//	}

	/* (non-Javadoc)
 * @see com.neuronrobotics.sdk.common.BowlerAbstractConnection#waitingForConnection()
 */
@Override
	public boolean waitingForConnection() {
		// TODO Auto-generated method stub
		return false;
	}

}
