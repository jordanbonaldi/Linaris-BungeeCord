package net.neferett.linaris.commands.moderation.bans;

import net.neferett.linaris.commands.ModeratorCommand;
import net.neferett.linaris.managers.bans.BanManager;
import net.neferett.linaris.managers.player.BStaff;

public class UnBanCommand extends ModeratorCommand {

	public UnBanCommand() {
		super("unban", 2);
	}

	@Override
	public void execute(final BStaff p, final String[] args) {
		if (args.length != 1) {
			p.sendMessage("§cUtilisation§f: §c/ban <pseudo>");
			return;
		}

		final String player = args[0];

		if (BanManager.get().isBan(player) == null) {
			p.sendMessage("§cLe joueur n'est pas ban !");
			return;
		}

		p.sendMessage("§7Le joueur a été débanni !");
		BanManager.get().unBan(player);

	}

}
