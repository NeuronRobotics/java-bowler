package com.neuronrobotics.sdk.namespace.bcs.pid;


public interface IExtendedPIDControl extends IPidControlNamespace{

	boolean runOutputHysteresisCalibration(int group);
}
