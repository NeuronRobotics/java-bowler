package com.neuronrobotics.sdk.common.device.server.bcs.rpc;

import com.neuronrobotics.sdk.common.BowlerDataType;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.MACAddress;
import com.neuronrobotics.sdk.common.RpcEncapsulation;
import com.neuronrobotics.sdk.common.device.server.BowlerAbstractDeviceServerNamespace;
import com.neuronrobotics.sdk.common.device.server.BowlerAbstractServer;
import com.neuronrobotics.sdk.common.device.server.IBowlerCommandProcessor;

// TODO: Auto-generated Javadoc
/**
 * The Class BcsRpcNamespaceImp.
 */
public class BcsRpcNamespaceImp extends BowlerAbstractDeviceServerNamespace{
	
	/** The server. */
	private BowlerAbstractServer server;
	
	/**
	 * Instantiates a new bcs rpc namespace imp.
	 *
	 * @param s the s
	 * @param mac the mac
	 */
	public BcsRpcNamespaceImp(BowlerAbstractServer s,MACAddress mac){
		super( mac,"bcs.rpc.*;;");
		this.server = s;
		
		rpc.add(new RpcEncapsulation(1, 
				getNamespace() , 
				"_rpc", 
				BowlerMethod.GET, 
				new BowlerDataType[]{	BowlerDataType.I08,//namespace index
										BowlerDataType.I08}, //rpc index
				BowlerMethod.POST, 
				new BowlerDataType[]{	BowlerDataType.I08,//namespace index
										BowlerDataType.I08,//rpc index
										BowlerDataType.I08,// number of RPC's
										BowlerDataType.ASCII},
										new IBowlerCommandProcessor() {
											
											@Override
											public Object[] process(Object[] data) {
												int nsIndex = 	(Integer) data[0];
												int rpcIndex = 	(Integer) data[1];
												Object[] back = new Object[4];
												
												back[0] = new Integer(nsIndex);
												back[1] = new Integer(rpcIndex);
												
												back[2] = new Integer(server.getNamespaces().get(nsIndex).getRpcList().size());
												back[3] = server.getNamespaces().get(nsIndex).getRpcList().get(rpcIndex).getRpc();
												return back;
											}
										}));//RPC
		
		rpc.add(new RpcEncapsulation(1, 
				getNamespace() , 
				"args", 
				BowlerMethod.GET, 
				new BowlerDataType[]{	BowlerDataType.I08,//namespace index
										BowlerDataType.I08}, //rpc index
				BowlerMethod.POST, 
				new BowlerDataType[]{	BowlerDataType.I08,//namespace index
										BowlerDataType.I08,//rpc index
										BowlerDataType.I08,//Downstream method
										BowlerDataType.STR,//downstream arguments
										BowlerDataType.I08,//upstream method
										BowlerDataType.STR},
										new IBowlerCommandProcessor() {
			
											@Override
											public Object[] process(Object[] data) {
												int nsIndex = 	(Integer) data[0];
												int rpcIndex = 	(Integer) data[1];
												Object[] back = new Object[6];
												RpcEncapsulation myRpc = server.getNamespaces().get(nsIndex).getRpcList().get(rpcIndex );
												
												back[0] = new Integer(nsIndex);
												back[1] = new Integer(rpcIndex);
												
												back[2] = new Integer(myRpc.getDownstreamMethod().getValue());
												back[3] = new ByteList(myRpc.getDownstreamArguments());
												back[4] = new Integer(myRpc.getUpStreamMethod().getValue());
												back[5] = new ByteList( myRpc.getUpstreamArguments());
												return back;
											}
										}));//upstream arguments
		
	}


}
