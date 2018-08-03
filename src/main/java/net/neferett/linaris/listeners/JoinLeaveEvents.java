package net.neferett.linaris.listeners;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.neferett.linaris.managers.bans.BanManager;
import net.neferett.linaris.managers.bans.DoubleAccount;
import net.neferett.linaris.managers.bans.DoubleAccount.MODE;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BPlayerHandler;

public class JoinLeaveEvents implements Listener {

	@EventHandler
	public void onQuit(final PlayerDisconnectEvent e) {

		final BPlayer p = BPlayerHandler.get().getPlayer(e.getPlayer());
		p.getData().setBoolean("connected", false);
		if (BanManager.get().isBan(p.getName()) == null)
			DoubleAccount.get().updateMode(p.getAddress(), p.getName(), MODE.OFFLINE);

		p.removePlayer();

		BPlayerHandler.get().removePlayer(e.getPlayer());
	}

}
