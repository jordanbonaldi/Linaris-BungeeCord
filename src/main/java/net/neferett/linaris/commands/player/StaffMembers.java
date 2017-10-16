package net.neferett.linaris.commands.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.api.Rank;
import net.neferett.linaris.commands.PlayerCommand;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BPlayerHandler;
import net.neferett.linaris.utils.time.TimeUtils;

public class StaffMembers extends PlayerCommand {

	public class Staff {

		Map<String, String>	datas;

		String				name;

		public Staff(final String name, final Map<String, String> datas) {
			this.name = name;
			this.datas = datas;
		}

		public int getInt(final String k) {
			return Integer.parseInt(this.datas.get(k));
		}

		public String getName() {
			return this.name;
		}

		public int getPoints() {
			return this.getInt("points");
		}

		public Rank getRank() {
			return Rank.get(this.getInt("rank"));
		}

		public boolean isOnline() {
			return GameServers.get().getProxy().getPlayer(this.name) != null;
		}

	}

	String		f			= "§7§m-----------------------------------------";

	List<Staff>	staffcache	= new ArrayList<>();

	public StaffMembers() {
		super("staffmembers", true, "staffs");
	}

	public TextComponent build(final Staff s) {
		final String name = Character.toUpperCase(s.getName().charAt(0)) + s.getName().substring(1);
		final TextComponent text = new TextComponent(
				"   §" + s.getRank().getColor() + "§n" + name + "§r   §f§l->   §" + s.getRank().getColor()
						+ s.getRank().getName() + "   §f[" + (s.isOnline() ? "§aEN LIGNE" : "§cHORS-LIGNE") + "§f]");
		text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/m " + s.getName()));
		text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				TextComponent.fromLegacyText(this.getPlayersInfo(s).toString())));
		return text;
	}

	@Override
	public void execute(final BPlayer p, final PlayerData pd, final String[] args) {

		this.refresh();

		p.sendMessage(this.f);
		p.sendMessage("");

		if (args.length != 1) {

			p.sendMessage("§aListe de tous les membres du staff§f: ");

			this.staffcache.forEach(e -> {
				p.sendMessage("");
				p.sendMessage(this.build(e));
			});
		} else {
			final Staff s = this.staffcache.stream().filter(e -> e.getName().equalsIgnoreCase(args[0])).findFirst()
					.orElse(null);
			if (s == null) {
				p.sendMessage("§cCe membre du staff n'existe pas !");
				return;
			}

			p.sendMessage("§7Pseudo§f: §" + s.getRank().getColor() + Character.toUpperCase(s.getName().charAt(0))
					+ s.getName().substring(1));
			p.sendMessage("");
			p.sendMessage("§7Rang§f: §" + s.getRank().getColor() + s.getRank().getName());
			p.sendMessage("");
			p.sendMessage("§7Points§f: §" + this.getColorPoints(s.getPoints()) + s.getPoints());
			p.sendMessage("");
			p.sendMessage("§7Actuellement§f: " + (s.isOnline() ? "§aEN LIGNE" : "§cHORS-LIGNE"));
		}

		p.sendMessage("");
		p.sendMessage(this.f);
	}

	char getColorPoints(final int points) {
		return points > 75 ? 'a' : points > 50 ? 'b' : points > 30 ? 'e' : points > 15 ? '6' : 'c';
	}

	private StringBuilder getPlayersInfo(final Staff s) {
		final BPlayer p = BPlayerHandler.get().getPlayer(s.name);

		final PlayerData data = GameServers.get().getPlayerDataManager().getPlayerData(s.name);
		if (p == null)
			return new StringBuilder("§7Points§f: §" + this.getColorPoints(s.getPoints()) + s.getPoints()).append("\n")
					.append("§7Vu pour la derniere fois, il y a §c"
							+ (!data.contains("LastConnection") ? "Jamais" : TimeUtils.minutesToDayHoursMinutes(
									System.currentTimeMillis() / 1000 - data.getLong("LastConnection") / 1000)));
		return new StringBuilder("§7Serveur§f: §a" + p.getServName()).append("\n")
				.append("§7Points§f: §" + this.getColorPoints(s.getPoints()) + s.getPoints()).append("\n")
				.append("§7En jeu depuis§f: §c" + p.onlineSince()).append("\n")
				.append("§7Version§f: §c" + p.getVersion()).append("\n").append("§eClique pour envoyer un message !");
	}

	public void refresh() {
		this.staffcache.clear();
		this.staffcache = BPlayerHandler.get().getAllStaffs().entrySet().stream()
				.map(a -> new Staff(a.getKey(), a.getValue()))
				.sorted((a1, a2) -> a2.getRank().getModerationLevel() - a1.getRank().getModerationLevel())
				.collect(Collectors.toList());
	}

}
