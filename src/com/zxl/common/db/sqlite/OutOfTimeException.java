package com.zxl.common.db.sqlite;

public class OutOfTimeException extends Exception {
	private static final long serialVersionUID = 3754872416951601521L;

	public OutOfTimeException() {
		super();
	}

	public OutOfTimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public OutOfTimeException(String message) {
		super(message);
	}

	public OutOfTimeException(Throwable cause) {
		super(cause);
	}

}
