package net.neferett.linaris.DataBase.rabbitmq;

import java.io.IOException;

import net.neferett.linaris.GameServers;
import net.neferett.linaris.DataBase.rabbitmq.messaging.RabbitMQMessagingServer;
import net.neferett.linaris.api.server.GameServer;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BPlayerHandler;
import net.neferett.linaris.utils.json.JSONObject;

public class MoveToServerRabbitMQ extends RabbitMQMessagingServer {

	public MoveToServerRabbitMQ() throws IOException {
		super("servermoving");
	}

	@Override
	public void onMessage(final JSONObject message) throws Exception {

		final String name = message.getString("player");
		final BPlayer player = BPlayerHandler.get().getPlayer(name);
		if (player == null)
			return;

		final String serverName = message.getString("server");
		final GameServer server = GameServers.get().getServersManager().getServer(serverName);

		if (server == null)
			return;

		server.wantGoOn(player, true, "custom");

	}

}
