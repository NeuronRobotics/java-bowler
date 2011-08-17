package com.neuronrobotics.sdk.addons.kinematics;

public class MockRotoryLink extends AbstractRotoryLink {
	int val=0;
	public MockRotoryLink(int home, int lowerLimit, int upperLimit, double scale) {
		super(home, lowerLimit, upperLimit, scale);
		setHome(0);
		setLowerLimit(-355);
		setUpperLimit(355);
		setScale(Math.PI/180);
		setTargetValue(35);
	}

	@Override
	public void cacheTargetValue() {
		val=getTargetValue();
		System.out.println("Cacheing value="+val);
	}

	@Override
	public void flush(double time) {
		val=getTargetValue();
		System.out.println("Flushing value="+val);
	}

	@Override
	public int getCurrentPosition() {
		// TODO Auto-generated method stub
		return 35;
	}

	@Override
	public void flushAll(double time) {
		// TODO Auto-generated method stub
		val=getTargetValue();
		System.out.println("Flushing all Values");
	}

}
