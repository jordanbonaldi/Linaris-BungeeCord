package net.neferett.linaris.managers.others;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.neferett.linaris.GameServers;

public class AutoMessageManager {

	public static AutoMessageManager get() {
		return GameServers.get().getAm();
	}

	List<AutoMessages>	am	= new ArrayList<>();

	int					pos;

	ScheduledTask		task;

	public AutoMessageManager() {
		this.pos = 0;
	}

	public void addAutoMessage(final String t, final String d, final boolean c, final String i) {
		this.am.add(new AutoMessages(t, d, c, i));
	}

	public void kill() {
		this.am.clear();
		if (this.task != null)
			ProxyServer.getInstance().getScheduler().cancel(this.task);
	}

	public AutoMessages next() {
		return this.pos + 1 > this.am.size() ? this.am.get(this.pos = 0) : this.am.get(this.pos++);
	}

	public void start() {
		this.task = ProxyServer.getInstance().getScheduler().schedule(GameServers.get(), () -> {
			this.next().display();
		}, 3, 3, TimeUnit.MINUTES);
	}

}
