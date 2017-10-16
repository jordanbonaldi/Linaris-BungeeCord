package net.neferett.linaris.commands.moderation.bans;

import net.neferett.linaris.commands.ModeratorCommand;
import net.neferett.linaris.managers.bans.BanManager;
import net.neferett.linaris.managers.player.BStaff;

public class UnMuteCommands extends ModeratorCommand {

	public UnMuteCommands() {
		super("unmute", 1);
	}

	@Override
	public void execute(final BStaff p, final String[] args) {
		if (args.length != 1) {
			p.sendMessage("§cUtilisation§f: §c/unmute <pseudo>");
			return;
		}

		final String player = args[0];

		if (BanManager.get().isMute(player) == null) {
			p.sendMessage("§cLe joueur n'est pas mute !");
			return;
		}

		p.sendMessage("§7Le joueur a été unmute !");
		BanManager.get().unMute(player);

	}

}
