package com.neuronrobotics.sdk.addons.kinematics.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR;
import com.neuronrobotics.sdk.addons.kinematics.IRegistrationListenerNR;
import com.neuronrobotics.sdk.addons.kinematics.ITaskSpaceUpdateListenerNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.common.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class PosePanelNR.
 */
public class PosePanelNR extends JPanel implements ITaskSpaceUpdateListenerNR, IRegistrationListenerNR {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7089803986840024349L;
	
	/** The set. */
	private JButton set = new JButton("Set");
	
	/** The update target. */
	private JCheckBox updateTarget = new JCheckBox("Update Target");
	
	/** The time. */
	private JTextField time = new JTextField(5);
	
	/** The model. */
	private AbstractKinematicsNR model;
	
	/** The input. */
	private boolean input;
	
	/** The display only. */
	private boolean displayOnly;
	
	/** The matrix. */
	private MatrixDisplayNR matrix;
	
	/**
	 * Instantiates a new pose panel nr.
	 *
	 * @param m the m
	 * @param isSetTargetinput the is set targetinput
	 * @param displayOnly the display only
	 * @param text the text
	 */
	public PosePanelNR(AbstractKinematicsNR m, boolean isSetTargetinput, boolean displayOnly, String text) {
		setModel(m,isSetTargetinput);
		//pose = getModel().getCurrentPose();
		this.input = isSetTargetinput;
		this.displayOnly=displayOnly;
		setLayout(new MigLayout());
		setBorder(BorderFactory.createLoweredBevelBorder());
		JPanel control = new JPanel(new MigLayout());
		matrix = new MatrixDisplayNR(text);
		
		if(isSetTargetinput || displayOnly){
			//add(new JLabel(text),"wrap");	
			control.add(new JLabel(""));	
			time.setVisible(false);
			set.setVisible(false);
			updateTarget.setVisible(false);
			matrix.setEditable(false);
		}
		else{
			//add(new JLabel(text),"wrap");
			control.add(new JLabel("Transition Seconds"));	
			matrix.setEditable(true);
		}
		if(displayOnly)
			matrix.setTransform(m.getCurrentTaskSpaceTransform());
		else
			matrix.setTransform(new TransformNR());

		control.add(time, "wrap");
		control.add(set,"wrap");
		updateTarget.setSelected(true);
		control.add(updateTarget,"wrap");
		time.setText("5.0");
		set.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setPose();
			}
		});
		
		
		add(control,"wrap");
		add(matrix);

	}
	
	/**
	 * Sets the pose.
	 */
	public void setPose(){
		if(!input){
			double t = Double.parseDouble(time.getText());
			TransformNR target = new TransformNR( matrix.getTableDataMatrix());
			try {
				Log.info("GUI Seting pose :"+target);
				getModel().setDesiredTaskSpaceTransform(target, t);
			} catch (Exception e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(new JFrame(),e1.getMessage(), "Range warning",JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	
	/**
	 * Sets the model.
	 *
	 * @param model the model
	 * @param usePoseListener the use pose listener
	 */
	private void setModel(AbstractKinematicsNR model, boolean usePoseListener) {
		this.model = model;
		this.model.addPoseUpdateListener(this);	
		this.model.addRegistrationListener(this);
	}
	
	/**
	 * Gets the model.
	 *
	 * @return the model
	 */
	public AbstractKinematicsNR getModel() {
		return model;
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.ITaskSpaceUpdateListenerNR#onTargetTaskSpaceUpdate(com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR, com.neuronrobotics.sdk.addons.kinematics.math.TransformNR)
	 */
	@Override
	public void onTargetTaskSpaceUpdate(AbstractKinematicsNR source, TransformNR pose) {
		if((!input) && (displayOnly || updateTarget.isSelected()) ){
			
			matrix.setTransform(pose);	
		}
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.ITaskSpaceUpdateListenerNR#onTaskSpaceUpdate(com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR, com.neuronrobotics.sdk.addons.kinematics.math.TransformNR)
	 */
	@Override
	public void onTaskSpaceUpdate(AbstractKinematicsNR source, TransformNR pose) {
		if (matrix == null)
			return;
		if(input && displayOnly ){
			matrix.setTransform(pose);
		}
	}

	/**
	 * Sets the button enabled.
	 *
	 * @param b the new button enabled
	 */
	public void setButtonEnabled(boolean b){
		set.setEnabled(b);
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.IRegistrationListenerNR#onBaseToFiducialUpdate(com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR, com.neuronrobotics.sdk.addons.kinematics.math.TransformNR)
	 */
	@Override
	public void onBaseToFiducialUpdate(AbstractKinematicsNR source,TransformNR regestration) {
		if(displayOnly)
			matrix.setTransform(source.getCurrentTaskSpaceTransform());
	}
	
	/* (non-Javadoc)
	 * @see com.neuronrobotics.sdk.addons.kinematics.IRegistrationListenerNR#onFiducialToGlobalUpdate(com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR, com.neuronrobotics.sdk.addons.kinematics.math.TransformNR)
	 */
	@Override
	public void onFiducialToGlobalUpdate(AbstractKinematicsNR source,TransformNR regestration) {
		if(displayOnly)
			matrix.setTransform(source.getCurrentTaskSpaceTransform());
	}

}
