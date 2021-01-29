package com.datastax.mailpay.dao;

import com.datastax.demo.utils.PropertyHelper;
import com.datastax.mailpay.Result;
import com.datastax.mailpay.State;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class MailPayDao {
	private static final Logger logger = LoggerFactory.getLogger(MailPayDao.class);
	private CqlSession session;

	private static final String keyspaceName = "datastax_mailpay";
	private static final String txtable = keyspaceName + ".transactions";
	private static final String statetable = keyspaceName + ".transaction_state";

	private static final String INSERT =
			"insert into " + txtable + "(account, transaction_time, transaction_id, other_account, amount, reference) values (?,?,?,?,?,?)";
	
	/*
	 * For Transactions States the process is as follows
	 * 
	 * Lock accounts
	 * 
	 * Insert 'STARTING' STATUS
	 * 
	 * Insert into acc1 - if success - status = ACC1
	 * Insert into acc2 - if success - status = ACC2
	 * 
	 * Update STATUS to 'SUCCEEDED'
	 * 
	 * If either acc1 or acc2 fails - retry and then leave in state to be fixed.
	 * 
	 * transactions states  
	 *  
	 * 
	 */
	private static final String INSERT_STATE =
			"insert into " + statetable + "(account1, account2, transaction_id, transaction_time, amount, reference, status) values (?,?,?,?,?,?,?)";
	private static final String INSERT_STATE_STATUS =
			"update " + statetable + " set status=? where account1 = ? and account2 = ? and transaction_id =?";
	
	private PreparedStatement insert;
	private PreparedStatement insertState;
	private PreparedStatement insertStateStatus;

	public MailPayDao() {
		try {
			this.session = CqlSession.builder()
					.addContactPoints(PropertyHelper.getContactPoints())
					.withLocalDatacenter(PropertyHelper.getLocalDatacenter())
					.build();
			this.insert = this.session.prepare(INSERT);
			this.insertState = this.session.prepare(INSERT_STATE);
			this.insertStateStatus = this.session.prepare(INSERT_STATE_STATUS);
		} catch (Exception e) {
			logger.error("Could not initialize MailPayDao", e);
			System.exit(1);
		}
	}
	
	public boolean insertTransactionState(String transactionId, String acc1, String acc2, double amount, String reference, DateTime transactionTime, State state) {
		session.execute(insertState.bind(acc1, acc2, transactionId, Instant.ofEpochMilli(transactionTime.getMillis()), amount, reference, state.toString()));
		return true;
	}
	
	public boolean updateStateStatus (String acc1, String acc2, String transactionId, State state){
		session.execute(insertStateStatus.bind(state.toString(), acc1, acc2, transactionId));
		return true;
	}
	
	public Result insertTransaction(String transactionId, String acc1, String acc2, double amount, String reference, DateTime transactionTime) {
		Result result = new Result(transactionId);
		
		// Insert the transaction into the transactions table.
		try {
			session.execute(insert.bind(acc1, Instant.ofEpochMilli(transactionTime.getMillis()), transactionId, acc2, amount, reference));
			result.setApproved(true);
		} catch (Exception e) {
			logger.warn(e.getMessage());
			result.setApproved(false);
			result.setResponseText(e.getMessage());
		}
		return result;
	}
}
