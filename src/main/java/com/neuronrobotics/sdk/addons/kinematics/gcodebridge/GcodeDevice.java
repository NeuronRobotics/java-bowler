package com.neuronrobotics.sdk.addons.kinematics.gcodebridge;

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
	
	InputStream ins;
	OutputStream outs;
	private int timeoutMs = 1000;

	public GcodeDevice(NRSerialPort serial){
		this.serial = serial;
		
	}

	@Override
	public void disconnectDeviceImp() {
		if(ins!=null){
			try {
				ins.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ins=null;
		}
		if(outs!=null){
			try {
				outs.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			outs=null;
		}
	}

	@Override
	public boolean connectDeviceImp() {
		disconnectDeviceImp();
		serial.connect();
		ins=serial.getInputStream();
		outs = serial.getOutputStream();
		
		return true;
	}

	@Override
	public ArrayList<String> getNamespacesImp() {
		// TODO Auto-generated method stub
		return new ArrayList<String>();
	}
	
	private  String getLine(){
		@SuppressWarnings("resource")
		String ret=null;
		synchronized(ins){
			java.util.Scanner s = new java.util.Scanner(ins).useDelimiter("\\n");
			ret =s.hasNext() ? s.next() : "";
		}
	    return ret;
	}

	@Override
	public   String runLine(String line) {
		try {
			synchronized(outs){
				outs.write(line.getBytes());
			}
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

}
