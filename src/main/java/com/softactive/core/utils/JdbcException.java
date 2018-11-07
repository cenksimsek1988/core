package com.softactive.core.utils;

import lombok.Getter;

@Getter
public class JdbcException extends Exception {

	private static final long serialVersionUID = -5631980306339450160L;

	public JdbcException(Exception e) {
		super(e);
	}

	public JdbcException(String message) {
		super(message);
	}

	public JdbcException(String message, Integer listIndex) {
		super(message);
		this.listIndex = listIndex;
	}

	private Integer listIndex;

}