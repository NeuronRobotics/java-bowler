package com.neuronrobotics.sdk.addons.kinematics;

public abstract class MockRotoryLink extends AbstractRotoryLink {
	int val=0;
	public MockRotoryLink(int home, int lowerLimit, int upperLimit, double scale) {
		super(home, lowerLimit, upperLimit, scale);
		setHome(0);
		setLowerLimit(-355);
		setUpperLimit(355);
		setScale(1.0);
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
		return (int)Math.toRadians(val);
	}

}
