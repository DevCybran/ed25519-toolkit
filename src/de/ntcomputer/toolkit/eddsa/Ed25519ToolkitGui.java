package de.ntcomputer.toolkit.eddsa;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;

import de.ntcomputer.toolkit.eddsa.Ed25519ToolkitModel.SignatureFilePair;

public class Ed25519ToolkitGui {
	private final Ed25519ToolkitModel model;
	private final Ed25519ToolkitController controller;
	private JFileChooser privateKeyChooser;
	private JFileChooser publicKeyChooser;
	private JFileChooser sourceFileChooser;
	private JFileChooser signatureFileChooser;
	private int guiLocks = 0;
	
	private JFrame frmEdToolkit;
	private JTextField tfSourceFile;
	private JTextField tfSignatureFile;
	private JLabel lblKeyLoadStatusDynamic;
	private JButton btnSavePrivateKey;
	private JButton btnSavePublicKey;
	private JLabel lblOperationStatusDynamic;
	private JButton btnSign;
	private JButton btnVerify;
	private JButton btnGenerateKeypair;
	private JButton btnLoadPrivateKey;
	private JButton btnLoadPublicKey;
	private JLabel lblKeySaveStatusDynamic;
	private JButton btnSourceFileSelect;
	private JButton btnSignatureFileSelect;

	/**
	 * Create the application.
	 */
	public Ed25519ToolkitGui(Ed25519ToolkitModel model, Ed25519ToolkitController controller) {
		this.model = model;
		this.controller = controller;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		frmEdToolkit = new JFrame();
		frmEdToolkit.setResizable(false);
		frmEdToolkit.setTitle("Ed25519 Toolkit");
		frmEdToolkit.setBounds(100, 100, 767, 385);
		frmEdToolkit.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmEdToolkit.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel mainPanel = new JPanel();
		frmEdToolkit.getContentPane().add(mainPanel);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		JPanel keyLoadPanel = new JPanel();
		mainPanel.add(keyLoadPanel);
		keyLoadPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Key loading", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		keyLoadPanel.setLayout(new BoxLayout(keyLoadPanel, BoxLayout.Y_AXIS));
		
		JPanel keyLoadButtonPanel = new JPanel();
		keyLoadPanel.add(keyLoadButtonPanel);
		keyLoadButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btnGenerateKeypair = new JButton("Generate keypair");
		btnGenerateKeypair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				generateKeypair();
			}
		});
		keyLoadButtonPanel.add(btnGenerateKeypair);
		
		btnLoadPrivateKey = new JButton("Load private key");
		btnLoadPrivateKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadPrivateKey();
			}
		});
		keyLoadButtonPanel.add(btnLoadPrivateKey);
		
		btnLoadPublicKey = new JButton("Load public key");
		btnLoadPublicKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadPublicKey();
			}
		});
		keyLoadButtonPanel.add(btnLoadPublicKey);
		
		Component verticalStrut_1 = Box.createVerticalStrut(10);
		keyLoadPanel.add(verticalStrut_1);
		
		JSeparator separator = new JSeparator();
		keyLoadPanel.add(separator);
		
		JPanel keyLoadStatusPanel = new JPanel();
		keyLoadPanel.add(keyLoadStatusPanel);
		
		JLabel lblKeyLoadStatus = new JLabel("Status: ");
		keyLoadStatusPanel.add(lblKeyLoadStatus);
		
		lblKeyLoadStatusDynamic = new JLabel("No key loaded.");
		keyLoadStatusPanel.add(lblKeyLoadStatusDynamic);
		
		JPanel keySavePanel = new JPanel();
		keySavePanel.setBorder(new TitledBorder(null, "Key saving", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		mainPanel.add(keySavePanel);
		keySavePanel.setLayout(new BoxLayout(keySavePanel, BoxLayout.Y_AXIS));
		
		JPanel keySaveButtonPanel = new JPanel();
		keySavePanel.add(keySaveButtonPanel);
		
		btnSavePrivateKey = new JButton("Save private key");
		btnSavePrivateKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				savePrivateKey();
			}
		});
		keySaveButtonPanel.add(btnSavePrivateKey);
		btnSavePrivateKey.setEnabled(false);
		
		btnSavePublicKey = new JButton("Save public key");
		btnSavePublicKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				savePublicKey();
			}
		});
		keySaveButtonPanel.add(btnSavePublicKey);
		btnSavePublicKey.setEnabled(false);
		
		Component verticalStrut_2 = Box.createVerticalStrut(10);
		keySavePanel.add(verticalStrut_2);
		
		JSeparator separator_2 = new JSeparator();
		keySavePanel.add(separator_2);
		
		JPanel keySaveStatusPanel = new JPanel();
		keySavePanel.add(keySaveStatusPanel);
		
		JLabel lblKeySaveStatus = new JLabel("Status:");
		keySaveStatusPanel.add(lblKeySaveStatus);
		
		lblKeySaveStatusDynamic = new JLabel("idle");
		keySaveStatusPanel.add(lblKeySaveStatusDynamic);
		
		JPanel operationPanel = new JPanel();
		mainPanel.add(operationPanel);
		operationPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Sign / Verify", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		operationPanel.setLayout(new BoxLayout(operationPanel, BoxLayout.Y_AXIS));
		
		JPanel operationPanelNested = new JPanel();
		operationPanel.add(operationPanelNested);
		operationPanelNested.setLayout(new BoxLayout(operationPanelNested, BoxLayout.Y_AXIS));
		
		JPanel operationFilePanel = new JPanel();
		operationPanelNested.add(operationFilePanel);
		GridBagLayout gbl_operationFilePanel = new GridBagLayout();
		gbl_operationFilePanel.columnWidths = new int[] {30, 80, 200, 80, 50};
		gbl_operationFilePanel.rowHeights = new int[] {25, 25};
		gbl_operationFilePanel.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0};
		gbl_operationFilePanel.rowWeights = new double[]{1.0, 0.0};
		operationFilePanel.setLayout(gbl_operationFilePanel);
		
		JLabel lblSourceFile = new JLabel("Source file:");
		GridBagConstraints gbc_lblSourceFile = new GridBagConstraints();
		gbc_lblSourceFile.insets = new Insets(0, 0, 5, 5);
		gbc_lblSourceFile.gridx = 1;
		gbc_lblSourceFile.gridy = 0;
		operationFilePanel.add(lblSourceFile, gbc_lblSourceFile);
		
		tfSourceFile = new JTextField();
		tfSourceFile.setEditable(false);
		GridBagConstraints gbc_tfSourceFile = new GridBagConstraints();
		gbc_tfSourceFile.insets = new Insets(0, 0, 5, 5);
		gbc_tfSourceFile.gridx = 2;
		gbc_tfSourceFile.gridy = 0;
		operationFilePanel.add(tfSourceFile, gbc_tfSourceFile);
		tfSourceFile.setColumns(40);
		
		btnSourceFileSelect = new JButton("select...");
		btnSourceFileSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectSourceFile();
			}
		});
		GridBagConstraints gbc_btnSourceFileSelect = new GridBagConstraints();
		gbc_btnSourceFileSelect.insets = new Insets(0, 0, 5, 0);
		gbc_btnSourceFileSelect.gridx = 3;
		gbc_btnSourceFileSelect.gridy = 0;
		operationFilePanel.add(btnSourceFileSelect, gbc_btnSourceFileSelect);
		
		JLabel lblSignatureFile = new JLabel("Signature file:");
		GridBagConstraints gbc_lblSignatureFile = new GridBagConstraints();
		gbc_lblSignatureFile.insets = new Insets(0, 0, 0, 5);
		gbc_lblSignatureFile.gridx = 1;
		gbc_lblSignatureFile.gridy = 1;
		operationFilePanel.add(lblSignatureFile, gbc_lblSignatureFile);
		
		tfSignatureFile = new JTextField();
		tfSignatureFile.setEditable(false);
		GridBagConstraints gbc_tfSignatureFile = new GridBagConstraints();
		gbc_tfSignatureFile.insets = new Insets(0, 0, 0, 5);
		gbc_tfSignatureFile.gridx = 2;
		gbc_tfSignatureFile.gridy = 1;
		operationFilePanel.add(tfSignatureFile, gbc_tfSignatureFile);
		tfSignatureFile.setColumns(40);
		
		btnSignatureFileSelect = new JButton("select...");
		btnSignatureFileSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectSignatureFile();
			}
		});
		GridBagConstraints gbc_btnSignatureFileSelect = new GridBagConstraints();
		gbc_btnSignatureFileSelect.gridx = 3;
		gbc_btnSignatureFileSelect.gridy = 1;
		operationFilePanel.add(btnSignatureFileSelect, gbc_btnSignatureFileSelect);
		
		JPanel operationButtonPanel = new JPanel();
		operationPanelNested.add(operationButtonPanel);
		
		btnSign = new JButton("Sign");
		btnSign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sign();
			}
		});
		btnSign.setEnabled(false);
		operationButtonPanel.add(btnSign);
		
		btnVerify = new JButton("Verify");
		btnVerify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				verify();
			}
		});
		btnVerify.setEnabled(false);
		operationButtonPanel.add(btnVerify);
		
		Component verticalStrut = Box.createVerticalStrut(10);
		operationPanelNested.add(verticalStrut);
		
		JSeparator separator_1 = new JSeparator();
		operationPanelNested.add(separator_1);
		
		JPanel operationStatusPanel = new JPanel();
		operationPanelNested.add(operationStatusPanel);
		
		JLabel lblOperationStatus = new JLabel("Status: ");
		operationStatusPanel.add(lblOperationStatus);
		
		lblOperationStatusDynamic = new JLabel("idle");
		operationStatusPanel.add(lblOperationStatusDynamic);
		
		sourceFileChooser = new JFileChooser();
		signatureFileChooser = new JFileChooser();
		publicKeyChooser = new JFileChooser();
		privateKeyChooser = new JFileChooser();
		
		frmEdToolkit.pack();
		frmEdToolkit.setLocationRelativeTo(null);
		frmEdToolkit.setVisible(true);
	}
	
	private void lockKeyLoading() {
		if(this.guiLocks++ == 0) {
			btnGenerateKeypair.setEnabled(false);
			btnLoadPrivateKey.setEnabled(false);
			btnLoadPublicKey.setEnabled(false);
		}
	}
	
	private void unlockKeyLoading() {
		if(--this.guiLocks == 0) {
			btnGenerateKeypair.setEnabled(true);
			btnLoadPrivateKey.setEnabled(true);
			btnLoadPublicKey.setEnabled(true);
			switch(model.getKeyOrigin()) {
			case GENERATED_KEYPAIR:
				lblKeyLoadStatusDynamic.setText("Keypair generated.");
				break;
			case LOADED_PRIVATE_KEY:
				lblKeyLoadStatusDynamic.setText("Private key loaded.");
				break;
			case LOADED_PUBLIC_KEY:
				lblKeyLoadStatusDynamic.setText("Public key loaded.");
				break;
			default:
				lblKeyLoadStatusDynamic.setText("No key loaded.");
				break;
			}
		}
	}
	
	private void lockKeySaving() {
		this.lockKeyLoading();
		btnSavePrivateKey.setEnabled(false);
		btnSavePublicKey.setEnabled(false);
	}
	
	private void unlockKeySaving() {
		if(this.model.hasPrivateKey()) btnSavePrivateKey.setEnabled(true);
		if(this.model.hasPublicKey()) btnSavePublicKey.setEnabled(true);
		lblKeySaveStatusDynamic.setText("idle");
		this.unlockKeyLoading();
	}
	
	private void lockOperations() {
		this.lockKeyLoading();
		btnSourceFileSelect.setEnabled(false);
		btnSignatureFileSelect.setEnabled(false);
		btnSign.setEnabled(false);
		btnVerify.setEnabled(false);
	}
	
	private void unlockOperations() {
		btnSourceFileSelect.setEnabled(true);
		btnSignatureFileSelect.setEnabled(true);
		if(this.model.hasSignatureFiles()) {
			if(this.model.hasPrivateKey()) btnSign.setEnabled(true);
			if(this.model.hasPublicKey()) btnVerify.setEnabled(true);
		}
		lblOperationStatusDynamic.setText("idle");
		this.unlockKeyLoading();
	}
	
	private void lockEverything() {
		this.lockKeyLoading();
		this.lockKeySaving();
		this.lockOperations();
	}
	
	private void unlockEverything() {
		this.unlockOperations();
		this.unlockKeySaving();
		this.unlockKeyLoading();
	}
	
	private <T> TaskListener<T> tl(SuccessListener<T> sl, ErrorListener el) {
		return new TaskListener<T>() {
			@Override
			public void onSuccess(T result) {
				EventQueue.invokeLater(() -> {
					sl.onSuccess(result);
				});
			}
			@Override
			public void onError(Exception e) {
				EventQueue.invokeLater(() -> {
					el.onError(e);
				});
			}
		};
	}
	
	private void generateKeypair() {
		this.lockEverything();
		lblKeyLoadStatusDynamic.setText("Generating keypair, please wait...");
		this.controller.generateKeypair(tl((Void) -> {
			unlockEverything();
		}, (Exception e) -> {
			unlockEverything();
			JOptionPane.showMessageDialog(frmEdToolkit, "Error generating keypair: "+e.getMessage(), "Key generation failed", JOptionPane.ERROR_MESSAGE);
		}));
	}
	
	private void loadPrivateKey() {
		this.lockEverything();
		lblKeyLoadStatusDynamic.setText("Loading private key...");
		int returnVal = privateKeyChooser.showOpenDialog(frmEdToolkit);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = privateKeyChooser.getSelectedFile();
            char[] password = PasswordChooser.open(false);
            if(password!=null) {
            	lblKeyLoadStatusDynamic.setText("Loading private key, please wait...");
            	this.controller.loadPrivateKey(file, password, tl((Void) -> {
        			unlockEverything();
        		}, (Exception e) -> {
        			unlockEverything();
        			if(e instanceof FileNotFoundException || e instanceof NoSuchFileException) {
        				JOptionPane.showMessageDialog(frmEdToolkit, "Error loading private key: File '"+e.getMessage()+"' does not exist.", "Key loading failed", JOptionPane.ERROR_MESSAGE);
        			} else {
        				JOptionPane.showMessageDialog(frmEdToolkit, "Error loading private key: "+e.getMessage(), "Key loading failed", JOptionPane.ERROR_MESSAGE);
        			}
        		}));
	            return;
            }
		}
		this.unlockEverything();
	}
	
	private void loadPublicKey() {
		this.lockEverything();
		lblKeyLoadStatusDynamic.setText("Loading public key...");
		int returnVal = publicKeyChooser.showOpenDialog(frmEdToolkit);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = publicKeyChooser.getSelectedFile();
            lblKeyLoadStatusDynamic.setText("Loading public key, please wait...");
        	this.controller.loadPublicKey(file, tl((Void) -> {
    			unlockEverything();
    		}, (Exception e) -> {
    			unlockEverything();
    			if(e instanceof FileNotFoundException || e instanceof NoSuchFileException) {
    				JOptionPane.showMessageDialog(frmEdToolkit, "Error loading public key: File '"+e.getMessage()+"' does not exist.", "Key loading failed", JOptionPane.ERROR_MESSAGE);
    			} else {
    				JOptionPane.showMessageDialog(frmEdToolkit, "Error loading public key: "+e.getMessage(), "Key loading failed", JOptionPane.ERROR_MESSAGE);
    			}
    		}));
        	return;
		}
		this.unlockEverything();
	}
	
	private void savePrivateKey() {
		this.lockKeySaving();
		lblKeySaveStatusDynamic.setText("Saving private key...");
		int returnVal = privateKeyChooser.showSaveDialog(frmEdToolkit);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = privateKeyChooser.getSelectedFile();
            boolean confirmed = true;
            if(file.isFile()) {
            	int dialogResult = JOptionPane.showConfirmDialog (frmEdToolkit, "Are you sure you want to overwrite the existing file '"+file.getAbsolutePath()+"'?", "Overwrite file", JOptionPane.YES_OPTION | JOptionPane.CANCEL_OPTION);
            	if(dialogResult!=JOptionPane.YES_OPTION) confirmed = false;
            }
            if(confirmed) {
	            char[] password = PasswordChooser.open(true);
	            if(password!=null) {
	            	lblKeySaveStatusDynamic.setText("Saving private key, please wait...");
	            	this.controller.savePrivateKey(file, password, tl((Void) -> {
	            		unlockKeySaving();
	            		JOptionPane.showMessageDialog(frmEdToolkit, "Private key successfully saved!", "Key saved", JOptionPane.INFORMATION_MESSAGE);
	        		}, (Exception e) -> {
	        			unlockKeySaving();
	        			JOptionPane.showMessageDialog(frmEdToolkit, "Error saving private key: "+e.getMessage(), "Key saving failed", JOptionPane.ERROR_MESSAGE);
	        		}));
		            return;
	            }
            }
		}
		this.unlockKeySaving();
		lblKeySaveStatusDynamic.setText("Saving private key aborted.");
	}
	
	private void savePublicKey() {
		this.lockKeySaving();
		lblKeySaveStatusDynamic.setText("Saving public key...");
		int returnVal = publicKeyChooser.showSaveDialog(frmEdToolkit);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = publicKeyChooser.getSelectedFile();
            boolean confirmed = true;
            if(file.isFile()) {
            	int dialogResult = JOptionPane.showConfirmDialog (frmEdToolkit, "Are you sure you want to overwrite the existing file '"+file.getAbsolutePath()+"'?", "Overwrite file", JOptionPane.YES_OPTION | JOptionPane.CANCEL_OPTION);
            	if(dialogResult!=JOptionPane.YES_OPTION) confirmed = false;
            }
            if(confirmed) {
            	lblKeySaveStatusDynamic.setText("Saving public key, please wait...");
            	this.controller.savePublicKey(file, tl((Void) -> {
            		unlockKeySaving();
            		JOptionPane.showMessageDialog(frmEdToolkit, "Public key successfully saved!", "Key saved", JOptionPane.INFORMATION_MESSAGE);
        		}, (Exception e) -> {
        			unlockKeySaving();
        			JOptionPane.showMessageDialog(frmEdToolkit, "Error saving public key: "+e.getMessage(), "Key saving failed", JOptionPane.ERROR_MESSAGE);
        		}));
	            return;
            }
		}
		this.unlockKeySaving();
		lblKeySaveStatusDynamic.setText("Saving public key aborted.");
	}
	
	private void selectSourceFile() {
		this.lockOperations();
		int returnVal = sourceFileChooser.showOpenDialog(frmEdToolkit);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File sourceFile = sourceFileChooser.getSelectedFile();
            File signatureFile = new File(sourceFile.getParent(), sourceFile.getName()+".sig");
            tfSourceFile.setText(sourceFile.getAbsolutePath());
            tfSignatureFile.setText(signatureFile.getAbsolutePath());
            model.setSignatureFiles(sourceFile, signatureFile);
		}
		this.unlockOperations();
	}
	
	private void selectSignatureFile() {
		this.lockOperations();
		int returnVal = signatureFileChooser.showSaveDialog(frmEdToolkit);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File signatureFile = signatureFileChooser.getSelectedFile();
            tfSignatureFile.setText(signatureFile.getAbsolutePath());
            model.setSignatureFile(signatureFile);
		}
		this.unlockOperations();
	}
	
	private void sign() {
		this.lockOperations();
		lblOperationStatusDynamic.setText("Signing file...");
        SignatureFilePair signatureFiles = model.getSignatureFiles();
        boolean confirmed = true;
        if(signatureFiles.getSignatureFile().isFile()) {
        	int dialogResult = JOptionPane.showConfirmDialog (frmEdToolkit, "Are you sure you want to overwrite the existing file '"+signatureFiles.getSignatureFile().getAbsolutePath()+"'?", "Overwrite file", JOptionPane.YES_OPTION | JOptionPane.CANCEL_OPTION);
        	if(dialogResult!=JOptionPane.YES_OPTION) confirmed = false;
        }
        if(confirmed) {
        	lblOperationStatusDynamic.setText("Signing file, please wait...");
        	this.controller.sign(tl((Void) -> {
        		unlockOperations();
        		JOptionPane.showMessageDialog(frmEdToolkit, "Signature file successfully created and saved!", "Signed", JOptionPane.INFORMATION_MESSAGE);
    		}, (Exception e) -> {
    			unlockOperations();
    			if(e instanceof FileNotFoundException || e instanceof NoSuchFileException) {
    				JOptionPane.showMessageDialog(frmEdToolkit, "Error creating signature file: File '"+e.getMessage()+"' does not exist.", "Signing failed", JOptionPane.ERROR_MESSAGE);
    			} else {
    				JOptionPane.showMessageDialog(frmEdToolkit, "Error creating signature file: "+e.getMessage(), "Signing failed", JOptionPane.ERROR_MESSAGE);
    			}
    		}));
            return;
        }
		this.unlockOperations();
		lblOperationStatusDynamic.setText("Signing file aborted.");
	}
	
	private void verify() {
		this.lockOperations();
    	lblOperationStatusDynamic.setText("Verifying file signature, please wait...");
    	this.controller.verify(tl((Boolean result) -> {
    		unlockOperations();
    		if(result) JOptionPane.showMessageDialog(frmEdToolkit, "Signature file successfully verified! The signature is valid!", "Signature verified: valid", JOptionPane.INFORMATION_MESSAGE);
    		else JOptionPane.showMessageDialog(frmEdToolkit, "Signature file verified, the signature is INVALID!", "Signature verified: invalid", JOptionPane.WARNING_MESSAGE);
		}, (Exception e) -> {
			unlockOperations();
			if(e instanceof FileNotFoundException || e instanceof NoSuchFileException) {
				JOptionPane.showMessageDialog(frmEdToolkit, "Error verifying signature file: File '"+e.getMessage()+"' does not exist.", "Signature verification failed", JOptionPane.ERROR_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(frmEdToolkit, "Error verifying signature file: "+e.getMessage(), "Signature verification failed", JOptionPane.ERROR_MESSAGE);
			}
		}));
	}

}
