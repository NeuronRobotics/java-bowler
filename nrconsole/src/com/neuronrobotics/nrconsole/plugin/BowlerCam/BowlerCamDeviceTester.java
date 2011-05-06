package com.neuronrobotics.nrconsole.plugin.BowlerCam;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;



import net.miginfocom.swing.MigLayout;


//import com.neuronrobotics.sdk.BowlerImaging.ImageProcessingFactory;

import com.neuronrobotics.sdk.bowlercam.device.BowlerCamDevice;
import com.neuronrobotics.sdk.bowlercam.device.IWebcamImageListener;
import com.neuronrobotics.sdk.bowlercam.device.ItemMarker;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.network.BowlerUDPClient;
import com.neuronrobotics.sdk.util.ThreadUtil;

@SuppressWarnings("unused")
public class BowlerCamDeviceTester{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7385372910345405369L;
	private JFrame frame = new JFrame();
	private  BowlerCamPanel bcp;

	public BowlerCamDeviceTester() throws IOException, InterruptedException{
		initGui();
//		if (!ConnectionDialog.getBowlerDevice(cam)){
//			System.exit(1);
//		}
		bcp.setConnection(new BowlerUDPClient());
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Log.enableDebugPrint(true);
			Log.enableSystemPrint(true);
			new BowlerCamDeviceTester();
		}catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	private void initGui() throws IOException, InterruptedException{
		frame.setLayout(new MigLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBackground(Color.black);
		//frame.setLocationRelativeTo(null);
		frame.setSize(new Dimension(640,480));
		frame.setVisible(true);
		
		bcp =new BowlerCamPanel();
		frame.add(bcp);
	}


}
