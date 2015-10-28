package com.neuronrobotics.replicator.driver;

// TODO: Auto-generated Javadoc
/**
 * The Class MaterialData.
 */
public class MaterialData {
	
	/**
	 * Gets the slicer for config.
	 *
	 * @return the slicer for config
	 */
	public StlSlicer getSlicerForConfig() {
		return new StlSlicer(this);
	}
}
