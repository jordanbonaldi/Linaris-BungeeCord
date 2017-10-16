package net.neferett.linaris.commands.player.data;

import net.md_5.bungee.api.ChatColor;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.commands.PlayerCommand;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.utils.time.TimeUtils;

public class CommandCoins extends PlayerCommand {

	private static String sep = new StringBuilder().append(ChatColor.GRAY).append(ChatColor.STRIKETHROUGH)
			.append("---------------------------------------------").toString();

	public CommandCoins() {
		super("coins", true);
	}

	@Override
	public void execute(final BPlayer p, final PlayerData pd, final String[] args) {

		p.sendMessage(sep);
		p.sendMessage("");
		p.sendMessage("§7Informations sur vos §eCoins");
		p.sendMessage("");
		p.sendMessage("§7Coins§f: §e" + pd.getCoins());
		p.sendMessage("");
		p.sendMessage("§7Gains de Coins§f: §e" + (100 + pd.getRank().getCoinsBonus()) + "%");
		p.sendMessage("");
		if (pd.contains("booster"))
			p.sendMessage("§7Booster§f: " + TimeUtils.minutesToDayHoursMinutes(
					(int) (Math.abs(pd.getBoosterFinish() - System.currentTimeMillis()) / 1000 / 60)));
		else
			p.sendMessage("§7Booster§f: §cDésactivé");
		p.sendMessage("");
		p.sendMessage(sep);
	}
}
