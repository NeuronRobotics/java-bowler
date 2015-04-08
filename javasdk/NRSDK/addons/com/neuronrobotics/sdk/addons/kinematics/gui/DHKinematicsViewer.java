package com.neuronrobotics.sdk.addons.kinematics.gui;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics;

public class DHKinematicsViewer extends JFXPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4624867202513493512L;
	DHParameterKinematics robot;
	private Group viewContainer = new Group();

	public DHKinematicsViewer(final DHParameterKinematics bot) {
		robot = bot;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				
				Jfx3dManager viewer = new Jfx3dManager(viewContainer);

				viewer.attachArm(bot,bot.getFactory().getDyio());

				setScene(new Scene(viewContainer));
			}
		});

	}
}
