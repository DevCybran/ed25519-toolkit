package de.ntcomputer.toolkit.eddsa;

public class Ed25519Toolkit {

	public static void main(String[] args) {
		Ed25519ToolkitModel model = new Ed25519ToolkitModel();
		Ed25519ToolkitController controller = new Ed25519ToolkitController(model);
		new Ed25519ToolkitGui(model, controller);
	}

}
