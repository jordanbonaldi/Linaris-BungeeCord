package net.neferett.linaris.api;

@SuppressWarnings("serial")
public class InvalidTypeException extends IllegalArgumentException {
	public InvalidTypeException() {
	}

	public InvalidTypeException(String s) {
		super(s);
	}

	public InvalidTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidTypeException(Throwable cause) {
		super(cause);
	}
}
