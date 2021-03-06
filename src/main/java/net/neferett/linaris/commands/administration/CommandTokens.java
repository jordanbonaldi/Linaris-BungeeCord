package net.neferett.linaris.commands.administration;

import java.util.Map;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BPlayerHandler;

public class CommandTokens extends Command {

	public CommandTokens() {
		super("coins");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(final CommandSender sender, final String[] args) {
		if (!(sender instanceof ProxiedPlayer))
			if (args.length < 2) {
				sender.sendMessage("§c/coins give|get|remove|set <player> <value>");
				return;
			} else {
				final BPlayer p = BPlayerHandler.get().getPlayer(args[1]);

				if (p == null) {
					sender.sendMessage(TextComponent.fromLegacyText("Le joueur n'existe pas !"));
					return;
				}

				if (args[0].equalsIgnoreCase("give")) {
					p.addTokens(Integer.parseInt(args[2]));
					if (args[0].equalsIgnoreCase("remove")) {
						p.removeTokens(Integer.parseInt(args[2]));
						sender.sendMessage("§f§m----------- §r§c Boutique §f§m-----------");
						sender.sendMessage("§f");
						sender.sendMessage(
								"§7Vous avez ajouté §e" + Integer.parseInt(args[2]) + " coins §7a " + p.getName());
						sender.sendMessage("");
						sender.sendMessage("§7Le joueur §e" + p.getName() + " §7a désormais §e"
								+ p.getData().getTokens() + " coins");
						sender.sendMessage("");
					}
					return;
				} else if (args[0].equalsIgnoreCase("get")) {
					sender.sendMessage("§f§m----------- §r§c Boutique §f§m-----------");
					sender.sendMessage("§f");
					sender.sendMessage(
							"§7Le joueur §e" + p.getName() + " §7a §e" + p.getData().getTokens() + " coins");
					sender.sendMessage("");
					return;
				} else if (args[0].equalsIgnoreCase("remove")) {
					p.removeTokens(Integer.parseInt(args[2]));
					sender.sendMessage("§f§m----------- §r§c Boutique §f§m-----------");
					sender.sendMessage("§f");
					sender.sendMessage(
							"§7Vous avez rétiré §e" + Integer.parseInt(args[2]) + " coins §7a " + p.getName());
					sender.sendMessage("");
					sender.sendMessage(
							"§7Le joueur §e" + p.getName() + " §7a désormais §e" + p.getData().getTokens() + " coins");
					sender.sendMessage("");
					return;
				} else if (args[0].equalsIgnoreCase("set")) {
					p.setTokens(Integer.parseInt(args[2]));
					sender.sendMessage("§f§m----------- §r§c Boutique §f§m-----------");
					sender.sendMessage("§f");
					sender.sendMessage("§7Vous avez set §e" + Integer.parseInt(args[2]) + " coins §7a " + p.getName());
					sender.sendMessage("");
					sender.sendMessage(
							"§7Le joueur §e" + p.getName() + " §7a ainsi §e" + p.getData().getTokens() + " coins");
					sender.sendMessage("");
					return;
				}
			}
	}

}
