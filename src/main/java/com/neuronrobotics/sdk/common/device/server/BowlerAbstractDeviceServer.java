package com.neuronrobotics.sdk.common.device.server;

import java.io.IOException;
import java.util.ArrayList;

import com.neuronrobotics.sdk.commands.bcs.core.NamespaceCommand;
import com.neuronrobotics.sdk.commands.bcs.core.PingCommand;
import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerDatagramFactory;
import com.neuronrobotics.sdk.common.Log;


// TODO: Auto-generated Javadoc
/**
 * The Class BowlerAbstractDeviceServer.
 */
public abstract class BowlerAbstractDeviceServer extends BowlerAbstractDevice {
	
	/** The core. */
	private String core = "bcs.core.*;0.3;;";
	
	/** The namespaces. */
	private ArrayList<String> namespaces = new  ArrayList<String>();
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractDevice#connect()
	 */
	@Override
	public boolean connect(){
		super.connect();
		addNamespace(core);
		return isAvailable();
	}
	
	/**
	 * Adds the namespace.
	 *
	 * @param nms the nms
	 */
	public void addNamespace(String nms){
		if(!namespaces.contains(nms))
			namespaces.add(nms);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractDevice#onAllResponse(com.neuronrobotics.sdk.common.BowlerDatagram)
	 */
	public void onAllResponse(BowlerDatagram data) {
		String rpc = data.getRPC();
		if(rpc.contains("_nms")) {
			Log.info("Got a namespace packet of size: "+data.getData().size());
			if(data.getData().size() == 0) {
				try {
					sendSyncResponse(new NamespaceCommand(namespaces.size(),true));
				} catch (IOException e) {
					e.printStackTrace();
				}
				return;
			}
			if(data.getData().size() == 1) {
				int index = data.getData().get(0);
				try {
					sendSyncResponse(new NamespaceCommand(namespaces.size(),namespaces.get(index)));	
				} catch (IOException e) {
					e.printStackTrace();
				}
				return;
			}
		}else if(rpc.contains("_png")){
			//Log.info("Got ping");
			try {
				sendSyncResponse(new PingCommand());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}else
			onSynchronusRecive(data);

	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.IBowlerDatagramListener#onAsyncResponse(com.neuronrobotics.sdk.common.BowlerDatagram)
	 */
	public void onAsyncResponse(BowlerDatagram data) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Send a sendable to the getConnection().
	 *
	 * @param command the command
	 * @param rpcIndexID the rpc index id
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void sendAsync(BowlerAbstractCommand command, int rpcIndexID) throws IOException {
		command.setNamespaceIndex(rpcIndexID);
		BowlerDatagram bd = BowlerDatagramFactory.build(getAddress(), command);
		//Log.debug("ASYN>>\n"+bd.toString());
		getConnection().sendAsync(bd);
		getConnection().getDataOuts().flush();
	}
	
	/**
	 * Send a sendable to the getConnection().
	 *
	 * @param command the command
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void sendSyncResponse(BowlerAbstractCommand command) throws IOException {
		BowlerDatagram bd =BowlerDatagramFactory.build(getAddress(), command);
		//Log.debug("RESP>>\n"+bd.toString());
		getConnection().sendAsync(bd);
		getConnection().getDataOuts().flush();
	}
	
	/**
	 * Send packet with no response.
	 *
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void sendPacketWithNoResponse(BowlerDatagram data) throws IOException {
		//Log.debug("RESP>>\n"+bd.toString());
		getConnection().sendAsync(data);
		getConnection().getDataOuts().flush();
	}
	
	/**
	 * On synchronus recive.
	 *
	 * @param data the data
	 */
	public abstract void onSynchronusRecive(BowlerDatagram data);
}
