package com.neuronrobotics.sdk.dyio.sequencer;

import java.util.ArrayList;

import com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.IAnalogInputListener;
import com.neuronrobotics.sdk.dyio.peripherals.IServoPositionUpdateListener;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;

public class ServoOutputScheduleChannel implements ISchedulerListener, IAnalogInputListener {
	
	private ServoChannel output;
	AnalogInputChannel input;
	private double inputScale=.25;
	private int inputCenter = 128;
	private int inputValue;
	
	private boolean recording=false;
	private double interval;
	
	private int currentValue;
	private ArrayList<MapData> data = new ArrayList<MapData>();
	private int outputMax=200;
	private int outputMin=50;
	private int index=0;
	private Tester directTester;
	private int analogInputChannelNumber=8;
	public ServoOutputScheduleChannel(ServoChannel srv) {
		output=srv;
		currentValue = output.getValue();
		srv.SetPosition(currentValue);
		srv.flush();
	}
	public int getChannelNumber(){
		return output.getChannel().getChannelNumber();
	}
	public void pauseRecording(){
		System.out.println("pausing recording");
		if(input != null)
			input.removeAnalogInputListener(this);
		setRecording(false);
	}
	public void resumeRecording(){
		if(input==null)
			initInput();
		System.out.println("resuming recording");
		setRecording(true);
	}
	
	
	
	public void addAnalogInputListener(IAnalogInputListener l){
		input.addAnalogInputListener(l);
		input.setAsync(true);
		input.configAdvancedAsyncNotEqual(10);
	}
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
	public void startRecording(){
		initInput();
		resumeRecording();
	}

	@Override
	public void onTimeUpdate(double ms) {
		index = (int) (ms/getInterval());
		while(index>=data.size()){
			data.add(new MapData(currentValue));
		}
			
		if(isRecording())
			data.get(index).input=getCurrentTargetValue();
		currentValue = data.get(index).input;
		//System.out.println("Setting servo value="+data.get(index).input);
	
		output.SetPosition(data.get(index).input);
	}


	@Override
	public void setIntervalTime(int msInterval, int totalTime) {
		setInterval(msInterval);
		int slices = totalTime/msInterval;
		if(data.size()==0){
			System.out.println("Setting up sample data:");
			data = new ArrayList<MapData>();
			setCurrentTargetValue(currentValue);
			if(getCurrentTargetValue()>getOutputMax()){
				setCurrentTargetValue(getOutputMax());
			}
			if(getCurrentTargetValue()<getOutputMin()){
				setCurrentTargetValue(getOutputMin());
			}
			currentValue=getCurrentTargetValue();
			for(int i=0;i<slices;i++){
				data.add(new MapData(currentValue));
			}
			data.add(new MapData(currentValue));
		}
		
	}

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
	
	public void setOutput(ServoChannel output) {
		this.output = output;
	}

	public ServoChannel getOutput() {
		return output;
	}

	public void setInputCenter(int inputCenter) {
		this.inputCenter =inputCenter;
	}
	public int getInputCenter() {
		return inputCenter;
	}

	public void setInputScale(double inputScale) {
		this.inputScale = inputScale;
	}
	public double getInputScale() {
		return inputScale;
	}

	public void setOutputMinMax(int outputMin,int outputMax) {
		this.outputMax = outputMax;
		this.outputMin = outputMin;
	}
	public int getOutputMax() {
		return outputMax;
	}
	public int getOutputMin() {
		return outputMin;
	}
	private class MapData{
		public int input;
		public MapData(int i){
			input=i;
		}
	}
	public boolean isRecording() {
		return recording;
	}
	public void addIServoPositionUpdateListener(IServoPositionUpdateListener l) {
		getOutput().addIServoPositionUpdateListener(l);
	}
	public void removeIServoPositionUpdateListener(IServoPositionUpdateListener l) {
		getOutput().removeIServoPositionUpdateListener(l);
	}
	
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
	public int getInputChannelNumber() {
		if(input!= null)
			return input.getChannel().getChannelNumber();
		return getAnalogInputChannelNumber();
	}
	public void setData(int[] data2) {
		data = new ArrayList<MapData>();
		for(int i=0;i<data2.length;i++){
			data.add(new MapData(data2[i]));
		}
	}
	public void startTest() {
		System.out.println("Starting test for output: "+getChannelNumber());
		initInput();
		directTester = new Tester();
		directTester.start();
	}
	public void stopTest() {
		if(directTester!=null) {
			directTester.kill();
			if(input != null)
				input.removeAnalogInputListener(this);
		}
		directTester=null;
	}
	public boolean isTesting() {
		return directTester!=null;
	}
	
	public void setInterval(double interval) {
		this.interval = interval;
	}
	public double getInterval() {
		return interval;
	}
	public void setCurrentTargetValue(int inputValue) {
		this.inputValue = inputValue;
	}
	public int getCurrentTargetValue() {
		return inputValue;
	}
	public void flush(){
		output.SetPosition(getCurrentTargetValue());
		output.flush();
	}
	public void setAnalogInputChannelNumber(int analogInputChannelNumber) {
		System.out.println("Setting analog input number: "+analogInputChannelNumber);
		this.analogInputChannelNumber = analogInputChannelNumber;
	}
	public int getAnalogInputChannelNumber() {
		if(input != null)
			return input.getChannel().getChannelNumber();
		return analogInputChannelNumber;
	}
	public void setRecording(boolean recording) {
		this.recording = recording;
	}
	private class Tester extends Thread {
		private boolean running=true;
		
		public void run() {
			//System.out.println("Starting Test");
			while(running) {
				flush();
				try {Thread.sleep((long) getInterval());} catch (InterruptedException e) {}
			}
			//System.out.println("Test Done");
		}
		
		public void kill() {
			running=false;
		}
	}
	@Override
	public void onReset() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPlay() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

}
