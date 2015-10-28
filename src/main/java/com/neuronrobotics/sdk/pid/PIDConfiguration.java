package com.neuronrobotics.sdk.pid;

import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.ByteList;

// TODO: Auto-generated Javadoc
/**
 * The Class PIDConfiguration.
 */
public class PIDConfiguration {
	
	/** The group. */
	private int group=0;
	
	/** The enabled. */
	private boolean enabled=false;
	
	/** The inverted. */
	private boolean inverted=false;
	
	/** The async. */
	private boolean async=false;
	
	/** The kp. */
	private double KP=.1;
	
	/** The ki. */
	private double KI=0;
	
	/** The kd. */
	private double KD=0;
	
	/** The latch. */
	private int latch=0;
	
	/** The use latch. */
	private boolean useLatch=false;
	
	/** The stop on index. */
	private boolean stopOnIndex=false;
	
	/** The up. */
	private double up=0;
	
	/** The low. */
	private double low=0;
	
	/** The h stop. */
	private double hStop=0;
	
	/**
	 * Instantiates a new PID configuration.
	 */
	public PIDConfiguration(){
		
	}
	
	/**
	 * Instantiates a new PID configuration.
	 *
	 * @param channel the channel
	 */
	public PIDConfiguration(int channel){
		group=channel;
	}

	/**
	 * Instantiates a new PID configuration.
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
	 * @param up the up
	 * @param low the low
	 * @param hStop the h stop
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
	
	/**
	 * Instantiates a new PID configuration.
	 *
	 * @param group the group
	 * @param enabled the enabled
	 * @param inverted the inverted
	 * @param async the async
	 * @param KP the kp
	 * @param KI the ki
	 * @param KD the kd
	 * @param latch the latch
	 * @param useLatch the use latch
	 * @param stopOnLatch the stop on latch
	 */
	public PIDConfiguration(int group,
			boolean enabled,
			boolean inverted,
			boolean async,
			double KP,double KI,double KD, 
			double latch, boolean useLatch, boolean stopOnLatch) {
		
		this(group,enabled,inverted,async,KP,KI,KD,latch,useLatch,stopOnLatch,0,0,0);
	}
	
	/**
	 * Used to parse a PID configuration out of a PID packet.
	 *
	 * @param conf the conf
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
	 * Used to parse a PID configuration out of a PID packet.
	 *
	 * @param args the args
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
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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
	
	/**
	 * Sets the group.
	 *
	 * @param group the new group
	 */
	public void setGroup(int group) {
		this.group = group;
	}
	
	/**
	 * Gets the group.
	 *
	 * @return the group
	 */
	public int getGroup() {
		return group;
	}
	
	/**
	 * Sets the enabled.
	 *
	 * @param enabled the new enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * Checks if is enabled.
	 *
	 * @return true, if is enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Sets the inverted.
	 *
	 * @param inverted the new inverted
	 */
	public void setInverted(boolean inverted) {
		this.inverted = inverted;
	}
	
	/**
	 * Checks if is inverted.
	 *
	 * @return true, if is inverted
	 */
	public boolean isInverted() {
		return inverted;
	}
	
	/**
	 * Sets the async.
	 *
	 * @param async the new async
	 */
	public void setAsync(boolean async) {
		this.async = async;
	}
	
	/**
	 * Checks if is async.
	 *
	 * @return true, if is async
	 */
	public boolean isAsync() {
		return async;
	}
	
	/**
	 * Sets the kp.
	 *
	 * @param kP the new kp
	 */
	public void setKP(double kP) {
		KP = kP;
	}
	
	/**
	 * Gets the kp.
	 *
	 * @return the kp
	 */
	public double getKP() {
		return KP;
	}
	
	/**
	 * Sets the ki.
	 *
	 * @param kI the new ki
	 */
	public void setKI(double kI) {
		KI = kI;
	}
	
	/**
	 * Gets the ki.
	 *
	 * @return the ki
	 */
	public double getKI() {
		return KI;
	}
	
	/**
	 * Sets the kd.
	 *
	 * @param kD the new kd
	 */
	public void setKD(double kD) {
		KD = kD;
	}
	
	/**
	 * Gets the kd.
	 *
	 * @return the kd
	 */
	public double getKD() {
		return KD;
	}
	
	/**
	 * Gets the index latch.
	 *
	 * @return the index latch
	 */
	public double getIndexLatch() {
		return latch;
	}
	
	/**
	 * Sets the index latch.
	 *
	 * @param latch the new index latch
	 */
	public void setIndexLatch(double latch) {
		this.latch=(int) latch;
	}
	
	/**
	 * Sets the use latch.
	 *
	 * @param useLatch the new use latch
	 */
	public void setUseLatch(boolean useLatch) {
		this.useLatch = useLatch;
	}
	
	/**
	 * Checks if is use latch.
	 *
	 * @return true, if is use latch
	 */
	public boolean isUseLatch() {
		return useLatch;
	}
	
	/**
	 * Sets the stop on index.
	 *
	 * @param stopOnIndex the new stop on index
	 */
	public void setStopOnIndex(boolean stopOnIndex) {
		this.stopOnIndex = stopOnIndex;
	}
	
	/**
	 * Checks if is stop on index.
	 *
	 * @return true, if is stop on index
	 */
	public boolean isStopOnIndex() {
		return stopOnIndex;
	}
	
	/**
	 * Gets the args.
	 *
	 * @return the args
	 */
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
	
	/**
	 * Gets the upper hystersys.
	 *
	 * @return the upper hystersys
	 */
	public double getUpperHystersys(){
		return up;
	}

	/**
	 * Sets the upper hystersys.
	 *
	 * @param up the new upper hystersys
	 */
	public void setUpperHystersys(double up) {
		this.up = up;		
	}
	
	/**
	 * Gets the lower hystersys.
	 *
	 * @return the lower hystersys
	 */
	public double getLowerHystersys(){
		return low;
	}

	/**
	 * Sets the lower hystersys.
	 *
	 * @param low the new lower hystersys
	 */
	public void setLowerHystersys(double low) {
		this.low = low;			
	}
	
	/**
	 * Gets the hystersys stop.
	 *
	 * @return the hystersys stop
	 */
	public double getHystersysStop(){
		return hStop;
	}

	/**
	 * Sets the hystersys stop.
	 *
	 * @param hStop the new hystersys stop
	 */
	public void setHystersysStop(double hStop) {
		this.hStop = hStop;		
	}
}
