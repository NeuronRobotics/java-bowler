package com.neuronrobotics.sdk.common.device.server;

import java.util.ArrayList;

import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.BowlerDatagramFactory;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.MACAddress;
import com.neuronrobotics.sdk.common.RpcEncapsulation;

// TODO: Auto-generated Javadoc
/**
 * The Class BowlerAbstractDeviceServerNamespace.
 */
public abstract class BowlerAbstractDeviceServerNamespace {
	
	/** The rpc. */
	protected ArrayList<RpcEncapsulation> rpc=new ArrayList<RpcEncapsulation>();

	/** The ns. */
	protected final String  ns ;
	
	/** The mac. */
	private final MACAddress mac ;

	/** The namespace index. */
	private int namespaceIndex=0;
	
	/**
	 * Instantiates a new bowler abstract device server namespace.
	 *
	 * @param addr the addr
	 * @param namespaceString the namespace string
	 */
	public BowlerAbstractDeviceServerNamespace( MACAddress addr, String namespaceString){
		this.ns = namespaceString;
		this.mac = addr;
	}
	
	/**
	 * Check rpc.
	 *
	 * @param data the data
	 * @return true, if successful
	 */
	public boolean checkRpc(BowlerDatagram data){
		for(RpcEncapsulation enc: getRpcList()){
			if(data.getRPC().contains(enc.getRpc()) && enc.getDownstreamMethod() == data.getMethod()){
				return true;
			}
		}
		return false;
	}

	
	/**
	 * Gets the namespace.
	 *
	 * @return the namespace
	 */
	public String getNamespace() {
		return ns;
	}

	/**
	 * Gets the rpc list.
	 *
	 * @return the rpc list
	 */
	public ArrayList<RpcEncapsulation> getRpcList() {
		
		return rpc;
	}
	
	
	/**
	 * Adds the rpc.
	 *
	 * @param newRpc the new rpc
	 */
	public void addRpc(RpcEncapsulation newRpc) {
		rpc.add(newRpc);
	}
	
	/**
	 * Gets the address.
	 *
	 * @return the address
	 */
	public MACAddress getAddress() {
		return mac;
	}

	/**
	 * Process.
	 *
	 * @param data the data
	 * @return the bowler datagram
	 */
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
		
		Object [] backData = parser.getProcessor().process(dataParsed);
		
		BowlerAbstractCommand back = parser.getCommandUpstream(backData);
		
		return BowlerDatagramFactory.build(getAddress(), back);
		
	}

	/**
	 * Gets the namespace index.
	 *
	 * @return the namespace index
	 */
	public int getNamespaceIndex() {
		return namespaceIndex;
	}
	
	/**
	 * Sets the namespace index.
	 *
	 * @param ns the new namespace index
	 */
	public void setNamespaceIndex(int ns){
		namespaceIndex = ns;
	}
	
	
}
