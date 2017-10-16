package net.neferett.linaris.api.friends;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.google.gson.Gson;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.SettingsManager;
import net.neferett.linaris.commands.player.FriendsCommand;
import net.neferett.linaris.utils.player.PlayersUtils;
import net.neferett.linaris.utils.player.Sound;
import redis.clients.jedis.Jedis;

public class FriendsManagement {

	protected GameServers plugin;

	public FriendsManagement(GameServers plugin) {
		this.plugin = plugin;
		plugin.getProxy().getPluginManager().registerCommand(plugin, new FriendsCommand(this, plugin));
	}

	public String sendRequest(String from, String add) {

		from = from.toLowerCase();
		add = add.toLowerCase();
		
		if (from.equals(add))
			return "§cVous êtes déjà votre propre ami... T_T";

		String dbKey = "friendrequest:" + from + ":" + add;
		String checkKey = "friendrequest:" + add + ":" + from;

		if (isFriend(from, add))
			return "§cVous êtes déjà amis !";

		Jedis jedis = plugin.getConnector().getResource();
		String value = jedis.get(dbKey);
		if (jedis.get(checkKey) != null) {
			jedis.close();
			return grantRequest(add, from);
		}

		if (value != null) {
			jedis.close();
			return "§cJoueur déjà ajouté, demandez lui de vous ajouter à son tour !";
		}

		String allow = SettingsManager.getSetting(add, "friends-enabled");
		if (allow != null && allow.equals("false")) {
			jedis.close();
			return "Ce joueur n'accepte pas les demandes d'ami";
		}

		if (plugin.getProxy().getPlayer(add) == null) {
			jedis.close();
			return "§cCe joueur n'est pas en ligne !";
		}

		if (add == null) {
			jedis.close();
			return "§cFatal Error ! Contactez un admin...";
		}

		FriendRequest request = new FriendRequest(from, add, new Date());
		jedis.set(dbKey, new Gson().toJson(request));
		jedis.close();
		
		request(from, add, new Date(System.currentTimeMillis()));

		return "§eDemande d'ami envoyée à §a" + add;
	}

	public boolean isFriend(String from, String isFriend) {
		from = from.toLowerCase();
		isFriend = isFriend.toLowerCase();
		return StringFriendList(from).contains(isFriend);
	}

	public String grantRequest(String from, String add) {

		from = from.toLowerCase();
		add = add.toLowerCase();
		
		if (from.equals(add))
			return "§cVous êtes déjà votre propre ami... T_T";

		if (isFriend(from, add))
			return "§cVous êtes déjà amis !";

		String dbKey = "friendrequest:" + from + ":" + add;
		Jedis jedis = plugin.getConnector().getResource();
		String value = jedis.get(dbKey);
		if (value == null) {
			jedis.close();
			return "§cVous n'avez pas de demande d'ami de ce joueur !";
		}

		jedis.del(dbKey);

		if (add == null) {
			jedis.close();
			return "§cFatal Error ! Contactez un admin...";
		}

		jedis.rpush("friends:" + from, add.toString());
		jedis.rpush("friends:" + add, from.toString());

		jedis.close();
		
		response(from, add);

		return "§bVous êtes maintenant ami avec §a" + from + " §b!";
	}
	
	@SuppressWarnings("deprecation")
	public String removeFriend(String asking, String askTo) {
		
		asking = asking.toLowerCase();
		askTo = askTo.toLowerCase();
		
		String dbKey = "friends:" + asking;
		String dbKeyTo = "friends:" + askTo;

		Jedis jedis = plugin.getConnector().getResource();
		boolean failed = (jedis.lrem(dbKey, 0, askTo.toString()) == 0
				|| jedis.lrem(dbKeyTo, 0, asking.toString()) == 0);
		jedis.close();
		if (failed)
			return "§cVous n'êtes pas ami avec ce joueur !";
		String name = askTo;
		
		ProxiedPlayer pl = ProxyServer.getInstance().getPlayer(name);
		if (pl != null)
			pl.sendMessage("§cVous n'êtes plus ami avec §e" + asking +" §c!");
		
		return "§cVous n'êtes plus ami avec §e" + name +" §c!";
	}

	public ArrayList<String> friendList(String asking) {

		asking = asking.toLowerCase();
		
		ArrayList<String> playerNames = new ArrayList<>();

		for (String id : StringFriendList(asking)) {
			playerNames.add(id);
		}
		return playerNames;
	}

	public ArrayList<String> StringFriendList(String asking) {
		
		asking = asking.toLowerCase();
		
		ArrayList<String> playerIDs = new ArrayList<>();

		Jedis jedis = plugin.getConnector().getResource();
		for (String data : jedis.lrange("friends:" + asking, 0, -1)) {
			if (data == null || data.equals("")) {
				jedis.lrem("friends:" + asking, 0, data);
				continue;
			}

			String id = data;
			playerIDs.add(id);

		}
		jedis.close();
		return playerIDs;
	}

	public HashMap<String, String> associatedFriendsList(String asking) {
		
		asking = asking.toLowerCase();
		
		HashMap<String, String> ret = new HashMap<>();

		for (String id : StringFriendList(asking)) {
			String name = id;
			if (name == null) {
				continue;
			}
			ret.put(id, name);
		}
		return ret;
	}

	public HashMap<String, String> onlineAssociatedFriendsList(String asking) {
		
		asking = asking.toLowerCase();
		
		HashMap<String, String> ret = new HashMap<>();
		HashMap<String, String> map = associatedFriendsList(asking);

		map.keySet().stream().filter(id -> plugin.getProxy().getPlayer(id) != null)
				.forEach(id -> ret.put(id, map.get(id)));

		return ret;
	}

	public ArrayList<String> requestsList(String asking) {
		
		asking = asking.toLowerCase();
		
		String dbKey = "friendrequest:*:" + asking;
		ArrayList<String> playerNames = new ArrayList<>();

		Jedis jedis = plugin.getConnector().getResource();
		for (String data : jedis.keys(dbKey)) {
			String[] parts = data.split(":");

			String id = parts[1];
			playerNames.add(id);

		}
		jedis.close();
		return playerNames;
	}

	public ArrayList<String> sentRequestsList(String asking) {
		
		asking = asking.toLowerCase();
		
		String dbKey = "friendrequest:" + asking + ":";
		ArrayList<String> playerNames = new ArrayList<>();

		Jedis jedis = plugin.getConnector().getResource();
		for (String data : jedis.keys(dbKey)) {
			String[] parts = data.split(":");
			String id = parts[1];
			playerNames.add(id);

		}
		jedis.close();
		return playerNames;
	}

	@SuppressWarnings("deprecation")
	public void request(String from, String to, Date date) {
		ProxiedPlayer pl = ProxyServer.getInstance().getPlayer(to);
		if (pl == null)
			return;

		String pseudo = from;
		if (pseudo == null)
			return;

		pl.sendMessage("§6----------------------------------------------------");
		
		TextComponent line1 = new TextComponent("§a" + pseudo +" §evous a envoyé une demande d'ami !");
		TextComponent line2 = new TextComponent("§eCliquez pour accepter ou faites /friends add §a" + pseudo);
		
		line1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§d§oCliquez pour accepter §b" + pseudo + " §d§odans vos amis").create()));
		line2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§d§oCliquez pour accepter §b" + pseudo + " §d§odans vos amis").create()));
		
		line1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friends add " + pseudo));
		line2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friends add " + pseudo));
		
		pl.sendMessage(line1);
		pl.sendMessage(line2);	
		
		pl.sendMessage("§6----------------------------------------------------");
		PlayersUtils.sendSound(pl, Sound.VILLAGER_HAGGLE, 1f, 1f);
	}

	public void response(String from, String to) {
		final ProxiedPlayer pl = ProxyServer.getInstance().getPlayer(from);
		if (pl == null)
			return;

		String pseudo = to;
		if (pseudo == null)
			return;

	
		pl.sendMessage(new ComponentBuilder("§bVous êtes maintenant ami avec §a" + to + " §b!").create());


	}
}
