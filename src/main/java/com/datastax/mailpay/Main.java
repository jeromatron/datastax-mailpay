package com.datastax.mailpay;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.demo.utils.PropertyHelper;
import com.datastax.demo.utils.Timer;

public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	private final AtomicLong counter = new AtomicLong(0);

	public Main() {
		int noOfThreads = Integer.parseInt(PropertyHelper.getProperty("noOfThreads", "20"));
		int noOfAccounts = Integer.parseInt(PropertyHelper.getProperty("noOfAccounts", "1000000"));
		int noOfTransactions = Integer.parseInt(PropertyHelper.getProperty("noOfTransactions", "500000"));

		BlockingQueue<Transaction> queue = new ArrayBlockingQueue<Transaction>(5);
		ExecutorService executor = Executors.newFixedThreadPool(noOfThreads);

		Timer timer = new Timer();
		timer.start();

		for (int i = 0; i < noOfThreads; i++) {
			executor.execute(new Writer(queue));
		}

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		while (counter.get() < noOfTransactions) {
			Transaction transaction = TransactionGenerator.createRandomTransaction(noOfAccounts);
			try {
				queue.put(transaction);
				counter.incrementAndGet();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (counter.get() % 1000 == 0) {
				logger.info("Total : " + counter.get());
			}
		}

		timer.end();
		logger.info(noOfTransactions + " took " + timer.getTimeTakenSeconds() + " sec ("
				+ (noOfTransactions / timer.getTimeTakenSeconds()) + ") a sec");
	}

	/**
	 * @param args
	 */
	public static void main(String... args) {
		new Main();
	}
}