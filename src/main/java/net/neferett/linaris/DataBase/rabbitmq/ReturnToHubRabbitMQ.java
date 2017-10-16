package net.neferett.linaris.DataBase.rabbitmq;

import java.io.IOException;

import net.neferett.linaris.DataBase.rabbitmq.messaging.RabbitMQMessagingServer;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BPlayerHandler;
import net.neferett.linaris.utils.json.JSONObject;

public class ReturnToHubRabbitMQ extends RabbitMQMessagingServer {

	public ReturnToHubRabbitMQ() throws IOException {
		super("returntohub");
	}

	@Override
	public void onMessage(final JSONObject message) throws Exception {

		final String name = message.getString("player");
		final BPlayer player = BPlayerHandler.get().getPlayer(name);
		if (player == null)
			return;

		player.connectTo("Lobby");

	}

}
