package com.softactive.core.exception;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MySheetNotFoundException extends Exception {
	private static final long serialVersionUID = 4214920247209728250L;
	private String msg;
	public MySheetNotFoundException(String msg) {
		this.msg = msg;
	}
	@Override
	public String toString() {
		return msg;
	}
}
