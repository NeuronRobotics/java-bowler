package com.neuronrobotics.sdk.pid;

import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration;

public interface ILinkFactoryProvider {
	
	LinkConfiguration requestLinkConfiguration(int index);

}
