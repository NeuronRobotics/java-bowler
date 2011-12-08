package com.neuronrobotics.addons.driving;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.util.ThreadUtil;

import gnu.io.NRSerialPort;

public class HokuyoURGDevice {
	private NRSerialPort serial;
	private DataInputStream ins;
	private DataOutputStream outs;
	private Thread receive;
	
	private final int center = 384;//from datasheet
	private final double degreesPerAngleUnit = 0.352422908;//from datasheet
	
	
	private URG2Packet packet=null;
	
	public HokuyoURGDevice(NRSerialPort port){
		serial=port;
		serial.connect();                                 
		 

		ins = new DataInputStream(serial.getInputStream());                         
		 
		outs = new DataOutputStream(serial.getOutputStream());
		
		receive = new Thread(){
			public void run(){
				ByteList bl = new ByteList();
				//System.out.println("Starting listener");
				while(true){
					try {
						if(ins.available()>0){
							while(ins.available()>0){
								int b = ins.read();
								if(b==10 && bl.get(bl.size()-1)==10){
									if(bl.size()>0){
										try{
											URG2Packet p =new URG2Packet(new String(bl.getBytes()));
											//System.out.println("New Packet: \n"+p);
											setPacket(p);
											bl = new ByteList();
										}catch(Exception ex){
											setPacket(null);
											//System.out.println("Unknown packet");
											//ex.printStackTrace();
										}
										
									}
								}else{
									bl.add(b);
								}
							}
						}else{
							
						}
					} catch (IOException e) {

						e.printStackTrace();
						break;
					}
					try {Thread.sleep(1);} catch (InterruptedException e) {}
				}
			}
		};
		clear();
		receive.start();
	}
	
	public void clear() {
		send("QT\n");
	}
	public URG2Packet startSweep(double startDeg, double endDeg, double degPerStep) {
		setPacket(null);
		scan(degreeToTicks(startDeg),degreeToTicks(endDeg),1,1,1);
		while(getPacket() == null) {
			ThreadUtil.wait(10);
		}
		return getPacket();
	}
	private int degreeToTicks(double degrees) {
		int tick =(int)(degrees/degreesPerAngleUnit)+center;
		if(tick<0)
			tick=0;
		if(tick > (center*2))
			tick=center*2;
		return tick;
	}
	/**
	 * 
	 * @param startStep
	 * @param endStep
	 * @param clusterCount
	 * @param scanInterval
	 * @param numberOfScans
	 */
	public void scan(int startStep,int endStep,int clusterCount,int scanInterval,int numberOfScans){
		clear();
		String cmd = "MD";
		cmd+=new DecimalFormat("0000").format(startStep);
		cmd+=new DecimalFormat("0000").format(endStep);
		cmd+=new DecimalFormat("00").format(clusterCount);
		cmd+=new DecimalFormat("0").format(scanInterval);
		cmd+=new DecimalFormat("00").format(numberOfScans);
		cmd+="\n\r";
		send(cmd);
	}
	
	private void send(String data){
		try {
			//System.out.println("\nSending: "+data);
			outs.write(data.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		} 
		 
	}

	public URG2Packet getPacket() {
		return packet;
	}

	public void setPacket(URG2Packet packet) {
		this.packet = packet;
	}
}
