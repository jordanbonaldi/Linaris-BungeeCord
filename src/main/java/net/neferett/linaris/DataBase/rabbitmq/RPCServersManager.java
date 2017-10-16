package net.neferett.linaris.DataBase.rabbitmq;

import net.neferett.linaris.GameServers;
import net.neferett.linaris.utils.database.RabbitMQRCPServer;
import net.neferett.linaris.utils.json.JSONObject;

public class RPCServersManager extends RabbitMQRCPServer {

	public RPCServersManager() throws Exception {
		super("serverm");
	}

	@Override
	public JSONObject onMessage(JSONObject message) throws Exception {


		System.out.println(message.toString());
		JSONObject callback = new JSONObject();
		String serverName = GameServers.get().getServersManager().registerServer(message.getString("gameName"), message.getString("ip"),message.getInt("port"));
		callback.put("serverName", serverName);

		return callback;
	}

}
