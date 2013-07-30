package com.neuronrobotics.sdk.common.device.server;

import java.util.ArrayList;

import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.RpcEncapsulation;

public abstract class BowlerAbstractDeviceServerNamespace {
	
	protected ArrayList<RpcEncapsulation> rpc=new ArrayList<RpcEncapsulation>();

	protected String  ns =new String("test.fail.*");
	
	public boolean checkRpc(BowlerDatagram data){
		for(RpcEncapsulation enc: getRpcList()){
			if(data.getRPC().contains(enc.getRpc()) && enc.getDownstreamMethod() == data.getMethod()){
				return true;
			}
		}
		return false;
	}
	protected void setNamespace(String namespaceString) {
		ns = namespaceString;
	}
	
	public String getNamespace() {
		return ns;
	}

	public ArrayList<RpcEncapsulation> getRpcList() {
		
		return rpc;
	}

	
	public abstract BowlerDatagram process(BowlerDatagram data);
	
}
