package net.neferett.linaris.api;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.party.PartiesManagement;
import net.neferett.linaris.api.server.GameServer;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BPlayerHandler;
import net.neferett.linaris.utils.others.RemoteQueue;
import net.neferett.linaris.utils.others.RemoteQueue.QueueType;
import net.neferett.linaris.utils.stringutils.Strings;

public class QueuesManagement {

	private final ConcurrentHashMap<String, RemoteQueue> queues;

	public QueuesManagement(final GameServers api) {
		this.queues = new ConcurrentHashMap<>();

		ProxyServer.getInstance().getScheduler().schedule(api, this::checkQueues, 500, 500, TimeUnit.MILLISECONDS);

	}

	@SuppressWarnings("deprecation")
	public void addInQueue(final ProxiedPlayer player, final Games game, String map) {

		map = map == null ? null : map;
		final String name = player.getName().toLowerCase();

		final PartiesManagement pm = GameServers.get().getPartiesManagement();

		final boolean isFirst = GameServers.get().getPlayerDataManager().getPlayerData(name).getRank()
				.getVipLevel() > 0;

		if (pm.haveParty(name)) {

			final UUID party = pm.getPlayerParty(name);

			if (pm.isLeader(name)) {

				final boolean followOn = SettingsManager.isEnabled(name, "party-follow", true);

				if (followOn) {
					final RemoteQueue queue = new RemoteQueue(name, game, map, QueueType.PARTY, isFirst);
					this.queues.put(name, queue);
					pm.sendMessageToPartyFollow(party, false,
							Strings.filesPrefix + "§b" + player.getName() + "§6 a rejoint la file : §e"
									+ queue.getGame().getDisplayName()
									+ (queue.getMapID() != null ? " §b" + queue.getMapName() : ""));
				} else {
					final RemoteQueue queue = new RemoteQueue(name, game, map, QueueType.ALONE, isFirst);
					this.queues.put(name, queue);
					player.sendMessage(
							Strings.filesPrefix + "§6Vous rejoignez la file : §e" + queue.getGame().getDisplayName()
									+ (queue.getMapID() != null ? " §b" + queue.getMapName() : ""));
				}

			} else {

				final boolean followOn = SettingsManager.isEnabled(name, "party-follow", true);

				if (followOn)
					player.sendMessage(Strings.filesPrefix
							+ "§cSeul le chef de groupe peut rejoindre une file d'attente,"
							+ " désactivez votre follow §f/p follow off §cou quittez le groupe si vous voulez rejoindre seul !");
				else {
					final RemoteQueue queue = new RemoteQueue(name, game, map, QueueType.ALONE, isFirst);
					this.queues.put(name, queue);
					player.sendMessage(
							Strings.filesPrefix + "§6Vous rejoignez la file : §e" + queue.getGame().getDisplayName()
									+ (queue.getMapID() != null ? " §b" + queue.getMapName() : ""));
				}
			}

			return;

		} else {

			final RemoteQueue queue = new RemoteQueue(name, game, map, QueueType.ALONE, isFirst);
			this.queues.put(name, queue);
			player.sendMessage(Strings.filesPrefix + "§6Vous rejoignez la file : §e" + queue.getGame().getDisplayName()
					+ (queue.getMapID() != null ? " §b" + queue.getMapName() : ""));
			return;

		}

	}

	public void checkQueue(final RemoteQueue queue) {
		final BPlayer player = BPlayerHandler.get().getPlayer(queue.getPlayerName());
		if (player == null || queue.isFinish()) {
			this.queues.remove(queue);
			return;
		}

		if (queue.getType() == QueueType.ALONE)
			this.refreshForPlayer(queue, player);
		else {

			final PartiesManagement pm = GameServers.get().getPartiesManagement();

			if (pm.haveParty(queue.getPlayerName())) {

				final UUID party = pm.getPlayerParty(queue.getPlayerName());

				if (pm.isLeader(queue.getPlayerName()))
					this.refreshForParty(queue, party);
				else {

					player.sendMessage(Strings.filesPrefix
							+ "§cSeul le chef de groupe peut rejoindre une file d'attente,"
							+ " désactivez votre follow §f/p follow off §cou quitter le groupe si vous voulez rejoindre seul !");
					queue.setFinish(true);
					this.queues.remove(queue);

				}

			}
		}

	}

	void checkQueues() {

		this.queues.values().stream()
				.filter(u -> u.isFirst() || u.getStartDate().getTime() * (1000 * 30) >= new Date().getTime())
				.forEach((queue) -> {

					this.checkQueue(queue);

				});

	}

	public ConcurrentHashMap<String, RemoteQueue> getQueues() {
		return this.queues;
	}

	@SuppressWarnings("deprecation")
	public void leftQueue(final ProxiedPlayer player, final String reason) {
		final RemoteQueue queue = this.queues.get(player.getName().toLowerCase());
		if (queue == null)
			return;
		queue.setFinish(true);
		this.queues.remove(player.getName().toLowerCase());
		if (reason != null && !reason.isEmpty())
			if (queue.getType() == QueueType.ALONE)
				player.sendMessage(Strings.filesPrefix + "§7Vous quittez la file d'attente (" + reason + ")");
			else {

				final PartiesManagement pm = GameServers.get().getPartiesManagement();

				if (pm.haveParty(player.getName().toLowerCase())) {

					final UUID party = pm.getPlayerParty(player.getName().toLowerCase());

					pm.sendMessageToPartyFollow(party, false,
							Strings.filesPrefix + "§7Vous quittez la file d'attente (" + reason + ")");

				} else
					player.sendMessage(Strings.filesPrefix + "§7Vous quittez la file d'attente (" + reason + ")");
			}
	}

	public void refreshForParty(final RemoteQueue queue, final UUID party) {

		final PartiesManagement pm = GameServers.get().getPartiesManagement();
		final List<BPlayer> partyPlayer = pm.getOnlineFollowPlayersInParty(party);
		if (partyPlayer == null) {
			queue.setFinish(true);
			this.queues.remove(queue);
			return;
		}
		if (partyPlayer.isEmpty()) {
			queue.setFinish(true);
			this.queues.remove(queue);
			return;
		}

		Supplier<Stream<GameServer>> servers;
		if (queue.getMapID() == null || queue.getMapID().isEmpty())
			servers = () -> GameServers.get().getServersManager().getServers().values().stream()
					.filter(u -> u.getPlayers() + partyPlayer.size() <= u.getMaxPlayers()
							&& u.getGameName().equals(queue.getGame().getDisplayName()) && u.canJoin() && u.canSee());
		else
			servers = () -> GameServers.get().getServersManager().getServers().values().stream()
					.filter(u -> u.getPlayers() + partyPlayer.size() <= u.getMaxPlayers()
							&& u.getGameName().equals(queue.getGame().getDisplayName())
							&& u.getMapName().equals(queue.getMapID()) && u.canJoin() && u.canSee());

		if (servers == null || servers.get().count() == 0) {

			if (queue.getLastSay().getTime() + 1000 * 20 < new Date().getTime()) {
				queue.setLastSay(new Date());
				pm.sendMessageToPartyFollow(party, false,
						Strings.filesPrefix + "§7Vous êtes en attente sur : §e" + queue.getGame().getDisplayName()
								+ (queue.getMapID() != null ? " §b" + queue.getMapName() : ""));
			}

			return;
		}

		final Comparator<GameServer> byPlayersNumber = (e1, e2) -> Integer.compare(e2.getPlayers(), e1.getPlayers());

		Supplier<Stream<GameServer>> finalServer;
		finalServer = () -> servers.get().sorted(byPlayersNumber);

		final Optional<GameServer> find = finalServer.get().findFirst();

		if (find.isPresent()) {
			final GameServer server = find.get();
			server.wantGoOn(party, partyPlayer, true);
			queue.setFinish(true);
			this.queues.remove(queue);
		}

	}

	public void refreshForPlayer(final RemoteQueue queue, final BPlayer player) {

		if (player == null) {
			queue.setFinish(true);
			this.queues.remove(queue);
			return;
		}
		Supplier<Stream<GameServer>> servers;
		if (queue.getMapID() == null || queue.getMapID().isEmpty())
			servers = () -> GameServers.get().getServersManager().getServers().values().stream()
					.filter(u -> u.getPlayers() + 1 <= u.getMaxPlayers()
							&& u.getGameName().equals(queue.getGame().getDisplayName()) && u.canJoin() && u.canSee());
		else
			servers = () -> GameServers.get().getServersManager().getServers().values().stream()
					.filter(u -> u.getPlayers() + 1 <= u.getMaxPlayers()
							&& u.getGameName().equals(queue.getGame().getDisplayName())
							&& u.getMapName().equals(queue.getMapID()) && u.canJoin() && u.canSee());
		if (servers == null || servers.get().count() == 0) {

			if (queue.getLastSay().getTime() + 1000 * 20 < new Date().getTime()) {
				queue.setLastSay(new Date());
				player.sendMessage(
						Strings.filesPrefix + "§7Vous êtes en attente sur : §e" + queue.getGame().getDisplayName()
								+ (queue.getMapID() != null ? " §b" + queue.getMapName() : ""));
			}

			return;
		}

		final Comparator<GameServer> byPlayersNumber = (e1, e2) -> Integer.compare(e2.getPlayers(), e1.getPlayers());

		Supplier<Stream<GameServer>> finalServer;
		finalServer = () -> servers.get().sorted(byPlayersNumber);

		final Optional<GameServer> find = finalServer.get().findFirst();

		if (find.isPresent()) {
			final GameServer server = find.get();
			server.wantGoOn(player, true);
			queue.setFinish(true);
			this.queues.remove(queue);
			// server.teleportToServer(player);
		}

	}
}
