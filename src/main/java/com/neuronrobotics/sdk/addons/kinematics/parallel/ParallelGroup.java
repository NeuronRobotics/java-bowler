package com.neuronrobotics.sdk.addons.kinematics.parallel;

import java.util.ArrayList;
import java.util.HashMap;

import com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR;
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics;
import com.neuronrobotics.sdk.addons.kinematics.ITaskSpaceUpdateListenerNR;
import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration;
import com.neuronrobotics.sdk.addons.kinematics.LinkFactory;
import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.sun.javafx.geom.Vec3d;

public class ParallelGroup extends DHParameterKinematics {

	private ArrayList<DHParameterKinematics> constituantLimbs = new ArrayList<DHParameterKinematics>();
	private HashMap<DHParameterKinematics, TransformNR> tipOffset = new HashMap<DHParameterKinematics, TransformNR>();
	private HashMap<DHParameterKinematics, String> tipOffsetRelativeToName = new HashMap<>();
	private HashMap<DHParameterKinematics, Integer> tipOffsetRelativeIndex = new HashMap<>();
	/** The cad engine. */
	private String[] toolEngine = new String[] { "https://gist.github.com/33f2c10ab3adc5bd91f0a58ea7f24d14.git",
			"parallelTool.groovy" };
	private String name;

	public ParallelGroup(String name) {
		this.name = name;
	}

	public void addLimb(DHParameterKinematics limb, TransformNR tip, String name, int index) {
		if (!getConstituantLimbs().contains(limb)) {
			getConstituantLimbs().add(limb);
			for (LinkConfiguration c : limb.getFactory().getLinkConfigurations()) {

				getFactory().addLink(limb.getFactory().getLink(c));// adding the configurations the the single
				// factory
			}
		}
		if (tip != null) {
			setupReferencedLimb(limb, tip, name, index);
		}

	}
	
	public DHParameterKinematics getFKLimb() {
		for(DHParameterKinematics d:getConstituantLimbs()) {
			if(getTipOffset(d)==null) {
				return d;// this is the first limb with no relative tip
			}
		}
		// this should be impossible
		throw new RuntimeException("FK lim must be possible, one limb must not have a reference to another");
	}
	/**
	 * Calc home.
	 *
	 * @return the transform nr
	 */
	@Override
	public TransformNR calcHome() {
		return getFKLimb().calcHome();
	}
	public void setupReferencedLimb(DHParameterKinematics limb, TransformNR tip, String name, int index) {
		tipOffsetRelativeToName.put(limb, name);
		tipOffsetRelativeIndex.put(limb, index);
		getTipOffset().put(limb, tip);
	}
	
	public void clearReferencedLimb(DHParameterKinematics limb) {
		tipOffsetRelativeToName.remove(limb);
		tipOffsetRelativeIndex.remove(limb);
		getTipOffset().remove(limb);
	}

	@Override
	public void disconnectDevice() {
		// TODO Auto-generated method stub
		for (DHParameterKinematics l : getConstituantLimbs()) {
			l.disconnect();
		}
		close();

	}

	@Override
	public boolean connectDevice() {
		// TODO Auto-generated method stub
		return true;
	}

	private double[] compute(DHParameterKinematics l, HashMap<String, double[]> IKvalues,
			TransformNR taskSpaceTransform) throws Exception {
		String scriptingName = l.getScriptingName();
		if (IKvalues.get(scriptingName) != null) {
			// existes already
			return IKvalues.get(scriptingName);
		}
		if (getTipOffset().get(l) == null) {
			// no offset, compute as normal
			double[] jointSpaceVect = l.inverseKinematics(l.inverseOffset(taskSpaceTransform));
			IKvalues.put(scriptingName, jointSpaceVect);
		} else {
			TransformNR offset = getTipOffset().get(l);
			String refLimbName = tipOffsetRelativeToName.get(l);
			int index = tipOffsetRelativeIndex.get(l);
			DHParameterKinematics referencedLimb = findReferencedLimb(refLimbName);
			if (referencedLimb == null)
				throw new RuntimeException("Referenced limb missing, IK for " + l.getScriptingName() + " Failed");
			double[] jointSpaceVectReferenced = compute(referencedLimb, IKvalues, taskSpaceTransform);

			TransformNR transformTOLinksTip = referencedLimb.getChain().getChain(jointSpaceVectReferenced).get(index)
					.times(offset.inverse());
			double[] jointSpaceVect = l.inverseKinematics(l.inverseOffset(transformTOLinksTip));
			IKvalues.put(scriptingName, jointSpaceVect);
		}

		return IKvalues.get(scriptingName);
	}

	private DHParameterKinematics findReferencedLimb(String refLimbName) {
		DHParameterKinematics referencedLimb = null;
		for (DHParameterKinematics lm : getConstituantLimbs()) {
			if (lm.getScriptingName().toLowerCase().contentEquals(refLimbName.toLowerCase())) {
				// FOund the referenced limb
				referencedLimb = lm;
			}
		}
		return referencedLimb;
	}
	/**
	 * Sets the current pose target.
	 *
	 * @param currentPoseTarget the new current pose target
	 */
	@Override
	public void setCurrentPoseTarget(TransformNR currentPoseTarget) {
		if(checkTaskSpaceTransform(currentPoseTarget)) {
			super.setCurrentPoseTarget(currentPoseTarget);
			System.out.println("Paralell set to "+currentPoseTarget);
		}
	}
	@Override
	public double[] inverseKinematics(TransformNR taskSpaceTransform) throws Exception {

		int numBerOfLinks = 0;
		for (DHParameterKinematics l : getConstituantLimbs()) {
			numBerOfLinks += l.getNumberOfLinks();
		}
		double[] linkValues = new double[numBerOfLinks];
		int limbOffset = 0;
		HashMap<String, double[]> IKvalues = new HashMap<>();

		for (DHParameterKinematics l : getConstituantLimbs()) {
			// Use the built in IK model for the limb
			double[] jointSpaceVect =compute(l,IKvalues,taskSpaceTransform);
			// Load the link vector into the total vector
			for (int i = 0; i < jointSpaceVect.length; i++) {
				linkValues[limbOffset + i] = jointSpaceVect[i];
			}
			limbOffset += jointSpaceVect.length;
		}
		IKvalues.clear();
		return linkValues;
	}

	@Override
	public TransformNR forwardKinematics(double[] jointSpaceVector) {
		HashMap<DHParameterKinematics, TransformNR> tips = new HashMap<DHParameterKinematics, TransformNR>();

		for (DHParameterKinematics l : getConstituantLimbs()) {
			TransformNR fwd = l.getCurrentTaskSpaceTransform();
			if (fwd == null)
				throw new RuntimeException("Implementations of the kinematics need to return a transform not null");
			// Log.info("Getting robot task space "+fwd);
			tips.put(l, fwd);

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
				roty = -Math.atan2(x * Math.cos(rotx), z);
			} else {
				roty = Math.atan2(x * Math.cos(rotx), -z);
			}
			double rotz = Math.atan2(Math.cos(rotx), Math.sin(rotx) * Math.sin(roty));

			return new TransformNR(x, y, x, new RotationNR(rotx, roty, rotz));
		} else if (getConstituantLimbs().size() == 2) {
			return tips.get(getFKLimb());// assume the first link is
															// in control or
															// orentation
		} else
			throw new RuntimeException("There needs to be at least 2 limbs for paralell");
	}

	/**
	 * Gets the cad engine.
	 *
	 * @return the cad engine
	 */
	public String[] getGitCadToolEngine() {
		return toolEngine;
	}

	/**
	 * Sets the cad engine.
	 *
	 * @param cadEngine the new cad engine
	 */
	public void setGitCadToolEngine(String[] cadEngine) {
		if (cadEngine != null && cadEngine[0] != null && cadEngine[1] != null)
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
	public TransformNR getTipOffset(DHParameterKinematics l) {
		return tipOffset.get(l);
	}
	public void setTipOffset(DHParameterKinematics l,TransformNR n) {
		tipOffset.put(l,n);
	}
	public String getTipOffsetRelativeName(DHParameterKinematics l) {
		return tipOffsetRelativeToName.get(l);
	}
	public int getTipOffsetRelativeIndex(DHParameterKinematics l) {
		return tipOffsetRelativeIndex.get(l);
	}
	public void setTipOffset(HashMap<DHParameterKinematics, TransformNR> tipOffset) {
		this.tipOffset = tipOffset;
	}

	public void removeLimb(DHParameterKinematics limb) {
		if (constituantLimbs.contains(limb)) {
			constituantLimbs.remove(limb);
			getTipOffset().remove(limb);
			setFactory(new LinkFactory());// clear the links
			for (DHParameterKinematics remaining : constituantLimbs) {
				for (LinkConfiguration c : remaining.getFactory().getLinkConfigurations()) {
					getFactory().addLink(remaining.getFactory().getLink(c));// adding the configurations the the single
					// factory
				}
			}
		}
	}

	public String getNameOfParallelGroup() {
		return name;
	}

	public void close() {
		constituantLimbs.clear();
		tipOffset.clear();
		constituantLimbs.clear();
		tipOffsetRelativeToName.clear();

	}

}
