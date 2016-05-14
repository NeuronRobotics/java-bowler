package com.neuronrobotics.sdk.addons.kinematics.gcodebridge;

import com.neuronrobotics.sdk.addons.kinematics.AbstractLink;
import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration;

public class GCodeHeater extends AbstractLink  implements IGCodeChannel {

	private GcodeDevice device;
	private String axis = "";
	private double value =0;
	public GCodeHeater(LinkConfiguration conf, String gcodeAxis,GcodeDevice device) {
		super(conf);
		// TODO Auto-generated constructor stub
		this.axis = gcodeAxis;
		this.device = device;
	}

	@Override
	public void cacheTargetValueDevice() {
		// TODO Auto-generated method stub

	}

	@Override
	public void flushDevice(double time) {
		if(axis.contains("B")){
			device.runLine("M104 S"+getTargetValue());
		}
		if(axis.contains("T")){
			device.runLine("M140 S"+getTargetValue());
		}
	}

	@Override
	public void flushAllDevice(double time) {
		device.flush(time);
	}

	@Override
	public double getCurrentPosition() {
		// TODO Auto-generated method stub
		return value;
	}

	@Override
	public String getAxis() {
		// TODO Auto-generated method stub
		return axis;
	}

	@Override
	public void setValue(double value) {
		this.value=value;
	}

}
