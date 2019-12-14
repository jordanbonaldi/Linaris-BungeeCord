package net.neferett.linaris.commands.administration;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.ranks.RankAPI;
import net.neferett.linaris.api.server.GameServer;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BPlayerHandler;

import java.util.stream.Collectors;

public class CommandShop extends Command {

	public CommandShop() {
		super("shop");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(final CommandSender sender, final String[] args) {
		if (!(sender instanceof ProxiedPlayer))
			if (args.length < 2)
				sender.sendMessage("§c/shoprank <player> <value>");
			else {
				final BPlayer p = BPlayerHandler.get().getPlayer(args[0]);

				if (p == null) {
					sender.sendMessage(TextComponent.fromLegacyText("Le joueur n'existe pas !"));

					return;
				}

				RankAPI rank = GameServers.get().getRanksmanager().getRank(args[1]);

				if (rank == null) {
					sender.sendMessage(GameServers.get().getRanksmanager().getRanks().stream().map(RankAPI::getName).collect(Collectors.joining(", ")));

					return;
				}

				p.getData().setRank(rank);
				p.sendMessage("§f§m----------- §r§c Guarden §f§m-----------");
				p.sendMessage("§f");
				p.sendMessage("§aFélicitation !");
				p.sendMessage("§7Vous venez de recevoir le rang §" + rank.getColor() + rank.getName());
				p.sendMessage("");
			}
	}

}
