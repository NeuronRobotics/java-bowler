package com.neuronrobotics.addons.driving;

import java.util.ArrayList;

public interface ISensorListener {
	public void onRangeSensorEvent(ArrayList<DataPoint> data,long timeStamp);
	public void onLineSensorEvent(Integer left,Integer  middle,Integer  right,long timeStamp);
}
