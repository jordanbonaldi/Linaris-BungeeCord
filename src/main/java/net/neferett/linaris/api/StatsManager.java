package net.neferett.linaris.api;

import java.util.HashMap;
import java.util.Set;

import net.neferett.linaris.GameServers;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

public class StatsManager {

	GameServers api;
	
	public StatsManager(GameServers api) {
		this.api = api;
	}

	public void increase(final String player, final Games game,final String stat, final double amount) {
		api.getProxy().getScheduler().runAsync(api, () -> {
			Jedis j = api.getConnector().getResource();
			j.zincrby("gamestats:" + game.getID() + ":" + stat, amount, player);
			j.close();
		});
	}

	public void setValue(String player, final Games game,String stat, double value) {
		api.getProxy().getScheduler().runAsync(api, () -> {
			Jedis j = api.getConnector().getResource();
			j.zadd("gamestats:" + game.getID() + ":" + stat, value, player);
			j.close();
		});
	}

	public double getStatValue(String player, final Games game, String stat) {
		
		Jedis j = api.getConnector().getResource();
		double value = j.zscore("gamestats:"+game.getID()+":"+stat, player);
		j.close();

		return value;
	}
	
	public HashMap<String,Double> getStatsValue(final Games game, String stat,int topNumber) {
		HashMap<String,Double> scores = new HashMap<String,Double>();
		
		Jedis j = api.getConnector().getResource();
		Set<Tuple> players = j.zrevrangeWithScores("gamestats:"+game.getID()+":"+stat, 0, topNumber);
		players.forEach((p) -> {
			scores.put(p.getElement(), p.getScore());
		});
		j.close();

		return scores;
	}
}
