package net.neferett.linaris.api;

import java.util.concurrent.ConcurrentHashMap;

import net.neferett.linaris.GameServers;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by zyuiop
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class PlayerDataManager {

	protected ConcurrentHashMap<String, PlayerData> cachedData = new ConcurrentHashMap<>();
	private final GameServers GameServers;

	public PlayerDataManager(GameServers GameServers) {
		this.GameServers = GameServers;
	}


	public PlayerData getPlayerData(String player) {
		return getPlayerData(player, false);
	}

	public PlayerData getPlayerData(String player, boolean forceRefresh) {
		player = player.toLowerCase();
		if (!cachedData.containsKey(player)) {
			PlayerData data = new PlayerData(player, GameServers);
			cachedData.put(player, data);
			return data;
		}

		PlayerData data = cachedData.get(player);

		if (forceRefresh) {
			data.updateData();
			return data;
		}

		data.refreshIfNeeded();
		return data;
	}

	public void update(String player) {
		player = player.toLowerCase();
		if (!cachedData.containsKey(player)) {
			PlayerData data = new PlayerData(player, GameServers);
			cachedData.put(player, data);
			return;
		}

		PlayerData data = cachedData.get(player);
		data.updateData();
	}

	public void unload(String player) {
		player = player.toLowerCase();
		cachedData.remove(player);
	}
}
