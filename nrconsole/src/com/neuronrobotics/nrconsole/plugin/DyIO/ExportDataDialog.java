package com.neuronrobotics.nrconsole.plugin.DyIO;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class ExportDataDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private NRConsoleDyIOPlugin console;

	public ExportDataDialog(NRConsoleDyIOPlugin nrConsoleDyIOPlugin) {
		this.console = nrConsoleDyIOPlugin;
		
		JButton excelBtn = new JButton("Excel");
		excelBtn.addActionListener(this);
		
		JPanel panel = new JPanel(new MigLayout());
		panel.add(new JLabel("Export data as:"));
		panel.add(excelBtn);
		
		add(panel);
		
		setModal(true);
		setTitle("Export Data");
		setSize(new Dimension(300, 200));
		setResizable(false);
		
	}
	
	public void showDialog() {
		setLocationRelativeTo(null);
		setVisible(true);
	}

	
	public void actionPerformed(ActionEvent e) {
		console.recordData();
	}
}
