package com.neuronrobotics.test.kinematics;

import com.neuronrobotics.sdk.addons.kinematics.MockRotoryLink;
import com.neuronrobotics.sdk.addons.kinematics.dh.DHnode;

public class DHtest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DHnode l0 = new DHnode(new MockRotoryLink(0,0,0,0), 0, 6.0, 0);
		DHnode l1 = new DHnode(new MockRotoryLink(0,0,0,0), 0, 4.0, 0);
		DHnode l2 = new DHnode(new MockRotoryLink(0,0,0,0), 0, 3.9, 0);
		
		System.out.println("Link 0="+l0);
		System.out.println("Link 1="+l1);
		System.out.println("Link 2="+l2);
		
		System.out.println("ok");
	}

}
