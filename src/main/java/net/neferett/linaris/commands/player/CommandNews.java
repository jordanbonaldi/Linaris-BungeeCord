package net.neferett.linaris.commands.player;

import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.commands.PlayerCommand;
import net.neferett.linaris.managers.player.BPlayer;

public class CommandNews extends PlayerCommand {

	public CommandNews() {
		super("news", true);
	}

	@Override
	public void execute(final BPlayer p, final PlayerData pd, final String[] args) {
		p.sendNews();
	}

}
