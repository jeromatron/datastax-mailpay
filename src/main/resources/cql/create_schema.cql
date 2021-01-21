CREATE KEYSPACE IF NOT EXISTS datastax_mailpay WITH replication = {'class': 'NetworkTopologyStrategy', 'dc1' : 1};

USE datastax_mailpay;

CREATE TABLE IF NOT EXISTS lock(
    id text PRIMARY KEY,
    lock text
);

CREATE TABLE IF NOT EXISTS lock_mem(
    id text PRIMARY KEY,
    lock text
) WITH compaction= { 'class': 'MemoryOnlyStrategy' }
     AND compression = {'sstable_compression' : ''}
     AND default_time_to_live = 2
	 AND gc_grace_seconds = 300
     AND caching = {'keys':'NONE', 'rows_per_partition':'NONE'};

CREATE TABLE IF NOT EXISTS transaction_state(
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

CREATE CUSTOM INDEX IF NOT EXISTS transaction_state_status_sai_idx ON
datastax_mailpay.transaction_state (status)
USING 'StorageAttachedIndex'
WITH OPTIONS = {'case_sensitive': 'false', 'normalize': 'true', 'ascii': 'true'};

CREATE CUSTOM INDEX IF NOT EXISTS transaction_state_transaction_time_sai_idx ON
datastax_mailpay.transaction_state (transaction_time)
USING 'StorageAttachedIndex';

CREATE CUSTOM INDEX IF NOT EXISTS transaction_state_amount_sai_idx ON
datastax_mailpay.transaction_state (amount)
USING 'StorageAttachedIndex';

CREATE TABLE IF NOT EXISTS transactions(
	account text, 
	transaction_time timestamp, 
	transaction_id text,
	other_account text,
	reference text, 
	amount double,
	PRIMARY KEY(account, transaction_time)
) WITH CLUSTERING ORDER BY (transaction_time desc);