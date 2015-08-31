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
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;

import de.ntcomputer.crypto.hash.ProgressListener;
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
	private JLabel lblFileSignatureStatusDynamic;
	private JButton btnSignFile;
	private JButton btnVerifyFile;
	private JButton btnGenerateKeypair;
	private JButton btnLoadPrivateKey;
	private JButton btnLoadPublicKey;
	private JLabel lblKeySaveStatusDynamic;
	private JButton btnSourceFileSelect;
	private JButton btnSignatureFileSelect;
	private JTextArea stringTextArea;
	private JTextArea signatureStringTextArea;
	private JButton btnSignString;
	private JButton btnVerifyString;
	private JLabel lblStringSignatureStatusDynamic;

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
		
		JPanel fileSignaturePanel = new JPanel();
		mainPanel.add(fileSignaturePanel);
		fileSignaturePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Sign / Verify file", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		fileSignaturePanel.setLayout(new BoxLayout(fileSignaturePanel, BoxLayout.Y_AXIS));
		
		JPanel fileSignatureSelectPanel = new JPanel();
		fileSignaturePanel.add(fileSignatureSelectPanel);
		GridBagLayout gbl_fileSignatureSelectPanel = new GridBagLayout();
		gbl_fileSignatureSelectPanel.columnWidths = new int[] {30, 80, 400, 80, 30};
		gbl_fileSignatureSelectPanel.rowHeights = new int[] {25, 25};
		gbl_fileSignatureSelectPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0};
		gbl_fileSignatureSelectPanel.rowWeights = new double[]{0.0, 0.0};
		fileSignatureSelectPanel.setLayout(gbl_fileSignatureSelectPanel);
		
		JLabel lblSourceFile = new JLabel("Source file:");
		GridBagConstraints gbc_lblSourceFile = new GridBagConstraints();
		gbc_lblSourceFile.insets = new Insets(0, 0, 5, 5);
		gbc_lblSourceFile.gridx = 1;
		gbc_lblSourceFile.gridy = 0;
		fileSignatureSelectPanel.add(lblSourceFile, gbc_lblSourceFile);
		
		tfSourceFile = new JTextField();
		tfSourceFile.setEditable(false);
		GridBagConstraints gbc_tfSourceFile = new GridBagConstraints();
		gbc_tfSourceFile.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfSourceFile.insets = new Insets(0, 0, 5, 5);
		gbc_tfSourceFile.gridx = 2;
		gbc_tfSourceFile.gridy = 0;
		fileSignatureSelectPanel.add(tfSourceFile, gbc_tfSourceFile);
		
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
		fileSignatureSelectPanel.add(btnSourceFileSelect, gbc_btnSourceFileSelect);
		
		JLabel lblSignatureFile = new JLabel("Signature file:");
		GridBagConstraints gbc_lblSignatureFile = new GridBagConstraints();
		gbc_lblSignatureFile.insets = new Insets(0, 0, 0, 5);
		gbc_lblSignatureFile.gridx = 1;
		gbc_lblSignatureFile.gridy = 1;
		fileSignatureSelectPanel.add(lblSignatureFile, gbc_lblSignatureFile);
		
		tfSignatureFile = new JTextField();
		tfSignatureFile.setEditable(false);
		GridBagConstraints gbc_tfSignatureFile = new GridBagConstraints();
		gbc_tfSignatureFile.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfSignatureFile.insets = new Insets(0, 0, 0, 5);
		gbc_tfSignatureFile.gridx = 2;
		gbc_tfSignatureFile.gridy = 1;
		fileSignatureSelectPanel.add(tfSignatureFile, gbc_tfSignatureFile);
		
		btnSignatureFileSelect = new JButton("select...");
		btnSignatureFileSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectSignatureFile();
			}
		});
		GridBagConstraints gbc_btnSignatureFileSelect = new GridBagConstraints();
		gbc_btnSignatureFileSelect.gridx = 3;
		gbc_btnSignatureFileSelect.gridy = 1;
		fileSignatureSelectPanel.add(btnSignatureFileSelect, gbc_btnSignatureFileSelect);
		
		JPanel fileSignatureButtonPanel = new JPanel();
		fileSignaturePanel.add(fileSignatureButtonPanel);
		
		btnSignFile = new JButton("Sign");
		btnSignFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				signFile();
			}
		});
		btnSignFile.setEnabled(false);
		fileSignatureButtonPanel.add(btnSignFile);
		
		btnVerifyFile = new JButton("Verify");
		btnVerifyFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				verifyFile();
			}
		});
		btnVerifyFile.setEnabled(false);
		fileSignatureButtonPanel.add(btnVerifyFile);
		
		Component verticalStrut = Box.createVerticalStrut(10);
		fileSignaturePanel.add(verticalStrut);
		
		JSeparator separator_1 = new JSeparator();
		fileSignaturePanel.add(separator_1);
		
		JPanel fileSignatureStatusPanel = new JPanel();
		fileSignaturePanel.add(fileSignatureStatusPanel);
		
		JLabel lblFileSignatureStatus = new JLabel("Status: ");
		fileSignatureStatusPanel.add(lblFileSignatureStatus);
		
		lblFileSignatureStatusDynamic = new JLabel("idle");
		fileSignatureStatusPanel.add(lblFileSignatureStatusDynamic);
		
		JPanel stringSignaturePanel = new JPanel();
		stringSignaturePanel.setBorder(new TitledBorder(null, "Sign / Verify string", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		mainPanel.add(stringSignaturePanel);
		stringSignaturePanel.setLayout(new BoxLayout(stringSignaturePanel, BoxLayout.Y_AXIS));
		
		JPanel stringSignatureFieldPanel = new JPanel();
		stringSignaturePanel.add(stringSignatureFieldPanel);
		GridBagLayout gbl_stringSignatureFieldPanel = new GridBagLayout();
		gbl_stringSignatureFieldPanel.columnWidths = new int[] {30, 80, 280, 30};
		gbl_stringSignatureFieldPanel.rowHeights = new int[] {25, 50, 25, 50};
		gbl_stringSignatureFieldPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 1.0};
		gbl_stringSignatureFieldPanel.rowWeights = new double[]{1.0, 1.0, 0.0, 0.0};
		stringSignatureFieldPanel.setLayout(gbl_stringSignatureFieldPanel);
		
		JLabel lblString = new JLabel("String:");
		GridBagConstraints gbc_lblString = new GridBagConstraints();
		gbc_lblString.insets = new Insets(0, 0, 5, 5);
		gbc_lblString.gridx = 1;
		gbc_lblString.gridy = 0;
		stringSignatureFieldPanel.add(lblString, gbc_lblString);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 2;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 2;
		gbc_scrollPane.gridy = 0;
		stringSignatureFieldPanel.add(scrollPane, gbc_scrollPane);
		
		stringTextArea = new JTextArea();
		scrollPane.setViewportView(stringTextArea);
		stringTextArea.setLineWrap(true);
		
		JLabel lblSignatureString = new JLabel("Signature string:");
		GridBagConstraints gbc_lblSignatureString = new GridBagConstraints();
		gbc_lblSignatureString.insets = new Insets(0, 0, 5, 5);
		gbc_lblSignatureString.gridx = 1;
		gbc_lblSignatureString.gridy = 2;
		stringSignatureFieldPanel.add(lblSignatureString, gbc_lblSignatureString);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.gridheight = 2;
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 2;
		gbc_scrollPane_1.gridy = 2;
		stringSignatureFieldPanel.add(scrollPane_1, gbc_scrollPane_1);
		
		signatureStringTextArea = new JTextArea();
		scrollPane_1.setViewportView(signatureStringTextArea);
		signatureStringTextArea.setLineWrap(true);
		
		JPanel stringSignatureButtonPanel = new JPanel();
		stringSignaturePanel.add(stringSignatureButtonPanel);
		
		btnSignString = new JButton("Sign");
		btnSignString.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				signString();
			}
		});
		btnSignString.setEnabled(false);
		stringSignatureButtonPanel.add(btnSignString);
		
		btnVerifyString = new JButton("Verify");
		btnVerifyString.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				verifyString();
			}
		});
		btnVerifyString.setEnabled(false);
		stringSignatureButtonPanel.add(btnVerifyString);
		
		Component verticalStrut_3 = Box.createVerticalStrut(10);
		stringSignaturePanel.add(verticalStrut_3);
		
		JSeparator separator_3 = new JSeparator();
		stringSignaturePanel.add(separator_3);
		
		JPanel stringSignatureStatusPanel = new JPanel();
		stringSignaturePanel.add(stringSignatureStatusPanel);
		
		JLabel lblStringSignatureStatus = new JLabel("Status: ");
		stringSignatureStatusPanel.add(lblStringSignatureStatus);
		
		lblStringSignatureStatusDynamic = new JLabel("idle");
		stringSignatureStatusPanel.add(lblStringSignatureStatusDynamic);
		
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
	
	private void lockFileSignatures() {
		this.lockKeyLoading();
		btnSourceFileSelect.setEnabled(false);
		btnSignatureFileSelect.setEnabled(false);
		btnSignFile.setEnabled(false);
		btnVerifyFile.setEnabled(false);
	}
	
	private void unlockFileSignatures() {
		btnSourceFileSelect.setEnabled(true);
		btnSignatureFileSelect.setEnabled(true);
		if(this.model.hasSignatureFiles()) {
			if(this.model.hasPrivateKey()) btnSignFile.setEnabled(true);
			if(this.model.hasPublicKey()) btnVerifyFile.setEnabled(true);
		}
		lblFileSignatureStatusDynamic.setText("idle");
		this.unlockKeyLoading();
	}
	
	private void lockStringSignatures() {
		this.lockKeyLoading();
		stringTextArea.setEnabled(false);
		signatureStringTextArea.setEnabled(false);
		btnSignString.setEnabled(false);
		btnVerifyString.setEnabled(false);
	}
	
	private void unlockStringSignatures() {
		stringTextArea.setEnabled(true);
		signatureStringTextArea.setEnabled(true);
		if(this.model.hasPrivateKey()) btnSignString.setEnabled(true);
		if(!signatureStringTextArea.getText().isEmpty()) {
			if(this.model.hasPublicKey()) btnVerifyString.setEnabled(true);
		}
		lblStringSignatureStatusDynamic.setText("idle");
		this.unlockKeyLoading();
	}
	
	private void lockEverything() {
		this.lockKeyLoading();
		this.lockKeySaving();
		this.lockFileSignatures();
		this.lockStringSignatures();
	}
	
	private void unlockEverything() {
		this.unlockFileSignatures();
		this.unlockKeySaving();
		this.unlockKeyLoading();
		this.unlockStringSignatures();
	}
	
	private <T> TaskListener<T> tl(SuccessListener<T> sl, ErrorListener el, ProgressListener pl) {
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
			@Override
			public void onProgress(long progress, long limit) {
				EventQueue.invokeLater(() -> {
					pl.onProgress(progress, limit);
				});
			}
		};
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
			@Override
			public void onProgress(long progress, long limit) {
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
		this.lockFileSignatures();
		int returnVal = sourceFileChooser.showOpenDialog(frmEdToolkit);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File sourceFile = sourceFileChooser.getSelectedFile();
            File signatureFile = new File(sourceFile.getParent(), sourceFile.getName()+".sig");
            tfSourceFile.setText(sourceFile.getAbsolutePath());
            tfSignatureFile.setText(signatureFile.getAbsolutePath());
            model.setSignatureFiles(sourceFile, signatureFile);
		}
		this.unlockFileSignatures();
	}
	
	private void selectSignatureFile() {
		this.lockFileSignatures();
		int returnVal = signatureFileChooser.showSaveDialog(frmEdToolkit);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File signatureFile = signatureFileChooser.getSelectedFile();
            tfSignatureFile.setText(signatureFile.getAbsolutePath());
            model.setSignatureFile(signatureFile);
		}
		this.unlockFileSignatures();
	}
	
	private void signFile() {
		this.lockFileSignatures();
		lblFileSignatureStatusDynamic.setText("Signing file...");
        SignatureFilePair signatureFiles = model.getSignatureFiles();
        boolean confirmed = true;
        if(signatureFiles.getSignatureFile().isFile()) {
        	int dialogResult = JOptionPane.showConfirmDialog (frmEdToolkit, "Are you sure you want to overwrite the existing file '"+signatureFiles.getSignatureFile().getAbsolutePath()+"'?", "Overwrite file", JOptionPane.YES_OPTION | JOptionPane.CANCEL_OPTION);
        	if(dialogResult!=JOptionPane.YES_OPTION) confirmed = false;
        }
        if(confirmed) {
        	lblFileSignatureStatusDynamic.setText("Signing file, please wait...");
        	this.controller.signFile(tl((Void) -> {
        		unlockFileSignatures();
        		JOptionPane.showMessageDialog(frmEdToolkit, "Signature file successfully created and saved!", "Signed", JOptionPane.INFORMATION_MESSAGE);
    		}, (Exception e) -> {
    			unlockFileSignatures();
    			if(e instanceof FileNotFoundException || e instanceof NoSuchFileException) {
    				JOptionPane.showMessageDialog(frmEdToolkit, "Error creating signature file: File '"+e.getMessage()+"' does not exist.", "Signing failed", JOptionPane.ERROR_MESSAGE);
    			} else {
    				JOptionPane.showMessageDialog(frmEdToolkit, "Error creating signature file: "+e.getMessage(), "Signing failed", JOptionPane.ERROR_MESSAGE);
    			}
    		}, (long progress, long limit) -> {
    			lblFileSignatureStatusDynamic.setText("Signing file, please wait... (" + (Math.round(1000.0D*progress/limit)/10.0D) + "%)");
    		}));
            return;
        }
		this.unlockFileSignatures();
		lblFileSignatureStatusDynamic.setText("Signing file aborted.");
	}
	
	private void verifyFile() {
		this.lockFileSignatures();
    	lblFileSignatureStatusDynamic.setText("Verifying signature file, please wait...");
    	this.controller.verifyFile(tl((Boolean result) -> {
    		unlockFileSignatures();
    		if(result) JOptionPane.showMessageDialog(frmEdToolkit, "Signature file successfully verified! The signature is valid!", "Signature verified: valid", JOptionPane.INFORMATION_MESSAGE);
    		else JOptionPane.showMessageDialog(frmEdToolkit, "Signature file verified, the signature is INVALID!", "Signature verified: invalid", JOptionPane.WARNING_MESSAGE);
		}, (Exception e) -> {
			unlockFileSignatures();
			if(e instanceof FileNotFoundException || e instanceof NoSuchFileException) {
				JOptionPane.showMessageDialog(frmEdToolkit, "Error verifying signature file: File '"+e.getMessage()+"' does not exist.", "Signature verification failed", JOptionPane.ERROR_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(frmEdToolkit, "Error verifying signature file: "+e.getMessage(), "Signature verification failed", JOptionPane.ERROR_MESSAGE);
			}
		}, (long progress, long limit) -> {
			lblFileSignatureStatusDynamic.setText("Verifying signature file, please wait... (" + (Math.round(1000.0D*progress/limit)/10.0D) + "%)");
		}));
	}
	
	private void signString() {
		this.lockStringSignatures();
		lblStringSignatureStatusDynamic.setText("Signing string, please wait...");
    	this.controller.signString(stringTextArea.getText(), tl((String result) -> {
    		signatureStringTextArea.setText(result);
    		unlockStringSignatures();
    		JOptionPane.showMessageDialog(frmEdToolkit, "Signature string successfully created!", "Signed", JOptionPane.INFORMATION_MESSAGE);
		}, (Exception e) -> {
			unlockStringSignatures();
			JOptionPane.showMessageDialog(frmEdToolkit, "Error creating signature string: "+e.getMessage(), "Signing failed", JOptionPane.ERROR_MESSAGE);
		}));
	}
	
	private void verifyString() {
		this.lockStringSignatures();
		lblStringSignatureStatusDynamic.setText("Verifying signature string, please wait...");
    	this.controller.verifyString(stringTextArea.getText(), signatureStringTextArea.getText(), tl((Boolean result) -> {
    		unlockStringSignatures();
    		if(result) JOptionPane.showMessageDialog(frmEdToolkit, "Signature string successfully verified! The signature is valid!", "Signature verified: valid", JOptionPane.INFORMATION_MESSAGE);
    		else JOptionPane.showMessageDialog(frmEdToolkit, "Signature string verified, the signature is INVALID!", "Signature verified: invalid", JOptionPane.WARNING_MESSAGE);
		}, (Exception e) -> {
			unlockStringSignatures();
			JOptionPane.showMessageDialog(frmEdToolkit, "Error verifying signature string: "+e.getMessage(), "Signature verification failed", JOptionPane.ERROR_MESSAGE);
		}));
	}

}
