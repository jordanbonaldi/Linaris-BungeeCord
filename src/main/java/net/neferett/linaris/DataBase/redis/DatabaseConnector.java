package net.neferett.linaris.DataBase.redis;

import net.neferett.linaris.GameServers;
import redis.clients.jedis.Jedis;

public abstract class DatabaseConnector {

	protected String		password;
	protected GameServers	plugin;

	public abstract void disable();

	public abstract Jedis getBanResource();

	public abstract Jedis getBungeeResource();

	public abstract Jedis getCheatResource();

	public abstract Jedis getResource();

	public abstract Jedis getStaffResource();

	public abstract Jedis getTokenResource();

	public abstract void initiateConnections() throws InterruptedException;

	public abstract void killConnections();
}
