package net.neferett.linaris.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.managers.bans.BanManager;
import net.neferett.linaris.managers.bans.BanManager.PseudoMute;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BPlayerHandler;
import net.neferett.linaris.utils.time.TimeUtils;

public class ChatEvents implements Listener {

	List<String> forbiddenCmds = new ArrayList<>();

	public ChatEvents() {
		this.forbiddenCmds.add("/bukkit:");
		this.forbiddenCmds.add("/pl");
		this.forbiddenCmds.add("/about");
		this.forbiddenCmds.add("/ver");
		this.forbiddenCmds.add("/me");
		this.forbiddenCmds.add("/tell");
		this.forbiddenCmds.add("/reload");
		this.forbiddenCmds.add("/minecraft:");
		this.forbiddenCmds.add("/icanhasbukkit");
		this.forbiddenCmds.add("/ee");
		this.forbiddenCmds.add("/eb");
	}

	@EventHandler
	public void onPlayerChat(final ChatEvent e) {
		if (!(e.getSender() instanceof ProxiedPlayer))
			return;

		final BPlayer p = BPlayerHandler.get().getPlayer((ProxiedPlayer) e.getSender());
		final String lowerMessage = e.getMessage().toLowerCase();

		if (p == null)
			return;

		if (e.isCommand()) {
			final String command = e.getMessage().split(" ")[0];

			if (command.contains("cmdurl")) {
				e.setMessage("");
				e.setCancelled(true);
				return;
			}

			if (!command.equalsIgnoreCase("/login") && !command.equalsIgnoreCase("/log")
					&& !command.equalsIgnoreCase("/l") && !command.equalsIgnoreCase("/register")
					&& !command.equalsIgnoreCase("/reg") && p.getServName().equalsIgnoreCase("Login")) {
				e.setCancelled(true);
				return;
			}
		}

		if (!e.isCommand() || e.isCommand()
				&& (lowerMessage.startsWith("/w") || lowerMessage.startsWith("/m") || lowerMessage.startsWith("/r"))) {
			final BanManager bm = BanManager.get();

			final PseudoMute ps = bm.isMute(p.getName());
			if (ps != null) {
				final long tms = ps.getFromTime() / 1000 + ps.getTime() - System.currentTimeMillis() / 1000;

				p.sendMessage("§7Vous êtes mute pour encore §e" + TimeUtils.minutesToDayHoursMinutes(tms) + "§7 !");
				e.setCancelled(true);
				return;
			}

		}

		if (lowerMessage.startsWith("connected with") && lowerMessage.endsWith("minechat")
				|| e.getMessage().trim().length() < 2) {
			e.setCancelled(true);
			return;
		}

		if (p.getRank().getModerationLevel() < 3 && !e.isCommand()
				|| e.isCommand() && (lowerMessage.startsWith("/w") || lowerMessage.startsWith("/m")))
			GameServers.get().getAntiSpamListener().onChat(e);

		if (!e.isCommand() || e.isCommand() && (lowerMessage.startsWith("/w") || lowerMessage.startsWith("/m")))
			GameServers.get().getAntiSwear().onSwear(e);

		if (this.forbiddenCmds.stream().filter(msg -> lowerMessage.startsWith(msg.toLowerCase())).findFirst()
				.orElse(null) != null) {
			p.sendMessage("Unknown command. Type \"/help\" for help.");
			e.setCancelled(true);
			return;
		}

		if (e.getMessage().startsWith("*")) {
			e.setCancelled(true);
			this.partyChatTask(p, e);
			return;
		}

		if (e.getMessage().toLowerCase().startsWith("@staff") && p.getRank().getModerationLevel() >= 1) {
			e.setCancelled(true);
			this.staffChatTask(p, e);
			return;
		}
	}

	@EventHandler
	public void onTabCompleteEvent(final TabCompleteEvent e) {
		final String[] args = e.getCursor().split(" ");
		if (e.getCursor().equals("/")) {
			e.setCancelled(true);
			return;
		}

		final String checked = (args.length > 0 ? args[args.length - 1] : e.getCursor()).toLowerCase();

		if (checked.length() <= 1)
			return;

		for (final ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
			final String playerName = player.getName();
			if (playerName.toLowerCase().startsWith(checked))
				e.getSuggestions().add(playerName);
		}
	}

	public void partyChatTask(final BPlayer p, final ChatEvent e) {
		GameServers.get().getTasksManager().addTask(() -> {
			final UUID party = p.getParty();
			if (party != null)
				p.sendMessagetoParty("§b" + p.getName() + ": §e" + e.getMessage().replaceFirst("\\*", "").trim());
			else
				p.sendMessage("§cL'étoile §e* §csert à parler en groupe et vous n'avez pas de groupe !");
			return;
		});
	}

	public void staffChatTask(final BPlayer p, final ChatEvent e) {
		GameServers.get().getTasksManager().addTask(() -> {

			if (e.getMessage().toLowerCase().startsWith("@staff")) {
				if (e.getMessage().toLowerCase().equals("@staff")) {
					p.sendMessage("§cMerci d'entrer un message");
					return;
				}

				BPlayerHandler.get().getOnlineStaffs().forEach(staff -> {
					staff.sendMessage(p.getRank().getPrefix(p.getData()) + "§c" + p.getName() + " §6-> §aStaff§e"
							+ e.getMessage().toLowerCase().replaceFirst("@staff", ""));
				});
			}
			return;
		});
	}

}
