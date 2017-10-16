package net.neferett.linaris.commands.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelStatistics;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.api.Rank;
import net.neferett.linaris.commands.PlayerCommand;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BPlayerHandler;
import net.neferett.linaris.managers.player.BYoutuber;
import net.neferett.linaris.managers.player.StaffPlayer;
import net.neferett.linaris.managers.player.yt.SubsCount;
import net.neferett.linaris.utils.others.NumberFormater;
import net.neferett.linaris.utils.time.TimeUtils;

public class YTMembers extends PlayerCommand {

	public class YT {

		ChannelStatistics	ch;

		Map<String, String>	datas;

		String				name;

		public YT(final String name, final Map<String, String> datas) {
			this.name = name;
			this.datas = datas;
			if (datas.containsKey("channelid"))
				this.ch = new SubsCount().buildSearch((e) -> e.setId(datas.get("channelid"))).getChannel()
						.getStatistics();
		}

		public boolean contain(final String k) {
			return this.datas.containsKey(k);
		}

		public String get(final String k) {
			return this.datas.get(k);
		}

		public int getInt(final String k) {
			return Integer.parseInt(this.datas.get(k));
		}

		public String getName() {
			return this.name;
		}

		public Rank getRank() {
			return Rank.get(this.getInt("rank"));
		}

		public int getSubs() {
			if (this.ch == null)
				return 0;
			return this.ch.getSubscriberCount().intValue();
		}

		public int getVideos() {
			if (this.ch == null)
				return 0;
			return this.ch.getVideoCount().intValue();
		}

		public int getViews() {
			if (this.ch == null)
				return 0;
			return this.ch.getViewCount().intValue();
		}

		public boolean isOnline() {
			return GameServers.get().getProxy().getPlayer(this.name) != null;
		}

	}

	String		f			= "§7§m-----------------------------------------";

	List<YT>	staffcache	= new ArrayList<>();

	public YTMembers() {
		super("ytmembers", true, "yt");
	}

	public TextComponent build(final YT y) {
		final String name = Character.toUpperCase(y.getName().charAt(0)) + y.getName().substring(1);
		final TextComponent text = new TextComponent(
				"   " + y.getRank().getLogo(GameServers.get().getPlayerDataManager().getPlayerData(name)) + " §f" + name
						+ "§r   §f§l->   §fY§cT" + "   §f[" + (y.isOnline() ? "§aEN LIGNE" : "§cHORS-LIGNE") + "§f]");
		text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/m " + y.getName()));
		text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				TextComponent.fromLegacyText(this.getPlayersInfo(y).toString())));
		return text;
	}

	@Override
	public void execute(final BPlayer p, final PlayerData pd, final String[] args) {

		this.refresh();

		p.sendMessage(this.f);
		p.sendMessage("");

		if (args.length == 0) {

			p.sendMessage("§aListe de tous les §fYou§cTubers§f: ");

			this.staffcache.forEach(e -> {
				p.sendMessage("");
				p.sendMessage(this.build(e));
			});
		} else if (!args[0].equalsIgnoreCase("config")) {
			final YT y = this.staffcache.stream().filter(e -> e.getName().equalsIgnoreCase(args[0])).findFirst()
					.orElse(null);
			if (y == null) {
				p.sendMessage("§cCe youtuber n'existe pas !");
				p.sendMessage("");
				p.sendMessage(this.f);
				return;
			}

			ChannelStatistics ch = null;

			if (y.contain("channelid"))
				ch = new SubsCount().buildSearch((e) -> e.setId(y.get("channelid"))).getChannel().getStatistics();

			p.sendMessage("§7Pseudo§f: §" + y.getRank().getColor() + Character.toUpperCase(y.getName().charAt(0))
					+ y.getName().substring(1));
			p.sendMessage("");
			p.sendMessage("§7Rang§f: §" + y.getRank().getColor() + y.getRank().getName());
			p.sendMessage("");
			p.sendMessage("§7Logo§f: "
					+ y.getRank().getLogo(GameServers.get().getPlayerDataManager().getPlayerData(y.getName())));
			p.sendMessage("");
			p.sendMessage("§7Actuellement§f: " + (y.isOnline() ? "§aEN LIGNE" : "§cHORS-LIGNE"));
			if (ch != null) {
				p.sendMessage("");
				p.sendMessage("§7Abonnés§f: §e" + NumberFormater.format(ch.getSubscriberCount().longValue()));
				p.sendMessage("");
				p.sendMessage("§7Vidéos§f: §e" + NumberFormater.format(ch.getVideoCount().longValue()));
				p.sendMessage("");
				p.sendMessage("§7Vues totales§f: §e" + NumberFormater.format(ch.getViewCount().longValue()));
			}
		} else if (args[0].equalsIgnoreCase("config") && args.length == 2) {
			final BYoutuber y = BPlayerHandler.get().getYT(p.getName());
			final SubsCount sc = new SubsCount().buildSearch((e) -> e.setId(args[1]));
			final Channel c = sc.getChannel();

			if (y == null) {
				p.sendMessage("§cVous n'êtes pas §cYoutuber§c !");
				p.sendMessage("");
				p.sendMessage(this.f);
				return;
			}
			if (c == null) {
				p.sendMessage("§cVotre channel id est erroné !");
				p.sendMessage("");
				p.sendMessage(this.f);
				return;
			}

			final StaffPlayer yt = new StaffPlayer("yt", p.getName());
			yt.set("channelid", args[1]);

			p.sendMessage("§eVous venez de configurer votre chaîne youtube avec succès !");
		}
		p.sendMessage("");
		p.sendMessage(this.f);
	}

	private StringBuilder getPlayersInfo(final YT y) {
		final BPlayer p = BPlayerHandler.get().getPlayer(y.getName());
		final PlayerData data = GameServers.get().getPlayerDataManager().getPlayerData(y.name);

		if (p == null && !y.contain("channelid"))
			return new StringBuilder("§7Vu pour la derniere fois, il y a §c"
					+ (!data.contains("LastConnection") ? "Jamais" : TimeUtils.minutesToDayHoursMinutes(
							System.currentTimeMillis() / 1000 - data.getLong("LastConnection") / 1000)));
		else if (p == null && y.contain("channelid")) {
			final ChannelStatistics ch = new SubsCount().buildSearch((e) -> e.setId(y.get("channelid"))).getChannel()
					.getStatistics();
			return new StringBuilder("§7Abonnés§f: §e" + NumberFormater.format(ch.getSubscriberCount().longValue()))
					.append("\n").append("§7Vidéos§f: §e" + NumberFormater.format(ch.getVideoCount().longValue()))
					.append("\n")
					.append("§7Nombre de vues§f: §e" + NumberFormater.format(ch.getViewCount().longValue()))
					.append("\n")
					.append("§7Vu pour la derniere fois, il y a §c"
							+ (!data.contains("LastConnection") ? "Jamais" : TimeUtils.minutesToDayHoursMinutes(
									System.currentTimeMillis() / 1000 - data.getLong("LastConnection") / 1000)));
		} else if (!y.contain("channelid"))
			return new StringBuilder("§7Serveur§f: §a" + p.getServName()).append("\n")
					.append("§7En jeu depuis§f: §c" + p.onlineSince()).append("\n")
					.append("§7Version§f: §c" + p.getVersion()).append("\n")
					.append("§eClique pour envoyer un message !");

		final ChannelStatistics ch = new SubsCount().buildSearch((e) -> e.setId(y.get("channelid"))).getChannel()
				.getStatistics();

		return new StringBuilder("§7Serveur§f: §a" + p.getServName()).append("\n")
				.append("§7Abonnés§f: §e" + NumberFormater.format(ch.getSubscriberCount().longValue())).append("\n")
				.append("§7Vidéos§f: §e" + NumberFormater.format(ch.getVideoCount().longValue())).append("\n")
				.append("§7Nombre de vues§f: §e" + NumberFormater.format(ch.getViewCount().longValue())).append("\n")
				.append("§7En jeu depuis§f: §c" + p.onlineSince()).append("\n")
				.append("§7Version§f: §c" + p.getVersion()).append("\n").append("§eClique pour envoyer un message !");
	}

	public void refresh() {
		this.staffcache.clear();
		this.staffcache = BPlayerHandler.get().getAllYTs().entrySet().stream()
				.map(a -> new YT(a.getKey(), a.getValue())).sorted((a1, a2) -> a2.getSubs() - a1.getSubs())
				.collect(Collectors.toList());
	}

}
