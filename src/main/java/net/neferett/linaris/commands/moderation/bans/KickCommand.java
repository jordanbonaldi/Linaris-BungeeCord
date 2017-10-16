package net.neferett.linaris.commands.moderation.bans;

import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.neferett.linaris.commands.ModeratorCommand;
import net.neferett.linaris.managers.bans.BanManager;
import net.neferett.linaris.managers.bans.BanReason;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BPlayerHandler;
import net.neferett.linaris.managers.player.BStaff;

public class KickCommand extends ModeratorCommand {

	public KickCommand() {
		super("kick", 2);
	}

	@Override
	public void execute(final BStaff p, final String[] args) {
		if (args.length != 2) {
			p.sendMessage("§cUtilisation§f: §c/kick <pseudo> <type>");
			return;
		}

		final String player = args[0];

		final int time = 0;
		final BanReason b = BanManager.get().getReasons().stream()
				.filter(e -> e.getName().toLowerCase().equals(args[1].toLowerCase())).findFirst().orElse(null);

		if (b == null) {
			p.sendMessage("§aMerci de choisir parmis ces raisons§f: ");
			p.sendMessage("");
			p.sendMessage("§c" + StringUtils.join(
					BanManager.get().getReasons().stream().map(e -> e.getName()).collect(Collectors.toList()),
					"§f, §c"));
			p.sendMessage("");
		}

		final BPlayer pl = BPlayerHandler.get().getPlayer(player);
		if (pl != null) {
			ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText("§f[§cKick§f] §e" + pl.getName()
					+ " §7a été kick par §e" + p.getName() + "§7 pour§f: §c" + b.getName()));
			pl.quit(BanManager.get().KickEject(false, b.getName(), System.currentTimeMillis(), time, p.getName()));
		}

	}

}
