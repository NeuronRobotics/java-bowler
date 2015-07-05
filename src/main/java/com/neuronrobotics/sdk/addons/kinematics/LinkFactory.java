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
	private IPidControlNamespace pid=null;
	private DyIO dyio=null;
	//private VirtualGenericPIDDevice virtual = new VirtualGenericPIDDevice(1000000);
	private boolean hasPid=false;
	private boolean hasServo=false;
	private boolean hasStepper=false;
	private boolean forceVirtual = false;
	private ArrayList<AbstractLink> links = new ArrayList<AbstractLink>();
	private ArrayList<LinkConfiguration> linkConfigurations=null ;
	public LinkFactory (){
		hasPid=false;
		hasServo=false;
		hasStepper=false;
		forceVirtual=true;
	}
	
	public LinkFactory (BowlerAbstractDevice d){
		if(d==null){
			forceVirtual=true;
			return;
		}
		if(DyIO.class.isInstance(d)){
			dyio=(DyIO)d;
			hasServo=true;
			hasStepper=true;
		}
		if(IExtendedPIDControl.class.isInstance(d)){
			pid=(IExtendedPIDControl)d;
			hasPid=true;
		}
	}
	
	public LinkFactory(ILinkFactoryProvider connection,IExtendedPIDControl d) {
		pid=d;
		hasPid=true;
		//Log.enableInfoPrint();
		//TODO fill in the auto link configuration
		LinkConfiguration first = connection.requestLinkConfiguration(0);
		first.setPidConfiguration( pid);
		getLink(first);
		
		for (int i=1;i<first.getTotlaNumberOfLinks();i++){
			LinkConfiguration tmp = connection.requestLinkConfiguration(i);
			tmp.setPidConfiguration(pid);
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
		AbstractLink tmp=null;
		System.err.println("Loading link: "+c.getName()+" type = "+c.getType()+" device= "+c.getDeviceScriptingName());
		DyIO d;
		IPidControlNamespace p;
		switch(c.getType()){

		case ANALOG_PRISMATIC:
			d = dyio;
			if(c.getDeviceScriptingName()!=null)
				d=(DyIO) DeviceManager.getSpecificDevice(DyIO.class, c.getDeviceScriptingName());
			if(d!=null)
				tmp = new AnalogPrismaticLink(	new AnalogInputChannel(d.getChannel(c.getHardwareIndex())), 
											c);
			tmp.setUseLimits(false);
			break;
		case ANALOG_ROTORY:
			d = dyio;
			if(c.getDeviceScriptingName()!=null)
				d=(DyIO) DeviceManager.getSpecificDevice(DyIO.class, c.getDeviceScriptingName());
			if(d!=null)
				tmp = new AnalogRotoryLink(	new AnalogInputChannel(d.getChannel(c.getHardwareIndex())), 
											c);
			tmp.setUseLimits(false);
			break;
		case PID_TOOL:
		case PID:
			p = pid;
			if(c.getDeviceScriptingName()!=null)
				p=(IPidControlNamespace) DeviceManager.getSpecificDevice(IPidControlNamespace.class, c.getDeviceScriptingName());
			if(p!=null)
				tmp=new PidRotoryLink(	p.getPIDChannel(c.getHardwareIndex()),
										c);
			tmp.setUseLimits(true);
			break;
		case PID_PRISMATIC:
			 p = pid;
			if(c.getDeviceScriptingName()!=null)
				p=(IPidControlNamespace) DeviceManager.getSpecificDevice(IPidControlNamespace.class, c.getDeviceScriptingName());
			if(p!=null)
				tmp=new PidPrismaticLink(	p.getPIDChannel(c.getHardwareIndex()),
										c);
			tmp.setUseLimits(true);
			break;
		case SERVO_PRISMATIC:
			d = dyio;
			if(c.getDeviceScriptingName()!=null)
				d=(DyIO) DeviceManager.getSpecificDevice(DyIO.class, c.getDeviceScriptingName());
			if(d!=null){
				tmp = new ServoPrismaticLink(	new ServoChannel(d.getChannel(c.getHardwareIndex())), 
											c);
				tmp.setUseLimits(true);
				
			}
			break;
		case SERVO_ROTORY:
		case SERVO_TOOL:
			d = dyio;
			if(c.getDeviceScriptingName()!=null)
				d=(DyIO) DeviceManager.getSpecificDevice(DyIO.class, c.getDeviceScriptingName());
			if(d!=null){
				tmp = new ServoRotoryLink(	new ServoChannel(d.getChannel(c.getHardwareIndex())), 
											c);
				tmp.setUseLimits(true);
				
			}
			break;
		case STEPPER_PRISMATIC:
			d = dyio;
			if(c.getDeviceScriptingName()!=null)
				d=(DyIO) DeviceManager.getSpecificDevice(DyIO.class, c.getDeviceScriptingName());
			if(d!=null){
				tmp = new StepperPrismaticLink(	new CounterOutputChannel(d.getChannel(c.getHardwareIndex())), 
											c);
				tmp.setUseLimits(true);
				
			}
			break;
		case STEPPER_TOOL:
		case STEPPER_ROTORY:
			d = dyio;
			if(c.getDeviceScriptingName()!=null)
				d=(DyIO) DeviceManager.getSpecificDevice(DyIO.class, c.getDeviceScriptingName());
			if(d!=null){
				tmp = new StepperRotoryLink(	new CounterOutputChannel(d.getChannel(c.getHardwareIndex())), 
											c);
				tmp.setUseLimits(true);
				
			}
			break;
		case DUMMY:
		case VIRTUAL:
			tmp=new PidRotoryLink(	new VirtualGenericPIDDevice(100000).getPIDChannel(c.getHardwareIndex()),
					c);
			tmp.setUseLimits(false);
			break;
		}
		
		if(tmp==null){
			if(!c.getType().isPrismatic()){
				tmp=new PidRotoryLink(	new VirtualGenericPIDDevice(100000).getPIDChannel(c.getHardwareIndex()),
						c);
				tmp.setUseLimits(false);
			}else{
				tmp=new PidPrismaticLink(	new VirtualGenericPIDDevice(100000).getPIDChannel(c.getHardwareIndex()),
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
		if(hasServo){
			dyio.flushCache(seconds);
			Log.info("Flushing DyIO");
		}
		if(hasPid){
			pid.flushPIDChannels(seconds);
			Log.info("Flushing PID");
		}
		
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
		if(hasPid){
			return pid.isAvailable();
		}
		if(hasServo){
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
