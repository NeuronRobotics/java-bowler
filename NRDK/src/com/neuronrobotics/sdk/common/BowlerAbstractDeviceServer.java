package com.neuronrobotics.sdk.common;

import java.io.IOException;
import java.util.ArrayList;

import com.neuronrobotics.sdk.commands.bcs.core.NamespaceCommand;
import com.neuronrobotics.sdk.commands.bcs.core.PingCommand;


public abstract class BowlerAbstractDeviceServer extends BowlerAbstractDevice {
	private String core = "bcs.core.*;0.3;;";
	private ArrayList<String> namespaces = new  ArrayList<String>();
	@Override
	public boolean connect(){
		super.connect();
		addNamespace(core);
		return isAvailable();
	}
	public void addNamespace(String nms){
		if(!namespaces.contains(nms))
			namespaces.add(nms);
	}
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
					sendSyncResponse(new NamespaceCommand(namespaces.get(index)));	
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

	public void onAsyncResponse(BowlerDatagram data) {
		// TODO Auto-generated method stub

	}
	/**
	 * Send a sendable to the getConnection().
	 *
	 * @param sendable the sendable without expecting a response
	 * @throws IOException 
	 */
	public void sendAsync(BowlerAbstractCommand command, int asyncID) throws IOException {
		BowlerDatagram bd = BowlerDatagramFactory.build(getAddress(), command,asyncID);
		//Log.debug("ASYN>>\n"+bd.toString());
		getConnection().sendAsync(bd);
		getConnection().getDataOuts().flush();
	}
	/**
	 * Send a sendable to the getConnection().
	 *
	 * @param sendable the sendable without expecting a response
	 * @throws IOException 
	 */
	public void sendSyncResponse(BowlerAbstractCommand command) throws IOException {
		BowlerDatagram bd =BowlerDatagramFactory.build(getAddress(), command,0);
		//Log.debug("RESP>>\n"+bd.toString());
		getConnection().sendAsync(bd);
		getConnection().getDataOuts().flush();
	}
	public void sendPacketWithNoResponse(BowlerDatagram data) throws IOException {
		//Log.debug("RESP>>\n"+bd.toString());
		getConnection().sendAsync(data);
		getConnection().getDataOuts().flush();
	}
	public abstract void onSynchronusRecive(BowlerDatagram data);
}
