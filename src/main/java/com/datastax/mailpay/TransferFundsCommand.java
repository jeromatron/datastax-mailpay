package com.datastax.mailpay;

import com.datastax.lock.LockService;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class TransferFundsCommand extends HystrixCommand<Boolean>{

	private String acc1;
	private String acc2;
	private double amount;
	private String reference; 
	
	private LockService lockService = LockService.getInstance();
	
	public TransferFundsCommand(String acc1, String acc2, double amount, String reference) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Payment"))
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(300)));
		
		this.acc1 = acc1;
		this.acc2 = acc2;
		this.amount = amount;
		this.reference = reference;		
	}


	@Override
	protected Boolean run() throws Exception {
		simulate ();
				
		
		return true;
	}
	
	
	@Override
    protected Boolean getFallback() {
		return false;
	}

	private void simulate() {
	    /* simulate performing network call to retrieve user information */
        try {
            Thread.sleep((int) (Math.random() * 10) + 2);
        } catch (InterruptedException e) {
            // do nothing
        }

        /* fail 5% of the time to show how fallback works */
        if (Math.random() > 0.95) {
            throw new RuntimeException("random failure processing UserAccount network response");
        }

        /* latency spike 5% of the time so timeouts can be triggered occasionally */
        if (Math.random() > 0.95) {
            // random latency spike
            try {
                Thread.sleep((int) (Math.random() * 300) + 25);
            } catch (InterruptedException e) {
                // do nothing
            }
        }		
	}

	
}
