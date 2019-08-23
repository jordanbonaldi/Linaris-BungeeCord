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
			System.out.println("toto");
			p.tryLogPlayer();
			return;
		} else if (e.getTarget().getName().startsWith("Login")) {
			p.connectTo("Lobby");
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
