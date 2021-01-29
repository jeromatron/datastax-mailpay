package com.datastax.demo;

import com.datastax.demo.utils.PropertyHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemaSetup extends SchemaUtil {
	private static final Logger logger = LoggerFactory.getLogger(SchemaSetup.class);

	public SchemaSetup() throws Exception {
		super();
	}

	public static void main(String... args) {
		SchemaSetup setup = null;
		try {
			setup = new SchemaSetup();

			String createKeyspace = "CREATE KEYSPACE IF NOT EXISTS $keyspace WITH replication = $replicationOptions;"
					.replace("$keyspace", PropertyHelper.getKeyspaceName())
					.replace("$replicationOptions", PropertyHelper.getReplicationOptions());
			setup.run(createKeyspace);

			String createLockTable = """
						CREATE TABLE IF NOT EXISTS $keyspace.lock (
							id text PRIMARY KEY,
							lock text
						);
					""".replace("$keyspace", PropertyHelper.getKeyspaceName());
			setup.run(createLockTable);

			String createLockMemTable = """
						CREATE TABLE IF NOT EXISTS $keyspace.lock_mem (
							id text PRIMARY KEY,
							lock text
						) WITH compaction= { 'class': 'MemoryOnlyStrategy' }
							 AND compression = {'sstable_compression' : ''}
							 AND default_time_to_live = 2
							 AND gc_grace_seconds = 300
							 AND caching = {'keys':'NONE', 'rows_per_partition':'NONE'};
					""".replace("$keyspace", PropertyHelper.getKeyspaceName());
			setup.run(createLockMemTable);

			String createTransactionsTable = """
						CREATE TABLE IF NOT EXISTS $keyspace.transactions (
							account text,
							transaction_time timestamp,
							transaction_id text,
							other_account text,
							reference text,
							amount double,
							PRIMARY KEY(account, transaction_time)
						) WITH CLUSTERING ORDER BY (transaction_time desc);
					""".replace("$keyspace", PropertyHelper.getKeyspaceName());
			setup.run(createTransactionsTable);

			String createTransactionStateTable = """
						CREATE TABLE IF NOT EXISTS $keyspace.transaction_state (
							account1 text,
							account2 text,
							status text,
							transaction_id text,
							transaction_time timestamp,
							reference text,
							amount double,
							transaction_error text,
							PRIMARY KEY ((account1, account2, transaction_id))
						);
					""".replace("$keyspace", PropertyHelper.getKeyspaceName());
			setup.run(createTransactionStateTable);

			String createTransactionStateStatusIndex = """
						CREATE CUSTOM INDEX IF NOT EXISTS transaction_state_status_sai_idx ON
						$keyspace.transaction_state (status)
						USING 'StorageAttachedIndex'
						WITH OPTIONS = {'case_sensitive': 'false', 'normalize': 'true', 'ascii': 'true'};
					""".replace("$keyspace", PropertyHelper.getKeyspaceName());
			setup.run(createTransactionStateStatusIndex);

			String createTransactionStateTransactionTimeIndex = """
						CREATE CUSTOM INDEX IF NOT EXISTS transaction_state_transaction_time_sai_idx ON
						$keyspace.transaction_state (transaction_time)
						USING 'StorageAttachedIndex';
					""".replace("$keyspace", PropertyHelper.getKeyspaceName());
			setup.run(createTransactionStateTransactionTimeIndex);

			String createTransactionStateAmountIndex = """
						CREATE CUSTOM INDEX IF NOT EXISTS transaction_state_amount_sai_idx ON
						$keyspace.transaction_state (amount)
						USING 'StorageAttachedIndex';
					""".replace("$keyspace", PropertyHelper.getKeyspaceName());
			setup.run(createTransactionStateAmountIndex);

		} catch (Exception e) {
			logger.error("Cannot complete SchemaSetup", e);
			System.exit(1);
		} finally {
			if (setup != null) setup.shutdown();
		}
	}
}
