package com.neuronrobotics.sdk.addons.kinematics;

public enum VitaminFrame {
	// the MobilBase root, or the tip of the link
	DefaultFrame("default"),
	// the place on the link where the previous one ends, where the shaft for the
	// motor that turns it should be
	LinkOrigin("origin"),
	// The tip of the previous link. the place where the motor that turns a link
	// would be mounted. if the first link this would be the limbs root
	previousLinkTip("lastlink");

	private String text;

	VitaminFrame(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public static VitaminFrame fromString(String text) {
		for (VitaminFrame b : VitaminFrame.values()) {
			if (b.text.equalsIgnoreCase(text)) {
				return b;
			}
		}
		return null;
	}
}
