package com.neuronrobotics.test.dyio;

import java.util.ArrayList;

public class BaudTest {
	
	public BaudTest(){
		ArrayList<BaudObject> picBauds = new ArrayList<BaudObject>();
		ArrayList<BaudObject> avrBauds = new ArrayList<BaudObject>();
		
		for(int i=0xffff;i>0;i--){
			double tmp = (80000000.0/(4.0*(((double)i)+1.0)));
			
			int calculated = (int) ((80000000.0/(4.0*tmp))-1.0);
			if(calculated == i&& tmp>10000){
				//System.out.println("Pic Baud = "+tmp+" baud Set = "+calculated);
				picBauds.add(new BaudObject(i, tmp));
			}
		}
		
		
		for(int i=0xffff;i>0;i--){
			double tmp = 18432000.0/(16.0*((double)i+1.0));
			
			int calculated =(int) (( 18432000.0/(16.0*tmp))-1.0);
			if(calculated == i && tmp>10000){
				//System.out.println("AVR Baud = "+tmp+" baud Set = "+calculated);
				avrBauds.add(new BaudObject(i, tmp));
			}
		}
		
		for(BaudObject p:picBauds ){
			for(BaudObject a: avrBauds){
				double percent = almostEqual( a.getBaudrate(), p.getBaudrate());
				if(percent < .1 && percent > -1 ){
					System.out.print("\r\nAVR Baud = "+a.intValue()+" \tPic Baud = "+p.intValue()+ "\tPercent = "+percent);
					System.out.print( " AVR Baud = "+a.getbRGValue()+" \tPic Baud = "+p.getbRGValue());
				}
			}
		}
	}
	
	public static double almostEqual(double a, double b){
		double absoluteDifference = (a-b);
		double percent = (absoluteDifference/a)*100;
	    return percent;
	}
	
	private class BaudObject{
		private int bRGValue;
		private double baudrate;

		BaudObject(int BRGValue, double baudrate){
			setbRGValue(BRGValue);
			this.setBaudrate(baudrate);
			
		}

		public int getbRGValue() {
			return bRGValue;
		}

		public void setbRGValue(int bRGValue) {
			this.bRGValue = bRGValue;
		}

		public double getBaudrate() {
			return baudrate;
		}

		public void setBaudrate(double baudrate) {
			this.baudrate = baudrate;
		}

		public int intValue() {
			// TODO Auto-generated method stub
			return new Double(getBaudrate()).intValue();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		new  BaudTest();
				
	}

}
