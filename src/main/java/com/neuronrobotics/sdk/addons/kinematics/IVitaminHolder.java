package com.neuronrobotics.sdk.addons.kinematics;

import java.util.ArrayList;

public interface IVitaminHolder {
	ArrayList<VitaminLocation> getVitamins();

	default void addVitamin(VitaminLocation location) {
		if (hasVitamin(location))
			throw new RuntimeException("Vitamin Name " + location.getName() + "already exists");
		addVitaminInternal(location);
	}

	default boolean hasVitamin(VitaminLocation location) {
		for (VitaminLocation v : getVitamins()) {
			String name = v.getName();
			String name2 = location.getName();
			if (name.contentEquals(name2)) {
				return true;
			}
		}
		return false;
	}

	void addVitaminInternal(VitaminLocation location);

	void removeVitamin(VitaminLocation loc);

	default ArrayList<VitaminLocation> getVitamins(VitaminFrame frame) {
		ArrayList<VitaminLocation> copy = new ArrayList<>();
		for (VitaminLocation v : getVitamins()) {
			if (v.getFrame() == frame)
				copy.add(v);
		}
		return copy;
	}

	default ArrayList<VitaminLocation> getOriginVitamins() {
		return getVitamins(VitaminFrame.LinkOrigin);
	}

	default ArrayList<VitaminLocation> getDefaultVitamins() {
		return getVitamins(VitaminFrame.DefaultFrame);
	}

	default ArrayList<VitaminLocation> getPreviousLinkVitamins() {
		return getVitamins(VitaminFrame.previousLinkTip);
	}
}
