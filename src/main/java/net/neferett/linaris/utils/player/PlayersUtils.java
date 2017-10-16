package net.neferett.linaris.utils.player;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.DataBase.rabbitmq.messaging.RabbitMQMessagingClient;
import net.neferett.linaris.utils.json.JSONArray;
import net.neferett.linaris.utils.json.JSONObject;

public class PlayersUtils {

	public static void sendSound(ProxiedPlayer p , Sound sound,float volume,float pitch) {
		try {
			JSONObject request = new JSONObject();
			request.put("type", "sounds");
			request.put("player", p.getName());
			JSONArray args = new JSONArray();
			args.put(sound.name());
			args.put(volume);
			args.put(pitch);
			request.put("args", args);
			
			new RabbitMQMessagingClient("playereffects", request);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static List<String> getOnlineNames() {
		List<String> names = new ArrayList<>();
		for (ProxiedPlayer pp : GameServers.get().getProxy().getPlayers())
			names.add(pp.getName());
		return names;
			
	}
	
}
