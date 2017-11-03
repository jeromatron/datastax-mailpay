create keyspace if not exists datastax_mailpay WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1' };

use datastax_mailpay;

create table  if not exists lock(
    id text PRIMARY KEY,
    lock text
);

create table  if not exists lock_mem(
    id text PRIMARY KEY,
    lock text
) WITH compaction= { 'class': 'MemoryOnlyStrategy' }
     AND compression = {'sstable_compression' : ''}
     AND default_time_to_live = 60
	 AND gc_grace_seconds = 300
     AND caching = {'keys':'NONE', 'rows_per_partition':'NONE'};

create table if not exists transactions(
	account text, 
	transaction_time time, 
	transaction_id text,
	other_account text,
	reference text, 
	amount double,
	PRIMARY KEY(account, transaction_time)
) WITH CLUSTERING ORDER BY (transaction_time desc);


