package com.neuronrobotics.sdk.common;

public class RpcEncapsulation {

	private String namespace;
	private String rpc;
	private BowlerMethod method;
	private String[] downstreamArguments;
	private String[] upstreamArguments;
	private BowlerMethod upStreamMethod;

	public RpcEncapsulation(String namespace, String rpc, BowlerMethod downStreamMethod,String[] downstreamArguments, BowlerMethod upStreamMethod,String[] upstreamArguments){
		this.setNamespace(namespace);
		this.setRpc(rpc);
		setArguments( downStreamMethod,downstreamArguments, upStreamMethod, upstreamArguments);
	}
	public RpcEncapsulation(String namespace, String rpc, BowlerMethod downStreamMethod){
		this.setNamespace(namespace);
		this.setRpc(rpc);
		this.setDownStreamMethod(downStreamMethod);
	}
	
	public void setArguments(BowlerMethod downStreamMethod,String[] downstreamArguments, BowlerMethod upStreamMethod,String[] upstreamArguments){
		this.setUpStreamMethod(upStreamMethod);
		this.setDownstreamArguments(downstreamArguments);
		this.setUpstreamArguments(upstreamArguments);
		this.setDownStreamMethod(downStreamMethod);
	}
	
	public BowlerAbstractCommand getCommand(int [] doswnstreamData){
		BowlerAbstractCommand command = new BowlerAbstractCommand() {};
		
		command.setOpCode(getRpc());
		command.setMethod(getMethod());
		
		for(int i=0;(i<downstreamArguments.length && i < doswnstreamData.length);i++ ){
			if(downstreamArguments[i].contains("i08")){
				command.getCallingDataStorage().add(doswnstreamData[i]);
			}else
			if(downstreamArguments[i].contains("i16")){
				command.getCallingDataStorage().addAs16(doswnstreamData[i]);
			}else
			if(downstreamArguments[i].contains("i32")){
				command.getCallingDataStorage().addAs32(doswnstreamData[i]);
			}
		}
		
		return command;
	}
	
	public int [] parseResponse(BowlerDatagram datagram){
		int [] response = new int [upstreamArguments.length];
		ByteList data = datagram.getData();
		for(int i=0;(i<upstreamArguments.length);i++ ){
			if(upstreamArguments[i].contains("i08")){
				response [i] = data.pop();
			}else
			if(upstreamArguments[i].contains("i16")){
				response [i] = ByteList.convertToInt(data.popList(1));
			}else
			if(upstreamArguments[i].contains("i32")){
				response [i] = ByteList.convertToInt(data.popList(3));
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

	public BowlerMethod getMethod() {
		return method;
	}

	public void setDownStreamMethod(BowlerMethod method) {
		this.method = method;
	}



	public String[] getDownstreamArguments() {
		return downstreamArguments;
	}



	public void setDownstreamArguments(String[] downstreamArguments) {
		this.downstreamArguments = downstreamArguments;
	}



	public String[] getUpstreamArguments() {
		return upstreamArguments;
	}



	public void setUpstreamArguments(String[] upstreamArguments) {
		this.upstreamArguments = upstreamArguments;
	}

	public BowlerMethod getUpStreamMethod() {
		return upStreamMethod;
	}

	public void setUpStreamMethod(BowlerMethod upStreamMethod) {
		this.upStreamMethod = upStreamMethod;
	}



}
