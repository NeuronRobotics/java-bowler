package com.neuronrobotics.sdk.common;

import com.neuronrobotics.sdk.common.device.server.IBowlerCommandProcessor;

// TODO: Auto-generated Javadoc
/**
 * The Class RpcEncapsulation.
 */
public class RpcEncapsulation {

	/** The namespace. */
	private String namespace;
	
	/** The rpc. */
	private String rpc;
	
	/** The method. */
	private BowlerMethod method;
	
	/** The downstream arguments. */
	private BowlerDataType[] downstreamArguments;
	
	/** The upstream arguments. */
	private BowlerDataType[] upstreamArguments;
	
	/** The up stream method. */
	private BowlerMethod upStreamMethod;
	
	/** The namespace index. */
	private int namespaceIndex;
	
	/** The processor. */
	private IBowlerCommandProcessor processor;
	
	/**
	 * This is an encapsulation object for a given RPC.
	 *
	 * @param namespaceIndex the namespace index
	 * @param namespace 			The corosponding Namespace
	 * @param rpc 				The 4 byte RPC code
	 * @param downStreamMethod 	The method for sending messages
	 * @param downstreamArguments The array of data types for a downstream message
	 * @param upStreamMethod 	The return method type
	 * @param upstreamArguments 	THe return method arguments
	 */
	public RpcEncapsulation(int namespaceIndex,String namespace, String rpc, 
			BowlerMethod downStreamMethod,BowlerDataType[] downstreamArguments, 
			BowlerMethod upStreamMethod,BowlerDataType[] upstreamArguments){
		this(namespaceIndex, namespace, rpc, downStreamMethod, downstreamArguments, upStreamMethod, upstreamArguments, null);
	}
	
	/**
	 * This is an encapsulation object for a given RPC.
	 *
	 * @param namespaceIndex the namespace index
	 * @param namespace 			The corosponding Namespace
	 * @param rpc 				The 4 byte RPC code
	 * @param downStreamMethod 	The method for sending messages
	 * @param downstreamArguments The array of data types for a downstream message
	 * @param upStreamMethod 	The return method type
	 * @param upstreamArguments 	THe return method arguments
	 * @param processor the processor
	 */
	public RpcEncapsulation(int namespaceIndex,String namespace, String rpc, 
			BowlerMethod downStreamMethod,BowlerDataType[] downstreamArguments, 
			BowlerMethod upStreamMethod,BowlerDataType[] upstreamArguments, IBowlerCommandProcessor processor){
		this.setProcessor(processor);
		this.setNamespaceIndex(namespaceIndex);
		this.setNamespace(namespace);
		this.setRpc(rpc);
		setArguments( downStreamMethod,downstreamArguments, upStreamMethod, upstreamArguments);
	}
	
	/**
	 * Sets the arguments.
	 *
	 * @param downStreamMethod the down stream method
	 * @param downstreamArguments the downstream arguments
	 * @param upStreamMethod the up stream method
	 * @param upstreamArguments the upstream arguments
	 */
	public void setArguments(BowlerMethod downStreamMethod,BowlerDataType[] downstreamArguments, BowlerMethod upStreamMethod,BowlerDataType[] upstreamArguments){
		this.setUpStreamMethod(upStreamMethod);
		this.setDownstreamArguments(downstreamArguments);
		this.setUpstreamArguments(upstreamArguments);
		this.setDownStreamMethod(downStreamMethod);
	}
	
	/**
	 * Gets the command.
	 *
	 * @param doswnstreamData the doswnstream data
	 * @return the command
	 */
	public BowlerAbstractCommand getCommand(Object [] doswnstreamData){
		return getCommand(doswnstreamData, downstreamArguments);
	}
	
	/**
	 * Gets the command upstream.
	 *
	 * @param doswnstreamData the doswnstream data
	 * @return the command upstream
	 */
	public BowlerAbstractCommand getCommandUpstream(Object [] doswnstreamData){
		return getCommand(doswnstreamData, upstreamArguments);
	}
	
	
	/**
	 * Gets the command.
	 *
	 * @param doswnstreamData the doswnstream data
	 * @param arguments the arguments
	 * @return the command
	 */
	public BowlerAbstractCommand getCommand(Object [] doswnstreamData, BowlerDataType [] arguments){
		BowlerAbstractCommand command = new BowlerAbstractCommand() {};
		
		command.setOpCode(getRpc());
		command.setMethod(getDownstreamMethod());
		command.setNamespaceIndex(getNamespaceIndex());
		
		for(int i=0;(i<arguments.length && i < doswnstreamData.length);i++ ){
			try{
				switch(arguments[i]){
				case ASCII:
					command.getCallingDataStorage().add(doswnstreamData[i].toString());
					command.getCallingDataStorage().add(0);
					break;
				case FIXED100:
					double d = Double.parseDouble(doswnstreamData[i].toString())*100;
					command.getCallingDataStorage().addAs32((int)d);
					break;
				case FIXED1k:
					double k = Double.parseDouble(doswnstreamData[i].toString())*1000;
					command.getCallingDataStorage().addAs32((int)k);
					break;
				case I08:
					command.getCallingDataStorage().add(Integer.parseInt(doswnstreamData[i].toString()));
					break;
				case BOOL:
					command.getCallingDataStorage().add(Boolean.parseBoolean((doswnstreamData[i].toString()))?1:0);
					break;
				case I16:
					command.getCallingDataStorage().addAs16(Integer.parseInt(doswnstreamData[i].toString()));
					break;
				case I32:
					command.getCallingDataStorage().addAs32(Integer.parseInt(doswnstreamData[i].toString()));
					break;
				case I32STR:
					try{
						Integer [] data32 = (Integer [])doswnstreamData[i];
						command.getCallingDataStorage().add(data32.length);
						for(int i1=0;i1<data32.length;i1++){
							command.getCallingDataStorage().addAs32(data32[i1]);
						}
					}catch (ClassCastException ex){
						int [] data32 = (int [])doswnstreamData[i];
						command.getCallingDataStorage().add(data32.length);
						for(int i1=0;i1<data32.length;i1++){
							command.getCallingDataStorage().addAs32(data32[i1]);
						}
					}
					break;
				case FIXED1k_STR:
					double [] dataDouble = (double [])doswnstreamData[i];
					command.getCallingDataStorage().add(dataDouble.length);
					for(int i1=0;i1<dataDouble.length;i1++){
						command.getCallingDataStorage().addAs32((int) (dataDouble[i1]*1000.0));
					}
					break;
				case INVALID:
					break;
				case STR:
					try{
						ByteList data = (ByteList )doswnstreamData[i];
						command.getCallingDataStorage().add(data.size());
						for(int i1=0;i1<data.size();i1++){
							command.getCallingDataStorage().add(data.get(i1));
						}
					}catch (ClassCastException ex){
						try{
							Integer [] data32 = (Integer [])doswnstreamData[i];
							command.getCallingDataStorage().add(data32.length);
							for(int i1=0;i1<data32.length;i1++){
								command.getCallingDataStorage().addAs32(data32[i1]);
							}
						}catch (ClassCastException ex1){
							int [] data32 = (int [])doswnstreamData[i];
							command.getCallingDataStorage().add(data32.length);
							for(int i1=0;i1<data32.length;i1++){
								command.getCallingDataStorage().addAs32(data32[i1]);
							}
						}
					}
					break;
				default:
					throw new RuntimeException("Unrecognized data type "+arguments[i]);
				}
			}catch(Exception e){
				e.printStackTrace();
				Log.error("Expected : "+ arguments[i]+" got: "+doswnstreamData[i].getClass());
				if(arguments.length != doswnstreamData.length){
					Log.error("Wrong size : "+ arguments.length+" got: "+doswnstreamData.length);
				}else{
					for(int j=0;j<arguments.length;j++){
						Log.error("Valid : "+ arguments[j]+" got: "+doswnstreamData[i].getClass());
					}
				}
				
			}

		}
		
		return command;
	}
	
	/**
	 * Parses the response.
	 *
	 * @param datagram the datagram
	 * @return the object[]
	 */
	public Object [] parseResponse(BowlerDatagram datagram){
		return parseResponse(datagram, upstreamArguments);
	}
	
	/**
	 * Parses the response downstream.
	 *
	 * @param datagram the datagram
	 * @return the object[]
	 */
	public Object [] parseResponseDownstream(BowlerDatagram datagram){
		return parseResponse(datagram, downstreamArguments);
	}
	
	/**
	 * Parses the response.
	 *
	 * @param datagram the datagram
	 * @param arguments the arguments
	 * @return the object[]
	 */
	public Object [] parseResponse(BowlerDatagram datagram, BowlerDataType [] arguments){
		Object [] response = new Object[arguments.length];
		int i=0;
		try{
			int numVals32;
			ByteList data = datagram.getData();
			for(i=0;(i<arguments.length);i++ ){
				
				switch(arguments[i]){
				case ASCII:
					String s = data.asString();
					data.popList(s.length()+1);
					response [i] = s;
					break;
				case FIXED100:
					response [i] = new Double(ByteList.convertToInt(data.popList(4)))/100.0;
					break;
				case FIXED1k:
					response [i] = new Double(ByteList.convertToInt(data.popList(4)))/1000.0;
					break;
				case I08:
					response [i] = new Integer(data.getUnsigned(0));
					data.pop();
					break;
				case BOOL:
					response [i] = new Boolean(data.getUnsigned(0)!=0);
					data.pop();
					break;
				case I16:
					response [i] = new Integer(ByteList.convertToInt(data.popList(2)));
					break;
				case I32:
					response [i] = new Integer(ByteList.convertToInt(data.popList(4),true));
					break;
				case I32STR:
					numVals32 = data.getUnsigned(0);
					data.pop();
					ByteList d32 = new ByteList(data.popList(numVals32*4));
					Integer [] i32Data = new Integer[numVals32];
					response [i] = i32Data;
					for(int j=0;j<numVals32;j++){
						i32Data[j]=new Integer(ByteList.convertToInt(d32.popList(4)));
					}
					break;
				case FIXED1k_STR:
					numVals32 = data.getUnsigned(0);
					data.pop();
					ByteList dStr = new ByteList(data.popList(numVals32*4));
					double [] dData = new double[numVals32];
					response [i] = dData;
					for(int j=0;j<numVals32;j++){
						dData[j]=new Double(ByteList.convertToInt(dStr.popList(4)))/1000.0;
					}
					break;
				case INVALID:
					break;
				case STR:
					int numVals = data.getUnsigned(0);
					data.pop();
					ByteList iData = new ByteList();
					response [i] = iData;
					if(numVals>0){
						ByteList d = new ByteList(data.popList(numVals));
						for(int j=0;j<numVals;j++){
							iData.add(new Integer(d.getUnsigned(j)));
						}
						
					}
					
					break;
				default:
					throw new RuntimeException("Unrecognized data type"+arguments[i]);
				}
			}
		}catch(java.lang.ClassCastException e){
			e.printStackTrace();
			Log.error("Expected : "+ arguments[i]+" got: "+response[i].getClass());
			if(arguments.length != response.length){
				Log.error("Wrong size : "+ arguments.length+" got: "+response.length);
			}else{
				for(int j=0;j<arguments.length;j++){
					Log.error("Valid : "+ arguments[j]+" got: "+response[i].getClass());
				}
			}
			
		}catch(RuntimeException ex){
			Log.error("Failed to parse "+i+"\n"+datagram+"\nFrom "+this);
			throw ex;
		}
		
		return response;
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
	 * Sets the namespace.
	 *
	 * @param namespace the new namespace
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	/**
	 * Gets the rpc.
	 *
	 * @return the rpc
	 */
	public String getRpc() {
		return rpc;
	}

	/**
	 * Sets the rpc.
	 *
	 * @param rpc the new rpc
	 */
	public void setRpc(String rpc) {
		this.rpc = rpc;
	}

	/**
	 * Gets the downstream method.
	 *
	 * @return the downstream method
	 */
	public BowlerMethod getDownstreamMethod() {
		return method;
	}

	/**
	 * Sets the down stream method.
	 *
	 * @param method the new down stream method
	 */
	public void setDownStreamMethod(BowlerMethod method) {
		this.method = method;
	}

	/**
	 * Gets the downstream arguments.
	 *
	 * @return the downstream arguments
	 */
	public BowlerDataType[] getDownstreamArguments() {
		return downstreamArguments;
	}
	

	/**
	 * Sets the downstream arguments.
	 *
	 * @param downstreamArguments the new downstream arguments
	 */
	public void setDownstreamArguments(BowlerDataType[] downstreamArguments) {
		for(int i=0;i<downstreamArguments.length;i++){
			if(downstreamArguments[i] == null){
				throw new RuntimeException("RPC argument can not be null");
			}
		}
		this.downstreamArguments = downstreamArguments;
	}

	/**
	 * Gets the upstream arguments.
	 *
	 * @return the upstream arguments
	 */
	public BowlerDataType[] getUpstreamArguments() {
		return upstreamArguments;
	}

	/**
	 * Sets the upstream arguments.
	 *
	 * @param upstreamArguments the new upstream arguments
	 */
	public void setUpstreamArguments(BowlerDataType[] upstreamArguments) {
		if(upstreamArguments== null)
			return;// asynchronus packets have no upstream
		for(int i=0;i<upstreamArguments.length;i++){
			if(upstreamArguments[i] == null){
				throw new RuntimeException("RPC argument can not be null");
			}
		}
		this.upstreamArguments = upstreamArguments;
	}

	/**
	 * Gets the up stream method.
	 *
	 * @return the up stream method
	 */
	public BowlerMethod getUpStreamMethod() {
		return upStreamMethod;
	}

	/**
	 * Sets the up stream method.
	 *
	 * @param upStreamMethod the new up stream method
	 */
	public void setUpStreamMethod(BowlerMethod upStreamMethod) {
		this.upStreamMethod = upStreamMethod;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String s=getNamespace()+" "+getRpc()+" "+getDownstreamMethod();
		if(getDownstreamArguments()!=null){
			s+=" (";
			for(int i=0;i<getDownstreamArguments().length;i++){
				
				s+=getDownstreamArguments()[i]+ " ";
			}
			s+=") ";
			s+=" "+getUpStreamMethod()+" (";
			for(int i=0;i<getUpstreamArguments().length;i++){
				s+=getUpstreamArguments()[i]+ " ";
			}
			s+=") ";
		}
		return s;	
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
	 * @param namespaceIndex the new namespace index
	 */
	public void setNamespaceIndex(int namespaceIndex) {
		this.namespaceIndex = namespaceIndex;
	}

	/**
	 * Gets the processor.
	 *
	 * @return the processor
	 */
	public IBowlerCommandProcessor getProcessor() {
		return processor;
	}

	/**
	 * Sets the processor.
	 *
	 * @param processor the new processor
	 */
	public void setProcessor(IBowlerCommandProcessor processor) {
		this.processor = processor;
	}

}
