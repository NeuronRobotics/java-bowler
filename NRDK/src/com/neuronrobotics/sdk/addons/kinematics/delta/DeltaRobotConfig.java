package com.neuronrobotics.sdk.addons.kinematics.delta;

public class DeltaRobotConfig {
	//Sample code from http://forums.trossenrobotics.com/tutorials/introduction-129/delta-robot-kinematics-3276/
	 // robot geometry
	 // (look at pics above for explanation)
	 private  double e = 115.0;     // end effector
	 private  double f = 457.3;     // base
	 private  double re = 232.0;
	 private  double rf = 112.0;
	public  DeltaRobotConfig(double e, double f, double re, double rf){
		 setE(e);
		 setF(f);
		 setRe(re);
		 setRf(rf);
	}
	public DeltaRobotConfig(DeltaRobotConfig config) {
		setE(config.getE());
		 setF(config.getF());
		 setRe(config.getRe());
		 setRf(config.getRf());
	}
	private void setE(double e) {
		this.e = e;
	}

	public double getE() {
		return e;
	}

	private void setF(double f) {
		this.f = f;
	}

	public double getF() {
		return f;
	}

	private void setRe(double re) {
		this.re = re;
	}

	public double getRe() {
		return re;
	}

	private void setRf(double rf) {
		this.rf = rf;
	}

	public double getRf() {
		return rf;
	}
}
