package com.datastax.mailpay.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.demo.utils.Timer;
import com.datastax.lock.LockService;

public class LockCommand implements Command{
	private static final Logger logger = LoggerFactory.getLogger(LockCommand.class);
	
	private final String acc1;
	private final String acc2;
	
	private static final LockService lockService = LockService.getInstance();
	
	public LockCommand(String acc1, String acc2) {
		this.acc1 = acc1;
		this.acc2 = acc2;
	}

	@Override
	public Boolean run() throws Exception {
		Timer transactionTimer = new Timer();		
		lockService.getLock(acc1);
		lockService.getLock(acc2);
		
		transactionTimer.end();
		
		logger.debug("Lock Time taken :" + transactionTimer.getTimeTakenMillis() + "ms");
		return true;
	}    
}
