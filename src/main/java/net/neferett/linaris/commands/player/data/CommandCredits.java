package net.neferett.linaris.commands.player.data;

import net.md_5.bungee.api.ChatColor;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.commands.PlayerCommand;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.utils.time.TimeUtils;

public class CommandCredits extends PlayerCommand {

	private static String sep = new StringBuilder().append(ChatColor.GRAY).append(ChatColor.STRIKETHROUGH)
			.append("---------------------------------------------").toString();

	public CommandCredits() {
		super("credits", true);
	}

	@Override
	public void execute(final BPlayer p, final PlayerData pd, final String[] args) {

		p.sendMessage(sep);
		p.sendMessage("");
		p.sendMessage("§7Informations sur vos §BCrédits");
		p.sendMessage("");
		p.sendMessage("§7Crédits§f: §b" + pd.getSuperCoins());
		p.sendMessage("");
		p.sendMessage("§7Gains de Crédits§f: §b" + (100 + pd.getRank().getMCoinsBonus()) + "%");
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
