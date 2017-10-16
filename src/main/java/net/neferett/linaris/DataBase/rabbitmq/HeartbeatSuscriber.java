package net.neferett.linaris.DataBase.rabbitmq;

import java.io.IOException;

import net.neferett.linaris.DataBase.rabbitmq.messaging.RabbitMQMessagingServer;
import net.neferett.linaris.managers.servers.ServersManager;
import net.neferett.linaris.utils.json.JSONObject;

public class HeartbeatSuscriber extends RabbitMQMessagingServer {

	ServersManager manager;

	public HeartbeatSuscriber(ServersManager manager) throws IOException {
		super("servers");
		this.manager = manager;
	}

	@Override
	public void onMessage(JSONObject message) throws Exception {

		String type = message.getString("type");

		if (type.equals("heartbeat")) {

			String name = message.getString("servName");
			String ip = message.getString("ip");
			String port = message.getString("port");
			String gameName = message.getString("gameName");
			String mapName = message.getString("mapName");
			String players = message.getString("players");
			String maxPlayers = message.getString("maxPlayers");
			String canJoin = message.getString("canJoin");
			String canSee = message.getString("canSee");
			manager.heartBeet(name, ip, port,gameName,mapName,maxPlayers,players,canJoin,canSee);
		
			return;

		} else if (type.equals("stop")) {

			String name = message.getString("servName");
			manager.remove(name);

			return;

		}
	}

}
