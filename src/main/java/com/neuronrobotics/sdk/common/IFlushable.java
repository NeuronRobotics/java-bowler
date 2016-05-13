package com.neuronrobotics.sdk.common;

public interface IFlushable {
	/**
	 * This interface says the device can cache values and flush them in one push
	 * @param seconds the duration of the flush, from current position and time to cached positions in this many seconds
	 */
	public void flush(double seconds);
}
