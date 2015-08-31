package de.ntcomputer.toolkit.eddsa;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.Callable;

import de.ntcomputer.crypto.eddsa.Ed25519PrivateKey;
import de.ntcomputer.crypto.eddsa.Ed25519PublicKey;
import de.ntcomputer.toolkit.eddsa.Ed25519ToolkitModel.KeyOrigin;
import de.ntcomputer.toolkit.eddsa.Ed25519ToolkitModel.SignatureFilePair;

public class Ed25519ToolkitController {
	private final Ed25519ToolkitModel model;
	
	public Ed25519ToolkitController(Ed25519ToolkitModel model) {
		this.model = model;
	}
	
	private <T> void executeTask(Callable<T> task, TaskListener<T> listener) {
		new Thread(() -> {
			T result;
			try {
				result = task.call();
			} catch(Exception e) {
				e.printStackTrace();
				listener.onError(e);
				return;
			}
			listener.onSuccess(result);
		}).start();
	}
	
	public void generateKeypair(TaskListener<Void> listener) {
		this.model.resetKeys();
		this.executeTask(() -> {
			Ed25519PrivateKey privateKey = Ed25519PrivateKey.generate();
			Ed25519PublicKey publicKey = privateKey.derivePublicKey();
			model.setPrivateKey(KeyOrigin.GENERATED_KEYPAIR, privateKey, publicKey);
			return null;
		}, listener);
	}
	
	public void loadPrivateKey(File file, char[] password, TaskListener<Void> listener) {
		this.model.resetKeys();
		this.executeTask(() -> {
			Ed25519PrivateKey privateKey;
			try {
				privateKey = Ed25519PrivateKey.loadFromFile(file, password);
			} finally {
				Arrays.fill(password, (char) 0x00);
			}
			Ed25519PublicKey publicKey = privateKey.derivePublicKey();
			model.setPrivateKey(KeyOrigin.LOADED_PRIVATE_KEY, privateKey, publicKey);
			return null;
		}, listener);
	}
	
	public void loadPublicKey(File file, TaskListener<Void> listener) {
		this.model.resetKeys();
		this.executeTask(() -> {
			Ed25519PublicKey publicKey = Ed25519PublicKey.loadFromFile(file);
			model.setPublicKey(KeyOrigin.LOADED_PUBLIC_KEY, publicKey);
			return null;
		}, listener);
	}

	public void savePrivateKey(File file, char[] password, TaskListener<Void> listener) {
		Ed25519PrivateKey privateKey = model.getPrivateKey();
		this.executeTask(() -> {
			try {
				privateKey.saveAsFile(file, password);
			} finally {
				Arrays.fill(password, (char) 0x00);
			}
			return null;
		}, listener);
	}
	
	public void savePublicKey(File file, TaskListener<Void> listener) {
		Ed25519PublicKey publicKey = model.getPublicKey();
		this.executeTask(() -> {
			publicKey.saveAsFile(file);
			return null;
		}, listener);
	}
	
	public void signFile(TaskListener<Void> listener) {
		SignatureFilePair files = this.model.getSignatureFiles();
		this.signFile(files.getSourceFile(), files.getSignatureFile(), listener);
	}
	
	public void signFile(File sourceFile, File signatureFile, TaskListener<Void> listener) {
		Ed25519PrivateKey privateKey = model.getPrivateKey();
		this.executeTask(() -> {
			privateKey.signToFile(sourceFile, signatureFile, listener);
			return null;
		}, listener);
	}
	
	public void verifyFile(TaskListener<Boolean> listener) {
		SignatureFilePair files = this.model.getSignatureFiles();
		this.verifyFile(files.getSourceFile(), files.getSignatureFile(), listener);
	}
	
	public void verifyFile(File sourceFile, File signatureFile, TaskListener<Boolean> listener) {
		Ed25519PublicKey publicKey = model.getPublicKey();
		this.executeTask(() -> {
			return publicKey.verifyFromFile(sourceFile, signatureFile, listener);
		}, listener);
	}
	
	public void signString(String data, TaskListener<String> listener) {
		Ed25519PrivateKey privateKey = model.getPrivateKey();
		this.executeTask(() -> {
			return privateKey.sign(data);
		}, listener);
	}
	
	public void verifyString(String data, String signature, TaskListener<Boolean> listener) {
		Ed25519PublicKey publicKey = model.getPublicKey();
		this.executeTask(() -> {
			return publicKey.verify(data, signature);
		}, listener);
	}

}
