package com.neuronrobotics.sdk.addons.kinematics.delta;

import com.neuronrobotics.sdk.addons.kinematics.AbstractRotoryLink;

public class DeltaRobot {
	AbstractRotoryLink t1;
	AbstractRotoryLink t2;
	AbstractRotoryLink t3;
	DeltaRobotKinematics kinematics;
	public DeltaRobot(AbstractRotoryLink theta1,AbstractRotoryLink theta2,AbstractRotoryLink theta3, DeltaRobotKinematics kin){
		t1=theta1;
		t2=theta2;
		t3=theta3;
		kinematics=kin;
		t1.Home();
		t2.Home();
		t3.Home();
		t1.flush(2);
	}
}
