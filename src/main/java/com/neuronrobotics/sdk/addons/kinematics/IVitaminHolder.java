package com.neuronrobotics.sdk.addons.kinematics;

import java.util.ArrayList;

public interface IVitaminHolder {
	ArrayList<VitaminLocation> getVitamins() ;
	void addVitamin(VitaminLocation location);
	void removeVitamin(VitaminLocation loc);
}
