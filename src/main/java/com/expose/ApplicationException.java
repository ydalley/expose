package com.expose;

import java.util.Stack;

public class ApplicationException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3098556436857019819L;
	private Stack<String> messages = new Stack<String>();

	public ApplicationException() {
	}

	public ApplicationException(String message) {
		super(message);
		add(message);
	}

	public ApplicationException(Throwable cause) {
		super(cause);
	}

	public ApplicationException(String message, Throwable cause) {
		super(message, cause);
		add(message);
	}
	
	public void add(String message){
		messages.push(message);
	}

	public Stack<String> getMessages() {
		return messages;
	}

}
