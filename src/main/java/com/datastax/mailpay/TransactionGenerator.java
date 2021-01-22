package com.datastax.mailpay;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;

public class TransactionGenerator {
	private static final long DAY_MILLIS = 1000 * 60 * 60 * 24;
	private static final long noOfDays = 90;

	public static Transaction createRandomTransaction(int noOfAccounts) {
		long noOfMillis = noOfDays * DAY_MILLIS;

		int acc1 = Double.valueOf(Math.random() * noOfAccounts).intValue();
		int acc2 = Double.valueOf(Math.random() * noOfAccounts).intValue();

		while (acc1 == acc2) {
			acc2 = Double.valueOf(Math.random() * noOfAccounts).intValue();
		}

		long millis = DateTime.now().getMillis() - (Double.valueOf(Math.random() * noOfMillis).longValue() + 1L);
		DateTime newDate = DateTime.now().withMillis(millis);

		String transactionId = UUID.randomUUID().toString();

		return new Transaction(acc1 + "@gmail.com", acc2 + "@gmail.com", acc1 + "-" + acc2, newDate, transactionId, Math.random()*1000);
	}

	public static List<String> statuses = Arrays.asList("SUCCESS", "FAILED", "CANCELLED");
}
