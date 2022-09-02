package com.neuronrobotics.sdk.addons.kinematics;

import java.util.ArrayList;
import java.util.HashMap;

public interface IHardwareSyncPulseProvider {
	HashMap<IHardwareSyncPulseProvider,ArrayList<IHardwareSyncPulseReciver>>syncPulse=new HashMap<>();

	
	default public void addIHardwareSyncPulseReciver(IHardwareSyncPulseReciver r) {
		if (getListeners().contains(r) || r==this)
			return;
		getListeners().add(r);
	}

	default public void removeIHardwareSyncPulseReciver(IHardwareSyncPulseReciver r) {
		if (getListeners().contains(r))
			getListeners().remove(r);
	}

	default public void doSync() {
		for (IHardwareSyncPulseReciver r : getListeners()) {
			if(r!=this)
				r.sync();
		}
	}
	
	default public ArrayList<IHardwareSyncPulseReciver> getListeners(){
		if(syncPulse.get(this)==null)
			syncPulse.put(this, new ArrayList<>());
		return syncPulse.get(this);
	}
}
