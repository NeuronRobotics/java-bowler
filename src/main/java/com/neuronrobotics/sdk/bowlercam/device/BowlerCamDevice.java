package com.neuronrobotics.sdk.bowlercam.device;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.neuronrobotics.sdk.commands.neuronrobotics.bowlercam.BlobCommand;
import com.neuronrobotics.sdk.commands.neuronrobotics.bowlercam.ImageCommand;
import com.neuronrobotics.sdk.commands.neuronrobotics.bowlercam.ImageURLCommand;
import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.ByteList;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.util.ThreadUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class BowlerCamDevice.
 */
public class BowlerCamDevice extends BowlerAbstractDevice {
	
	/** The tmp. */
	private ByteList tmp = new ByteList();
	
	/** The image listeners. */
	//private String srvUrl = null;
	private ArrayList<IWebcamImageListener> imageListeners 	= new ArrayList<IWebcamImageListener>();
	
	/** The captures. */
	private ArrayList<highSpeedAutoCapture> captures= new ArrayList<highSpeedAutoCapture> ();
	
	/** The images. */
	private ArrayList<BufferedImage>		images	= new ArrayList<BufferedImage>();
	
	/** The urls. */
	private ArrayList<String> 				urls	= new ArrayList<String> ();
	
	/** The mark. */
	private ArrayList<ItemMarker> mark = new  ArrayList<ItemMarker> ();
	
	/** The got last mark. */
	private boolean gotLastMark = false;
	
	/**
	 * Adds the webcam image listener.
	 *
	 * @param l the l
	 */
	//private highSpeedAutoCapture hsac = null;
	public void addWebcamImageListener(IWebcamImageListener l){
		if(!imageListeners.contains(l))
			imageListeners.add(l);
	}
	
	/**
	 * Fire i webcam image listener event.
	 *
	 * @param camera the camera
	 * @param im the im
	 */
	private void fireIWebcamImageListenerEvent(int camera,BufferedImage im){
		for(IWebcamImageListener l:imageListeners){
			l.onNewImage(camera,im);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.BowlerAbstractDevice#onAllResponse(com.neuronrobotics.sdk.common.BowlerDatagram)
	 */
	public void onAllResponse(BowlerDatagram data) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Gets the high speed image.
	 *
	 * @param cam the cam
	 * @return the high speed image
	 * @throws MalformedURLException the malformed url exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public BufferedImage getHighSpeedImage(int cam) throws MalformedURLException, IOException {
		//System.out.println("Getting HighSpeedImage");
		while(urls.size()<(cam+1) && isAvailable()){
			Log.info("Adding dummy url: "+urls.size());
			urls.add(null);
		}
		while(images.size() <(cam+1) && isAvailable()){
			Log.info("Adding dummy image: "+images);
			images.add(null);
		}
		if(urls.get(cam) == null){
			//System.out.println("URL List element is empty: "+urls);
			urls.set(cam,getImageServerURL(cam));
		}
		try {
			//System.out.println("Reading: "+urls.get(cam) );
			ImageReader ir = new ImageReader(cam);
			ir.start();
			long start = System.currentTimeMillis();
			while(((System.currentTimeMillis()-start)<200) && ir.isDone()==false){
				ThreadUtil.wait(5);
			}
			if(!ir.isDone())
				Log.error("Image read timed out");
		}catch(Exception ex) {
			//Log.error("Image capture failed");	
		}
		return images.get(cam);
	}
	
	/**
	 * The Class ImageReader.
	 */
	private class ImageReader extends Thread{
		
		/** The cam. */
		int cam;
		
		/** The done. */
		private boolean done=false;
		
		/**
		 * Instantiates a new image reader.
		 *
		 * @param cam the cam
		 */
		public ImageReader(int cam){
			this.cam=cam;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run(){
				try {
					images.set(cam,ImageIO.read(new URL(urls.get(cam))));
				} catch (Exception e) {
					Log.error("Image Read threw an exception: "+e.getMessage());
				}
				done=(true);
		}
		
		/**
		 * Checks if is done.
		 *
		 * @return true, if is done
		 */
		public boolean isDone() {
			return done;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.common.IBowlerDatagramListener#onAsyncResponse(com.neuronrobotics.sdk.common.BowlerDatagram)
	 */
	public void onAsyncResponse(BowlerDatagram data) {
		if(data.getRPC().contains("_img")){
			ByteList d = data.getData();
			int camera = ByteList.convertToInt(d.popList(2));
			int index = ByteList.convertToInt(d.popList(2));
			int total = ByteList.convertToInt(d.popList(2));
			byte [] imgData = d.popList(d.size());
			Log.info("Got image chunk\n"+data+"\nindex: "+index+", total: "+total+", len: "+imgData.length);
			synchronized(tmp) {
				tmp.add(imgData);
			}
			if(index == (total)){
				////System.out.println("Making image");
		        BufferedImage image=null;
				try {
					synchronized(tmp) {
						image = ByteArrayToImage(tmp.getBytes());
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					image=null;
				}
				synchronized(tmp) {
					tmp.clear();
				}
				images.set(camera, image);
				fireIWebcamImageListenerEvent(camera,images.get(camera));
				//System.out.println("Image OK");
			}
			
		}
		if(data.getRPC().contains("blob")){
			int x = ByteList.convertToInt(data.getData().getBytes(0, 4));
			int y = ByteList.convertToInt(data.getData().getBytes(4, 4));
			int r = ByteList.convertToInt(data.getData().getBytes(8, 4));
			if(x==0 && y == 0 && r == 0){
				gotLastMark = true;
				return;	
			}
			mark.add(new ItemMarker(x,y,r));
		}

	}
	
	/**
	 * Update image.
	 *
	 * @param chan the chan
	 * @param scale the scale
	 * @return true, if successful
	 */
	public boolean updateImage(int chan, double scale){
		return send(new ImageCommand(chan, scale))==null;
	}
	
	/**
	 * Gets the image server url.
	 *
	 * @param chan the chan
	 * @return the image server url
	 */
	public String getImageServerURL(int chan){
		//Log.info("Requesting image server URL");
		while(urls.size() < (chan+1) && isAvailable()){
			urls.add(null);
		}
		if(urls.get(chan) != null)
			return urls.get(chan);
		BowlerDatagram b=send(new ImageURLCommand(chan));
		urls.set(chan,b.getData().asString());
		return urls.get(chan);
	}
	
	/**
	 * Gets the image.
	 *
	 * @param chan the chan
	 * @return the image
	 */
	public BufferedImage getImage(int chan) {
		return images.get(chan);
	}
	
	/**
	 * Start high speed auto capture.
	 *
	 * @param cam the cam
	 * @param scale the scale
	 * @param fps the fps
	 */
	public void startHighSpeedAutoCapture(int cam,double scale,int fps) {
		stopAutoCapture(cam);
		while((captures.size() <= cam)&& isAvailable())
			captures.add(null);
		captures.set(cam,new highSpeedAutoCapture(cam,scale,fps));
		captures.get(cam).start();
	}
	
	/**
	 * Stop auto capture.
	 *
	 * @param cam the cam
	 */
	public void stopAutoCapture(int cam) {
		try{
			captures.get(cam).kill();
			captures.set(cam,null);
		}catch (Exception e){}
	}
	
	/**
	 * Update filter.
	 *
	 * @param c the c
	 * @param threshhold the threshhold
	 * @param within the within
	 * @param minBlobSize the min blob size
	 * @param maxBlobSize the max blob size
	 * @return true, if successful
	 */
	public boolean updateFilter(Color c, int threshhold,boolean within,int minBlobSize,int maxBlobSize){
		boolean back = false;
		try{
			back = send(new BlobCommand(c, threshhold, within, minBlobSize, maxBlobSize))==null;
		}catch (Exception e){
			e.printStackTrace();
		}
		return back;
	}
	
	/**
	 * Gets the blobs.
	 *
	 * @return the blobs
	 */
	public ArrayList<ItemMarker> getBlobs(){
		mark.clear();
		send(new BlobCommand());
		while(gotLastMark == false && isAvailable()){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return mark;
	}
	
	/**
	 * The Class highSpeedAutoCapture.
	 */
	private class highSpeedAutoCapture extends Thread{
		
		/** The cam. */
		int cam;
		
		/** The scale. */
		double scale;
		
		/** The mspf. */
		int mspf;
		
		/** The running. */
		boolean running = true;
		
		/**
		 * Instantiates a new high speed auto capture.
		 *
		 * @param cam the cam
		 * @param scale the scale
		 * @param fps the fps
		 */
		public highSpeedAutoCapture(int cam,double scale,int fps){
			this.cam=cam;
			this.scale=scale;
			if(fps == 0) {
				mspf = 0;
				return;
			}
			mspf = (int)(1000.0/((double)fps));
			//System.out.println("MS/frame: "+mspf);
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			//System.out.println("Starting auto capture on: "+getImageServerURL(cam));
			long st = System.currentTimeMillis();
			while(running && isAvailable()) {
				//System.out.println("Getting image from: "+getImageServerURL(cam));
				try {
					//System.out.println("Capturing");
					BufferedImage im =getHighSpeedImage(cam);
					if(scale>1.01||scale<.99)
						im = resize(im, scale);
					if(im!=null){
						//System.out.println("Fireing");
						fireIWebcamImageListenerEvent(cam,im);
					}
					//System.out.println("ok");
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(mspf != 0) {
					long diff = System.currentTimeMillis() - st;
					////System.out.print("\nMS diff: "+diff);
					if(diff<mspf) {
						try {
							////System.out.print(" sleeping: "+(mspf-diff));
							Thread.sleep(mspf-diff);
						} catch (InterruptedException e) {
						}
					}
					st =  System.currentTimeMillis() ;
				}
			}
		}
		
		/**
		 * Kill.
		 */
		public void kill() {
			//System.out.println("Killing auto capture on cam: "+cam);
			running = false;
		}
	}
	
	/**
	 * Resize.
	 *
	 * @param image the image
	 * @param scale the scale
	 * @return the buffered image
	 */
	public BufferedImage resize(BufferedImage image, double scale) {
		if(image == null)
			return null;
		if (scale<.01)
			scale = .01;
		int width =  (int)(((double)image.getWidth())*scale);
		int height = (int)(((double)image.getHeight())*scale);
		BufferedImage resizedImage = new BufferedImage(width, height,image.getType());
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		return resizedImage;
	}
	
	/**
	 * Byte array to image.
	 *
	 * @param array the array
	 * @return the buffered image
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public BufferedImage ByteArrayToImage(byte [] array) throws IOException{
		BufferedImage image = null;
		image = ImageIO.read(new ByteArrayInputStream(array));
		return image;
	}
}
