package net.neferett.linaris.managers.player;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.ranks.RankManager;
import redis.clients.jedis.Jedis;

public class BPlayerHandler {

	public static BPlayerHandler get() {
		return GameServers.get().getH();
	}

	List<String>						ips	= new ArrayList<>();
	Pattern								pattern;

	LinkedHashMap<String, BPlayer>		pl	= new LinkedHashMap<>();
	LinkedHashMap<String, BStaff>		ps	= new LinkedHashMap<>();
	LinkedHashMap<String, BYoutuber>	py	= new LinkedHashMap<>();

	public BPlayerHandler() {
		this.pattern = Pattern.compile("[a-zA-Z0-9_]{1,16}");
	}

	public Map<String, Map<String, String>> getAllStaffs() {
		final Map<String, Map<String, String>> staffs = new HashMap<>();
		final Jedis j = GameServers.get().getConnector().getStaffResource();
		j.keys("staff:*").forEach(name -> staffs.put(name.substring(6), j.hgetAll("staff:" + name.substring(6))));
		j.close();
		return staffs;
	}

	public Map<String, Map<String, String>> getAllYTs() {
		final Map<String, Map<String, String>> yt = new HashMap<>();
		final Jedis j = GameServers.get().getConnector().getStaffResource();
		j.keys("yt:*").forEach(name -> {
			yt.put(name.substring(3), j.hgetAll("yt:" + name.substring(3)));
		});
		j.close();
		return yt;
	}

	public List<BStaff> getOnlineStaffs() {
		return this.ps.entrySet().stream().map(e -> e.getValue())
				.sorted((d1, d2) -> d2.getRank().getModerationLevel() - d1.getRank().getModerationLevel())
				.collect(Collectors.toList());
	}

	public List<BYoutuber> getOnlineYT() {
		return this.py.entrySet().stream().map(e -> e.getValue())
				.sorted((d1, d2) -> d2.getSubs().intValue() - d1.getSubs().intValue()).collect(Collectors.toList());
	}

	public BPlayer getPlayer(final ProxiedPlayer p) {
		if (GameServers.get().getPlayerDataManager().getPlayerData(p.getName().toLowerCase()).getRank()
				.getModerationLevel() >= 1)
			return this.getStaff(p);
//		else if (GameServers.get().getPlayerDataManager().getPlayerData(p.getName().toLowerCase()).getRank()
//				.equals(RankManager.getInstance().getRank(10)))
//			return this.getYT(p);
		if (this.pl.containsKey(p.getName().toLowerCase()))
			return this.pl.get(p.getName().toLowerCase());
		final BPlayer bp = new BPlayer(p);
		this.pl.put(p.getName().toLowerCase(), bp);
		return bp;
	}

	public BPlayer getPlayer(final String p) {
		if (GameServers.get().getProxy().getPlayer(p) == null)
			return null;
		if (GameServers.get().getPlayerDataManager().getPlayerData(p.toLowerCase()).getRank().getModerationLevel() >= 1)
			return this.getStaff(p);
//		else if (GameServers.get().getPlayerDataManager().getPlayerData(p.toLowerCase()).getRank()
//				.equals(RankManager.getInstance().getRank(10)))
//			return this.getYT(p);
		if (this.pl.containsKey(p.toLowerCase()))
			return this.pl.get(p.toLowerCase());
		final BPlayer bp = new BPlayer(GameServers.get().getProxy().getPlayer(p));
		this.pl.put(p.toLowerCase(), bp);
		return bp;
	}

	public BStaff getStaff(final ProxiedPlayer p) {
		if (GameServers.get().getPlayerDataManager().getPlayerData(p.getName().toLowerCase()).getRank()
				.getModerationLevel() < 1)
			return null;
		if (this.ps.containsKey(p.getName().toLowerCase()))
			return this.ps.get(p.getName().toLowerCase());
		final BStaff bp = new BStaff(p);
		this.ps.put(p.getName().toLowerCase(), bp);
		return bp;
	}

	public BStaff getStaff(final String p) {
		if (GameServers.get().getProxy().getPlayer(p) == null || GameServers.get().getPlayerDataManager()
				.getPlayerData(p.toLowerCase()).getRank().getModerationLevel() < 1)
			return null;
		if (this.ps.containsKey(p.toLowerCase()))
			return this.ps.get(p.toLowerCase());
		final BStaff bp = new BStaff(GameServers.get().getProxy().getPlayer(p));
		this.ps.put(p.toLowerCase(), bp);
		return bp;
	}

	public BYoutuber getYT(final ProxiedPlayer p) {
		if (!GameServers.get().getPlayerDataManager().getPlayerData(p.getName()).getRank()
				.equals(RankManager.getInstance().getRank(10)))
			return null;
		if (this.py.containsKey(p.getName().toLowerCase()))
			return this.py.get(p.getName().toLowerCase());
		final BYoutuber bp = new BYoutuber(p);
		this.py.put(p.getName().toLowerCase(), bp);
		return bp;
	}

	public BYoutuber getYT(final String p) {
		if (!GameServers.get().getPlayerDataManager().getPlayerData(p).getRank()
				.equals(RankManager.getInstance().getRank(10)))
			return null;
		if (this.py.containsKey(p.toLowerCase()))
			return this.py.get(p.toLowerCase());
		final BYoutuber bp = new BYoutuber(GameServers.get().getProxy().getPlayer(p));
		this.py.put(p.toLowerCase(), bp);
		return bp;
	}

	public boolean isUserNameValide(final String username) {
		return this.pattern.matcher(username).matches();
	}

	public boolean isValidIP(final String a) {
		return this.ips.stream().filter(ip -> ip.equals(a)).findFirst().orElse(null) == null;
	}

	public void loadIps() throws MalformedURLException, IOException {
		final Scanner Blacklist = new Scanner(
				new URL("http://myip.ms/files/blacklist/csf/latest_blacklist.txt").openStream());
		System.out.println("[AJB] Downloading Blacklist...");
		while (Blacklist.hasNextLine()) {
			final String IP = Blacklist.nextLine();
			if (IP.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}"))
				this.ips.add(IP);
		}
		Blacklist.close();
	}

	public void removePlayer(final ProxiedPlayer p) {
		if (this.pl.containsKey(p.getName().toLowerCase()))
			this.pl.remove(p.getName().toLowerCase());
		else if (this.ps.containsKey(p.getName().toLowerCase()))
			this.ps.remove(p.getName().toLowerCase());
		else if (this.py.containsKey(p.getName().toLowerCase()))
			this.py.remove(p.getName().toLowerCase());
	}

	public void removePlayer(final String p) {
		if (this.pl.containsKey(p.toLowerCase()))
			this.pl.remove(p.toLowerCase());
		else if (this.ps.containsKey(p.toLowerCase()))
			this.ps.remove(p.toLowerCase());
		else if (this.py.containsKey(p.toLowerCase()))
			this.py.remove(p.toLowerCase());
	}

}
