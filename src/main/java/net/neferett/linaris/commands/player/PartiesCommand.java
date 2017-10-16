package net.neferett.linaris.commands.player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.api.SettingsManager;
import net.neferett.linaris.api.party.PartiesManagement;
import net.neferett.linaris.commands.PlayerCommand;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.utils.player.PlayersUtils;

public class PartiesCommand extends PlayerCommand implements TabExecutor {

	protected final PartiesManagement	partiesManagement;
	protected final GameServers			plugin;

	public PartiesCommand(final PartiesManagement partiesManagement, final GameServers plugin) {
		super("groupe", true, "p", "groupes", "party", "grp");
		this.partiesManagement = partiesManagement;
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

			if (command.equalsIgnoreCase("invite")) {

				if (arg == null) {
					this.showHelp(p);
					return;
				}

				this.partiesManagement.sendRequest(p, arg);

			} else if (command.equalsIgnoreCase("accept")) {

				if (arg == null) {
					this.showHelp(p);
					return;
				}

				this.partiesManagement.grantRequest(arg, p);

			} else if (command.equalsIgnoreCase("list")) {

				if (!this.partiesManagement.havePartyMessage(p))
					return;

				final UUID party = this.partiesManagement.getPlayerParty(p.getName().toLowerCase());

				p.sendMessage("§6---Party de §b" + this.partiesManagement.getLeader(party) + "§6---");

				final List<String> list = this.partiesManagement.getPlayersInParty(party);

				for (final String name : list) {
					final ProxiedPlayer target = GameServers.get().getProxy().getPlayer(name);
					if (target == null)
						p.sendMessage("§a" + name + " §f<> §7OffLine");
					else {
						final PlayerData pdtarget = GameServers.get().getPlayerDataManager().getPlayerData(name);
						p.sendMessage("§a" + target.getName() + " §f<> §e"
								+ (pd.getCurrentServer() != null ? pdtarget.getCurrentServer().getServName() : "")
								+ " §7(follow: "
								+ (SettingsManager.isEnabled(name, "party-follow", true) ? "§aActivé" : "§cDésactivé")
								+ "§7)");
					}
				}

				p.sendMessage("§6---------------------");

			} else if (command.equalsIgnoreCase("kick")) {

				if (arg == null) {
					this.showHelp(p);
					return;
				}

				this.partiesManagement.kickParty(p, arg);

			} else if (command.equalsIgnoreCase("tp")) {

				if (arg == null) {
					this.partiesManagement.tpParty(p);
					return;
				}

				this.partiesManagement.tpParty(p, arg);

			} else if (command.equalsIgnoreCase("lead")) {

				if (arg == null) {
					this.showHelp(p);
					return;
				}

				this.partiesManagement.setLeaderParty(p, arg);

			} else if (command.equalsIgnoreCase("allow")) {

				if (arg == null) {
					this.showAllowHelp(p);
					return;
				}

				if (arg.equalsIgnoreCase("all")) {

					SettingsManager.setSetting(p.getName(), "party-enable", "true");

					p.sendMessage("§7Invitations de groupes : §aAccepter toutes les invitations");

					return;
				}

				if (arg.equalsIgnoreCase("none")) {

					SettingsManager.setSetting(p.getName(), "party-enable", "false");

					p.sendMessage("§7Invitations de groupes : §cRefuser toutes les invitations");

					return;
				}

				if (arg.equalsIgnoreCase("friends")) {

					SettingsManager.setSetting(p.getName(), "party-enable", "friends");

					p.sendMessage("§7Invitations de groupes : §eAccepter seulement les amis");

					return;
				}

				this.showAllowHelp(p);

			} else if (command.equalsIgnoreCase("follow")) {

				if (arg == null) {
					this.showFollowHelp(p);
					return;
				}

				if (arg.equalsIgnoreCase("on")) {

					SettingsManager.setSetting(p.getName(), "party-follow", String.valueOf(true));

					p.sendMessage("§7Party Follow : §aActivé");

					if (this.partiesManagement.haveParty(p.getName().toLowerCase())) {
						final UUID party = this.partiesManagement.getPlayerParty(p.getName().toLowerCase());
						this.partiesManagement.sendMessageToParty(party, true,
								"§fParty Follow de §b" + p.getName() + " §f: §aActivé");
					}

					return;
				}

				if (arg.equalsIgnoreCase("off")) {

					SettingsManager.setSetting(p.getName(), "party-follow", String.valueOf(false));

					p.sendMessage("§7Party Follow : §cDésactivé");

					if (this.partiesManagement.haveParty(p.getName().toLowerCase())) {
						final UUID party = this.partiesManagement.getPlayerParty(p.getName().toLowerCase());
						this.partiesManagement.sendMessageToParty(party, true,
								"§fParty Follow de §b" + p.getName() + " §f: §cDésactivé");
					}

					return;
				}

				this.showFollowHelp(p);

			} else if (command.equalsIgnoreCase("sound")) {

				final boolean sound = SettingsManager.isEnabled(p.getName(), "party-sound", true);
				SettingsManager.setSetting(p.getName(), "party-sound",
						sound ? Boolean.toString(false) : Boolean.toString(true));

				p.sendMessage("§aSon des invitations: " + (sound ? "§cDésactivé" : "§aActivé"));

			} else if (command.equalsIgnoreCase("leave"))
				this.partiesManagement.leaveOrDisbandParty(p);
			else if (command.equalsIgnoreCase("disband")) {

				if (!this.partiesManagement.isLeaderMessage(p))
					return;

				this.partiesManagement.disbandParty(p);

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

	public void showAllowHelp(final BPlayer target) {
		target.sendMessage(new TextComponent("§6§6--Gestion des invitations des groupe--"));
		target.sendMessage("§e/party allow all §f> pour autoriser tous le monde à vous inviter");
		target.sendMessage("§e/party allow friends §f> pour autoriser uniquement vos amis");
		target.sendMessage("§e/party allow none §f> personne ne pourra vous inviter en groupe");
	}

	public void showFollowHelp(final BPlayer target) {
		target.sendMessage(new TextComponent("§6§6--Gestion des invitations des groupe--"));
		target.sendMessage("§e/party follow on §f> pour suivre automatiquement le chef de groupe");
		target.sendMessage("§e/party follow off §f> pour ne pas suivre le chef de groupe");
	}

	public void showHelp(final BPlayer target) {
		target.sendMessage(new TextComponent("§6§6--Gestion des groupes--"));
		target.sendMessage("§e/party invite <nom du joueur> §f> inviter un joueur");
		target.sendMessage("§e/party tp <nom du joueur> §f> rejoindre un membre du groupe");
		target.sendMessage("§e/party list §f> affiche la liste des joueurs du groupe");
		target.sendMessage("§e/party accept <nom du joueur> §f> accepter une invitation");
		target.sendMessage("§e/party lead <nom du joueur> §f> nommer un nouveau chef");
		target.sendMessage("§e/party kick <nom du joueur> §f> kicker un joueur");
		target.sendMessage("§e/party leave §f> quitter le groupe");
		target.sendMessage("§e/party disband §f> détruire le groupe");
		target.sendMessage("§e/party allow §f> d§finir qui a le droit de vous inviter");
		target.sendMessage("§e/party follow on|off §f> suivre ou non le chef de groupe");
		target.sendMessage("§e/party sound §f> activer ou désactiver le son des invitations");
	}
}
