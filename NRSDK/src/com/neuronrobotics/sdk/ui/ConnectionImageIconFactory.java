package com.neuronrobotics.sdk.ui;

import javax.swing.ImageIcon;

public class ConnectionImageIconFactory {
	public static ImageIcon getIcon(String path){
		try{
			return new ImageIcon(AbstractConnectionPanel.class.getResource(path));
		}catch (Exception e){
			
		}
		return new ImageIcon();
	}
}
