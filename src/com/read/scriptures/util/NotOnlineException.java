package com.read.scriptures.util;

public class NotOnlineException extends Exception {
	private static final long serialVersionUID = 3754872416951601521L;

	public NotOnlineException() {
		super();
	}

	public NotOnlineException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotOnlineException(String message) {
		super(message);
	}

	public NotOnlineException(Throwable cause) {
		super(cause);
	}

}
