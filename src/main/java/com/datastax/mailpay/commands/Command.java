package com.datastax.mailpay.commands;

public interface Command {
	Boolean run() throws Exception;
}
