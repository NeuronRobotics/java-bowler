package com.neuronrobotics.sdk.common.device.server;

import java.util.ArrayList;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.IBowlerDatagramListener;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.RpcEncapsulation;
import com.neuronrobotics.sdk.genericdevice.GenericDevice;

// TODO: Auto-generated Javadoc
/**
 * The Class BowlerDeviceReServerNamespace.
 */
public class BowlerDeviceReServerNamespace extends BowlerAbstractDeviceServerNamespace{
	
	/** The device. */
	private BowlerAbstractConnection device;
	
	/** The server. */
	private BowlerAbstractServer server;

	/**
	 * Instantiates a new bowler device re server namespace.
	 *
	 * @param device the device
	 * @param server the server
	 * @param useAsync the use async
	 * @param namespaceIndex the namespace index
	 * @param namespaceString the namespace string
	 * @param gen the gen
	 */
	public BowlerDeviceReServerNamespace(	BowlerAbstractConnection device,
											BowlerAbstractServer server, 
											boolean useAsync, 
											int namespaceIndex, 
											String namespaceString,
											GenericDevice gen){
		super( gen.getAddress() ,namespaceString);
		this.device = device;
		this.setServer(server);
		ArrayList<RpcEncapsulation> rpcEnc= gen.getRpcList(namespaceString);
		for (RpcEncapsulation r:rpcEnc){
			Log.info("Adding Namespace "+r.getNamespace());
			getRpcList().add(r);
		}
		if(useAsync){
			device.addDatagramListener(new IBowlerDatagramListener() {
				@Override
				public void onAsyncResponse(BowlerDatagram data) {
					getServer().pushAsyncPacket(data);
				}
			});
		}
		
	}
	
	/**
	 * Gets the server.
	 *
	 * @return the server
	 */
	public BowlerAbstractServer getServer() {
		return server;
	}

	/**
	 * Sets the server.
	 *
	 * @param server the new server
	 */
	public void setServer(BowlerAbstractServer server) {
		this.server = server;
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.device.server.BowlerAbstractDeviceServerNamespace#process(com.neuronrobotics.sdk.common.BowlerDatagram)
	 */
	@Override
	public BowlerDatagram process(BowlerDatagram data){
		BowlerDatagram bd = device.sendSynchronusly(data);
		return bd;
	}

}
