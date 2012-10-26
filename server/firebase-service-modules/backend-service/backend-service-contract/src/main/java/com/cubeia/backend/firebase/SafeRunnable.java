package com.cubeia.backend.firebase;

import org.apache.log4j.Logger;

public abstract class SafeRunnable implements Runnable {

	@Override
	public final void run() {
		try {
			execute();
		} catch(Throwable th) {
			Logger.getLogger(getClass()).error("Unexpected error", th);
		}
	}

	protected abstract void execute();
	
}
