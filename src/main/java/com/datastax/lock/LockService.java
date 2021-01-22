package com.datastax.lock;

import com.datastax.lock.dao.LockDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockService {
	private static final Logger logger = LoggerFactory.getLogger( LockService.class );

	private final LockDao dao = new LockDao();

	private static final LockService lockService = new LockService();
	
	public static LockService getInstance() {
		return lockService;
	}
	
	public boolean getLock(String id){
		return dao.getLock(id);
	}

	public boolean releaseLock(String id) {
		return dao.releaseLock(id);
	}
}
