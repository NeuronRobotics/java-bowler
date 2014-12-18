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
	private int latch=0;
	private boolean useLatch=false;
	private boolean stopOnIndex=false;
	private double up=0;
	private double low=0;
	private double hStop=0;
	public PIDConfiguration(){
		
	}
	
	public PIDConfiguration(int channel){
		group=channel;
	}

	/**
	 * 
	 * @param group This is the PID group this configuration object represents
	 * @param enabled True if the controller is running, false otherwise
	 * @param inverted This inverts the output value. Set true if the controller diverges
	 * @param async sets the flag to send this channels async values upstream
	 * @param KP Proportional constant
	 * @param KI Integral constant
	 * @param KD Derivative constant
	 * @param latch The value to latch into the PID controller if the home switch is hit (encoder only, not used in analog PID)
	 * @param useLatch Use the value to latch into the PID controller if the home switch is hit (encoder only, not used in analog PID)
	 * @param stopOnLatch Set the setpoint of the controller to current if home switch is hit (encoder only, not used in analog PID)
	 */
	public PIDConfiguration(int group,boolean enabled,boolean inverted,boolean async,double KP,double KI,double KD, double latch, boolean useLatch, boolean stopOnLatch, double up, double low,double hStop){
		setGroup(group);
		setEnabled(enabled);
		setInverted(inverted);
		setAsync(async);
		setKP(KP);
		setKI(KI);
		setKD(KD);
		setIndexLatch((int)latch);
		setUseLatch(useLatch);
		setStopOnIndex(stopOnLatch);
		setUpperHystersys(up);
		setLowerHystersys(low);
		setHystersysStop(hStop);
		
	}
	
	public PIDConfiguration(int group,
			boolean enabled,
			boolean inverted,
			boolean async,
			double KP,double KI,double KD, 
			double latch, boolean useLatch, boolean stopOnLatch) {
		
		this(group,enabled,inverted,async,KP,KI,KD,latch,useLatch,stopOnLatch,0,0,0);
	}
	
	/**
	 * Used to parse a PID configuration out of a PID packet
	 * @param conf
	 */

	public PIDConfiguration(BowlerDatagram conf) {
		setGroup(   conf.getData().get(0));
		setEnabled( conf.getData().get(1)>0);
		setInverted(conf.getData().get(2)>0);
		setAsync(   conf.getData().get(3)>0);
		setKP(((double)ByteList.convertToInt(conf.getData().getBytes(4, 4),false))/100);
		setKI(((double)ByteList.convertToInt(conf.getData().getBytes(8, 4),false))/100);
		setKD(((double)ByteList.convertToInt(conf.getData().getBytes(12, 4),false))/100);
		try{
			setIndexLatch(((int)ByteList.convertToInt(conf.getData().getBytes(16, 4),true)));
			setUseLatch(conf.getData().getBytes(20, 1)[0]>0);
			setStopOnIndex(conf.getData().getBytes(21, 1)[0]>0);
			
			setHystersysStop(((double)ByteList.convertToInt(conf.getData().getBytes(22, 4),true))/1000);
			setUpperHystersys(((double)ByteList.convertToInt(conf.getData().getBytes(26, 4),true))/1000);
			setLowerHystersys(((double)ByteList.convertToInt(conf.getData().getBytes(30, 4),true))/1000);
			
		}catch(Exception e){
			System.err.println("No latch value sent");
		}
	}
	
	
	/**
	 * Used to parse a PID configuration out of a PID packet
	 * @param conf
	 */

	public PIDConfiguration(Object [] args) {
		setGroup((Integer) args[0]);
		setEnabled((Integer) args[1]==1?true:false);
		setInverted((Integer) args[2]==1?true:false);
		setAsync((Integer) args[3]==1?true:false);
		setKP((Double) args[4]);
		setKI((Double) args[5]);
		setKD((Double) args[6]);
		setIndexLatch((Integer) args[7]);
		setUseLatch((Integer) args[8]==1?true:false);
		setStopOnIndex((Integer) args[9]==1?true:false);
		setHystersysStop((Double) args[10]);
		setUpperHystersys((Double) args[11]);
		setLowerHystersys((Double) args[12]);

	}
	

	@Override
	public String toString(){
		String s="PID configuration group #"+getGroup();
		s+="\n\tConstants: P="+getKP()+" I="+getKI()+" D="+getKD();
		s+="\n\tEnabled = "+isEnabled();
		s+="\n\tAsync = "+isAsync();
		s+="\n\tInverted = "+isInverted();
		s+="\n\tUse Latch  "+ isUseLatch();
		s+="\n\tStop on Index  "+ isStopOnIndex();
		s+="\n\tLatch value "+getIndexLatch();
		s+="\n\tCenter Stop "+getHystersysStop();
		s+="\n\tUpper Hysterysys "+getUpperHystersys();
		s+="\n\tLower Hysterysys "+getLowerHystersys();
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
	public double getIndexLatch() {
		return latch;
	}
	public void setIndexLatch(double latch) {
		this.latch=(int) latch;
	}
	public void setUseLatch(boolean useLatch) {
		this.useLatch = useLatch;
	}
	public boolean isUseLatch() {
		return useLatch;
	}
	public void setStopOnIndex(boolean stopOnIndex) {
		this.stopOnIndex = stopOnIndex;
	}
	public boolean isStopOnIndex() {
		return stopOnIndex;
	}
	public Object[] getArgs() {
		Object[] args = new Object[]{
		group,
		enabled?1:0,
		inverted?1:0,
		async?1:0,
		KP,
		KI,
		KD,
		latch,
		useLatch?1:0,
		stopOnIndex?1:0,
		hStop,
		up,
		low				
		};
		return args;
	}
	
	public double getUpperHystersys(){
		return up;
	}

	public void setUpperHystersys(double up) {
		this.up = up;		
	}
	public double getLowerHystersys(){
		return low;
	}

	public void setLowerHystersys(double low) {
		this.low = low;			
	}
	public double getHystersysStop(){
		return hStop;
	}

	public void setHystersysStop(double hStop) {
		this.hStop = hStop;		
	}
}
