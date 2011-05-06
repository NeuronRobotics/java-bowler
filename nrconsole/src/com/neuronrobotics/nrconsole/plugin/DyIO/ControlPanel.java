package com.neuronrobotics.nrconsole.plugin.DyIO;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.Timer;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.nrconsole.plugin.DyIO.channelwidgets.AnalogChannelUI;
import com.neuronrobotics.nrconsole.plugin.DyIO.channelwidgets.ControlWidget;
import com.neuronrobotics.nrconsole.plugin.DyIO.channelwidgets.CounterInputWidget;
import com.neuronrobotics.nrconsole.plugin.DyIO.channelwidgets.CounterOutputWidget;
import com.neuronrobotics.nrconsole.plugin.DyIO.channelwidgets.DigitalInputWidget;
import com.neuronrobotics.nrconsole.plugin.DyIO.channelwidgets.DigitalOutputWidget;
import com.neuronrobotics.nrconsole.plugin.DyIO.channelwidgets.LabelChannelUI;
import com.neuronrobotics.nrconsole.plugin.DyIO.channelwidgets.PPMReaderWidget;
import com.neuronrobotics.nrconsole.plugin.DyIO.channelwidgets.SPIChannelWidget;
import com.neuronrobotics.nrconsole.plugin.DyIO.channelwidgets.ServoChannelUI;
import com.neuronrobotics.nrconsole.plugin.DyIO.channelwidgets.ServoWidget;
import com.neuronrobotics.nrconsole.plugin.DyIO.channelwidgets.UARTChannelUI;
import com.neuronrobotics.sdk.commands.bcs.io.GetChannelModeCommand;
import com.neuronrobotics.sdk.dyio.DyIOChannelEvent;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.IChannelEventListener;
import com.neuronrobotics.sdk.dyio.IDyIOChannelModeChangeListener;
import com.neuronrobotics.sdk.dyio.peripherals.DyIOAbstractPeripheral;

public class ControlPanel extends JPanel  implements IChannelEventListener,IDyIOChannelModeChangeListener {
	
	public static final int panelHight =100;
	public static final int panelWidth =400;
	
	private static final long serialVersionUID = 1L;
	private JComboBox modes = new JComboBox();
	private ChannelManager manager;
	private HashMap<DyIOChannelMode, ControlWidget> widgets = new HashMap<DyIOChannelMode, ControlWidget>();
	private ControlWidget currentWidget=null;
	private JCheckBox recordData = new JCheckBox("Record Channel");
	private AdvancedAsyncWidget advanced;
	private DyIOChannelMode previousMode=null;
	private Timer timer = new Timer(1000, new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
			autoPoll();
		}
	});
	public void setupModesComboBox(){
		System.out.println("Resetting channel "+getManager().getChannel().getChannelNumber()+" modes: "+getManager().getChannel().getAvailableModes());
		boolean hasServ = false;
		for(DyIOChannelMode m:getManager().getChannel().getAvailableModes() ){
			if(m==DyIOChannelMode.SERVO_OUT)
				hasServ = true;
		}
		if(getManager().getChannel().getMode() == DyIOChannelMode.SERVO_OUT && hasServ == false){
			//Invalid state, ignore for now...
			return;
		}
		if(hasServ == false){
			modes.removeItem(DyIOChannelMode.SERVO_OUT);
		}else{
			boolean servoExists = false;
			for(int i=0;i<modes.getItemCount();i++){
				if(modes.getItemAt(i)==DyIOChannelMode.SERVO_OUT){
					servoExists = true;
				}
			}
			if(!servoExists){
				modes.addItem(DyIOChannelMode.SERVO_OUT);
				if(getManager().getChannel().getMode() == DyIOChannelMode.SERVO_OUT){
					for(int i=0;i<modes.getItemCount();i++){
						if(modes.getItemAt(i)==DyIOChannelMode.SERVO_OUT){
							modes.setSelectedItem(i);
						}
					}
				}
			}
		}
	}
	public ControlPanel(ChannelManager m) {
		setManager(m);
		advanced = new AdvancedAsyncWidget();
		for(DyIOChannelMode md:getManager().getChannel().getAvailableModes() ){
			modes.addItem(md);
		}
		setupModesComboBox();
		modes.setRenderer(new ModeComboRenderer());
		modes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DyIOChannelMode mode = (DyIOChannelMode) modes.getSelectedItem();
				getManager().setMode(mode);
				
			}
		});
		
		recordData.addActionListener(new ActionListener() {
			
			
			public void actionPerformed(ActionEvent e) {
				getManager().fireOnRecordingEvent();
				if(recordData.isSelected() ) {
					getManager().getChannelRecorder().setGraphing(true);
					timer.restart();
				} else {
					getManager().getChannelRecorder().setGraphing(false);
					timer.stop();
				}
			}
		});
		
		setLayout(new MigLayout());
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black, 2), 
				                                   getManager().getChannel().toString().trim()));
		
		setMinimumSize(new Dimension(panelWidth, panelHight));
	}
	
	public void setUpModeUI() {	
		
		DyIOChannelMode mode = getMode();
		if(previousMode==null || mode != previousMode){
			//System.out.println(this.getClass()+"Setup mode UI: "+getManager().getChannel());
			previousMode=mode;
			try{
				switch(mode) {
				case ANALOG_IN:
					setCurrentWidget(new AnalogChannelUI(getManager()));
					break;
				case DIGITAL_IN:
					setCurrentWidget(new DigitalInputWidget(getManager()));
					break;
				case DIGITAL_OUT:
					setCurrentWidget(new DigitalOutputWidget(getManager()));
					break;
				case SERVO_OUT:
					setCurrentWidget(new ServoWidget(getManager(), mode));
					break;
				case DC_MOTOR_DIR:
				case DC_MOTOR_VEL:
				case PWM_OUT:
					setCurrentWidget(new ServoChannelUI(getManager(), mode));
					break;
				case USART_RX:
				case USART_TX:
					setCurrentWidget(new UARTChannelUI(getManager(), mode));
					break;
				case COUNT_IN_INT:
					setCurrentWidget(new CounterInputWidget(getManager(),mode));
					break;
				case COUNT_IN_DIR:
				case COUNT_IN_HOME:
					setCurrentWidget(new LabelChannelUI(getManager(), "---"));
					break;
				case COUNT_OUT_INT:
					setCurrentWidget(new CounterOutputWidget(getManager(),mode));
					break;
				case COUNT_OUT_DIR:
				case COUNT_OUT_HOME:
					setCurrentWidget(new LabelChannelUI(getManager(), "---"));
					break;
				case SPI_CLOCK:
					setCurrentWidget(new SPIChannelWidget(getManager()));
					break;
				case SPI_MISO:
				case SPI_MOSI:
					setCurrentWidget(new LabelChannelUI(getManager(), "---"));
					break;
				case PPM_IN:
					setCurrentWidget(new PPMReaderWidget(getManager()));
					break;
				case OFF:
				default:
					setCurrentWidget(new LabelChannelUI(getManager(), "---"));
				}
			}catch(Exception e){
				setCurrentWidget(new LabelChannelUI(getManager(), "---"));
			}
		}else{
			return;
		}

		widgets.put(mode, getCurrentWidget());

		modes.setSelectedItem(mode);
		
		removeAll();		
		add(new JLabel("Modes "), "cell 0 0");
		add(modes, "cell 0 0, spanx");
		add(recordData, "cell 0 0, spanx");
		if(getCurrentWidget().getChannel().hasAsync()){
			add(advanced , "cell 0 1");
			advanced.setControlPanel(this);
		}
		


		add(getCurrentWidget(), "cell 0 3, spanx, wrap");
		revalidate();
		repaint();
	}
	
	private ControlWidget getWidget(DyIOChannelMode mode) {

		return getCurrentWidget();
	}
	
	public void refresh() {
		//System.out.println(this.getClass()+" Refresh");
		try {
			setUpModeUI();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void autoPoll() {
		if(getCurrentWidget() == null) {
			return;
		}
		
		if(!recordData.isSelected()) {
			return;
		}
		
		getCurrentWidget().pollValue();
	}
	
	public void setAutoPoll(boolean autoPoll) {
		if(autoPoll) {
			timer.restart();
		} else {
			timer.stop();
		}
	}
	

	public boolean isRecordingEnabled() {
		return recordData.isSelected();
	}
	
	
	public void onChannelEvent(DyIOChannelEvent e) {
		
	}
	
	
	private class ModeComboRenderer extends JLabel implements ListCellRenderer {
		private static final long serialVersionUID = 1L;

		public ModeComboRenderer() {
			setOpaque(true);
			setHorizontalAlignment(LEFT);
			setVerticalAlignment(CENTER);
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			DyIOChannelMode mode = (DyIOChannelMode) value;
			if(mode == null)
				mode = DyIOChannelMode.DIGITAL_IN;
			
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			try {
				setIcon(new ImageIcon(DyIOPanel.class.getResource("images/icon-" + mode.toSlug() + ".png")));
			} catch(Exception e) {
				setIcon(new ImageIcon(DyIOPanel.class.getResource("images/icon-off.png")));
			}
			setText(mode.toString());
			
			return this;
		}
	}
	
	private class AutoPollTime {
		private String title;
		private int time;
		
		public AutoPollTime(String title, int time) {
			this.title = title;
			this.time = time;
		}
		
		public int toInt() {
			return time;
		}
		
		
		public String toString() {
			return title;
		}
		
		
		public boolean equals(Object o) {
			if(!(o instanceof AutoPollTime)) {
				return false;
			}
			
			AutoPollTime a = (AutoPollTime) o;
			return a.time == time;
		}
	}

	public DyIOChannelMode getMode() {
		return getManager().getChannel().getMode();
	}

	
	public void onModeChange(DyIOChannelMode newMode) {
		// TODO Auto-generated method stub
		
	}

	public void setManager(ChannelManager manager) {
		this.manager = manager;
	}

	public ChannelManager getManager() {
		return manager;
	}

	public DyIOAbstractPeripheral getPerpheral() {
		return getCurrentWidget().getPerphera();
	}

	public void setCurrentWidget(ControlWidget currentWidget) {
		this.currentWidget = currentWidget;
	}

	public ControlWidget getCurrentWidget() {
		return currentWidget;
	}
}
