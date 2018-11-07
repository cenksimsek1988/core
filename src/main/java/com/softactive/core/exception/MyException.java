package com.softactive.core.exception;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MyException extends Exception {
	private static final long serialVersionUID = 839110657769157769L;
	private String msg;
	public MyException(String msg) {
		this.msg = msg;
	}
	@Override
	public String toString() {
		return msg;
	}
}
