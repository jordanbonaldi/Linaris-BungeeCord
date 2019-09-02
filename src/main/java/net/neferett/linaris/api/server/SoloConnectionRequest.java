package net.neferett.linaris.api.server;

import com.google.gson.Gson;

import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.utils.database.RabbitMQRCPClient;
import net.neferett.linaris.utils.json.JSONException;
import net.neferett.linaris.utils.json.JSONObject;

public class SoloConnectionRequest extends RabbitMQRCPClient {

	class SoloConnection {

		String[]	args;
		String		player;
		int			rank;

		public SoloConnection(final String player, final int rank, final String[] args) {
			this.player = player;
			this.rank = rank;
			this.args = args;
		}

		public String[] getArgs() {
			return this.args;
		}

		public String getJSON() {
			return new Gson().toJson(this);
		}

		public String getPlayer() {
			return this.player;
		}

		public int getRank() {
			return this.rank;
		}
	}

	public SoloConnectionRequest(final BPlayer p, final GameServer server, final String[] args,
			final SoloConnectionServerCallBack callback) throws Exception {

		final JSONObject json = new JSONObject();
		json.put("type", "soloconnect");
		final PlayerData data = GameServers.get().getPlayerDataManager().getPlayerData(p.getName());
		json.put("request",
				new JSONObject(new SoloConnection(p.getName().toLowerCase(), data.getRank().getId(), args).getJSON()));

		this.setRequestQueueName("gcrequest-" + server.getServName().toLowerCase());
		this.setMessage(json);

		try {
			this.send();
		} catch (Exception e) {
			e.printStackTrace();
		}

		final JSONObject jcallb = this.getCallback();

		if (jcallb != null)
			try {
				final ConnectionServerRespond respond = new Gson().fromJson(jcallb.toString(),
						ConnectionServerRespond.class);

				if (respond != null)
					callback.done(server, respond.getStatus(), p);
				else
					callback.done(server, ConnectionStatus.DENY, p);

			} catch (final Exception e) {
				callback.done(server, ConnectionStatus.DENY, p);
			}
		else
			callback.done(server, ConnectionStatus.DENY, p);
	}

}
