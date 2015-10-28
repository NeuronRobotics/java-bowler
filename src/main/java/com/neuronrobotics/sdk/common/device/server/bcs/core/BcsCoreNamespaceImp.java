package com.neuronrobotics.sdk.common.device.server.bcs.core;

import com.neuronrobotics.sdk.common.BowlerDataType;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.MACAddress;
import com.neuronrobotics.sdk.common.RpcEncapsulation;
import com.neuronrobotics.sdk.common.device.server.BowlerAbstractDeviceServerNamespace;
import com.neuronrobotics.sdk.common.device.server.BowlerAbstractServer;
import com.neuronrobotics.sdk.common.device.server.IBowlerCommandProcessor;

// TODO: Auto-generated Javadoc
/**
 * The Class BcsCoreNamespaceImp.
 */
public class BcsCoreNamespaceImp extends BowlerAbstractDeviceServerNamespace{

	
	/** The server. */
	private BowlerAbstractServer server;

	/**
	 * Instantiates a new bcs core namespace imp.
	 *
	 * @param s the s
	 * @param mac the mac
	 */
	public BcsCoreNamespaceImp(final BowlerAbstractServer s, MACAddress mac){
		super( mac ,"bcs.core.*;;");
		this.server = s;
		rpc.add(new RpcEncapsulation(0, 
				getNamespace() , 
				"_png", 
				BowlerMethod.GET, 
				new BowlerDataType[]{}, 
				BowlerMethod.POST, 
				new BowlerDataType[]{},
				new IBowlerCommandProcessor() {
					@Override
					public Object[] process(Object[] data) {
						Object[] back = new Object[0];
						return back;
					}
				}));
		
		rpc.add(new RpcEncapsulation(0, 
				getNamespace() , 
				"_nms", 
				BowlerMethod.GET, 
				new BowlerDataType[]{BowlerDataType.I08}, 
				BowlerMethod.POST, 
				new BowlerDataType[]{BowlerDataType.ASCII,BowlerDataType.I08},
				new IBowlerCommandProcessor() {
					@Override
					public Object[] process(Object[] data) {
						int nsIndex = 	(Integer) data[0];
						Object[] back = new Object[2];
						
						back[0] = server.getNamespaces().get(nsIndex).getNamespace();
						back[1] = new Integer(server.getNamespaces().size());
		
						return back;
					}
				}));
	}


}
