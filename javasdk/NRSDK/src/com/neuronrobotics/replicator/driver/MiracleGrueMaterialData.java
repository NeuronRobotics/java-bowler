package com.neuronrobotics.replicator.driver;

public class MiracleGrueMaterialData extends MaterialData {
	String configData;
	public String toString() {
		return configData;
	}
	public StlSlicer getSlicerForConfig() {
		return new Slic3r(this);
	}
}
