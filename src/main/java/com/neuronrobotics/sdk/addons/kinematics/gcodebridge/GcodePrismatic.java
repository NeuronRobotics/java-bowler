package com.neuronrobotics.sdk.addons.kinematics.gcodebridge;

import com.neuronrobotics.sdk.addons.kinematics.AbstractPrismaticLink;
import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration;

public class GcodePrismatic extends AbstractPrismaticLink implements IGCodeChannel {
	private GcodeDevice device;
	private String axis = "";
	private double value =0;
	public GcodePrismatic(LinkConfiguration conf, GcodeDevice device, String linkAxis) {
		super(conf);
		// TODO Auto-generated constructor stub
		this.device = device;
		axis=linkAxis;
		loadCurrent();
	}

	@Override
	public void cacheTargetValueDevice() {
		//value
	}
	
	private void loadCurrent(){
		String m114 =device.runLine("M114");
		String[] currentPosStr = m114.split("Count")[0].split(" ");// get the current position
		//System.out.println("Fush with current = "+m114);
		for(String s:currentPosStr){
			if(s.contains(getAxis())){
				String [] parts = s.split(":");
				//System.out.println("Found axis = "+s);
				setValue(Double.parseDouble(parts[1]));
				return;
			}
		}
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
