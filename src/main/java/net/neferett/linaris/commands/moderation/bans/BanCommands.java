package net.neferett.linaris.commands.moderation.bans;

import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.commands.ModeratorCommand;
import net.neferett.linaris.managers.bans.BanManager;
import net.neferett.linaris.managers.bans.BanReason;
import net.neferett.linaris.managers.bans.DoubleAccount;
import net.neferett.linaris.managers.bans.DoubleAccount.MODE;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BPlayerHandler;
import net.neferett.linaris.managers.player.BStaff;

public class BanCommands extends ModeratorCommand {

	public BanCommands() {
		super("ban", 2);
	}

	@Override
	public void execute(final BStaff p, final String[] args) {
		if (args.length != 2) {
			p.sendMessage("§cUtilisation§f: §c/ban <pseudo> <type>");
			return;
		}

		final String player = args[0];

		int time = 0;
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

		final BPlayer pl = BPlayerHandler.get().getPlayer(player);

		if (pl != null)
			DoubleAccount.get().updateMode(pl.getAddress(), pl.getName(), MODE.BANNED);

		final PlayerData pd = GameServers.get().getPlayerDataManager().getPlayerData(player);

		if (b.getTimeinc() < 0)
			time = -1;
		else if (!pd.contains("bans-" + b.getName())) {
			pd.setInt("bans-" + b.getName(), b.getTimeinc());
			time = b.getTimeinc();
		} else if (pd.getInt("bans-" + b.getName()) < b.getTimemax()) {
			final int i = pd.getInt("bans-" + b.getName());
			pd.setInt("bans-" + b.getName(), i + b.getTimeinc());
			time = i;
		} else
			time = b.getTimemax();

		BanManager.get().pseudoBan(player, p.getName(), time, b.getName(), false, null, true);

		if (pl != null)
			pl.quit(BanManager.get().BannedEject(time < 0 ? true : false, b.getName(), System.currentTimeMillis(), time,
					p.getName()));

		// if (p != null) {
		// pl.connectTo("Lobby");
		// pl.sendMessage(BanManager.get().BannedEdject(time < 0, b.getName(),
		// System.currentTimeMillis(), time,
		// p.getName()));
		// }

	}

}
