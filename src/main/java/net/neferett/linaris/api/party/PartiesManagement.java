package net.neferett.linaris.api.party;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.api.SettingsManager;
import net.neferett.linaris.api.server.ConnectionStatus;
import net.neferett.linaris.api.server.GameServer;
import net.neferett.linaris.commands.player.PartiesCommand;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BPlayerHandler;
import net.neferett.linaris.utils.stringutils.Strings;
import redis.clients.jedis.Jedis;

public class PartiesManagement {

	private final GameServers api;

	public PartiesManagement(final GameServers api) {
		this.api = api;
		api.getProxy().getPluginManager().registerCommand(api, new PartiesCommand(this, api));
	}

	/**
	 * Ajoute un joueur dans une party ( d§finie le current party et l'ajoute
	 * dans la liste )
	 *
	 * @param party
	 * @param player
	 */
	public void addPlayerInParty(final UUID party, final String player) {
		final Jedis jedis = this.api.getConnector().getResource();
		jedis.set("currentparty:" + player, party.toString());
		jedis.lpush("party:" + party + ":members", player);
		jedis.close();
	}

	/**
	 * Cr§§ une party et d§finir le leader
	 *
	 * @param partyid
	 * @param player
	 */
	public void createParty(final UUID partyid, final String player) {
		this.setLeader(partyid, player);
		this.addPlayerInParty(partyid, player);
		final PlayerData pd = this.api.getPlayerDataManager().getPlayerData(player);
		this.setCurrentServer(partyid, pd.getCurrentServer());
	}

	/**
	 * Disband la party d'un joueur
	 *
	 * @param p
	 */
	public void disbandParty(final BPlayer p) {
		final String from = p.getName().toLowerCase();
		if (!this.haveParty(from)) {
			p.sendMessage("§cVous n'êtes pas dans un groupe !");
			return;
		}
		if (!this.isLeader(from)) {
			p.sendMessage("§cVous n'êtes pas le chef du groupe !");
			return;
		}
		this.disbandParty(this.getPlayerParty(from));
	}

	/**
	 * Disband une partie
	 *
	 * @param partyid
	 */
	@SuppressWarnings("deprecation")
	public void disbandParty(final UUID party) {
		if (!this.exist(party))
			return;
		final String dbKeyRequest = "partyrequests:" + party + ":*";
		final String dbKey = "party:" + party + ":*";
		final Jedis jedis = this.api.getConnector().getResource();
		for (final String data : jedis.keys(dbKeyRequest))
			jedis.del(data);
		for (final String name : this.getPlayersInParty(party)) {
			this.removePlayerInParty(party, name);
			final ProxiedPlayer p = this.api.getProxy().getPlayer(name);
			if (p != null)
				p.sendMessage("§bDissolution (" + this.getLeader(party) + " §ea dissous le groupe§b)");
		}
		for (final String data : jedis.keys(dbKey))
			jedis.del(data);
		jedis.close();
	}

	/**
	 * Savoir si une party existe ou non
	 *
	 * @param party
	 * @return
	 */
	public boolean exist(final UUID party) {
		final Jedis jedis = this.api.getConnector().getResource();
		final boolean leader = jedis.exists("party:" + party + ":lead");
		jedis.close();
		return leader;
	}

	/**
	 * Donne le serveur actuel de la party ou le serveur du leader
	 *
	 * @param party
	 * @return
	 */
	public String getCurrentServer(final UUID party) {
		final Jedis jedis = this.api.getConnector().getResource();
		final String server = jedis.get("party:" + party + ":server");
		jedis.close();
		return server;
	}

	public boolean getFollow(String player) {

		player = player.toLowerCase();

		return SettingsManager.isEnabled(player, "party-follow", true);

	}

	/**
	 * Get le leader d'une party
	 *
	 * @param party
	 * @return
	 */
	public String getLeader(final UUID party) {
		final Jedis jedis = this.api.getConnector().getResource();
		final String leader = jedis.get("party:" + party + ":lead");
		jedis.close();
		return leader;
	}

	/**
	 * Donne la liste de tous les membres d'une party ( en ligne )
	 *
	 * @param party
	 * @return
	 */
	public ArrayList<BPlayer> getOnlineFollowPlayersInParty(final UUID party) {

		final ArrayList<BPlayer> playerIDs = new ArrayList<>();

		final Jedis jedis = this.api.getConnector().getResource();
		for (final String data : jedis.lrange("party:" + party + ":members", 0, -1)) {
			if (data == null || data.equals("")) {
				jedis.lrem("party:" + party + ":members", 0, data);
				continue;
			}

			final BPlayer p = BPlayerHandler.get().getPlayer(data);
			if (p != null)
				if (this.getFollow(data))
					playerIDs.add(p);

		}
		jedis.close();
		return playerIDs;
	}

	/**
	 * Donne la liste de tous les membres d'une party ( en ligne )
	 *
	 * @param party
	 * @return
	 */
	public ArrayList<ProxiedPlayer> getOnlinePlayersInParty(final UUID party) {

		final ArrayList<ProxiedPlayer> playerIDs = new ArrayList<>();

		final Jedis jedis = this.api.getConnector().getResource();
		for (final String data : jedis.lrange("party:" + party + ":members", 0, -1)) {
			if (data == null || data.equals("")) {
				jedis.lrem("party:" + party + ":members", 0, data);
				continue;
			}

			final String id = data;

			final ProxiedPlayer p = this.api.getProxy().getPlayer(id);
			if (p != null)
				playerIDs.add(p);

		}
		jedis.close();
		return playerIDs;
	}

	/**
	 * Retourne la party d'un joueur
	 *
	 * @param player
	 * @return
	 */
	public UUID getPlayerParty(final String player) {
		final Jedis jedis = this.api.getConnector().getResource();
		final String val = jedis.get("currentparty:" + player);
		jedis.close();
		return val != null ? UUID.fromString(val) : null;
	}

	/**
	 * Donne la liste de tous les membres d'une party ( leader compris )
	 *
	 * @param party
	 * @return
	 */
	public ArrayList<String> getPlayersInParty(final UUID party) {

		final ArrayList<String> playerIDs = new ArrayList<>();

		final Jedis jedis = this.api.getConnector().getResource();
		for (final String data : jedis.lrange("party:" + party + ":members", 0, -1)) {
			if (data == null || data.equals("")) {
				jedis.lrem("party:" + party + ":members", 0, data);
				continue;
			}

			final String id = data;
			playerIDs.add(id);

		}
		jedis.close();
		return playerIDs;
	}

	/**
	 * Accepte une invitations
	 *
	 * @param from
	 * @param add
	 */
	public void grantRequest(String from, final BPlayer p) {

		from = from.toLowerCase();
		final String add = p.getName().toLowerCase();

		final UUID party = this.getPlayerParty(from);
		if (party == null) {
			p.sendMessage("Cette personne n'est pas dans un groupe !");
			return;
		}

		if (this.haveParty(add)) {
			final UUID partyTarget = this.getPlayerParty(add);
			if (partyTarget.equals(party)) {
				p.sendMessage("Vous êtes déjà dans ce groupe !");
				return;
			}
		}

		final String dbKey = "partyrequests:" + party + ":" + add;
		final Jedis jedis = this.api.getConnector().getResource();
		final String value = jedis.get(dbKey);
		if (value == null) {
			jedis.close();
			p.sendMessage("§cAucune invitations de ce joueur !");
			return;
		}

		if (this.getPlayersInParty(party).size() >= 5) {
			jedis.close();
			p.sendMessage("§cParty compl§te");
			return;
		}

		jedis.del(dbKey);

		jedis.close();

		if (this.haveParty(add))
			this.leaveOrDisbandParty(p);

		this.addPlayerInParty(party, add);

		this.sendMessageToParty(party, true, "§a" + p.getName() + "§e a rejoint le groupe !");

	}

	/**
	 * Retourne si un joueur a une party ou non
	 *
	 * @param player
	 * @return
	 */
	public boolean haveParty(final String player) {
		final Jedis jedis = this.api.getConnector().getResource();
		final boolean val = jedis.exists("currentparty:" + player);
		jedis.close();
		return val;
	}

	/**
	 * Retourne si un joueur a une party ou non avec un message
	 *
	 * @param player
	 * @return
	 */
	public boolean havePartyMessage(final BPlayer player) {
		final Jedis jedis = this.api.getConnector().getResource();
		final boolean val = jedis.exists("currentparty:" + player.getName().toLowerCase());
		jedis.close();
		if (!val)
			player.sendMessage("§cVous n'êtes pas dans un groupe !");
		return val;
	}

	/**
	 * Retourne si une personne est leader d'une party ou non
	 *
	 * @param player
	 * @return
	 */
	public boolean isLeader(final String player) {
		if (!this.haveParty(player))
			return false;
		final UUID party = this.getPlayerParty(player);
		if (!this.getLeader(party).equals(player))
			return false;
		return true;
	}

	/**
	 * Retourne si une personne est leader d'une party ou non avec un m§ssage
	 *
	 * @param player
	 * @return
	 */
	public boolean isLeaderMessage(final BPlayer player) {
		if (!this.haveParty(player.getName().toLowerCase())) {
			player.sendMessage("§cVous n'êtes pas dans un groupe !");
			return false;
		}
		final UUID party = this.getPlayerParty(player.getName().toLowerCase());
		if (!this.getLeader(party).equals(player.getName().toLowerCase())) {
			player.sendMessage("§cVous n'êtes pas le chef du groupe !");
			return false;
		}
		return true;
	}

	/**
	 * Kick un joueur
	 *
	 * @param p
	 */
	public void kickParty(final BPlayer p, String target) {
		final String from = p.getName().toLowerCase();
		target = target.toLowerCase();
		if (!this.haveParty(from)) {
			p.sendMessage("§cVous n'êtes pas dans un groupe !");
			return;
		}
		if (!this.isLeader(from)) {
			p.sendMessage("§cVous n'êtes pas le chef du groupe !");
			return;
		}
		if (from.equals(target)) {
			p.sendMessage("§cVous ne pouvez pas vous kicker vous même ! (/party leave)");
			return;
		}
		final UUID party = this.getPlayerParty(from);

		if (!this.haveParty(target)) {
			p.sendMessage("§cCe joueur n'est pas dans votre groupe !");
			return;
		}

		if (!this.getPlayerParty(target).equals(party)) {
			p.sendMessage("§cCe joueur n'est pas dans votre groupe !");
			return;
		}

		this.sendMessageToParty(party, true, "§a" + target + " §ea §t§ kicker du groupe !");
		this.removePlayerInParty(party, target);
	}

	/**
	 * Leave une party ou la disband
	 *
	 * @param p
	 */
	public void leaveOrDisbandParty(final BPlayer p) {
		final String from = p.getName().toLowerCase();
		if (!this.haveParty(from)) {
			p.sendMessage("§cVous n'êtes pas dans un groupe !");
			return;
		}
		final UUID party = this.getPlayerParty(from);
		if (!this.isLeader(from)) {
			this.sendMessageToParty(party, true, "§a" + p.getName() + "§e a quitté le groupe");
			this.removePlayerInParty(party, from);
			return;
		}
		this.disbandParty(party);
	}

	/**
	 * Surpprime un joueur dans une party ( d§finie le current party et l'enl§ve
	 * dans la liste )
	 *
	 * @param party
	 * @param player
	 */
	public void removePlayerInParty(final UUID party, final String player) {
		final Jedis jedis = this.api.getConnector().getResource();
		jedis.del("currentparty:" + player);
		jedis.lrem("party:" + party + ":members", 0, player);
		jedis.close();
	}

	/**
	 * Get le nom de tous les joueurs invit§ dans une party
	 *
	 * @param party
	 * @return
	 */
	public ArrayList<String> requestsList(final UUID party) {

		final String dbKey = "partyrequests:" + party + ":*";
		final ArrayList<String> playerNames = new ArrayList<>();

		final Jedis jedis = this.api.getConnector().getResource();
		for (final String data : jedis.keys(dbKey)) {
			final String[] parts = data.split(":");

			final String id = parts[2];
			playerNames.add(id);

		}
		jedis.close();
		return playerNames;
	}

	/**
	 * Envoie un m§ssage § une party
	 *
	 * @param party
	 * @param message
	 */
	@SuppressWarnings("deprecation")
	public void sendMessageToParty(final UUID party, final boolean prefix, final String message) {
		if (!this.exist(party))
			return;
		final List<String> list = this.getPlayersInParty(party);
		for (final String name : list) {
			final ProxiedPlayer target = GameServers.get().getProxy().getPlayer(name);
			if (target != null)
				target.sendMessage(prefix ? Strings.partyPrefix + message : "" + message);
		}
	}

	/**
	 * Envoie un m§ssage § une party
	 *
	 * @param party
	 * @param message
	 */
	@SuppressWarnings("deprecation")
	public void sendMessageToPartyFollow(final UUID party, final boolean prefix, final String message) {
		if (!this.exist(party))
			return;
		final List<String> list = this.getPlayersInParty(party);
		for (final String name : list) {
			final ProxiedPlayer target = GameServers.get().getProxy().getPlayer(name);
			if (target != null)
				if (this.getFollow(name))
					target.sendMessage(prefix ? Strings.partyPrefix + message : "" + message);
		}
	}

	public void sendRequest(final BPlayer p, String add) {

		final String from = p.getName().toLowerCase();
		add = add.toLowerCase();

		UUID party = this.getPlayerParty(from);

		if (party == null) {
			UUID newId = UUID.randomUUID();
			while (this.exist(newId))
				newId = UUID.randomUUID();
			this.createParty(newId, from);
			party = newId;
			p.sendMessage(Strings.partyPrefix + "§fGroupe créé, invitez-y des joueurs !");
		}

		if (!this.isLeader(from)) {
			p.sendMessage("§cVous n'êtes pas le chef du groupe !");
			return;
		}

		if (this.getLeader(party).equals(add)) {
			p.sendMessage("§cVous ne pouvez pas vous inviter vous même !");
			return;
		}

		if (this.getPlayersInParty(party).contains(add)) {
			p.sendMessage("§cCe joueur est déjà dans votre groupe");
			return;
		}

		final String dbKey = "partyrequests:" + party.toString() + ":" + add;

		final Jedis jedis = this.api.getConnector().getResource();
		final String value = jedis.get(dbKey);

		if (value != null) {
			jedis.close();
			p.sendMessage("§cInvitation déjà envoyée pour ce joueur, attendez sa réponse !");
			return;
		}

		if (this.api.getProxy().getPlayer(add) == null) {
			jedis.close();
			p.sendMessage("§cCe joueur n'est pas en ligne !");
			return;
		}

		final String allow = SettingsManager.getSetting(add, "party-enabled", "true");

		if (allow.equals("false")) {
			jedis.close();
			p.sendMessage("Ce joueur n'accepte pas les invitations !");
			return;
		}

		if (allow.equals("friends"))
			if (!this.api.getFriendsManager().isFriend(from, add)) {
				jedis.close();
				p.sendMessage("Ce joueur n'accepte que ses amis !");
				return;
			}

		if (add == null) {
			jedis.close();
			p.sendMessage("§cFatal Error ! Contactez un admin...");
		}

		final PartyRequest request = new PartyRequest(party.toString(), add, new Date());
		jedis.set(dbKey, new Gson().toJson(request));
		jedis.close();

		PartiesUtils.request(from, add, new Date(System.currentTimeMillis()));

		p.sendMessage("§eInvitation envoyée à §a" + add);
	}

	/**
	 * D§finie le serveur actuel de la party
	 *
	 * @param party
	 * @param server
	 */
	public void setCurrentServer(final UUID party, final GameServer server) {
		final Jedis jedis = this.api.getConnector().getResource();
		jedis.set("party:" + party + ":server", server.getServName());
		jedis.close();
	}

	/**
	 * D§finie le leader d'une party
	 *
	 * @param party
	 * @param leader
	 */
	public void setLeader(final UUID party, final String leader) {
		final Jedis jedis = this.api.getConnector().getResource();
		jedis.set("party:" + party + ":lead", leader);
		jedis.close();
	}

	/**
	 * D§finie le leader
	 *
	 * @param p
	 */
	public void setLeaderParty(final BPlayer p, String target) {
		final String from = p.getName().toLowerCase();
		target = target.toLowerCase();
		if (!this.haveParty(from)) {
			p.sendMessage("§cVous n'êtes pas dans un groupe !");
			return;
		}
		if (!this.isLeader(from)) {
			p.sendMessage("§cVous n'êtes pas le chef du groupe !");
			return;
		}
		if (from.equals(target)) {
			p.sendMessage("§cVous ne pouvez pas vous désigner vous même !");
			return;
		}
		final UUID party = this.getPlayerParty(from);

		if (!this.haveParty(target)) {
			p.sendMessage("§cCe joueur n'est pas dans votre groupe !");
			return;
		}

		if (!this.getPlayerParty(target).equals(party)) {
			p.sendMessage("§cCe joueur n'est pas dans votre groupe !");
			return;
		}

		this.sendMessageToParty(party, true, "§a" + target + " §ea été kicker du groupe !");
		this.setLeader(party, target);
	}

	/**
	 * Rejoindre le chef du groupe
	 *
	 * @param p
	 * @param target
	 */
	public void tpParty(final BPlayer p) {
		final String from = p.getName().toLowerCase();
		if (!this.haveParty(from)) {
			p.sendMessage("§cVous n'êtes pas dans un groupe !");
			return;
		}

		if (this.isLeader(from)) {
			p.sendMessage("§cVous ne pouvez pas vous téléporter à vous même, rajoutez le nom d'un autre joueur");
			return;
		}

		final UUID party = this.getPlayerParty(from);

		this.tpParty(p, this.getLeader(party));
	}

	/**
	 * Rejoindre un membre du groupe
	 *
	 * @param p
	 * @param target
	 */
	public void tpParty(final BPlayer p, String target) {
		final String from = p.getName().toLowerCase();
		target = target.toLowerCase();
		if (!this.haveParty(from)) {
			p.sendMessage("§cVous n'êtes pas dans un groupe !");
			return;
		}

		if (from.equals(target)) {
			p.sendMessage("§cVous ne pouvez pas vous téléporter à vous même, rajoutez le nom d'un autre joueur");
			return;
		}

		final UUID party = this.getPlayerParty(from);

		if (!this.haveParty(target)) {
			p.sendMessage("§cCe joueur n'est pas dans votre groupe !");
			return;
		}

		if (!this.getPlayerParty(target).equals(party)) {
			p.sendMessage("§cCe joueur n'est pas dans votre groupe !");
			return;
		}

		if (this.api.getProxy().getPlayer(target) == null) {
			p.sendMessage("§cCe joueur n'est pas en ligne !");
			return;
		}

		final PlayerData targetData = this.api.getPlayerDataManager().getPlayerData(target);
		final GameServer currentServer = targetData.getCurrentServer();

		if (currentServer != null)
			currentServer.wantGoOn(p, true, (server, status, player) -> {
				if (status == ConnectionStatus.DENY)
					player.sendMessage("§cVous ne pouvez pas rejoindre ce joueur !");
			});
		else
			p.sendMessage("§cCe joueur n'est pas en ligne !");
	}

}
