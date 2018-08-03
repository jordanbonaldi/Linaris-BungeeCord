package net.neferett.linaris.managers.player;

import net.neferett.linaris.GameServers;
import net.neferett.linaris.DataBase.RedisAbstract;
import net.neferett.linaris.api.ranks.RankAPI;

public class StaffPlayer extends RedisAbstract {

	public StaffPlayer(final String s, final String name) {
		super(s, name, () -> GameServers.get().getConnector().getStaffResource());
		this.refresh();
	}

	public StaffPlayer(final String s, final String name, final RankAPI r) {
		this(s, name);
		if (!this.cache.containsKey("rank") || r.getId() != this.getInt("rank"))
			this.setInt("rank", r.getId());
	}

}
