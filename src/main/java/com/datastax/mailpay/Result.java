package com.datastax.mailpay;

public class Result {
	private boolean approved;
	private final String transactionId;
	private String responseText;
	private String responseCode;

	public Result(String transactionId) {
		this.transactionId = transactionId;
	}

	public boolean isApproved() {
		return approved;
	}

	public String getResponseText() {
		return responseText;
	}

	public boolean isDeclined() {
		return !approved;
	}
	public String getResponseCode() {
		return responseCode;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}
	
	public String getTransactionId() {
		return transactionId;
	}

	@Override
	public String toString() {
		return "Result [approved=" + approved +
				", transactionId=" + transactionId +
				", responseText=" + responseText +
				", responseCode=" + responseCode + "]";
	}
}
