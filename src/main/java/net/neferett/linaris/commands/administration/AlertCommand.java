package net.neferett.linaris.commands.administration;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.commands.PlayerCommand;
import net.neferett.linaris.managers.player.BPlayer;

public class AlertCommand extends PlayerCommand {

	public AlertCommand() {
		super("annonce", true);
	}

	@Override
	public void execute(final BPlayer p, final PlayerData pd, final String[] args) {
		if (pd.getRank().getModerationLevel() < 2)
			return;

		if (args.length < 2) {
			p.sendMessage("§c/alert");
			return;
		}

		final StringBuilder sb = new StringBuilder("");
		for (final String arg : args)
			sb.append(arg + " ");

		this.sendMessageToStaff(sb.toString());

	}

	@SuppressWarnings("deprecation")
	public void sendMessageToStaff(final String message) {
		for (final ProxiedPlayer pl : GameServers.get().getProxy().getPlayers())
			pl.sendMessage("§7[§9Annonce§7] §e" + message.replace("&", "§"));
	}

}
