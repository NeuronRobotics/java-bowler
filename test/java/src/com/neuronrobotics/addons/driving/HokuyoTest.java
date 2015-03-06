package com.neuronrobotics.addons.driving;

import gnu.io.NRSerialPort;

import java.util.ArrayList;

import javax.swing.JFrame;

import com.neuronrobotics.addons.driving.virtual.ObsticleType;

public class HokuyoTest implements ISensorListener {
	
	private SimpleDisplay display = new SimpleDisplay();
	private JFrame frame = new JFrame();
	private long start;
	private HokuyoTest(){
		LaserRangeSensor range = new LaserRangeSensor(new NRSerialPort("/dev/ttyACM0", 115200));
		range.addSensorListener(this);
		start=System.currentTimeMillis();
		range.StartSweep(-90, 90, .5);
		frame.add(display);
		frame.setSize(1024, 768);
		frame.setVisible(true);
		frame.setExtendedState(JFrame.EXIT_ON_CLOSE);
		
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
		System.out.println("Range Sensor Event "+(System.currentTimeMillis()-start));
		display.setUserDefinedData(data,ObsticleType.USERDEFINED);
		start=System.currentTimeMillis();
		source.StartSweep(-90, 90, .5);
		
	}

	@Override
	public void onLineSensorEvent(AbstractSensor source, Integer left,Integer middle, Integer right, long timeStamp) {
		//unused
	}
}
