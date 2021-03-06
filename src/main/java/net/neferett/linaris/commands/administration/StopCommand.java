package net.neferett.linaris.commands.administration;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.managers.others.StopManager;
import net.neferett.linaris.managers.others.StopManager.callBack;
import net.neferett.linaris.utils.time.TimeUtils;
import net.neferett.socket.api.FastSendMessage;

public class StopCommand extends Command {

	boolean stopped = false;

	public StopCommand() {
		super("linaris:stop");
	}

	@Override
	public void execute(final CommandSender sender, final String[] args) {
		if (sender instanceof ProxiedPlayer && GameServers.get().getPlayerDataManager().getPlayerData(sender.getName())
				.getRank().getModerationLevel() < 3)
			return;
		int time = 120;

		if (args.length == 1)
			time = Integer.parseInt(args[0]);

		new StopManager(time, new callBack() {

			@Override
			public void cmd() {
				if (!StopCommand.this.stopped) {
					new FastSendMessage("149.202.65.5", 12000, "stop " + GameServers.get().getDataFolder()
							.getAbsolutePath().replace(GameServers.get().getDataFolder().getPath(), "\n")).build();
					ProxyServer.getInstance().stop();
					StopCommand.this.stopped = true;
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
