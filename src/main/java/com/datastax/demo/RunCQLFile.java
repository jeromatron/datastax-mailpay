package com.datastax.demo;

import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.demo.utils.FileUtils;
import com.datastax.oss.driver.api.core.CqlSession;

public abstract class RunCQLFile {
	private static Logger logger = LoggerFactory.getLogger(RunCQLFile.class);

	private CqlSession session;
	private String CQL_FILE;

	RunCQLFile(String cqlFile) {
		logger.info("Running file " + cqlFile);
		this.CQL_FILE = cqlFile;
		session = CqlSession.builder().build();
	}
	
	void internalSetup() {
		this.runfile();		
	}
	
	void runfile() {
		String readFileIntoString = FileUtils.readFileIntoString(CQL_FILE);
		
		String[] commands = readFileIntoString.split(";");
		
		for (String command : commands){
			String cql = command.trim();
			
			if (cql.isEmpty()){
				continue;
			}
			
			if (cql.toLowerCase().startsWith("drop")){
				this.runAllowFail(cql);
			} else {
				this.run(cql);
			}			
		}
	}

	void runAllowFail(String cql) {
		try {
			run(cql);
		} catch (InvalidQueryException e) {
			logger.warn("Ignoring exception - " + e.getMessage());
		}
	}

	void run(String cql){
		logger.info("Running : " + cql);
		SimpleStatement s = SimpleStatement.builder(cql).setExecutionProfileName("schema_operations").build();
		session.execute(s);
	}

	void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (Exception e) {
		}
	}
	
	void shutdown() {
		session.close();
	}
}
