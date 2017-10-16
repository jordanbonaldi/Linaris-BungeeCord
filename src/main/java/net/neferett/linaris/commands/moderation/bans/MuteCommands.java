package net.neferett.linaris.commands.moderation.bans;

import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.commands.ModeratorCommand;
import net.neferett.linaris.managers.bans.BanManager;
import net.neferett.linaris.managers.bans.BanReason;
import net.neferett.linaris.managers.player.BStaff;

public class MuteCommands extends ModeratorCommand {

	public MuteCommands() {
		super("mute", 1);
	}

	@Override
	public void execute(final BStaff p, final String[] args) {
		if (args.length != 2) {
			p.sendMessage("§cUtilisation§f: §c/mute <pseudo> <type>");
			return;
		}

		final String player = args[0];

		final BanReason b = BanManager.get().getReasons().stream()
				.filter(e -> e.getName().toLowerCase().equals(args[1].toLowerCase())).findFirst().orElse(null);

		if (b == null) {
			p.sendMessage("§aMerci de choisir parmis ces raisons§f: ");
			p.sendMessage("");
			p.sendMessage("§c" + StringUtils.join(
					BanManager.get().getReasons().stream().map(e -> e.getName()).collect(Collectors.toList()),
					"§f, §c"));
			p.sendMessage("");
			return;
		}

		int time = 0;

		final PlayerData pd = GameServers.get().getPlayerDataManager().getPlayerData(player);

		if (!pd.contains("mute-" + b.getName())) {
			pd.setInt("mute-" + b.getName(), b.getTimeinc());
			time = b.getTimeinc();
		} else if (pd.getInt("mute-" + b.getName()) < b.getTimemax()) {
			final int i = pd.getInt("mute-" + b.getName());
			pd.setInt("mute-" + b.getName(), i + b.getTimeinc());
			time = i;
		} else
			time = b.getTimemax();

		BanManager.get().pseudoMute(player, p.getName(), time, b.getName(), true);

	}

}
