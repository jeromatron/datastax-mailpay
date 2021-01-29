package com.datastax.utils;

public interface KillableRunner extends Runnable {
	void shutdown();
}
