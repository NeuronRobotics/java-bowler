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

import com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR;
import com.neuronrobotics.sdk.addons.kinematics.IRegistrationListenerNR;
import com.neuronrobotics.sdk.addons.kinematics.ITaskSpaceUpdateListenerNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.common.Log;

import net.miginfocom.swing.MigLayout;

public class PosePanelNR extends JPanel implements ITaskSpaceUpdateListenerNR, IRegistrationListenerNR {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7089803986840024349L;
	private JButton set = new JButton("Set");
	private JCheckBox updateTarget = new JCheckBox("Update Target");
	private JTextField time = new JTextField(5);
	private AbstractKinematicsNR model;
	private boolean input;
	private boolean displayOnly;
	private MatrixDisplayNR matrix;
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
	
	public void setModel(AbstractKinematicsNR model, boolean usePoseListener) {
		this.model = model;
		this.model.addPoseUpdateListener(this);	
		this.model.addRegistrationListener(this);
	}
	public AbstractKinematicsNR getModel() {
		return model;
	}
	@Override
	public void onTargetTaskSpaceUpdate(AbstractKinematicsNR source, TransformNR pose) {
		if((!input) && (displayOnly || updateTarget.isSelected()) ){
			
			matrix.setTransform(pose);	
		}
	}
	@Override
	public void onTaskSpaceUpdate(AbstractKinematicsNR source, TransformNR pose) {
		if(input && displayOnly ){
			matrix.setTransform(pose);
		}
	}

	public void setButtonEnabled(boolean b){
		set.setEnabled(b);
	}
	@Override
	public void onBaseToFiducialUpdate(AbstractKinematicsNR source,TransformNR regestration) {
		if(displayOnly)
			matrix.setTransform(source.getCurrentTaskSpaceTransform());
	}
	@Override
	public void onFiducialToGlobalUpdate(AbstractKinematicsNR source,TransformNR regestration) {
		if(displayOnly)
			matrix.setTransform(source.getCurrentTaskSpaceTransform());
	}

}
