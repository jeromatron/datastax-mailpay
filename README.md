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
To specify cluster contact points, use the `contactPoints` command line parameter e.g. `-DcontactPoints=192.168.25.100,192.168.25.101`
The contact points can take multiple points in the IP,IP,IP (no spaces).

## Replication
`replication` defaults to `SimpleStrategy` and replication factor of 1.  You can override this by specifying replication in 1+ data center specifically.
For example, if you specify `-Dreplication=Sydney,3,Jakarta,3`, it will use the NetworkTopologyStrategy and create the keyspace with replication in both the `Sydney` and the `Jakarta` data centers.

## Local data center
`localDatacenter` defaults to `Cassandra` which is the workload specific default of DataStax Enterprise.  If you give `replication` that specifies data centers, you'll need to also specify a `localDatacenter`. 

## Keyspace name
`keyspaceName` defaults to `datastax_mailpay`.

## Schema setup
To create a single node cluster with replication factor of 1 for standard localhost setup, run the following

    mvn clean compile exec:java -Dexec.mainClass="com.datastax.utils.SchemaSetup"

### An example of using different options
See [PropertyHelper](/src/main/java/com/datastax/utils/PropertyHelper.java)

    mvn clean compile exec:java -Dexec.mainClass="com.datastax.utils.SchemaSetup" -DcontactPoints=10.101.33.84

## Running the mailpay simulation
To run the processor with defaults (local database, DC named `Cassandra`, `SimpleStrategy`, replication factor: `1`, keyspace named `datastax_mailpay`)

    mvn clean compile exec:java -Dexec.mainClass="com.datastax.mailpay.Main"

### Some examples of using different options 

    mvn clean compile exec:java -Dexec.mainClass="com.datastax.mailpay.Main" -DcontactPoints=10.101.33.84 -Dreplication=3

Above we are using the replication strategy `SimpleStrategy` with a replication factor of `3` with a remote database server.

    mvn clean compile exec:java -Dexec.mainClass="com.datastax.mailpay.Main" -DcontactPoints=10.101.33.84 -DlocalDatacenter=Sydney -Dreplication=Sydney,3,Jakarta,3 -DkeyspaceName=mailpay

Here we are using the replication strategy `NetworkTopologyStrategy` with replication in `Sydney` (`3`) and `Jakarta` (`3`) with a remote database server and an overridden keyspace name of `mailpay`.

## Cleaning up the schema and data
To remove the tables and the schema, run the following.

    mvn clean compile exec:java -Dexec.mainClass="com.datastax.utils.SchemaTeardown"

Note : If you're running `mailpay` against a cluster, Cassandra will automatically flush and snapshot the data to create a backup.
To reclaim the disk space, delete the data on disk for the keyspace.