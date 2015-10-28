package com.neuronrobotics.sdk.dyio.sequencer;

import java.util.ArrayList;

import com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.IAnalogInputListener;
import com.neuronrobotics.sdk.dyio.peripherals.IServoPositionUpdateListener;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;

// TODO: Auto-generated Javadoc
/**
 * The Class ServoOutputScheduleChannel.
 */
public class ServoOutputScheduleChannel implements ISchedulerListener, IAnalogInputListener {
	
	/** The output. */
	private ServoChannel output;
	
	/** The input. */
	AnalogInputChannel input;
	
	/** The input scale. */
	private double inputScale=.25;
	
	/** The input center. */
	private int inputCenter = 128;
	
	/** The input value. */
	private int inputValue;
	
	/** The recording. */
	private boolean recording=false;
	
	/** The interval. */
	private double interval;
	
	/** The current value. */
	private int currentValue;
	
	/** The data. */
	private ArrayList<MapData> data = new ArrayList<MapData>();
	
	/** The output max. */
	private int outputMax=255;
	
	/** The output min. */
	private int outputMin=0;
	
	/** The index. */
	private int index=0;
	
	/** The direct tester. */
	private Tester directTester;
	
	/** The analog input channel number. */
	private int analogInputChannelNumber=8;
	
	/**
	 * Instantiates a new servo output schedule channel.
	 *
	 * @param srv the srv
	 */
	public ServoOutputScheduleChannel(ServoChannel srv) {
		output=srv;
		setCurrentValue(output.getValue());

	}
	
	/**
	 * Gets the channel number.
	 *
	 * @return the channel number
	 */
	public int getChannelNumber(){
		return output.getChannel().getChannelNumber();
	}
	
	/**
	 * Pause recording.
	 */
	public void pauseRecording(){
		System.out.println("pausing recording");
		if(input != null)
			input.removeAnalogInputListener(this);
		setRecording(false);
	}
	
	/**
	 * Resume recording.
	 */
	public void resumeRecording(){
		if(input==null)
			initInput();
		System.out.println("resuming recording");
		setRecording(true);
	}
	
	
	
	/**
	 * Adds the analog input listener.
	 *
	 * @param l the l
	 */
	public void addAnalogInputListener(IAnalogInputListener l){
		input.addAnalogInputListener(l);
		input.setAsync(true);
		input.configAdvancedAsyncNotEqual(10);
	}
	
	/**
	 * Inits the input.
	 */
	private void initInput() {
		if(input==null || (input.getChannel().getChannelNumber() != getAnalogInputChannelNumber())){
			input=new AnalogInputChannel(output.getChannel().getDevice().getChannel(analogInputChannelNumber),true);
		}

		if(input.getChannel().getChannelNumber() != analogInputChannelNumber) {
			System.out.println("Re-Setting analog input channel: "+analogInputChannelNumber);
			input.removeAllAnalogInputListeners();
			input=new AnalogInputChannel(output.getChannel().getDevice().getChannel(analogInputChannelNumber),true);
		}
		addAnalogInputListener(this);
	}
	
	/**
	 * Start recording.
	 */
	public void startRecording(){
		initInput();
		resumeRecording();
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.dyio.sequencer.ISchedulerListener#onTimeUpdate(double)
	 */
	@Override
	public void onTimeUpdate(double ms) {
		index = (int) (ms/getInterval());
		while(index>=data.size()){
			data.add(new MapData(getCurrentValue()));
		}
			
		if(isRecording())
			data.get(index).input=getCurrentTargetValue();
		
		setCurrentValue(data.get(index).input);
		
	
		//output.SetPosition(data.get(index).input);
		//System.out.println("Setting servo "+getChannelNumber()+" value="+getCurrentValue());
	}


	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.dyio.sequencer.ISchedulerListener#setIntervalTime(int, int)
	 */
	@Override
	public void setIntervalTime(int msInterval, int totalTime) {
		setInterval(msInterval);
		int slices = totalTime/msInterval;
		if(data.size()==0){
			System.out.println("Setting up sample data: "+msInterval+"ms for: "+totalTime);
			data = new ArrayList<MapData>();
			setCurrentTargetValue(getCurrentValue());
			if(getCurrentTargetValue()>getOutputMax()){
				setCurrentTargetValue(getOutputMax());
			}
			if(getCurrentTargetValue()<getOutputMin()){
				setCurrentTargetValue(getOutputMin());
			}
			setCurrentValue(getCurrentTargetValue());
			for(int i=0;i<slices;i++){
				data.add(new MapData(getCurrentValue()));
			}
			data.add(new MapData(getCurrentValue()));
		}
		
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.dyio.peripherals.IAnalogInputListener#onAnalogValueChange(com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel, double)
	 */
	@Override
	public void onAnalogValueChange(AnalogInputChannel chan, double value) {

		double centerOffset =getInputCenter()-(512*getInputScale());
		//System.out.println("Center Offset="+centerOffset);
		
		double scaled  = (value*getInputScale());
		double recentered =  (scaled+centerOffset);
		
		
		setCurrentTargetValue((int) recentered );
		//System.out.println("Analog value="+(int)value+" scaled="+(int)scaled +" recentered="+(int)recentered);
		if(getCurrentTargetValue()>getOutputMax()){
			setCurrentTargetValue(getOutputMax());
		}
		if(getCurrentTargetValue()<getOutputMin()){
			setCurrentTargetValue(getOutputMin());
		}
	}
	
	/**
	 * Sets the output.
	 *
	 * @param output the new output
	 */
	public void setOutput(ServoChannel output) {
		this.output = output;
	}

	/**
	 * Gets the output.
	 *
	 * @return the output
	 */
	public ServoChannel getOutput() {
		return output;
	}

	/**
	 * Sets the input center.
	 *
	 * @param inputCenter the new input center
	 */
	public void setInputCenter(int inputCenter) {
		this.inputCenter =inputCenter;
	}
	
	/**
	 * Gets the input center.
	 *
	 * @return the input center
	 */
	public int getInputCenter() {
		return inputCenter;
	}

	/**
	 * Sets the input scale.
	 *
	 * @param inputScale the new input scale
	 */
	public void setInputScale(double inputScale) {
		this.inputScale = inputScale;
	}
	
	/**
	 * Gets the input scale.
	 *
	 * @return the input scale
	 */
	public double getInputScale() {
		return inputScale;
	}

	/**
	 * Sets the output min max.
	 *
	 * @param outputMin the output min
	 * @param outputMax the output max
	 */
	public void setOutputMinMax(int outputMin,int outputMax) {
		this.outputMax = outputMax;
		this.outputMin = outputMin;
	}
	
	/**
	 * Gets the output max.
	 *
	 * @return the output max
	 */
	public int getOutputMax() {
		return outputMax;
	}
	
	/**
	 * Gets the output min.
	 *
	 * @return the output min
	 */
	public int getOutputMin() {
		return outputMin;
	}
	
	/**
	 * The Class MapData.
	 */
	private class MapData{
		
		/** The input. */
		public int input;
		
		/**
		 * Instantiates a new map data.
		 *
		 * @param i the i
		 */
		public MapData(int i){
			input=i;
		}
	}
	
	/**
	 * Checks if is recording.
	 *
	 * @return true, if is recording
	 */
	public boolean isRecording() {
		return recording;
	}
	
	/**
	 * Adds the i servo position update listener.
	 *
	 * @param l the l
	 */
	public void addIServoPositionUpdateListener(IServoPositionUpdateListener l) {
		getOutput().addIServoPositionUpdateListener(l);
	}
	
	/**
	 * Removes the i servo position update listener.
	 *
	 * @param l the l
	 */
	public void removeIServoPositionUpdateListener(IServoPositionUpdateListener l) {
		getOutput().removeIServoPositionUpdateListener(l);
	}
	
	/**
	 * Gets the xml.
	 *
	 * @return the xml
	 */
	public String getXml(){
		String s="";
		s+="\t<ServoOutputSequence>\n";
		s+="\t\t<outputMax>"+outputMax+"</outputMax>\n";
		s+="\t\t<outputMin>"+outputMin+"</outputMin>\n";
		s+="\t\t<outputChannel>"+getChannelNumber()+"</outputChannel>\n";
		s+="\t\t<inputEnabled>"+isRecording()+"</inputEnabled>\n";
		s+="\t\t<inputScale>"+inputScale+"</inputScale>\n";
		s+="\t\t<outputCenter>"+inputCenter+"</outputCenter>\n";
		s+="\t\t<inputChannel>"+getAnalogInputChannelNumber()+"</inputChannel>\n";
		s+="\t\t<data>";
		for(int i=0;i<data.size();i++){
			s+=data.get(i).input;
			if(i<data.size()-1)
				s+=",";
		}
		s+=	"</data>\n";
		s+="\t</ServoOutputSequence>\n";
		return s;
	}
	
	/**
	 * Gets the input channel number.
	 *
	 * @return the input channel number
	 */
	public int getInputChannelNumber() {
		if(input!= null)
			return input.getChannel().getChannelNumber();
		return getAnalogInputChannelNumber();
	}
	
	/**
	 * Sets the data.
	 *
	 * @param data2 the new data
	 */
	public void setData(int[] data2) {
		data = new ArrayList<MapData>();
		for(int i=0;i<data2.length;i++){
			data.add(new MapData(data2[i]));
		}
	}
	
	/**
	 * Start test.
	 */
	public void startTest() {
		System.out.println("Starting test for output: "+getChannelNumber());
		initInput();
		directTester = new Tester();
		directTester.start();
	}
	
	/**
	 * Stop test.
	 */
	public void stopTest() {
		if(directTester!=null) {
			directTester.kill();
			if(input != null)
				input.removeAnalogInputListener(this);
		}
		directTester=null;
	}
	
	/**
	 * Checks if is testing.
	 *
	 * @return true, if is testing
	 */
	public boolean isTesting() {
		return directTester!=null;
	}
	
	/**
	 * Sets the interval.
	 *
	 * @param interval the new interval
	 */
	public void setInterval(double interval) {
		this.interval = interval;
	}
	
	/**
	 * Gets the interval.
	 *
	 * @return the interval
	 */
	public double getInterval() {
		return interval;
	}
	
	/**
	 * Sets the current target value.
	 *
	 * @param inputValue the new current target value
	 */
	public void setCurrentTargetValue(int inputValue) {
		try{
		
		}catch( Exception e){}
		this.inputValue = inputValue;
	}
	
	/**
	 * Gets the current target value.
	 *
	 * @return the current target value
	 */
	public int getCurrentTargetValue() {
		return inputValue;
	}
	
	/**
	 * Flush.
	 */
	public void flush(){
		output.SetPosition(getCurrentTargetValue());
		output.flush();
	}
	
	/**
	 * Sets the analog input channel number.
	 *
	 * @param analogInputChannelNumber the new analog input channel number
	 */
	public void setAnalogInputChannelNumber(int analogInputChannelNumber) {
		//System.out.println("Setting analog input number: "+analogInputChannelNumber);
		this.analogInputChannelNumber = analogInputChannelNumber;
	}
	
	/**
	 * Gets the analog input channel number.
	 *
	 * @return the analog input channel number
	 */
	public int getAnalogInputChannelNumber() {
		if(input != null)
			return input.getChannel().getChannelNumber();
		return analogInputChannelNumber;
	}
	
	/**
	 * Sets the recording.
	 *
	 * @param recording the new recording
	 */
	public void setRecording(boolean recording) {
		this.recording = recording;
	}
	
	/**
	 * The Class Tester.
	 */
	private class Tester extends Thread {
		
		/** The running. */
		private boolean running=true;
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			//System.out.println("Starting Test");
			while(running) {
				flush();
				try {Thread.sleep((long) getInterval());} catch (InterruptedException e) {}
			}
			//System.out.println("Test Done");
		}
		
		/**
		 * Kill.
		 */
		public void kill() {
			running=false;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.dyio.sequencer.ISchedulerListener#onReset()
	 */
	@Override
	public void onReset() {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.dyio.sequencer.ISchedulerListener#onPlay()
	 */
	@Override
	public void onPlay() {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.dyio.sequencer.ISchedulerListener#onPause()
	 */
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Gets the current value.
	 *
	 * @return the current value
	 */
	public int getCurrentValue() {
		return currentValue;
	}
	
	/**
	 * Sets the current value.
	 *
	 * @param currentValue the new current value
	 */
	public void setCurrentValue(int currentValue) {
		this.currentValue = currentValue;
	}
	
	/**
	 * Sync.
	 *
	 * @param loopTime the loop time
	 */
	public void sync(int loopTime) {

		getOutput().SetPosition(getCurrentValue(), loopTime);
	}

}
