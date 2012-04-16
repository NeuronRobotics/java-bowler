package com.neuronrobotics.sdk.addons.kinematics;

import com.neuronrobotics.sdk.addons.kinematics.AbstractKinematics;
import com.neuronrobotics.sdk.addons.kinematics.LinkFactory;
import com.neuronrobotics.sdk.addons.kinematics.math.Transform;
import com.neuronrobotics.sdk.addons.kinematics.xml.XmlFactory;
import com.neuronrobotics.sdk.dyio.DyIO;


public class TrobotKinematics extends AbstractKinematics {
	
	private DHChain chain=null;

	public TrobotKinematics() {
		this(null,"TrobotLinks.xml");
	}
	
	public TrobotKinematics( DyIO dev) {
		this(dev,"TrobotLinks.xml");

	}
	public TrobotKinematics( DyIO dev, String configFile) {
		super(XmlFactory.getDefaultConfigurationStream(configFile),new LinkFactory( dev));
		chain = new DHChain(getFactory().getUpperLimits(), getFactory().getLowerLimits(),false);
		
	}

	@Override
	public double[] inverseKinematics(Transform taskSpaceTransform)throws Exception {
		return getDhChain().inverseKinematics(taskSpaceTransform, getCurrentJointSpaceVector());
	}

	@Override
	public Transform forwardKinematics(double[] jointSpaceVector) {
		if(jointSpaceVector == null || getDhChain() == null)
			return new Transform();
		return getDhChain().forwardKinematics(jointSpaceVector);
	}

	public void setDhChain(DHChain chain) {
		this.chain = chain;
	}

	public DHChain getDhChain() {
		return chain;
	}

}
