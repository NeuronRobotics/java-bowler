package com.neuronrobotics.nrconsole.plugin.DyIO.Secheduler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.neuronrobotics.nrconsole.plugin.DyIO.DyIORegestry;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class SchedulerGui extends JPanel{
	private SchedulerThread st=null;// = new SchedulerThread();
	private JSlider slider = new JSlider();
	private JButton play = new JButton("Play");
	private JCheckBox loop = new JCheckBox("Loop");
	private JLabel time = new JLabel("Miliseconds: ");
	private JTextField length = new JTextField("2000");
	/**
	 * 
	 */
	//private DyIO d = new DyIO();
	private static final long serialVersionUID = -2532174391435417313L;
	public SchedulerGui(){
		setName("DyIO Scheduler");
		slider.setMajorTickSpacing(1000);
		slider.setPaintTicks(true);
		setBounds(100);
		setValue(0);
		
		play.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent arg0) {
				if(st == null){
					long setpoint;
					try{
						setpoint = Long.parseLong(length.getText());
					}catch (NumberFormatException n){
						setpoint=1000;
					}
					length.setText(new Long(setpoint).toString());
					st = new SchedulerThread(setpoint);
					st.start();
					play.setText("Pause");
				}else{
					st.kill();
					st=null;
					play.setText("Play");
				}
			}
			
		});
		
		add(length);
		add(time);
		add (slider);
		add(play);
		add(loop);
		
	}
	
	
	public boolean setConnection(BowlerAbstractConnection connection) {
		DyIORegestry.setConnection(connection);
		return DyIORegestry.get().ping()!=null;
	}
	private void setValue(long  val){
		//System.out.println("Setting value: "+val);
		try{
			slider.setValue((int) (val));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void setBounds(double top){
		slider.setMaximum(0);
		slider.setMaximum((int) (top));
	}
	private boolean isLooping(){
		return loop.isSelected();
	}
	private class SchedulerThread extends Thread{
		private double time;
		private boolean run = true;
		long StartOffset;
		public SchedulerThread(double ms){
			time = ms;
			StartOffset = slider.getValue();
			setBounds((int)(ms));
			slider.setValue((int) StartOffset);
			//setValue(0);
		}
		public void run(){
			System.out.println("Starting timer");
			do{
				long start = System.currentTimeMillis();
				StartOffset = slider.getValue();
				while( (((double)(System.currentTimeMillis()-start))<(time-StartOffset)) && run){
					long offset = ((System.currentTimeMillis()-start))+StartOffset;
					setValue(offset);
					ThreadUtil.wait(100);
				}
				if(run)
					setValue(0);
			}while(isLooping() && run);
			
			play.setText("Play");
			st=null;
		}
		public void kill(){
			run = false;
		}
	}

}
