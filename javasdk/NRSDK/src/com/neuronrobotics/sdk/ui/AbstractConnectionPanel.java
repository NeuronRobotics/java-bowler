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

/**
 * 
 */
public abstract class AbstractConnectionPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private String title;
	private ImageIcon icon;
	
	public abstract BowlerAbstractConnection getConnection();
	public abstract void refresh();
	
	public AbstractConnectionPanel(String title, ImageIcon icon) {
		setTitle(title);
		
		setIcon(icon);
	}
	
	public AbstractConnectionPanel(String title) {
		setTitle(title);
		setIcon(ConnectionImageIconFactory.getIcon("images/connection-icon.png"));
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}
	
	public ImageIcon getIcon(){
		
		if(icon == null)
			return new ImageIcon();
		return icon;
	}
}
