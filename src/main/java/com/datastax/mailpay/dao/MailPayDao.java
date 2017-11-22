package com.datastax.mailpay.dao;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.mailpay.Result;
import com.datastax.mailpay.State;

public class MailPayDao{

	private static Logger logger = LoggerFactory.getLogger(MailPayDao.class);
	private Session session;

	private static String keyspaceName = "datastax_mailpay";
	private static String txtable = keyspaceName + ".transactions";
	private static String statetable = keyspaceName + ".transaction_state";

	private static String INSERT = "insert into " + txtable + "(account, transaction_time, transaction_id, other_account, amount, reference) values (?,?,?,?,?,?)";
	
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
	private static String INSERT_STATE = "insert into " + statetable + "(account1, account2, transaction_id, transaction_time, amount, reference, status) values (?,?,?,?,?,?,?)";
	private static String INSERT_STATE_STATUS = "update " + statetable + " set status=? where account1 = ? and account2 = ? and transaction_id =?";
	
	private PreparedStatement insert;
	private PreparedStatement insertState;
	private PreparedStatement insertStateStatus;

	public MailPayDao(String[] contactPoints) {

		Cluster cluster = Cluster.builder().addContactPoints(contactPoints).build();

		this.session = cluster.connect();
		this.insert = this.session.prepare(INSERT);
		this.insertState = this.session.prepare(INSERT_STATE);
		this.insertStateStatus = this.session.prepare(INSERT_STATE_STATUS);
	}
	
	public boolean insertTransactionState(String transactionId, String acc1, String acc2, double amount, String reference, DateTime transactionTime, State state) {
		
		session.execute(insertState.bind(acc1, acc2, transactionId, transactionTime.toDate(), amount, reference, state.toString()));				
		return true;
	}
	
	public boolean updateStateStatus (String acc1, String acc2, String transactionId, State state){
		session.execute(insertStateStatus.bind(state.toString(), acc1, acc2, transactionId));
		
		return true;
	}
	
	public Result insertTransaction(String transactionId, String acc1, String acc2, double amount, String reference, DateTime transactionTime) {

		Result result = new Result(transactionId);
		
		//Insert the transaction into the transactions table.
		try{
			session.execute(insert.bind(acc1, transactionTime.toDate(), transactionId, acc2, amount, reference));
			result.setApproved(true);
		}catch (Exception e){
			logger.warn(e.getMessage());
			result.setApproved(false);
			result.setResponseText(e.getMessage());			
		}
		return result;		
	}
}
