package net.neferett.linaris.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BPlayerHandler;

public abstract class PlayerCommand extends Command {

	boolean needLogin;

	public PlayerCommand(final String name, final boolean needLogin) {
		super(name);
		this.needLogin = needLogin;
	}

	public PlayerCommand(final String name, final boolean needLogin, final String... alias) {
		super(name, null, alias);
		this.needLogin = needLogin;
	}

	public abstract void execute(BPlayer p, PlayerData pd, String[] args);

	@Override
	public void execute(final CommandSender sender, final String[] args) {

		if (!(sender instanceof ProxiedPlayer))
			return;

		final BPlayer p = BPlayerHandler.get().getPlayer(sender.getName());

		GameServers.get().getTasksManager().addTask(() -> {

			if (!this.needLogin || BPlayerHandler.get().getPlayer(p.getName().toLowerCase()).isLogged())
				this.execute(p, p.getData(), args);
			else
				p.sendMessage("§cVous devez être connecté !");

		});

	}

}
