package com.datastax.mailpay;

import java.util.UUID;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.demo.utils.PropertyHelper;
import com.datastax.demo.utils.Timer;
import com.datastax.mailpay.dao.MailPayDao;

public class MailPayService {

	private static Logger logger = LoggerFactory.getLogger(MailPayService.class);
	private static MailPayService service = new MailPayService();

	private MailPayDao dao = new MailPayDao(PropertyHelper.getProperty("contactPoints", "localhost").split(","));

	public static MailPayService getInstance() {
		return service;
	}

	public Result transferMoney(String acc1, String acc2, double amount, String reference,
			DateTime transactionTime) throws Exception {

		simulateDelayOrException();

		String transactionId = UUID.randomUUID().toString();
		Result result = dao.insertTransaction(transactionId, acc1, acc2, amount, reference, transactionTime);
		
		if (result.isDeclined()){
			throw new DBException(result.getResponseText());
		}
		logger.debug(String.format("Transfer made %s to %s", acc1, acc2));
		return result;
	}

	private void simulateDelayOrException() {

		Timer timer = new Timer();
		/* simulate performing network call to retrieve user information */
		try {
			Thread.sleep((int) (Math.random() * 10) + 2);
		} catch (InterruptedException e) {
		}

		/* fail 5% of the time to show how fallback works */
		if (Math.random() > 0.999) {
			throw new RuntimeException("random failure processing transaction response");
		}

		/*
		 * latency spike 5% of the time so timeouts can be triggered
		 * occasionally
		 */
		if (Math.random() > 0.95) {
			// random latency spike
			try {
				Thread.sleep((int) (Math.random() * 300) + 25);
			} catch (InterruptedException e) {
			}
		}
		timer.end();
		logger.debug("Timer delay took " + timer.getTimeTakenMillis());
	}
}
