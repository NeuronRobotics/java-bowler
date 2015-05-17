package com.neuronrobotics.sdk.addons.kinematics.gui;

/*
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR;
import com.neuronrobotics.sdk.addons.kinematics.DHLink;
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics;
import com.neuronrobotics.sdk.addons.kinematics.ITaskSpaceUpdateListenerNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.IConnectionEventListener;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.dypid.DyPIDConfiguration;
import com.neuronrobotics.sdk.dyio.peripherals.DigitalInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.IDigitalInputListener;
import com.neuronrobotics.sdk.pid.PIDConfiguration;

import javafx.application.Application;
import javafx.application.Platform;
import static javafx.application.Application.launch;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.PerspectiveCamera;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import static javafx.scene.input.KeyCode.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;
import javafx.scene.Node;

/**
 * MoleculeSampleApp
 */
public class Jfx3dManager extends JFXPanel {

	private final Group root = new Group();
	final Group axisGroup = new Group();
	final Xform world = new Xform();
	final PerspectiveCamera camera = new PerspectiveCamera(true);
	final Xform cameraXform = new Xform();
	final Xform cameraXform2 = new Xform();
	final Xform cameraXform3 = new Xform();
	final double cameraDistance = 1000;
	final Xform moleculeGroup = new Xform();
	private Timeline timeline;
	boolean timelinePlaying = false;
	double ONE_FRAME = 1.0 / 24.0;
	double DELTA_MULTIPLIER = 200.0;
	double CONTROL_MULTIPLIER = 0.1;
	double SHIFT_MULTIPLIER = 0.1;
	double ALT_MULTIPLIER = 0.5;
	double mousePosX;
	double mousePosY;
	double mouseOldX;
	double mouseOldY;
	double mouseDeltaX;
	double mouseDeltaY;

	private final Group manipulator = new Group();
	private final Group lookGroup = new Group();

	private int boxSize = 50;
	// private Box myBox = new Box(1, 1,boxSize);

	private DHParameterKinematics model;
	private DyIO master;

	private boolean buttonPressed = false;
	private SubScene scene;
	private MeshView selectedObject = null;
	private Affine selsectedAffine = new Affine();
	private Affine robotBase = new Affine();
	private Affine cameraVR = new Affine();

	public Jfx3dManager() {
		buildScene();
		buildCamera();
		buildAxes();

		setSubScene(new SubScene(getRoot(), 1024, 1024, true, null));
		getSubScene().setFill(Color.GREY);
		handleKeyboard(getSubScene(), world);
		handleMouse(getSubScene(), world);
		getSubScene().setCamera(camera);

		setScene(new Scene(new Group(getSubScene())));

	}
	public void removeObjects(){
		lookGroup.getChildren().clear();
	}

	public void removeObject(MeshView previous) {
		if (previous != null) {
			lookGroup.getChildren().remove(previous);
		}

	}

	public MeshView addObject(MeshView current) {

		lookGroup.getChildren().add(current);
		return current;
	}

	public void saveToPng(File f) {
		String fName = f.getAbsolutePath();

		if (!fName.toLowerCase().endsWith(".png")) {
			fName += ".png";
		}

		int snWidth = 1024;
		int snHeight = 1024;

		double realWidth = getRoot().getBoundsInLocal().getWidth();
		double realHeight = getRoot().getBoundsInLocal().getHeight();

		double scaleX = snWidth / realWidth;
		double scaleY = snHeight / realHeight;

		double scale = Math.min(scaleX, scaleY);

		PerspectiveCamera snCam = new PerspectiveCamera(false);
		snCam.setTranslateZ(-200);

		SnapshotParameters snapshotParameters = new SnapshotParameters();
		snapshotParameters.setTransform(new Scale(scale, scale));
		snapshotParameters.setCamera(snCam);
		snapshotParameters.setDepthBuffer(true);
		snapshotParameters.setFill(Color.TRANSPARENT);

		WritableImage snapshot = new WritableImage(snWidth,
				(int) (realHeight * scale));

		getRoot().snapshot(snapshotParameters, snapshot);

		try {
			ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png",
					new File(fName));
		} catch (IOException ex) {
			ex.printStackTrace();
			Log.error(ex.getMessage());
		}
	}

	public void attachArm(final DHParameterKinematics model) {
		master = model.getFactory().getDyio();

		new DigitalInputChannel(master, 23)
				.addDigitalInputListener(new IDigitalInputListener() {
					@Override
					public void onDigitalValueChange(
							DigitalInputChannel source, final boolean isHigh) {
						//System.err.println("Button pressed");
						Platform.runLater(new Runnable() {
							
							@Override
							public void run() {
								if (!isHigh) {

									ObservableList<Node> cadBits = lookGroup
											.getChildren();
									for (Node n : cadBits) {
										double x = n.getTranslateX();
										double y = n.getTranslateY();
										double z = n.getTranslateZ();
										//if (threedBoundCheck(x, y, z,/selsectedAffine, 10)) {
											if (MeshView.class.isInstance(n)) {
												System.out
														.println("Selecting Object");
												selectedObject = (MeshView) n;
											}else{
												System.out.println("Not Touching "+n.getClass());
											}
										//}
									}
									if (selectedObject != null) {
										System.out
										.println("Grabbing Object ");
										selectedObject.getTransforms().clear();
										selectedObject.getTransforms().addAll(
												robotBase,
												selsectedAffine
												);
									}
									
								} else {
									// button released, look for devices
									if (selectedObject != null) {
										// freeze it in place
										selectedObject.getTransforms().clear();
										selectedObject.getTransforms().addAll(
												robotBase,
												selsectedAffine.clone());
										selectedObject = null;
									}
								}
							}
						});
	
					}
				});

		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				ArrayList<DHLink> links = model.getDhChain().getLinks();
				for(DHLink dh : links) {
					final Axis a = new Axis(15);
					a.getChildren().add(new Sphere(5));
					a.getTransforms().add(dh.getListener());
					manipulator.getChildren().add(a);
					master.addConnectionEventListener(new IConnectionEventListener() {
						@Override public void onDisconnect(BowlerAbstractConnection source) {
							manipulator.getChildren().remove(a);
							a.getTransforms().clear();
						}
						@Override public void onConnect(BowlerAbstractConnection source) {}
					});
				}
				//get the affine of the tip of the chain
				selsectedAffine =  links.get(links.size()-1).getListener();

				robotBase.setTx(100);
				robotBase.setTy(100);
				robotBase.setTz(-5);
				manipulator.getTransforms().add(robotBase);
				world.getChildren().addAll(manipulator);
			}
		});

	}

	private boolean oneDBound(double location, double target, double bound) {
		if (location > (target + bound))
			return false;
		if (location < (target - bound))
			return false;
		return true;
	}

	private boolean threedBoundCheck(double x, double y, double z, Affine a,
			double distance) {
//		if (oneDBound(x, a.getTx(), distance))
//			return false;
//		if (oneDBound(y, a.getTy(), distance))
//			return false;
//		if (oneDBound(z, a.getTz(), distance))
//			return false;
		return true;
	}

	public void attachArm(DyIO master, String xml) {
		for (int i = 0; i < master.getPIDChannelCount(); i++) {
			// disable PID controller, default PID and dypid configurations are
			// disabled.
			master.ConfigureDynamicPIDChannels(new DyPIDConfiguration(i));
			master.ConfigurePIDController(new PIDConfiguration());
		}
		attachArm(new DHParameterKinematics(master, xml));
	}

	public void disconnect() {
		if (master != null) {
			master.disconnect();
		}
	}

	private void buildScene() {
		world.rx.setAngle(-90);// point z upwards
		world.ry.setAngle(180);// arm out towards user
		getRoot().getChildren().add(world);
	}

	private void buildCamera() {
		getRoot().getChildren().add(cameraXform);
		cameraXform.getChildren().add(cameraXform2);
		cameraXform2.getChildren().add(cameraXform3);
		cameraXform3.getChildren().add(camera);
		cameraXform3.setRotateZ(180.0);

		camera.setNearClip(0.1);
		camera.setFarClip(10000.0);
		camera.setTranslateZ(-cameraDistance);
		camera.getTransforms().add(getCameraVR());
		cameraXform.ry.setAngle(320.0);
		cameraXform.rx.setAngle(40);
		
	}
	
	public DoubleProperty getCameraFieldOfViewProperty(){
		return camera.fieldOfViewProperty();
	}

	private void buildAxes() {

		axisGroup.getChildren().addAll(new Axis());
		world.getChildren().addAll(axisGroup, lookGroup);
	}

	private void handleMouse(SubScene scene, final Node root) {
		scene.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				mousePosX = me.getSceneX();
				mousePosY = me.getSceneY();
				mouseOldX = me.getSceneX();
				mouseOldY = me.getSceneY();
			}
		});
		scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				mouseOldX = mousePosX;
				mouseOldY = mousePosY;
				mousePosX = me.getSceneX();
				mousePosY = me.getSceneY();
				mouseDeltaX = (mousePosX - mouseOldX);
				mouseDeltaY = (mousePosY - mouseOldY);

				double modifier = 1.0;
				double modifierFactor = 0.1;

				if (me.isControlDown()) {
					modifier = 0.1;
				}
				if (me.isShiftDown()) {
					modifier = 10.0;
				}
				if (me.isPrimaryButtonDown()) {
					cameraXform.ry.setAngle(cameraXform.ry.getAngle()
							- mouseDeltaX * modifierFactor * modifier * 2.0); // +
					cameraXform.rx.setAngle(cameraXform.rx.getAngle()
							+ mouseDeltaY * modifierFactor * modifier * 2.0); // -
				} else if (me.isSecondaryButtonDown()) {
					double z = camera.getTranslateZ();
					double newZ = z + mouseDeltaX * modifierFactor * modifier;
					camera.setTranslateZ(newZ);
				} else if (me.isMiddleButtonDown()) {
					cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX
							* modifierFactor * modifier * 0.3); // -
					cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY
							* modifierFactor * modifier * 0.3); // -
				}
			}
		});
		scene.addEventHandler(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {

			@Override
			public void handle(ScrollEvent t) {
				if (ScrollEvent.SCROLL == (t).getEventType()) {

					double zoomFactor = (t.getDeltaY());

					double z = camera.getTranslateZ();
					double newZ = z + zoomFactor;
					camera.setTranslateZ(newZ);
					// System.out.println("Z = "+newZ);
				}
				t.consume();
			}
		});

	}

	private void handleKeyboard(SubScene scene, final Node root) {
		final boolean moveCamera = true;
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				Duration currentTime;
				switch (event.getCode()) {
				case Z:
					if (event.isShiftDown()) {
						cameraXform.ry.setAngle(0.0);
						cameraXform.rx.setAngle(0.0);
						camera.setTranslateZ(-1000.0);
					}
					cameraXform2.t.setX(0.0);
					cameraXform2.t.setY(0.0);
					break;
				case X:
					if (event.isControlDown()) {
						if (axisGroup.isVisible()) {
							axisGroup.setVisible(false);
						} else {
							axisGroup.setVisible(true);
						}
					}
					break;
				case S:
					if (event.isControlDown()) {
						if (moleculeGroup.isVisible()) {
							moleculeGroup.setVisible(false);
						} else {
							moleculeGroup.setVisible(true);
						}
					}
					break;
				case SPACE:
					if (timelinePlaying) {
						timeline.pause();
						timelinePlaying = false;
					} else {
						timeline.play();
						timelinePlaying = true;
					}
					break;
				case UP:
					if (event.isControlDown() && event.isShiftDown()) {
						cameraXform2.t.setY(cameraXform2.t.getY() - 10.0
								* CONTROL_MULTIPLIER);
					} else if (event.isAltDown() && event.isShiftDown()) {
						cameraXform.rx.setAngle(cameraXform.rx.getAngle()
								- 10.0 * ALT_MULTIPLIER);
					} else if (event.isControlDown()) {
						cameraXform2.t.setY(cameraXform2.t.getY() - 1.0
								* CONTROL_MULTIPLIER);
					} else if (event.isAltDown()) {
						cameraXform.rx.setAngle(cameraXform.rx.getAngle() - 2.0
								* ALT_MULTIPLIER);
					} else if (event.isShiftDown()) {
						double z = camera.getTranslateZ();
						double newZ = z + 5.0 * SHIFT_MULTIPLIER;
						camera.setTranslateZ(newZ);
					}
					break;
				case DOWN:
					if (event.isControlDown() && event.isShiftDown()) {
						cameraXform2.t.setY(cameraXform2.t.getY() + 10.0
								* CONTROL_MULTIPLIER);
					} else if (event.isAltDown() && event.isShiftDown()) {
						cameraXform.rx.setAngle(cameraXform.rx.getAngle()
								+ 10.0 * ALT_MULTIPLIER);
					} else if (event.isControlDown()) {
						cameraXform2.t.setY(cameraXform2.t.getY() + 1.0
								* CONTROL_MULTIPLIER);
					} else if (event.isAltDown()) {
						cameraXform.rx.setAngle(cameraXform.rx.getAngle() + 2.0
								* ALT_MULTIPLIER);
					} else if (event.isShiftDown()) {
						double z = camera.getTranslateZ();
						double newZ = z - 5.0 * SHIFT_MULTIPLIER;
						camera.setTranslateZ(newZ);
					}
					break;
				case RIGHT:
					if (event.isControlDown() && event.isShiftDown()) {
						cameraXform2.t.setX(cameraXform2.t.getX() + 10.0
								* CONTROL_MULTIPLIER);
					} else if (event.isAltDown() && event.isShiftDown()) {
						cameraXform.ry.setAngle(cameraXform.ry.getAngle()
								- 10.0 * ALT_MULTIPLIER);
					} else if (event.isControlDown()) {
						cameraXform2.t.setX(cameraXform2.t.getX() + 1.0
								* CONTROL_MULTIPLIER);
					} else if (event.isAltDown()) {
						cameraXform.ry.setAngle(cameraXform.ry.getAngle() - 2.0
								* ALT_MULTIPLIER);
					}
					break;
				case LEFT:
					if (event.isControlDown() && event.isShiftDown()) {
						cameraXform2.t.setX(cameraXform2.t.getX() - 10.0
								* CONTROL_MULTIPLIER);
					} else if (event.isAltDown() && event.isShiftDown()) {
						cameraXform.ry.setAngle(cameraXform.ry.getAngle()
								+ 10.0 * ALT_MULTIPLIER); // -
					} else if (event.isControlDown()) {
						cameraXform2.t.setX(cameraXform2.t.getX() - 1.0
								* CONTROL_MULTIPLIER);
					} else if (event.isAltDown()) {
						cameraXform.ry.setAngle(cameraXform.ry.getAngle() + 2.0
								* ALT_MULTIPLIER); // -
					}
					break;
				}
			}
		});
	}

	// @Override
	// public void start(Stage primaryStage) {
	//
	//
	// }

	/**
	 * The main() method is ignored in correctly deployed JavaFX application.
	 * main() serves only as fallback in case the application can not be
	 * launched through deployment artifacts, e.g., in IDEs with limited FX
	 * support. NetBeans ignores main().
	 *
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		System.setProperty("prism.dirtyopts", "false");

		JFrame frame = new JFrame();
		frame.setContentPane(new Jfx3dManager());

		frame.setSize(1024, 1024);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	public SubScene getSubScene() {
		return scene;
	}

	public void setSubScene(SubScene scene) {
		this.scene = scene;
	}

	public Group getRoot() {
		return root;
	}
	public Affine getCameraVR() {
		return cameraVR;
	}
	public void setCameraVR(Affine cameraVR) {
		this.cameraVR = cameraVR;
	}
	public void removeArm() {
		world.getChildren().remove(manipulator);
	}
}