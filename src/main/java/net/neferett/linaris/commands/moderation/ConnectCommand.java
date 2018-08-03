package net.neferett.linaris.commands.moderation;

import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.api.server.GameServer;
import net.neferett.linaris.commands.PlayerCommand;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BPlayerHandler;

public class ConnectCommand extends PlayerCommand {

	public ConnectCommand() {
		super("connect", true);
	}

	@Override
	public void execute(final BPlayer p, final PlayerData pd, final String[] args) {
		if (pd.getRank().getModerationLevel() < 4)
			return;

		if (args.length == 1) {
			final String serverName = args[0];

			final GameServer server = GameServers.get().getServersManager().getServer(serverName);
			if (server == null) {
				p.sendMessage("§cCe serveur n'existe pas");
				return;
			}

			server.wantGoOn(p, true);
			return;
		}
		if (args.length == 2) {

			final String serverName = args[0];

			final GameServer server = GameServers.get().getServersManager().getServer(serverName);
			if (server == null) {
				p.sendMessage("§cCe serveur n'existe pas");
				return;
			}

			final BPlayer target = BPlayerHandler.get().getPlayer(args[1]);

			if (target == null) {
				p.sendMessage("§cCe joueur n'existe pas");
				return;
			}

			server.wantGoOn(target, true);
			target.sendMessage("§aTéléportation en cours... :: Par " + p.getName());
			p.sendMessage("§aTéléportation du joueur en cours...");
			return;
		}
	}

}
