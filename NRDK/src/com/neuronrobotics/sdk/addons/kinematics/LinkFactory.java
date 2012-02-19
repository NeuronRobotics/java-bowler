package com.neuronrobotics.sdk.addons.kinematics;
import java.util.ArrayList;

import com.neuronrobotics.addons.driving.virtual.VirtualGenericPIDDevice;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;
import com.neuronrobotics.sdk.genericdevice.GenericPIDDevice;
import com.neuronrobotics.sdk.pid.IPIDControl;

public class LinkFactory {
	private IPIDControl pid=null;
	private DyIO dyio=null;
	private VirtualGenericPIDDevice virtual = new VirtualGenericPIDDevice(1000000);
	private boolean hasPid=false;
	private boolean hasServo=false;
	private boolean hasStepper=false;
	private boolean forceVirtual = false;
	private ArrayList<AbstractLink> links = new ArrayList<AbstractLink>();
	
	public LinkFactory (){
		hasPid=false;
		hasServo=false;
		hasStepper=false;
		forceVirtual=true;
	}
	
	public LinkFactory (DyIO d){
		if(d==null){
			forceVirtual=true;
			return;
		}
		dyio=d;
		pid=d;
		hasPid=true;
		hasServo=true;
		hasStepper=true;
	}
	public LinkFactory (GenericPIDDevice d){
		if(d==null){
			forceVirtual=true;
			return;
		}
		pid=d;
		hasPid=true;
	}
	public AbstractLink getLink(LinkConfiguration c){
		for(AbstractLink l:links){
			if(l.getLinkConfiguration() == c)
				return l;
		}
		AbstractLink tmp=null;
		if(!forceVirtual){
			if(c.getType().equals("servo-rotory")){
				tmp = new ServoRotoryLink(	new ServoChannel(dyio.getChannel(c.getHardwareIndex())), 
											(int)c.getIndexLatch(),
											(int)c.getLowerLimit(),
											(int)c.getUpperLimit(),
											c.getScale());
			}else if (c.getType().equals("dummy")){
				tmp=new PidRotoryLink(	virtual.getPIDChannel(c.getHardwareIndex()),
						(int)0,
						(int)c.getLowerLimit(),
						(int)c.getUpperLimit(),
						c.getScale());
			}else{
				tmp=new PidRotoryLink(	pid.getPIDChannel(c.getHardwareIndex()),
										(int)0,
										(int)c.getLowerLimit(),
										(int)c.getUpperLimit(),
										c.getScale());
				
			}
		}else{
			
			int home=0;
			if(c.getType().equals("servo-rotory"))
				home = c.getIndexLatch();
			tmp=new PidRotoryLink(	virtual.getPIDChannel(c.getHardwareIndex()),
					(int)home,
					(int)c.getLowerLimit(),
					(int)c.getUpperLimit(),
					c.getScale());
		}
		tmp.setLinkConfiguration(c);
		links.add(tmp);
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
	public void flush(double seconds){
		if(hasPid){
			pid.flushPIDChannels(seconds);
		}
		if(hasServo){
			dyio.flushCache(seconds);
		}
		virtual.flushPIDChannels(seconds);
	}
	public IPIDControl getPid() {
		return pid;
	}
	public DyIO getDyio(){
		return dyio;
	}
	public void setCachedTargets(double[] jointSpaceVect) {
		if(jointSpaceVect.length!=links.size())
			throw new IndexOutOfBoundsException();
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
}
