package com.datastax.utils;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SchemaUtil {
    private static final Logger logger = LoggerFactory.getLogger(SchemaUtil.class);

    CqlSession session;

    public SchemaUtil() throws Exception {
        session = CqlSession.builder()
                .addContactPoints(PropertyHelper.getContactPoints())
                .withLocalDatacenter(PropertyHelper.getLocalDatacenter())
                .build();
    }

    void run(String cql) {
        logger.info("Running : " + System.lineSeparator() + cql);
        session.execute(SimpleStatement.builder(cql).setExecutionProfileName("schema_operations").build());
        logger.info("Schema in agreement after the change? " + session.checkSchemaAgreement());
    }

    void shutdown() {
        if (session != null) session.close();
    }
}
