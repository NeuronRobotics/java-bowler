package com.neuronrobotics.sdk.dyio.sequencer;
/*************************************************************************
 *  Compilation:  javac -classpath .:jl1.0.jar MP3.java         (OS X)
 *                javac -classpath .;jl1.0.jar MP3.java         (Windows)
 *  Execution:    java -classpath .:jl1.0.jar MP3 filename.mp3  (OS X / Linux)
 *                java -classpath .;jl1.0.jar MP3 filename.mp3  (Windows)
 *  
 *  Plays an MP3 file using the JLayer MP3 library.
 *
 *  Reference:  http://www.javazoom.net/javalayer/sources.html
 *
 *
 *  To execute, get the file jl1.0.jar from the website above or from
 *
 *      http://www.cs.princeton.edu/introcs/24inout/jl1.0.jar
 *
 *  and put it in your working directory with this file MP3.java.
 *
 *************************************************************************/

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import com.neuronrobotics.sdk.util.ThreadUtil;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class SequencerMP3 {
    
    private String fn="";
    // constructor that takes the name of an MP3 file
    private MediaPlayer player;
    private int trackLength = 37;
    public SequencerMP3(String filename) {
    	fn = filename;
        try {
        	new JFXPanel(); // initializes JavaFX environment 
        	player = new MediaPlayer(
    													new Media(
    															new File(fn).toURI().toString()));
        	while(player.getStatus() != MediaPlayer.Status.READY){ThreadUtil.wait(200);}
        	trackLength =(int) player.getCycleDuration().toMillis();
        	player.setOnStopped(new Runnable() {
				@Override
				public void run() {
//					pause=true;
				}
			});
        }
        catch (Exception e) {
            System.out.println("Problem playing file " + filename+"\r\n");
            //e.printStackTrace();
            throw e;
        }
    }
    
    public void pause(){
    	player.pause();
    }

    public void close() { 
    	if (player != null) 
    		player.stop(); 
    }

    public boolean isPlaying() {
		if(player!=null)
			return (player.getCurrentTime().toMillis()<getTrackLength());
		return false;
	}
	public int getCurrentTime() {
		return (int) player.getCurrentTime().toMillis();
	}
	public void setCurrentTime(int time) {
		player.seek(new Duration(time));
	}
	/**
	 * 
	 * @return length in Ms
	 */
	public int getTrackLength(){
		return trackLength;
	}
	
	private double getPercent() {
		if(player.getCurrentTime().toMillis()<=0){
			return 0;
		}
		if(player!=null) {
			return (player.getCurrentTime().toMillis()*100/getTrackLength());
		}
		return 0;
	}

	
	public void playStep(){
//		player.setStartTime(player.getCurrentTime());
//		player.setStopTime(new Duration(player.getCurrentTime().toMillis()+getMsStepDuration()));
		player.play();
		
	}

    // play the MP3 file to the sound card
    public void play() {

        player.play();
    	

    }


    // test client
    public static void main(String[] args) {
    	SequencerMP3 mp3 = new SequencerMP3("track.mp3");
    	
		mp3.play();
		System.out.println("Track length= "+mp3.getTrackLength());
		while(mp3.isPlaying() ){
			System.out.println("Current "+mp3.getCurrentTime() +" Percent = "+mp3.getPercent());
			ThreadUtil.wait(100);
		}
		System.out.println("Finished "+mp3.getCurrentTime()+" of "+mp3.getTrackLength());
		System.exit(0);
		//mediaPlayer.

    }



	

}
