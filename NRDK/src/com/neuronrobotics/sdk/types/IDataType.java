package com.neuronrobotics.sdk.types;

import com.neuronrobotics.sdk.common.ByteList;

public interface IDataType {
	public void setValue(ByteList value);
	
	public void setCached(boolean isCached);
	public boolean isCached();
}
