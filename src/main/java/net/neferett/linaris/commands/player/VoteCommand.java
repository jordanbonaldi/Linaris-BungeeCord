package net.neferett.linaris.commands.player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.neferett.linaris.GameServers;

public class VoteCommand extends Command {

	public VoteCommand(final GameServers plugin) {
		super("vote");
	}

	@Override
	public void execute(final CommandSender sender, final String[] args) {
		if (args.length < 0)
			sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Erreur merci de spécifier un message."));
		else {
			final StringBuilder builder = new StringBuilder();

			for (final String s : args) {
				builder.append(ChatColor.translateAlternateColorCodes('&', s));
				builder.append(" ");
			}

			final String message = builder.substring(0, builder.length() - 1);
			GameServers.get().getProxy().broadcast(TextComponent.fromLegacyText("§f[§c§lVote§f] §c" + message));
		}

	}
}
