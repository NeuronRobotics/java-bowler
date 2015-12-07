package com.neuronrobotics.sdk.addons.kinematics;

public enum DhLinkType {
	
	ROTORY,
	PRISMATIC,
	// This is used to denote a link that does not change the configuration of the robot when it moves. 
	// This can include grippers, wheels and cameras
	TOOL;

}
