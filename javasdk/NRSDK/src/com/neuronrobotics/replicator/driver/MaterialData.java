package com.neuronrobotics.replicator.driver;

public class MaterialData {
	public StlSlicer getSlicerForConfig() {
		return new StlSlicer(this);
	}
}
