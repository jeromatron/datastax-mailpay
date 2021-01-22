package com.datastax.mailpay;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.demo.utils.KillableRunner;
import com.datastax.mailpay.commands.LockCommand;
import com.datastax.mailpay.commands.TransferFundsCommand;
import com.datastax.mailpay.commands.UnLockCommand;

class Writer implements KillableRunner {
	private static final Logger logger = LoggerFactory.getLogger(Writer.class);
	private final BlockingQueue<Transaction> queue;
	private volatile boolean shutdown = false;

	public Writer(BlockingQueue<Transaction> queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		Transaction transaction;
		while (!shutdown) {
			transaction = queue.poll();

			if (transaction != null) {
				try {					
					// Lock accounts
					LockCommand lock = new LockCommand(transaction.getAcc1(), transaction.getAcc2());
					lock.run();

					// Transfer money
					TransferFundsCommand transferFundsCommand = new TransferFundsCommand(transaction);
					boolean succeeded = transferFundsCommand.run();
					
					if (!succeeded) {
						logger.info("Transfer failed.");
					}

					// Unlock accounts
					UnLockCommand unlock = new UnLockCommand(transaction.getAcc1(), transaction.getAcc2());
					unlock.run();

				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
		}
	}

	@Override
	public void shutdown() {
		while (!queue.isEmpty())
			shutdown = true;
	}
}
