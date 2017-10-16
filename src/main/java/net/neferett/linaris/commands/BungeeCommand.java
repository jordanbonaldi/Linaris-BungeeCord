package net.neferett.linaris.commands;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class BungeeCommand  {
	public abstract void onCommand(ProxiedPlayer player, String[] args);
}
