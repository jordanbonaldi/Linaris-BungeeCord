package net.neferett.linaris.managers.bans;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.neferett.linaris.GameServers;
import net.neferett.linaris.DataBase.RedisAbstract;

public class DoubleAccount {

	class dc extends RedisAbstract {

		public dc(final String name) {
			super("dc", name, () -> GameServers.get().getConnector().getBanResource());
		}
	}

	public enum MODE {
		BANNED("BANNI", "§c"), OFFLINE("HORS-LIGNE", "§7"), ONLINE("EN LIGNE", "§a");

		public static MODE getValuesByString(final String n) {
			return Arrays.asList(values()).stream().filter(e -> e.toString().toLowerCase().equals(n.toLowerCase()))
					.findFirst().orElse(null);
		}

		String	color;

		String	name;

		private MODE(final String name, final String color) {
			this.name = name;
			this.color = color;
		}

		public String getColor() {
			return this.color;
		}

		public String getName() {
			return this.name;
		}
	}

	public static DoubleAccount get() {
		return GameServers.get().getDc();
	}

	HashMap<String, dc> dccache = new HashMap<>();

	public void addToIP(final String ip, final String psd) {
		this.getAccount(ip).set(psd, MODE.ONLINE.toString());
	}

	public dc getAccount(final String ip) {
		if (this.dccache.containsKey(ip))
			return this.dccache.get(ip);
		else {
			final dc d = new dc(ip);
			this.dccache.put(ip, d);
			return d;
		}
	}

	public Map<String, String> getDoubleAccount(final String ip) {
		return this.getAccount(ip).getAll();
	}

	public void removeFrom(final String ip, final String psd) {
		this.getAccount(ip).removeKey(psd.toLowerCase());
	}

	public void updateMode(final String ip, final String pseudo, final MODE m) {
		this.getAccount(ip).set(pseudo, m.toString());

	}

}
