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
	private ArrayList<BowlerAbstractConnection> localServers = new ArrayList<BowlerAbstractConnection>();
	
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
					Log.warning("Removing server from listener");
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
			Log.error("Upstream packet detected"+data);
			return null;
		}
		if(!data.getRPC().contains("_png"))
			Log.debug("Server Got >> "+data);
		else{
			Log.info("Got >> ping");
		}
		BowlerDatagram bd = processLocal(data);
		if(bd != null){
			if(!data.getRPC().contains("_png"))
				Log.debug("Server Response << "+bd);
			else{
				Log.info("Response << ping");
			}
			return bd;
		}else{
			Log.error("Packet unknown"+data);
		}
		return null;
	}
	
	private void removeServer(BowlerAbstractConnection b){
		Log.error("Server Removed "+b);
		//new RuntimeException().printStackTrace();
		getServers().remove(b);
	}

	public synchronized void pushAsyncPacket(BowlerDatagram data) {
		localServers.clear();
		for(int i=0;i<servers.size();i++){
			localServers.add(getServers().get(i));
		}
		
		for(int i=0;i<servers.size();i++){
			try{
				boolean run = false;
				if(localServers.get(i).getClass() == BowlerTCPServer.class){
					BowlerTCPServer b =(BowlerTCPServer)localServers.get(i);
					if(b.isClientConnected()){
						run=true;
						Log.info("TCP Bowler client ...OK!");
					}
				}else{
					run=true;
				}
				if(localServers.get(i).getClass() != BowlerUDPServer.class ){
					//System.out.println("Sending packet to "+getServers().get(i).getClass());
					if(run && localServers.get(i).isConnected()){
						//Log.warning("ASYNC<<\r\n"+data );
						String classString = localServers.get(i).getClass().toString();
						localServers.get(i).sendAsync(data);
						Log.debug("Sent packet to "+classString);
					}
				}
			}catch (IndexOutOfBoundsException ie){
				ie.printStackTrace();
			}catch(Exception e){
				try{
					e.printStackTrace();
					BowlerAbstractConnection abs = localServers.get(i);
					Log.error("No client connected to this connection "+abs.getClass());
					abs.disconnect();

				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
		localServers.clear();
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
