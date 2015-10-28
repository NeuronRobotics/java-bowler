package com.neuronrobotics.addons.driving;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JFrame;

import com.neuronrobotics.addons.driving.virtual.ObsticleType;
import com.neuronrobotics.sdk.ui.ConnectionImageIconFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class SimpleDisplay.
 */
public class SimpleDisplay extends NrMap {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7042174918507023465L;
	
	/** The frame. */
	private JFrame frame = new JFrame();
	
	/**
	 * Instantiates a new simple display.
	 */
	public SimpleDisplay(){
        getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.black);
        getFrame().add(this);
        getFrame().setSize((int)width+200,(int)height+200);
        getFrame().setLocationRelativeTo(null);
        getFrame().setVisible(true);
        getFrame().setIconImage( ConnectionImageIconFactory.getIcon("images/hat.png").getImage());
        
	}
	
	/**
	 * Gets the frame.
	 *
	 * @return the frame
	 */
	public JFrame getFrame() {
		return frame;
	}
	
	/**
	 * Sets the frame.
	 *
	 * @param frame the new frame
	 */
	public void setFrame(JFrame frame) {
		this.frame = frame;
	}
	
	/**
	 * Sets the data.
	 *
	 * @param data the new data
	 */
	public void setData(ArrayList<DataPoint> data) {
		 //removeAllUserDefinedObsticles();
		 for(DataPoint d:data){
			 double pix =  getCmToPixel(d.getRange()/100);
			 double centerX=(width/2);
			 double centerY=(height/2);
			 if(!(pix>centerX || pix>centerY )){
				 double deltX = pix*Math.cos(Math.toRadians(d.getAngle()));
				 double deltY = pix*Math.sin(Math.toRadians(d.getAngle()));
				 addUserDefinedObsticle((int)(centerX+deltX), (int)(centerY+deltY), 2,ObsticleType.USERDEFINED);
			 }else{
				 //System.out.println("Range too long: "+pix+" cm="+d.getRange()/100);
			 }
		 }
		 updateMap();
	}
}
