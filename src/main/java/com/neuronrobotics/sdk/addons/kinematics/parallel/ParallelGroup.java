package com.neuronrobotics.sdk.addons.kinematics.parallel;

import java.util.ArrayList;
import java.util.HashMap;

import com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR;
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics;
import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration;
import com.neuronrobotics.sdk.addons.kinematics.LinkFactory;
import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.sun.javafx.geom.Vec3d;

public class ParallelGroup extends AbstractKinematicsNR {

	private ArrayList<DHParameterKinematics> constituantLimbs = new ArrayList<DHParameterKinematics>();
	private HashMap<DHParameterKinematics, TransformNR> tipOffset = new HashMap<DHParameterKinematics, TransformNR>();
	/** The cad engine. */
	private String [] toolEngine =new String[]{"https://gist.github.com/33f2c10ab3adc5bd91f0a58ea7f24d14.git","parallelTool.groovy"}; 
	
	
	public void addLimb(DHParameterKinematics limb, TransformNR tip) {
		if (!getConstituantLimbs().contains(limb)) {
			getConstituantLimbs().add(limb);
		}
		getTipOffset().put(limb, tip);
		for (LinkConfiguration c : limb.getFactory().getLinkConfigurations()) {
			
			getFactory().addLink(limb.getFactory().getLink(c));// adding the configurations the the single
									// factory
		}

	}

	@Override
	public void disconnectDevice() {
		// TODO Auto-generated method stub
		for (DHParameterKinematics l : getConstituantLimbs()) {
			l.disconnect();
		}
	}

	@Override
	public boolean connectDevice() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public double[] inverseKinematics(TransformNR taskSpaceTransform) throws Exception {
		int numBerOfLinks = 0;
		for (DHParameterKinematics l : getConstituantLimbs()) {
			numBerOfLinks += l.getNumberOfLinks();
		}
		double[] linkValues = new double[numBerOfLinks];
		int limbOffset = 0;
		for (DHParameterKinematics l : getConstituantLimbs()) {
			TransformNR localTip = taskSpaceTransform.times(getTipOffset().get(l).inverse());
			// Use the built in IK model for the limb
			double[] jointSpaceVect = l.inverseKinematics(l.inverseOffset(localTip));
			// Load the link vector into the total vector
			for (int i = 0; i < jointSpaceVect.length; i++) {
				linkValues[limbOffset + i] = jointSpaceVect[i];
			}
			limbOffset += jointSpaceVect.length;
		}

		return linkValues;
	}

	@Override
	public TransformNR forwardKinematics(double[] jointSpaceVector) {
		HashMap<DHParameterKinematics, TransformNR> tips = new HashMap<DHParameterKinematics, TransformNR>();
		for (DHParameterKinematics l : getConstituantLimbs()) {
			TransformNR fwd = l.forwardKinematics(l.getCurrentJointSpaceVector());
			if (fwd == null)
				throw new RuntimeException("Implementations of the kinematics need to return a transform not null");
			// Log.info("Getting robot task space "+fwd);
			tips.put(l, l.forwardOffset(fwd));

			// tips.get(l).times(tipOffset.get(l)));//apply tip offset
			// TODO check to see if the TIps are alligned as you add them and
			// throw an exception if a tip is misalligned
		}
		if (getConstituantLimbs().size() > 3) {
			// we are assuming any passive links are encoded
			double dx = 0;
			double dy = 0;
			double dz = 0;

			for (int i = 0; i < 3; i++) {
				TransformNR l = tips.get(getConstituantLimbs().get(i));
				Vec3d p1 = new Vec3d(l.getX(), l.getY(), l.getZ());
				dx += p1.x;
				dy += p1.y;
				dz += p1.z;
			}
			double x = dx /= 3;
			double y = dy /= 3;
			double z = dz /= 3;

			double rotx = Math.atan2(y, z);
			double roty;
			 if (z >= 0) {
			    roty = -Math.atan2( x * Math.cos(rotx), z );
			 }else{
			    roty = Math.atan2( x * Math.cos(rotx), -z );
			 }
			double rotz = Math.atan2(Math.cos(rotx), Math.sin(rotx) * Math.sin(roty));

			return new TransformNR(x,y,x,new RotationNR(rotx,roty,rotz));
		} else {
			return tips.get(getConstituantLimbs().get(0));// assume the first link is
														// in control or
														// orentation
		}
	}
	
	/**
	 * Gets the cad engine.
	 *
	 * @return the cad engine
	 */
	public String [] getGitCadToolEngine() {
		return toolEngine;
	}

	/**
	 * Sets the cad engine.
	 *
	 * @param cadEngine the new cad engine
	 */
	public void setGitCadToolEngine(String [] cadEngine) {
		if(cadEngine!=null&& cadEngine[0]!=null &&cadEngine[1]!=null)
		this.toolEngine = cadEngine;
	}

	public ArrayList<DHParameterKinematics> getConstituantLimbs() {
		return constituantLimbs;
	}

	public void setConstituantLimbs(ArrayList<DHParameterKinematics> constituantLimbs) {
		this.constituantLimbs = constituantLimbs;
	}

	public HashMap<DHParameterKinematics, TransformNR> getTipOffset() {
		return tipOffset;
	}

	public void setTipOffset(HashMap<DHParameterKinematics, TransformNR> tipOffset) {
		this.tipOffset = tipOffset;
	}

	public void removeLimb(DHParameterKinematics limb) {
		if(constituantLimbs.contains(limb)){
			constituantLimbs.remove(limb);
			getTipOffset().remove(limb);
			setFactory(new LinkFactory());// clear the links
			for(DHParameterKinematics remaining: constituantLimbs){
				for (LinkConfiguration c : remaining.getFactory().getLinkConfigurations()) {
					getFactory().addLink(remaining.getFactory().getLink(c));// adding the configurations the the single
											// factory
				}
			}
		}
	}
	
	
	
}
