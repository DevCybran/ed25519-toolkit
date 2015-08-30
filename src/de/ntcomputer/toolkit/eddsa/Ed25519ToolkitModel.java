package de.ntcomputer.toolkit.eddsa;

import java.io.File;

import javax.security.auth.DestroyFailedException;

import de.ntcomputer.crypto.eddsa.Ed25519PrivateKey;
import de.ntcomputer.crypto.eddsa.Ed25519PublicKey;

public class Ed25519ToolkitModel {
	private KeyOrigin keyOrigin = KeyOrigin.NONE;
	private Ed25519PrivateKey privateKey = null;
	private Ed25519PublicKey publicKey = null;
	private File sourceFile = null;
	private File signatureFile = null;
	
	public synchronized void resetKeys() {
		if(this.privateKey!=null) {
			try {
				this.privateKey.destroy();
			} catch (DestroyFailedException e) {
			}
		}
		this.privateKey = null;
		this.publicKey = null;
		this.keyOrigin = KeyOrigin.NONE;
	}
	
	public synchronized KeyOrigin getKeyOrigin() {
		return keyOrigin;
	}

	public synchronized boolean hasPrivateKey() {
		return this.privateKey != null;
	}
	
	public synchronized boolean hasPublicKey() {
		return this.publicKey != null;
	}
	
	public synchronized Ed25519PrivateKey getPrivateKey() {
		return privateKey;
	}
	
	public synchronized void setPrivateKey(KeyOrigin origin, Ed25519PrivateKey privateKey, Ed25519PublicKey publicKey) {
		this.keyOrigin = origin;
		this.privateKey = privateKey;
		this.publicKey = publicKey;
	}
	
	public synchronized Ed25519PublicKey getPublicKey() {
		return publicKey;
	}
	
	public synchronized void setPublicKey(KeyOrigin origin, Ed25519PublicKey publicKey) {
		this.keyOrigin = origin;
		this.publicKey = publicKey;
	}
	
	public synchronized boolean hasSignatureFiles() {
		return this.sourceFile!=null && this.signatureFile!=null;
	}
	
	public synchronized SignatureFilePair getSignatureFiles() {
		return new SignatureFilePair(sourceFile, signatureFile);
	}
	
	public synchronized void setSignatureFiles(File sourceFile, File signatureFile) {
		this.sourceFile = sourceFile;
		this.signatureFile = signatureFile;
	}
	
	public synchronized void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}
	
	public synchronized void setSignatureFile(File signatureFile) {
		this.signatureFile = signatureFile;
	}
	
	public static class SignatureFilePair {
		private final File sourceFile, signatureFile;
		
		private SignatureFilePair(File sourceFile, File signatureFile) {
			this.sourceFile = sourceFile;
			this.signatureFile = signatureFile;
		}

		public File getSourceFile() {
			return sourceFile;
		}

		public File getSignatureFile() {
			return signatureFile;
		}
	}
	
	public static enum KeyOrigin {
		NONE,
		GENERATED_KEYPAIR,
		LOADED_PRIVATE_KEY,
		LOADED_PUBLIC_KEY
	}
	
}
