package com.neuronrobotics.test.nrdk;

import com.neuronrobotics.sdk.common.ByteList;

// TODO: Auto-generated Javadoc
/**
 * The Class ByteListTest.
 */
public class ByteListTest {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String [] args) {
		byte [] b = ByteList.convertTo16(526);
		System.out.println(b[0] + " - " + b[1]);
		
		int i = ByteList.convertToInt(b);
		System.out.println(i);
	}
}
