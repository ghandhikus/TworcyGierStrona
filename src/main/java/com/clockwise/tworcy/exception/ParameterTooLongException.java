package com.clockwise.tworcy.exception;

public class ParameterTooLongException extends RuntimeException {
	private static final long serialVersionUID = -7445593581146889114L;

	public ParameterTooLongException() {
		super();
	}
	public ParameterTooLongException(String message) {
		super(message);
	}
	public ParameterTooLongException(String message, Throwable cause) {
		super(message, cause);
	}
	public ParameterTooLongException(Throwable cause) {
		super(cause);
	}

}
