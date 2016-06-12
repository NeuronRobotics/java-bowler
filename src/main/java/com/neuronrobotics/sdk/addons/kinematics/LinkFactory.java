package com.neuronrobotics.sdk.addons.kinematics;
import java.util.ArrayList;
import java.util.HashMap;

import com.neuronrobotics.imageprovider.AbstractImageProvider;
import com.neuronrobotics.imageprovider.VirtualCameraFactory;
import com.neuronrobotics.sdk.addons.kinematics.gcodebridge.GcodeDevice;
import com.neuronrobotics.sdk.addons.kinematics.gcodebridge.GcodePrismatic;
import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.DeviceManager;
import com.neuronrobotics.sdk.common.IFlushable;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.CounterOutputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;
import com.neuronrobotics.sdk.namespace.bcs.pid.IExtendedPIDControl;
import com.neuronrobotics.sdk.namespace.bcs.pid.IPidControlNamespace;
import com.neuronrobotics.sdk.pid.GenericPIDDevice;
import com.neuronrobotics.sdk.pid.ILinkFactoryProvider;
import com.neuronrobotics.sdk.pid.VirtualGenericPIDDevice;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating Link objects.
 */
public class LinkFactory {
	
	/** The virtual. */
	private VirtualGenericPIDDevice virtual=null; 
	
	/** The links. */
	private ArrayList<AbstractLink> links = new ArrayList<AbstractLink>();
	
	/** The link configurations. */
	private ArrayList<LinkConfiguration> linkConfigurations=null ;
	
//	/** The dyio. */
//	private DyIO dyio;
//	
//	/** The pid. */
//	private IPidControlNamespace pid;
//	
	
	/**
	 * Instantiates a new link factory.
	 */
	public LinkFactory(){
		this(null);
	}
	
	/**
	 * Instantiates a new link factory.
	 *
	 * @param bad the bad
	 */
	public LinkFactory(BowlerAbstractDevice bad){
		if(bad!=null)
			DeviceManager.addConnection(bad, bad.getScriptingName());
	}
	
	/**
	 * Instantiates a new link factory.
	 *
	 * @param connection the connection
	 * @param d the d
	 */
	public LinkFactory(ILinkFactoryProvider connection,IExtendedPIDControl d) {
		this(null);
		//Log.enableInfoPrint();
		//TODO fill in the auto link configuration
		LinkConfiguration first = connection.requestLinkConfiguration(0);
		first.setPidConfiguration( d);
		getLink(first);
		
		for (int i=1;i<first.getTotlaNumberOfLinks();i++){
			LinkConfiguration tmp = connection.requestLinkConfiguration(i);
			tmp.setPidConfiguration(d);
			getLink(tmp);
		}

		
	}
	
	/**
	 * Gets the link.
	 *
	 * @param name the name
	 * @return the link
	 */
	public AbstractLink getLink(String name) {
		for(AbstractLink l:links){
			if(l.getLinkConfiguration().getName().equalsIgnoreCase(name))
				return l;
		}
		String data = "No link of name '"+name+"' exists";
		for(AbstractLink l:links){
			data +="\n"+l.getLinkConfiguration().getName();
		}
		throw new RuntimeException(data);
	}
	
	/**
	 * Gets the link.
	 *
	 * @param c the c
	 * @return the link
	 */
	public AbstractLink getLink(LinkConfiguration c){
		for(AbstractLink l:links){
			if(l.getLinkConfiguration() == c)
				return l;
		}
		return getLinkLocal( c);
	}
	
	/**
	 * Refresh hardware layer.
	 *
	 * @param c the c
	 */
	public void refreshHardwareLayer(LinkConfiguration c){
		//retreive the old link
		AbstractLink oldLink = getLink( c);
		links.remove(oldLink);
		AbstractLink newLink = getLinkLocal( c);
		for(ILinkListener l:oldLink.getLinks()){
			newLink.addLinkListener(l);
		}
		oldLink.removeAllLinkListener();
		
	}
	
	/**
	 * Gets the link local.
	 *
	 * @param c the c
	 * @return the link local
	 */
	private AbstractLink getLinkLocal(LinkConfiguration c){

		AbstractLink tmp=null;
		Log.info("Loading link: "+c.getName()+" type = "+c.getType()+" device= "+c.getDeviceScriptingName());

		switch(c.getType()){
		
			
		case ANALOG_PRISMATIC:
			if(getDyio(c)!=null){
				tmp = new AnalogPrismaticLink(	new AnalogInputChannel(getDyio(c).getChannel(c.getHardwareIndex())), 
											c);
				tmp.setUseLimits(false);
			}
			break;
		case ANALOG_ROTORY:
			if(getDyio(c)!=null){
				tmp = new AnalogRotoryLink(	new AnalogInputChannel(getDyio(c).getChannel(c.getHardwareIndex())), 
											c);
				tmp.setUseLimits(false);
			}
			break;
		case PID_TOOL:
		case PID:
			if(getPid(c)!=null){
				tmp=new PidRotoryLink(	getPid(c).getPIDChannel(c.getHardwareIndex()),
										c);
			}
			break;
		case PID_PRISMATIC:
			if(getPid(c)!=null){
				tmp=new PidPrismaticLink(	getPid(c).getPIDChannel(c.getHardwareIndex()),
										c);
			}
			break;
		case SERVO_PRISMATIC:
			if(getDyio(c)!=null){
				tmp = new ServoPrismaticLink(	new ServoChannel(getDyio(c).getChannel(c.getHardwareIndex())), 
											c);
				
			}
			break;
		case SERVO_ROTORY:
		case SERVO_TOOL:
			if(getDyio(c)!=null){
				tmp = new ServoRotoryLink(	new ServoChannel(getDyio(c).getChannel(c.getHardwareIndex())), 
											c);
				
			}
			break;
		case STEPPER_PRISMATIC:
			if(getDyio(c)!=null){
				tmp = new StepperPrismaticLink(	new CounterOutputChannel(getDyio(c).getChannel(c.getHardwareIndex())), 
											c);				
			}
			break;
		case STEPPER_TOOL:
		case STEPPER_ROTORY:
			if(getDyio(c)!=null){
				tmp = new StepperRotoryLink(	new CounterOutputChannel(getDyio(c).getChannel(c.getHardwareIndex())), 
											c);				
			}
			break;
		case DUMMY:
		case VIRTUAL:
			String myVirtualDevName=c.getDeviceScriptingName();
			virtual = (VirtualGenericPIDDevice)DeviceManager.getSpecificDevice(VirtualGenericPIDDevice.class, myVirtualDevName);
			if(virtual==null){
				virtual=new VirtualGenericPIDDevice();
				DeviceManager.addConnection(virtual, myVirtualDevName);
			}
			tmp=new PidRotoryLink(	virtual.getPIDChannel(c.getHardwareIndex()),
					c);
			break;
		case CAMERA:
			String myVirtualDevName1=c.getDeviceScriptingName();
			AbstractImageProvider img = (AbstractImageProvider)DeviceManager.getSpecificDevice(AbstractImageProvider.class, myVirtualDevName1);
			if(img==null){
				img= VirtualCameraFactory.getVirtualCamera();
				DeviceManager.addConnection(img, myVirtualDevName1);
			}
			tmp=new CameraLink(c,img);
			break;
		case GCODE_HEATER_TOOL:
			if(getGCODE(c)!=null){
				tmp = getGCODE(c).getHeater(c);
			}
			break;
		case GCODE_STEPPER_PRISMATIC:
		case GCODE_STEPPER_ROTORY:
		case GCODE_STEPPER_TOOL:
			if(getGCODE(c)!=null){
				tmp = getGCODE(c).getLink(c);
			}
			break;
		default:
			break;
		}
		
		if(tmp==null){
			String myVirtualDevName=c.getDeviceScriptingName();
			virtual = (VirtualGenericPIDDevice)DeviceManager.getSpecificDevice(VirtualGenericPIDDevice.class, myVirtualDevName);
			if(virtual==null){
				virtual=new VirtualGenericPIDDevice();
				DeviceManager.addConnection(virtual, myVirtualDevName);
			}
			if(!c.getType().isPrismatic()){
				tmp=new PidRotoryLink(	virtual.getPIDChannel(c.getHardwareIndex()),
						c);
			}else{
				tmp=new PidPrismaticLink(virtual.getPIDChannel(c.getHardwareIndex()),
						c);
			}
		}
		tmp.setLinkConfiguration(c);
		addLink(tmp);
		return tmp;
	}
	/**
	 * THis interface lets the user add a link after instantiation
	 * @param link the link to be added in order
	 */
	public void addLink(AbstractLink link){
		links.add(link);
		if(!getLinkConfigurations().contains(link.getLinkConfiguration()))
			getLinkConfigurations().add(link.getLinkConfiguration());
	}
	
	/**
	 * Gets the lower limits.
	 *
	 * @return the lower limits
	 */
	public double [] getLowerLimits(){
		double [] up = new double [links.size()];
		for(int i=0;i< up.length;i++){
			up[i] = links.get(i).getMinEngineeringUnits();
		}
		return up;
	}
	
	/**
	 * Gets the upper limits.
	 *
	 * @return the upper limits
	 */
	public double [] getUpperLimits(){
		double [] up = new double [links.size()];
		for(int i=0;i< up.length;i++){
			up[i] = links.get(i).getMaxEngineeringUnits();
		}
		return up;
	}
	
	/**
	 * Adds the link listener.
	 *
	 * @param l the l
	 */
	public void addLinkListener(ILinkListener l){
		for(AbstractLink lin:links){
			lin.addLinkListener(l);
		}
	}
	
	/**
	 * Flush.
	 *
	 * @param seconds the seconds
	 */
	public void flush(final double seconds){
		HashMap<String , Boolean> flushed=new HashMap<String, Boolean>();
 		for(LinkConfiguration c:getLinkConfigurations()){
 			String name = c.getDeviceScriptingName();
			// if a device is disconnected it is removed from the device manager. the factory should check all devices
			if(DeviceManager.getSpecificDevice(IFlushable.class,name)==null){
				getLink(c).flush(seconds);//links flushed directly because there is no flushable device
			}else{
				if(flushed.get(name)==null){
					flushed.put(name,true);
					IFlushable flushDevice = (IFlushable)DeviceManager.getSpecificDevice(IFlushable.class,name);
					flushDevice.flush(seconds);
				}
			}
			
			
		}
		//System.out.println("Flush Took "+(System.currentTimeMillis()-time)+"ms");
	}
	
	/**
	 * Gets the pid from the database..
	 *
	 * @return the pid from the database.
	 */
	public IPidControlNamespace getPid(LinkConfiguration c) {

			return (IPidControlNamespace) DeviceManager.getSpecificDevice(IPidControlNamespace.class, c.getDeviceScriptingName());

	}
	
	/**
	 * Gets the dyio.
	 *
	 * @return the dyio from the database.
	 */
	public DyIO getDyio(LinkConfiguration c){
	
			return (DyIO) DeviceManager.getSpecificDevice(DyIO.class, c.getDeviceScriptingName());

	}
	
	/**
	 * Gets the Gcode device from the database.
	 *
	 * @return the GCODE device
	 */
	public GcodeDevice getGCODE(LinkConfiguration c){
	
			return (GcodeDevice) DeviceManager.getSpecificDevice(GcodeDevice.class, c.getDeviceScriptingName());

	}
	
	/**
	 * Sets the cached targets.
	 *
	 * @param jointSpaceVect the new cached targets
	 */
	public void setCachedTargets(double[] jointSpaceVect) {
		if(jointSpaceVect.length!=links.size())
			throw new IndexOutOfBoundsException("Expected "+links.size()+" links, got "+jointSpaceVect.length);
		int i=0;
		for(AbstractLink lin:links){
			try{
				lin.setTargetEngineeringUnits(jointSpaceVect[i]);
			}catch (Exception ee){
				throw new RuntimeException("Joint "+i+" failed, "+ee.getMessage());
			}
			i++;
		}
	}

	/**
	 * Checks if is connected.
	 *
	 * @return true, if is connected
	 */
	public boolean isConnected() {
		for(LinkConfiguration c:getLinkConfigurations()){
			// if a device is disconnected it is removed from the device manager. the factory should check all devices
			if(DeviceManager.getSpecificDevice(null, c.getDeviceScriptingName())==null){
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets the link configurations.
	 *
	 * @return the link configurations
	 */
	public ArrayList<LinkConfiguration> getLinkConfigurations() {
		if(linkConfigurations== null){
			linkConfigurations=new ArrayList<LinkConfiguration>();
		}
		return linkConfigurations;
	}

	/**
	 * Removes the link listener.
	 *
	 * @param l the l
	 */
	public void removeLinkListener(AbstractKinematicsNR l) {
		// TODO Auto-generated method stub
		for(AbstractLink lin:links){
			lin.removeLinkListener(l);
		}
	}

	/**
	 * Delete link.
	 *
	 * @param i the i
	 */
	public void deleteLink(int i) {
		links.remove(i);
		getLinkConfigurations().remove(i);
	}
}
