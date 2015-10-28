package com.neuronrobotics.sdk.dyio.sequencer;

import java.io.File;
import com.neuronrobotics.sdk.common.Log;
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

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;
import com.neuronrobotics.sdk.util.ThreadUtil;
// TODO: Auto-generated Javadoc

/**
 * The Class CoreScheduler.
 *
 * @author hephaestus
 */
public class CoreScheduler {
	
	/** The loop time. */
	private int loopTime;
	
	/** The flush time. */
	private long flushTime = 0; 
	
	/** The st. */
	private SchedulerThread st=null;
	
	/** The mp3. */
	private SequencerMP3 mp3;
	
	/** The loop. */
	private boolean loop = false;
	
	/** The listeners. */
	private ArrayList< ISchedulerListener> listeners = new ArrayList< ISchedulerListener>();
	
	/** The outputs. */
	private ArrayList< ServoOutputScheduleChannel> outputs = new ArrayList< ServoOutputScheduleChannel>();
	
	/** The dyio. */
	private DyIO dyio;
	
	/** The filename. */
	private String filename=null;
	
	/** The ms duration. */
	private int msDuration=0;
	
	/** The audio file. */
	//private int trackLength;
	private File audioFile=null;
	
	/** The flusher. */
	DyIOFlusher flusher;
	
	/** The Start offset. */
	private long StartOffset;
	
	/**
	 * Instantiates a new core scheduler.
	 *
	 * @param d the d
	 * @param loopTime the loop time
	 * @param duration the duration
	 */
	public CoreScheduler(DyIO d, int loopTime,int duration ){
		setDyIO(d);
		this.setLoopTime(loopTime);
		msDuration=duration;
	}
	
	/**
	 * Instantiates a new core scheduler.
	 *
	 * @param d the d
	 * @param f the f
	 */
	public CoreScheduler(DyIO d, File f){
		setDyIO(d);
		loadFromFile(f);
	}
	
	/**
	 * Load from file.
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
			    		// smooth out and out of place zeros. i should figure out how they keep sneeking in here...
			    		for(int j=1;j<data.length-1;j++){
			    			int before = data[j-1];
			    			int current = data[j];
			    			int after = data[j+1];
			    			if(current == 0 &&before!=0 &&  after!=0){
			    				System.out.println("Smoothing xml");
			    				data[j]=(before+after)/2;
			    			}
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
	
	/**
	 * Gets the tag value.
	 *
	 * @param sTag the s tag
	 * @param eElement the e element
	 * @return the tag value
	 */
	private static String getTagValue(String sTag, Element eElement){
	    NodeList nlList= eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	    Node nValue = (Node) nlList.item(0); 
	    //System.out.println("\t\t"+sTag+" = "+nValue.getNodeValue());
	    return nValue.getNodeValue();    
	}
	
	/**
	 * Sets the audio file.
	 *
	 * @param f the new audio file
	 */
	public void setAudioFile(File f) {
		if( audioFile==f || f==null)
			return;
		audioFile=f;
		filename=f.getAbsolutePath();
    	mp3 = new SequencerMP3(f.getAbsolutePath());
    	msDuration = mp3.getTrackLength();
    	System.out.println("Setting track length: "+msDuration);
    	setSequenceParams( msDuration, 0);
    	
	}
	
	/**
	 * Gets the track length.
	 *
	 * @return the track length
	 */
	public int getTrackLength(){
		return msDuration;
	}
	
	/**
	 * Sets the looping.
	 *
	 * @param b the new looping
	 */
	public void setLooping(boolean b){
		loop=b;
	}
	
	/**
	 * Checks if is looping.
	 *
	 * @return true, if is looping
	 */
	private boolean isLooping(){
		return loop;
	}
	
	/**
	 * Checks if is playing.
	 *
	 * @return true, if is playing
	 */
	public boolean isPlaying() {
		if(getSt() !=null)
			return !getSt().isPause();
		return false;
	}
	
	/**
	 * Adds the servo channel.
	 *
	 * @param dyIOChannel the dy io channel
	 * @return the servo output schedule channel
	 */
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
	
	/**
	 * Removes the servo output schedule channel.
	 *
	 * @param s the s
	 */
	public void removeServoOutputScheduleChannel(ServoOutputScheduleChannel s){
		getOutputs().remove(s);
	}
	
	/**
	 * Sets the sequence params.
	 *
	 * @param setpoint the setpoint
	 * @param StartOffset the start offset
	 */
	public void setSequenceParams(int setpoint,long StartOffset){
		msDuration=setpoint;
		this.StartOffset=StartOffset;
		//System.out.println("Starting scheduler setpoint="+setpoint+" offset="+StartOffset);
		if(getSt()==null)
			setSt(new SchedulerThread(msDuration,StartOffset));
		if(mp3!=null)
			mp3.setCurrentTime((int) StartOffset);
	}
	
	/**
	 * Play step.
	 */
	public void playStep() {
		if(getSt()!=null){
			getSt().playStep();
		}else{
			throw new RuntimeException("The sequence paramaters are not set");
		}
	}
	
	/**
	 * Play.
	 */
	public void play(){
		mp3.play();
		getSt().setPause(false);
		callPlay();
		ThreadUtil.wait(100);
	}
	
	/**
	 * Play.
	 *
	 * @param setpoint the setpoint
	 * @param StartOffset the start offset
	 */
	public void play(int setpoint,long StartOffset) {
		setSequenceParams( setpoint, StartOffset);
		play();
	}
	
	/**
	 * Pause.
	 */
	public void pause() {
		if(getSt()!=null)
			getSt().pause();
		mp3.pause();
		callPause();
	}
	
	/**
	 * Adds the i scheduler listener.
	 *
	 * @param l the l
	 */
	public void addISchedulerListener(ISchedulerListener l){
		for(ISchedulerListener sl:listeners){
			if(sl==l)
				return;
		}
		listeners.add(l);
	}
	
	/**
	 * Removes the i scheduler listener.
	 *
	 * @param l the l
	 */
	public void removeISchedulerListener(ISchedulerListener l){
		listeners.remove(l);
	}
	
	/**
	 * Sets the current time.
	 *
	 * @param time the new current time
	 */
	public void setCurrentTime(long time) {
		
		for(ServoOutputScheduleChannel s :getOutputs()){
			s.onTimeUpdate(time);
		}
		flusher.setFlush();
		for(ISchedulerListener l:listeners){
			l.onTimeUpdate(time);
		}

	}
	
	/**
	 * Call reset.
	 */
	private void callReset(){
		for(ISchedulerListener l:listeners){
			l.onReset();
		}
	}
	
	/**
	 * Call pause.
	 */
	private void callPause(){
		for(ISchedulerListener l:listeners){
			l.onPause();
		}
	}
	
	/**
	 * Call play.
	 */
	private void callPlay(){
		for(ISchedulerListener l:listeners){
			l.onPlay();
		}
	}
	
	/**
	 * Gets the xml.
	 *
	 * @return the xml
	 */
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
	
	/**
	 * Sets the outputs.
	 *
	 * @param outputs the new outputs
	 */
	public void setOutputs(ArrayList< ServoOutputScheduleChannel> outputs) {
		this.outputs = outputs;
	}

	/**
	 * Gets the outputs.
	 *
	 * @return the outputs
	 */
	public ArrayList< ServoOutputScheduleChannel> getOutputs() {
		return outputs;
	}
	
	/**
	 * The Class DyIOFlusher.
	 */
	private class DyIOFlusher extends Thread{
		
		/** The running. */
		private boolean running = true;
		
		/** The flush. */
		private boolean flush = false;
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run(){
			setName("DyIO scheduler flush thread");
			while(isRunning()){
				if(isFlush()){
					flush = false;
					long start = System.currentTimeMillis();
					if(getDyIO()!=null){
						//Log.enableInfoPrint();
						double seconds =((double)(getLoopTime()))/1000;
						for(ServoOutputScheduleChannel s :getOutputs()){
							s.sync((int) seconds);
						}
						getDyIO().flushCache(seconds);
						//Log.enableDebugPrint();
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
		
		/**
		 * Checks if is running.
		 *
		 * @return true, if is running
		 */
		public boolean isRunning() {
			return running;
		}
		
		/**
		 * Sets the flush.
		 */
		public void setFlush() {
			this.flush = true;
		}
		
		/**
		 * Checks if is flush.
		 *
		 * @return true, if is flush
		 */
		public boolean isFlush() {
			return flush;
		}
	}
	


	/**
	 * The Class SchedulerThread.
	 */
	private class SchedulerThread extends Thread{
		
		/** The time. */
		private double time;
		
		/** The run. */
		private boolean run = true;

		/** The start. */
		long start = System.currentTimeMillis();
		
		/** The pause. */
		private boolean pause = false;
		
		/**
		 * Instantiates a new scheduler thread.
		 *
		 * @param ms the ms
		 * @param so the so
		 */
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

		/**
		 * Play step.
		 */
		public void playStep(){
			//System.out.println("Stepping scheduler");
			boolean playing;
			long current;
			if(mp3==null){
				
				playing = (((double)(System.currentTimeMillis()-start))<(time-StartOffset));
				current =((System.currentTimeMillis()-start))+StartOffset;
			}else{
				
				playing = mp3.isPlaying();
				current = mp3.getCurrentTime();
			}
			if(!playing){
				kill();
				return;
			}
				
			setCurrentTime(current);
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run(){
			//System.out.println("Starting timer");
			do{
				do{
					while(pause){
						ThreadUtil.wait(10);
					}
					
					long start = System.currentTimeMillis();
					playStep();
					ThreadUtil.wait(getLoopTime());
					//System.out.println("Flush took "+(System.currentTimeMillis()-start));
				}while(isRun());
				setCurrentTime(0);
				setPause(true);
				callReset();
				callPause();
			}while(true);
		}
		
		/**
		 * Pause.
		 */
		public void pause(){
			if(mp3!=null) {
				mp3.pause();
			}
			setPause(true);
		}
		
		/**
		 * Kill.
		 */
		public void kill(){
			if(mp3!=null) {
				mp3.pause();
			}
			setPause(false);
		}
		
		/**
		 * Checks if is run.
		 *
		 * @return true, if is run
		 */
		public boolean isRun() {
			if(mp3!=null){
				return run && mp3.isPlaying();
			}
			return run;
		}
//		public void setRun(boolean run) {
//			this.run = run;
/**
 * Checks if is pause.
 *
 * @return true, if is pause
 */
//		}
		public boolean isPause() {
			return pause;
		}
		
		/**
		 * Sets the pause.
		 *
		 * @param pause the new pause
		 */
		public void setPause(boolean pause) {
			this.pause = pause;
		}
	}

	/**
	 * Gets the audio file.
	 *
	 * @return the audio file
	 */
	public File getAudioFile() {
		return audioFile;
	}

	/**
	 * Sets the dy io.
	 *
	 * @param dyio the new dy io
	 */
	public void setDyIO(DyIO dyio) {
		this.dyio = dyio;
		flusher = new DyIOFlusher();
		flusher.start();
	}

	/**
	 * Gets the dy io.
	 *
	 * @return the dy io
	 */
	public DyIO getDyIO() {
		return dyio;
	}

	/**
	 * Sets the loop time.
	 *
	 * @param loopTime the new loop time
	 */
	public void setLoopTime(int loopTime) {
		this.loopTime = loopTime;
	}

	/**
	 * Gets the loop time.
	 *
	 * @return the loop time
	 */
	public int getLoopTime() {
		return loopTime;
	}

	/**
	 * Gets the st.
	 *
	 * @return the st
	 */
	public SchedulerThread getSt() {
		return st;
	}

	/**
	 * Sets the st.
	 *
	 * @param st the new st
	 */
	public void setSt(SchedulerThread st) {
		this.st = st;
		st.setPause(true);
		st.start();
	}




}
