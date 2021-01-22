package com.datastax.mailpay.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.demo.utils.Timer;
import com.datastax.mailpay.MailPayService;
import com.datastax.mailpay.Result;
import com.datastax.mailpay.State;
import com.datastax.mailpay.Transaction;

public class TransferFundsCommand implements Command {
	private static final Logger logger = LoggerFactory.getLogger(TransferFundsCommand.class);

	private final Transaction transaction;
	private static final MailPayService mailPayService = MailPayService.getInstance();

	public TransferFundsCommand(Transaction transaction) {
		this.transaction = transaction;
	}

	@Override
	public Boolean run() throws Exception {
		Timer transactionTimer = new Timer();		
		
		if (mailPayService.insertTransactionState(transaction)){
			logger.debug(transaction.getTransactionId() + " state created");
		}
		
		Result result1 = mailPayService.transferMoney(transaction.getAcc1(), transaction.getAcc2(), transaction.getAmount(), 
				transaction.getReference(), transaction.getTransactionTime(), transaction.getTransactionId());
		
		if (result1.isApproved()){
			try{
				mailPayService.transferMoney(transaction.getAcc2(), transaction.getAcc1(), -transaction.getAmount(), 
						transaction.getReference(), transaction.getTransactionTime(), transaction.getTransactionId());
								
				mailPayService.updateStateStatus(transaction.getAcc1(), transaction.getAcc2(), transaction.getTransactionId(), State.SUCCESSFUL);
			}catch (Exception e){
				//Any exception must undo the result from the first Debit. 
				e.getMessage();
				
				logger.info("Undo transaction " + result1.getTransactionId());

				try{
					Result undoresult = mailPayService.transferMoney(transaction.getAcc1(), transaction.getAcc2(), -transaction.getAmount(), "Undoing transaction " + transaction.getTransactionId(), transaction.getTransactionTime().plusMillis(100), transaction.getTransactionId());
					
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
		} else {
			return false;
		}
		transactionTimer.end();
		
		logger.debug("Transfer money -  Time taken :" + transactionTimer.getTimeTakenMillis() + "ms");
		return true;
	}
}
