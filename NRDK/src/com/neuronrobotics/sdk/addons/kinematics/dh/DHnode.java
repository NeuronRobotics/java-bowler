package com.neuronrobotics.sdk.addons.kinematics.dh;

import com.neuronrobotics.sdk.addons.kinematics.AbstractRotoryLink;

public class DHnode extends AbstractTransform{
	private AbstractRotoryLink link;
	private double D, R, Alpha;
	/**
	 * 
	 * @param D 	offset along previous z to the common normal
	 * @param R		length of the common normal (aka a, but if using this notation, do not confuse with α). Assuming a revolute joint, this is the radius about previous z.
	 * @param Alpha angle about common normal, from old z axis to new z axis
	 */
	public DHnode (AbstractRotoryLink link,double D,double R, double Alpha){
		this.link=link;
		this.D=D;
		this.R=R;
		this.Alpha=Alpha;
	}
}
