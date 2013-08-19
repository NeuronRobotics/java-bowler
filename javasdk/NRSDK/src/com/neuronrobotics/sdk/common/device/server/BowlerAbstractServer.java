package com.neuronrobotics.sdk.common.device.server;

import java.util.ArrayList;

import com.neuronrobotics.sdk.commands.bcs.core.NamespaceCommand;
import com.neuronrobotics.sdk.commands.bcs.core.PingCommand;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerDataType;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerDatagramFactory;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.ISynchronousDatagramListener;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.MACAddress;
import com.neuronrobotics.sdk.common.RpcEncapsulation;
import com.neuronrobotics.sdk.common.device.server.bcs.core.BcsCoreNamespaceImp;
import com.neuronrobotics.sdk.common.device.server.bcs.rpc.BcsRpcNamespaceImp;

public  abstract class BowlerAbstractServer  implements ISynchronousDatagramListener  {

	
	private BowlerAbstractConnection srv;
	
	private ArrayList<BowlerAbstractDeviceServerNamespace> namespaces = new ArrayList<BowlerAbstractDeviceServerNamespace>();
	
	private BcsCoreNamespaceImp bcsCore;
	private BcsRpcNamespaceImp  bcsRpc;

	private MACAddress macAddress;
	
	public BowlerAbstractServer(MACAddress mac){
		this.setMacAddress(mac);
		bcsCore = new BcsCoreNamespaceImp(this,mac);
		bcsRpc = 	new BcsRpcNamespaceImp(this,mac);
		setup();
	}
	
	private void setup(){
		if(!getNamespaces().contains(bcsCore )){
			getNamespaces().add(bcsCore );
			bcsCore.setNamespaceIndex(0);
		}
		if(!getNamespaces().contains(bcsRpc  )){
			bcsRpc.setNamespaceIndex(1);
			getNamespaces().add(bcsRpc );
		}
	}
	
	public void addBowlerDeviceServerNamespace(BowlerAbstractDeviceServerNamespace ns){
		setup();
		if(!getNamespaces().contains(ns)){
			for(int i=0;i<getNamespaces().size();i++){
				if(getNamespaces().get(i).getNamespace().contains(ns.getNamespace())){
					Log.error("Duplicate Namespace"+ns.getNamespace());
					return;
				}
			}
			ns.setNamespaceIndex(getNamespaces().size());
			getNamespaces().add(ns);
		}
	}
	public void removeBowlerDeviceServerNamespace(BowlerAbstractDeviceServerNamespace ns){
		setup();
		if(getNamespaces().contains(ns))
			getNamespaces().remove(ns);
	}
	
	private BowlerDatagram processLocal(BowlerDatagram data){
		setup();
		if(getNamespaces().size()==0){
			throw new RuntimeException("No namespaces defined");
		}
		for(BowlerAbstractDeviceServerNamespace n:getNamespaces()){
			//System.out.println("Checking "+n.getNamespaces().get(0));
			if (n.checkRpc(data)){
				BowlerDatagram d= n.process(data);
				if (d!=null){
					if(!d.getRPC().contains("_err"))
						return d;
				}
			}
		}
		System.err.println("No namespace found for "+data);
		return null;
	}
	
	public BowlerAbstractConnection getServer() {
		return srv;
	}

	public void setServer(BowlerAbstractConnection srv) {
		this.srv = srv;
		srv.connect();
		//pushAsyncPacket(BowlerDatagramFactory.build(getMacAddress(), new PingCommand()));
		srv.addSynchronousDatagramListener(this);
	}
	
	@Override
	public void onSyncReceive(BowlerDatagram data) {
		Log.info("Got >> "+data);
		BowlerDatagram bd = processLocal(data);
		if(bd != null){
			Log.info("Response << "+bd);
			pushAsyncPacket(bd);
		}else{
			Log.error("Packet unknown"+data);
		}
	}

	public void pushAsyncPacket(BowlerDatagram data) {
		getServer().sendAsync(data);
	}

	public ArrayList<BowlerAbstractDeviceServerNamespace> getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(ArrayList<BowlerAbstractDeviceServerNamespace> namespaces) {
		this.namespaces = namespaces;
	}

	public MACAddress getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(MACAddress macAddress) {
		this.macAddress = macAddress;
	}



}
