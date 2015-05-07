package com.neuronrobotics.sdk.addons.kinematics;

import java.io.*;
import java.math.*;

public class Mike_For {
	
	public static double[] Mike_For (int link1, int link2, int link3, double link1angle, double link2angle, double link3angle){
		
	// Convert angles to radians.
	link1angle = Math.toRadians(link1angle);
	link2angle = Math.toRadians(link2angle);
	link3angle = Math.toRadians(link3angle);
		
	// Link 1 tip position.	
	double link1x = link1*Math.cos(link1angle);
	double link1y = link1*Math.sin(link1angle);	
		
	// Link 2 tip position.	
	double link2x = link1x + link2*Math.cos(link1angle + link2angle);
	double link2y = link1y + link2*Math.sin(link1angle + link2angle);	
	
	// Link 3 tip position.	
	double link3x = link2x + link3*Math.cos(link1angle + link2angle+ link3angle);
	double link3y = link2y + link3*Math.sin(link1angle + link2angle+ link3angle);	
		
	// Return forward kinematics of current pose.
	double[] forward_kinematics = {link3x, link3y};
	return forward_kinematics;
	
	}
	
	public static double error (double[] desired, int link1, int link2, int link3, double link1angle, double link2angle, double link3angle){
	
		// Public function that takes in kinematic chain and desired point and returns the error.
		double error_x;
		double error_y;
		double[] for_kin;
		double total_error;
		
		for_kin = Mike_For.Mike_For(link1, link2, link3, link1angle, link2angle, link3angle);
		error_x = desired[0] - for_kin[0]; 
		error_y = desired[1] - for_kin[1]; 
		total_error = Math.sqrt(error_x * error_x + error_y * error_y);
	
	return total_error;
	
	}
	
}