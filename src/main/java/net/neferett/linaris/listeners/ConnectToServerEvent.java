package net.neferett.linaris.listeners;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BPlayerHandler;
import net.neferett.linaris.managers.player.BYoutuber;

public class ConnectToServerEvent implements Listener {

	@EventHandler
	public void OnPlayerSwicth(final ServerConnectEvent e) throws Exception {
		final BPlayer p = BPlayerHandler.get().getPlayer(e.getPlayer());

		if (!p.isLogged() && e.getTarget().getName().startsWith("Login")) {
			p.tryLogPlayer();
			return;
		} else if (e.getTarget().getName().startsWith("Login")) {
			p.connectTo("Lobby");
			return;
		} else if (e.getTarget().getName().contains("hub")) {
			final BYoutuber yt = BPlayerHandler.get().getYT(e.getPlayer());

			if (yt == null)
				return;
			else if (!yt.isConfig()) {
				yt.sendMessage("§7§m--------------------------------");
				yt.sendMessage("");
				yt.sendMessage("§7Notre systeme à remarqué que vous avez oublié de");
				yt.sendMessage("§7configurer votre grade §fYou§cTuber§7 pour se faire faites§f:");
				yt.sendMessage("");
				yt.sendMessage("      §c/yt config <channelid>");
				yt.sendMessage("");
				yt.sendMessage("§7Les joueurs pourront ainsi voir votre nombre d'abonnés et de vues en direct via§f:");
				yt.sendMessage("      §c/yt §7ou §c/yt <pseudo>");
				yt.sendMessage("");
				yt.sendMessage("§7§m--------------------------------");
			}
			return;
		}

		if (e.getPlayer().getServer() == null) {
			final ServerInfo s = GameServers.get().getLoginsserver().stream()
					.sorted((e1, e2) -> e2.getPlayers().size() - e1.getPlayers().size()).findFirst().orElse(null);

			if (s == null)
				return;
			e.setTarget(s);
			return;
		}
	}

}
