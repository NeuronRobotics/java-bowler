package com.neuronrobotics.addons.driving;

import gnu.io.NRSerialPort;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.NonBowlerDevice;
import com.neuronrobotics.sdk.util.ThreadUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class HokuyoURGDevice.
 */
public class HokuyoURGDevice extends NonBowlerDevice{
	
	/** The serial. */
	private NRSerialPort serial;
	
	/** The ins. */
	private DataInputStream ins;
	
	/** The outs. */
	private DataOutputStream outs;
	
	/** The receive. */
	private Thread receive;
	
	/** The center. */
	private final int center = 384;//from datasheet
	
	/** The degrees per angle unit. */
	private final double degreesPerAngleUnit = 0.352422908;//from datasheet
	
	
	/** The packet. */
	private URG2Packet packet=null;
	
	/** The run. */
	boolean run=true;
	
	/** The done. */
	protected boolean done=false;
	
	/**
	 * Instantiates a new hokuyo urg device.
	 *
	 * @param port the port
	 */
	public HokuyoURGDevice(NRSerialPort port){
		serial=port;
	}
	
	/**
	 * Clear.
	 */
	public void clear() {
		send("QT\n");
	}
	
	/**
	 * Start sweep.
	 *
	 * @param startDeg the start deg
	 * @param endDeg the end deg
	 * @param degPerStep the deg per step
	 * @return the UR g2 packet
	 */
	public URG2Packet startSweep(double startDeg, double endDeg, double degPerStep) {
		setPacket(null);
		int tick =(int)(degPerStep/degreesPerAngleUnit);
		if (tick>99)
			tick=99;
		if(tick<1)
			tick=1;
		tick=1;//HACK
		scan(degreeToTicks(startDeg),degreeToTicks(endDeg),tick,0,1);
		ThreadUtil.wait(10);
		long start = System.currentTimeMillis();
		 do{
			if(System.currentTimeMillis()-start>2000)
				break;
			ThreadUtil.wait(10);
			
		}while(getPacket() == null ||!getPacket().getCmd().contains("MD") );
		if(getPacket()==null){
			System.err.println("Sweep failed, resetting and trying again");
			clear();
			startSweep(startDeg, endDeg, degPerStep);
		}
		System.out.print("Sweep got packet= "+getPacket());
		return getPacket();
	}
	
	/**
	 * Degree to ticks.
	 *
	 * @param degrees the degrees
	 * @return the int
	 */
	private int degreeToTicks(double degrees) {
		int tick =(int)(degrees/degreesPerAngleUnit)+center;
		if(tick<0)
			tick=0;
		if(tick > (center*2))
			tick=center*2;
		return tick;
	}
	
	/**
	 * Scan.
	 *
	 * @param startStep 	tick to start at
	 * @param endStep 		tick to end at
	 * 						Starting step and End Step can be any points between 0 and maximum step (see section 4). End Step
	 * 							should be always greater than Starting step.
	 * @param clusterCount 	Cluster Count is the number of adjacent steps that can be merged into single data and has a range 0 to
	 * 							99. When cluster count is more than 1, step having minimum measurement value (excluding error) in the
	 * 							cluster will be the output data. 
	 * @param scanInterval 	Scan Interval and
	 * 							Skipping the number of scans when obtaining multiple scan data can be set in Scan Interval. The value
	 * 							should be in decimal.
	 * @param numberOfScans User can request number of scan data by supplying the count in Number of Scan. If Number of Scan is
	 * 							set to 00 the data is supplied indefinitely unless canceled using [QT-Command] or [RS-Command].
	 * 							The value should be in decimal.
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
	
	/**
	 * Send.
	 *
	 * @param data the data
	 */
	private void send(String data){
		try {
			//System.out.println("\nSending: "+data);
			outs.write(data.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		} 
		 
	}

	/**
	 * Gets the packet.
	 *
	 * @return the packet
	 */
	public URG2Packet getPacket() {
		return packet;
	}

	/**
	 * Sets the packet.
	 *
	 * @param packet the new packet
	 */
	public void setPacket(URG2Packet packet) {
		this.packet = packet;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.NonBowlerDevice#disconnectDeviceImp()
	 */
	@Override
	public void disconnectDeviceImp() {
		run=false;
		if(receive!=null){
			receive.interrupt();
			while(!done && receive.isAlive());
			receive=null;
		}
		try{
			if(serial.isConnected())
				serial.disconnect();
		}catch(Exception ex){}
		
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.NonBowlerDevice#connectDeviceImp()
	 */
	@Override
	public boolean connectDeviceImp() {
		serial.connect();                                 
		 

		ins = new DataInputStream(serial.getInputStream());                         
		 
		outs = new DataOutputStream(serial.getOutputStream());
		
		receive = new Thread(){
			public void run(){
				setName("HokuyoURGDevice updater");
				ByteList bl = new ByteList();
				//System.out.println("Starting listener");
				while(run && !Thread.interrupted()){
					try {
						if(ins.available()>0){
							while(ins.available()>0 && run && !Thread.interrupted()){
								int b = ins.read();
								if(b==10 && bl.get(bl.size()-1)==10){
									if(bl.size()>0){
										try{
											URG2Packet p =new URG2Packet(new String(bl.getBytes()));
											Log.debug("New Packet: \n"+p);
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
								ThreadUtil.wait(1);
							}
						}else{
							
						}
					} catch (Exception e) {

						//e.printStackTrace();
						run=false;
						
					}
					try {Thread.sleep(1);} catch (InterruptedException e) {run=false;}
				}
				done=true;
			}
		};
		clear();
		receive.start();
		return serial.isConnected();
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.NonBowlerDevice#getNamespacesImp()
	 */
	@Override
	public ArrayList<String> getNamespacesImp() {
		// TODO Auto-generated method stub
		return new ArrayList<String>();
	}
}
