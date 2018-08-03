package net.neferett.linaris.utils.time;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.managers.others.StopManager;
import net.neferett.linaris.managers.others.StopManager.callBack;
import net.neferett.socket.api.FastSendMessage;

public class TimeUtils {

	private static boolean rebooting = false;

	public static long daysToSeconds(final int a) {
		return a * 86400;
	}

	public static long getLastConnection(final long last) {
		if (last != 0)
			return System.currentTimeMillis() - last / 1000;
		return 0;
	}

	public static long hoursToSeconds(final int a) {
		return a * 3600;
	}

	public static String minutesToDayHoursMinutes(long l) {
		final long _m = 60, _h = 60 * _m, _d = 24 * _h, _w = 7 * _d, _mo = 30 * _d, _y = 12 * _mo;

		final int year = (int) (l / _y);
		l %= _y;

		final int months = (int) (l / _mo);
		l %= _mo;

		final int weeks = (int) (l / _w);
		l %= _w;

		final int days = (int) (l / _d);
		l %= _d;

		final int hours = (int) (l / _h);
		l %= _h;

		final int minutes = (int) (l / _m);
		l %= _m;

		final String result = (year > 0 ? " §e" + year + " §aAnnée" + (year > 1 ? "s" : "") : "")
				+ (months > 0 ? " §e" + months + " §aMois" : "")
				+ (weeks > 0 ? " §e" + weeks + " §aSemaine" + (weeks > 1 ? "s" : "") : "")
				+ (days > 0 ? " §e" + days + " §aJour" + (days > 1 ? "s" : "") : "")
				+ (hours > 0 ? " §e" + hours + " §aHeure" + (hours > 1 ? "s" : "") : "")
				+ (minutes > 0 ? " §e" + minutes + " §aMinute" + (minutes > 1 ? "s" : "") : "")
				+ (l > 0 ? " §e" + l + " §aSeconde" + (l > 1 ? "s" : "") : "");
		return result.trim();
	}

	public static long minutesToSeconds(final int a) {
		return a * 60;
	}

	public static void scheduleAutoReboot() {
		GameServers.get().getProxy().getScheduler().runAsync(GameServers.get(), () -> {
			final int rebootHour = 4;
			final int rebootMinute = 50;
			final int rebootSeconds = 0;

			final Calendar now = Calendar.getInstance();

			final Calendar nextReboot = (Calendar) now.clone();
			nextReboot.set(Calendar.HOUR_OF_DAY, rebootHour);
			nextReboot.set(Calendar.MINUTE, rebootMinute);
			nextReboot.set(Calendar.SECOND, rebootSeconds);

			final long diff = nextReboot.getTimeInMillis() - now.getTimeInMillis();

			System.out.println("Now = " + now.getTime().toString());

			if (diff <= 0)
				nextReboot.add(Calendar.DAY_OF_YEAR, 1);

			System.out.println("Reboot Scheduled = " + nextReboot.getTime().toString());

			final Timer timer = new Timer();
			now.getTime();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					ProxyServer.getInstance().getScheduler().runAsync(GameServers.get(), () -> {
						TimeUtils.stopReboot();
						timer.cancel();
					});
				}
			}, nextReboot.getTime());
		});
	}

	public static void stopReboot() {
		new StopManager(180, new callBack() {

			@Override
			public void cmd() {
				if (!rebooting) {
					new FastSendMessage("149.202.65.5", 12000, "stop " + GameServers.get().getDataFolder()
							.getAbsolutePath().replace(GameServers.get().getDataFolder().getPath(), "\n")).build();
					rebooting = true;
					ProxyServer.getInstance().stop();
				}
			}

			@Override
			public void thread(final int time) {
				if (time > 15 && time % 10 == 0 || time <= 15)
					ProxyServer.getInstance().broadcast(
							TextComponent.fromLegacyText("§f[§cGuarden§f] §7Redemarrage de tous les serveurs dans "
									+ TimeUtils.minutesToDayHoursMinutes(time) + " §7!"));
			}

		}).start();
	}

}
