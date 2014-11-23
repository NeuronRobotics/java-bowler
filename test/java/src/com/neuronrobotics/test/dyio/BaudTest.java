package com.neuronrobotics.test.dyio;

import java.util.ArrayList;

public class BaudTest {
	
	public static double almostEqual(double a, double b){
		double absoluteDifference = Math.abs(a-b);
		double percent = (absoluteDifference/a)*100;
	    return percent;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<Double> picBauds = new ArrayList<Double>();
		ArrayList<Double> avrBauds = new ArrayList<Double>();
		
		for(int i=0xffff;i>0;i--){
			double tmp = (80000000.0/(4.0*(((double)i)+1.0)));
			
			int calculated = (int) ((80000000.0/(4.0*tmp))-1.0);
			if(calculated == i&& tmp>10000){
				//System.out.println("Pic Baud = "+tmp+" baud Set = "+calculated);
				picBauds.add(tmp);
			}
		}
		
		
		for(int i=0xffff;i>0;i--){
			double tmp = 18432000.0/(16.0*((double)i+1.0));
			
			int calculated =(int) (( 18432000.0/(16.0*tmp))-1.0);
			if(calculated == i && tmp>10000){
				//System.out.println("AVR Baud = "+tmp+" baud Set = "+calculated);
				avrBauds.add(tmp);
			}
		}
		
		for(Double p:picBauds ){
			for(Double a: avrBauds){
				double percent = almostEqual( a, p);
				if(percent < .2){
					System.out.println("AVR Baud = "+a.intValue()+" \tPic Baud = "+p.intValue()+ "\tPercent = "+percent);
				}
			}
		}
		
				
	}

}
