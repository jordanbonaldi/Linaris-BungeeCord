package net.neferett.linaris.api.server;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.managers.player.BPlayer;
import redis.clients.jedis.Jedis;

public class GameServer implements Comparable<GameServer> {

	private boolean	canJoin;

	private boolean	canSee;
	private boolean	fakeServer;
	private String	gameName;

	private Long	lastHeartbeet;
	private String	mapName;

	private int		maxPlayers;
	private int		players;

	private String	servName;

	public GameServer() {
		this.fakeServer = true;
	}

	public GameServer(final long lastHeartbeet, final String server, final ServerInfo infos) {
		this.lastHeartbeet = lastHeartbeet;
		this.servName = server;
	}

	public boolean canJoin() {
		return this.canJoin;
	}

	public boolean canSee() {
		return this.canSee;
	}

	@Override
	public int compareTo(final GameServer o) {
		return this.getPlayers() - o.getPlayers();
	}

	public Predicate<Long> CreateTestCoolDown(final long time) {
		return past -> this.getTimeLeft(past, time) > 0;
	}

	public String getGameName() {
		return this.gameName;
	}

	public Long getLastHeartbeet() {
		return this.lastHeartbeet;
	}

	public String getMapID() {
		return this.mapName;
	}

	public String getMapName() {
		return this.mapName.replace("_", " ");
	}

	public int getMaxPlayers() {
		return this.maxPlayers;
	}

	public int getPlayers() {
		return this.players;
	}

	public Set<String> getPlayersOnline() {
		final Jedis jedis = GameServers.get().getConnector().getBungeeResource();
		final Set<String> list = jedis.smembers("connectedonserv:" + this.servName);
		jedis.close();

		return list;
	}

	/**
	 *
	 * Teleport System
	 *
	 */

	public ServerInfo getServerInfo() {
		return GameServers.get().getProxy().getServerInfo(this.getServName());
	}

	public String getServName() {
		return this.servName;
	}

	public Long getTimeLeft(final Long past, final long time) {
		return past / 1000 + time - System.currentTimeMillis() / 1000;
	}

	public void heartBeet() {
		this.lastHeartbeet = System.currentTimeMillis();
	}

	public boolean isFake() {
		return this.fakeServer;
	}

	public boolean isOnline() {
		return this.CreateTestCoolDown(30).test(this.lastHeartbeet);
	}

	public void reconnect(final BPlayer p, final String... args) {

		if (this.getMaxPlayers() <= this.getPlayers() + 1)
			return;

		ProxyServer.getInstance().getScheduler().runAsync(GameServers.get(), () -> {

			try {

				new SoloConnectionRequest(p, this, args, (server, status, player) -> {

					final PlayerData data = GameServers.get().getPlayerDataManager().getPlayerData(player.getName());
					data.removeReconnectServer();

					if (status == ConnectionStatus.ALLOW) {

						final ServerInfo info = this.getServerInfo();

						player.callBackConnect(info, (aBoolean, throwable) -> {

							if (aBoolean) {
								this.players++;
								data.setCurrentServer(this);

								player.sendMessage("§7Vous rejoignez:§e " + this.getServName());
								player.sendMessage(
										"§6§nReconnexion automatique dans votre partie:§b " + this.getServName());
							}

						});

					} else
						player.connect("Lobby");
				});

			} catch (final Exception e) {
				e.printStackTrace();
				return;
			}

		});

		return;

	}

	public void setCanJoin(final boolean canJoin) {
		this.canJoin = canJoin;
	}

	public void setCanSee(final boolean canSee) {
		this.canSee = canSee;
	}

	public void setGameName(final String name) {
		this.gameName = name;
	}

	public void setLastHeartbeet(final long lastHeartbeet) {
		this.lastHeartbeet = lastHeartbeet;
	}

	public void setMapName(final String name) {
		this.mapName = name;
	}

	public void setMaxPlayers(final int players) {
		this.maxPlayers = players;
	}

	public void setPlayers(final int players) {
		this.players = players;
	}

	private boolean canJoinAndSee(BPlayer p, SoloConnectionServerCallBack callback) {
		if (!this.canJoin) {
			if (callback != null)
				callback.done(this, ConnectionStatus.DENY, p);
			return true;
		}

		if (!this.canSee) {
			if (callback != null)
				callback.done(this, ConnectionStatus.DENY, p);
			return true;
		}

		return false;
	}

	public void wantGoOn(final BPlayer p, final boolean useInfos, final SoloConnectionServerCallBack callback,
			final String... args) {

		if (this.getMaxPlayers() <= this.getPlayers() + 1) {
			if (callback != null)
				callback.done(this, ConnectionStatus.DENY, p);
			return;
		}

		if (useInfos && this.canJoinAndSee(p, callback))
			return;

		ProxyServer.getInstance().getScheduler().runAsync(GameServers.get(), () -> {

			try {

				new SoloConnectionRequest(p, this, args, (server, status, player) -> {

					if (status == ConnectionStatus.ALLOW) {

						final ServerInfo info = this.getServerInfo();

						player.callBackConnect(info, (aBoolean, throwable) -> {

							System.out.println(aBoolean);

//							if (aBoolean) {
								this.players++;
								final PlayerData data = GameServers.get().getPlayerDataManager()
										.getPlayerData(player.getName());
								data.setCurrentServer(this);
//							}

						});

					}

					if (callback != null)
						callback.done(server, status, player);

				});

			} catch (final Exception e) {
				e.printStackTrace();
			}

		});
	}

	public void wantGoOn(final BPlayer p, final boolean useInfos, final String... args) {
		this.wantGoOn(p, useInfos, null, args);
	}

	public void wantGoOn(final UUID party, final List<BPlayer> pls, final boolean useInfos,
			final MultiConnectionServerCallBack callback, final String... args) {

		if (this.getMaxPlayers() <= this.getPlayers() + pls.size()) {
			if (callback != null)
				callback.done(this, ConnectionStatus.DENY, pls);
			return;
		}

		if (useInfos) {
			if (!this.canJoin) {
				if (callback != null)
					callback.done(this, ConnectionStatus.DENY, pls);
				return;
			}
			if (!this.canSee) {
				if (callback != null)
					callback.done(this, ConnectionStatus.DENY, pls);
				return;
			}
		}
		ProxyServer.getInstance().getScheduler().runAsync(GameServers.get(), () -> {

			try {
				new PartyConnectionRequest(party, pls, this, args, (server, status, players) -> {

					if (status == ConnectionStatus.ALLOW) {

						final ServerInfo info = this.getServerInfo();

						for (final BPlayer target : players)
							target.callBackConnect(info, (aBoolean, throwable) -> {

								if (aBoolean) {
									this.players++;
									final PlayerData data = GameServers.get().getPlayerDataManager()
											.getPlayerData(target.getName());
									data.setCurrentServer(this);

									target.sendMessage("§7Vous rejoignez:§e " + this.getServName());

								}

							});

					}

					if (callback != null)
						callback.done(server, status, players);

				});

			} catch (final Exception e) {
				e.printStackTrace();
				return;
			}

		});

		return;

	}

	public void wantGoOn(final UUID party, final List<BPlayer> pls, final boolean useInfos, final String... args) {
		this.wantGoOn(party, pls, useInfos, null, args);
	}

	public void wantGoWithGhost(final BPlayer p, final String player) {

		ProxyServer.getInstance().getScheduler().runAsync(GameServers.get(), () -> {

			try {

				new SoloConnectionRequest(p, this, new String[] { "invisible", player }, (server, status, players) -> {

					if (status == ConnectionStatus.ALLOW) {

						final ServerInfo info = this.getServerInfo();

						p.callBackConnect(info, (aBoolean, throwable) -> {

							if (aBoolean) {
								this.players++;
								final PlayerData data = GameServers.get().getPlayerDataManager()
										.getPlayerData(p.getName());
								data.setCurrentServer(this);

								p.sendMessage("§7Mode Ghost:§e " + this.getServName());

								data.setBoolean("invisible", true);
								data.updateData();

							}

							if (throwable != null)
								throwable.printStackTrace();

						});

					}

				});

			} catch (final Exception e) {
				e.printStackTrace();
				return;
			}

		});

		return;

	}

}
