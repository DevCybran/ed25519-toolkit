package de.ntcomputer.toolkit.eddsa;

@FunctionalInterface
public interface SuccessListener<T> {

	void onSuccess(T result);

}