package net.neferett.linaris.commands.moderation.bans;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.neferett.linaris.commands.ModeratorCommand;
import net.neferett.linaris.managers.bans.DoubleAccount;
import net.neferett.linaris.managers.bans.DoubleAccount.MODE;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BPlayerHandler;
import net.neferett.linaris.managers.player.BStaff;

public class DCCommands extends ModeratorCommand {

	public DCCommands() {
		super("dc", 1);
	}

	@Override
	public void execute(final BStaff p, final String[] args) {
		if (args.length != 1) {
			p.sendMessage("§cUtilisation§f: §c/dc <pseudo>");
			return;
		}

		final BPlayer pl = BPlayerHandler.get().getPlayer(args[0]);

		if (pl == null) {
			p.sendMessage("§cLe joueur n'est pas connecté !");
			return;
		}

		p.sendMessage("§aListe de tous les doubles comptes§f: §f[§aEN LIGNE§f] §f[§7HORS-LIGNE§f] §f[§cBANNI§f]");
		p.sendMessage("");

		final DoubleAccount d = DoubleAccount.get();

		final StringBuilder b = new StringBuilder();
		final AtomicInteger i = new AtomicInteger(0);

		final Map<String, String> all = d.getDoubleAccount(pl.getAddress());

		all.forEach((name, s) -> {
			b.append(MODE.getValuesByString(s).getColor() + name + (i.get() == all.size() - 1 ? "" : "§f, "));
			i.getAndIncrement();
		});

		p.sendMessage(b.toString());
		p.sendMessage("");

	}

}
