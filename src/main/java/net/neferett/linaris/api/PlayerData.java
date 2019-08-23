package net.neferett.linaris.api;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.ranks.RankAPI;
import net.neferett.linaris.api.ranks.RankManager;
import net.neferett.linaris.api.server.GameServer;
import redis.clients.jedis.Jedis;

public class PlayerData extends PlayerDataAbstract {

	private Date				lastRefresh	= null;
	private final GameServers	plugin;

	public PlayerData(final String player, final GameServers bridge) {
		super(player);
		this.plugin = bridge;
		this.updateData();
	}

	@Override
	public boolean contains(final String key) {
		this.refreshIfNeeded();
		return super.contains(key);
	}

	@Override
	public String get(final String key) {
		this.refreshIfNeeded();
		return super.get(key);
	}

	public int getBooster() {
		if (!this.contains("booster"))
			return 0;
		return this.getInt("booster");
	}

	public long getBoosterFinish() {
		if (this.contains("boosterFinish"))
			return this.getLong("boosterFinish");
		return 0;
	}

	public double getCoins() {
		if (!this.contains("coins"))
			this.setDouble("coins", 0);
		return this.getDouble("coins");
	}

	public GameServer getCurrentServer() {

		final String key = "currentserver:" + this.playerID + ":*";
		final Jedis jedis = this.plugin.getConnector().getResource();
		final Set<String> keys = jedis.keys(key);
		if (keys.isEmpty())
			return null;

		String server = null;

		final Iterator<String> servers = keys.iterator();

		while (servers.hasNext() && server == null)
			server = servers.next();

		if (server == null)
			return null;

		server = jedis.get(server);
		jedis.close();

		return GameServers.get().getServersManager().getServer(server);
	}

	@Override
	public Set<String> getKeys() {
		this.refreshIfNeeded();
		return super.getKeys();
	}

	public RankAPI getRank() {
		if (!this.contains("rank"))
			this.set("rank", Integer.toString(1));
		return RankManager.getInstance().getRank(this.getInt("rank"));
	}

	public GameServer getReconnectServer() {

		final String key = "reconnectserver:" + this.playerID + ":*";
		final Jedis jedis = this.plugin.getConnector().getResource();
		final Set<String> keys = jedis.keys(key);
		if (keys.isEmpty())
			return null;

		String server = null;

		for (final String k : keys) {
			server = jedis.get(k);
			break;
		}

		jedis.close();

		return GameServers.get().getServersManager().getServer(server);
	}

	public double getSuperCoins() {
		if (!this.contains("legendarycoins"))
			this.setDouble("legendarycoins", 0);
		return this.getDouble("legendarycoins");
	}

	public int getTokens() {
		if (!this.contains("tokens"))
			this.setInt("tokens", 0);
		return this.getInt("tokens");
	}

	@Override
	public Map<String, String> getValues() {
		this.refreshIfNeeded();
		return super.getValues();
	}

	protected void refreshIfNeeded() {
		if (this.lastRefresh == null || this.lastRefresh.getTime() + 1000 * 60 * 5 < System.currentTimeMillis())
			GameServers.get().getTasksManager().addTask(this::updateData);
	}

	@Override
	public void remove(final String key) {
		this.playerData.remove(key);
		this.plugin.getTasksManager().addTask(() -> {
			final Jedis jedis = this.plugin.getConnector().getResource();
			jedis.hdel("player:" + this.playerID, key);
			jedis.close();
		});
	}

	public void removeReconnectServer() {
		final Jedis jedis = this.plugin.getConnector().getResource();
		final String keyBis = "reconnectserver:" + this.playerID + ":*";
		final Set<String> keys = jedis.keys(keyBis);
		for (final String k : keys)
			jedis.del(k);
		jedis.close();
	}

	@Override
	public void set(final String key, final String value) {
		this.playerData.put(key, value);

		this.plugin.getTasksManager().addTask(() -> {
			final Jedis jedis = this.plugin.getConnector().getResource();
			jedis.hset("player:" + this.playerID, key, value);
			jedis.close();
		});
	}

	@Override
	public void setBoolean(final String key, final boolean value) {
		this.set(key, String.valueOf(value));
	}

	public void setBoosterTime(final long days) {
		this.setDouble("booster", 0.25);

		if (this.contains("boosterFinish"))
			if (this.getLong("boosterFinish") == 0)
				this.set("boosterFinish", Long.toString(System.currentTimeMillis() + days * 86400000));
			else
				this.set("boosterFinish", Long.toString(this.getLong("boosterFinish") + days * 86400000));
		else
			this.set("boosterFinish", Long.toString(System.currentTimeMillis() + days * 86400000));

	}

	public void setCurrentServer(final GameServer server) {
		final String key = "currentserver:" + this.playerID + ":" + server.getServName();
		final Jedis jedis = this.plugin.getConnector().getResource();

		final String keyBis = "currentserver:" + this.playerID + ":*";
		final Set<String> keys = jedis.keys(keyBis);
		for (final String k : keys)
			jedis.del(k);

		jedis.set(key, server.getServName());
		jedis.close();
	}

	@Override
	public void setDouble(final String key, final double value) {
		this.set(key, String.valueOf(value));
	}

	@Override
	public void setInt(final String key, final int value) {
		this.set(key, String.valueOf(value));
	}

	@Override
	public void setLong(final String key, final long value) {
		this.set(key, String.valueOf(value));
	}

	public void setRank(final int rank) {
		this.set("rank", Integer.toString(rank));
	}

	public void setRank(final RankAPI rank) {
		this.set("rank", Integer.toString(rank.getId()));
		if (this.contains("rankFinish"))
			this.remove("rankFinish");
	}

	public void setRankTime(final RankAPI rank, final long days) {
		this.set("rank", Integer.toString(rank.getId()));
		if (this.contains("rankFinish"))
			if (this.getLong("rankFinish") == 0)
				this.set("rankFinish", Long.toString(System.currentTimeMillis() + days * 86400000));
			else
				this.set("rankFinish", Long.toString(this.getLong("rankFinish") + days * 86400000));
		else
			this.set("rankFinish", Long.toString(System.currentTimeMillis() + days * 86400000));
	}

	public void setReconnectServer(final GameServer server) {
		final String key = "reconnectserver:" + this.playerID + ":" + server.getServName();
		final Jedis jedis = this.plugin.getConnector().getResource();

		final String keyBis = "reconnectserver:" + this.playerID + ":*";
		final Set<String> keys = jedis.keys(keyBis);
		for (final String k : keys)
			jedis.del(k);

		jedis.set(key, server.getServName());
		jedis.close();
	}

	public void updateData() {
		final Jedis jedis = this.plugin.getConnector().getResource();
		final Map<String, String> data = jedis.hgetAll("player:" + this.playerID);
		jedis.close();
		this.playerData = data;
		this.lastRefresh = new Date();
		if (this.contains("boosterFinish"))
			if (System.currentTimeMillis() > this.getLong("boosterFinish")) {
				this.remove("boosterFinish");
				this.remove("booster");
			}
		if (this.contains("rankFinish"))
			if (System.currentTimeMillis() > this.getLong("rankFinish")) {
				this.remove("rankFinish");
				this.setInt("rank", 0);
			}
	}

}
