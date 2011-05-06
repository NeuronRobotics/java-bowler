package com.neuronrobotics.nrconsole;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.event.InternalFrameListener;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.nrconsole.plugin.DyIO.ControlPanel;
import com.sun.org.apache.bcel.internal.generic.NEW;


public class NRConsoleWindow extends JFrame {
	private ArrayList<JPanel> panels=new ArrayList<JPanel>();
	public static final String name = "Neuron Robotics Console 3.7.5";
	public static int panelHight = 700;
	public static int panelWidth = 1095;
	private static final long serialVersionUID = 1L;
	private JPanel scroller = new  JPanel();
	private JScrollPane scrollPanel = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
	
	private JTabbedPane modePane = new JTabbedPane();
	private JPanel active=null;
	private static NRConsoleWindow instance = null;
	public static Dimension getNRWindowSize(){
		if(instance!= null)
			return instance.getWindowSize();
		return  new Dimension(400,400);
	}
	public Dimension getWindowSize(){
		Dimension d = new Dimension(getWidth(),getHeight());
		//System.out.println("Window size "+d);
		return d;
	}
	private JPanel logoPanel = new JPanel() {
		private static final long serialVersionUID = 1L;
		private ImageIcon logo = new ImageIcon(NRConsole.class.getResource("images/logo.png"));
		
		public void paintComponent (Graphics g) {
			Graphics2D g2 = (Graphics2D)instance.getGraphics();
			super.paintComponent(g);
			setSize(getWindowSize());
	    	try {
	    		Dimension d = getWindowSize();
	    		int y = (d.height - (3*logo.getIconHeight() / 2));
	    		
	    		g2.drawImage(logo.getImage(), 50, y, logo.getIconWidth(), logo.getIconHeight(), this);
	    	} catch (Exception e) {
	    		
	    	}
	    }
	};
	
	public NRConsoleWindow() {
		super(name);
		instance=this;
		displayLogo();
		scrollPanel.setViewportView(scroller);
		add(scrollPanel);
		getContentPane().addHierarchyBoundsListener(new HierarchyBoundsListener(){
			
			public void ancestorMoved(HierarchyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void ancestorResized(HierarchyEvent arg0) {
				//System.out.println("Resized: "+getWindowSize());
				scroller.setSize(getWindowSize());
				scroller.setPreferredSize(new Dimension(panelWidth-53,panelHight-105));
				scroller.setVisible(true);
				modePane.setSize(getWindowSize());
			}			
		});
	}
	
	
	public void repaint(){		
		logoPanel.repaint();
		
		for(JPanel p: panels){
			p.repaint();
		}
	
		super.repaint();
	}
	private void updateUI(){
		setSize(new Dimension(panelWidth+53,panelHight+105));
		//scroller.setSize(getWindowSize());
		scroller.setPreferredSize(new Dimension(panelWidth,panelHight));
		setLocationRelativeTo(null);
		scroller.invalidate();
		scroller.repaint();
		scroller.setVisible(true);
		invalidate();
		repaint();
		setVisible(true);
	}
	
	public void setDeviceManager(PluginManager deviceManager) {
		panelHight = 700;
		panelWidth = 1095;
		scroller.removeAll();
		modePane.removeAll();
		panels=new ArrayList<JPanel>();
		for(JPanel p: deviceManager.getPanels()){
			modePane.addTab(p.getName(), p);
			panels.add(p);
		}

		scroller.add(modePane);
		updateUI();
	}

	public void displayLogo() {
		panelHight = 700;
		panelWidth = 1095;
		scroller.removeAll();
		scroller.add(logoPanel);
		updateUI();
	}

	private class WindowScroller extends JPanel implements Scrollable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -8759782155710955452L;
		private int maxUnitIncrement = 50;
		
		public WindowScroller() {
			setOpaque(false);
			setLayout(new MigLayout());
		}
		
		
		public Dimension getPreferredScrollableViewportSize() {
			System.out.println("Getting preffered size");
			return new Dimension(panelWidth, panelHight);
		}

		
		public int getScrollableBlockIncrement(Rectangle visibleRect, int arg1,int arg2) {
			// TODO Auto-generated method stub
			return maxUnitIncrement;
		}

		
		public boolean getScrollableTracksViewportHeight() {
			// TODO Auto-generated method stub
			return false;
		}

		
		public boolean getScrollableTracksViewportWidth() {
			// TODO Auto-generated method stub
			return false;
		}

		
		public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {

	       return maxUnitIncrement;
	    }		
	}
}
