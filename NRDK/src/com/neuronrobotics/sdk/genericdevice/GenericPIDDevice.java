package com.neuronrobotics.sdk.genericdevice;

import java.util.ArrayList;

import com.neuronrobotics.sdk.commands.bcs.pid.ConfigurePIDCommand;
import com.neuronrobotics.sdk.commands.bcs.pid.ControlAllPIDCommand;
import com.neuronrobotics.sdk.commands.bcs.pid.ControlPIDCommand;
import com.neuronrobotics.sdk.commands.bcs.pid.ResetPIDCommand;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.MACAddress;
import com.neuronrobotics.sdk.pid.IPIDControl;
import com.neuronrobotics.sdk.pid.IPIDEventListener;
import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDCommandException;
import com.neuronrobotics.sdk.pid.PIDConfiguration;
import com.neuronrobotics.sdk.pid.PIDEvent;
import com.neuronrobotics.sdk.pid.PIDLimitEvent;

public class GenericPIDDevice extends BowlerAbstractDevice implements IPIDControl {
	private ArrayList<PIDChannel> channels = new ArrayList<PIDChannel>();
	public GenericPIDDevice(BowlerAbstractConnection connection) {
		setAddress(new MACAddress(MACAddress.BROADCAST));
		setConnection(connection);
	}

	public GenericPIDDevice() {
	}

	
	public void onAllResponse(BowlerDatagram data) {
		// TODO Auto-generated method stub

	}

	
	public void onAsyncResponse(BowlerDatagram data) {
		if(data.getRPC().contains("_pid")){
			firePIDEvent(new PIDEvent(data));
		}
		if(data.getRPC().contains("pidl")){
			firePIDLimitEvent(new PIDLimitEvent(data));
		}

	}
	
	public boolean SetPIDSetPoint(int group,int setpoint,double seconds){
		channels.get(group).setCachedTargetValue(setpoint);
		return send(new  ControlPIDCommand((char) group,setpoint, seconds))!=null;
	}
	
	public boolean SetAllPIDSetPoint(int []setpoints,double seconds){
		for(int i=0;i<channels.size();i++){
			channels.get(i).setCachedTargetValue(setpoints[i]);
		}
		return send(new  ControlAllPIDCommand(setpoints, seconds))!=null;
	}
	
	public int GetPIDPosition(int group) {
		BowlerDatagram b = send(new  ControlPIDCommand((char) group));
		return ByteList.convertToInt(b.getData().getBytes(1, 4),true);
	}
	
	public int [] GetAllPIDPosition() {
		BowlerDatagram b = send(new ControlAllPIDCommand());
		ByteList data = b.getData();
		int [] back = new int[data.size()/4];
		for(int i=0;i<back.length;i++) {
			back[i] = ByteList.convertToInt( data.getBytes(i*4, (i*4)+4),true);
		}
		if(back.length != channels.size()){
			channels =  new ArrayList<PIDChannel>();
			for(int i=0;i<back.length;i++){
				PIDChannel c =new PIDChannel(this,i);
				c.setCachedTargetValue(back[i]);
				channels.add(c);
			}
		}
		return back;
	}
	
	
	public boolean ConfigurePIDController(PIDConfiguration config) {
		return send(new  ConfigurePIDCommand(config))!=null;
	}

	
	public PIDConfiguration getPIDConfiguration(int group) {
		BowlerDatagram conf = send(new ConfigurePIDCommand( (char) group) );
		PIDConfiguration back=new PIDConfiguration (conf);
		return back;
	}
	
	public boolean ResetPIDChannel(int group) {
		BowlerDatagram rst = send(new  ResetPIDCommand((char) group));
		if(rst==null)
			return false;
		int val = GetPIDPosition(group);
		firePIDResetEvent(group,val);
		return true;
	}

	
	public boolean ResetPIDChannel(int group, int valueToSetCurrentTo) {
		BowlerDatagram rst = send(new  ResetPIDCommand((char) group,valueToSetCurrentTo));
		if(rst==null)
			return false;
		int val = GetPIDPosition(group);
		firePIDResetEvent(group,val);
		return true;
	}
	
	
	private ArrayList<IPIDEventListener> PIDEventListeners = new ArrayList<IPIDEventListener>();
	
	public void addPIDEventListener(IPIDEventListener l) {
		synchronized(PIDEventListeners){
			if(!PIDEventListeners.contains(l))
				PIDEventListeners.add(l);
		}
	}
	public void firePIDLimitEvent(PIDLimitEvent e){
		synchronized(PIDEventListeners){
			for(IPIDEventListener l: PIDEventListeners)
				l.onPIDLimitEvent(e);
		}
	}
	public void firePIDEvent(PIDEvent e){
		synchronized(PIDEventListeners){
			for(IPIDEventListener l: PIDEventListeners)
				l.onPIDEvent(e);
		}
	}
	public void firePIDResetEvent(int group,int value){
		for(IPIDEventListener l: PIDEventListeners)
			l.onPIDReset(group,value);
	}

	@Override
	public void flushPIDChannels(double time) {
		int [] data = new int[channels.size()];
		for(int i=0;i<channels.size();i++){
			data[i]=channels.get(i).getCachedTargetValue();
		}
		SetAllPIDSetPoint(data, time);
	}

	@Override
	public boolean SetPIDVelicity(int group, int unitsPerSecond, double seconds) {
		long dist = (long)unitsPerSecond*(long)seconds;
		long delt = ((long) (GetPIDPosition(group))-dist);
		if(delt>2147483646 || delt<-2147483646){
			throw new PIDCommandException("(Current Position) - (Velocity * Time) too large: "+delt);
		}
		return SetPIDSetPoint(group, (int) delt, seconds);
	}
	

}
