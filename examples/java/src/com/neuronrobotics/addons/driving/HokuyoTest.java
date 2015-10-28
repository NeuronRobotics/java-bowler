package com.neuronrobotics.addons.driving;

import gnu.io.NRSerialPort;

import java.util.ArrayList;

import javax.swing.JFrame;

import com.neuronrobotics.addons.driving.virtual.ObsticleType;

// TODO: Auto-generated Javadoc
/**
 * The Class HokuyoTest.
 */
public class HokuyoTest implements ISensorListener {
	
	/** The display. */
	private SimpleDisplay display = new SimpleDisplay();
	
	/** The frame. */
	private JFrame frame = new JFrame();
	
	/** The start. */
	private long start;
	
	/**
	 * Instantiates a new hokuyo test.
	 */
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

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String [] args){
		new HokuyoTest();
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.ISensorListener#onRangeSensorEvent(com.neuronrobotics.addons.driving.AbstractSensor, java.util.ArrayList, long)
	 */
	@Override
	public void onRangeSensorEvent(AbstractSensor source,ArrayList<DataPoint> data, long timeStamp) {
		System.out.println("Range Sensor Event "+(System.currentTimeMillis()-start));
		display.setUserDefinedData(data,ObsticleType.USERDEFINED);
		start=System.currentTimeMillis();
		source.StartSweep(-90, 90, .5);
		
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.ISensorListener#onLineSensorEvent(com.neuronrobotics.addons.driving.AbstractSensor, java.lang.Integer, java.lang.Integer, java.lang.Integer, long)
	 */
	@Override
	public void onLineSensorEvent(AbstractSensor source, Integer left,Integer middle, Integer right, long timeStamp) {
		//unused
	}
}
