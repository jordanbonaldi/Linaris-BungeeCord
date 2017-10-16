package net.neferett.linaris.commands.player;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.commands.PlayerCommand;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BPlayerHandler;
import net.neferett.linaris.managers.player.BStaff;

public class CommandStaff extends PlayerCommand {

	public CommandStaff() {
		super("staff", true);
	}

	@Override
	public void execute(final BPlayer p, final PlayerData pd, final String[] args) {

		final AtomicInteger i = new AtomicInteger(0);

		final List<BStaff> staff = BPlayerHandler.get().getOnlineStaffs();

		p.sendMessage(ChatColor.GRAY + "§m-----------------------------------------------------");
		p.sendMessage("");
		final String staffsize = staff.size() == 0 ? "" : "§f(§c" + staff.size() + "§f)";
		p.sendMessage(ChatColor.GREEN + "" + "Staff en ligne " + staffsize + " §f:");

		final TextComponent text = new TextComponent();

		if (staff.size() != 0) {
			staff.forEach(s -> {
				if (i.incrementAndGet() == staff.size())
					text.addExtra(s.build());
				else {
					text.addExtra(s.build());
					text.addExtra(", ");
				}
			});
			p.sendMessage(text);
		} else
			p.sendMessage(ChatColor.RED + "Aucun membre du staff n'est en ligne pour le moment.");
		p.sendMessage("");
		p.sendMessage(ChatColor.GRAY + "§m-----------------------------------------------------");

	}

}
