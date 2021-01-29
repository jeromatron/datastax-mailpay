package com.datastax.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemaTeardown extends SchemaUtil {
	private static final Logger logger = LoggerFactory.getLogger(SchemaTeardown.class);

	SchemaTeardown() throws Exception {
		super();
	}

	public static void main(String... args) {
		SchemaTeardown teardown = null;
		try {
			teardown = new SchemaTeardown();
			String dropKeyspace = "DROP KEYSPACE $keyspace".replace("$keyspace", PropertyHelper.getKeyspaceName());
			teardown.run(dropKeyspace);
		} catch (Exception e) {
			logger.error("Cannot complete teardown", e);
			System.exit(1);
		} finally {
			if (teardown != null) teardown.shutdown();
		}
	}
}