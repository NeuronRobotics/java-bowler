package com.neuronrobotics.sdk.addons.kinematics.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import net.miginfocom.swing.MigLayout;

import Jama.Matrix;

import com.neuronrobotics.sdk.addons.kinematics.AbstractKinematics;
import com.neuronrobotics.sdk.addons.kinematics.IRegistrationListener;
import com.neuronrobotics.sdk.addons.kinematics.math.Transform;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
//import com.neuronrobotics.sdk.pid.IPIDControl;
//import com.neuronrobotics.sdk.ui.ConnectionImageIconFactory;


public class SampleGui extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8543020276155087015L;
	private AbstractKinematics model;

	private JButton homeAll;
	private JButton estop = new JButton("E-Stop");
	private JPanel links = new JPanel(new MigLayout());
	private ArrayList<SampleGuiAxisWidget> axis = new ArrayList<SampleGuiAxisWidget>();
	private MatrixDisplay ras;
	private MatrixDisplay robReg;
	private PosePanel setPanel;
	public SampleGui(){
		setLayout(new MigLayout());
	}
	public void setKinematicsModel( AbstractKinematics model ){
		setModel(model);
		populate();
	}
	
	private void populate() {		
		removeAll();
		setHomeAll(new JButton("Home All"));
		getHomeAll().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread(){
					public void run(){
						getModel().homeAllLinks();
						getHomeAll().setText("Homed");
						setButtonEnabled(true);
					}
				}.start();
				getHomeAll().setText("Homing...");
				setButtonEnabled(false);
			}
		});
		estop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				model.emergencyStop();
			}
		});
		
		for(int i=0;i<model.getNumberOfLinks();i++){
			SampleGuiAxisWidget w = new SampleGuiAxisWidget(i,model);
			links.add(w,"wrap");
			axis.add(w);
		}
		
		setRas(new MatrixDisplay("Global to Base"));
		getRas().setEditable(true);
		getRas().setTransform(getModel().getFiducialToGlobalTransform());
		getRas().addTableModelListener(new TableModelListener() {
				public void tableChanged(TableModelEvent e) {
					Transform m = new Transform( new Matrix(getRas().getTableData()));
					System.out.println("Setting ras transform: "+m);
					getModel().setGlobalToFiducialTransform(m);
				}
		});
		
		getModel().addRegistrationListener(new IRegistrationListener() {
			public void onFiducialToGlobalUpdate(AbstractKinematics source,Transform registration) {
				getRas().setTransform(registration);
			}
			public void onBaseToFiducialUpdate(AbstractKinematics source,Transform registration) {}
		});
		
		setRobReg(new MatrixDisplay("Base to Robot"));
		getRobReg().setEditable(true);
		getRobReg().setTransform(getModel().getRobotToFiducialTransform());
		getModel().addRegistrationListener(new IRegistrationListener() {
			public void onFiducialToGlobalUpdate(AbstractKinematics source,Transform registration) {}
			public void onBaseToFiducialUpdate(AbstractKinematics source,Transform registration) {
				getRobReg().setTransform(registration);
			}
		});
		
		JPanel poses = new JPanel(new MigLayout());
		JPanel registration = new JPanel(new MigLayout());
		registration.add(new JLabel("Robot Registration"),"wrap");
		registration.add(getRas(),"wrap");
		registration.add(getRobReg(),"wrap");
		poses.add(registration);
		poses.add(new PosePanel(getModel(),true,true,"Current Actual Pose"));
		poses.add(new PosePanel(getModel(),false,true,"Current Target Pose"));
		setPanel = new PosePanel(getModel(),false, false,"New Target Pose");
		poses.add(setPanel,"wrap");
		
		JPanel buttons = new JPanel(new MigLayout());
		buttons.add(estop);
		buttons.add(getHomeAll());
		add(poses,"wrap");
		add(buttons,"wrap");
		add(links,"wrap");
		
	}
	
	private void setButtonEnabled(boolean b){
		setPanel.setButtonEnabled(b);
		for(SampleGuiAxisWidget w:axis){
			w.setButtonEnabled(b);
		}
		getHomeAll().setEnabled(b);
	}
	

	public void setModel(AbstractKinematics model2) {
		this.model = model2;
	}
	public AbstractKinematics getModel() {
		return model;
	}
	public void setHomeAll(JButton homeAll) {
		this.homeAll = homeAll;
	}
	public JButton getHomeAll() {
		return homeAll;
	}

	public MatrixDisplay getRas() {
		return ras;
	}

	public void setRas(MatrixDisplay ras) {
		this.ras = ras;
	}

	public MatrixDisplay getRobReg() {
		return robReg;
	}

	public void setRobReg(MatrixDisplay robReg) {
		this.robReg = robReg;
	}

}
