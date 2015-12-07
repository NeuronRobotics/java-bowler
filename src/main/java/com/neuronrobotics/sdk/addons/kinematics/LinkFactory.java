package com.neuronrobotics.sdk.addons.kinematics;
import java.util.ArrayList;

import com.neuronrobotics.imageprovider.AbstractImageProvider;
import com.neuronrobotics.imageprovider.VirtualCameraFactory;
import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.DeviceManager;
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
	
	/** The dyio. */
	private DyIO dyio;
	
	/** The pid. */
	private IPidControlNamespace pid;
	
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

		if(dyio==null)
			dyio=(DyIO) DeviceManager.getSpecificDevice(DyIO.class, c.getDeviceScriptingName());
		if(pid==null)
			pid=(IPidControlNamespace) DeviceManager.getSpecificDevice(IPidControlNamespace.class, c.getDeviceScriptingName());
		
		AbstractLink tmp=null;
		Log.info("Loading link: "+c.getName()+" type = "+c.getType()+" device= "+c.getDeviceScriptingName());
		switch(c.getType()){

		case ANALOG_PRISMATIC:
			if(dyio!=null){
				tmp = new AnalogPrismaticLink(	new AnalogInputChannel(dyio.getChannel(c.getHardwareIndex())), 
											c);
				tmp.setUseLimits(false);
			}
			break;
		case ANALOG_ROTORY:
			if(dyio!=null){
				tmp = new AnalogRotoryLink(	new AnalogInputChannel(dyio.getChannel(c.getHardwareIndex())), 
											c);
				tmp.setUseLimits(false);
			}
			break;
		case PID_TOOL:
		case PID:
			if(pid!=null){
				tmp=new PidRotoryLink(	pid.getPIDChannel(c.getHardwareIndex()),
										c);
			}
			break;
		case PID_PRISMATIC:
			if(pid!=null){
				tmp=new PidPrismaticLink(	pid.getPIDChannel(c.getHardwareIndex()),
										c);
			}
			break;
		case SERVO_PRISMATIC:
			if(dyio!=null){
				tmp = new ServoPrismaticLink(	new ServoChannel(dyio.getChannel(c.getHardwareIndex())), 
											c);
				
			}
			break;
		case SERVO_ROTORY:
		case SERVO_TOOL:
			if(dyio!=null){
				tmp = new ServoRotoryLink(	new ServoChannel(dyio.getChannel(c.getHardwareIndex())), 
											c);
				
			}
			break;
		case STEPPER_PRISMATIC:
			if(dyio!=null){
				tmp = new StepperPrismaticLink(	new CounterOutputChannel(dyio.getChannel(c.getHardwareIndex())), 
											c);				
			}
			break;
		case STEPPER_TOOL:
		case STEPPER_ROTORY:
			if(dyio!=null){
				tmp = new StepperRotoryLink(	new CounterOutputChannel(dyio.getChannel(c.getHardwareIndex())), 
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
		links.add(tmp);
		if(!getLinkConfigurations().contains(c))
			getLinkConfigurations().add(c);
		return tmp;
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
		long time = System.currentTimeMillis();
		//TODO this feature needs to be made to work, it should also check to see if all the links are on the same device
		if(dyio!=null){
			dyio.flushCache(seconds);
		}
		if(pid!=null){
			pid.flushPIDChannels(seconds);
		}else{	
			for(AbstractLink l:links){
				if(l.getLinkConfiguration().getDeviceScriptingName()!=null)
					l.flush(seconds);
			}
		}
		//System.out.println("Flush Took "+(System.currentTimeMillis()-time)+"ms");
	}
	
	/**
	 * Gets the pid.
	 *
	 * @return the pid
	 */
	public IPidControlNamespace getPid() {
		return pid;
	}
	
	/**
	 * Gets the dyio.
	 *
	 * @return the dyio
	 */
	public DyIO getDyio(){
		return dyio;
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
		if(pid!=null){
			return pid.isAvailable();
		}
		if(dyio!=null){
			return dyio.isAvailable();
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
