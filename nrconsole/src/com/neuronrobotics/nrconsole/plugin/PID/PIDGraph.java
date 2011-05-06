package com.neuronrobotics.nrconsole.plugin.PID;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.Log;

import com.neuronrobotics.graphing.CSVWriter;
import com.neuronrobotics.graphing.GraphDataElement;

public class PIDGraph extends JPanel  {
	private ArrayList<GraphDataElement> dataTable = new ArrayList<GraphDataElement>();
	private XYSeries setpoints;
	private XYSeries positions;
	private XYSeriesCollection xyDataset=new XYSeriesCollection();
	private JButton save = new JButton("Export Data");
	private JButton clear = new JButton("Clear graph");
	private int channel;
	
	private double startTime=System.currentTimeMillis();
	public PIDGraph(int channel){
		this.channel=channel;
		setLayout(new MigLayout());
		JFreeChart chart = ChartFactory.createXYLineChart(
				"Live Data", 
				"Time", 
				"Value",
				xyDataset, 
				PlotOrientation.VERTICAL, 
				true, 
				false, 
				false);
		ChartPanel cp = new ChartPanel(chart);
		add(cp,"wrap");
		add(save);
		add(clear,"wrap");
		save.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				String name ="Graph_Of_Group_"+getChannel()+"_"+System.currentTimeMillis()+".csv";
				CSVWriter.WriteToCSV(dataTable,name);
				dataTable.clear();
				setpoints.clear();
				positions.clear();
			}
		});
		clear.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				dataTable.clear();
				setpoints.clear();
				positions.clear();
			}
		});
		
		setSize(new Dimension(300,200));
		init();
	}
	/**
	 * long 
	 */
	private static final long serialVersionUID = 1L;
	public void addEvent(double setpoint, double position) {
		try{
			
			double[] data = {setpoint,position};
			double time = (System.currentTimeMillis()-startTime);
			dataTable.add(new GraphDataElement((long) time,data));
			XYDataItem s = new XYDataItem(time,setpoint);
			XYDataItem p = new XYDataItem(time,position);
			synchronized (setpoints){
				setpoints.add(s);
			}
			synchronized(positions){
				positions.add(p);
			}
		}catch(Exception e){
			System.err.println("Failed to set a data point");
			e.printStackTrace();
			Log.error(e);
			Log.error(e.getStackTrace());
			init();
		}
	}
	private void init(){
		xyDataset.removeAllSeries();
		setpoints = new  XYSeries("Set Point");
		positions = new  XYSeries("Position");
		xyDataset.addSeries(setpoints);
		xyDataset.addSeries(positions);
	}
	private int getChannel() {
		return channel;
	}

}
