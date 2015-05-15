package com.neuronrobotics.sdk.common;

import java.util.ArrayList;

public class NamespaceEncapsulation {
	
	private final String namespace;
	private ArrayList<RpcEncapsulation> rpcList= null;

	public NamespaceEncapsulation(String ns){
		namespace=ns;
	}

	public String getNamespace() {
		return namespace;
	}

	public ArrayList<RpcEncapsulation> getRpcList() {
		return rpcList;
	}

	public void setRpcList(ArrayList<RpcEncapsulation> rpcList) {
		this.rpcList = rpcList;
	}
	
	@Override
	public String toString(){
		String s=namespace+" Num RPC="+ rpcList.size();
		for(RpcEncapsulation rpc:getRpcList()){
			s+="\n\t"+rpc.toString();
		}
		return s;
	}
	
}
