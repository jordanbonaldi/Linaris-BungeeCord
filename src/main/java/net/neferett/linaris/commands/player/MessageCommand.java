package net.neferett.linaris.commands.player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.api.SettingsManager;
import net.neferett.linaris.commands.PlayerCommand;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.utils.player.PlayersUtils;

public class MessageCommand extends PlayerCommand implements TabExecutor {

	public MessageCommand() {
		super("w", true, new String[] { "m", "msg" });
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(final BPlayer p, final PlayerData pd, final String[] args) {

		if (args.length == 1) {

			final String info = args[0];

			if (info.equalsIgnoreCase("on")) {

				SettingsManager.setSetting(p.getName(), "messages-enable", "on");

				p.sendMessage("§7Messages privés : §aActivés");

				return;
			}

			if (info.equalsIgnoreCase("off")) {

				SettingsManager.setSetting(p.getName(), "messages-enable", "off");

				p.sendMessage("§7Messages privés : §cDésactivés");

				return;
			}

			if (info.equalsIgnoreCase("friends")) {

				SettingsManager.setSetting(p.getName(), "messages-enable", "friends");

				p.sendMessage("§7Messages privés : §eAmis uniquement");

				return;
			}

			if (info.equalsIgnoreCase("sound")) {

				final boolean sound = SettingsManager.isEnabled(p.getName(), "messages-sound", true);
				SettingsManager.setSetting(p.getName(), "messages-sound",
						sound ? Boolean.toString(false) : Boolean.toString(true));

				p.sendMessage("§aSon des messages privés: " + (sound ? "§cDésactivés" : "§aActivés"));

				return;
			}
		}

		if (args.length >= 2) {

			final ProxiedPlayer target = GameServers.get().getProxy().getPlayer(args[0]);

			if (target == null) {
				p.sendMessage("§e" + args[0] + "§c n'est pas en ligne !");
				return;
			}

			if (target.equals(p)) {
				p.sendMessage("§cVous ne pouvez pas parler tout seul !");
				return;
			}

			if (!SettingsManager.getSetting(target.getName(), "messages-enable", "on").equals("on")) {
				p.sendMessage("§e" + target.getName() + "§c n'accepte pas de message !");
				return;
			}

			final StringBuilder sb = new StringBuilder("");
			for (int i = 1; i < args.length; i++)
				sb.append(args[i] + " ");

			p.sendMessage("§3Envoyé à §e" + target.getName() + ":§7 " + sb.toString().trim());
			target.sendMessage("§3Reçu de §e" + p.getName() + ":§d " + sb.toString().trim());

			// Send Sound

			pd.set("lastplayermessage", target.getName());

			return;
		}

		p.sendMessage("§e/w <nom du joueur> <message> §fpour envoyer un message");
		p.sendMessage("§e/w on:off §fpour activer ou désactiver vos messages");
		p.sendMessage("§e/w sound §fpour activer ou désactiver le son des messages");
	}

	@Override
	public Iterable<String> onTabComplete(final CommandSender sender, final String[] args) {
		final List<String> result = new ArrayList<>();

		if (args.length < 1)
			return result;
		else {
			result.addAll(PlayersUtils.getOnlineNames().stream()
					.filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList()));

			return result;
		}
	}

}
