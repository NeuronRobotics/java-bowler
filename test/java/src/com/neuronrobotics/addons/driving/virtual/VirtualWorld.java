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

import com.neuronrobotics.addons.driving.AbstractDrivingRobot;

public class VirtualWorld extends JPanel implements MouseListener,MouseMotionListener{
	/**
	 * long 
	 */
	JLabel lab=new JLabel();
	private static final long serialVersionUID = 3437012102714959690L;
	private ArrayList<DrivingRobotUI> bots = new ArrayList<DrivingRobotUI>();
	private JFrame frame;
	private static final double width = 600;
	private static final double hight = 480;
	
	private static final double botStartX = width /2;
	private static final double botStartY = hight /2;
	
	public VirtualWorld() {
		System.out.println("Starting new Virtual World");
		
		initGui();
		
		
	}
	
	private void initGui(){
		frame = new JFrame("Virtual World");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.black);
        frame.add(this);
        frame.setSize((int)width,(int)hight);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.addMouseListener(this);
        frame.addMouseMotionListener(this);
        
        removeAll();
        updateMap();
        add(lab);
	}
	
	BufferedImage getMap() {
		BufferedImage display = new BufferedImage(frame.getWidth(), frame.getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics2D g =  display.createGraphics();
		//Backdrop is blue
		g.setColor(Color.blue);
		g.fillRect(0,0, frame.getWidth(),frame.getHeight());
		
		//White Oval tack
		g.setColor(Color.white);
		g.fillOval(30,10, (int)(width-50 ),(int)( hight-80 ));
		
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
		
		return display;
	}
	public ObsticleType getObsticle(int x,int y) {
		return ObsticleType.get(new Color(getMap().getRGB(x, y)));
	}
	private void updateMap() {
		BufferedImage display = getMap();
		
		
		
		lab.setIcon(new ImageIcon(display ) );
	}
	
	public void addRobot(AbstractDrivingRobot robot) {
		if(!bots.contains(robot))
			bots.add(new DrivingRobotUI(robot,botStartX ,botStartY));
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
