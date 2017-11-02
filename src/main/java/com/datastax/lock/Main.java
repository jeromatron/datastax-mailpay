package com.datastax.lock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.demo.utils.PropertyHelper;
import com.datastax.demo.utils.Timer;

public class Main {
	private static final long TOTAL = 10000000;
	private static Logger logger = LoggerFactory.getLogger(Main.class);
	private static int NO_OF_SEQUENCES = 100;
	private int noOfThreads;
	private AtomicLong counter = new AtomicLong(0);

	public Main() {

		String noOfThreadsStr = PropertyHelper.getProperty("noOfThreads", "10");
		noOfThreads = Integer.parseInt(noOfThreadsStr);
		
		NO_OF_SEQUENCES = Integer.parseInt(PropertyHelper.getProperty("noOfSeqs", "100"));
		
		LockService service = new LockService();
		ExecutorService executor = Executors.newFixedThreadPool(noOfThreads);
				
		Timer timer = new Timer();
		timer.start();
		
		for (int i = 0; i < noOfThreads; i++) {
			executor.execute(new Writer(service));
		}
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		while (counter.get()<TOTAL) {
			if (counter.get()%10000==0){
				logger.info("Total : " + counter.get());
			}
		}
		
		timer.end();
		logger.info(TOTAL + " took " + timer.getTimeTakenSeconds() + " sec (" + (TOTAL/timer.getTimeTakenSeconds()) + ") a sec" );
		System.exit(0);
	}

	class Writer implements Runnable {

		private LockService service;

		public Writer(LockService service) {
			this.service = service;
		}

		@Override
		public void run() {
			while(true){

			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Main();

		System.exit(0);
	}
}
