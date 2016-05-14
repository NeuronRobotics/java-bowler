package com.neuronrobotics.sdk.addons.kinematics.gcodebridge;

import com.neuronrobotics.sdk.addons.kinematics.AbstractPrismaticLink;
import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration;

public class GcodePrismatic extends AbstractPrismaticLink {
	private GcodeDevice device;
	private String axis = "";
	double value =0;
	public GcodePrismatic(LinkConfiguration conf, GcodeDevice device, String linkAxis) {
		super(conf);
		// TODO Auto-generated constructor stub
		this.device = device;
		axis=linkAxis;
	}

	@Override
	public void cacheTargetValueDevice() {
		//value
	}

	@Override
	public void flushDevice(double time) {
		String[] currentPosStr = device.runLine("M114").split(" ");// get the current position
		for(String s:currentPosStr){
			if(s.contains(getAxis())){
				String [] parts = s.split(":");
				value = Double.parseDouble(parts[1]);
			}
		}
		double distance = getTargetValue()-value;
		if(distance !=0){
			int feedrate = (int)(distance/(time/60));//mm/min
			device.runLine("G1 "+getAxis()+""+getTargetValue()+" F"+feedrate);
		}
	}

	@Override
	public void flushAllDevice(double time) {
		device.flush(time);
	}

	@Override
	public double getCurrentPosition() {
		// TODO Auto-generated method stub
		return  value;
	}

	public String getAxis() {
		return axis;
	}

	public void setAxis(String axis) {
		this.axis = axis;
	}

}
