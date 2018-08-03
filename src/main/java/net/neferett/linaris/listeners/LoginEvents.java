package net.neferett.linaris.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.ranks.RankManager;
import net.neferett.linaris.managers.bans.BanManager;
import net.neferett.linaris.managers.bans.BanManager.IPBans;
import net.neferett.linaris.managers.bans.BanManager.PseudoBans;
import net.neferett.linaris.managers.bans.DoubleAccount;
import net.neferett.linaris.managers.bans.DoubleAccount.MODE;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BPlayerHandler;

public class LoginEvents implements Listener {

	@EventHandler
	public void onJoin(final PostLoginEvent e) throws Exception {
		final BPlayer p = BPlayerHandler.get().getPlayer(e.getPlayer());

		p.getData().setBoolean("connected", true);

		if (p.getData().contains("mod") && !p.getData().getBoolean("mod") && p.getRank().getModerationLevel() > 1)
			p.getData().setRank(0);

		final DoubleAccount dc = DoubleAccount.get();

		if (p.getAddress() != p.getData().get("Log")) {
			dc.removeFrom(p.getData().get("Log"), p.getName());
			dc.addToIP(p.getAddress(), p.getName());
			dc.updateMode(p.getAddress(), p.getName(), MODE.ONLINE);
		}

		p.tryIPLog();
	}

	@EventHandler
	public void onPreJoin(final LoginEvent e) {

		e.registerIntent(GameServers.get());

		if (!BPlayerHandler.get().isUserNameValide(e.getConnection().getName())) {

			e.setCancelReason("§cNom d'utilisateur non valide !");
			e.setCancelled(true);
			return;
		} else if (!BPlayerHandler.get().isValidIP(e.getConnection().getAddress().getAddress().getHostAddress())) {
			e.setCancelReason("§cVotre ip n'est pas autorisé a vous connecter sur notre infrastucture !");
			e.setCancelled(true);
			return;
		}

		e.completeIntent(GameServers.get());

		final String ip = e.getConnection().getAddress().getAddress().getHostAddress();

		final BanManager bm = BanManager.get();

		final IPBans i = bm.isIPBan(ip);
		final PseudoBans pseudo = bm.isBan(e.getConnection().getName());

		if (i != null) {
			e.setCancelReason(bm.BannedEject(i.life(), i.bannedReason(), i.getFromTime(), i.getTime(), i.Bannedby()));
			e.setCancelled(true);
			return;
		} else if (pseudo != null) {
			e.setCancelReason(bm.BannedEject(pseudo.life(), pseudo.bannedReason(), pseudo.getFromTime(),
					pseudo.getTime(), pseudo.Bannedby()));
			e.setCancelled(true);
			return;
		}

		if (ProxyServer.getInstance().getOnlineCount() >= GameServers.get().getConfigManager().getSlots())
			if (GameServers.get().getPlayerDataManager().getPlayerData(e.getConnection().getName())
					.getRank() == RankManager.getInstance().getRank(0)) {
				e.setCancelled(true);
				e.setCancelReason(ChatColor.RED + "Le serveur est plein son accès est réservé aux §e§lVIPS");
				return;
			}
	}

}
