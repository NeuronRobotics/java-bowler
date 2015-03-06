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

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SequencerMP3 {
    
    private String fn="";
    private boolean pause = false;
    private boolean playing = false;
    // constructor that takes the name of an MP3 file
    public SequencerMP3(String filename) {
    	fn = filename;
        try {
            FileInputStream fis     = new FileInputStream(fn);
            BufferedInputStream bis = new BufferedInputStream(fis);
            //player = new MyPlayer(bis);
            
        }
        catch (Exception e) {
            System.out.println("Problem playing file " + filename);
            e.printStackTrace();
        }
    }
    
    public void pause(){
    	pause=true;
    	//player.setPause(true);
    }

    public void close() { 
//    	if (player != null) 
//    		player.close(); 
    }

    public boolean isPlaying() {
//		if(player!=null)
//			return !player.isComplete();
		return false;
	}
	public int getCurrentTime() {
		return 0;
//		return player.getCurrentTime();
	}
	public void setCurrentTime(int time) {
		//System.out.println("Setting current time="+time);
//		player.setCurrentTime(time);
	}
	/**
	 * 
	 * @return length in Ms
	 */
	public int getTrackLength(){
		return 0;
//		return player.getTrackLength();
	}
	public double getPercent() {
		return 0;
//		if(player!=null) {
//			return player.getPercent();
//		}
//		return 0;
	}
	private double getNumFrames() {
		return 0;
//		return player.getNumberOfFrames();
	}
	
	public void playStep(){
//		player.playStep();
	}

    // play the MP3 file to the sound card
    public void play() {
    	if(pause){
        	pause=false;
//        	player.setPause(false);
        	if(playing)
        		return;
    	}
        // run in new thread to play in background
        new Thread() {
            public void run() {
            	playing=true;
//            	player.setCurrentFrame(0);
                try { 
//                	do{
//            			while(pause){
//            				Thread.sleep(1);
//            			}
//                		playStep();
//                	}while(!player.isComplete());
                }catch (Exception e) {
                	System.out.println(e); 
                }
                playing=false;
            }
        }.start();
    }


    // test client
    public static void main(String[] args) {
    	new JFXPanel(); // initializes JavaFX environment
    	File resource = new File("track.mp3"); 
        Media hit;
		hit = new Media(resource.toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(hit);
		mediaPlayer.play();
        

    }


	

}
