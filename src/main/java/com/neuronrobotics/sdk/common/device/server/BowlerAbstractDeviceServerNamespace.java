package com.neuronrobotics.sdk.common.device.server;

import java.util.ArrayList;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerDatagramFactory;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.MACAddress;
import com.neuronrobotics.sdk.common.RpcEncapsulation;

public abstract class BowlerAbstractDeviceServerNamespace {
	
	protected ArrayList<RpcEncapsulation> rpc=new ArrayList<RpcEncapsulation>();

	protected final String  ns ;
	private final MACAddress mac ;

	private int namespaceIndex=0;
	
	public BowlerAbstractDeviceServerNamespace( MACAddress addr, String namespaceString){
		this.ns = namespaceString;
		this.mac = addr;
	}
	
	public boolean checkRpc(BowlerDatagram data){
		for(RpcEncapsulation enc: getRpcList()){
			if(data.getRPC().contains(enc.getRpc()) && enc.getDownstreamMethod() == data.getMethod()){
				return true;
			}
		}
		return false;
	}

	
	public String getNamespace() {
		return ns;
	}

	public ArrayList<RpcEncapsulation> getRpcList() {
		
		return rpc;
	}

	public MACAddress getAddress() {
		return mac;
	}

	public BowlerDatagram process(BowlerDatagram data) {
		Object [] dataParsed=null;
		RpcEncapsulation parser=null;
		for(RpcEncapsulation enc:	getRpcList()){
			if(enc.getRpc().contains(data.getRPC())&& enc.getDownstreamMethod() == data.getMethod()){
				parser = enc;
			}
		}
		if(parser == null)
			return null;
		dataParsed = parser.parseResponseDownstream(data);
		
		Object [] backData = parser.getProcessor().process(dataParsed, data.getRPC(), data.getMethod());
		
		BowlerAbstractCommand back = parser.getCommandUpstream(backData);
		
		return BowlerDatagramFactory.build(getAddress(), back);
		
	}

	public int getNamespaceIndex() {
		return namespaceIndex;
	}
	
	public void setNamespaceIndex(int ns){
		namespaceIndex = ns;
	}
	
	
}
