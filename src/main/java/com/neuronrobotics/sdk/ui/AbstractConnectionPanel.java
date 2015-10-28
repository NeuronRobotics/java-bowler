/*******************************************************************************
 * Copyright 2010 Neuron Robotics, LLC
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.neuronrobotics.sdk.ui;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractConnectionPanel.
 */
public abstract class AbstractConnectionPanel extends JPanel {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The title. */
	private String title;
	
	/** The icon. */
	private ImageIcon icon;
	
	/** The connection dialog. */
	private ConnectionDialog connectionDialog;
	
	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 */
	public abstract BowlerAbstractConnection getConnection();
	
	/**
	 * Refresh.
	 */
	public abstract void refresh();
	

	
	/**
	 * Instantiates a new abstract connection panel.
	 *
	 * @param title the title
	 * @param icon the icon
	 * @param connectionDialog the connection dialog
	 */
	public AbstractConnectionPanel(String title, ImageIcon icon,ConnectionDialog connectionDialog) {
		setTitle(title);
		
		setIcon(icon);
		this.setConnectionDialog(connectionDialog);
	}
	
	/**
	 * Instantiates a new abstract connection panel.
	 *
	 * @param title the title
	 * @param connectionDialog the connection dialog
	 */
	public AbstractConnectionPanel(String title,ConnectionDialog connectionDialog) {
		setTitle(title);
		setIcon(ConnectionImageIconFactory.getIcon("images/connection-icon.png"));
		this.setConnectionDialog(connectionDialog);
	}

	/**
	 * Sets the title.
	 *
	 * @param title the new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Sets the icon.
	 *
	 * @param icon the new icon
	 */
	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}
	
	/**
	 * Gets the icon.
	 *
	 * @return the icon
	 */
	public ImageIcon getIcon(){
		
		if(icon == null)
			return new ImageIcon();
		return icon;
	}
	
	/**
	 * Gets the connection dialog.
	 *
	 * @return the connection dialog
	 */
	public ConnectionDialog getConnectionDialog() {
		return connectionDialog;
	}
	
	/**
	 * Sets the connection dialog.
	 *
	 * @param connectionDialog the new connection dialog
	 */
	public void setConnectionDialog(ConnectionDialog connectionDialog) {
		this.connectionDialog = connectionDialog;
	}
}
