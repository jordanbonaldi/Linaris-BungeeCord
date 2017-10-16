package net.neferett.linaris.commands.player;

import java.util.HashMap;
import java.util.stream.Collectors;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.Rank;

public class CommandList extends Command {

	private static String sep;

	static {
		sep = new StringBuilder().append(ChatColor.DARK_GRAY).append(ChatColor.STRIKETHROUGH)
				.append("---------------------------------------------").toString();
	}

	public CommandList(final GameServers plugin) {
		super("list", "", "who", "ls", "playerlist", "online", "plist", "glist", "poto", "glist");
	}

	@Override
	@SuppressWarnings("deprecation")
	public void execute(final CommandSender sender, final String[] args) {
		int count;
		if (args.length == 0) {
			final ProxiedPlayer p = (ProxiedPlayer) sender;
			count = ProxyServer.getInstance().getOnlineCount();
			final int countlogin = ProxyServer.getInstance().getServerInfo("Login").getPlayers().size();
			final int srv = p.getServer().getInfo().getPlayers().size();
			sender.sendMessage(TextComponent.fromLegacyText(CommandList.sep));
			sender.sendMessage("  §6Il y a actuellement§f: §b" + count + " connecté" + this.s(count) + "§6.");
			if (sender instanceof ProxiedPlayer)
				sender.sendMessage(
						"  §eSoit§f: §a" + srv + " connecté" + this.s(srv) + " §esur le serveur où vous êtes.");
			sender.sendMessage(
					"  §eSoit aussi§f: §a" + countlogin + " joueur" + this.s(countlogin) + " §een cours de login..");
			if (GameServers.get().getPlayerDataManager().getPlayerData(sender.getName()).getRank()
					.getModerationLevel() > 1) {
				final HashMap<ProxiedPlayer, String> map = this.getPlayers();
				sender.sendMessage("");
				for (final Rank r : Rank.values()) {
					final int a = this.getRankPlayers(r.getName(), map);
					if (a != 0)
						sender.sendMessage("  §" + r.getColor() + r.getName() + "§f: §7"
								+ this.getRankPlayers(r.getName(), map) + " joueur" + (a > 1 ? "s" : ""));
				}
			}
		} else if (args.length == 1) {
			final String server = args[0];
			if (ProxyServer.getInstance().getServerInfo(server) == null) {
				sender.sendMessage("§cLe serveur §e" + args[0] + " §cn'existe pas !");
				return;
			}
			final int serv = ProxyServer.getInstance().getServerInfo(server).getPlayers().size();
			sender.sendMessage(TextComponent.fromLegacyText(CommandList.sep));
			sender.sendMessage("  §6Il y a actuellement§f: §b" + serv + " connecté" + this.s(serv)
					+ "§6 sur le serveur §c" + server);
			if (GameServers.get().getPlayerDataManager().getPlayerData(sender.getName()).getRank()
					.getModerationLevel() > 1) {
				final HashMap<ProxiedPlayer, String> map = this.getPlayers();
				sender.sendMessage("");
				for (final Rank r : Rank.values()) {
					final int a = this.getRankPlayersByServer(r.getName(), server, map);
					if (a != 0)
						sender.sendMessage("  §" + r.getColor() + r.getName() + "§f: §c"
								+ this.getRankPlayers(r.getName(), map) + " joueurs");
				}
			}
		}
		sender.sendMessage(TextComponent.fromLegacyText(CommandList.sep));
	}

	public HashMap<ProxiedPlayer, String> getPlayers() {
		return (HashMap<ProxiedPlayer, String>) ProxyServer.getInstance().getPlayers().stream()
				.collect(Collectors.toMap(p -> p, pa -> GameServers.get().getPlayerDataManager()
						.getPlayerData(pa.getName()).getRank().getName()));
	}

	public int getRankPlayers(final String rankname, final HashMap<ProxiedPlayer, String> map) {
		return map.values().stream().filter(rank -> rank.equals(rankname)).collect(Collectors.toList()).size();
	}

	public int getRankPlayersByServer(final String rankname, final String servname,
			final HashMap<ProxiedPlayer, String> map) {
		return map.entrySet().stream().filter(
				m -> m.getValue().equals(rankname) && m.getKey().getServer().getInfo().getName().equals(servname))
				.collect(Collectors.toList()).size();
	}

	private String s(final int count) {
		return count > 1 ? "s" : "";
	}

}
