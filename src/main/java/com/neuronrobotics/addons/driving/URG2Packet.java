package com.neuronrobotics.addons.driving;

import java.util.ArrayList;

import com.neuronrobotics.sdk.common.ByteList;

// TODO: Auto-generated Javadoc
/**
 * The Class URG2Packet.
 */
public class URG2Packet {
	
	/** The cmd. */
	private String cmd;
	
	/** The junk. */
	String junk;
	
	/** The status. */
	String status;
	
	/** The timestamp. */
	int timestamp=0;
	
	/** The data lines. */
	ByteList dataLines=new ByteList();
	
	/** The center. */
	private final int center = 384;
	
	/** The degrees per angle unit. */
	private final double degreesPerAngleUnit = 0.352422908;
	
	/** The start. */
	private int start;
	
	/** The end. */
	private int end;
	
	/** The steps per data point. */
	private int stepsPerDataPoint;
	
	/** The data. */
	private ArrayList<DataPoint> data=new ArrayList<DataPoint>();
	
	
	/**
	 * Instantiates a new UR g2 packet.
	 *
	 * @param line the line
	 */
	public URG2Packet(String line){
		String [] sections = line.split("\\n");//This removes the \n from the data
		setCmd(sections[0]);
		if(getCmd().contains("MD")||getCmd().contains("MS")){
			//junk = sections[1];
			status = sections[1];
			start = Integer.parseInt(getCmd().substring(2, 6));
			end   = Integer.parseInt(getCmd().substring(6, 10));
			stepsPerDataPoint = Integer.parseInt(getCmd().substring(10, 12));
			if(sections.length>2){
				String ts = new String(new ByteList(sections[2].getBytes()).getBytes(0,4));
				//timestamp = decodeURG(ts);
				for(int i=3;i<sections.length;i++){
					byte [] sec = sections[i].getBytes();
					ByteList bl = new ByteList(sec);
					int len =  sections[i].length()-1;//Remove the '\r' from the data
					dataLines.add(bl.getBytes(0, len));
				}
				int angleTicks = start;
				//System.out.println("Packet = "+line);
				//System.out.println("Data = "+dataLines.toString());
				while(dataLines.size()>2){
					int range = decodeURG(dataLines.popList(3));
					double angle = ((double)(angleTicks-center))*degreesPerAngleUnit;
					getData().add(new DataPoint(range, -1*angle));
					angleTicks+=stepsPerDataPoint;
				}
			}else {
				throw new RuntimeException("Unknown packet: "+line+" Command="+getCmd());
			}
			
		}else if(getCmd().contains("QT")) {
			//do nothing
		}else{
			throw new RuntimeException("Unknown packet: "+line);
		}
	}
	
	/**
	 * Decode urg.
	 *
	 * @param bs the bs
	 * @return the int
	 */
	public static int decodeURG(byte[] bs){
		if(bs.length!=3){
			System.err.println("URG fail: "+bs.length);
			throw new IndexOutOfBoundsException("URG decode expected 3 bytes, got: "+bs.length );
		}
		int back =0;
		byte [] d = bs;
		for(int i=0;i<d.length;i++){
			d[i]-=0x30;
			int tmp = rawByteToInt(d[i]);
			int power =(int) ((d.length-i-1)*6);
			long val = (long) (tmp * Math.pow(2, power));
			back+=val;
		}
		return back;
	}
	
	/**
	 * Decode urg.
	 *
	 * @param data the data
	 * @return the int
	 */
	public static int decodeURG(String data){
		System.out.println("Decoding string="+data);
		return decodeURG(data.getBytes());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String s="Command: "+getCmd();
		s+="\nStatus: "+status;
		if(getData().size()>0){
			s+="\nStart: "+start;
			s+="\nEnd: "+end;
			s+="\nStep: "+stepsPerDataPoint;
			s+="\nTimestamp: "+timestamp;
			s+="\nData: "+getData();
			s+="\nData Size: "+getData().size();
		}
		return s;		
	}
	
	
	/**
	 * Raw byte to int.
	 *
	 * @param b the b
	 * @return the int
	 */
	public static int rawByteToInt(byte b){
		int tmp =(int)b;
		if (tmp < 0){
			// This solves the Java signedness problem of "bytes"
			tmp +=256;
		}
		return tmp;
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public ArrayList<DataPoint> getData() {
		return data;
	}
	
	/**
	 * Gets the cmd.
	 *
	 * @return the cmd
	 */
	public String getCmd() {
		return cmd;
	}
	
	/**
	 * Sets the cmd.
	 *
	 * @param cmd the new cmd
	 */
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	
}
