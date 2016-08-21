package com.neuronrobotics.sdk.addons.kinematics;

public interface INewLinkProvider {
	/**
	 * THis interface if for providing new link providers to the LinkFactory system
	 * @param conf
	 * @return
	 */
	AbstractLink generate(LinkConfiguration conf);
}
