package net.neferett.linaris.commands.player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.api.SettingsManager;
import net.neferett.linaris.api.friends.FriendsManagement;
import net.neferett.linaris.commands.PlayerCommand;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.utils.player.PlayersUtils;

public class FriendsCommand extends PlayerCommand implements TabExecutor {

	protected final FriendsManagement	friendsManagement;
	protected final GameServers			plugin;

	public FriendsCommand(final FriendsManagement friendsManagement, final GameServers plugin) {
		super("friends", true, "friend", "amis", "ami");
		this.friendsManagement = friendsManagement;
		this.plugin = plugin;
	}

	@Override
	public void execute(final BPlayer p, final PlayerData pd, final String[] args) {
		if (args.length == 0)
			this.showHelp(p);
		else {
			final String command = args[0];
			String arg = null;
			if (args.length > 1)
				arg = args[1];

			if (command.equalsIgnoreCase("add")) {
				if (arg == null) {
					p.sendMessage("§cN'oubliez pas d'indiquer le pseudo de l'ami à ajouter !");
					return;
				}

				final String rep = this.friendsManagement.sendRequest(p.getName(), arg);
				p.sendMessage(rep);
			} else if (command.equalsIgnoreCase("remove")) {
				if (arg == null) {
					p.sendMessage("§cN'oubliez pas d'indiquer le pseudo de l'ami à supprimer !");
					return;
				}

				final String rep = this.friendsManagement.removeFriend(p.getName(), arg);
				p.sendMessage(rep);

			} else if (command.equalsIgnoreCase("list")) {

				p.sendMessage("§6----------------------------------------------------");

				final List<String> list = this.friendsManagement.friendList(p.getName());

				String onlineList = "";

				for (final String name : list) {
					final ProxiedPlayer target = this.plugin.getProxy().getPlayer(name);
					if (target != null)
						onlineList += "§a" + target.getName() + ", ";
				}

				if (onlineList.equals(""))
					onlineList = "§aAucun";
				else
					onlineList = onlineList.substring(0, onlineList.lastIndexOf(","));

				p.sendMessage("§eAmis en ligne: §a[" + onlineList + "§a]");

				String offlineList = "";

				for (final String name : list)
					if (this.plugin.getProxy().getPlayer(name) == null)
						offlineList += "§c" + name + ", ";

				if (offlineList.equals(""))
					offlineList = "§cAucun";
				else
					offlineList = offlineList.substring(0, offlineList.lastIndexOf(","));

				p.sendMessage("§fAmis hors-ligne: §c[" + offlineList + "§c]");

				p.sendMessage("§6----------------------------------------------------");
			} else if (command.equalsIgnoreCase("on")) {

				SettingsManager.setSetting(p.getName(), "friends-enabled", "true");
				p.sendMessage("§7Demandes d'amis §aactivées !");

			} else if (command.equalsIgnoreCase("off")) {

				SettingsManager.setSetting(p.getName(), "friends-enabled", "false");
				p.sendMessage("§7Demandes d'amis §cdésactivées !");

			} else
				this.showHelp(p);
		}
	}

	@Override
	public Iterable<String> onTabComplete(final CommandSender p, final String[] args) {
		final List<String> result = new ArrayList<>();

		if (args.length < 2)
			return result;
		else {
			result.addAll(PlayersUtils.getOnlineNames().stream()
					.filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList()));

			return result;
		}
	}

	public void showHelp(final BPlayer target) {
		target.sendMessage(new TextComponent("§6--Gestion des Amis--"));
		target.sendMessage("§e/friends list §f> Lister ses amis");
		target.sendMessage("§e/friends add <nom du joueur> §f> pour envoyer un message");
		target.sendMessage("§e/friends remove <nom du joueur> §f> pour activer ou désactiver vos messages");
		target.sendMessage("§e/friends on §f> activer les demandes d'amitié");
		target.sendMessage("§e/friends off §f> refuser les demandes d'amitié");
		target.sendMessage("§e/friends sound §f> activer ou désactiver le son des demande d'ami");
	}
}
