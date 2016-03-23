package com.clockwise.tworcy.exception;

public class InvalidYoutubeLink extends RuntimeException {
	private static final long serialVersionUID = 823498238942943L;

	public InvalidYoutubeLink() {
		super();
	}
	public InvalidYoutubeLink(String message) {
		super(message);
	}
	public InvalidYoutubeLink(String message, Throwable cause) {
		super(message, cause);
	}
	public InvalidYoutubeLink(Throwable cause) {
		super(cause);
	}

}
