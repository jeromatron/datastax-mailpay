create keyspace if not exists datastax_locking WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1' };

use datastax_locking;

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
