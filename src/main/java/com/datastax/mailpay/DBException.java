package com.datastax.mailpay;

public class DBException extends Exception {
	public DBException(String msg) {
		super(msg);
	}
}
