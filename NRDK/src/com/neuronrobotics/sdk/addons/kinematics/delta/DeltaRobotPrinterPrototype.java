package com.neuronrobotics.sdk.addons.kinematics.delta;

import java.io.InputStream;

import com.neuronrobotics.sdk.addons.kinematics.AbstractKinematics;
import com.neuronrobotics.sdk.addons.kinematics.LinkFactory;
import com.neuronrobotics.sdk.addons.kinematics.math.Transform;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;
import com.neuronrobotics.sdk.dyio.DyIO;

public class DeltaRobotPrinterPrototype extends AbstractKinematics{
	DeltaRobotKinematics kinematics;
	
	//Configuration hard coded
	private  double e = 115.0;     // end effector
	private  double f = 457.3;     // base
	private  double re = 232.0;
	private  double rf = 112.0;
	
	static InputStream s = XmlFactory.getDefaultConfigurationStream("DeltaPrototype.xml");
	
	public DeltaRobotPrinterPrototype(DyIO dyio) {
		super(s,new LinkFactory( dyio));
		//parse out the extruder configs
		//parse delta robot configs
		
		kinematics = new DeltaRobotKinematics(new DeltaRobotConfig(e, f, re, rf));
		setNoFlush(true);
	}
	
	@Override
	public double[] setDesiredTaskSpaceTransform(Transform taskSpaceTransform, double seconds) throws Exception{
		double[] back = super.setDesiredTaskSpaceTransform(taskSpaceTransform, seconds);
		//Set the extruder value
		
		getFactory().flush(seconds);
		return back;
	}

	@Override
	public double[] inverseKinematics(Transform taskSpaceTransform)throws Exception {
		return kinematics.delta_calcInverse(taskSpaceTransform);
	}
	@Override
	public Transform forwardKinematics(double[] jointSpaceVector) {
		return kinematics.delta_calcForward(jointSpaceVector);
	}
}
