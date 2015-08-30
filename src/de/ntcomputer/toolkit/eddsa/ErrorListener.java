package de.ntcomputer.toolkit.eddsa;

@FunctionalInterface
public interface ErrorListener {

	void onError(Exception e);

}