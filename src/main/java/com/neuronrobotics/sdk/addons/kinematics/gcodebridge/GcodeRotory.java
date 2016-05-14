package com.neuronrobotics.sdk.addons.kinematics.gcodebridge;

import com.neuronrobotics.sdk.addons.kinematics.AbstractPrismaticLink;
import com.neuronrobotics.sdk.addons.kinematics.AbstractRotoryLink;
import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration;

public class GcodeRotory extends AbstractRotoryLink implements IGCodeChannel {
	private GcodeDevice device;
	private String axis = "";
	private double value =0;
	public GcodeRotory(LinkConfiguration conf, GcodeDevice device, String linkAxis) {
		super(conf);
		// TODO Auto-generated constructor stub
		this.device = device;
		axis=linkAxis;
		//loadCurrent();
	}

	@Override
	public void cacheTargetValueDevice() {
		//value
	}
	
	private void loadCurrent(){
		device.loadCurrent();
	}

	@Override
	public void flushDevice(double time) {
		loadCurrent();
		
		double distance = getTargetValue()-getValue();
		if(distance !=0){
			int feedrate = (int)Math.abs((distance/(time/60)));//mm/min
			device.runLine("G1 "+getAxis()+""+getTargetValue()+" F"+feedrate);
		}
	}

	@Override
	public void flushAllDevice(double time) {
		device.flush(time);
	}

	@Override
	public double getCurrentPosition() {

		return  getValue();
	}

	public String getAxis() {
		return axis;
	}

	public void setAxis(String axis) {
		this.axis = axis;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
		fireLinkListener( value);
	}

}
