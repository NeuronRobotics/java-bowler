package com.neuronrobotics.sdk.addons.kinematics.gui;


import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;
import Jama.Matrix;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

// TODO: Auto-generated Javadoc
/**
 * The Class MatrixDisplayNR.
 */
public class MatrixDisplayNR extends JPanel {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 9213263491347912407L;
	
	/** The model. */
	private MyDefaultTableModel model = new MyDefaultTableModel();
	
	/** The table. */
	private JTable table = new JTable(model);
	
	/** The listeners. */
	private ArrayList<TableModelListener> listeners = new ArrayList<TableModelListener>();
	
	/**
	 * Instantiates a new matrix display nr.
	 *
	 * @param name the name
	 */
	public MatrixDisplayNR(String name){
		setLayout(new MigLayout());
		add(new JLabel(name),"wrap");		
		getTable().setBorder(BorderFactory.createLoweredBevelBorder());		
		setBorder(BorderFactory.createLoweredBevelBorder());		
		// Disable auto resizing
		getTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		// Set the first visible column to 100 pixels wide
//		int vColIndex = 0;
//		TableColumn col = getTable().getColumnModel().getColumn(vColIndex);
//		int width = 40;
//		col.setPreferredWidth(width);	
		int colWidth = 66;
		getTable().getColumnModel().getColumn(0).setPreferredWidth(colWidth);
		getTable().getColumnModel().getColumn(1).setPreferredWidth(colWidth);
		getTable().getColumnModel().getColumn(2).setPreferredWidth(colWidth);
		getTable().getColumnModel().getColumn(3).setPreferredWidth(colWidth);

		add(getTable(),"wrap");
		setEditable(false);
	}

	/**
	 * Sets the transform.
	 *
	 * @param m the new transform
	 */
	public void setTransform(TransformNR m){
		getTable().setEnabled(false);
		for(TableModelListener l:listeners){
			getTable().getModel().removeTableModelListener(l);
		}
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				getTable().setValueAt(m.getMatrixTransform().get(i, j),i,j);
			}
		}
		for(TableModelListener l:listeners){
			getTable().getModel().addTableModelListener(l);
		}
		getTable().setEnabled(true);
		//System.out.println("Matrix display setting data "+m);
	}
	
	/**
	 * Gets the table data.
	 *
	 * @return the table data
	 */
	public double[][] getTableData() {
		double[][] data = new double[4][4];

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				String current = this.getTable().getValueAt(i, j).toString();
				data[i][j] = Double.parseDouble( current);
			}
		}
		return data;
	}
	
	/**
	 * Gets the table data matrix.
	 *
	 * @return the table data matrix
	 */
	public Matrix getTableDataMatrix() {
		return new Matrix(getTableData());
	}
	

	/**
	 * Gets the table.
	 *
	 * @return the table
	 */
	public JTable getTable() {
		return table;
	}
	
	/**
	 * Adds the table model listener.
	 *
	 * @param l the l
	 */
	public void addTableModelListener(TableModelListener l){
		listeners.add(l);
		getTable().getModel().addTableModelListener(l);
	}

	/**
	 * Sets the editable.
	 *
	 * @param b the new editable
	 */
	public void setEditable(boolean b) {
		model.setEditable(b);
	}

	/**
	 * The Class MyDefaultTableModel.
	 */
	private class MyDefaultTableModel extends DefaultTableModel { 
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 7096254212840475488L;
		
		/** The edit. */
		private boolean edit=false;
		
		/** The data. */
		private Object[][] data = {	{1,0,0,0},
									{0,1,0,0},
									{0,0,1,0},
									{0,0,0,1},
		};
		 
 		/**
 		 * Instantiates a new my default table model.
 		 */
 		public MyDefaultTableModel() {  
		   super(4,4);  
		   
		 }  
		 
 		/* (non-Javadoc)
 		 * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
 		 */
 		public boolean isCellEditable(int row, int col) {  
		   return edit;  
		 } 
		
		/**
		 * Sets the editable.
		 *
		 * @param b the new editable
		 */
		public void setEditable(boolean b){
			 edit=b;
		 }
        
        /* (non-Javadoc)
         * @see javax.swing.table.DefaultTableModel#getValueAt(int, int)
         */
        public Object getValueAt(int row, int col) {
            return data[row][col];
        }
        
        /* (non-Javadoc)
         * @see javax.swing.table.DefaultTableModel#setValueAt(java.lang.Object, int, int)
         */
        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
        public void setValueAt(Object value, int row, int col) {
        	if(col == 3 && row < 3){
        		data[row][col] = new DecimalFormat( "000.000" ).format(Double.parseDouble(value.toString()));	
        	} else if(row == 3){
        		data[row][col] = new DecimalFormat( "0" ).format(Double.parseDouble(value.toString()));	
        	}        		
        	else{
        		data[row][col] = new DecimalFormat( "0.000" ).format(Double.parseDouble(value.toString()));
        	}        		
            fireTableCellUpdated(row, col);
        }
	} 
}
