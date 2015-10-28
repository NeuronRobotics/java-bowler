package com.neuronrobotics.addons.driving.virtual;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;

import com.neuronrobotics.addons.driving.AbstractRobotDrive;
import com.neuronrobotics.addons.driving.NrMap;
import com.neuronrobotics.sdk.ui.ConnectionImageIconFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class VirtualWorld.
 */
public class VirtualWorld extends NrMap{
	
	/** long. */
	
	private static final long serialVersionUID = 3437012102714959690L;
	
	/** The bots. */
	private ArrayList<DrivingRobotUI> bots = new ArrayList<DrivingRobotUI>();
	
	/** The frame. */
	private JFrame frame;

	/**
	 * Instantiates a new virtual world.
	 */
	public VirtualWorld() {
		System.out.println("Starting new Virtual World");
		BufferedImage d = new BufferedImage(getWidth(), getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics2D g =  d.createGraphics();
		//Backdrop is blue
		g.setColor(Color.blue);
		g.fillRect(0,0, getWidth(),getHeight());
		
		//White Oval tack
		g.setColor(Color.white);
		int rad =20;
		g.fillRoundRect(30,50, 720,300, rad ,rad );
		g.fillRoundRect(450,50,150,500, rad ,rad );
		g.fillRoundRect(450,450,300,120, rad ,rad );
		g.setColor(Color.orange);
		g.fillOval(720, 460, 20, 20);
		//g.fillOval(30,10, (int)(width-50 ),(int)( hight-80 ));
		
		//Draw Line Follow Track
		g.setColor(Color.black);
		g.setStroke(new BasicStroke(10));
		//g.drawOval(80,50, (int)(width-160 ),(int)( hight-160 ));
		
		//Straight Sections
		int lSx = (int)(width/2-120 );
		int ly=(int)( height/2+60 );
		int lEx=(int)(width/2+120 );
		
		int sSx=(int)(width/2-50 );
		int sy=(int)( height/2-60 );
		int sEx= (int)(width/2+50 );
		
		g.drawLine(lSx, ly, lEx, ly);
		g.drawLine(sSx, sy, sEx, sy);
		
		int h = (int)(width/2 );
		g.drawLine(h, ly-20, h, ly+20);
		
		//End Archs
		int ar = 200;
		g.drawArc(lSx-(ar/2), ly-ar, ar, ar, 90, 180);
		g.drawArc(lEx-(ar/2), ly-ar, ar, ar, -90, 180);
		
		//Connect the ends
		g.drawLine(sEx, sy, lEx, ly-ar);
		g.drawLine(sSx, sy, lSx, ly-ar);
		setDisplay(d);
		initGui();
	}
	
	/**
	 * Instantiates a new virtual world.
	 *
	 * @param b the b
	 */
	public VirtualWorld(BufferedImage b) {
		super(b);
		setDisplay(b);
		initGui();
	}

	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.NrMap#initGui()
	 */
	public void initGui(){
		super.initGui();
		
		
        getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.black);
        getFrame().add(this);
        getFrame().setSize((int)width+200,(int)height+200);
        getFrame().setLocationRelativeTo(null);
        getFrame().setVisible(true);
        getFrame().setIconImage( ConnectionImageIconFactory.getIcon("images/hat.png").getImage());
        //frame.addMouseListener(this);
        //frame.addMouseMotionListener(this);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.addons.driving.NrMap#updateMap()
	 */
	public void updateMap() {
		//System.out.println("Updating Map");
		BufferedImage display = getMap();
		
		Graphics2D g =  display.createGraphics();
		if(bots != null){
			for(DrivingRobotUI b:bots) {
				b.drawRobot(g);
			}
		}
		setFinalDisplayImage(display);
		getFrame().setVisible(true);
		getFrame().repaint();
	}
	
	/**
	 * Adds the robot.
	 *
	 * @param robot the robot
	 * @param botStartX the bot start x
	 * @param botStartY the bot start y
	 */
	public void addRobot(AbstractRobotDrive robot,int botStartX ,int botStartY) {
		if(!bots.contains(robot))
			bots.add(new DrivingRobotUI(this,robot,botStartX ,botStartY));
		updateMap();
	}
	
	/**
	 * Adds the sensor display dot.
	 *
	 * @param platform the platform
	 * @param deltLateral the delt lateral
	 * @param deltForward the delt forward
	 * @param c the c
	 */
	public synchronized void addSensorDisplayDot(AbstractRobotDrive platform, double deltLateral, double deltForward, Color c){
		for( int i=0;i<bots.size();i++){
			DrivingRobotUI b = bots.get(i);
			if(b.getRobot()==platform){
				b.addSensorDisplayDot(deltLateral,deltForward,c );
			}
		}
	}

	/**
	 * Gets the obsticle.
	 *
	 * @param platform the platform
	 * @param deltLateral the delt lateral
	 * @param deltForward the delt forward
	 * @return the obsticle
	 */
	public ObsticleType getObsticle(AbstractRobotDrive platform, double deltLateral, double deltForward) {
		for( int i=0;i<bots.size();i++){
			DrivingRobotUI b = bots.get(i);
			if(b.getRobot()==platform){
				int [] loc = b.getSensorPixelLocation(deltLateral, deltForward);
				return getObsticle(loc[0],loc[1]);
			}
		}
		return null;
	}
	
	/**
	 * Gets the range data.
	 *
	 * @param robot the robot
	 * @param direction in radians
	 * @param pixelMaxRange in pixels
	 * @param type the type
	 * @return distance in mm
	 */
	public double getRangeData(AbstractRobotDrive robot, double direction,int pixelMaxRange, ObsticleType type) {
		for( int j=0;j<bots.size();j++){
			DrivingRobotUI b = bots.get(j);
			if(b.getRobot()==robot){
				double x = b.getRobotXToPixel();
				double y = b.getRobotYToPixel();
				double i=10;
				double increment = 2;
				double o =robot.getCurrentOrentation()+direction;
				//System.out.println("Getting range with resolution of "+getPixelToCm((int) (increment))*100+" mm");
				//System.out.println("getting range at sensor angle="+Math.toDegrees(direction)+" absolute="+Math.toDegrees(o));
				while(x>0&&x<getFrame().getWidth()&&y>0&&y<getFrame().getHeight() && i<pixelMaxRange){
					i+=increment;
					x += (increment*Math.sin(o));
					y += (increment*Math.cos(o));
//					System.out.println("Getting value at x="+x+" y="+y);
//					if(i%10==0){
//						b.setRangeVector((int)x,(int)y);
//						updateMap();
//					}
					if(getObsticle((int)x,(int)y)==type){
						b.setRangeVector((int)x,(int)y);
						updateMap();
						
						return getPixelToCm((int) (i))*100;
					}
				}
				
			}
		}
		return pixelMaxRange;
	}

	/**
	 * Gets the frame.
	 *
	 * @return the frame
	 */
	public JFrame getFrame() {
		if(frame == null)
			frame = new JFrame("Virtual World");
		return frame;
	}


}
