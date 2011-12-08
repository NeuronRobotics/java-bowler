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
		int tick =(int)(degPerStep/degreesPerAngleUnit);
		if (tick>99)
			tick=99;
		if(tick<1)
			tick=1;
		scan(degreeToTicks(startDeg),degreeToTicks(endDeg),tick,1,1);
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
	 * @param startStep 	tick to start at
	 * @param endStep 		tick to end at
	 * 						Starting step and End Step can be any points between 0 and maximum step (see section 4). End Step
							should be always greater than Starting step.
	 * @param clusterCount 	Cluster Count is the number of adjacent steps that can be merged into single data and has a range 0 to
							99. When cluster count is more than 1, step having minimum measurement value (excluding error) in the
							cluster will be the output data. 
	 * @param scanInterval 	Scan Interval and
							Skipping the number of scans when obtaining multiple scan data can be set in Scan Interval. The value
							should be in decimal.
	 * @param numberOfScans User can request number of scan data by supplying the count in Number of Scan. If Number of Scan is
							set to 00 the data is supplied indefinitely unless canceled using [QT-Command] or [RS-Command].
							The value should be in decimal.
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
