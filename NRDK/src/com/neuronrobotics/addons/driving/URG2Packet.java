package com.neuronrobotics.addons.driving;

import java.util.ArrayList;

import com.neuronrobotics.sdk.common.ByteList;

public class URG2Packet {
	String cmd;
	String junk;
	String status;
	int timestamp=0;
	ByteList dataLines=new ByteList();
	private final int center = 384;
	private final double degreesPerAngleUnit = 0.352422908;
	private int start;
	private int end;
	private int stepsPerDataPoint;
	private ArrayList<DataPoint> data=new ArrayList<DataPoint>();
	
	
	public URG2Packet(String line){
		String [] sections = line.split("\\n");
		cmd = sections[0];
		if(cmd.contains("MD")||cmd.contains("MS")){
			//junk = sections[1];
			status = sections[1];
			start = Integer.parseInt(cmd.substring(2, 6));
			end   = Integer.parseInt(cmd.substring(6, 10));
			stepsPerDataPoint = Integer.parseInt(cmd.substring(10, 12));
			if(sections.length>2){
				String ts = new String(new ByteList(sections[2].getBytes()).getBytes(0,4));
				//timestamp = decodeURG(ts);
				for(int i=3;i<sections.length;i++){
					byte [] sec = sections[i].getBytes();
					ByteList bl = new ByteList(sec);
					int len =  sections[i].length()-1;//Remove the '\r'
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
				throw new RuntimeException("Unknown packet: "+line+" Command="+cmd);
			}
			
		}else if(cmd.contains("QT")) {
			//do nothing
		}else{
			throw new RuntimeException("Unknown packet: "+line);
		}
	}
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
	public static int decodeURG(String data){
		System.out.println("Decoding string="+data);
		return decodeURG(data.getBytes());
	}
	@Override
	public String toString(){
		String s="Command: "+cmd;
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

	public ArrayList<DataPoint> getData() {
		return data;
	}
	
}
