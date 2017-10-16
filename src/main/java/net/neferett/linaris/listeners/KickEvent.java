package net.neferett.linaris.listeners;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BPlayerHandler;

public class KickEvent implements Listener {

	@EventHandler
	public void onPlayerKick(final ServerKickEvent event) {
		final ServerInfo kickedFrom = event.getKickedFrom();
		final BPlayer p = BPlayerHandler.get().getPlayer(event.getPlayer());

		if (p.getServName().startsWith("Login")) {
			p.sendMessage("§aReconnectez-vous !");
			p.quit("§Merci de bien vouloir vous reconnecter");
		}

		if (kickedFrom.getName().startsWith("Login"))
			p.quit(event.getKickReasonComponent().toString());
		else
			event.setCancelled(true);

		event.setCancelServer(p.getServInfo("Lobby"));
	}

}
