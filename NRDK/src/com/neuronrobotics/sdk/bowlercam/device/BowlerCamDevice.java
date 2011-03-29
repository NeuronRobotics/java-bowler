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

public class BowlerCamDevice extends BowlerAbstractDevice {
	private ByteList tmp = new ByteList();
	//private String srvUrl = null;
	private ArrayList<IWebcamImageListener> imageListeners 	= new ArrayList<IWebcamImageListener>();
	private ArrayList<highSpeedAutoCapture> captures= new ArrayList<highSpeedAutoCapture> ();
	private ArrayList<BufferedImage>		images	= new ArrayList<BufferedImage>();
	private ArrayList<String> 				urls	= new ArrayList<String> ();
	private ArrayList<ItemMarker> mark = new  ArrayList<ItemMarker> ();
	private boolean gotLastMark = false;
	//private highSpeedAutoCapture hsac = null;
	public void addWebcamImageListener(IWebcamImageListener l){
		if(!imageListeners.contains(l))
			imageListeners.add(l);
	}
	private void fireIWebcamImageListenerEvent(int camera,BufferedImage im){
		for(IWebcamImageListener l:imageListeners){
			l.onNewImage(camera,im);
		}
	}
	@Override
	public void onAllResponse(BowlerDatagram data) {
		// TODO Auto-generated method stub

	}
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
	private class ImageReader extends Thread{
		int cam;
		private boolean done=false;
		public ImageReader(int cam){
			this.cam=cam;
		}
		public void run(){
				try {
					images.set(cam,ImageIO.read(new URL(urls.get(cam))));
				} catch (Exception e) {
					e.printStackTrace();
				}
				done=(true);
		}
		public boolean isDone() {
			return done;
		}
	}
	@Override
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
	public boolean updateImage(int chan, double scale){
		return send(new ImageCommand(chan, scale))==null;
	}
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
	public BufferedImage getImage(int chan) {
		return images.get(chan);
	}
	public void startHighSpeedAutoCapture(int cam,double scale,int fps) {
		stopAutoCapture(cam);
		while((captures.size() <= cam)&& isAvailable())
			captures.add(null);
		captures.set(cam,new highSpeedAutoCapture(cam,scale,fps));
		captures.get(cam).start();
	}
	public void stopAutoCapture(int cam) {
		try{
			captures.get(cam).kill();
			captures.set(cam,null);
		}catch (Exception e){}
	}
	public boolean updateFilter(Color c, int threshhold,boolean within,int minBlobSize,int maxBlobSize){
		boolean back = false;
		try{
			back = send(new BlobCommand(c, threshhold, within, minBlobSize, maxBlobSize))==null;
		}catch (Exception e){
			e.printStackTrace();
		}
		return back;
	}
	
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
	
	private class highSpeedAutoCapture extends Thread{
		int cam;
		double scale;
		int mspf;
		boolean running = true;
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
		public void kill() {
			//System.out.println("Killing auto capture on cam: "+cam);
			running = false;
		}
	}
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
	public BufferedImage ByteArrayToImage(byte [] array) throws IOException{
		BufferedImage image = null;
		image = ImageIO.read(new ByteArrayInputStream(array));
		return image;
	}
}
