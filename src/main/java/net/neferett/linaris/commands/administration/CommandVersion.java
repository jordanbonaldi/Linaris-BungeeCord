package net.neferett.linaris.commands.administration;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.commands.PlayerCommand;
import net.neferett.linaris.managers.player.BPlayer;

public class CommandVersion extends PlayerCommand {

	private int	_v_07;
	private int	_v_08;
	private int	_v_09;
	private int	_v_10;
	private int	_v_11;
	private int	_v_12;

	public CommandVersion() {
		super("onlineversion", true);
	}

	@Override
	public void execute(final BPlayer p, final PlayerData pd, final String[] args) {
		if (pd.getRank().getModerationLevel() > 3) {

			p.sendMessage(ChatColor.GRAY + "§m-----------------------------------------------------");
			p.sendMessage("");
			p.sendMessage(ChatColor.GREEN + "" + "Version des joueurs §f:");
			p.sendMessage("");

			this._v_12 = this._v_11 = this._v_10 = this._v_09 = this._v_08 = this._v_07 = 0;
			this.version();

			p.sendMessage("§eJoueurs en 1.12§f: §c" + this._v_12);
			p.sendMessage("§eJoueurs en 1.11§f: §c" + this._v_11);
			p.sendMessage("§eJoueurs en 1.10§f: §c" + this._v_10);
			p.sendMessage("§eJoueurs en 1.9§f: §c" + this._v_09);
			p.sendMessage("§eJoueurs en 1.8§f: §c" + this._v_08);
			p.sendMessage("§eJoueurs en 1.7§f: §c" + this._v_07);
			p.sendMessage("");
			p.sendMessage("");
			p.sendMessage("§7Joueurs en ligne§f: §e"
					+ (this._v_12 + this._v_11 + this._v_10 + this._v_09 + this._v_08 + this._v_07));

			p.sendMessage("");
			p.sendMessage(ChatColor.GRAY + "§m-----------------------------------------------------");

		}

	}

	public void version() {

		for (final ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
			final int version = pp.getPendingConnection().getVersion();
			if (version > 316)
				this._v_12++;
			else if (version > 300 && version < 318)
				this._v_11++;
			else if (version > 200 && version < 301)
				this._v_10++;
			else if (version <= 110 && version >= 48)
				this._v_09++;
			else if (pp.getPendingConnection().getVersion() <= 47 && version >= 6)
				this._v_08++;
			else if (version <= 5 && version >= 0)
				this._v_07++;
		}
	}

}
