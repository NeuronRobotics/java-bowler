package com.neuronrobotics.sdk.pid;

import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.ByteList;

public class PIDConfiguration {
	private int group=0;
	private boolean enabled=false;
	private boolean inverted=false;
	private boolean async=false;
	private double KP=1;
	private double KI=0;
	private double KD=0;
	public PIDConfiguration(){
		
	}
	public PIDConfiguration(int group,boolean enabled,boolean inverted,boolean async,double KP,double KI,double KD){
		setGroup(group);
		setEnabled(enabled);
		setInverted(inverted);
		setAsync(async);
		setKP(KP);
		setKI(KI);
		setKD(KD);
	}
	public PIDConfiguration(BowlerDatagram conf) {
		setGroup(   conf.getData().get(0));
		setEnabled( conf.getData().get(1)>0);
		setInverted(conf.getData().get(2)>0);
		setAsync(   conf.getData().get(3)>0);
		setKP(((double)ByteList.convertToInt(conf.getData().getBytes(4, 4),false))/100);
		setKI(((double)ByteList.convertToInt(conf.getData().getBytes(8, 4),false))/100);
		setKD(((double)ByteList.convertToInt(conf.getData().getBytes(12, 4),false))/100);
	}
	@Override
	public String toString(){
		String s="PID configuration group #"+getGroup();
		s+="\n\tConstants: P="+getKP()+" I="+getKI()+" D="+getKD();
		s+="\n\tEnabled = "+isEnabled();
		s+="\n\tAsync = "+isAsync();
		s+="\n\tInverted = "+isInverted();
		return s;
	}
	public void setGroup(int group) {
		this.group = group;
	}
	public int getGroup() {
		return group;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setInverted(boolean inverted) {
		this.inverted = inverted;
	}
	public boolean isInverted() {
		return inverted;
	}
	public void setAsync(boolean async) {
		this.async = async;
	}
	public boolean isAsync() {
		return async;
	}
	public void setKP(double kP) {
		KP = kP;
	}
	public double getKP() {
		return KP;
	}
	public void setKI(double kI) {
		KI = kI;
	}
	public double getKI() {
		return KI;
	}
	public void setKD(double kD) {
		KD = kD;
	}
	public double getKD() {
		return KD;
	}
}
