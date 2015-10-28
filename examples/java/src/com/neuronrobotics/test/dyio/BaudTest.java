package com.neuronrobotics.test.dyio;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class BaudTest.
 */
public class BaudTest {
	
	/**
	 * Instantiates a new baud test.
	 */
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
	
	/**
	 * Almost equal.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the double
	 */
	public static double almostEqual(double a, double b){
		double absoluteDifference = (a-b);
		double percent = (absoluteDifference/a)*100;
	    return percent;
	}
	
	/**
	 * The Class BaudObject.
	 */
	private class BaudObject{
		
		/** The b rg value. */
		private int bRGValue;
		
		/** The baudrate. */
		private double baudrate;

		/**
		 * Instantiates a new baud object.
		 *
		 * @param BRGValue the BRG value
		 * @param baudrate the baudrate
		 */
		BaudObject(int BRGValue, double baudrate){
			setbRGValue(BRGValue);
			this.setBaudrate(baudrate);
			
		}

		/**
		 * Gets the b rg value.
		 *
		 * @return the b rg value
		 */
		public int getbRGValue() {
			return bRGValue;
		}

		/**
		 * Sets the b rg value.
		 *
		 * @param bRGValue the new b rg value
		 */
		public void setbRGValue(int bRGValue) {
			this.bRGValue = bRGValue;
		}

		/**
		 * Gets the baudrate.
		 *
		 * @return the baudrate
		 */
		public double getBaudrate() {
			return baudrate;
		}

		/**
		 * Sets the baudrate.
		 *
		 * @param baudrate the new baudrate
		 */
		public void setBaudrate(double baudrate) {
			this.baudrate = baudrate;
		}

		/**
		 * Int value.
		 *
		 * @return the int
		 */
		public int intValue() {
			// TODO Auto-generated method stub
			return new Double(getBaudrate()).intValue();
		}
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		new  BaudTest();
				
	}

}
