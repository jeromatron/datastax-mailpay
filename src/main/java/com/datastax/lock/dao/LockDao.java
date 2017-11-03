package com.datastax.lock.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.WriteTimeoutException;
import com.datastax.driver.core.policies.LoggingRetryPolicy;

public class LockDao {

	private static Logger logger = LoggerFactory.getLogger(LockDao.class);
	private Session session;

	private static String keyspaceName = "datastax_mailpay";
	private static String seqtable = keyspaceName + ".lock_mem";

	private String LOCK_UPDATE = "update " + seqtable + " set lock = ? where id = ? if lock=?";
	private String DELETE_UPDATE = "delete from " + seqtable + " where id = ?";

	private PreparedStatement update;
	private PreparedStatement delete;

	public LockDao(String[] contactPoints) {

		Cluster cluster = Cluster.builder()
				.addContactPoints(contactPoints)
				.build();	

		this.session = cluster.connect();

		this.update = session.prepare(LOCK_UPDATE);
		this.delete = session.prepare(DELETE_UPDATE);		
	}

	public boolean getLock(String id) {

		try {
			ResultSet resultSet = this.session.execute(update.bind("locked", id, null));

			if (resultSet != null) {
				Row row = resultSet.one();

				if (row.getBool(0) == false) {
					String failedLocked = row.getString("lock");
					logger.info("Update failed as current lock was " + failedLocked + " not null");
					return false;
				} else {
					logger.debug("lock acquired for " + id);
				}
			}
		} catch (WriteTimeoutException e) {
			logger.warn(e.getMessage());
			return false;
		}

		return true;
	}
	
	public boolean releaseLock(String id) {

		try {
			this.session.execute(delete.bind(id));
			logger.debug("lock deleted for " + id);
		} catch (WriteTimeoutException e) {
			logger.warn(e.getMessage());
			return false;
		}

		return true;
	}
}
