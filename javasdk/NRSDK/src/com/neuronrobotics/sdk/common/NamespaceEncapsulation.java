package com.neuronrobotics.sdk.common;

import java.util.ArrayList;

public class NamespaceEncapsulation {
	
	private final String namespace;
	private ArrayList<RpcEncapsulation> rpcList= new ArrayList<RpcEncapsulation>();

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
}
