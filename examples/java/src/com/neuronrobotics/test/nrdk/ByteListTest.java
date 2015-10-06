package com.neuronrobotics.test.nrdk;

import com.neuronrobotics.sdk.common.ByteList;

public class ByteListTest {
	public static void main(String [] args) {
		byte [] b = ByteList.convertTo16(526);
		System.out.println(b[0] + " - " + b[1]);
		
		int i = ByteList.convertToInt(b);
		System.out.println(i);
	}
}
