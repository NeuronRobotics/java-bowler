package com.neuronrobotics.sdk.addons.kinematics.gcodebridge;

import java.util.ArrayList;

import com.neuronrobotics.sdk.common.NonBowlerDevice;

import gnu.io.NRSerialPort;

public class GcodeDevice extends NonBowlerDevice {
	
	private NRSerialPort serial;

	public GcodeDevice(NRSerialPort serial){
		this.serial = serial;
		
	}

	@Override
	public void disconnectDeviceImp() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean connectDeviceImp() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ArrayList<String> getNamespacesImp() {
		// TODO Auto-generated method stub
		return new ArrayList<String>();
	}

}
