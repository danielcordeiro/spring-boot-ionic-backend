package com.dgc.cursomc.exception;

public class DataIntegrityException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DataIntegrityException(String message) {
		super(message);
	}

	public DataIntegrityException() {
		super();
	}

	public DataIntegrityException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public DataIntegrityException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public DataIntegrityException(Throwable arg0) {
		super(arg0);
	}

}
