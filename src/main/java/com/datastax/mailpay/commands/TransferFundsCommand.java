package com.datastax.mailpay.commands;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.demo.utils.Timer;
import com.datastax.mailpay.MailPayService;
import com.datastax.mailpay.Result;

public class TransferFundsCommand implements Command {

	private static Logger logger = LoggerFactory.getLogger(TransferFundsCommand.class);

	private String acc1;
	private String acc2;
	private double amount;
	private String reference;
	private DateTime transactionTime;

	private static MailPayService mailPayService = MailPayService.getInstance();

	public TransferFundsCommand(String acc1, String acc2, double amount, String reference, DateTime transactionTime) {
		this.acc1 = acc1;
		this.acc2 = acc2;
		this.amount = amount;
		this.reference = reference;
		this.transactionTime = transactionTime;
	}

	@Override
	public Boolean run() throws Exception {
		
		Timer transactionTimer = new Timer();		
		
		Result result1 = mailPayService.transferMoney(acc1, acc2, amount, reference, transactionTime);
		
		if (result1.isApproved()){
			try{
				mailPayService.transferMoney(acc2, acc1, -amount, reference, transactionTime);
			}catch (Exception e){
				//Any exception must undo the result from the first Debit. 
				e.getMessage();
				logger.info("Undo transaction " + result1.getTransactionId());

				try{
					Result undoresult = mailPayService.transferMoney(acc1, acc2, -amount, "Undoing transaction " + result1.getTransactionId(), transactionTime.plusMillis(100));
					
					if (undoresult.isDeclined()){
						throw new Exception (undoresult.getResponseText());
					}
					
					logger.info("Transaction " + result1.getTransactionId() + "rolled back successfully");
				}catch (Exception e1){
					logger.info("Can't undo transaction " + result1.getTransactionId() );
					logger.info(e1.getMessage());
					logger.info("Need to log message");
				}				
			}
		}else{
			return false;
		}
		transactionTimer.end();
		
		logger.debug("Transfer money -  Time taken :" + transactionTimer.getTimeTakenMillis() + "ms");
		return true;
	}
}
