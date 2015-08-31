package de.ntcomputer.toolkit.eddsa;

import de.ntcomputer.crypto.hash.ProgressListener;

public interface TaskListener<T> extends SuccessListener<T>, ErrorListener, ProgressListener {

}
