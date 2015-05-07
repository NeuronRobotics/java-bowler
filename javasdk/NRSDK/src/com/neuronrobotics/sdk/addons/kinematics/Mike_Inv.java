package com.neuronrobotics.sdk.addons.kinematics;

import java.io.*;
import java.math.*;


public class Mike_Inv {
		public static void Mike_Inv(String[] args){
		
		// Set our constant values
		final double STEP = 0.1;	// Step size.
		final int MAX_ITERATIONS = 5000; // Max loop iteration number.
		final double TOLERANCE = 0.05; // Set tolerance boundary for system.
		final double MIN_DELTA_ERROR = 0.0001; // Designed to minimize unnecessary jitter by setting a minimum error value.
		
		// This variable must be greater than or equal to the step size or it will jump back and forth repeatedly.
		final double BOUNDARY_LIMIT = 0.5;  // Set a safety distance for how close the links should get to their link boundary limit.
		
		// Declare kinematic values, in cm and degrees.
		final int link1 = 50;
		final int link2 = 60;
		final int link3 = 70;
		double link1angle = 0;
		double link2angle = 0;
		double link3angle = 0;
		
		// Declare link boundary limits.
		final double LINK1_LOW = -180;
		final double LINK1_HIGH = 180;
		final double LINK2_LOW = -180;
		final double LINK2_HIGH = 180;
		final double LINK3_LOW = -180;
		final double LINK3_HIGH = 180;
	
		// Desired x, y coordinate.
		final double[] desired = {14.3228, -23.3636};
		
		// Array for forward kinematics value, choose arbitrary value to initialize.
		double[] for_kin = {1000, 1000};
		
		// Describe our error variables.	
		double forward_error; // Temporary error variables.
		double backward_error;
		double total_error = 1000;  // Initialize these variables at a high value so we don't accidentally trip this condition on the first runthrough.
		double prev_error = 1000;
		double error_store = 1000;  
		double link_temp_forward; // Temporary step angle variables.
		double link_temp_backward;
		double dist_to_low; // Distance to boundary condition variables.
		double dist_to_high;
		
		
		/* LOOP - This loop, and the entire code for that matter, can easily be soft-coded in order to account for an n number of links, but with
		 * the limited forward kinematics scope of this applet, I have chosen to hard code it to 3 links, in
		 * the 2d space.  */
		
		int count = 0;  //  Initialize count for loop.
		// Loop until the distance to the desired point is within our set tolerance or the max iteration value is reached.
		while(count < MAX_ITERATIONS && total_error > TOLERANCE && error_store >= MIN_DELTA_ERROR){
				
		/* Link 1 */
			// Step forward, calculate error.
			link_temp_forward = link1angle + STEP;
			forward_error = Mike_For.error(desired, link1, link2, link3, link_temp_forward, link2angle, link3angle);

			// Step backward, calculate error.
			link_temp_backward = link1angle - STEP;
			backward_error = Mike_For.error(desired, link1, link2, link3, link_temp_forward, link2angle, link3angle);
			
			// Calculate distance to link boundary conditions.
			dist_to_low = Math.abs(LINK1_LOW - link1angle);
			dist_to_high = Math.abs(LINK1_HIGH - link1angle);
			
			// If too close to boundary conditions, move link away from boundary condition, otherwise move link to minimize error. 
			if(dist_to_low > BOUNDARY_LIMIT && dist_to_high > BOUNDARY_LIMIT){
				// Compare errors, move link to minimize error.
				if(forward_error - backward_error <= 0){
					link1angle = link_temp_forward;
				}
				else{
					link1angle = link_temp_backward;
				}
			} // If at min boundary, move forward.
			else if(dist_to_low <= BOUNDARY_LIMIT && dist_to_low <= dist_to_high){
				link1angle = link_temp_forward;
				System.out.println("LINK1_LOW " + LINK1_LOW);
				System.out.println("link1angle " + link1angle);
				System.out.println("dist_to_low " + dist_to_low);
				System.out.println("Trip link 1 min Boundary Limit: " + count);
			} // If at max boundary, move backward.
			else if(dist_to_high <= BOUNDARY_LIMIT && dist_to_high <= dist_to_low){
				link1angle = link_temp_backward;
				System.out.println("Trip link 1 max Boundary Limit: " + count);
			}
			
			
		/* Link 2 */
			// Step forward, calculate error.
			link_temp_forward = link2angle + STEP;
			forward_error = Mike_For.error(desired, link1, link2, link3, link1angle, link_temp_forward, link3angle);

			// Step backward, calculate error.
			link_temp_backward = link2angle - STEP;
			backward_error = Mike_For.error(desired, link1, link2, link3, link1angle, link_temp_backward, link3angle);
			
			// Calculate distance to link boundary conditions.
			dist_to_low = Math.abs(LINK2_LOW - link2angle);
			dist_to_high = Math.abs(LINK2_HIGH - link2angle);
			
			// If too close to boundary conditions, move link away from boundary condition, otherwise move link to minimize error. 
			if(dist_to_low > BOUNDARY_LIMIT && dist_to_high > BOUNDARY_LIMIT){
				// Compare errors, move link to minimize error.
				if(forward_error - backward_error <= 0){
					link2angle = link_temp_forward;
				}
				else{
					link2angle = link_temp_backward;
				}
			} // If at min boundary, move forward.
			else if(dist_to_low <= BOUNDARY_LIMIT && dist_to_low <= dist_to_high){
				link2angle = link_temp_forward;
				System.out.println("Trip link 2 min Boundary Limit: " + count);
			} // If at max boundary, move backward.
			else if(dist_to_high <= BOUNDARY_LIMIT && dist_to_high <= dist_to_low){
				link2angle = link_temp_backward;
				System.out.println("Trip link 2 max Boundary Limit: " + count);
			}
				
			
		/* Link 3 */
			// Step forward, calculate error.
			link_temp_forward = link3angle + STEP;
			forward_error = Mike_For.error(desired, link1, link2, link3, link1angle, link2angle, link_temp_forward);

			// Step backward, calculate error.
			link_temp_backward = link3angle - STEP;
			backward_error = Mike_For.error(desired, link1, link2, link3, link1angle, link2angle, link_temp_backward);
		
			// Calculate distance to link boundary conditions.
			dist_to_low = Math.abs(LINK3_LOW - link3angle);
			dist_to_high = Math.abs(LINK3_HIGH - link3angle);
			
			// If too close to boundary conditions, move link away from boundary condition, otherwise move link to minimize error. 
			if(dist_to_low > BOUNDARY_LIMIT && dist_to_high > BOUNDARY_LIMIT){
				// Compare errors, move link to minimize error.
				if(forward_error - backward_error <= 0){
					link3angle = link_temp_forward;
				}
				else{
					link3angle = link_temp_backward;
				}
			} // If at min boundary, move forward.
			else if(dist_to_low <= BOUNDARY_LIMIT && dist_to_low <= dist_to_high){
				link3angle = link_temp_forward;
				System.out.println("Trip link 3 min Boundary Limit: " + count);
			} // If at max boundary, move backward.
			else if(dist_to_high <= BOUNDARY_LIMIT && dist_to_high <= dist_to_low){
				link3angle = link_temp_backward;
				System.out.println("Trip link 3 max Boundary Limit: " + count);
			}
			
			// Increment count.
			count ++;
			
			// Update error values.
			prev_error = total_error; // Store previous error value before updating the total_error for current loop.
			total_error = Mike_For.error(desired, link1, link2, link3, link1angle, link2angle, link_temp_backward);
			error_store = Math.abs(prev_error - total_error);

		}
		
		for_kin = Mike_For.Mike_For(link1, link2, link3, link1angle, link2angle, link3angle);
		
		// Print out system report when loop is complete and inverse kinematics solution is found.
		System.out.println("Kinematic Report");
		System.out.println("------------------");
		System.out.println(count + " iterations performed.");
		System.out.format("Final Link 1 angle: " + "%.2f%n", link1angle);
		System.out.format("Final Link 2 angle: " + "%.2f%n", link2angle);
		System.out.format("Final Link 3 angle: " + "%.2f%n", link3angle);
	
		System.out.format("Error_store: " + "%.5f%n", error_store);
		System.out.format("Forward x position: " + "%.3f%n", for_kin[0]);
		System.out.format("Forward y position: " + "%.3f%n", for_kin[1]);
	}

}