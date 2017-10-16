package net.neferett.linaris.commands.moderation.bans;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.commands.ModeratorCommand;
import net.neferett.linaris.managers.bans.BanManager;
import net.neferett.linaris.managers.bans.BanReason;
import net.neferett.linaris.managers.bans.DoubleAccount;
import net.neferett.linaris.managers.bans.DoubleAccount.MODE;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BPlayerHandler;
import net.neferett.linaris.managers.player.BStaff;

public class IPBanCommands extends ModeratorCommand {

	public IPBanCommands() {
		super("ipban", 2);
	}

	@Override
	public void execute(final BStaff p, final String[] args) {
		if (args.length != 2) {
			p.sendMessage("§cUtilisation§f: §c/ipban <pseudo> <type>");
			return;
		}

		final BPlayer pl = BPlayerHandler.get().getPlayer(args[0]);

		if (pl == null) {
			p.sendMessage("§cLe joueur §e" + args[0] + " §cn'existe pas !");
			return;
		}

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

		final AtomicInteger time = new AtomicInteger(0);

		DoubleAccount.get().updateMode(pl.getAddress(), pl.getName(), MODE.BANNED);

		final PlayerData pd = pl.getData();

		if (b.getTimeinc() < 0)
			time.set(-1);
		else if (!pd.contains("bans-" + b.getName())) {
			pd.setInt("bans-" + b.getName(), b.getTimeinc());
			time.set(b.getTimeinc());
		} else if (pd.getInt("bans-" + b.getName()) < b.getTimemax()) {
			final int i = pd.getInt("bans-" + b.getName());
			pd.setInt("bans-" + b.getName(), i + b.getTimeinc());
			time.set(i);
		} else
			time.set(b.getTimemax());

		BanManager.get().ipBan(pl.getAddress(), pl.getName(), p.getName(), time.get(), b.getName(), true);

		pl.quit(BanManager.get().BannedEject(time.get() < 0 ? true : false, b.getName(), System.currentTimeMillis(),
				time.get(), p.getName()));

		ProxyServer.getInstance().getPlayers().forEach(e -> {
			if (!e.getName().toLowerCase().equals(pl.getName().toLowerCase())
					&& e.getAddress().getAddress().getHostAddress().equals(pl.getAddress())) {
				BanManager.get().ipBan(e.getAddress().getAddress().getHostAddress(), e.getName(), p.getName(),
						time.get(), b.getName(), false);
				e.disconnect(TextComponent.fromLegacyText(BanManager.get().BannedEject(time.get() < 0 ? true : false,
						b.getName(), System.currentTimeMillis(), time.get(), p.getName())));
			}

		});
	}

}
