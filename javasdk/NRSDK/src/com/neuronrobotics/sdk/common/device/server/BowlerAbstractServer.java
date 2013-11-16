package com.neuronrobotics.sdk.common.device.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.neuronrobotics.sdk.commands.bcs.core.NamespaceCommand;
import com.neuronrobotics.sdk.commands.bcs.core.PingCommand;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerDataType;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerDatagramFactory;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.IConnectionEventListener;
import com.neuronrobotics.sdk.common.ISynchronousDatagramListener;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.MACAddress;
import com.neuronrobotics.sdk.common.RpcEncapsulation;
import com.neuronrobotics.sdk.common.device.server.bcs.core.BcsCoreNamespaceImp;
import com.neuronrobotics.sdk.common.device.server.bcs.rpc.BcsRpcNamespaceImp;
import com.neuronrobotics.sdk.network.BowlerTCPServer;
import com.neuronrobotics.sdk.network.BowlerUDPServer;

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
	
	ServerSocket serverSocket; 
	
	public void startNetworkServer() throws IOException{
		addServer(new BowlerUDPServer(1865));
		serverSocket = new ServerSocket(1866);
		new Thread(){
			public void run(){
				while(true){
					Socket s;
					try {
						Log.warning("\n\nWaiting for connection...");
						s = serverSocket.accept();
						addServer(new BowlerTCPServer(s));
						Log.warning("Got a connection!");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();

	}

	public void addServer(BowlerAbstractConnection srv) {
		if(!servers.contains(srv)){
			srv.addConnectionEventListener(new IConnectionEventListener() {
				@Override
				public void onDisconnect(BowlerAbstractConnection source) {
					removeServer(source);
				}
				@Override
				public void onConnect(BowlerAbstractConnection source) {
					
				}
			});
			servers.add(srv);
			srv.connect();
			srv.setSynchronousDatagramListener(this);
		}
	}
	
	@Override
	public BowlerDatagram onSyncReceive(BowlerDatagram data) {
		if(data.isUpstream()){
			// a server ignores upstream packets received
			
			return null;
		}
		if(!data.getRPC().contains("_png"))
			Log.debug("Got >> "+data);
		else{
			//Log.debug("Got >> ping");
		}
		BowlerDatagram bd = processLocal(data);
		if(bd != null){
			if(!data.getRPC().contains("_png"))
				Log.debug("Response << "+bd);
			else{
				//Log.debug("Response << ping");
			}
			return bd;
		}else{
			Log.error("Packet unknown"+data);
		}
		return null;
	}
	
	private void removeServer(BowlerAbstractConnection b){
		Log.error("Removing server");
		new RuntimeException().printStackTrace();
		getServers().remove(b);
	}

	public synchronized void pushAsyncPacket(BowlerDatagram data) {
		for(int i=0;i<servers.size();i++){
			try{
				boolean run = false;
				if(getServers().get(i).getClass() == BowlerTCPServer.class){
					BowlerTCPServer b =(BowlerTCPServer)getServers().get(i);
					if(b.isClientConnected()){
						run=true;
						Log.info("TCP Bowler client ...OK!");
					}else{
						Log.warning("TCP Bowler client not detected, dropping\n"+data);
						removeServer(b);
					}
				}else{
					run=true;
				}
				if(getServers().get(i).getClass() != BowlerUDPServer.class){
					//System.out.println("Sending packet to "+getServers().get(i).getClass());
					if(run && getServers().get(i).isConnected()){
						//Log.warning("ASYNC<<\r\n"+data );
						getServers().get(i).sendAsync(data);
						System.out.println("Sent packet to "+getServers().get(i).getClass());
					}else{
						BowlerAbstractConnection abs = getServers().get(i);
						removeServer(abs);
					}
				}
			}catch(Exception e){
				Log.error("No client connected to this connection "+getServers().get(i).getClass());
				BowlerAbstractConnection abs = getServers().get(i);
				abs.disconnect();
				removeServer(abs);
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
