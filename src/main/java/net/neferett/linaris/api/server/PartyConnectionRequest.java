package net.neferett.linaris.api.server;

import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;

import net.neferett.linaris.GameServers;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.utils.database.RabbitMQRCPClient;
import net.neferett.linaris.utils.json.JSONObject;

public class PartyConnectionRequest extends RabbitMQRCPClient {

	class MultiConnection {

		String[]	args;
		String		party;
		String[]	players;
		int[]		ranks;

		public MultiConnection(final UUID uuid, final String[] players, final int[] ranks, final String[] args) {
			this.party = uuid.toString();
			this.players = players;
			this.ranks = ranks;
			this.args = args;
		}

		public String[] getArgs() {
			return this.args;
		}

		public String getJSON() {
			return new Gson().toJson(this);
		}

		public UUID getParty() {
			return UUID.fromString(this.party);
		}

		public String[] getPlayers() {
			return this.players;
		}

		public int[] getRanks() {
			return this.ranks;
		}
	}

	public PartyConnectionRequest(final UUID party, final List<BPlayer> pls, final GameServer server,
			final String[] args, final MultiConnectionServerCallBack callback) throws Exception {

		final String[] players = new String[pls.size()];
		for (int i = 0; i < pls.size(); i++)
			players[i] = pls.get(i).getName().toLowerCase();
		final int[] ranks = new int[pls.size()];
		for (int i = 0; i < pls.size(); i++)
			ranks[i] = GameServers.get().getPlayerDataManager().getPlayerData(pls.get(i).getName()).getRank().getId();

		final JSONObject json = new JSONObject();
		json.put("type", "multiconnect");
		json.put("request", new JSONObject(new MultiConnection(party, players, ranks, args).getJSON()));

		this.setRequestQueueName("gcrequest-" + server.getServName().toLowerCase());
		this.setMessage(json);

		this.send();

		final JSONObject jcallb = this.getCallback();

		if (jcallb != null)
			try {

				final ConnectionServerRespond respond = new Gson().fromJson(jcallb.toString(),
						ConnectionServerRespond.class);
				if (respond != null)
					callback.done(server, respond.getStatus(), pls);
				else
					callback.done(server, ConnectionStatus.DENY, pls);

			} catch (final Exception e) {
				callback.done(server, ConnectionStatus.DENY, pls);
			}
		else
			callback.done(server, ConnectionStatus.DENY, pls);

	}

}
