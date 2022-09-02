package com.neuronrobotics.sdk.pid;

import java.util.ArrayList;
import java.util.HashMap;

import com.neuronrobotics.sdk.addons.kinematics.IHardwareSyncPulseProvider;
import com.neuronrobotics.sdk.addons.kinematics.IHardwareSyncPulseReciver;
import com.neuronrobotics.sdk.common.BowlerAbstractCommand;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.InvalidConnectionException;
import com.neuronrobotics.sdk.common.InvalidResponseException;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.NoConnectionAvailableException;
import com.neuronrobotics.sdk.util.ThreadUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class VirtualGenericPIDDevice.
 */
public class VirtualGenericPIDDevice extends GenericPIDDevice  implements IHardwareSyncPulseProvider{

	/** The Constant threadTime. */
	private static final long threadTime = 10;

	/** The drive threads. */
	private HashMap<PIDConfiguration, InterpolationEngine> interpolationEngines = new HashMap<>();

	/** The configs. */
	private HashMap<Integer, PIDConfiguration> configs = new HashMap<>();

	/** The P dconfigs. */
	private ArrayList<PDVelocityConfiguration> PDconfigs = new ArrayList<PDVelocityConfiguration>();
	
	/** The sync. */
	SyncThread sync = new SyncThread();

	/** The max ticks per second. */
	private double maxTicksPerSecond;

	/** The num channels. */
	private int numChannels = 24;

	private float[] backs;

	private String myVirtualDevName;

	/**
	 * Instantiates a new virtual generic pid device.
	 * @param myVirtualDevName 
	 */
	public VirtualGenericPIDDevice(String myVirtualDevName) {
		this(1000000,myVirtualDevName);
	}

	/**
	 * Instantiates a new virtual generic pid device.
	 *
	 * @param maxTicksPerSecond the max ticks per second
	 * @param myVirtualDevName2 
	 */
	public VirtualGenericPIDDevice(double maxTicksPerSecond, String myVirtualDevName) {
		this.setMaxTicksPerSecond(maxTicksPerSecond);
		if(myVirtualDevName == null)
			throw new RuntimeException("Name of virtual device can not be null");
		this.myVirtualDevName = myVirtualDevName;
		getImplementation().setChannelCount(new Integer(numChannels));
		GetAllPIDPosition();
		for (int i = 0; i < numChannels; i++) {
			configs.put(i, new PIDConfiguration());
			PDconfigs.add(new PDVelocityConfiguration());
		}

		sync.start();
		//new RuntimeException("Instantiation of VirtualGenericPIDDevice "+myVirtualDevName).printStackTrace();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.neuronrobotics.sdk.pid.GenericPIDDevice#ConfigurePDVelovityController(com
	 * .neuronrobotics.sdk.pid.PDVelocityConfiguration)
	 */
	@Override
	public boolean ConfigurePDVelovityController(PDVelocityConfiguration config) {
		PDconfigs.set(config.getGroup(), config);

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.neuronrobotics.sdk.pid.GenericPIDDevice#getPDVelocityConfiguration(int)
	 */
	@Override
	public PDVelocityConfiguration getPDVelocityConfiguration(int group) {
		return PDconfigs.get(group);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.pid.GenericPIDDevice#ConfigurePIDController(com.
	 * neuronrobotics.sdk.pid.PIDConfiguration)
	 */
	public boolean ConfigurePIDController(PIDConfiguration config) {
		configs.put(config.getGroup(), config);

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.pid.GenericPIDDevice#getPIDConfiguration(int)
	 */
	public PIDConfiguration getPIDConfiguration(int group) {
		return configs.get(group);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractDevice#getNamespaces()
	 */
	@Override
	public ArrayList<String> getNamespaces() {
		ArrayList<String> s = new ArrayList<String>();
		s.add("bcs.pid.*");
		return s;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.pid.GenericPIDDevice#killAllPidGroups()
	 */
	@Override
	public boolean killAllPidGroups() {
		for (PIDConfiguration c : configs.values())
			c.setEnabled(false);
		return true;
	}

	/**
	 * since there is no connection, this is an easy to nip off com functionality.
	 *
	 * @param command the command
	 * @return the bowler datagram
	 * @throws NoConnectionAvailableException the no connection available exception
	 * @throws InvalidResponseException       the invalid response exception
	 */
	@Override
	public BowlerDatagram send(BowlerAbstractCommand command)
			throws NoConnectionAvailableException, InvalidResponseException {
		RuntimeException r = new RuntimeException("This method is never supposed to be called in the virtual PID");
		r.printStackTrace();
		throw r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.pid.GenericPIDDevice#ResetPIDChannel(int, int)
	 */
	@Override
	public boolean ResetPIDChannel(int group, float valueToSetCurrentTo) {
		getDriveThread(group).ResetEncoder(valueToSetCurrentTo);
		float val = GetPIDPosition(group);
		firePIDResetEvent(group, val);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.pid.GenericPIDDevice#SetPIDSetPoint(int, int,
	 * double)
	 */
	@Override
	public boolean SetPIDSetPoint(int group, float setpoint, double seconds) {
		// new RuntimeException("Virtual setpoint, group="+group+"
		// setpoint="+setpoint).printStackTrace();;
		//sync.setPause(true);
		getDriveThread(group).StartLinearMotion(setpoint, seconds);
		//sync.setPause(false);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.pid.GenericPIDDevice#SetPDVelocity(int, int,
	 * double)
	 */
	@Override
	public boolean SetPDVelocity(int group, int unitsPerSecond, double seconds) throws PIDCommandException {
		if (unitsPerSecond > getMaxTicksPerSecond())
			throw new RuntimeException("Saturated PID on channel: " + group + " Attempted Ticks Per Second: "
					+ unitsPerSecond + ", when max is" + getMaxTicksPerSecond() + " set: " + getMaxTicksPerSecond()
					+ " sec: " + seconds);
		if (unitsPerSecond < -getMaxTicksPerSecond())
			throw new RuntimeException("Saturated PID on channel: " + group + " Attempted Ticks Per Second: "
					+ unitsPerSecond + ", when max is" + getMaxTicksPerSecond() + " set: " + getMaxTicksPerSecond()
					+ " sec: " + seconds);
		if (seconds < 0.1 && seconds > -0.1) {
			// System.out.println("Setting virtual velocity="+unitsPerSecond);
			getDriveThread(group).SetVelocity(unitsPerSecond);
		} else {
			SetPIDInterpolatedVelocity(group, unitsPerSecond, seconds);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#
	 * flushPIDChannels
	 */
	@Override
	public void flushPIDChannels(double time) {
		float[] data = new float[getChannels().size()];
		for (int i = 0; i < data.length; i++) {
			data[i] = getPIDChannel(i).getCachedTargetValue();
		}
		Log.info("Flushing in " + time + "ms");
		SetAllPIDSetPoint(data, time);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.pid.GenericPIDDevice#SetAllPIDSetPoint(int[],
	 * double)
	 */
	@Override
	public boolean SetAllPIDSetPoint(float[] setpoints, double seconds) {
		//sync.setPause(true);
		for (int i = 0; i < setpoints.length; i++) {
			getDriveThread(i).StartLinearMotion(setpoints[i], seconds);
		}
		//sync.setPause(false);
		return true;
	}

	private InterpolationEngine getDriveThread(int i) {
		for (PIDConfiguration c : interpolationEngines.keySet()) {
			if (c.getGroup() == i) {
				return interpolationEngines.get(c);
			}
		}
		for (PIDConfiguration c : interpolationEngines.keySet()) {
			System.err.println(c);
		}

		throw new RuntimeException("Device is missing, id " + i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.pid.GenericPIDDevice#GetPIDPosition(int)
	 */
	@Override
	public float GetPIDPosition(int group) {
		// TODO Auto-generated method stub
		return (float) getDriveThread(group).getPosition();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractDevice#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.pid.GenericPIDDevice#GetAllPIDPosition()
	 */
	@Override
	public float[] GetAllPIDPosition() {
		if (backs == null) {
			backs = new float[numChannels];

			setChannels(new ArrayList<PIDChannel>());
			// lastPacketTime = new long[back.length];
			for (int i = 0; i < backs.length; i++) {
				backs[i] = 0;
				PIDChannel c = new PIDChannel(this, i);
				c.setCachedTargetValue(backs[i]);
				getChannels().add(c);
				PIDConfiguration conf = new PIDConfiguration();
				conf.setGroup(i);
				conf.setEnabled(true);
				InterpolationEngine d = new InterpolationEngine();
				interpolationEngines.put(conf, d);
				configs.put(i, conf);
			}
		}
		for (int i = 0; i < backs.length; i++)
			backs[i] = GetPIDPosition(i);
		return backs;
	}

	/**
	 * Sets the max ticks per second.
	 *
	 * @param maxTicksPerSecond the new max ticks per second
	 */
	public void setMaxTicksPerSecond(double maxTicksPerSecond) {
		this.maxTicksPerSecond = maxTicksPerSecond;
	}

	/**
	 * Gets the max ticks per second.
	 *
	 * @return the max ticks per second
	 */
	public double getMaxTicksPerSecond() {
		return maxTicksPerSecond;
	}

	/**
	 * This class is designed to simulate a wheel driveing with a perfect
	 * controller.
	 *
	 * @author hephaestus
	 */
	private class SyncThread extends Thread {

		/** The pause. */
		private boolean sync = false;

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			setName("Bowler Platform Virtual PID sync thread");
			PIDEvent e= 	new PIDEvent();
			long time;
			while (true) {
				try {
					Thread.sleep(threadTime);
				} catch (InterruptedException ex) {
				}
//				while (isPause()) {
//					isPaused = true;
//					ThreadUtil.wait(1);
//				}
//				isPaused = false;
				sync=false;
				time = System.currentTimeMillis();
				for (PIDConfiguration key : interpolationEngines.keySet()) {
					InterpolationEngine dr = interpolationEngines.get(key);
					if (key.isEnabled()) {
						if (dr.update()) {
							try {
								e.set(key.getGroup(), (float) dr.getTicks(), time, 0);
								firePIDEvent(e);
								sync=true;
							} catch (NullPointerException ex) {
								// initialization issue, let it work itself out
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}else {
						System.err.println("Virtual Device "+key.getGroup()+" is disabled");
					}
				}
				if(sync)
					doSync();
			}
		}

//		/**
//		 * Checks if is pause.
//		 *
//		 * @return true, if is pause
//		 */
//		public boolean isPause() {
//			return pause;
//		}

		/**
		 * Sets the pause.
		 *
		 * @param pause the new pause
		 */
//		public void setPause(boolean pause) {
//			if (pause)
//				isPaused = false;
//			this.pause = pause;
////			while (!isPaused) {
////				try {
////					Thread.sleep(threadTime);
////				} catch (InterruptedException e) {
////				}
////			}
//		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neuronrobotics.sdk.pid.GenericPIDDevice#connect()
	 */
	@Override
	public boolean connect() {
		fireConnectEvent();
		return true;
	}

	/**
	 * This method tells the connection object to disconnect its pipes and close out
	 * the connection. Once this is called, it is safe to remove your device.
	 */
	@Override
	public void disconnect() {
		fireDisconnectEvent();

	}

}
