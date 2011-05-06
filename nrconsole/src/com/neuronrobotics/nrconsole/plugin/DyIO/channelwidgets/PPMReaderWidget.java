package com.neuronrobotics.nrconsole.plugin.DyIO.channelwidgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.nrconsole.plugin.DyIO.ChannelManager;
import com.neuronrobotics.sdk.dyio.peripherals.DyIOAbstractPeripheral;
import com.neuronrobotics.sdk.dyio.peripherals.IPPMReaderListener;
import com.neuronrobotics.sdk.dyio.peripherals.PPMReaderChannel;

public class PPMReaderWidget extends ControlWidget implements IPPMReaderListener{

	/**
	 * long 
	 */
	private static final long serialVersionUID = 1L;
	private PPMReaderChannel ppmr;
	private JLabel [] ppmLabels = new JLabel[6] ;
	private JComboBox [] ppmLinks = new JComboBox [6] ;
	private int [] cross;
	private JPanel values = new JPanel(new MigLayout());
	public PPMReaderWidget(ChannelManager c) {
		super(c);
		System.out.println("\nInitializing PPM channel");
		try {
			ppmr = new PPMReaderChannel(getChannel());
			ppmr.addPPMReaderListener(this);
			cross = ppmr.getCrossLink();
			int [] vals = ppmr.getValues();
			for(int i=0;i<ppmLabels.length;i++){
				ppmLabels[i]=new JLabel(new Integer(vals[i]).toString());
				ppmLinks[i] = new JComboBox();

				ppmLinks[i].addItem("None");
				for(int j=0;j<24;j++){
					ppmLinks[i].addItem(new Integer(j));
				}
				selectChan(i,cross[i]);
				ppmLinks[i].addActionListener(new linkListener(i));
				
				
				values.add(new JLabel("PPM "+new Integer(i)+" : "));
				values.add(ppmLabels[i]);
				values.add(ppmLinks[i],"wrap");
			}
			add(values);
			System.out.println("\nPPM UI ok");
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException();
		}
		
	}
	private void selectChan(int ppmChan,int dyioChan){
		//System.out.println("\nSelecting dyio channel: "+dyioChan+" on ppm: "+ppmChan);
		if(dyioChan == PPMReaderChannel.NO_CROSSLINK)
			return;
		for(int i=0;i<25;i++){
			Object o = ppmLinks[ppmChan].getItemAt(i);
			Integer in= new Integer(PPMReaderChannel.NO_CROSSLINK);
			try{
				 in= (Integer)o;
			}catch(ClassCastException nx) {
				String s = ((String) o);
				try {
					in = new Integer(s);
				}catch (NumberFormatException nf) {
					
				}
			}
			//System.out.println("Int value of item: "+o);
			if(in.intValue() == dyioChan){
				ppmLinks[ppmChan].setSelectedItem(o);
				return;
			}
		}
	}
	private class linkListener implements ActionListener{
		private int index;
		public linkListener(int i){
			index=i;
		}
		
		public void actionPerformed(ActionEvent e) {
			int [] links=ppmr.getCrossLink();
			
			try{
				Integer val = (Integer)ppmLinks[index].getSelectedItem();
				links[index] = val.intValue();
				
			}catch(Exception ex){
				links[index]=PPMReaderChannel.NO_CROSSLINK;
			}
			ppmr.setCrossLink(links);
		}
		
	}
	
	
	public void onPPMPacket(int[] values) {
		for(int i=0;i<ppmLabels.length;i++){
			ppmLabels[i].setText(new Integer(values[i]).toString());
		}
		repaint();
//		String s="\nPPM event: [";
//		for(int i=0;i<values.length;i++){
//			s+=values[i]+" ";
//		}
//		System.out.println(s+"]");
	}
	
	public DyIOAbstractPeripheral getPerphera() {
		return ppmr;
	}

}
