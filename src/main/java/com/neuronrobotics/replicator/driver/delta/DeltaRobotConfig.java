package com.neuronrobotics.replicator.driver.delta;

// TODO: Auto-generated Javadoc
/**
 * The Class DeltaRobotConfig.
 */
public class DeltaRobotConfig {
	//Sample code from http://forums.trossenrobotics.com/tutorials/introduction-129/delta-robot-kinematics-3276/
	 // robot geometry
	 /** The e. */
	// (look at pics above for explanation)
	 private  double e ;     // end effector
	 
 	/** The f. */
 	private  double f;     // base
	 
 	/** The re. */
 	private  double re ;
	 
 	/** The rf. */
 	private  double rf;
	
	/**
	 * Instantiates a new delta robot config.
	 *
	 * @param e the e
	 * @param f the f
	 * @param re the re
	 * @param rf the rf
	 */
	public  DeltaRobotConfig(double e, double f, double re, double rf){
		 setE(e);
		 setF(f);
		 setRe(re);
		 setRf(rf);
	}
	
	/**
	 * Instantiates a new delta robot config.
	 *
	 * @param config the config
	 */
	public DeltaRobotConfig(DeltaRobotConfig config) {
		setE(config.getE());
		 setF(config.getF());
		 setRe(config.getRe());
		 setRf(config.getRf());
	}
	
	/**
	 * Sets the e.
	 *
	 * @param e the new e
	 */
	private void setE(double e) {
		this.e = e;
	}

	/**
	 * Gets the e.
	 *
	 * @return the e
	 */
	public double getE() {
		return e;
	}

	/**
	 * Sets the f.
	 *
	 * @param f the new f
	 */
	private void setF(double f) {
		this.f = f;
	}

	/**
	 * Gets the f.
	 *
	 * @return the f
	 */
	public double getF() {
		return f;
	}

	/**
	 * Sets the re.
	 *
	 * @param re the new re
	 */
	private void setRe(double re) {
		this.re = re;
	}

	/**
	 * Gets the re.
	 *
	 * @return the re
	 */
	public double getRe() {
		return re;
	}

	/**
	 * Sets the rf.
	 *
	 * @param rf the new rf
	 */
	private void setRf(double rf) {
		this.rf = rf;
	}

	/**
	 * Gets the rf.
	 *
	 * @return the rf
	 */
	public double getRf() {
		return rf;
	}
}
