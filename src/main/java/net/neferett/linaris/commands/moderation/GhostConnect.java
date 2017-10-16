package net.neferett.linaris.commands.moderation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.api.server.GameServer;
import net.neferett.linaris.commands.PlayerCommand;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.utils.player.PlayersUtils;

public class GhostConnect extends PlayerCommand implements TabExecutor {

	public GhostConnect() {
		super("ghostconnect", true);
	}

	@Override
	public void execute(final BPlayer p, final PlayerData pd, final String[] args) {

		if (pd.getRank().getModerationLevel() < 2)
			return;

		if (args.length == 1) {

			final GameServer server = GameServers.get().getServersManager().getServer(args[0]);

			if (server == null) {
				p.sendMessage("§cLe serveur §e" + args[0] + " §cn'existe pas !");
				return;
			}

			p.sendMessage("§cServer: §9" + server.getServName());

			server.wantGoWithGhost(p, null);

		}

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
