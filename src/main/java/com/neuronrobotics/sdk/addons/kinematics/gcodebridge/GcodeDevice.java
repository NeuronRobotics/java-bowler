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

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.NonBowlerDevice;
import com.neuronrobotics.sdk.util.ThreadUtil;

import gnu.io.NRSerialPort;
import sun.nio.ch.IOUtil;

public class GcodeDevice extends NonBowlerDevice implements IGcodeExecuter{
	
	private NRSerialPort serial;
	
	private DataInputStream ins=null;
	private DataOutputStream outs=null;
	private int timeoutMs = 1000;
	private GCodeDeviceConfiguration config = new GCodeDeviceConfiguration();

	public GcodeDevice(NRSerialPort serial){
		this.serial = serial;
		
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
			while(ins.available()>0){
				ret+=new String(new byte[] {(byte) ins.read()});
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
		long start = System.currentTimeMillis();
		String ret= "";
		while(ret.contentEquals("") && 
				(System.currentTimeMillis()-start)<getTimeoutMs()){
			
			ThreadUtil.wait(10);
			ret = getLine();
		}
		if((System.currentTimeMillis()-start)<getTimeoutMs()){
			Log.error("GCODE TIMEOUT: "+line);
		}
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

}
