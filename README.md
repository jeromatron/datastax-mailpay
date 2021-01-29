# Mail Pay
====================

## Scenario

This is an example application for exchanging money using email addresses. 

The process will add a transaction to both the payer and payee's ledger. 

The steps are 

1. Create an entry in the `lock` table to lock the accounts 
2. Create an entry in the `transaction_state` table with a status of `STARTED` to ensure that we have a record of what we are trying to do.
   This will act as a state machine for the transaction. The `transaction_state` table will contain a copy of the transactions so that they can be replayed.   
3. Write to both the payer's and payee's ledger. 
4. Update transaction state to `SUCCESS`.   
5. Unlock the accounts. 

If there are problems at any stage, the transactions can be replayed by the entry in the state table. 

## Contact points
To specify contact points use the contactPoints command line parameter e.g. '-DcontactPoints=192.168.25.100,192.168.25.101'
The contact points can take multiple points in the IP,IP,IP (no spaces).

## Schema setup
To create a single node cluster with replication factor of 1 for standard localhost setup, run the following

    mvn clean compile exec:java -Dexec.mainClass="com.datastax.demo.SchemaSetup"

## Running the mailpay simulation
To run the processor 

    mvn clean compile exec:java -Dexec.mainClass="com.datastax.mailpay.Main"

## Cleaning up the schema and data
To remove the tables and the schema, run the following.

    mvn clean compile exec:java -Dexec.mainClass="com.datastax.demo.SchemaTeardown"

Note : If you're running mailpay against a cluster, Cassandra will automatically flush and snapshot the data to create a backup.
To reclaim the disk space, delete the data on disk for the keyspace.