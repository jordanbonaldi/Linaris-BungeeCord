package net.neferett.linaris.commands.administration;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.commands.ModeratorCommand;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BPlayerHandler;
import net.neferett.linaris.managers.player.BStaff;

import java.util.Arrays;
import java.util.stream.Collectors;

public class BoutiqueCommand extends Command {

	public BoutiqueCommand() {
		super("boutique");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(final CommandSender sender, final String[] args) {
		if (!(sender instanceof ProxiedPlayer))
			GameServers.get().getProxy().broadcast("§f[§cBoutique§f] §7Le joueur §e" + args[0] + " §7vient d'acheter §e" +
					String.join(" ", Arrays.asList(args).subList(1, args.length)));
	}

}
