package com.neuronrobotics.sdk.common;

public class RpcEncapsulation {

	private String namespace;
	private String rpc;
	private BowlerMethod method;
	private BowlerDataType[] downstreamArguments;
	private BowlerDataType[] upstreamArguments;
	private BowlerMethod upStreamMethod;
	private int namespaceIndex;

	/**
	 * This is an encapsulation object for a given RPC
	 * 
	 * @param namespace 			The corosponding Namespace
	 * @param rpc					The 4 byte RPC code
	 * @param downStreamMethod		The method for sending messages
	 * @param downstreamArguments	The array of data types for a downstream message
	 * @param upStreamMethod		The return method type
	 * @param upstreamArguments 	THe return method arguments
	 */
	public RpcEncapsulation(int namespaceIndex,String namespace, String rpc, 
			BowlerMethod downStreamMethod,BowlerDataType[] downstreamArguments, 
			BowlerMethod upStreamMethod,BowlerDataType[] upstreamArguments){
		this.setNamespaceIndex(namespaceIndex);
		this.setNamespace(namespace);
		this.setRpc(rpc);
		setArguments( downStreamMethod,downstreamArguments, upStreamMethod, upstreamArguments);
	}
	
	public void setArguments(BowlerMethod downStreamMethod,BowlerDataType[] downstreamArguments, BowlerMethod upStreamMethod,BowlerDataType[] upstreamArguments){
		this.setUpStreamMethod(upStreamMethod);
		this.setDownstreamArguments(downstreamArguments);
		this.setUpstreamArguments(upstreamArguments);
		this.setDownStreamMethod(downStreamMethod);
	}
	
	public BowlerAbstractCommand getCommand(Object [] doswnstreamData){
		BowlerAbstractCommand command = new BowlerAbstractCommand() {};
		
		command.setOpCode(getRpc());
		command.setMethod(getDownstreamMethod());
		command.setNamespaceIndex(getNamespaceIndex());
		
		for(int i=0;(i<downstreamArguments.length && i < doswnstreamData.length);i++ ){
			switch(downstreamArguments[i]){
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
			case I16:
				command.getCallingDataStorage().addAs16(Integer.parseInt(doswnstreamData[i].toString()));
				break;
			case I32:
				command.getCallingDataStorage().addAs32(Integer.parseInt(doswnstreamData[i].toString()));
				break;
			case I32STR:
				Integer [] data32 = (Integer [])doswnstreamData[i];
				command.getCallingDataStorage().add(data32.length);
				for(int i1=0;i1<data32.length;i1++){
					command.getCallingDataStorage().addAs32(data32[i1]);
				}
				break;
			case INVALID:
				break;
			case STR:
				Integer [] data = (Integer [])doswnstreamData[i];
				command.getCallingDataStorage().add(data.length);
				for(int i1=0;i1<data.length;i1++){
					command.getCallingDataStorage().add(data[i1]);
				}
				break;
			default:
				break;
			}
		}
		
		return command;
	}
	
	public Object [] parseResponse(BowlerDatagram datagram){
		Object [] response = new Object[upstreamArguments.length];
		ByteList data = datagram.getData();
		for(int i=0;(i<upstreamArguments.length);i++ ){
			
			switch(upstreamArguments[i]){
			case ASCII:
				String s = data.asString();
				data.popList(s.length()+1);
				response [i] = s;
				break;
			case FIXED100:
				response [i] = new Double(ByteList.convertToInt(data.popList(4)))/100;
				break;
			case FIXED1k:
				response [i] = new Double(ByteList.convertToInt(data.popList(4)))/1000;
				break;
			case I08:
				response [i] = new Integer(data.getUnsigned(0));
				data.pop();
				break;
			case I16:
				response [i] = new Integer(ByteList.convertToInt(data.popList(2)));
				break;
			case I32:
				byte [] i32 = data.popList(4);
				response [i] = new Integer(ByteList.convertToInt(i32,true));
				break;
			case I32STR:
				int numVals32 = data.getUnsigned(0);
				data.pop();
				ByteList d32 = new ByteList(data.popList(numVals32*4));
				Integer [] i32Data = new Integer[numVals32];
				response [i] = i32Data;
				for(int j=0;j<numVals32;j++){
					i32Data[j]=new Integer(ByteList.convertToInt(d32.popList(4)));
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
				break;
			}
		}
		
		return response;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getRpc() {
		return rpc;
	}

	public void setRpc(String rpc) {
		this.rpc = rpc;
	}

	public BowlerMethod getDownstreamMethod() {
		return method;
	}

	public void setDownStreamMethod(BowlerMethod method) {
		this.method = method;
	}

	public BowlerDataType[] getDownstreamArguments() {
		return downstreamArguments;
	}

	public void setDownstreamArguments(BowlerDataType[] downstreamArguments) {
		this.downstreamArguments = downstreamArguments;
	}

	public BowlerDataType[] getUpstreamArguments() {
		return upstreamArguments;
	}

	public void setUpstreamArguments(BowlerDataType[] upstreamArguments) {
		this.upstreamArguments = upstreamArguments;
	}

	public BowlerMethod getUpStreamMethod() {
		return upStreamMethod;
	}

	public void setUpStreamMethod(BowlerMethod upStreamMethod) {
		this.upStreamMethod = upStreamMethod;
	}

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

	public int getNamespaceIndex() {
		return namespaceIndex;
	}

	public void setNamespaceIndex(int namespaceIndex) {
		this.namespaceIndex = namespaceIndex;
	}

}
