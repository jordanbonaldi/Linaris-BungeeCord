package net.neferett.linaris.commands.player;

import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.commands.PlayerCommand;
import net.neferett.linaris.managers.player.BPlayer;

public class CommandCoins extends PlayerCommand {

	public CommandCoins() {
		super("coins", true);
	}

	@Override
	public void execute(final BPlayer p, final PlayerData pd, final String[] args) {
		p.sendMessage("§7Vous avez actuellement §e" + pd.getCoins() + " Coins§7!");
	}

}
