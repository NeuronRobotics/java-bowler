package com.neuronrobotics.sdk.pid;

import java.io.IOException;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;

public class VirtualGenericPidDeviceConnection extends BowlerAbstractConnection {

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

	@Override
	public boolean waitingForConnection() {
		// TODO Auto-generated method stub
		return false;
	}

}
