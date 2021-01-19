package com.neuronrobotics.imageprovider;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


import javax.imageio.ImageIO;

import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.InvalidConnectionException;
import com.neuronrobotics.sdk.common.NonBowlerDevice;

/**
 * This is a class is used as an interface to create cameras for the Bowler system. 
 * @author hephaestus
 *
 */
public abstract class AbstractImageProvider extends NonBowlerDevice {
	private BufferedImage image = null;
	private javafx.scene.transform.Affine globalPos;
	/**
	 * This method should capture a new image and load it into the Mat datatype
	 * @param imageData
	 * @return
	 */
	protected abstract boolean captureNewImage(BufferedImage imageData);
	
	/**
	 * This method should capture a new image and return it
	 * @return
	 */
	public abstract BufferedImage captureNewImage();
	
	@Override
	public void onAsyncResponse(BowlerDatagram data) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean connect(){
		return true;
	}
	
	/**
	 * Determines if the device is available.
	 *
	 * @return true if the device is avaiable, false if it is not
	 * @throws InvalidConnectionException the invalid connection exception
	 */
	@Override
	public boolean isAvailable() throws InvalidConnectionException{
		return true;
	}
	

	
	/**
	 * copy from buffered image to buffered image
	 * @param src
	 * @param dest
	 */
	public static void deepCopy(BufferedImage src, BufferedImage dest) {
		Graphics g = dest.createGraphics();
		g.drawImage(src, 0, 0, null);
	}
	
	/**
	 * @param inputImage
	 * @param displayImage
	 * @return latest image
	 */
	public BufferedImage getLatestImage(BufferedImage inputImage, BufferedImage displayImage){
		captureNewImage(inputImage);
		if(displayImage!=null){
			AbstractImageProvider.deepCopy(inputImage,displayImage);
		}
		image = inputImage;
		
		return image;
	}
	
	/**
	 * @return latest image
	 */
	public BufferedImage getLatestImage(){
		return image;
	}
	
	/**
	 * @param w
	 * @param h
	 * @return new blnak sized image
	 */
	public static BufferedImage newBufferImage(int w, int h) {
		return new BufferedImage(w, h,  BufferedImage.TYPE_3BYTE_BGR);
	
	}



	/**
	 * @param in
	 * @param w
	 * @param h
	 * @return grayed image
	 */
	public static  BufferedImage toGrayScale(BufferedImage in, int w, int h) {
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
		Graphics g = bi.createGraphics();
		g.drawImage(in, 0, 0, w, h, null);
		return bi;
	}

	/**
	 * @param in
	 * @param scale
	 * @return toGrayScale
	 */
	public  static BufferedImage toGrayScale(BufferedImage in, double scale) {
		int w = (int) (in.getWidth() * scale);
		int h = (int) (in.getHeight() * scale);
		return toGrayScale(in, w, h);
	}
	/**
	 * @param bf
	 * @return conversion to javafx i mage
	 */
	public static javafx.scene.image.Image getJfxImage(BufferedImage bf) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
	    try {
	        ImageIO.write( bf, "png", out);
	        out.flush();
	        } catch (IOException ex) {
	           
	        }
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
	    return new javafx.scene.image.Image(in);
	}
	/**
	 * @return image as Javafx
	 */ 
	public javafx.scene.image.Image getLatestJfxImage() {
		return getJfxImage(getLatestImage());
	}

	/**
	 * @param globalPos
	 */
	public void setGlobalPositionListener(javafx.scene.transform.Affine globalPos) {
		this.setGlobalPos(globalPos);
	}

	/**
	 * @return global positioning of the image
	 */
	public javafx.scene.transform.Affine getGlobalPos() {
		return globalPos;
	}

	/**
	 * @param globalPos
	 */
	public void setGlobalPos(javafx.scene.transform.Affine globalPos) {
		this.globalPos = globalPos;
		
	}
}
