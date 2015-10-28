package com.neuronrobotics.sdk.addons.kinematics.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javafx.embed.swing.JFXPanel;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import net.miginfocom.swing.MigLayout;
import Jama.Matrix;

import com.neuronrobotics.sdk.addons.kinematics.AbstractKinematicsNR;
import com.neuronrobotics.sdk.addons.kinematics.IRegistrationListenerNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
// TODO: Auto-generated Javadoc
//import com.neuronrobotics.sdk.pid.IPIDControl;
//import com.neuronrobotics.sdk.ui.ConnectionImageIconFactory;


/**
 * The Class SampleGuiNR.
 */
public class SampleGuiNR extends JPanel{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8543020276155087015L;
	
	/** The model. */
	private AbstractKinematicsNR model;

	/** The home all. */
	private JButton homeAll;
	
	/** The estop. */
	private JButton estop = new JButton("E-Stop");
	
	/** The links. */
	private JPanel links = new JPanel(new MigLayout());
	
	/** The axis. */
	private ArrayList<SampleGuiAxisWidgetNR> axis = new ArrayList<SampleGuiAxisWidgetNR>();
	
	/** The ras. */
	private MatrixDisplayNR ras;
	
	/** The rob reg. */
	private MatrixDisplayNR robReg;
	
	/** The set panel. */
	private PosePanelNR setPanel;
	
	/** The extra. */
	private JPanel extra = new JPanel();
	
	/**
	 * Instantiates a new sample gui nr.
	 */
	public SampleGuiNR(){
		setLayout(new MigLayout());
	}
	
	/**
	 * Sets the kinematics model.
	 *
	 * @param model the new kinematics model
	 */
	public void setKinematicsModel( AbstractKinematicsNR model ){
		setModel(model);
		populate();
	}
	
	/**
	 * Populate.
	 */
	private void populate() {		
		removeAll();
		
		setHomeAll(new JButton("Home All"));
		getHomeAll().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread(){
					public void run(){
						setName("Bowler Platform Homing thread");
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
			SampleGuiAxisWidgetNR w = new SampleGuiAxisWidgetNR(i,model);
			links.add(w,"wrap");
			axis.add(w);
		}
		
		setRas(new MatrixDisplayNR("Global to Base"));
		getRas().setEditable(true);
		getRas().setTransform(getModel().getFiducialToGlobalTransform());
		getRas().addTableModelListener(new TableModelListener() {
				public void tableChanged(TableModelEvent e) {
					TransformNR m = new TransformNR( new Matrix(getRas().getTableData()));
					System.out.println("Setting ras transform: "+m);
					getModel().setGlobalToFiducialTransform(m);
				}
		});
		
		getModel().addRegistrationListener(new IRegistrationListenerNR() {
			public void onFiducialToGlobalUpdate(AbstractKinematicsNR source,TransformNR registration) {
				getRas().setTransform(registration);
			}
			public void onBaseToFiducialUpdate(AbstractKinematicsNR source,TransformNR registration) {}
		});
		
		setRobReg(new MatrixDisplayNR("Base to Robot"));
		getRobReg().setEditable(true);
		getRobReg().setTransform(getModel().getRobotToFiducialTransform());
		getModel().addRegistrationListener(new IRegistrationListenerNR() {
			public void onFiducialToGlobalUpdate(AbstractKinematicsNR source,TransformNR registration) {}
			public void onBaseToFiducialUpdate(AbstractKinematicsNR source,TransformNR registration) {
				getRobReg().setTransform(registration);
			}
		});
		
		JPanel poses = new JPanel(new MigLayout());
		JPanel registration = new JPanel(new MigLayout());
		registration.add(new JLabel("Robot Registration"),"wrap");
		registration.add(getRas(),"wrap");
		registration.add(getRobReg(),"wrap");
		poses.add(registration);
		poses.add(new PosePanelNR(getModel(),true,true,"Current Actual Pose"));
		poses.add(new PosePanelNR(getModel(),false,true,"Current Target Pose"));
		setPanel = new PosePanelNR(getModel(),false, false,"New Target Pose");
		poses.add(setPanel,"wrap");
		
		JPanel buttons = new JPanel(new MigLayout());
		buttons.add(estop);
		buttons.add(getHomeAll());
		
		JPanel standard = new JPanel(new MigLayout());
		standard.add(poses,"wrap");
		standard.add(buttons,"wrap");
		standard.add(links,"wrap");
		
		add(extra,"wrap");
		add(standard,"wrap");
		
	}
	
	/**
	 * Adds the extra panel.
	 *
	 * @param jp the jp
	 */
	public void addExtraPanel(JFXPanel jp){
		extra.add(jp);
	}
	
	/**
	 * Sets the button enabled.
	 *
	 * @param b the new button enabled
	 */
	private void setButtonEnabled(boolean b){
		setPanel.setButtonEnabled(b);
		for(SampleGuiAxisWidgetNR w:axis){
			w.setButtonEnabled(b);
		}
		getHomeAll().setEnabled(b);
	}
	

	/**
	 * Sets the model.
	 *
	 * @param model2 the new model
	 */
	public void setModel(AbstractKinematicsNR model2) {
		this.model = model2;
	}
	
	/**
	 * Gets the model.
	 *
	 * @return the model
	 */
	public AbstractKinematicsNR getModel() {
		return model;
	}
	
	/**
	 * Sets the home all.
	 *
	 * @param homeAll the new home all
	 */
	public void setHomeAll(JButton homeAll) {
		this.homeAll = homeAll;
	}
	
	/**
	 * Gets the home all.
	 *
	 * @return the home all
	 */
	public JButton getHomeAll() {
		return homeAll;
	}

	/**
	 * Gets the ras.
	 *
	 * @return the ras
	 */
	public MatrixDisplayNR getRas() {
		return ras;
	}

	/**
	 * Sets the ras.
	 *
	 * @param ras the new ras
	 */
	public void setRas(MatrixDisplayNR ras) {
		this.ras = ras;
	}

	/**
	 * Gets the rob reg.
	 *
	 * @return the rob reg
	 */
	public MatrixDisplayNR getRobReg() {
		return robReg;
	}

	/**
	 * Sets the rob reg.
	 *
	 * @param robReg the new rob reg
	 */
	public void setRobReg(MatrixDisplayNR robReg) {
		this.robReg = robReg;
	}

}
