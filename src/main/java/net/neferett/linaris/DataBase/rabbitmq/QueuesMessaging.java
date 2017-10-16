package net.neferett.linaris.DataBase.rabbitmq;

import java.io.IOException;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.DataBase.rabbitmq.messaging.RabbitMQMessagingServer;
import net.neferett.linaris.api.Games;
import net.neferett.linaris.utils.json.JSONObject;

public class QueuesMessaging extends RabbitMQMessagingServer {

	public QueuesMessaging() throws IOException {
		super("gamequeues");
	}

	@Override
	public void onMessage(JSONObject message) throws Exception {

		String playerName = message.getString("player");
		String type = message.getString("type");
		if (type.equals("add")) {

			int gameId = message.getInt("game");

			ProxiedPlayer player = GameServers.get().getProxy().getPlayer(playerName);
			if (player == null)
				return;
			
			Games game = Games.getByID(gameId);
			
			if (game == null)
				return;

			GameServers.get().getQueuesManagement().addInQueue(player, game, message.has("map") ? message.getString("map") : null);

		} else if (type.equals("remove")) {
			
			ProxiedPlayer player = GameServers.get().getProxy().getPlayer(playerName);
			if (player == null)
				return;
			
			
			GameServers.get().getQueuesManagement().leftQueue(player,"Sortie volontaire");
		}
	

	}

}
