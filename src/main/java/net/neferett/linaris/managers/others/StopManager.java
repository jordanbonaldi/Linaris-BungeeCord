package net.neferett.linaris.managers.others;

import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ProxyServer;
import net.neferett.linaris.GameServers;

public class StopManager {

	public interface callBack {
		public void cmd();

		public void thread(int time);
	}

	callBack	c;

	int			time;

	public StopManager(final int t, final callBack c) {
		this.time = t;
		this.c = c;
	}

	public void start() {
		ProxyServer.getInstance().getScheduler().schedule(GameServers.get(), () -> {
			if (this.time == 0) {
				this.c.cmd();
				return;
			} else if (this.time < 0)
				return;
			this.c.thread(this.time);
			this.time--;
		}, 1, 1, TimeUnit.SECONDS);
	}

}
