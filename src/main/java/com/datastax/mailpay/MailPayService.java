package com.datastax.mailpay;

import com.datastax.demo.utils.Timer;
import com.datastax.mailpay.dao.MailPayDao;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailPayService {
	private static final Logger logger = LoggerFactory.getLogger(MailPayService.class);
	private static final MailPayService service = new MailPayService();

	private final MailPayDao dao = new MailPayDao();

	public static MailPayService getInstance() {
		return service;
	}

	public Result transferMoney(String acc1, String acc2, double amount, String reference, DateTime transactionTime, String transactionId) throws Exception {

		simulateDelayOrException();
				
		Result result = dao.insertTransaction(transactionId, acc1, acc2, amount, reference, transactionTime);
		
		if (result.isDeclined()){
			throw new DBException(result.getResponseText());
		}
		
		logger.debug(transactionId + " state updated");		
		logger.debug(String.format("Transfer made %s to %s", acc1, acc2));
		return result;
	}
	
	public boolean updateStateStatus(String acc1, String acc2, String transactionId, State state){
		this.dao.updateStateStatus(acc1, acc2, transactionId, state);
		
		return true;
	}

	private void simulateDelayOrException() {
		Timer timer = new Timer();
		/* simulate performing network call to retrieve user information */
		try {
			Thread.sleep((int) (Math.random() * 10) + 2);
		} catch (InterruptedException e) {
		}

		/* fail .001% of the time to show how fallback works */
		if (Math.random() > 0.999) {
			throw new RuntimeException("random failure processing transaction response");
		}

		/*
		 * latency spike .001% of the time so timeouts can be triggered
		 * occasionally
		 */
		if (Math.random() > 0.999) {
			// random latency spike
			try {
				Thread.sleep((int) (Math.random() * 300) + 25);
			} catch (InterruptedException e) {
			}
		}
		timer.end();
		logger.debug("Timer delay took " + timer.getTimeTakenMillis());
	}

	public boolean insertTransactionState(Transaction transaction) {
		dao.insertTransactionState(
				transaction.getTransactionId(),
				transaction.getAcc1(),
				transaction.getAcc2(),
				transaction.getAmount(),
				transaction.getReference(),
				transaction.getTransactionTime(),
				State.STARTED
		);
		return true;
	}
}
