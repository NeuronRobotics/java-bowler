package com.neuronrobotics.addons.driving;

import java.util.ArrayList;

public interface ISensorListener {
	public void onRangeSensorEvent(AbstractSensor source,ArrayList<DataPoint> data,long timeStamp);
	public void onLineSensorEvent(AbstractSensor source,Integer left,Integer  middle,Integer  right,long timeStamp);
}
