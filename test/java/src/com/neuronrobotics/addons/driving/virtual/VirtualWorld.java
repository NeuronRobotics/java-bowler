package com.neuronrobotics.addons.driving.virtual;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.neuronrobotics.addons.driving.AbstractRobot;

public class VirtualWorld extends JPanel{
	/**
	 * long 
	 */
	JLabel lab=new JLabel();
	private static final long serialVersionUID = 3437012102714959690L;
	private ArrayList<DrivingRobotUI> bots = new ArrayList<DrivingRobotUI>();
	private JFrame frame;
	private static final double width = 600;
	private static final double hight = 480;
	private double pixelToCm=5;
	private static final double botStartX = width /2;
	private static final double botStartY = (int)( hight/2+60 );
	private BufferedImage display;
	private static final int cmMaxRange=50000;
	public VirtualWorld() {
		System.out.println("Starting new Virtual World");
		initGui();
	}
	public VirtualWorld(BufferedImage b) {
		setDisplay(b);
		initGui();
	}
	public double getPixelToCm(int pix){
		return ((double)pix)/(pixelToCm);
	}
	public int getCmToPixel(double cm){
		return (int)(cm*(pixelToCm));
	}
	private void initGui(){
		frame = new JFrame("Virtual World");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.black);
        frame.add(this);
        frame.setSize((int)width+200,(int)hight+200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        //frame.addMouseListener(this);
        //frame.addMouseMotionListener(this);
        
        removeAll();
        updateMap();
        add(lab);
        
	}
	private void setDisplay(BufferedImage d){
		display=d;
		frame.setSize(display.getWidth(), display.getHeight());
	}
	BufferedImage getMap() {
		if(display==null){
			BufferedImage d = new BufferedImage(frame.getWidth(), frame.getHeight(),BufferedImage.TYPE_INT_RGB);
			Graphics2D g =  d.createGraphics();
			//Backdrop is blue
			g.setColor(Color.blue);
			g.fillRect(0,0, frame.getWidth(),frame.getHeight());
			
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
			int ly=(int)( hight/2+60 );
			int lEx=(int)(width/2+120 );
			
			int sSx=(int)(width/2-50 );
			int sy=(int)( hight/2-60 );
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
		}
		BufferedImage d = new BufferedImage(display.getWidth(), display.getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics2D g =d.createGraphics();
		g.drawImage(display,0, 0, null);
		return d;
	}
	public ObsticleType getObsticle(int x,int y) {
		return ObsticleType.get(new Color(getMap().getRGB(x, y)));
	}
	public void updateMap() {
		//System.out.println("Updating Map");
		BufferedImage display = getMap();
		
		Graphics2D g =  display.createGraphics();
		for(DrivingRobotUI b:bots) {
			b.drawRobot(g);
		}
		
		lab.setIcon(new ImageIcon(display ) );

		lab.setVisible(true);

		frame.repaint();
	}
	
	public void addRobot(AbstractRobot robot,int botStartX ,int botStartY) {
		if(!bots.contains(robot))
			bots.add(new DrivingRobotUI(this,robot,botStartX ,botStartY));
		updateMap();
	}
	
	public synchronized void addSensorDisplayDot(AbstractRobot platform, double deltLateral, double deltForward, Color c){
		for( int i=0;i<bots.size();i++){
			DrivingRobotUI b = bots.get(i);
			if(b.getRobot()==platform){
				b.addSensorDisplayDot(deltLateral,deltForward,c );
			}
		}
	}

	public ObsticleType getObsticle(AbstractRobot platform, double deltLateral, double deltForward) {
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
	 * 
	 * @param robot 
	 * @param direction in radians
	 * @param pixelMaxRange in pixels
	 * @return distance in cm
	 */
	public double getRangeData(AbstractRobot robot, double direction,int pixelMaxRange) {
		for( int j=0;j<bots.size();j++){
			DrivingRobotUI b = bots.get(j);
			if(b.getRobot()==robot){
				int x = b.getRobotXToPixel();
				int y = b.getRobotYToPixel();
				int i=0;
				double o =robot.getCurrentOrentation()+direction;
				//System.out.println("Getting range at angle: "+Math.toDegrees(o));
				while(x>0&&x<frame.getWidth()&&y>0&&y<frame.getHeight() && i<pixelMaxRange){
					i+=1;
					x += (i*Math.sin(o));
					y += (i*Math.cos(o));
					if(getObsticle(x,y)==ObsticleType.WALL){
						b.setRangeVector(x,y);
						return getPixelToCm(i);
					}
				}
				
			}
		}
		return cmMaxRange;
	}

}
