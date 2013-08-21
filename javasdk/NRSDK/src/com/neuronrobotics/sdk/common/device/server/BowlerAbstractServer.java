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
import com.neuronrobotics.sdk.network.BowlerTCPServer;

public  abstract class BowlerAbstractServer  implements ISynchronousDatagramListener  {

	
	private ArrayList<BowlerAbstractConnection> servers = new ArrayList<BowlerAbstractConnection>();
	
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
		Log.error("No namespace found for "+data);
		return null;
	}
	
	public ArrayList<BowlerAbstractConnection> getServers() {
		return servers;
	}

	public void addServer(BowlerAbstractConnection srv) {
		if(!servers.contains(srv)){
			srv.connect();
			srv.addSynchronousDatagramListener(this);
			servers.add(srv);
		}
	}
	
	@Override
	public void onSyncReceive(BowlerDatagram data) {
		if(!data.getRPC().contains("_png"))
			Log.warning("Got >> "+data);
		BowlerDatagram bd = processLocal(data);
		if(bd != null){
			if(!data.getRPC().contains("_png"))
				Log.warning("Response << "+bd);
			pushAsyncPacket(bd);
		}else{
			Log.error("Packet unknown"+data);
		}
	}

	public void pushAsyncPacket(BowlerDatagram data) {
		for(int i=0;i<servers.size();i++){
			try{
				boolean run = false;
				if(getServers().get(i).getClass() == BowlerTCPServer.class){
					if(((BowlerTCPServer)getServers().get(i)).isClientConnected()){
						run=true;
						Log.info("TCP Bowler client ...OK!");
					}else{
						Log.warning("TCP Bowler client not detected, dropping\n"+data);
					}
				}else{
					run=true;
				}
				if(run && getServers().get(i).isConnected())
					getServers().get(i).sendAsync(data);
			}catch(Exception e){
				Log.error("No client connected to this connection "+getServers().get(i).getClass());
			}
		}
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
