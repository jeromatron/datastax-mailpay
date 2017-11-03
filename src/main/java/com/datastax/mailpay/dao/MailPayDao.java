package com.datastax.mailpay.dao;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.mailpay.Result;

public class MailPayDao {

	private static Logger logger = LoggerFactory.getLogger(MailPayDao.class);
	private Session session;

	private static String keyspaceName = "datastax_mailpay";
	private static String txtable = keyspaceName + ".transactions";

	private static String INSERT = "insert into " + txtable + "(account, transaction_time, transaction_id, other_account, amount, reference) values (?,?,?,?,?,?)";
	
	private PreparedStatement insert;

	public MailPayDao(String[] contactPoints) {

		Cluster cluster = Cluster.builder().addContactPoints(contactPoints).build();

		this.session = cluster.connect();
		this.insert = this.session.prepare(INSERT);
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
