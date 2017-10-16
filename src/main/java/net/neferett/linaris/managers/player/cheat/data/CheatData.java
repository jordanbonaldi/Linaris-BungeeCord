package net.neferett.linaris.managers.player.cheat.data;

import java.util.HashMap;
import java.util.Map;

import net.neferett.linaris.GameServers;
import net.neferett.linaris.DataBase.RedisAbstract;
import net.neferett.linaris.managers.player.cheat.HackEnum;

public class CheatData extends RedisAbstract {

	Map<String, String>	cache	= new HashMap<>();

	String				dbtype;

	String				name;

	public CheatData(final String p) {
		super("cheatdatas", p.toLowerCase(), () -> GameServers.get().getConnector().getCheatResource());
		this.name = p.toLowerCase();
	}

	public void addBan(final HackEnum hack) {
		this.setInt(hack.getName(), this.getBan(hack) + 1);
	}

	public int getBan(final HackEnum hack) {
		if (!this.contains(hack.getName()))
			return 0;
		return this.getInt(hack.getName());
	}

}
