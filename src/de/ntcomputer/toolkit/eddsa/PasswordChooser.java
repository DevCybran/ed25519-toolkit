package de.ntcomputer.toolkit.eddsa;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PasswordChooser {
	private final boolean confirmPassword;
	private final JDialog dialog;
	private final JPanel contentPanel = new JPanel();
	private JPasswordField passwordField;
	private JPasswordField passwordConfirmField;
	private JButton okButton;
	private boolean isOK = false;
	private final DocumentListener passwordChangeListener = new DocumentListener() {
		@Override
		public void removeUpdate(DocumentEvent e) {
			validatePasswords();
		}
		@Override
		public void insertUpdate(DocumentEvent e) {
			validatePasswords();
		}
		@Override
		public void changedUpdate(DocumentEvent e) {
			validatePasswords();
		}
	};

	/**
	 * Create the dialog.
	 */
	public PasswordChooser(boolean confirmPassword) {
		this.confirmPassword = confirmPassword;
		this.dialog = new JDialog();
		dialog.setModal(true);
		dialog.setTitle("Password");
		dialog.setBounds(100, 100, 450, 300);
		dialog.getContentPane().setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		dialog.getContentPane().add(contentPanel);
		contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		{
			JPanel passwordPanel = new JPanel();
			contentPanel.add(passwordPanel);
			GridBagLayout gbl_passwordPanel = new GridBagLayout();
			gbl_passwordPanel.columnWidths = new int[] {20, 80};
			gbl_passwordPanel.rowHeights = new int[] {25, 25};
			gbl_passwordPanel.columnWeights = new double[]{0.0, 0.0};
			gbl_passwordPanel.rowWeights = new double[]{0.0, 0.0};
			passwordPanel.setLayout(gbl_passwordPanel);
			{
				JLabel lblPassword = new JLabel("Enter password:");
				GridBagConstraints gbc_lblPassword = new GridBagConstraints();
				gbc_lblPassword.anchor = GridBagConstraints.WEST;
				gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
				gbc_lblPassword.gridx = 0;
				gbc_lblPassword.gridy = 0;
				passwordPanel.add(lblPassword, gbc_lblPassword);
			}
			{
				passwordField = new JPasswordField();
				passwordField.getDocument().addDocumentListener(passwordChangeListener);
				GridBagConstraints gbc_passwordField = new GridBagConstraints();
				gbc_passwordField.anchor = GridBagConstraints.NORTHWEST;
				gbc_passwordField.insets = new Insets(0, 0, 5, 0);
				gbc_passwordField.gridx = 1;
				gbc_passwordField.gridy = 0;
				passwordPanel.add(passwordField, gbc_passwordField);
				passwordField.setColumns(40);
			}
			if(confirmPassword) {
				{
					JLabel lblConfirmPassword = new JLabel("Confirm password:");
					GridBagConstraints gbc_lblConfirmPassword = new GridBagConstraints();
					gbc_lblConfirmPassword.insets = new Insets(0, 0, 5, 5);
					gbc_lblConfirmPassword.anchor = GridBagConstraints.WEST;
					gbc_lblConfirmPassword.gridx = 0;
					gbc_lblConfirmPassword.gridy = 1;
					passwordPanel.add(lblConfirmPassword, gbc_lblConfirmPassword);
				}
				{
					passwordConfirmField = new JPasswordField();
					passwordConfirmField.getDocument().addDocumentListener(passwordChangeListener);
					GridBagConstraints gbc_passwordConfirmField = new GridBagConstraints();
					gbc_passwordConfirmField.insets = new Insets(0, 0, 5, 0);
					gbc_passwordConfirmField.anchor = GridBagConstraints.NORTHWEST;
					gbc_passwordConfirmField.gridx = 1;
					gbc_passwordConfirmField.gridy = 1;
					passwordPanel.add(passwordConfirmField, gbc_passwordConfirmField);
					passwordConfirmField.setColumns(40);
				}
			}
		}
		{
			JSeparator separator = new JSeparator();
			dialog.getContentPane().add(separator);
		}
		{
			JPanel buttonPane = new JPanel();
			FlowLayout fl_buttonPane = new FlowLayout(FlowLayout.RIGHT);
			fl_buttonPane.setHgap(10);
			buttonPane.setLayout(fl_buttonPane);
			dialog.getContentPane().add(buttonPane);
			{
				okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						isOK = true;
						dialog.setVisible(false);
					}
				});
				okButton.setEnabled(false);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				dialog.getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						isOK = false;
						dialog.setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		dialog.pack();
		
	}
	
	private void validatePasswords() {
		if(this.confirmPassword) {
			if(Arrays.equals(this.passwordField.getPassword(), this.passwordConfirmField.getPassword())) {
				this.okButton.setEnabled(true);
			} else {
				this.okButton.setEnabled(false);
			}
		}
	}
	
	public static char[] open(boolean confirmPassword) {
		PasswordChooser chooser = new PasswordChooser(confirmPassword);
		if(confirmPassword) chooser.okButton.setEnabled(false);
		else chooser.okButton.setEnabled(true);
		chooser.passwordField.requestFocusInWindow();
		chooser.dialog.setLocationRelativeTo(null);
		chooser.dialog.setVisible(true);
		char[] result;
		if(chooser.isOK) {
			result = chooser.passwordField.getPassword();
		} else {
			result = null;
		}
		chooser.passwordField.setText("");
		if(confirmPassword) chooser.passwordConfirmField.setText("");
		chooser.dialog.dispose();
		return result;
	}

}
