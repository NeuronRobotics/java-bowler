package com.neuronrobotics.sdk.dyio.sequencer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.neuronrobotics.sdk.addons.walker.WalkerServoLink;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;
import com.neuronrobotics.sdk.util.ThreadUtil;
/**
 * 
 * @author hephaestus
 *
 */
public class CoreScheduler {
	private int loopTime;
	private long flushTime = 0; 
	private SchedulerThread st=null;
	private SequencerMP3 mp3;
	private boolean loop = false;
	private ArrayList< ISchedulerListener> listeners = new ArrayList< ISchedulerListener>();
	private ArrayList< ServoOutputScheduleChannel> outputs = new ArrayList< ServoOutputScheduleChannel>();
	private DyIO dyio;
	private String filename=null;
	private int msDuration=0;
	//private int trackLength;
	private File audioFile=null;
	DyIOFlusher flusher;
	private long StartOffset;
	public CoreScheduler(DyIO d, int loopTime,int duration ){
		setDyIO(d);
		this.setLoopTime(loopTime);
		msDuration=duration;
	}
	
	public CoreScheduler(DyIO d, File f){
		setDyIO(d);
		loadFromFile(f);
	}
	
	/**
	 * 
	 * @param f The file to load the scheduler configuration from. This should be an xml.
	 */
	
	public void loadFromFile(File f){
		/**
		 * sample code from
		 * http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
		 */
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder;
	    Document doc = null;
	    try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(new FileInputStream(f));
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		//System.out.println("Parsing File...");
		NodeList nList = doc.getElementsByTagName("ServoOutputSequenceGroup");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			//System.out.println("Leg # "+temp);
		    Node nNode = nList.item(temp);
		    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		    	Element eElement = (Element) nNode;
		    	
		    	String filename = getTagValue("mp3",eElement);
		    	if(filename!=null){
		    		setAudioFile(new File(filename));
		    	}else{
		    		msDuration = Integer.parseInt(getTagValue("duration",eElement));
		    	}
		    	setLoopTime(Integer.parseInt(getTagValue("loopTime",eElement)));
		    	NodeList links = eElement.getElementsByTagName("ServoOutputSequence");
		    	for (int i = 0; i < links.getLength(); i++) {
		    		//System.out.println("\tLink # "+i);
		    		Node lNode = links.item(i);
		    		if (lNode.getNodeType() == Node.ELEMENT_NODE) {
			    		Element lElement = (Element) lNode;
			    		int max=Integer.parseInt(getTagValue("outputMax",lElement));
			    		int min=Integer.parseInt(getTagValue("outputMin",lElement));
			    		int channel=Integer.parseInt(getTagValue("outputChannel",lElement));
			    		boolean enabled = getTagValue("inputEnabled",lElement).contains("true");
			    		
			    		double inScale=Double.parseDouble(getTagValue("inputScale",lElement));
			    		int outCenter=Integer.parseInt(getTagValue("outputCenter",lElement));
			    		int inChannel=Integer.parseInt(getTagValue("inputChannel",lElement));
			    		
			    		String [] sdata =  getTagValue("data",lElement).split(",");
			    		int []data=new int[sdata.length];
			    		for(int j=0;j<data.length;j++){
			    			data[j]=Integer.parseInt(sdata[j]);
			    		}
			    		ServoOutputScheduleChannel so = addServoChannel(channel);
			    		so.setOutputMinMax(min,max);
			    		so.setInputCenter(outCenter);
			    		so.setInputScale(inScale);
			    		so.setAnalogInputChannelNumber(inChannel);
			    		if(!enabled){
			    			so.pauseRecording();
			    		}else {
			    			so.startRecording();
			    		}
			    		so.setData(data);
		    		}
		    	}

		    }else{
		    	//System.out.println("Not Element Node");
		    }
		}
		System.out.println("Populated Scheduler");
	}
	private static String getTagValue(String sTag, Element eElement){
	    NodeList nlList= eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	    Node nValue = (Node) nlList.item(0); 
	    //System.out.println("\t\t"+sTag+" = "+nValue.getNodeValue());
	    return nValue.getNodeValue();    
	}
	public void setAudioFile(File f) {
		if( audioFile==f || f==null)
			return;
		audioFile=f;
		filename=f.getAbsolutePath();
    	mp3 = new SequencerMP3(f.getAbsolutePath());
    	msDuration = mp3.getTrackLength();
    	setSequenceParams( msDuration, 0);
    	
	}
	public int getTrackLength(){
		return msDuration;
	}
	public void setLooping(boolean b){
		loop=b;
	}
	private boolean isLooping(){
		return loop;
	}
	public boolean isPlaying() {
		if(getSt() !=null)
			return !getSt().isPause();
		return false;
	}
	
	public ServoOutputScheduleChannel addServoChannel(int dyIOChannel){
		System.out.println("Adding DyIO channel: "+dyIOChannel);
		ServoChannel srv = new ServoChannel(getDyIO().getChannel(dyIOChannel));
		srv.SetPosition(srv.getValue());
		srv.flush();
		srv.getChannel().setCachedMode(true);
		ServoOutputScheduleChannel soc = new ServoOutputScheduleChannel(srv);
		soc.setIntervalTime(getLoopTime(), getTrackLength());
		addISchedulerListener(soc);
		//soc.setIntervalTime(loopTime);
		getOutputs().add(soc);
		return soc;
	}
	
	public void removeServoOutputScheduleChannel(ServoOutputScheduleChannel s){
		getOutputs().remove(s);
	}
	
	public void setSequenceParams(int setpoint,long StartOffset){
		msDuration=setpoint;
		this.StartOffset=StartOffset;
		//System.out.println("Starting scheduler setpoint="+setpoint+" offset="+StartOffset);
		if(getSt()==null)
			setSt(new SchedulerThread(msDuration,StartOffset));
		if(mp3!=null)
			mp3.setCurrentTime((int) StartOffset);
	}
	
	public void playStep() {
		if(getSt()!=null){
			getSt().playStep();
		}else{
			throw new RuntimeException("The sequence paramaters are not set");
		}
	}
	public void play(){
		getSt().setPause(false);
		callPlay();
	}
	public void play(int setpoint,long StartOffset) {
		setSequenceParams( setpoint, StartOffset);
		play();
	}
	public void pause() {
		if(getSt()!=null)
			getSt().pause();
		callPause();
	}
	
	public void addISchedulerListener(ISchedulerListener l){
		for(ISchedulerListener sl:listeners){
			if(sl==l)
				return;
		}
		listeners.add(l);
	}
	public void removeISchedulerListener(ISchedulerListener l){
		listeners.remove(l);
	}
	public void setCurrentTime(long time) {
		flusher.setFlush();
		for(ISchedulerListener l:listeners){
			l.onTimeUpdate(time);
		}

	}
	
	private void callReset(){
		for(ISchedulerListener l:listeners){
			l.onReset();
		}
	}
	private void callPause(){
		for(ISchedulerListener l:listeners){
			l.onPause();
		}
	}
	private void callPlay(){
		for(ISchedulerListener l:listeners){
			l.onPlay();
		}
	}
	public String getXml(){
		String s="";
		s+="<ServoOutputSequenceGroup>\n";
		if(mp3!=null){
			s+="\t<mp3>"+filename+"</mp3>\n";
		}else{
			s+="\t<duriation>"+msDuration+"</duriation>\n";
		}	
		s+="\t<loopTime>"+getLoopTime()+"</loopTime>\n";
		for(ServoOutputScheduleChannel so:getOutputs()){
			s+=so.getXml();
		}
		s+="</ServoOutputSequenceGroup>\n";
		return s;
	}
	
	public void setOutputs(ArrayList< ServoOutputScheduleChannel> outputs) {
		this.outputs = outputs;
	}

	public ArrayList< ServoOutputScheduleChannel> getOutputs() {
		return outputs;
	}
	
	private class DyIOFlusher extends Thread{
		private boolean running = true;
		private boolean flush = false;
		public void run(){
			while(isRunning()){
				if(isFlush()){
					flush = false;
					long start = System.currentTimeMillis();
					if(getDyIO()!=null){
						//Log.enableDebugPrint(true);
						double seconds =((double)(getLoopTime()))/1000;
						getDyIO().flushCache(seconds);
						//Log.enableDebugPrint(false);
					}
					flushTime = System.currentTimeMillis()-start;
					if(flushTime>getLoopTime()){
						System.err.println("Flush took:"+flushTime+ " and loop time="+getLoopTime());
						flushTime=getLoopTime();
					}
				}else{
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		public boolean isRunning() {
			return running;
		}
		public void setFlush() {
			this.flush = true;
		}
		public boolean isFlush() {
			return flush;
		}
	}
	


	private class SchedulerThread extends Thread{
		private double time;
		private boolean run = true;

		long start = System.currentTimeMillis();
		private boolean pause = false;
		
		public SchedulerThread(double ms,final long so){
			time = ms;
			StartOffset=so;
			//System.out.println("Slider value of init="+StartOffset);
			if(mp3!=null) {
				mp3.setCurrentTime((int) (StartOffset));
			}
			for(ServoOutputScheduleChannel s:getOutputs()){
				s.setIntervalTime(getLoopTime(), (int) time);
			}
		}
		public boolean isPlaying() {
			return run;
		}
		public void playStep(){
			//System.out.println("Stepping scheduler");
			boolean playing;
			long current;
			if(mp3==null){
				
				playing = (((double)(System.currentTimeMillis()-start))<(time-StartOffset));
				current =((System.currentTimeMillis()-start))+StartOffset;
			}else{
				mp3.playStep();
				playing = mp3.isPlaying();
				current = mp3.getCurrentTime();
			}
			if(!playing){
				kill();
				return;
			}
				
			setCurrentTime(current);
		}
		public void run(){
			//System.out.println("Starting timer");
			do{
				do{
					while(pause){
						ThreadUtil.wait(10);
					}
					playStep();
				}while(isRun());
				setCurrentTime(0);
				setPause(true);
				callReset();
				callPause();
			}while(true);
		}
		public void pause(){
			if(mp3!=null) {
				mp3.pause();
			}
			setPause(true);
		}
		public void kill(){
			if(mp3!=null) {
				mp3.pause();
			}
			setPause(false);
		}
		public boolean isRun() {
			if(mp3!=null){
				return run && mp3.isPlaying();
			}
			return run;
		}
//		public void setRun(boolean run) {
//			this.run = run;
//		}
		public boolean isPause() {
			return pause;
		}
		public void setPause(boolean pause) {
			this.pause = pause;
		}
	}

	public File getAudioFile() {
		return audioFile;
	}

	public void setDyIO(DyIO dyio) {
		this.dyio = dyio;
		flusher = new DyIOFlusher();
		flusher.start();
	}

	public DyIO getDyIO() {
		return dyio;
	}

	public void setLoopTime(int loopTime) {
		this.loopTime = loopTime;
	}

	public int getLoopTime() {
		return loopTime;
	}

	public SchedulerThread getSt() {
		return st;
	}

	public void setSt(SchedulerThread st) {
		this.st = st;
		st.setPause(true);
		st.start();
	}




}
