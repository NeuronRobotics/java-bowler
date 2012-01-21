/*
 * 11/19/04		1.0 moved to LGPL.
 * 29/01/00		Initial version. mdm@techie.com
 *-----------------------------------------------------------------------
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *----------------------------------------------------------------------
 */

package javazoom.jl.player;

import java.io.InputStream;
import java.util.ArrayList;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;
	
/**
 * The <code>Player</code> class implements a simple player for playback
 * of an MPEG audio stream. 
 * 
 * @author	Mat McGowan
 * @since	0.0.8
 */

// REVIEW: the audio device should not be opened until the
// first MPEG audio frame has been decoded. 
public class MyPlayer
{	  	
	/**
	 * The current frame number. 
	 */
	private int frame = 0;
	
	/**
	 * The MPEG audio bitstream. 
	 */
	// javac blank final bug. 
	/*final*/ private Bitstream		bitstream;
	
	/**
	 * The MPEG audio decoder. 
	 */
	/*final*/ private Decoder		decoder; 
	
	/**
	 * The AudioDevice the audio samples are written to. 
	 */
	private AudioDevice	audio;
	
	/**
	 * Has the player been closed?
	 */
	private boolean		closed = false;
	
	private boolean pause = false;
	
	/**
	 * Has the player played back all frames from the stream?
	 */
	private boolean		complete = false;

	private int			numFrames = 0;
	private long 		msPerFrame = 25; 
	
	private ArrayList<short[]> outputData = new ArrayList<short[]>();
	/**
	 * Creates a new <code>Player</code> instance. 
	 */
	public MyPlayer(InputStream stream) throws JavaLayerException
	{
		this(stream, null);	
	}
	
	public MyPlayer(InputStream stream, AudioDevice device) throws JavaLayerException
	{
		bitstream = new Bitstream(stream);		
		decoder = new Decoder();
				
		if (device!=null)
		{		
			audio = device;
		}
		else
		{			
			FactoryRegistry r = FactoryRegistry.systemRegistry();
			audio = r.createAudioDevice();
		}
		audio.open(decoder);
		getNumberOfFrames();
		complete = false;
	}
	public double getNumberOfFrames() {
		if(numFrames==0) {
			boolean ret = true;
			while (ret)
			{
				try
				{
					AudioDevice out = audio;
					if (out==null)
						return numFrames;

					Header h = bitstream.readFrame();	
					
					if (h==null)
						return numFrames;	
					// sample buffer set when decoder constructed
					SampleBuffer s = (SampleBuffer)decoder.decodeFrame(h, bitstream);
					outputData.add(s.getBuffer());														
					bitstream.closeFrame();
				}catch (RuntimeException ex){
					ex.printStackTrace();
				} catch (JavaLayerException e) {
					e.printStackTrace();
				}
				numFrames++;
			}
		}
		return numFrames;
	}
	public void play() throws Exception
	{
		play(Integer.MAX_VALUE);
	}
	public void setPause(boolean p){
		pause = p;
	}
	
	public void playStep(){
		if(frame<outputData.size()){
			complete = false;
			try {
				synchronized (this){
					audio.write(outputData.get(frame), 0, outputData.get(frame).length);
				}
				
			} catch (JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			frame++;
		}else{
			frame=0;
			complete = true;
		}
	}
	
	/**
	 * Plays a number of MPEG audio frames. 
	 * 
	 * @param frames	The number of frames to play. 
	 * @return	true if the last frame was played, or false if there are
	 *			more frames. 
	 * @throws InterruptedException 
	 */
	public boolean play(int frames) throws Exception
	{
		boolean ret = true;
		do{
			while(pause){
				Thread.sleep(1);
			}
			if (audio!=null)
			{					
				playStep();
			}
		}while(!complete);
		audio.flush();
//		for(frame=0;frame<outputData.size();) {
//			while(pause){
//				Thread.sleep(1);
//			}
//			out = audio;
//			if (audio!=null)
//			{					
//				playStep();
//			}				
//			
//		}

		return ret;
	}
		
	/**
	 * Cloases this player. Any audio currently playing is stopped
	 * immediately. 
	 */
	public synchronized void close()
	{		
		AudioDevice out = audio;
		if (out!=null)
		{ 
			closed = true;
			audio = null;	
			// this may fail, so ensure object state is set up before
			// calling this method. 
			out.close();
			try
			{
				bitstream.close();
			}
			catch (BitstreamException ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns the completed status of this player.
	 * 
	 * @return	true if all available MPEG audio frames have been
	 *			decoded, or false otherwise. 
	 */
	public boolean isComplete()
	{
		return complete;	
	}
				
	/**
	 * Retrieves the position in milliseconds of the current audio
	 * sample being played. This method delegates to the <code>
	 * AudioDevice</code> that is used by this player to sound
	 * the decoded audio samples. 
	 */
	public double getPercent()
	{
		return (getCurrentFrame()/getNumberOfFrames());
	}
	
	public int getCurrentTime(){
		return (int) (getCurrentFrame()*msPerFrame);
	}

	public void setCurrentFrame(int frame) {
		this.frame = frame;
	}
	
	/**
	 * sets the current playback time in Ms
	 * @param time
	 */
	public void setCurrentTime(double time) {
		if(time<0)
			time=0;
		if(time>getTrackLength())
			time=getTrackLength();
		
		frame = (int) (time/msPerFrame);
		//System.out.println("Setting current frame to="+frame+" time="+time);
	}
	public double getCurrentFrame() {
		return frame;
	}		
	/**
	 * 
	 * @return the length of the track in Ms 
	 */
	public int getTrackLength(){
		return (int) (getNumberOfFrames()*msPerFrame);
	}
	
}
