package com.neuronrobotics.sdk.namespace.bcs.pid;

import com.neuronrobotics.sdk.common.BowlerDataType;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.common.MACAddress;
import com.neuronrobotics.sdk.common.RpcEncapsulation;
import com.neuronrobotics.sdk.common.device.server.BowlerAbstractDeviceServerNamespace;
import com.neuronrobotics.sdk.common.device.server.IBowlerCommandProcessor;
import com.neuronrobotics.sdk.pid.IPIDEventListener;
import com.neuronrobotics.sdk.pid.PDVelocityConfiguration;
import com.neuronrobotics.sdk.pid.PIDChannel;
import com.neuronrobotics.sdk.pid.PIDCommandException;
import com.neuronrobotics.sdk.pid.PIDConfiguration;


// TODO: Auto-generated Javadoc
/**
 * The Class PidDeviceServerNamespace.
 */
public class PidDeviceServerNamespace extends BowlerAbstractDeviceServerNamespace implements IPidControlNamespace {

	/** The device. */
	private IPidControlNamespace device;

	/**
	 * Instantiates a new pid device server namespace.
	 *
	 * @param addr the addr
	 * @param device the device
	 */
	public PidDeviceServerNamespace(MACAddress addr,IPidControlNamespace device ) {
		super(addr, "bcs.pid.*;1.0;;");
		this.device = device;
		rpc.add(new RpcEncapsulation(getNamespaceIndex(), 
				getNamespace() , 
				"apid", 
				BowlerMethod.GET, 
				new BowlerDataType[]{}, 
				BowlerMethod.POST, 
				new BowlerDataType[]{BowlerDataType.I32STR},
				new IBowlerCommandProcessor() {
					@Override
					public Object[] process(Object[] data) {
						int [] current = GetAllPIDPosition();
						Integer [] d = new Integer[current.length];
						for(int i=0;i<current.length;i++){
							d[i]=new Integer(current[i]);
						}
						return new Object[]{d};
					}
				}));//Name
		
		
		
		
		rpc.add(new RpcEncapsulation(getNamespaceIndex(), 
				getNamespace() , 
				"_pid", 
				BowlerMethod.GET, 
				new BowlerDataType[]{BowlerDataType.I08}, 
				BowlerMethod.POST, 
				new BowlerDataType[]{BowlerDataType.I08,BowlerDataType.I32},
				new IBowlerCommandProcessor() {
					@Override
					public Object[] process(Object[] data) {
						//anythign
						return new Object[]{data[0],new Integer(GetPIDPosition((Integer) data[0]))};
					}
				}));//Name
		
		
		
		rpc.add(new RpcEncapsulation(getNamespaceIndex(), 
				getNamespace() , 
				"cpid", 
				BowlerMethod.GET, 
				new BowlerDataType[]{BowlerDataType.I08}, 
				BowlerMethod.POST, 
				new BowlerDataType[]{	BowlerDataType.I08,
										BowlerDataType.I08,
										BowlerDataType.I08,
										BowlerDataType.I08,
										BowlerDataType.FIXED100,
										BowlerDataType.FIXED100,
										BowlerDataType.FIXED100,
										BowlerDataType.I32,
										BowlerDataType.I08,
										BowlerDataType.I08,
										BowlerDataType.FIXED1k,
										BowlerDataType.FIXED1k,
										BowlerDataType.FIXED1k
									},
				new IBowlerCommandProcessor() {
					@Override
					public Object[] process(Object[] data) {
						PIDConfiguration conf = getPIDConfiguration((Integer)data[0]);
						return conf.getArgs();
					}
				}));//Name
		rpc.add(new RpcEncapsulation(getNamespaceIndex(), 
				getNamespace() , 
				"cpdv", 
				BowlerMethod.GET, 
				new BowlerDataType[]{	BowlerDataType.I08}, 
				BowlerMethod.POST, 
				new BowlerDataType[]{	BowlerDataType.I08,
										BowlerDataType.FIXED100,
										BowlerDataType.FIXED100},
				new IBowlerCommandProcessor() {
					@Override
					public Object[] process(Object[] data) {
						PDVelocityConfiguration conf = getPDVelocityConfiguration((Integer)data[0]);
						return conf.getArgs();
					}
				}));//Name
		rpc.add(new RpcEncapsulation(getNamespaceIndex(), 
				getNamespace() , 
				"gpdc", 
				BowlerMethod.GET, 
				new BowlerDataType[]{}, 
				BowlerMethod.POST, 
				new BowlerDataType[]{BowlerDataType.I32},
				new IBowlerCommandProcessor() {
					@Override
					public Object[] process(Object[] data) {
						return new Object[]{new Integer(getPIDChannelCount())};
					}
				}));//Name
		rpc.add(new RpcEncapsulation(getNamespaceIndex(), 
				getNamespace() , 
				"apid", 
				BowlerMethod.POST, 
				new BowlerDataType[]{	BowlerDataType.I32,
										BowlerDataType.I32STR
										}, 
				BowlerMethod.STATUS, 
				new BowlerDataType[]{	BowlerDataType.I08,
										BowlerDataType.I08
					},
				new IBowlerCommandProcessor() {
					@Override
					public Object[] process(Object[] data) {
						Integer time = (Integer)data[0];
						Integer [] d = (Integer [])data[1];
						int [] current = new int[d.length];
						for(int i=0;i<current.length;i++){
							current[i] = d[i];
						}
						SetAllPIDSetPoint(current, time);
						return new Object[]{new Integer(66),new Integer(3)};
					}
				}));//Name
		rpc.add(new RpcEncapsulation(getNamespaceIndex(), 
				getNamespace() , 
				"_pid", 
				BowlerMethod.POST, 
				new BowlerDataType[]{	BowlerDataType.I08,
										BowlerDataType.I32,
										BowlerDataType.I32
									}, 
				BowlerMethod.STATUS, 
				new BowlerDataType[]{	BowlerDataType.I08,
										BowlerDataType.I08
										},
				new IBowlerCommandProcessor() {
					@Override
					public Object[] process(Object[] data) {
						SetPIDSetPoint((Integer)data[0], 
								(Integer)data[1], 
								(Integer)data[2]);
						
						return new Object[]{data[0],new Integer(5)};
					}
				}));//Name
		rpc.add(new RpcEncapsulation(getNamespaceIndex(), 
				getNamespace() , 
				"_vpd", 
				BowlerMethod.POST, 
				new BowlerDataType[]{	BowlerDataType.I08,
										BowlerDataType.I32,
										BowlerDataType.I32
				}, 
				BowlerMethod.STATUS, 
				new BowlerDataType[]{	BowlerDataType.I08,
										BowlerDataType.I08
				},
				new IBowlerCommandProcessor() {
					@Override
					public Object[] process(Object[] data) {
						try {
							SetPDVelocity((Integer)data[0], 
									(Integer)data[1], 
									(Integer)data[2]);
						} catch (PIDCommandException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return new Object[]{data[0],new Integer(66)};
						}
						
						return new Object[]{data[0],new Integer(4)};
					}
				}));//Name
		rpc.add(new RpcEncapsulation(getNamespaceIndex(), 
				getNamespace() , 
				"rpid", 
				BowlerMethod.POST, 
				new BowlerDataType[]{	BowlerDataType.I08,
										BowlerDataType.I32
				}, 
				BowlerMethod.STATUS, 
				new BowlerDataType[]{	BowlerDataType.I08,
										BowlerDataType.I08
				},
				new IBowlerCommandProcessor() {
					@Override
					public Object[] process(Object[] data) {
						ResetPIDChannel((Integer)data[0], (Integer)data[1]);
						return new Object[]{data[0],new Integer(6)};
					}
				}));//Name
		rpc.add(new RpcEncapsulation(getNamespaceIndex(), 
				getNamespace() , 
				"kpid", 
				BowlerMethod.CRITICAL, 
				new BowlerDataType[]{}, 
				BowlerMethod.STATUS, 
				new BowlerDataType[]{	BowlerDataType.I08,
							BowlerDataType.I08
				},
				new IBowlerCommandProcessor() {
					@Override
					public Object[] process(Object[] data) {
						killAllPidGroups();
						return new Object[]{new Integer(66),new Integer(0)};
					}
				}));//Name
		rpc.add(new RpcEncapsulation(getNamespaceIndex(), 
				getNamespace() , 
				"cpid", 
				BowlerMethod.CRITICAL, 
				new BowlerDataType[]{	BowlerDataType.I08,
										BowlerDataType.I08,
										BowlerDataType.I08,
										BowlerDataType.I08,
										BowlerDataType.FIXED100,
										BowlerDataType.FIXED100,
										BowlerDataType.FIXED100,
										BowlerDataType.I32,
										BowlerDataType.I08,
										BowlerDataType.I08,
										BowlerDataType.FIXED1k,
										BowlerDataType.FIXED1k,
										BowlerDataType.FIXED1k
				}, 
				BowlerMethod.STATUS, 
				new BowlerDataType[]{	BowlerDataType.I08,
					BowlerDataType.I08
				},
				new IBowlerCommandProcessor() {
					@Override
					public Object[] process(Object[] data) {
						PIDConfiguration conf = new PIDConfiguration(data);
						ConfigurePIDController(conf);
						Log.info("PID setting "+conf);
						return new Object[]{data[0],new Integer(1)};
					}
				}));//Name
		rpc.add(new RpcEncapsulation(getNamespaceIndex(), 
				getNamespace() , 
				"cpdv", 
				BowlerMethod.CRITICAL, 
				new BowlerDataType[]{	BowlerDataType.I08,
										BowlerDataType.FIXED100,
										BowlerDataType.FIXED100
				}, 
				BowlerMethod.STATUS, 
				new BowlerDataType[]{	BowlerDataType.I08,
										BowlerDataType.I08
				},
				new IBowlerCommandProcessor() {
					@Override
					public Object[] process(Object[] data) {
						PDVelocityConfiguration conf = new PDVelocityConfiguration(data);
						ConfigurePDVelovityController(conf);
						Log.info("VPD setting "+conf);
						return new Object[]{data[0],new Integer(2)};
					}
				}));//Name
//		rpc.add(new RpcEncapsulation(getNamespaceIndex(), 
//				getNamespace() , 
//				"acal", 
//				BowlerMethod.CRITICAL, 
//				new BowlerDataType[]{BowlerDataType.I08
//				}, 
//				BowlerMethod.STATUS, 
//				new BowlerDataType[]{	BowlerDataType.I08,
//										BowlerDataType.I08
//				},
//				new IBowlerCommandProcessor() {
//					@Override
//					public Object[] process(Object[] data) {
//						runOutputHysteresisCalibration((Integer)data[0]);
//						return new Object[]{new Integer(37),new Integer(0)};
//					}
//				}));//Name
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#ResetPIDChannel(int, int)
	 */
	@Override
	public boolean ResetPIDChannel(int group, int valueToSetCurrentTo) {
		return device.ResetPIDChannel(group, valueToSetCurrentTo);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#ConfigurePIDController(com.neuronrobotics.sdk.pid.PIDConfiguration)
	 */
	@Override
	public boolean ConfigurePIDController(PIDConfiguration config) {
		return device.ConfigurePIDController(config);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#getPIDConfiguration(int)
	 */
	@Override
	public PIDConfiguration getPIDConfiguration(int group) {
		return device.getPIDConfiguration(group);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#ConfigurePDVelovityController(com.neuronrobotics.sdk.pid.PDVelocityConfiguration)
	 */
	@Override
	public boolean ConfigurePDVelovityController(PDVelocityConfiguration config) {
		return device.ConfigurePDVelovityController(config);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#getPDVelocityConfiguration(int)
	 */
	@Override
	public PDVelocityConfiguration getPDVelocityConfiguration(int group) {
		return device.getPDVelocityConfiguration(group);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#getPIDChannelCount()
	 */
	@Override
	public int getPIDChannelCount() {
		return device.getPIDChannelCount();
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#SetPIDSetPoint(int, int, double)
	 */
	@Override
	public boolean SetPIDSetPoint(int group, int setpoint, double seconds) {
		return device.SetPIDSetPoint(group, setpoint, seconds);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#SetAllPIDSetPoint(int[], double)
	 */
	@Override
	public boolean SetAllPIDSetPoint(int[] setpoints, double seconds) {
		return device.SetAllPIDSetPoint(setpoints, seconds);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#GetPIDPosition(int)
	 */
	@Override
	public int GetPIDPosition(int group) {
		return device.GetPIDPosition(group);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#GetAllPIDPosition()
	 */
	@Override
	public int[] GetAllPIDPosition() {
		return device.GetAllPIDPosition();
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#addPIDEventListener(com.neuronrobotics.sdk.pid.IPIDEventListener)
	 */
	@Override
	public void addPIDEventListener(IPIDEventListener l) {
		device.addPIDEventListener(l);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#removePIDEventListener(com.neuronrobotics.sdk.pid.IPIDEventListener)
	 */
	@Override
	public void removePIDEventListener(IPIDEventListener l) {
		device.removePIDEventListener(l);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#flushPIDChannels(double)
	 */
	@Override
	public void flushPIDChannels(double time) {
		device.flushPIDChannels(time);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#SetPIDInterpolatedVelocity(int, int, double)
	 */
	@Override
	public boolean SetPIDInterpolatedVelocity(int group, int unitsPerSecond,
			double seconds) throws PIDCommandException {
		return device.SetPIDInterpolatedVelocity(group, unitsPerSecond, seconds);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#SetPDVelocity(int, int, double)
	 */
	@Override
	public boolean SetPDVelocity(int group, int unitsPerSecond, double seconds)
			throws PIDCommandException {
		return device.SetPDVelocity(group, unitsPerSecond, seconds);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#getPIDChannel(int)
	 */
	@Override
	public PIDChannel getPIDChannel(int group) {
		return device.getPIDChannel(group);
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#killAllPidGroups()
	 */
	@Override
	public boolean killAllPidGroups() {
		return device.killAllPidGroups();
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		return true;
	}

//	@Override
//	public boolean runOutputHysteresisCalibration(int group) {
//		return device.runOutputHysteresisCalibration(group);
//	}

}
