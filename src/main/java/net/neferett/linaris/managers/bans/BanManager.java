package net.neferett.linaris.managers.bans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.DataBase.RedisAbstract;
import net.neferett.linaris.utils.time.TimeUtils;

public class BanManager {

	public class IPBans extends RedisAbstract {

		public IPBans(final String db, final String name) {
			super(db, name, () -> GameServers.get().getConnector().getBanResource());
		}

		public String Bannedby() {
			return this.get("by");
		}

		public String bannedReason() {
			return this.get("reason");
		}

		public long getFromTime() {
			return Long.valueOf(this.get("bms"));
		}

		public long getTime() {
			return Long.valueOf(this.get("time"));
		}

		public boolean isBanned() {
			return this.contains("banned");
		}

		public boolean life() {
			return this.contains("life");
		}

	}

	public class PseudoBans extends RedisAbstract {

		public PseudoBans(final String db, final String name) {
			super(db, name, () -> GameServers.get().getConnector().getBanResource());
		}

		public String Bannedby() {
			return this.get("by");
		}

		public String bannedReason() {
			return this.get("reason");
		}

		public long getFromTime() {
			return Long.valueOf(this.get("bms"));
		}

		public long getTime() {
			return Long.valueOf(this.get("time"));
		}

		public boolean isBanned() {
			return this.contains("banned");
		}

		public boolean life() {
			return this.contains("life");
		}

		public String likedToIPBan() {
			return this.contains("link") ? this.get("link") : null;
		}

	}

	public class PseudoMute extends RedisAbstract {

		public PseudoMute(final String db, final String name) {
			super(db, name, () -> GameServers.get().getConnector().getBanResource());
		}

		public String Bannedby() {
			return this.get("by");
		}

		public String bannedReason() {
			return this.get("reason");
		}

		public long getFromTime() {
			return Long.valueOf(this.get("bms"));
		}

		public long getTime() {
			return Long.valueOf(this.get("time"));
		}

		public boolean isMuted() {
			return this.contains("mute");
		}

	}

	public static BanManager get() {
		return GameServers.get().getBm();
	}

	HashMap<String, IPBans>		ipcache		= new HashMap<>();

	HashMap<String, PseudoMute>	mutecache	= new HashMap<>();

	HashMap<String, PseudoBans>	pseudocache	= new HashMap<>();

	List<BanReason>				reasons		= new ArrayList<>();

	public void addReason(final BanReason s) {
		this.reasons.add(s);
	}

	public String BannedEject(final boolean life, final String reason, final long from, final long time,
			final String f) {
		final StringBuilder b = new StringBuilder();

		final long tms = from / 1000 + time - System.currentTimeMillis() / 1000;

		b.append("§f§m------------§r §6Linaris§r §f§m------------\n");
		b.append("§c§l Vous êtes banni du serveur\n");
		b.append("\n");
		b.append("§7Raison§f: §c" + reason + "\n");
		if (!life)
			b.append("§7Temps restant§f: " + TimeUtils.minutesToDayHoursMinutes(tms) + "\n");
		else
			b.append("§7Temps§f: §cà vie !\n");
		b.append("§7Banni par§f: §e" + f + "\n");
		b.append("\n");
		b.append("§aPour contester cette sanction, merci de passer par§f: \n");
		b.append("  §f- §6TeamSpeak§f: §ets.linaris.fr\n");
		b.append("  §f- §6Forum§f: §elinaris.fr/forum\n");
		b.append("\n");
		b.append("§f§m-------------------------------\n");
		return b.toString();
	}

	public void clearReasons() {
		this.reasons.clear();
	}

	public List<BanReason> getReasons() {
		return this.reasons;
	}

	public void ipBan(final String p, final String pseudo, final String from, final long time, final String s,
			final boolean announce) {
		if (this.isIPBan(p) != null)
			return;

		IPBans ip;
		if (this.ipcache.containsKey(p))
			ip = this.ipcache.get(p);
		else {
			ip = new IPBans("ip", p);
			this.ipcache.put(p, ip);
		}

		ip.set("banned", "true");
		ip.set("time", Long.toString(time));
		ip.set("bms", Long.toString(System.currentTimeMillis()));
		ip.set("by", from);
		ip.set("reason", s);
		if (time < 0)
			ip.set("life", "true");
		this.pseudoBan(pseudo, from, time, s, true, p, announce);
	}

	public PseudoBans isBan(final String p) {
		PseudoBans i = null;
		if (this.pseudocache.containsKey(p.toLowerCase()))
			i = this.pseudocache.get(p.toLowerCase());
		else {
			i = new PseudoBans("pseudo", p.toLowerCase());
			this.pseudocache.put(p.toLowerCase(), i);
		}
		if (i.isBanned() == false)
			return null;

		final boolean t = i.getFromTime() / 1000 + i.getTime() - System.currentTimeMillis() / 1000 > 0;
		if (t || i.getTime() < 0)
			return i;
		else {
			i.remove();
			return null;
		}
	}

	public IPBans isIPBan(final String ip) {
		IPBans i = null;

		if (this.ipcache.containsKey(ip))
			i = this.ipcache.get(ip);
		else {
			i = new IPBans("ip", ip);
			this.ipcache.put(ip, i);
		}

		if (i.isBanned() == false)
			return null;

		final boolean t = i.getFromTime() / 1000 + i.getTime() - System.currentTimeMillis() / 1000 > 0;
		if (t || i.getTime() < 0)
			return i;
		else {
			i.remove();
			return null;
		}
	}

	public PseudoMute isMute(final String p) {
		PseudoMute i = null;

		if (this.mutecache.containsKey(p.toLowerCase()))
			i = this.mutecache.get(p.toLowerCase());
		else {
			i = new PseudoMute("mute", p.toLowerCase());
			this.mutecache.put(p.toLowerCase(), i);
		}
		if (i.isMuted() == false)
			return null;

		final boolean t = i.getFromTime() / 1000 + i.getTime() - System.currentTimeMillis() / 1000 > 0;
		if (t || i.getTime() < 0)
			return i;
		else {
			i.remove();
			return null;
		}
	}

	public String KickEject(final boolean life, final String reason, final long from, final long time, final String f) {
		final StringBuilder b = new StringBuilder();

		b.append("§f§m------------§r §6Linaris§r §f§m------------\n");
		b.append("§c§l Vous avez été Kick du serveur\n");
		b.append("\n");
		b.append("§7Raison§f: §c" + reason + "\n");
		b.append("§7Kick par§f: §e" + f + "\n");
		b.append("\n");
		b.append("§aPour contester cette sanction, merci de passer par§f: \n");
		b.append("  §f- §6TeamSpeak§f: §ets.linaris.fr\n");
		b.append("  §f- §6Forum§f: §elinaris.fr/forum\n");
		b.append("\n");
		b.append("§f§m-------------------------------\n");
		return b.toString();
	}

	public void pseudoBan(final String p, final String from, final long time, final String s, final boolean linked,
			final String ipbanned, final boolean announce) {
		if (this.isBan(p) != null)
			return;

		PseudoBans ip;
		if (this.pseudocache.containsKey(p.toLowerCase()))
			ip = this.pseudocache.get(p.toLowerCase());
		else {
			ip = new PseudoBans("pseudo", p.toLowerCase());
			this.pseudocache.put(p.toLowerCase(), ip);
		}

		ip.set("banned", "true");
		ip.set("time", Long.toString(time));
		ip.set("bms", Long.toString(System.currentTimeMillis()));
		ip.set("by", from);
		ip.set("reason", s);
		if (linked)
			ip.set("link", ipbanned);
		if (time < 0)
			ip.set("life", "true");

		if (announce)
			ProxyServer.getInstance().broadcast(TextComponent
					.fromLegacyText("§f[§cBans§f] §e" + p + " §7a été banni par §e" + from + "§7 pour§f: §c" + s));
	}

	public void pseudoMute(final String p, final String from, final long time, final String s, final boolean announce) {
		if (this.isBan(p.toLowerCase()) != null)
			return;

		PseudoMute ip;
		if (this.mutecache.containsKey(p.toLowerCase()))
			ip = this.mutecache.get(p.toLowerCase());
		else {
			ip = new PseudoMute("mute", p.toLowerCase());
			this.mutecache.put(p.toLowerCase(), ip);
		}

		ip.set("mute", "true");
		ip.set("time", Long.toString(time));
		ip.set("bms", Long.toString(System.currentTimeMillis()));
		ip.set("by", from);
		ip.set("reason", s);
		if (time < 0)
			ip.set("life", "true");

		if (announce)
			ProxyServer.getInstance().broadcast(TextComponent
					.fromLegacyText("§f[§cMute§f] §e" + p + " §7a été mute par §e" + from + "§7 pour§f: §c" + s));
	}

	public void unBan(final String p) {
		final PseudoBans b = this.pseudocache.get(p.toLowerCase());

		if (b == null)
			return;

		final String ip = b.likedToIPBan();
		if (ip != null) {
			final IPBans ipb = this.isIPBan(ip);
			if (ipb == null)
				return;
			ipb.remove();
			b.remove();
		} else
			b.remove();
	}

	public void unMute(final String p) {
		final PseudoMute b = this.mutecache.get(p.toLowerCase());

		if (b == null)
			return;
		b.remove();
	}

}
