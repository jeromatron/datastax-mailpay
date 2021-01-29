package com.datastax.lock.dao;

import com.datastax.demo.utils.PropertyHelper;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.servererrors.WriteTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockDao {
	private static final Logger logger = LoggerFactory.getLogger(LockDao.class);
	private CqlSession session;

	private static final String seqtable = PropertyHelper.getKeyspaceName() + ".lock_mem";

	private static final String LOCK_UPDATE = "update " + seqtable + " set lock = ? where id = ? if lock=?";
	private static final String DELETE_UPDATE = "delete from " + seqtable + " where id = ?";

	private PreparedStatement update;
	private PreparedStatement delete;

	public LockDao() {
		try {
			this.session = CqlSession.builder()
					.addContactPoints(PropertyHelper.getContactPoints())
					.withLocalDatacenter(PropertyHelper.getLocalDatacenter())
					.build();
			this.update = session.prepare(LOCK_UPDATE);
			this.delete = session.prepare(DELETE_UPDATE);
		} catch (Exception e) {
			logger.error("Cannot initialize LockDao", e);
			System.exit(1);
		}
	}

	public boolean getLock(String id) {
		try {
			ResultSet resultSet = this.session.execute(update.bind("locked", id, null));

			if (resultSet != null) {
				Row row = resultSet.one();

				if (row.getBoolean(0) == false) {
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
