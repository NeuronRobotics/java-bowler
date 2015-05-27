package com.neuronrobotics.sdk.namespace.bcs.pid;

import java.io.IOException;

import com.neuronrobotics.sdk.common.BowlerDataType;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.MACAddress;
import com.neuronrobotics.sdk.common.device.server.BowlerAbstractServer;
import com.neuronrobotics.sdk.pid.IPIDEventListener;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;
import com.neuronrobotics.sdk.pid.VirtualGenericPIDDevice;

public class PidDeviceServer extends BowlerAbstractServer implements IPIDEventListener {

	private PidDeviceServerNamespace pidServer;

	public PidDeviceServer(MACAddress mac,IPidControlNamespace device) {
		super(mac);
		pidServer = new PidDeviceServerNamespace(mac, device);
		addBowlerDeviceServerNamespace(pidServer);
		
		device.addPIDEventListener(this);
		Log.info("Starting UDP");
		try {
			startNetworkServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void main(String [] args){
		Log.enableInfoPrint();
		PidDeviceServer srv = new PidDeviceServer(new MACAddress(), new VirtualGenericPIDDevice(10000) );
		
	}

	@Override
	public void onPIDEvent(PIDEvent e) {
		Log.info("Pushing "+e);
		pushAsyncPacket(2,//0 is core, 1 is rpc 
				pidServer.getNamespace(), 
				"_pid", 
				new Object[]{
					new Byte((byte) e.getGroup()),
					new Integer(e.getValue()),
					new Integer((int) e.getTimeStamp()),
					new Integer(e.getVelocity())
				}, 
				new BowlerDataType[]{
					BowlerDataType.I08,//channel
					BowlerDataType.I32,//position
					BowlerDataType.I32,//timestamp
					BowlerDataType.I32//velocity
				} );
	}

	@Override
	public void onPIDLimitEvent(PIDLimitEvent e) {
		pushAsyncPacket(2,//0 is core, 1 is rpc 
				pidServer.getNamespace(), 
				"pidl", 
				new Object[]{
					new Byte((byte) e.getGroup()),
					new Byte( e.getLimitType().getValue()),
					new Integer(e.getValue()),
					new Integer((int) e.getTimeStamp()),
				}, 
				new BowlerDataType[]{
					BowlerDataType.I08,//channel
					BowlerDataType.I08,//type
					BowlerDataType.I32,//position
					BowlerDataType.I32,//timestamp
				} );
	}

	@Override
	public void onPIDReset(int group, int currentValue) {}//used for object state not commands

}
