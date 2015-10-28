package com.neuronrobotics.sdk.ui;

import javax.swing.ImageIcon;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating ConnectionImageIcon objects.
 */
public class ConnectionImageIconFactory {
	
	/**
	 * Gets the icon.
	 *
	 * @param path the path
	 * @return the icon
	 */
	public static ImageIcon getIcon(String path){
		try{
			return new ImageIcon(AbstractConnectionPanel.class.getResource(path));
		}catch (Exception e){
			
		}
		return new ImageIcon();
	}
}
