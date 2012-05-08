package com.neuronrobotics.sdk.types;

import com.neuronrobotics.sdk.common.ByteList;

public class DigitalInput implements IDataType {
	@SuppressWarnings("unused")
	private boolean value;
	private boolean inverted = false;
	private boolean cached = true;
	
	public void setValue(boolean isHigh) {
		value = isHigh;
	}
	
	public void setOn(boolean isOn) {
		value = isOn & !inverted;
	}
	
	public void setInverted(boolean isInverted) {
		inverted = isInverted;
	}
	
	
	public void setValue(ByteList value) {
		setValue(value.get(0) > 0);
	}

	
	public void setCached(boolean isCached) {
		cached = isCached;
	}

	
	public boolean isCached() {
		return cached;
	}
}
