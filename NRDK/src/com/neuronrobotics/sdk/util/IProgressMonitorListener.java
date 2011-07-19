package com.neuronrobotics.sdk.util;

public interface IProgressMonitorListener {
	public void onUpdate(double value);
	public void onComplete();
}
