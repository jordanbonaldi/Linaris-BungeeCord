package net.neferett.linaris.DataBase.redis;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import net.neferett.linaris.GameServers;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class SingleDatabaseConnector extends DatabaseConnector {

	protected JedisPool		cachePool;
	protected JedisPool		cachePoolBan;
	protected JedisPool		cachePoolCheat;
	protected JedisPool		cachePoolRank;
	protected JedisPool		cachePoolStaff;
	protected JedisPool		cachePoolToken;
	protected JedisPool		mainPool;
	private final String	masterIp;

	public SingleDatabaseConnector(final GameServers plugin, final String masterIp, final String password) {
		this.plugin = plugin;
		this.masterIp = masterIp;
		this.password = password;

		plugin.getLogger().info("[Database] Initializing connection.");
		try {
			this.initiateConnections();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void disable() {
		this.plugin.getLogger().info("[Disabling Connector] Removing pools...");
		this.killConnections();
	}

	@Override
	public Jedis getBanResource() {
		try {
			return this.cachePoolBan.getResource();
		} catch (final Exception e) {

			final GenericObjectPoolConfig config = new GenericObjectPoolConfig();
			config.setMaxTotal(20);
			config.setMinIdle(5);
			config.setMaxIdle(10);
			config.setMaxWaitMillis(200L);
			config.setBlockWhenExhausted(false);

			final String[] mainParts = StringUtils.split(this.masterIp, ":");
			final int mainPort = mainParts.length > 1 ? Integer.decode(mainParts[1]) : 6379;
			this.cachePoolBan = new JedisPool(config, mainParts[0], mainPort, 5000, this.password, 6);

			return this.cachePoolBan.getResource();
		}
	}

	@Override
	public Jedis getBungeeResource() {
		try {
			return this.cachePool.getResource();
		} catch (final Exception e) {

			final GenericObjectPoolConfig config = new GenericObjectPoolConfig();
			config.setMaxTotal(20);
			config.setMinIdle(5);
			config.setMaxIdle(10);
			config.setMaxWaitMillis(200L);
			config.setBlockWhenExhausted(false);

			final String[] mainParts = StringUtils.split(this.masterIp, ":");
			final int mainPort = mainParts.length > 1 ? Integer.decode(mainParts[1]) : 6379;
			this.cachePool = new JedisPool(config, mainParts[0], mainPort, 5000, this.password, 1);

			return this.cachePool.getResource();
		}
	}

	@Override
	public Jedis getCheatResource() {
		try {
			return this.cachePoolCheat.getResource();
		} catch (final Exception e) {

			final GenericObjectPoolConfig config = new GenericObjectPoolConfig();
			config.setMaxTotal(20);
			config.setMinIdle(5);
			config.setMaxIdle(10);
			config.setMaxWaitMillis(200L);
			config.setBlockWhenExhausted(false);

			final String[] mainParts = StringUtils.split(this.masterIp, ":");
			final int mainPort = mainParts.length > 1 ? Integer.decode(mainParts[1]) : 6379;
			this.cachePoolCheat = new JedisPool(config, mainParts[0], mainPort, 5000, this.password, 5);

			return this.cachePoolCheat.getResource();
		}
	}

	@Override
	public Jedis getRank() {
		try {
			return this.cachePoolRank.getResource();
		} catch (final Exception e) {

			final GenericObjectPoolConfig config = new GenericObjectPoolConfig();
			config.setMaxTotal(20);
			config.setMinIdle(5);
			config.setMaxIdle(10);
			config.setMaxWaitMillis(200L);
			config.setBlockWhenExhausted(false);

			final String[] mainParts = StringUtils.split(this.masterIp, ":");
			final int mainPort = mainParts.length > 1 ? Integer.decode(mainParts[1]) : 6379;
			this.cachePoolRank = new JedisPool(config, mainParts[0], mainPort, 5000, this.password, 8);

			return this.cachePoolRank.getResource();
		}
	}

	@Override
	public Jedis getResource() {
		try {
			return this.mainPool.getResource();
		} catch (final Exception e) {

			final GenericObjectPoolConfig config = new GenericObjectPoolConfig();
			config.setMaxTotal(20);
			config.setMinIdle(5);
			config.setMaxIdle(10);
			config.setMaxWaitMillis(200L);
			config.setBlockWhenExhausted(false);

			final String[] mainParts = StringUtils.split(this.masterIp, ":");
			final int mainPort = mainParts.length > 1 ? Integer.decode(mainParts[1]) : 6379;
			this.mainPool = new JedisPool(config, mainParts[0], mainPort, 5000, this.password);

			return this.mainPool.getResource();
		}
	}

	@Override
	public Jedis getStaffResource() {
		try {
			return this.cachePoolStaff.getResource();
		} catch (final Exception e) {

			final GenericObjectPoolConfig config = new GenericObjectPoolConfig();
			config.setMaxTotal(20);
			config.setMinIdle(5);
			config.setMaxIdle(10);
			config.setMaxWaitMillis(200L);
			config.setBlockWhenExhausted(false);

			final String[] mainParts = StringUtils.split(this.masterIp, ":");
			final int mainPort = mainParts.length > 1 ? Integer.decode(mainParts[1]) : 6379;
			this.cachePoolStaff = new JedisPool(config, mainParts[0], mainPort, 5000, this.password, 3);

			return this.cachePoolStaff.getResource();
		}
	}

	@Override
	public Jedis getTokenResource() {
		try {
			return this.cachePoolToken.getResource();
		} catch (final Exception e) {

			final GenericObjectPoolConfig config = new GenericObjectPoolConfig();
			config.setMaxTotal(20);
			config.setMinIdle(5);
			config.setMaxIdle(10);
			config.setMaxWaitMillis(200L);
			config.setBlockWhenExhausted(false);

			final String[] mainParts = StringUtils.split(this.masterIp, ":");
			final int mainPort = mainParts.length > 1 ? Integer.decode(mainParts[1]) : 6379;
			this.cachePoolToken = new JedisPool(config, mainParts[0], mainPort, 5000, this.password, 2);

			return this.cachePoolToken.getResource();
		}
	}

	@Override
	public void initiateConnections() throws InterruptedException {
		final GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxTotal(20);
		config.setMinIdle(5);
		config.setMaxIdle(10);
		config.setMaxWaitMillis(200L);
		config.setBlockWhenExhausted(false);

		final String[] mainParts = StringUtils.split(this.masterIp, ":");
		final int mainPort = mainParts.length > 1 ? Integer.decode(mainParts[1]) : 6379;

		this.mainPool = new JedisPool(config, mainParts[0], mainPort, 5000, this.password);

		this.cachePool = new JedisPool(config, mainParts[0], mainPort, 5000, this.password, 1);

		this.cachePoolToken = new JedisPool(config, mainParts[0], mainPort, 5000, this.password, 2);

		this.cachePoolStaff = new JedisPool(config, mainParts[0], mainPort, 5000, this.password, 3);

		this.cachePoolCheat = new JedisPool(config, mainParts[0], mainPort, 5000, this.password, 5);

		this.cachePoolBan = new JedisPool(config, mainParts[0], mainPort, 5000, this.password, 6);

		this.cachePoolRank = new JedisPool(config, mainParts[0], mainPort, 5000, this.password, 8);

		this.plugin.getLogger().info("[Database] Connection initialized.");

	}

	@Override
	public void killConnections() {
		this.mainPool.destroy();
		this.cachePool.destroy();
		this.cachePoolToken.destroy();
		this.cachePoolStaff.destroy();
		this.cachePoolCheat.destroy();
		this.cachePoolBan.destroy();
		this.cachePoolRank.destroy();
	}

}
