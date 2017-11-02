package com.datastax.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.demo.utils.PropertyHelper;
import com.datastax.lock.dao.LockDao;

public class LockService {
	
	private LockDao dao = new LockDao(PropertyHelper.getProperty("contactPoints", "localhost").split(","));
	private static Logger logger = LoggerFactory.getLogger( LockService.class );

	private static LockService lockService = new LockService();
	
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
