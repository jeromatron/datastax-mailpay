package com.datastax.mailpay.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.WriteTimeoutException;
import com.datastax.lock.dao.LockDao;

public class MailPayDao {

	private static Logger logger = LoggerFactory.getLogger(LockDao.class);
	private Session session;

	private static String keyspaceName = "datastax_mailpay";
	private static String txtable = keyspaceName + ".transactions";

	private PreparedStatement update;
	private PreparedStatement delete;

	public MailPayDao(String[] contactPoints) {

		Cluster cluster = Cluster.builder().addContactPoints(contactPoints).build();

		this.session = cluster.connect();
	}

}
