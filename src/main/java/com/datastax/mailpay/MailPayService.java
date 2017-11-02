package com.datastax.mailpay;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MailPayService {

	private static MailPayService service = new MailPayService();
	
	public static MailPayService getInstance() {
		return service;
	}
	
	public boolean transferMoney(String acc1, String acc2, double amount, String reference){
		
		
		//There needs to be a circut breaker.
		TransferFundsCommand command = new TransferFundsCommand(acc1, acc2, amount, reference);
		Future<Boolean> queue = command.queue();
		
		
		try {
			queue.get(300, TimeUnit.MILLISECONDS);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		
		return true;
	}
}
