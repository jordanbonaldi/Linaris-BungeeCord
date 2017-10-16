package net.neferett.linaris.DataBase;

import java.util.HashMap;
import java.util.Map;

import net.neferett.linaris.GameServers;
import redis.clients.jedis.Jedis;

public abstract class RedisAbstract {

	public interface Connector {
		public Jedis getConnector();
	}

	Connector						c;
	protected Map<String, String>	cache	= new HashMap<>();
	String							dbtype;

	protected String				name;

	public RedisAbstract(final String db, final String name, final Connector c) {
		this.name = name.toLowerCase();
		this.c = c;
		this.dbtype = db;
		this.refresh();
	}

	public boolean contains(final String key) {
		return this.cache.containsKey(key);
	}

	public String get(final String key) {
		if (!this.cache.containsKey(key))
			return null;
		return this.cache.get(key);
	}

	public Map<String, String> getAll() {
		return this.cache;
	}

	public int getInt(final String key) {
		if (this.cache.get(key) == null)
			return 0;
		return Integer.parseInt(this.cache.get(key));
	}

	public void refresh() {
		final Jedis j = this.c.getConnector();
		this.cache = j.hgetAll(this.dbtype + ":" + this.name);
		j.close();
	}

	public void remove() {
		GameServers.get().getTasksManager().addTask(() -> {
			final Jedis j = this.c.getConnector();
			j.del(this.dbtype + ":" + this.name);
			j.close();
			this.refresh();
		});
	}

	public void removeKey(final String key) {
		GameServers.get().getTasksManager().addTask(() -> {
			final Jedis j = this.c.getConnector();
			j.hdel(this.dbtype + ":" + this.name, key);
			j.close();
			this.refresh();
		});
	}

	public String set(final String key, final String value) {
		GameServers.get().getTasksManager().addTask(() -> {
			final Jedis j = this.c.getConnector();
			j.hset(this.dbtype + ":" + this.name, key, value);
			j.close();
			this.refresh();
		});
		return value;
	}

	public int setInt(final String key, final int a) {
		return Integer.parseInt(this.set(key, String.valueOf(a)));
	}

}
