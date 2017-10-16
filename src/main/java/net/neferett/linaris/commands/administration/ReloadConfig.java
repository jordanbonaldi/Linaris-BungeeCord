package net.neferett.linaris.commands.administration;

import java.io.IOException;

import net.neferett.linaris.GameServers;
import net.neferett.linaris.commands.ModeratorCommand;
import net.neferett.linaris.managers.player.BStaff;

public class ReloadConfig extends ModeratorCommand {

	public ReloadConfig() {
		super("rlconfig", 4);
	}

	@Override
	public void execute(final BStaff p, final String[] args) {
		GameServers.get().getConfigManager().refresh();
		try {
			GameServers.get().getAntiSwear().refresh();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		p.sendMessage("§7Configuration téléchargée !");
	}

}
