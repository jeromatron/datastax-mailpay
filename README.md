# Mail Pay
====================

## Scenario

This is an example application for exchanging money using email addresses. 

The process for will add a transaction to both the payer and payee's ledger. 

The steps involved are 

1. Create an entry in the lock table to lock the accounts 
2. Create an entry in the transaction_state table with a status if STARTED to ensure that we have a record of what we trying to do. This will act as a state machine for the transaction. The state table will contain a copy of the transactions so that they can replayed.   
3. Write to both payer and payee's ledger. 
4. Update transaction state to SUCCESS   
5. Unlock the accounts. 

If there are problems at any stage the, the transactions can either be replayed by the entry in the state table. 

## Schema Setup
Note : This will drop the keyspace "datastax_mailpay" and create a new one. All existing data will be lost. 

To specify contact points use the contactPoints command line parameter e.g. '-DcontactPoints=192.168.25.100,192.168.25.101'
The contact points can take multiple points in the IP,IP,IP (no spaces).

To create the a single node cluster with replication factor of 1 for standard localhost setup, run the following

    mvn clean compile exec:java -Dexec.mainClass="com.datastax.demo.SchemaSetup"

To run the processor 

    mvn clean compile exec:java -Dexec.mainClass="com.datastax.mailpay.Main"
		
To remove the tables and the schema, run the following.

    mvn clean compile exec:java -Dexec.mainClass="com.datastax.demo.SchemaTeardown"
    
    
    

