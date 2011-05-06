package com.neuronrobotics.nrconsole.plugin.bootloader;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.nrconsole.plugin.bootloader.core.Hexml;
import com.neuronrobotics.nrconsole.plugin.bootloader.core.NRBoot;
import com.neuronrobotics.nrconsole.plugin.bootloader.gui.StatusLabel;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.InvalidResponseException;
import com.neuronrobotics.sdk.common.NoConnectionAvailableException;

public class BootloaderPanel extends JPanel implements ActionListener {
	
	private NRBoot blApp;
	
	private JButton fileButton,loadButton;
	private Hexml hex=null;
	private JProgressBar progress = new JProgressBar();
	private StatusLabel fileStatus = new StatusLabel();
	private StatusLabel loadStatus = new StatusLabel();
	private String revision;
	private static File file = null;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6467421820450464854L;
	
	public BootloaderPanel(){
		//System.out.println("Starting GUI");
		setName("NR Bootloader");
		fileButton = new JButton();
		fileButton.addActionListener(this);
		resetFile();
		
		
		loadButton = new JButton();
		loadButton.addActionListener(this);
		resetLoad();
		
		JPanel buttonPanel = new JPanel(new MigLayout());

		buttonPanel.add(fileStatus);
        buttonPanel.add(fileButton, "wrap");
        
        JPanel prog = new JPanel(new MigLayout());
        prog.add(loadButton, "wrap");
        prog.add(progress,"wrap");
        
        buttonPanel.add(loadStatus);
        buttonPanel.add(prog);
        
        add(buttonPanel);
 
	}
	

	public boolean setConnection(BowlerAbstractConnection connection) {
       
		blApp = new NRBoot(connection);
		loadButton.setEnabled(true);
		return connection.isConnected();
	}

	public NRBoot getBlApp() {
		return blApp;
	}
	
	public void resetFile() {
		fileButton.setText("Select a NR Firmware File..");
	}
	
	public void resetLoad() {
		loadButton.setEnabled(false);
		loadButton.setText("Load Firmware");
		loadStatus.setStatus(0);
		reloadFile();
	}
	
	public void loadFile() {
		System.out.println("Loading file:");
    	JFileChooser fc = new JFileChooser();
    	File dir2=null; 
    	try {
    		dir2 = new File ("../DyIO/FirmwarePublish/Dev/");
    		if(!dir2.exists()) {
    			dir2=new File ("../firmware/");
    			if(!dir2.exists()) {
    				dir2=null;
    			}
    		}
    	}catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	if(dir2==null)
			dir2=new File (".");
    	if(file!=null){
    		fc.setSelectedFile(file);
    		System.out.println("Starting with: "+file.getAbsolutePath());
    	}else{
			System.out.println("Starting in: "+dir2.getAbsolutePath());
			fc.setCurrentDirectory(dir2);	
    	}
    	fc.setFileFilter(new FileFilter() {
			
			
			public String getDescription() {
				return "NR Firmware Format (xml)";
			}
			
			public boolean accept(File f) {
				if(f.isDirectory()) {
					return true;
				}
				
				String path = f.getAbsolutePath().toLowerCase();
				if ((path.endsWith("xml") && (path.charAt(path.length() - 3)) == '.')) {
					return true;
				}
				
				return f.getName().matches(".+\\.xml$");
			}
		});
    	
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            if(!file.getName().matches(".+\\.xml$")){
            	String message = "Invalid file type. Must be .xml";
            	JOptionPane.showMessageDialog(null, message, message, JOptionPane.ERROR_MESSAGE);
            	fileStatus.setStatus(StatusLabel.ERROR);
            	resetFile();
            	resetLoad();
            	return;
            }
            
            reloadFile();
        }
        loadButton.setEnabled(true);
	}
	private void reloadFile(){
		if(file!=null){
        	try {
        		hex = new Hexml(file);
        	}catch (Exception e) {
        		e.printStackTrace();
        		String message = "Invalid xml file";
        		JOptionPane.showMessageDialog(null, message, message, JOptionPane.ERROR_MESSAGE);
        		return;
        	}
        	fileButton.setText("Using: "+file.getName());
            revision = hex.getRevision();
            fileStatus.setStatus(StatusLabel.OK);
		}
	}
	
	public String getRevision() {
		return revision;
	}
	private void resetAll() {
		getBlApp().getDevice().disconnect();
		resetLoad();
	}
	public class LoaderChecker extends Thread{
		public void run() {
			progress.setMaximum(getBlApp().getProgressMax());
			progress.setMinimum(0);
			//progress.setIndeterminate(true);
			while(getBlApp().isLoadDone() == false) {
				try {Thread.sleep(100);} catch (InterruptedException e) {}
				progress.setValue(getBlApp().getProgressValue());
			}
			progress.setIndeterminate(false);
			String message = "Success! Your Bowler device is now updated to version: "+hex.getRevision();
    		JOptionPane.showMessageDialog(null, message, message, JOptionPane.INFORMATION_MESSAGE);
    		resetAll();
		}
	}

	
	
	public void actionPerformed(ActionEvent e) {
        //Handle open button action.
        if (e.getSource() == fileButton) {
        	loadFile();
        }
        if (e.getSource() == loadButton) {
        	try{
	        	if (getBlApp()!=null){
	        		loadStatus.setStatus(StatusLabel.OK);
	        		//System.out.println("Loading firmware");
	        		reloadFile();
	        		getBlApp().loadCores(hex.getCores());
		    		loadButton.setText("Loading....");
		    		loadButton.setEnabled(false);
		    		LoaderChecker l = new LoaderChecker();
		    		l.start();
	        	}	
        	}catch(InvalidResponseException ex){
        		String message = "Device is not in bootloader mode!";
        		JOptionPane.showMessageDialog(null, message, message, JOptionPane.ERROR_MESSAGE);
        	}catch(NoConnectionAvailableException ex){
        		String message = "Device is not no longer connected to bootloader";
        		JOptionPane.showMessageDialog(null, message, message, JOptionPane.ERROR_MESSAGE);
        		resetLoad();
        	}
        }
	}
}
