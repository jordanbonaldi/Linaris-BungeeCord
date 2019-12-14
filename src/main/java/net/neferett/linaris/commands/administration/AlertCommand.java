package net.neferett.linaris.commands.administration;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.commands.ModeratorCommand;
import net.neferett.linaris.commands.PlayerCommand;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BStaff;

public class AlertCommand extends ModeratorCommand {

	public AlertCommand() {
		super("alert", 3);
	}

	@SuppressWarnings("deprecation")
	public void sendMessage(final String message) {
		for (final ProxiedPlayer pl : GameServers.get().getProxy().getPlayers())
			pl.sendMessage("§7[§9Annonce§7] §e" + message.replace("&", "§"));
	}

	@Override
	public void execute(BStaff p, String[] args) {
		if (args.length < 2) {
			p.sendMessage("§c/alert");
			return;
		}

		final StringBuilder sb = new StringBuilder("");
		for (final String arg : args)
			sb.append(arg).append(" ");

		this.sendMessage(sb.toString());
	}
}
