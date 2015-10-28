package com.neuronrobotics.sdk.common;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class NamespaceEncapsulation.
 */
public class NamespaceEncapsulation {
	
	/** The namespace. */
	private final String namespace;
	
	/** The rpc list. */
	private ArrayList<RpcEncapsulation> rpcList= null;

	/**
	 * Instantiates a new namespace encapsulation.
	 *
	 * @param ns the ns
	 */
	public NamespaceEncapsulation(String ns){
		namespace=ns;
	}

	/**
	 * Gets the namespace.
	 *
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * Gets the rpc list.
	 *
	 * @return the rpc list
	 */
	public ArrayList<RpcEncapsulation> getRpcList() {
		return rpcList;
	}

	/**
	 * Sets the rpc list.
	 *
	 * @param rpcList the new rpc list
	 */
	public void setRpcList(ArrayList<RpcEncapsulation> rpcList) {
		this.rpcList = rpcList;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String s=namespace+" Num RPC="+ rpcList.size();
		for(RpcEncapsulation rpc:getRpcList()){
			s+="\n\t"+rpc.toString();
		}
		return s;
	}
	
}
