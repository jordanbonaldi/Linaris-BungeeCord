package net.neferett.linaris.api.party;

import java.util.Date;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.neferett.linaris.utils.player.PlayersUtils;
import net.neferett.linaris.utils.player.Sound;

public class PartiesUtils {
	
	@SuppressWarnings("deprecation")
	public static void request(String from, String to, Date date) {
		ProxiedPlayer pl = ProxyServer.getInstance().getPlayer(to);
		if (pl == null)
			return;

		String pseudo = from;
		if (pseudo == null)
			return;

		pl.sendMessage("§6----------------------------------------------------");
		
		TextComponent line1 = new TextComponent("§a" + pseudo +" §evous a invité dans son groupe !");
		TextComponent line2 = new TextComponent("§e/party accept §a" + pseudo + " §epour accepter");
		
		line1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§d§oCliquez pour rejoindre le groupe de §b" + pseudo).create()));
		line2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§d§oCliquez pour accepter §b" + pseudo).create()));
		
		line1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + pseudo));
		line2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + pseudo));
		
		pl.sendMessage(line1);
		pl.sendMessage(line2);	
		

		PlayersUtils.sendSound(pl, Sound.VILLAGER_HAGGLE, 1f, 1f);
		
		pl.sendMessage("§6----------------------------------------------------");
	}
	
}
