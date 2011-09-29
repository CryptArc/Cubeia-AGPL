package com.cubeia.backend.cashgame;

class CallbackHandler {
	boolean called = false;
	final Pointer<Object> responsePtr = new Pointer<Object>();

	void setResponse(Object response) {
		synchronized (responsePtr) {
			if (called) {
				throw new IllegalStateException("callback already invoked");
			}
			called = true;
			responsePtr.object = response;
			responsePtr.notify();
		}
	}

	Object getResponse(long timeout) throws InterruptedException {
		synchronized (responsePtr) {
			if (called) {
				return responsePtr.object;
			}

			responsePtr.wait(timeout);
			return responsePtr.object;
		}
	}

	class Pointer<T> {
		public volatile T object = null;
	}
}