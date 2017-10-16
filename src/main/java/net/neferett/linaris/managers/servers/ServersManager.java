package net.neferett.linaris.managers.servers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.DataBase.rabbitmq.HeartbeatSuscriber;
import net.neferett.linaris.api.Games;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.api.server.GameServer;
import redis.clients.jedis.Jedis;

public class ServersManager {

	private final GameServers						plugin;
	private ConcurrentHashMap<String, GameServer>	servers	= new ConcurrentHashMap<>();
	private final HeartbeatSuscriber				suscriber;

	public ServersManager(final GameServers plugin) throws IOException {
		this.plugin = plugin;
		this.servers = new ConcurrentHashMap<>();

		// Recover servers //
		final Jedis jedis = plugin.getConnector().getBungeeResource();
		final Map<String, String> servers = jedis.hgetAll("servers");
		jedis.close();
		for (final String server : servers.keySet()) {
			final String[] ip = servers.get(server).split(":");
			if (ip.length == 8)
				this.createServer(server, ip[0], ip[1], ip[2], ip[3], ip[4], ip[5], ip[6], ip[7]);
		}

		ProxyServer.getInstance().getScheduler().schedule(plugin, this::checkServers, 5, 5, TimeUnit.SECONDS);

		ProxyServer.getInstance().getScheduler().schedule(plugin, this::RecheckServers, 1, 1, TimeUnit.MINUTES);

		this.suscriber = new HeartbeatSuscriber(this);
	}

	void checkServers() {
		this.servers.values().stream().filter(server -> !server.isOnline()).forEach(server -> {
			ProxyServer.getInstance().getLogger()
					.severe("[Servers] Server " + server.getServName() + " detected as offline, removing.");
			this.remove(server.getServName());
			GameServers.get().getTasksManager().addTask(() -> {
				final Jedis jedis = GameServers.get().getConnector().getBungeeResource();
				jedis.hdel("servers", server.getServName());
				jedis.sadd("offlineservers", server.getServName());
				jedis.del("connectedonserv:" + server.getServName());
				jedis.close();
			});
		});
	}

	public void createServer(final String server, final String ip, final String port, final String gameName,
			final String mapName, final String maxPlayers, final String players, final String canJoin,
			final String canSee) {
		final InetSocketAddress address = new InetSocketAddress(ip, Integer.parseInt(port));

		final ServerInfo info = ProxyServer.getInstance().constructServerInfo(server, address,
				"Automatically added server", false);
		ProxyServer.getInstance().getServers().put(server, info);

		final GameServer gameServer = new GameServer(System.currentTimeMillis(), server, info);
		gameServer.setGameName(gameName);
		gameServer.setMapName(mapName);
		gameServer.setMaxPlayers(Integer.valueOf(maxPlayers));
		gameServer.setPlayers(Integer.valueOf(players));
		gameServer.setCanJoin(Boolean.valueOf(canJoin));
		gameServer.setCanSee(Boolean.valueOf(canSee));
		this.servers.put(server, gameServer);

		ProxyServer.getInstance().getLogger().info("[Servers] Created server " + server + " - " + gameName
				+ " with map " + mapName + ", " + ip + ":" + port);

		GameServers.get().getTasksManager().addTask(() -> {
			final Jedis jedis = GameServers.get().getConnector().getBungeeResource();
			jedis.srem("offlineservers", server);
			jedis.close();
		});
	}

	public void disable() {
		this.plugin.getLogger().info("[Disabling Servers Manager] Killing subscribtions...");
	}

	public GameServers getPlugin() {
		return this.plugin;
	}

	public GameServer getReconnectServerPlayer(final String player) {
		final PlayerData data = this.plugin.getPlayerDataManager().getPlayerData(player);
		final String server = data.get("reconnectserver");
		if (server == null)
			return null;
		return this.getServer(server);
	}

	public GameServer getServer(final String server) {
		return this.servers.get(server);
	}

	public GameServer getServerPlayer(final String player) {
		final PlayerData data = this.plugin.getPlayerDataManager().getPlayerData(player);
		final String server = data.get("currentserver");
		if (server == null)
			return null;
		return this.getServer(server);
	}

	public ConcurrentHashMap<String, GameServer> getServers() {
		return this.servers;
	}

	public LinkedList<GameServer> getServersByGameName(final String string) {
		final LinkedList<GameServer> servers = new LinkedList<>();
		this.servers.values().stream().filter((g) -> !g.isFake() && g.getGameName().equals(string)).forEach((g) -> {
			servers.add(g);
		});
		return servers;
	}

	public LinkedList<GameServer> getServersByGameNameAndMap(final String string, final String map) {
		final LinkedList<GameServer> servers = this.getServersByGameName(string);
		final LinkedList<GameServer> mpservers = new LinkedList<>();
		servers.stream().filter((g) -> g.getMapName().equals(map)).forEach((g) -> {
			mpservers.add(g);
		});
		return mpservers;
	}

	public Set<String> getServersName() {
		return this.servers.keySet();
	}

	public HeartbeatSuscriber getSuscriber() {
		return this.suscriber;
	}

	public void heartBeet(final String name, final String ip, final String port, final String gameName,
			final String mapName, final String maxPlayers, final String players, final String canJoin,
			final String canSee) {
		final GameServer server = this.servers.get(name);
		if (server == null || server.isFake())
			this.createServer(name, ip, port, gameName, mapName, maxPlayers, players, canJoin, canSee);
		else {
			server.heartBeet();
			server.setGameName(gameName);
			server.setMapName(mapName);
			server.setMaxPlayers(Integer.valueOf(maxPlayers));
			server.setPlayers(Integer.valueOf(players));
			server.setCanJoin(Boolean.valueOf(canJoin));
			server.setCanSee(Boolean.valueOf(canSee));
		}
	}

	public void RecheckServers() {
		GameServers.get().getTasksManager().addTask(() -> {
			final Jedis j = GameServers.get().getConnector().getBungeeResource();

			j.hgetAll("servers").entrySet().forEach((map) -> {
				if (!this.servers.containsKey(map.getKey())) {
					int i = 0;
					final List<String> serversinfo = Arrays.asList(map.getValue().split(":"));

					this.createServer(map.getKey(), serversinfo.get(i++), serversinfo.get(i++), serversinfo.get(i++),
							serversinfo.get(i++), serversinfo.get(i++), serversinfo.get(i++), serversinfo.get(i++),
							serversinfo.get(i));
				}
			});
			j.close();
		});
	}

	public String registerServer(String gameName, final String ip, final int port) {

		gameName = Games.getByDisplayName(gameName).getGameName();
		int i = 1;
		String servName = gameName + i;

		while (this.getServer(servName) != null) {
			i++;
			servName = gameName + i;
		}

		this.servers.put(servName, new GameServer());

		return servName;
	}

	public void remove(final String name) {
		ProxyServer.getInstance().getLogger()
				.info("[Servers] Removing server " + this.servers.get(name).getServName() + " - "
						+ this.servers.get(name).getGameName() + " with map " + this.servers.get(name).getMapName()
						+ ", " + this.servers.get(name).getServerInfo().getAddress().getAddress().getHostAddress() + ":"
						+ this.servers.get(name).getServerInfo().getAddress().getPort());
		this.servers.remove(name);
		if (ProxyServer.getInstance().getServers().containsKey(name))
			ProxyServer.getInstance().getServers().remove(name);
		GameServers.get().getTasksManager().addTask(() -> {
			final Jedis j = GameServers.get().getConnector().getBungeeResource();
			j.hdel("servers", name);
			j.sadd("offlineservers", name);
			j.del("connectedonserv:" + name);
			j.close();
		});
	}

}
