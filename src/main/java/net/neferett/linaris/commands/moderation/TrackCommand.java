package net.neferett.linaris.commands.moderation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.api.server.GameServer;
import net.neferett.linaris.commands.PlayerCommand;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.utils.player.PlayersUtils;

public class TrackCommand extends PlayerCommand implements TabExecutor {

	public TrackCommand() {
		super("track", true);
	}

	@Override
	public void execute(final BPlayer p, final PlayerData pd, final String[] args) {

		if (args.length == 1) {

			if (pd.getRank().getModerationLevel() < 2)
				return;

			final ProxiedPlayer target = GameServers.get().getProxy().getPlayer(args[0]);
			if (target == null) {
				p.sendMessage("§e" + args[0] + "§c n'est pas en ligne !");
				return;
			}

			if (target.equals(p)) {
				p.sendMessage("§cVous ne pouvez pas parler tout seul !");
				return;
			}

			final PlayerData targetData = GameServers.get().getPlayerDataManager().getPlayerData(target.getName());

			final GameServer server = targetData.getCurrentServer();

			if (server == null) {
				p.sendMessage("§e" + args[0] + "§c n'est sur aucun serveur !");
				return;
			}

			server.wantGoWithGhost(p, args[0]);

			p.sendMessage("§7Téléportation au joueur §e" + args[0] + "§7 en cours...");
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
