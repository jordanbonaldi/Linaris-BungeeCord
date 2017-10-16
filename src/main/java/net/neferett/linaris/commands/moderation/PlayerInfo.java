package net.neferett.linaris.commands.moderation;

import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.commands.ModeratorCommand;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BPlayerHandler;
import net.neferett.linaris.managers.player.BStaff;
import net.neferett.linaris.managers.player.cheat.HackEnum;
import net.neferett.linaris.managers.player.cheat.data.CheatData;
import net.neferett.linaris.utils.time.TimeUtils;

public class PlayerInfo extends ModeratorCommand {

	public PlayerInfo() {
		super("info", 1);
	}

	@Override
	public void execute(final BStaff p, final String[] args) {
		if (args.length == 0) {
			p.sendMessage("§cMerci de specifier un joueur !");
			return;
		}

		final BPlayer pl = BPlayerHandler.get().getPlayer(args[0]);

		final PlayerData data = pl == null ? GameServers.get().getPlayerDataManager().getPlayerData(args[0])
				: pl.getData();
		if (data == null) {
			p.sendMessage("§cLe joueur spécifié n'existe pas !");
			return;
		}

		final CheatData cheatdata = pl == null ? new CheatData(args[0]) : pl.getCheatData();

		p.sendMessage("§f§m=====================");
		p.sendMessage("");
		p.sendMessage("§7Joueur§f: §e" + args[0]);
		p.sendMessage("");
		p.sendMessage("§7Rang§f: §" + data.getRank().getColor() + data.getRank().getName());
		p.sendMessage("§7Crédits§f: §b" + data.getSuperCoins());
		p.sendMessage("§7Coins§f: §e" + data.getCoins());
		p.sendMessage("§7Tokens§f: §c" + data.getTokens());
		p.sendMessage("");
		if (pl != null) {
			p.sendMessage("§7Version§f: §c" + pl.getVersion());
			p.sendMessage("");
			p.sendMessage("§7En ligne depuis§f: §c" + pl.onlineSince());
			p.sendMessage("§7Actuellement sur§f: §a" + pl.getServName());
		} else
			p.sendMessage("§7Vu pour la derniere fois, il y a §c"
					+ (!data.contains("LastConnection") ? "Jamais" : TimeUtils.minutesToDayHoursMinutes(
							System.currentTimeMillis() / 1000 - data.getLong("LastConnection") / 1000)));
		p.sendMessage("");
		HackEnum.valuesAsList().forEach(e -> {
			if (cheatdata.getBan(e) > 0)
				p.sendMessage("§c" + e.getName() + " §7-> §c" + cheatdata.getBan(e) + " ban"
						+ (cheatdata.getBan(e) > 1 ? "s" : ""));
		});
		p.sendMessage("");
		p.sendMessage("§f§m=====================");
	}

}
