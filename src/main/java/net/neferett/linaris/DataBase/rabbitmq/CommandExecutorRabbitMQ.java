package net.neferett.linaris.DataBase.rabbitmq;

import java.io.IOException;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.DataBase.rabbitmq.messaging.RabbitMQMessagingServer;
import net.neferett.linaris.utils.json.JSONObject;

public class CommandExecutorRabbitMQ extends RabbitMQMessagingServer {

	public CommandExecutorRabbitMQ() throws IOException {
		super("cmdExecutor");
	}

	@Override
	public void onMessage(JSONObject message) throws Exception {

		String name = message.getString("player");
		ProxiedPlayer player = GameServers.get().getProxy().getPlayer(name);
		if (player == null) return;
		
		String command = message.getString("command");
		
		player.chat("/" + command);
		
		
	}

}
