package com.datastax.mailpay;

import org.joda.time.DateTime;

public class Transaction {

	private String acc1;
	private String acc2;
	private String reference;
	private DateTime transactionTime;
	private double amount;
	
	public Transaction(String acc1, String acc2, String reference, DateTime transactionTime, double amount) {
		super();
		this.acc1 = acc1;
		this.acc2 = acc2;
		this.reference = reference;
		this.transactionTime = transactionTime;
		this.amount = amount;
	}
	
	public String getAcc1() {
		return acc1;
	}
	public void setAcc1(String acc1) {
		this.acc1 = acc1;
	}
	public String getAcc2() {
		return acc2;
	}
	public void setAcc2(String acc2) {
		this.acc2 = acc2;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public DateTime getTransactionTime() {
		return transactionTime;
	}
	public void setTransactionTime(DateTime transactionTime) {
		this.transactionTime = transactionTime;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	@Override
	public String toString() {
		return "Transaction [acc1=" + acc1 + ", acc2=" + acc2 + ", reference=" + reference + ", transactionTime="
				+ transactionTime + ", amount=" + amount + "]";
	}
}
