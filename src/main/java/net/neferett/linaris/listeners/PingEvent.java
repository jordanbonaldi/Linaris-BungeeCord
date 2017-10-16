package net.neferett.linaris.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.neferett.linaris.GameServers;

public class PingEvent implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onProxyPing(final ProxyPingEvent e) throws Exception {
		final ServerPing sp = e.getResponse();

		sp.getPlayers().setMax(GameServers.get().getConfigManager().getSlots());
		sp.getPlayers().setOnline(ProxyServer.getInstance().getOnlineCount());

		sp.getPlayers().setSample(GameServers.get().getConfigManager().getPlayersPing());

		if (ProxyServer.getInstance().getOnlineCount() >= GameServers.get().getConfigManager().getSlots())
			sp.setDescription(GameServers.get().getConfigManager().getTopmotd().replaceAll("&", "§") + "\n"
					+ GameServers.get().getConfigManager().getMotdfull().replace("&", "§"));
		else
			sp.setDescription(GameServers.get().getConfigManager().getTopmotd().replaceAll("&", "§") + "\n"
					+ GameServers.get().getConfigManager().getMotd().replace("&", "§"));

		e.setResponse(sp);
	}

}
