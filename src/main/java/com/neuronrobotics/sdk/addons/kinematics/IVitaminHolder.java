package com.neuronrobotics.sdk.addons.kinematics;

import java.util.ArrayList;

public interface IVitaminHolder {
	ArrayList<VitaminLocation> getVitamins() ;
	void addVitamin(VitaminLocation location);
	void removeVitamin(VitaminLocation loc);
	default ArrayList<VitaminLocation> getVitamins(VitaminFrame frame){
		ArrayList<VitaminLocation> copy = new ArrayList<>();
		for(VitaminLocation v:getVitamins()) {
			if(v.getFrame()==frame)
				copy.add(v);
		}
		
		return copy;
	}
	default ArrayList<VitaminLocation> getOriginVitamins(){
		return getVitamins(VitaminFrame.LinkOrigin);
	}
	default ArrayList<VitaminLocation> getDefaultVitamins(){
		return getVitamins(VitaminFrame.DefaultFrame);
	}
	default ArrayList<VitaminLocation> getPreviousLinkVitamins(){
		return getVitamins(VitaminFrame.previousLinkTip);
	}
}
