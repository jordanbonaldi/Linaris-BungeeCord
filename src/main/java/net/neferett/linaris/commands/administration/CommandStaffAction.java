package net.neferett.linaris.commands.administration;

import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.api.ranks.RankAPI;
import net.neferett.linaris.api.ranks.RankManager;
import net.neferett.linaris.commands.ModeratorCommand;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BPlayerHandler;
import net.neferett.linaris.managers.player.BStaff;
import net.neferett.linaris.managers.player.StaffPlayer;

public class CommandStaffAction extends ModeratorCommand {

	public CommandStaffAction() {
		super("staffaction", 3);
	}

	void addPoints(final String d, final int points) {
		final BStaff st = BPlayerHandler.get().getStaff(d);
		if (st == null) {
			final StaffPlayer p = new StaffPlayer("staff", d.toLowerCase());
			p.setInt("points", p.getInt("points") + points);
		} else
			st.addPoints(points);
	}

	@Override
	public void execute(final BStaff p, final String[] args) {
		if (p.getRank().getModerationLevel() < 3)
			return;
		if (args.length < 2)
			p.sendMessage(
					"§cUtilisation: /staffaction <rank | mod> <set | add | points> ([Points] <add | set | remove> <number>) ([Rank] <rankname>) <player>");
		else if (args[0].equalsIgnoreCase("rank") && args.length == 3) {
			final RankAPI r = RankManager.getInstance().getRank(args[1]);
			final BPlayer pl = BPlayerHandler.get().getPlayer(args[2]);

			if (pl == null) {
				p.sendMessage("§cJoueur inconnu !");
				return;
			} else if (r == null) {
				p.sendMessage("§cRank inconnu !");
				return;
			}

			pl.getData().setRank(r);

			p.sendMessage("§7Grade §" + r.getColor() + r.getName() + "§7 attribué au joueur §e" + pl.getName());
		} else if (args[0].equalsIgnoreCase("rank") && args.length != 3) {
			p.sendMessage("§cUtilisation: /staffaction rank <rank> <player>");
			return;
		} else if (args[0].equalsIgnoreCase("mod") && args.length == 3
				&& (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
			final BPlayer pl = BPlayerHandler.get().getPlayer(args[2]);
			if (args[1].equalsIgnoreCase("add")) {
				pl.addToStaff();
				p.sendMessage("§7Membre ajouté au staff !");
			} else if (args[1].equalsIgnoreCase("remove")) {
				this.removeStaff(args[2]);
				p.sendMessage("§7Membre supprimé du staff !");
			}
		} else if (args[0].equalsIgnoreCase("mod") && args.length > 2 && !(args[1].equalsIgnoreCase("points")
				|| args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove")))
			p.sendMessage("§cUtilisation: /staffaction mod <add | remove> <player>");
		else if (args[0].equalsIgnoreCase("mod") && args.length > 2 && args[1].equalsIgnoreCase("points")
				&& args.length == 5) {
			final BStaff pl = BPlayerHandler.get().getStaff(args[3]);
			if (pl != null && pl.getName().equalsIgnoreCase(p.getName())) {
				p.sendMessage("§cVous ne pouvez pas changer vos propre points !");
				return;
			} else if (pl != null && pl.getRank().getModerationLevel() > p.getRank().getModerationLevel()) {
				p.sendMessage("§cVous ne pouvez pas changer les points d'un plus haut gradé !");
				return;
			}
			if (args[2].equalsIgnoreCase("add")) {
				this.addPoints(args[3], Integer.parseInt(args[4]));
				p.sendMessage("§7Ajout de §c" + args[4] + "§7 points à §e" + args[3]);
			} else if (args[2].equalsIgnoreCase("remove")) {
				this.removePoints(args[3], Integer.parseInt(args[4]));
				p.sendMessage("§7Suppression de §c" + args[4] + "§7 points à §e" + args[3]);
			} else if (args[2].equalsIgnoreCase("set")) {
				this.setPoints(args[3], Integer.parseInt(args[4]));
				p.sendMessage("§7Points de " + args[3] + "§7 mis à §c" + args[4]);
			} else
				p.sendMessage("§cUtilisation: /staffaction mod points <add | remove | set> <player>");
		} else if (args[0].equalsIgnoreCase("mod") && args.length > 2 && args[1].equalsIgnoreCase("points")
				&& args.length != 5) {
			p.sendMessage("§cUtilisation: /staffaction mod points <add | remove | set> <player>");
			return;
		}
	}

	void removePoints(final String d, final int points) {
		final BStaff st = BPlayerHandler.get().getStaff(d);
		if (!BPlayerHandler.get().getAllStaffs().containsKey(d.toLowerCase()))
			return;
		if (st == null) {
			final StaffPlayer p = new StaffPlayer("staff", d.toLowerCase());
			p.setInt("points", p.getInt("points") - points);
		} else
			st.removePoints(points);
	}

	void removeStaff(final String d) {
		final BStaff st = BPlayerHandler.get().getStaff(d);
		if (st == null) {
			final StaffPlayer p = new StaffPlayer("staff", d.toLowerCase());
			p.remove();
			final PlayerData pd = GameServers.get().getPlayerDataManager().getPlayerData(d);
			pd.setInt("modPoints", 0);
			if (!pd.contains("oldRank") || pd.getInt("oldRank") == 7)
				pd.setRank(4);
			else
				pd.setRank(pd.getInt("oldRank"));
			pd.setBoolean("mod", false);
		} else
			st.removeFromStaff();
	}

	void setPoints(final String d, final int points) {
		final BStaff st = BPlayerHandler.get().getStaff(d);
		if (st == null) {
			final StaffPlayer p = new StaffPlayer("staff", d.toLowerCase());
			p.setInt("points", points);
		} else
			st.setPoints(points);
	}

}
