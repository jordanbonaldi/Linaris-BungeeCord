package net.neferett.linaris.managers.player;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.api.party.PartiesManagement;
import net.neferett.linaris.api.ranks.RankAPI;
import net.neferett.linaris.api.ranks.RankManager;
import net.neferett.linaris.api.server.GameServer;
import net.neferett.linaris.managers.bans.BanManager;
import net.neferett.linaris.managers.bans.BanManager.IPBans;
import net.neferett.linaris.managers.bans.BanManager.PseudoBans;
import net.neferett.linaris.managers.player.buy.PlayerBuy;
import net.neferett.linaris.managers.player.buy.PlayerBuy.BuyItem;
import net.neferett.linaris.managers.player.cheat.data.CheatData;
import net.neferett.linaris.managers.player.yt.SubsCount;
import net.neferett.linaris.utils.stringutils.Strings;
import net.neferett.linaris.utils.time.TimeUtils;

public class BPlayer {

	CheatData				cd;
	boolean					logged;
	Matcher					matcher;
	String					name;
	long					online;
	ProxiedPlayer			p;
	Pattern					pattern;
	PlayerData				pd;
	HashMap<String, String>	pl	= new HashMap<>();

	SubsCount				sc;

	public BPlayer(final ProxiedPlayer p) {
		this.p = p;
		this.cd = new CheatData(p.getName().toLowerCase());
		this.name = p.getName();
		this.pd = GameServers.get().getPlayerDataManager().getPlayerData(p.getName());
		this.online = System.currentTimeMillis();
		if (!this.getRank().equals(RankManager.getInstance().getRank(10))
				&& BPlayerHandler.get().getAllYTs().containsKey(p.getName().toLowerCase())) {
			final StaffPlayer yt = new StaffPlayer("yt", p.getName());
			yt.remove();
		}
	}

	public void addGroup(final String g) {
		this.p.addGroups(g);
	}

	public void addTimer(final String n) {
		this.pl.put(n, Long.toString(System.currentTimeMillis()));
	}

	public void addTokens(final int t) {
		final int total = this.getData().getTokens() + t;
		this.getData().setInt("tokens", total);
		if (this.p != null && this.p.isConnected()) {
			for (int i = 0; i < 25; i++)
				this.sendMessage("");
			this.sendMessage("§f§m----------- §r§c Boutique §f§m-----------");
			this.sendMessage("§f");
			this.sendMessage("§aFélicitation !");
			this.sendMessage("§7Vous venez de recevoir §e" + t + " Coins");
			this.sendMessage("");
			this.sendMessage("§7Vous avez maintenant §f: §e" + this.getData().getTokens() + " Coins");
			this.sendMessage("");
			new PlayerBuy(BuyItem.TOKEN, this.p.getName(), t, this.p.getAddress().getAddress().getHostAddress(),
					new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss").format(Calendar.getInstance().getTime()),
					this.p.getServer().getInfo().getName()).addToRedis();
		}
	}

	public void addToStaff() {
		new StaffPlayer("staff", this.name, RankManager.getInstance().getRank(7)).setInt("points", 100);
		this.p.removeGroups("modo");
		this.getData().setRank(RankManager.getInstance().getRank(7));
		this.getData().setBoolean("mod", true);
		this.getData().setInt("oldRank", this.getRank().getId());
		this.p.disconnect(TextComponent.fromLegacyText("§cVous faites désormais partie du staff de ??? !"));
	}

	public void callBackConnect(final ServerInfo t, final Callback<Boolean> d) {
		GameServers.get().getProxy().getPlayer(this.p.getName()).connect(t, d);
	}

	public void changeRank(final int id) {
		if (RankManager.getInstance().getRank(id) == null)
			return;
		this.getData().setRank(RankManager.getInstance().getRank(id));
	}

	public void changeRank(final String name) {
		if (RankManager.getInstance().getRank(name) == null)
			return;
		this.getData().setRank(RankManager.getInstance().getRank(name));
	}

	public void connect(final String servname) {
		final GameServer sv = GameServers.get().getServersManager().getServer(servname);
		if (sv == null)
			return;
		sv.wantGoOn(this, true);
	}

	public void connectTo(final String servname) {
		final GameServer serv = GameServers.get().getServersManager().getServersByGameName(servname).stream()
				.filter(gm -> gm.getPlayers() + 1 <= gm.getMaxPlayers() && gm.canJoin()
						&& (this.p.getServer() == null
						|| !this.p.getServer().getInfo().getName().equals(gm.getServName()))).min(Comparator.comparingInt(GameServer::getPlayers)).orElse(null);
		if (serv == null)
			return;
		serv.wantGoOn(this, true);
	}

	public void delGroup(final String g) {
		this.p.removeGroups(g);
	}

	public String getAddress() {
		return this.p.getAddress().getAddress().getHostAddress();
	}

	public CheatData getCheatData() {
		return this.cd;
	}

	public PlayerData getData() {
		return this.pd;
	}

	public String getName() {
		return this.name;
	}

	public UUID getParty() {
		final PartiesManagement pm = GameServers.get().getPartiesManagement();
		if (pm.haveParty(this.p.getName().toLowerCase()))
			return pm.getPlayerParty(this.p.getName().toLowerCase());
		return null;
	}

	public RankAPI getRank() {
		return this.pd.getRank();
	}

	public Server getServ() {
		return this.p.getServer();
	}

	public ServerInfo getServInfo(final String servname) {
		return GameServers.get().getServersManager().getServersByGameName(servname).stream()
				.filter(gm -> gm.getPlayers() + 1 <= gm.getMaxPlayers() && gm.canJoin()
						&& (this.p.getServer() == null
								|| !this.p.getServer().getInfo().getName().equals(gm.getServName())))
				.sorted(Comparator.comparingInt(GameServer::getPlayers))
				.map(gm -> ProxyServer.getInstance().getServerInfo(gm.getServName())).findFirst().orElse(null);
	}

	public String getServName() {
		final String a = this.p.getServer().getInfo().getName();
		return a == null ? "Login" : a;
	}

	public long getTimer(final String n) {
		if (!this.pl.containsKey(n))
			return 0;
		return Long.valueOf(this.pl.get(n));
	}

	public String getVersion() {
		final int v = this.p.getPendingConnection().getVersion();
		return v > 316 ? "1.12" : v > 300 && v < 318 ? "1.11"
				: v > 200 && v < 301 ? "1.10" : v > 47 && v < 111 ? "1.9" : v > 5 && v < 48 ? "1.8" : "1.7";
	}

	public boolean isLogged() {
		return this.logged;
	}

	public boolean isTimer(final String n, final int t) {
		return this.getTimer(n) / 1000 + t - System.currentTimeMillis() / 1000 > 0;
	}

	@SuppressWarnings("deprecation")
	public void logPlayer() throws IOException {
		if (this.isLogged())
			return;
		this.setLogged(true);
		this.pd.set("Log", this.getAddress());
		if (this.pd.getReconnectServer() != null)
			this.pd.getReconnectServer().reconnect(this, "reconnect");
		else
			this.connectTo("Lobby");
		final TextComponent welcome = new TextComponent(
				"§aBonjour §b" + this.p.getName() + "§a et bienvenue sur ???");

		this.sendMessage(welcome);

		this.sendMessage("§6----------------------------------------------------");

		final List<BPlayer> list = GameServers.get().getFriendsManager().friendList(this.p.getName()).stream()
				.filter(p -> GameServers.get().getProxy().getPlayer(p) != null)
				.map(p -> BPlayerHandler.get().getPlayer(p)).collect(Collectors.toList());

		list.forEach(p -> {
			p.sendMessage(Strings.amisPrefix + "§e" + this.p.getName() + " §7§os'est connecté(e)");
		});

		this.sendMessage("§6Ami(s) en ligne §e" + list.size() + "§f/§e" + list.size() + " "
				+ StringUtils.join(list.stream().map(p -> p.getName()).collect(Collectors.toList()), ", "));
		this.sendMessage("§6----------------------------------------------------");
		this.sendMessage("§6");

		this.sendNews();

		final PartiesManagement pm = GameServers.get().getPartiesManagement();

		if (pm.haveParty(this.p.getName().toLowerCase())) {
			final UUID party = pm.getPlayerParty(this.p.getName().toLowerCase());
			pm.sendMessageToParty(party, true, "§e" + this.p.getName() + " §7§os'est connecté(e)");
			return;
		}

		final BanManager bm = BanManager.get();

		final IPBans i = bm.isIPBan(this.p.getPendingConnection().getName());
		final PseudoBans pseudo = bm.isBan(this.p.getName());

		if (i != null)
			this.p.sendMessage(bm.BannedEject(i.life(), i.bannedReason(), i.getFromTime(), i.getTime(), i.Bannedby()));
		else if (pseudo != null) {
			System.out.println("OK");
			this.p.sendMessage(bm.BannedEject(pseudo.life(), pseudo.bannedReason(), pseudo.getFromTime(),
					pseudo.getTime(), pseudo.Bannedby()));
		}
	}

	public String onlineSince() {
		return TimeUtils.minutesToDayHoursMinutes(System.currentTimeMillis() / 1000 - this.online / 1000);
	}

	public void quit(final String a) {
		this.p.disconnect(TextComponent.fromLegacyText(a));
	}

	public void removeFromStaff() {
		new StaffPlayer("staff", this.name).remove();
		if (!this.getData().contains("oldRank")
				|| this.getData().getInt("oldRank") == RankManager.getInstance().getRank(7).getId())
			this.getData().setRank(RankManager.getInstance().getRank(4));
		else
			this.getData().setRank(this.getData().getInt("oldRank"));
		this.getData().setBoolean("mod", false);
		this.p.disconnect(TextComponent
				.fromLegacyText("§cVous ne faites désormais plus partie du staff, merci de vous reconnecter !"));
	}

	public void removePlayer() {
		this.pd.setLong("LastConnection", System.currentTimeMillis());
		GameServers.get().getTasksManager().addTask(() -> {
			GameServers.get().getQueuesManagement().leftQueue(this.p, "Deconnexion");
			GameServers.get().getPartiesManagement().leaveOrDisbandParty(this);
			this.setLogged(false);
			GameServers.get().getFriendsManager().friendList(this.p.getName()).stream()
					.filter(p -> GameServers.get().getProxy().getPlayer(p) != null)
					.map(p -> BPlayerHandler.get().getPlayer(p)).collect(Collectors.toList()).forEach(p -> {
						p.sendMessage(Strings.amisPrefix + "§e" + this.p.getName() + " §a§os'est déconnecté(e)");
					});

			final PartiesManagement pm = GameServers.get().getPartiesManagement();
			if (pm.haveParty(this.p.getName().toLowerCase())) {
				final UUID party = pm.getPlayerParty(this.p.getName().toLowerCase());
				pm.sendMessageToParty(party, true, "§e" + this.p.getName() + " §a§os'est déconnecté(e)");
			}
		});
	}

	public void removeTokens(final int t) {
		int total = this.getData().getTokens() - t;
		if (total < 0)
			total = 0;
		this.getData().setInt("tokens", total);
	}

	public void sendMessage(final BaseComponent c) {
		this.p.sendMessage(c);
	}

	public void sendMessage(final BaseComponent[] c) {
		this.p.sendMessage(c);
	}

	public void sendMessage(final String message) {
		this.p.sendMessage(TextComponent.fromLegacyText(message));
	}

	public void sendMessagetoParty(final String message) {
		final PartiesManagement pm = GameServers.get().getPartiesManagement();

		final UUID party = this.getParty();

		if (party == null)
			return;

		pm.sendMessageToParty(party, true, message);
	}

	public void sendNews() {
		final List<String> news = GameServers.get().getConfigManager().getNews();

		if (news == null || news.size() == 0)
			return;

		this.sendMessage("§f§m§l------------------------------------");
		this.sendMessage("");
		this.sendMessage("§CDernière news§f:");

		news.forEach(e -> {
			this.sendMessage("");
			this.sendMessage(e);
		});

		this.sendMessage("");
		this.sendMessage("§f§m§l------------------------------------");
	}

	public void setLogged(final boolean logged) {
		this.logged = logged;
	}

	public void setTokens(final int t) {
		this.getData().setInt("tokens", t);
	}

	public void tryIPLog() throws IOException {
		if (this.getAddress().equals(this.pd.get("Log"))) {
			this.logPlayer();
			ProxyServer.getInstance().getScheduler().schedule(GameServers.get(), () -> {
				this.connectTo("Lobby");
			}, 2, TimeUnit.SECONDS);
		}
	}

	public void printMessageLogin() {
		if (!this.pd.contains("password")) {
			this.sendMessage("§aVotre compte n'existe pas encore !");
			this.sendMessage("§f");
			this.sendMessage("§eEnregistrez votre compte avec la commande suivante§f:");
			this.sendMessage("§c/register <MotDePasse> <MotDePasse>");
		} else {
			this.sendMessage("§aVotre compte a bien été chargé..");
			this.sendMessage("§f");
			this.sendMessage("§eConnectez vous avec la commande suivante§f:");
			this.sendMessage("§c/login <MotDePasse>");
		}
	}

	public void tryLogPlayer() {
		ProxyServer.getInstance().getScheduler().schedule(GameServers.get(), () -> {
			if (this.pd.contains("Log") && this.getAddress().equals(this.pd.get("Log")))
				try {
					this.logPlayer();
				} catch (IOException e) {
					e.printStackTrace();
				}
			else ProxyServer.getInstance().getScheduler().runAsync(GameServers.get(), this::printMessageLogin);
		}, 1, TimeUnit.SECONDS);
	}

}
