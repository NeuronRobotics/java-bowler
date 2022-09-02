package com.neuronrobotics.sdk.addons.kinematics;

import java.util.ArrayList;

public interface IHardwareSyncPulseProvider {
	ArrayList<IHardwareSyncPulseReciver> syncPulse = new ArrayList<>();
	
	default public void addIHardwareSyncPulseReciver(IHardwareSyncPulseReciver r) {
		if (syncPulse.contains(r))
			return;
		syncPulse.add(r);
	}

	default public void removeIHardwareSyncPulseReciver(IHardwareSyncPulseReciver r) {
		if (syncPulse.contains(r))
			syncPulse.remove(r);
	}

	default public void doSync() {
		for (IHardwareSyncPulseReciver r : syncPulse) {
			r.sync();
		}
	}
}
