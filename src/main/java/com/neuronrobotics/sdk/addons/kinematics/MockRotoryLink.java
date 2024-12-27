package com.neuronrobotics.sdk.addons.kinematics;

//  Auto-generated Javadoc
/**
 * The Class MockRotoryLink.
 */
public class MockRotoryLink extends AbstractRotoryLink {
	
	/** The val. */
	double val=0;
	
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
		conf.setPauseEvents(true);
		conf.setDeviceTheoreticalMax(Integer.MAX_VALUE);
		conf.setDeviceTheoreticalMin(Integer.MIN_VALUE);
		conf.setPauseEvents(false);
		}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractLink#cacheTargetValueDevice()
	 */
	@Override
	public void cacheTargetValueDevice() {
		val=getTargetValue();
		//com.neuronrobotics.sdk.common.Log.error("Cacheing value="+val);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractLink#flush(double)
	 */
	@Override
	public void flushDevice(double time) {
		val=getTargetValue();
		//com.neuronrobotics.sdk.common.Log.error("Flushing value="+val);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractLink#getCurrentPosition()
	 */
	@Override
	public double getCurrentPosition() {
		// Auto-generated method stub
		return 35;
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.AbstractLink#flushAll(double)
	 */
	@Override
	public void flushAllDevice(double time) {
		// Auto-generated method stub
		val=getTargetValue();
		//com.neuronrobotics.sdk.common.Log.error("Flushing all Values");
	}

}
