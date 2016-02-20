package com.clockwise.exceptions;

public class BadImageException extends RuntimeException {
	private static final long serialVersionUID = 6084382493492641173L;

	public BadImageException() {
		super();
	}
	public BadImageException(String message) {
		super(message);
	}
	public BadImageException(String message, Throwable cause) {
		super(message, cause);
	}
	public BadImageException(Throwable cause) {
		super(cause);
	}

}
