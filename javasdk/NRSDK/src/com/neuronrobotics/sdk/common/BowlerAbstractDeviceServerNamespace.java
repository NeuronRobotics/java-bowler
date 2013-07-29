package com.neuronrobotics.sdk.common;

import java.util.ArrayList;

public abstract class BowlerAbstractDeviceServerNamespace {
	
	public boolean checkRpc(BowlerDatagram data){
		for(String nsStr: getNamespaces()){
			if(getRpcList(nsStr).size()==0){
				return true;// The device has no RPC reporting
			}
			for(RpcEncapsulation enc: getRpcList(nsStr)){
				if(enc.getRpc() == data.getRPC() && enc.getDownstreamMethod() == data.getMethod()){
					return true;
				}
			}
		}
		return false;
	}
	
	public abstract BowlerDatagram process(BowlerDatagram data);
	
	public abstract ArrayList<String>  getNamespaces();
	
	public abstract ArrayList<RpcEncapsulation> getRpcList(String namespace);
	
}
