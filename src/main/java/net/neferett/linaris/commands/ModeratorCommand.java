package net.neferett.linaris.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.managers.player.BPlayerHandler;
import net.neferett.linaris.managers.player.BStaff;

public abstract class ModeratorCommand extends Command {

	int lvl;

	public ModeratorCommand(final String name, final int level) {
		super(name);
		this.lvl = level;
	}

	public ModeratorCommand(final String name, final int lvl, final String... alias) {
		super(name, null, alias);
		this.lvl = lvl;
	}

	public abstract void execute(BStaff p, String[] args);

	@Override
	public void execute(final CommandSender sender, final String[] args) {

		if (!(sender instanceof ProxiedPlayer))
			return;

		final BStaff p = BPlayerHandler.get().getStaff(sender.getName());

		GameServers.get().getTasksManager().addTask(() -> {

			if (p != null && (this.lvl == 0 || p.getRank().getVipLevel() >= this.lvl))
				this.execute(p, args);
			else
				BPlayerHandler.get().getPlayer(sender.getName()).sendMessage("§cVous devez être §6Modérateur §c!");
		});

	}

}
