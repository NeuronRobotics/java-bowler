package com.neuronrobotics.sdk.addons.kinematics.gcodebridge;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import com.neuronrobotics.sdk.addons.kinematics.AbstractLink;
import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration;
import com.neuronrobotics.sdk.addons.kinematics.LinkType;
import com.neuronrobotics.sdk.common.DeviceManager;
import com.neuronrobotics.sdk.common.IFlushable;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.NonBowlerDevice;
import com.neuronrobotics.sdk.util.ThreadUtil;

import gnu.io.NRSerialPort;

public class GcodeDevice extends NonBowlerDevice implements IGcodeExecuter, IFlushable{
	
	private static final String tool = LinkType.GCODE_STEPPER_TOOL.getName();

	private static final String rot = LinkType.GCODE_STEPPER_ROTORY.getName();

	private static final String pris = LinkType.GCODE_STEPPER_PRISMATIC.getName();

	private NRSerialPort serial;
	
	private DataInputStream ins=null;
	private DataOutputStream outs=null;
	private int timeoutMs = 1000;
	private GCodeDeviceConfiguration config = new GCodeDeviceConfiguration();
	private HashMap<LinkConfiguration,IGCodeChannel> links = new HashMap<LinkConfiguration,IGCodeChannel>();
	private AbstractLink heater=null;
	private AbstractLink bed=null;
	

	public GcodeDevice(NRSerialPort serial){
		this.serial = serial;
		
	}
	public AbstractLink getHeater(LinkConfiguration axis){
		String gcodeAxis;
		switch(axis.getHardwareIndex()){
		case 0:
			gcodeAxis=("T");
			if(heater==null)
				heater = new GCodeHeater(axis,gcodeAxis,this);
			return heater;
		case 1:
			gcodeAxis=("B");
			if(bed==null)
				bed = new GCodeHeater(axis,gcodeAxis,this);
			return bed;
		default:
			throw new RuntimeException("Gcode devices only support 2 heaters");
		}
		
	}
	public AbstractLink getLink(LinkConfiguration axis){
		if(links.get(axis)!=null)
			return (AbstractLink)links.get(axis);
		String gcodeAxis = "";
		AbstractLink tmp=null;
		axis.setDeviceTheoreticalMax(Integer.MAX_VALUE);
		axis.setDeviceTheoreticalMin(Integer.MIN_VALUE);
		int test =0;
		if(axis.getTypeString().contentEquals(pris))
			test=1;
		if(axis.getTypeString().contentEquals(rot))
			test=2;
		if(axis.getTypeString().contentEquals(tool))
			test=3;
		switch(test){
		case 1:
		case 2:
		case 3:
			switch(axis.getHardwareIndex()){
			case 0:
				gcodeAxis=("X");
				break;
			case 1:
				gcodeAxis=("Y");
				break;
			case 2:
				gcodeAxis=("Z");
				break;
			case 3:
				gcodeAxis=("E");
				break;
			default:
					throw new RuntimeException("Gcode devices only support 4 axis");
			}
			break;
			default:
				break;
		}
		switch(test){
		case 1:
			tmp = new GcodePrismatic(axis,this,gcodeAxis);
			
			break;
		case 2:
			tmp = new GcodeRotory(axis,this,gcodeAxis);
			
			break;
		case 3:
			tmp = new GcodeRotory(axis,this,gcodeAxis);
		
			break;
		default:
				break;
		}
		if(tmp!=null){
			links.put(axis,(IGCodeChannel) tmp);
		}
		return tmp;
	}

	@Override
	public void disconnectDeviceImp() {
		if(serial.isConnected()){
			runLine("M84");// Disable motors on exit
			getLine();// fluch buffer
		}
		if(outs!=null){
			try {
				outs.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				outs.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(ins!=null)
			try {
				ins.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		outs=null;
		ins=null;
		if(serial.isConnected())
			serial.disconnect();
	}

	@Override
	public boolean connectDeviceImp() {
		disconnectDeviceImp();
		if(!serial.connect()){
			throw new RuntimeException("Failed to connect to the serial device");
		}
		ins= new DataInputStream(serial.getInputStream());
		outs =  new DataOutputStream(serial.getOutputStream());
		runLine("M105");// initializes the device
		return true;
	}

	@Override
	public ArrayList<String> getNamespacesImp() {
		// TODO Auto-generated method stub
		return new ArrayList<String>();
	}
	@SuppressWarnings("resource")
	private  String getLine(){
		
		String ret="";
		try {
			if(ins.available()>0){
				java.util.Scanner s = new java.util.Scanner(ins).useDelimiter("\\A");
				ret =s.hasNext() ? s.next() : "";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return ret;
	}

	//usb.dst contains "1.121.2"
	@Override
	public   String runLine(String line) {
		if(!line.endsWith("\r\n"))
			line = line+"\r\n";
		try {
			//synchronized(outs){
				outs.write(line.getBytes());
				outs.flush();
			//}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long start = currentTimeMillis();
		String ret= "";
		while(ret.contentEquals("") && 
				(currentTimeMillis()-start)<getTimeoutMs()){
			
			wait(10);
			ret = getLine();
		}
		if((currentTimeMillis()-start)<getTimeoutMs()){
			Log.error("GCODE TIMEOUT: "+line);
		}
		Log.info("S>>"+line);
		Log.info("R<<"+ret);
		return ret;
	}

	@Override
	public void runFile(File gcode) {
		// TODO Auto-generated method stub
		
	}

	public int getTimeoutMs() {
		return timeoutMs;
	}

	public void setTimeoutMs(int timeoutMs) {
		this.timeoutMs = timeoutMs;
	}

	@Override
	public GCodeDeviceConfiguration getConfiguration() {
		return config;
	}

	@Override
	public void flush(double seconds) {
		String run = "G1 ";
		for(LinkConfiguration l:links.keySet()){
			IGCodeChannel thisLink = links.get(l);
			run +=thisLink.getAxis()+""+((AbstractLink)thisLink).getTargetValue()+" ";
		}
		loadCurrent();
		AbstractLink firstLink = (AbstractLink)links.get(links.keySet().toArray()[0]);
		double distance = firstLink.getTargetValue()-firstLink.getCurrentPosition();
		if(distance !=0 && seconds>0){
			int feedrate = (int)Math.abs((distance/(seconds/60)));//mm/min
			run +=" F"+feedrate;
		}
		if(bed!=null)
			bed.flush(seconds);
		if(heater!=null)
			heater.flush(seconds);
		runLine(run);
	}
	
	public void loadCurrent(){
		String m114 =runLine("M114");
		String[] currentPosStr = m114.split("Count")[0].split(" ");// get the current position
		//System.out.println("Fush with current = "+m114);
		for(String s:currentPosStr){
			for(LinkConfiguration l:links.keySet()){
				IGCodeChannel thisLink = links.get(l);
				if(s.contains(thisLink.getAxis())){
					String [] parts = s.split(":");
					///System.out.println("Found axis = "+s);
					thisLink.setValue(Double.parseDouble(parts[1]));
				}
			}
		}
	}

}
