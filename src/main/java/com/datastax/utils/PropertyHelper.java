package com.datastax.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

public class PropertyHelper {
	private static final Logger logger = LoggerFactory.getLogger(PropertyHelper.class);

	private static final String DEFAULT_KEYSPACE_NAME = "datastax_mailpay";
	private static final String DEFAULT_LOCAL_DATACENTER = "Cassandra";
	private static final String DEFAULT_CONTACT_POINT = "127.0.0.1";
	private static final String DEFAULT_CQL_PORT = "9042";
	private static final String DEFAULT_REPLICATION_FACTOR = "1";

	public static String getProperty(String name, String defaultValue){
		return System.getProperty(name) == null ? defaultValue : System.getProperty(name); 
	}

	public static Collection<InetSocketAddress> getContactPoints() {
		String contactPointsStr = getProperty("contactPoints", DEFAULT_CONTACT_POINT);
		Collection<InetSocketAddress> contactPoints = new ArrayList<>();
		for (String contactPoint: contactPointsStr.split(",")) {
			contactPoints.add(InetSocketAddress.createUnresolved(contactPoint, Integer.parseInt(getProperty("port", DEFAULT_CQL_PORT))));
		}
		return contactPoints;
	}

	public static String getLocalDatacenter() {
		return getProperty("localDatacenter", DEFAULT_LOCAL_DATACENTER);
	}

	public static String getKeyspaceName() {
		return getProperty("keyspaceName", DEFAULT_KEYSPACE_NAME);
	}

	/**
	 * the `replication` option can be one of the following
	 * - a single global replication factor across the cluster (SimpleStrategy), e.g. "1" or "3"
	 * - the replication factor for each data center (NetworkTopologyStrategy), e.g. "dc1,3", "dc1,3,dc2,3"
	 * @return
	 * @throws Exception
	 */
	public static String getReplicationOptions() throws Exception {
		StringBuilder replicationOptions = new StringBuilder("{'class': ");
		String replicationStr = getProperty("replication", DEFAULT_REPLICATION_FACTOR);
		String [] replicationArray = replicationStr.split(",");
		// If the length of the array is 1, it's a SimpleStrategy with a global replication factor
		if (replicationArray.length == 1) {
			replicationOptions.append("'SimpleStrategy', 'replication_factor': ").append(replicationArray[0]).append('}');
		} else {
			// If the length is more than 1, it's NetworkTopologyStrategy and expect a list of DC/RF pairs, e.g. dc1,3,dc2,3
			replicationOptions.append("'NetworkTopologyStrategy'");
			if ((replicationArray.length % 2) == 0) {
				for (int i = 0; i < replicationArray.length; i++) {
					if ((i % 2) == 0) {
						replicationOptions.append(", '").append(replicationArray[i]).append("': ");
					} else {
						replicationOptions.append(replicationArray[i]);
					}
				}
				replicationOptions.append('}');
			} else {
				throw new Exception("Make sure replication settings come in pairs, e.g. dc1,3,dc2,3");
			}
		}

		logger.info("replicationOptions: " + replicationOptions.toString());
		return replicationOptions.toString();
	}
}
