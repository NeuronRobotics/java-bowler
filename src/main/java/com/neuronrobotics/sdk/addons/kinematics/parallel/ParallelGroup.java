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

public class ParallelGroup extends DHParameterKinematics {

	private ArrayList<DHParameterKinematics> constituantLimbs = new ArrayList<DHParameterKinematics>();
	private HashMap<DHParameterKinematics, TransformNR> tipOffset = new HashMap<DHParameterKinematics, TransformNR>();
	private HashMap<DHParameterKinematics, String> tipOffsetRelativeToName = new HashMap<>();
	private HashMap<DHParameterKinematics, Integer> tipOffsetRelativeIndex = new HashMap<>();
	/** The cad engine. */
	private String[] toolEngine = new String[] { "https://gist.github.com/33f2c10ab3adc5bd91f0a58ea7f24d14.git",
			"parallelTool.groovy" };
	private String name;
	
	public TransformNR getTipOffsetFromThisLinkInLimb(DHParameterKinematics control,int index) {
		String name = control.getScriptingName();
		for(DHParameterKinematics s:tipOffsetRelativeToName.keySet()) {
			String refName = tipOffsetRelativeToName.get(s);
			if(refName.contentEquals(name)) {
				if(index==tipOffsetRelativeIndex.get(s)) {
					return getTipOffset(s);
				}
			}
		}
		return null;
	}

	public ParallelGroup(String name) {
		this.name = name;
		if (name==null)
			throw new RuntimeException();
	}

	public void addLimb(DHParameterKinematics limb, TransformNR tip, String name, int index) {

		setupReferencedLimb(limb, tip, name, index);

	}

	public DHParameterKinematics getFKLimb() {
		for (DHParameterKinematics d : getConstituantLimbs()) {
			if (getTipOffset(d) == null) {
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
		for (DHParameterKinematics d : getConstituantLimbs()) {
			if (d.getScriptingName().contentEquals(name)) {
				setupReferencedLimbStartup(limb, tip, name, index);
				return;
			}
		}
		throw new RuntimeException("Limb named: " + name + " does not exist");
	}

	public void setupReferencedLimbStartup(DHParameterKinematics limb, TransformNR tip, String name, int index) {
		if (!getConstituantLimbs().contains(limb)) {
			getConstituantLimbs().add(limb);
			for (LinkConfiguration c : limb.getFactory().getLinkConfigurations()) {

				getFactory().addLink(limb.getFactory().getLink(c));// adding the configurations the the single
				// factory
			}
		}
		if (tip != null) {
			tipOffsetRelativeToName.put(limb, name);
			tipOffsetRelativeIndex.put(limb, index);
			getTipOffset().put(limb, tip);
			System.out.println("Limb "+limb.getScriptingName()+" set relative to "+name);
		} else {
			clearReferencedLimb(limb);
			DHParameterKinematics fk=getFKLimb();
//			fk.addPoseUpdateListener(new ITaskSpaceUpdateListenerNR() {
//				@Override
//				public void onTaskSpaceUpdate(AbstractKinematicsNR source, TransformNR pose) {
//
//				}
//				
//				@Override
//				public void onTargetTaskSpaceUpdate(AbstractKinematicsNR source, TransformNR pose) {
//					HashMap<String, double[]> IKvalues = new HashMap<>();
//					for (DHParameterKinematics d : getConstituantLimbs()) {
//						if (getTipOffset(d) != null) {
//							try {
//								//System.out.println("Setting Kinematics for follower "+d.getScriptingName());
//								double[] jointSpaceVect = compute(d, IKvalues, pose);
//								//System.out.println(fk.getScriptingName()+" is Setting sublimb target "+d.getScriptingName());
//								d.throwExceptionOnJointLimit(false);
//								d.setDesiredJointSpaceVector(jointSpaceVect, 0);
//							} catch (Exception e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
//					}
//					IKvalues.clear();
//				}
//			});
		}
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

	private double[] compute(DHParameterKinematics ldh, HashMap<String, double[]> IKvalues,
			TransformNR taskSpaceTransform) throws Exception {
		String scriptingName = ldh.getScriptingName();
		if (IKvalues.get(scriptingName) == null) {
			//System.out.println("Perform IK "+ldh.getScriptingName());
			if (getTipOffset().get(ldh) == null) {
				// no offset, compute as normal
				double[] jointSpaceVect = ldh.inverseKinematics(ldh.inverseOffset(taskSpaceTransform));
				IKvalues.put(scriptingName, jointSpaceVect);
			} else {
				TransformNR offset = getTipOffset().get(ldh);
				String refLimbName = tipOffsetRelativeToName.get(ldh);
				int index = tipOffsetRelativeIndex.get(ldh);
				DHParameterKinematics referencedLimb = findReferencedLimb(refLimbName);
				if (referencedLimb == null)
					throw new RuntimeException("Referenced limb missing, IK for " + ldh.getScriptingName()
							+ " Failed looking for " + refLimbName);
				double[] jointSpaceVectReferenced = compute(referencedLimb, IKvalues, taskSpaceTransform);
	
				TransformNR transformTOLinksTip = referencedLimb.getChain().getChain(jointSpaceVectReferenced).get(index)
						.times(offset.inverse());
				double[] jointSpaceVect = ldh.inverseKinematics(ldh.inverseOffset(transformTOLinksTip));
				IKvalues.put(scriptingName, jointSpaceVect);
			}
		}
		return IKvalues.get(scriptingName);
	}

	private DHParameterKinematics findReferencedLimb(String refLimbName) {
		DHParameterKinematics referencedLimb = null;
		for (DHParameterKinematics lm : getConstituantLimbs()) {
			if (lm.getScriptingName().toLowerCase().contentEquals(refLimbName.toLowerCase())) {
				// FOund the referenced limb
				referencedLimb = lm;
			}else {
				//System.out.println("Searching for "+refLimbName+" no match with "+lm.getScriptingName());
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
		if (checkTaskSpaceTransform(currentPoseTarget)) {
			super.setCurrentPoseTarget(currentPoseTarget);
			//System.out.println("Paralell set to " + currentPoseTarget);
		}
	}
	public double[] getCurrentJointSpaceVector(DHParameterKinematics k) {
		// TODO Auto-generated method stub
		return null;
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
			double[] jointSpaceVect = compute(l, IKvalues, taskSpaceTransform);
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
		for (DHParameterKinematics l : getConstituantLimbs()) {
			if(l==getFKLimb())
				return l.getCurrentTaskSpaceTransform();

		}
		throw new RuntimeException( "FK limb is missing!");
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

//	public void setConstituantLimbs(ArrayList<DHParameterKinematics> constituantLimbs) {
//		this.constituantLimbs = constituantLimbs;
//	}

	public HashMap<DHParameterKinematics, TransformNR> getTipOffset() {
		return tipOffset;
	}

	public TransformNR getTipOffset(DHParameterKinematics l) {
		return tipOffset.get(l);
	}

	public void setTipOffset(DHParameterKinematics l, TransformNR n) {
		tipOffset.put(l, n);
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
			clearReferencedLimb( limb);
			constituantLimbs.remove(limb);
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
