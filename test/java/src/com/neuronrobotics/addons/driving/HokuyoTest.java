package com.neuronrobotics.addons.driving;

import java.util.ArrayList;

import gnu.io.NRSerialPort;

public class HokuyoTest implements ISensorListener {
	
	private SimpleDisplay display = new SimpleDisplay();
	
	private HokuyoTest(){
		LaserRangeSensor range = new LaserRangeSensor(new NRSerialPort("COM86", 115200));
		range.addSensorListener(this);
		range.StartSweep(-90, 90, 10);
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	public static void main(String [] args){
		new HokuyoTest();
	}

	@Override
	public void onRangeSensorEvent(AbstractSensor source,ArrayList<DataPoint> data, long timeStamp) {
		//System.out.println("Range Sensor Event "+data);
		display.setData(data);
		source.StartSweep(-90, 90, 10);
	}

	@Override
	public void onLineSensorEvent(AbstractSensor source, Integer left,Integer middle, Integer right, long timeStamp) {
		//unused
	}
}
