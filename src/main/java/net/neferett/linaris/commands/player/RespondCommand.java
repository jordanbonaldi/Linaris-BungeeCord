package net.neferett.linaris.commands.player;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.api.SettingsManager;
import net.neferett.linaris.commands.PlayerCommand;
import net.neferett.linaris.managers.player.BPlayer;

public class RespondCommand extends PlayerCommand {

	public RespondCommand() {
		super("r", true);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(final BPlayer p, final PlayerData pd, final String[] args) {
		if (args.length >= 1) {

			if (!pd.contains("lastplayermessage")) {
				p.sendMessage("§eunknow§c n'est pas en ligne !");
				return;
			}

			final String lastName = pd.get("lastplayermessage");
			final ProxiedPlayer target = GameServers.get().getProxy().getPlayer(lastName);
			if (target == null) {
				p.sendMessage("§e" + lastName + "§c n'est pas en ligne !");
				return;
			}

			if (!SettingsManager.getSetting(target.getName(), "messages-enable", "on").equals("on")) {
				p.sendMessage("§e" + target.getName() + "§c n'accepte pas de message !");
				return;
			}

			final StringBuilder sb = new StringBuilder("");
			for (final String arg : args)
				sb.append(arg + " ");

			p.sendMessage("§3Envoyé à §e" + target.getName() + ":§7 " + sb.toString().trim());
			target.sendMessage("§3Reçu de §e" + p.getName() + ":§d " + sb.toString().trim());

			// Send Sound

			return;
		}

		p.sendMessage("§e/r <message> §fpour renvoyer un message à votre dernier correspondant");
	}

}
