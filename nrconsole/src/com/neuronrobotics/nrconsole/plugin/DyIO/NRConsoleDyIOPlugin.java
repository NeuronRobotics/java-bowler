package com.neuronrobotics.nrconsole.plugin.DyIO;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
//import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.graphing.ExcelWriter;
import com.neuronrobotics.graphing.GraphingOptionsDialog;
import com.neuronrobotics.graphing.GraphingWindow;
import com.neuronrobotics.nrconsole.plugin.INRConsoleTabedPanelPlugin;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.nrconsole.plugin.hexapod.HexapodConfigPanel;
import com.neuronrobotics.nrconsole.plugin.hexapod.HexapodNRConsolePulgin;
import com.neuronrobotics.nrconsole.plugin.hexapod.ServoChannelConfiguration;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannel;
import com.neuronrobotics.sdk.dyio.DyIOPowerEvent;
import com.neuronrobotics.sdk.dyio.IDyIOEvent;
import com.neuronrobotics.sdk.dyio.IDyIOEventListener;
import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;

public class NRConsoleDyIOPlugin implements INRConsoleTabedPanelPlugin,IChannelPanelListener,IDyIOEventListener  {
	private GraphingWindow graphingWindow = new GraphingWindow();
	private GraphingOptionsDialog graphingOptionsDialog = new GraphingOptionsDialog(graphingWindow);
	private ExportDataDialog graphingDialog = new ExportDataDialog(this);
	private JMenuItem showGraphMenuItem = new JMenuItem("Show Graph");
	private JMenuItem showHexapodConfig = new JMenuItem("Show Hexapod Configuration");
	private JMenuItem graphOptionsMenuItem = new JMenuItem("Graphing Options");
	private JMenuItem exportData = new JMenuItem("Export Data to File");
	private boolean active=false;
	private DyIOPanel devicePanel = new DyIOPanel();
	private DyIOControlsPanel deviceControls = new DyIOControlsPanel();
	private ArrayList<ChannelManager> channels = new ArrayList<ChannelManager>();
	private HexapodConfigPanel hex=null;
	private JDialog hexFrame;
	private JPanel wrapper;
	public NRConsoleDyIOPlugin(){
		PluginManager.addNRConsoleTabedPanelPlugin(this);
		//hex = new HexapodNRConsolePulgin();
	}
	
	
	public JPanel getTabPane() {
		wrapper = new JPanel(new MigLayout()){
			/**
			 * 
			 */
			private static final long serialVersionUID = -5581797073561156394L;

			
			public void repaint(){
				super.repaint();
				getDeviceDisplay().repaint();
				getDeviceControls().repaint();
			}
		};
		wrapper.add(getDeviceDisplay(), "pos 5 5");
		wrapper.add(getDeviceControls(), "pos 560 5");
		wrapper.setName("DyIO");
		return wrapper;
	}

	
	public boolean isMyNamespace(ArrayList<String> names) {
		for(String s:names){
			if(s.contains("neuronrobotics.dyio.*")){
				active=true;
			}
		}
		return isAcvive();
	}

	
	public boolean setConnection(BowlerAbstractConnection connection) {
		DyIORegestry.setConnection(connection);
		DyIORegestry.get().addDyIOEventListener(this);
		setupDyIO();
		return true;
	}
	private void setupDyIO(){
		
		int index = 0;
		ArrayList<DyIOChannel> chans =(ArrayList<DyIOChannel>) DyIORegestry.get().getChannels();
		//System.out.println("DyIO state: "+device+ " \nchans: "+chans );
		for(DyIOChannel c : chans) {
			//System.out.println(this.getClass()+" Adding channel: "+index+" as mode: "+c.getMode());
			ChannelManager cm = new ChannelManager(c);
			cm.addListener(this);
			if(index == 0) {
				selectChannel(cm);
			}
			channels.add(cm);
			index++;
		}
		//System.out.println(this.getClass()+" setupDyIO: "+ channels.size());
		devicePanel.addChannels(channels.subList(00, 12), false);
		devicePanel.addChannels(channels.subList(12, 24), true);
		//onModeChange();
	}

	
	public boolean isAcvive() {
		// TODO Auto-generated method stub
		return active;
	}

	
	public ArrayList<JMenu> getMenueItems() {
		JMenu collectionMenu = new JMenu("DyIO");
		collectionMenu.add(showGraphMenuItem);
		collectionMenu.add(exportData);
		collectionMenu.add(showHexapodConfig);
		showGraphMenuItem.setMnemonic(KeyEvent.VK_G);
		showGraphMenuItem.addActionListener(new ActionListener() {
			
			
			public void actionPerformed(ActionEvent e) {
				displayGraphingWindow(true);
			}
		});
		showHexapodConfig.addActionListener(new ActionListener() {	
			
			public void actionPerformed(ActionEvent e) {
				hexFrame = new JDialog();
				if(hex == null)
					hex = new HexapodConfigPanel(hexFrame);
				hex.setDyIO();
				hexFrame.setTitle(hex.getName());
				hexFrame.add(hex);			
				hexFrame.pack();
				hexFrame.setLocationRelativeTo(null); 
				hexFrame.setVisible(true);
			}
		});
		exportData.setMnemonic(KeyEvent.VK_E);
		exportData.setEnabled(true);
		exportData.addActionListener(new ActionListener() {
			
			
			public void actionPerformed(ActionEvent e) {
				displayGraphingDialog();
			}
		});
		ArrayList<JMenu> m = new ArrayList<JMenu>();
		m.add(collectionMenu);
		if(isAcvive())
			return m;
		return null;
	}
	public void addActionListener(ActionListener l) {
		showGraphMenuItem.addActionListener(l);
		graphOptionsMenuItem.addActionListener(l);
		exportData.addActionListener(l);
	}
	public void displayGraphingWindow(boolean visible) {
		graphingWindow.setVisible(visible);
	}
	
	public void displayGraphingDialog() {
		graphingDialog.showDialog();
	}
	
	public void displayGraphingOptionsDialog(boolean b) {
		graphingOptionsDialog.setVisible(b);
	}
	
	public void toggleGraph(ChannelRecorder channelRecorder) {
		graphingWindow.toggleDataChannel(channelRecorder.getDataChannel());
	}

	
	public void onRecordingEvent(ChannelManager source, boolean enabled) {
		toggleGraph(source.getChannelRecorder());
	}
	
	public void selectChannel(ChannelManager cm) {
		cm.getControlPanel();
	}

	public DyIOPanel getDeviceDisplay() {
		return devicePanel;
	}
	
	public DyIOControlsPanel getDeviceControls() {
		return deviceControls;
	}
	
	
	public void onClick(ChannelManager source, int type) {
		switch(type) {
		case SINGLE_CLICK:
			deviceControls.setChannel(source.getControlPanel());
			break;
		case SHIFT_CLICK:
		case CTRL_CLICK:
			deviceControls.addChannel(source.getControlPanel());
			break;
		}
	}

	
	public void onModeChange() {
		for(ChannelManager cm : channels) {
			cm.refresh();
		}
	}

	public void recordData() {
		JFileChooser chooser = new JFileChooser();
		chooser.showSaveDialog(null);
		
		File file = chooser.getSelectedFile();
		chooser.setMultiSelectionEnabled(false);
        chooser.setAcceptAllFileFilterUsed(false);
        //FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Workbook", "xls");

        int option = chooser.showSaveDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            try {
                System.out.println(chooser.getFileFilter());

                //if(!filter.accept(file)) {
                	file = new File(file.getAbsolutePath() + ".xls");
                //}
                	
        		ExcelWriter ew = new ExcelWriter();
        		ew.setFile(file);
        		for(ChannelManager cm : channels) {
        			ew.addData(cm.getChannelRecorder().getDataChannel());
        		}
        		ew.cleanup();
        		JOptionPane.showMessageDialog(null, "Successfully exported data as an Excel file", "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch(Exception e) {
            	JOptionPane.showMessageDialog(null, "Unable to save file. Please check the file and try again.", "File Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
	}

	
	public void onDyIOEvent(IDyIOEvent e) {
		if(e.getClass() == DyIOPowerEvent.class){
			System.out.println("Got power event: "+e);
			devicePanel.setPowerEvent(((DyIOPowerEvent)e));
			try{
				for(ChannelManager cm : channels) {
					cm.onDyIOPowerEvent();
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}

}
