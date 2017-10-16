package net.neferett.linaris.commands.moderation;

import java.security.NoSuchAlgorithmException;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.commands.PlayerCommand;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.utils.password.HashAlgorithm;
import net.neferett.linaris.utils.password.PasswordSecurity;

public class ChangepassCommand extends PlayerCommand {

	public ChangepassCommand() {
		super("changepwa", true);
	}

	@Override
	public void execute(final BPlayer p, final PlayerData pd, final String[] args) {
		if (pd.getRank().getModerationLevel() >= 3)
			if (args.length >= 1) {
				String encryptPassword;
				try {
					encryptPassword = PasswordSecurity.getHash(HashAlgorithm.SHA256, args[1],
							p.getName().toLowerCase());
				} catch (final NoSuchAlgorithmException e) {
					p.sendMessage("§cErreur lors du changement de mot de passe");
					e.printStackTrace();
					return;
				}
				final ProxiedPlayer pp = ProxyServer.getInstance().getPlayer(args[0]);
				final PlayerData pdd = GameServers.get().getPlayerDataManager().getPlayerData(pp.getName());
				p.sendMessage("§7Mot de passe changé en §e" + args[1] + " §7pour§f §c" + args[0]);
				pdd.set("password", encryptPassword);

			} else
				p.sendMessage("§c/changepwa <joueur> <mot de passe>");

	}

}
