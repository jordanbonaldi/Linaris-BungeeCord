package net.neferett.linaris.commands.player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.commands.PlayerCommand;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BPlayerHandler;
import net.neferett.linaris.managers.player.cheat.HackEnum;

public class CommandReport extends PlayerCommand {

	public CommandReport() {
		super("report", true);
	}

	@Override
	public void execute(final BPlayer p, final PlayerData pd, final String[] args) {

		final List<String> hacks = Arrays.asList(HackEnum.values()).stream()
				.map(e -> Character.toUpperCase(e.toString().charAt(0)) + e.toString().toLowerCase().substring(1))
				.collect(Collectors.toList());

		if (p.isTimer("report", 120)) {
			p.sendMessage("§cMerci d'attendre avant de refaire cette commande !");
			return;
		}

		if (args.length >= 1) {
			final ProxiedPlayer pa = ProxyServer.getInstance().getPlayer(args[0]);

			if (pa == null) {
				p.sendMessage("§cCe joueur n'existe pas.");
				return;
			}
		}

		if (args.length <= 1) {
			p.sendMessage("§eMerci de bien vouloir respecter la syntaxe suivante§f:");
			p.sendMessage("");
			p.sendMessage("   §a/report <joueur> <cheat | insultes | bug | pub> <type>");
			p.sendMessage("");
		} else if (args.length == 2 && args[1].equalsIgnoreCase("insultes"))
			this.sendReport(p, args[0], "Insultes", null);
		else if (args.length == 2 && args[1].equalsIgnoreCase("pub"))
			this.sendReport(p, args[0], "PUB", null);
		else if (args.length == 2 && args[1].equalsIgnoreCase("bug"))
			p.sendMessage("§7Merci de bien vouloir expliquer votre §aBug");
		else if (args.length == 3 && args[1].equalsIgnoreCase("bug")) {
			final StringBuilder b = new StringBuilder();

			for (int i = 2; i < args.length; i++)
				b.append(args[i]);

			this.sendReport(p, args[0], "Bug", b.toString());
		} else if (args.length == 2 && args[1].equalsIgnoreCase("cheat"))
			this.hackChoose(p, hacks);
		else if (args.length == 3 && args[1].equalsIgnoreCase("cheat"))
			if (hacks.stream().map(e -> e.toLowerCase()).collect(Collectors.toList()).contains(args[2].toLowerCase()))
				this.sendHackReport(p, args[0], "Cheat", HackEnum.getEnumByName(args[2]));
			else
				this.hackChoose(p, hacks);

	}

	void hackChoose(final BPlayer p, final List<String> d) {
		p.sendMessage("§eMerci de bien vouloir choisir entre les Hacks suivant§f: ");
		p.sendMessage("");
		p.sendMessage("§7" + StringUtils.join(d, "§f, §7"));
	}

	void sendHackReport(final BPlayer reporter, final String reported, final String type, final HackEnum e) {
		reporter.addTimer("report");
		if (BPlayerHandler.get().getOnlineStaffs().size() == 0) {
			reporter.sendMessage("§cAucun membre du staff n'est en ligne, votre report ne peut pas être envoyé !");
			return;
		} else
			reporter.sendMessage("§7Votre report vient d'être envoyé aux §6Modérateurs");

		final BPlayer p = BPlayerHandler.get().getPlayer(reported);

		BPlayerHandler.get().getOnlineStaffs().forEach(staff -> {
			if (staff.getRank().getVipLevel() > 1)
				staff.sendMessage(
						"§f[§cREPORT§f] §e" + reporter.getName() + "§7 a report §e" + reported + "§7 pour§f: §e" + type
								+ " -> " + e.getName() + " §f(§7Total§f: §e" + p.getCheatData().getBan(e) + " Bans§f)");
		});
	}

	void sendReport(final BPlayer reporter, final String reported, final String type, final String infos) {
		if (BPlayerHandler.get().getOnlineStaffs().size() == 0) {
			reporter.sendMessage("§cAucun membre du staff n'est en ligne, votre report ne peut pas être envoyé !");
			return;
		} else
			reporter.sendMessage("§7Votre report vient d'être envoyé aux §6Modérateurs");

		BPlayerHandler.get().getOnlineStaffs().forEach(staff -> {
			if (staff.getRank().getVipLevel() > 1)
				staff.sendMessage("§f[§cREPORT§f] §e" + reporter.getName() + "§7 a report §e" + reported
						+ "§7 pour§f: §e" + type + (infos != null ? " -> " + infos : ""));
		});
	}

}
