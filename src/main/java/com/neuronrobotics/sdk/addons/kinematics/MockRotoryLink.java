package com.neuronrobotics.sdk.addons.kinematics;

// TODO: Auto-generated Javadoc
/**
 * The Class MockRotoryLink.
 */
public class MockRotoryLink extends AbstractRotoryLink {
	
	/** The val. */
	int val=0;
	
	/**
	 * Instantiates a new mock rotory link.
	 *
	 * @param conf the conf
	 */
	public MockRotoryLink(LinkConfiguration conf) {
		super(conf);
		setHome(0);
		setLowerLimit(-355);
		setUpperLimit(355);
		setScale(Math.PI/180);
		setTargetValue(35);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractLink#cacheTargetValueDevice()
	 */
	@Override
	public void cacheTargetValueDevice() {
		val=getTargetValue();
		System.out.println("Cacheing value="+val);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractLink#flush(double)
	 */
	@Override
	public void flushDevice(double time) {
		val=getTargetValue();
		System.out.println("Flushing value="+val);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractLink#getCurrentPosition()
	 */
	@Override
	public int getCurrentPosition() {
		// TODO Auto-generated method stub
		return 35;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractLink#flushAll(double)
	 */
	@Override
	public void flushAllDevice(double time) {
		// TODO Auto-generated method stub
		val=getTargetValue();
		System.out.println("Flushing all Values");
	}

}
