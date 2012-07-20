package LaundryMate.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JComboBox;

import LaundryMate.serial.*;
import LaundryMate.mail.*;
import LaundryMate.gui.*;

public class SettingsDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private final JDialog thisDialog = this;
	private final Action action = new SwingAction();
	private final Action action_1 = new SwingAction_1();
	private final Action saveAction = new SaveAction();
	
	private Settings settings;
	
	private JCheckBox chckbxWasher;
	private JCheckBox chckbxDryerComplete;
	private JTextArea washerMessageTextField;
	private JTextArea dryerMessageTextField;
	private JTextField emailTextField;
	private JTextArea textArea;
	private JComboBox comboBox_com;
	private JComboBox comboBox_washer_delay;
	private JComboBox comboBox_dryer_delay;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Settings s = new Settings(null,null,new SerialReader(null),new JFrame());
			s.addEmail( "you@me.com" );
			s.addEmail( "foobar@me.com" );
			s.setWasherMessage("custom msg");
			s.toggleDryerNotify();
			
			SettingsDialog dialog = new SettingsDialog( s, new ArrayList() );
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public SettingsDialog( Settings s, ArrayList<String> commNames ) {
		
		settings = s;
		setBounds(100, 100, 757, 418);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblEmail = new JLabel("Email");
		lblEmail.setBounds(12, 12, 70, 15);
		contentPanel.add(lblEmail);
		
		emailTextField = new JTextField();
		emailTextField.addActionListener( new AddEmailAction() );
		emailTextField.setBounds(69, 10, 178, 19);
		
		contentPanel.add(emailTextField);
		emailTextField.setColumns(10);
				
		JLabel lblSendMessageWhen = new JLabel("Send message when");
		lblSendMessageWhen.setBounds(297, 71, 166, 15);
		contentPanel.add(lblSendMessageWhen);
		
		JLabel lblMessage = new JLabel("Washer Complete Message");
		lblMessage.setBounds(12, 171, 235, 15);
		contentPanel.add(lblMessage);
		
		JButton resetButton = new JButton("Reset");
		resetButton.setAction(action);
		resetButton.setBounds(534, 7, 117, 25);
		contentPanel.add(resetButton);
		
		washerMessageTextField = new JTextArea();
		washerMessageTextField.setBounds(22, 193, 225, 81);
		washerMessageTextField.setText( settings.getWasherMessage() );
		washerMessageTextField.setEditable( settings.getWasherNotify() );
		washerMessageTextField.setBackground( ( settings.getWasherNotify() ? Color.WHITE : Color.LIGHT_GRAY ) );
		contentPanel.add(washerMessageTextField);
		
		dryerMessageTextField = new JTextArea();
		dryerMessageTextField.setText( settings.getDryerMessage() );
		dryerMessageTextField.setEditable( settings.getDryerNotify() );
		dryerMessageTextField.setBounds(310, 193, 218, 81);
		dryerMessageTextField.setBackground( ( settings.getDryerNotify() ? Color.WHITE : Color.LIGHT_GRAY ) );
		contentPanel.add(dryerMessageTextField);
		
		JLabel lblDryerCompleteMessage = new JLabel("Dryer Complete Message");
		lblDryerCompleteMessage.setBounds(300, 171, 235, 15);
		contentPanel.add(lblDryerCompleteMessage);
		
		textArea = new JTextArea();
		textArea.setBackground(Color.LIGHT_GRAY);
		textArea.setEditable(false);
		textArea.setText( settings.getEmail().replace(',','\n') );
		textArea.setBounds(22, 39, 235, 95);
		contentPanel.add(textArea);
		
		chckbxWasher = new JCheckBox("Washer complete");
		chckbxWasher.addChangeListener( new MyChangeListener(washerMessageTextField) );
		chckbxWasher.setSelected( settings.getWasherNotify() );
		
		chckbxWasher.setBounds(322, 94, 160, 23);
		contentPanel.add(chckbxWasher);
		
		chckbxDryerComplete = new JCheckBox("Dryer Complete");
		chckbxDryerComplete.addChangeListener( new MyChangeListener(dryerMessageTextField) );
		chckbxDryerComplete.setBounds(322, 123, 160, 23);
		chckbxDryerComplete.setSelected( settings.getDryerNotify() );
		contentPanel.add(chckbxDryerComplete);
		
		JButton btnClear = new JButton("Clear");
		btnClear.setBounds(176, 134, 81, 25);
		btnClear.addActionListener( new ClearEmailListAction() );
		contentPanel.add(btnClear);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.setBounds(248, 7, 70, 25);
		btnAdd.addActionListener( new AddEmailAction() );
		contentPanel.add(btnAdd);
		
		
		comboBox_com = new JComboBox( commNames.toArray() );
		
		int idx = commNames.indexOf( settings.getComIdentifier() );
		if ( idx != -1 )
		{
			comboBox_com.setSelectedIndex(idx);
		}
		comboBox_com.setBounds(544, 90, 154, 30);
		
		contentPanel.add(comboBox_com);
		
		JLabel lblComPort = new JLabel("Selected COM Port");
		lblComPort.setBounds(534, 71, 166, 15);
		contentPanel.add(lblComPort);
		
		JLabel lblCycleendDelay = new JLabel("Washer Cycle-End Delay");
		lblCycleendDelay.setBounds(532, 166, 211, 15);
		contentPanel.add(lblCycleendDelay);
		
		String[] times = { "0", "1","3","4","5","8","10" };
		
		comboBox_washer_delay = new JComboBox(times);
		comboBox_washer_delay.setSelectedItem( Integer.toString( settings.getWasherDelayInMin() ) );
		comboBox_washer_delay.setBounds(544, 188, 154, 30);
		
		contentPanel.add(comboBox_washer_delay);
		
		JLabel lblDryerCycleendDelay = new JLabel("Dryer Cycle-End Delay");
		lblDryerCycleendDelay.setBounds(534, 240, 211, 15);
		contentPanel.add(lblDryerCycleendDelay);
		
		comboBox_dryer_delay = new JComboBox(times);
		comboBox_dryer_delay.setSelectedItem( Integer.toString( settings.getDryerDelayInMin() ) );
		comboBox_dryer_delay.setBounds(546, 262, 154, 30);
		contentPanel.add(comboBox_dryer_delay);
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton saveButton = new JButton("Save");
				saveButton.setActionCommand("OK");
				saveButton.setAction(saveAction);
				buttonPane.add(saveButton);
			}
			{
				JButton cancelButton = new JButton("Close");
				cancelButton.setAction(action_1);
				getRootPane().setDefaultButton(cancelButton);
				buttonPane.add(cancelButton);
			}
		}
		
		setResizable(false);
	}
	
	private class MyChangeListener implements ChangeListener
	{
		private JTextArea textAreaToModify;
		public MyChangeListener( JTextArea t )
		{
			textAreaToModify = t;
		}
		public void stateChanged(ChangeEvent e) {
			AbstractButton b = (AbstractButton) e.getSource();
			ButtonModel bModel = b.getModel();
			if ( bModel.isSelected() )
			{
				textAreaToModify.setEditable(true);
				textAreaToModify.setBackground(Color.WHITE);
			}
			else
			{
				textAreaToModify.setEditable(false);
				textAreaToModify.setBackground(Color.LIGHT_GRAY);
			}
		}
		
	}
	private class ClearEmailListAction extends AbstractAction {
		public void actionPerformed( ActionEvent e )
		{
			textArea.setText("");
		}
	}
	private class AddEmailAction extends AbstractAction {
	
		public void actionPerformed( ActionEvent e ) {
			if ( textArea.getText().isEmpty() )
			{
				textArea.setText( emailTextField.getText() );
			}
			else
			{
				textArea.setText( textArea.getText() + "\n" + emailTextField.getText() );
			}
			emailTextField.setText("");
		}
	}
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "Reset");
			putValue(SHORT_DESCRIPTION, "Reset settings to Default");
		}
		public void actionPerformed(ActionEvent e) {
			washerMessageTextField.setText( Settings.DEFAULT_MESSAGE_WASHER);
			dryerMessageTextField.setText(Settings.DEFAULT_MESSAGE_DRYER);
			textArea.setText("");
			emailTextField.setText("");
			chckbxWasher.setSelected(true);
			chckbxDryerComplete.setSelected(true);
		}
	}
	private class SwingAction_1 extends AbstractAction {
		public SwingAction_1() {
			putValue(NAME, "Close");
			putValue(SHORT_DESCRIPTION, "Close");
		}
		public void actionPerformed(ActionEvent e) {
			thisDialog.dispose();
		}
	}
	private class SaveAction extends AbstractAction {
		public SaveAction() {
			putValue(NAME, "Save");
			putValue(SHORT_DESCRIPTION, "Save settings");
		}
		public void actionPerformed(ActionEvent e) {
			settings.setDryerNotify( chckbxDryerComplete.isSelected() );
			settings.setWasherNotify( chckbxWasher.isSelected() );
			
			settings.setDryerMessage( dryerMessageTextField.getText() );
			settings.setWasherMessage( washerMessageTextField.getText() );
			
			settings.setEmail( textArea.getText().replace('\n',',') );
			
			if ( comboBox_com.getSelectedIndex() >=0 )
			{
				settings.setComIdentifier( (String)comboBox_com.getSelectedItem() );
			} else 
			{ 
				settings.setComIdentifier("");
			}
			
			settings.setWasherDelayInMin( Integer.parseInt( (String) comboBox_washer_delay.getSelectedItem() ) );
			settings.setDryerDelayInMin(  Integer.parseInt( (String) comboBox_dryer_delay.getSelectedItem() ) );
			
			
			try {
				File f = new File("settings.settings");
				PrintWriter fout = new PrintWriter( new FileOutputStream(f) );
				//fout.println( settings.getDryerMessage() );
				
				System.err.println("INFO: supposed to save to file here. Not implemented");
			} catch (FileNotFoundException e1) {
				System.err.println("WARNING: could not write to settings file");
				e1.printStackTrace();
			}
		}
	}
}
