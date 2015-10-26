package com.neuronrobotics.sdk.common.device.server;

import com.neuronrobotics.sdk.common.BowlerMethod;

public interface IBowlerCommandProcessor {
	public Object [] process(Object [] data);
}
