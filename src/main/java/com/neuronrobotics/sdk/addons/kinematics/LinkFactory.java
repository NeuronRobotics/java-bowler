package com.neuronrobotics.sdk.addons.kinematics;
import java.util.ArrayList;

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

public class LinkFactory {
	private VirtualGenericPIDDevice virtual=null; 
	private ArrayList<AbstractLink> links = new ArrayList<AbstractLink>();
	private ArrayList<LinkConfiguration> linkConfigurations=null ;
	private final String myVirtualDevName="virtual_"+(int)(Math.random()*9999.0);
	private DyIO dyio;
	private IPidControlNamespace pid;
	
	public LinkFactory(){
		this(null);
	}
	public LinkFactory(BowlerAbstractDevice bad){
		if(bad!=null)
			DeviceManager.addConnection(bad, bad.getScriptingName());
		virtual = (VirtualGenericPIDDevice)DeviceManager.getSpecificDevice(VirtualGenericPIDDevice.class, myVirtualDevName);
		if(virtual==null){
			virtual=new VirtualGenericPIDDevice();
			DeviceManager.addConnection(virtual, myVirtualDevName);
		}
	}
	
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
	
	public AbstractLink getLink(String name) {
		for(AbstractLink l:links){
			if(l.getLinkConfiguration().getName().equalsIgnoreCase(name))
				return l;
		}
		String data = "No linke of name '"+name+"' exists";
		for(AbstractLink l:links){
			data +="\n"+l.getLinkConfiguration().getName();
		}
		throw new RuntimeException(data);
	}
	
	public AbstractLink getLink(LinkConfiguration c){
		for(AbstractLink l:links){
			if(l.getLinkConfiguration() == c)
				return l;
		}

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
			tmp.setUseLimits(false);}
			break;
		case ANALOG_ROTORY:
			if(dyio!=null){
				tmp = new AnalogRotoryLink(	new AnalogInputChannel(dyio.getChannel(c.getHardwareIndex())), 
											c);
			tmp.setUseLimits(false);}
			break;
		case PID_TOOL:
		case PID:
			if(pid!=null){
				tmp=new PidRotoryLink(	pid.getPIDChannel(c.getHardwareIndex()),
										c);
			tmp.setUseLimits(true);}
			break;
		case PID_PRISMATIC:
			if(pid!=null){
				tmp=new PidPrismaticLink(	pid.getPIDChannel(c.getHardwareIndex()),
										c);
			tmp.setUseLimits(true);}
			break;
		case SERVO_PRISMATIC:
			if(dyio!=null){
				tmp = new ServoPrismaticLink(	new ServoChannel(dyio.getChannel(c.getHardwareIndex())), 
											c);
				tmp.setUseLimits(true);
				
			}
			break;
		case SERVO_ROTORY:
		case SERVO_TOOL:
			if(dyio!=null){
				tmp = new ServoRotoryLink(	new ServoChannel(dyio.getChannel(c.getHardwareIndex())), 
											c);
				tmp.setUseLimits(true);
				
			}
			break;
		case STEPPER_PRISMATIC:
			if(dyio!=null){
				tmp = new StepperPrismaticLink(	new CounterOutputChannel(dyio.getChannel(c.getHardwareIndex())), 
											c);
				tmp.setUseLimits(true);
				
			}
			break;
		case STEPPER_TOOL:
		case STEPPER_ROTORY:
			if(dyio!=null){
				tmp = new StepperRotoryLink(	new CounterOutputChannel(dyio.getChannel(c.getHardwareIndex())), 
											c);
				tmp.setUseLimits(true);
				
			}
			break;
		case DUMMY:
		case VIRTUAL:
			tmp=new PidRotoryLink(	virtual.getPIDChannel(c.getHardwareIndex()),
					c);
			tmp.setUseLimits(false);
			break;
		}
		
		if(tmp==null){
			if(!c.getType().isPrismatic()){
				tmp=new PidRotoryLink(	virtual.getPIDChannel(c.getHardwareIndex()),
						c);
				tmp.setUseLimits(false);
			}else{
				tmp=new PidPrismaticLink(virtual.getPIDChannel(c.getHardwareIndex()),
						c);
				tmp.setUseLimits(false);
			}
		}
		tmp.setLinkConfiguration(c);
		links.add(tmp);
		getLinkConfigurations().add(c);
		return tmp;
	}
	
	public double [] getLowerLimits(){
		double [] up = new double [links.size()];
		for(int i=0;i< up.length;i++){
			up[i] = links.get(i).getMinEngineeringUnits();
		}
		return up;
	}
	
	public double [] getUpperLimits(){
		double [] up = new double [links.size()];
		for(int i=0;i< up.length;i++){
			up[i] = links.get(i).getMaxEngineeringUnits();
		}
		return up;
	}
	
	public void addLinkListener(ILinkListener l){
		for(AbstractLink lin:links){
			lin.addLinkListener(l);
		}
	}
	public void flush(final double seconds){
		long time = System.currentTimeMillis();
		
		for(AbstractLink l:links){
			if(l.getLinkConfiguration().getDeviceScriptingName()!=null)
				l.flush(seconds);
		}
		//System.out.println("Flush Took "+(System.currentTimeMillis()-time)+"ms");
	}
	public IPidControlNamespace getPid() {
		return pid;
	}
	public DyIO getDyio(){
		return dyio;
	}
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

	public boolean isConnected() {
		if(pid!=null){
			return pid.isAvailable();
		}
		if(dyio!=null){
			return dyio.isAvailable();
		}
		return true;
	}

	public ArrayList<LinkConfiguration> getLinkConfigurations() {
		if(linkConfigurations== null){
			linkConfigurations=new ArrayList<LinkConfiguration>();
		}
		return linkConfigurations;
	}

	public void removeLinkListener(AbstractKinematicsNR l) {
		// TODO Auto-generated method stub
		for(AbstractLink lin:links){
			lin.removeLinkListener(l);
		}
	}

	public void deleteLink(int i) {
		links.remove(i);
		getLinkConfigurations().remove(i);
	}
}
