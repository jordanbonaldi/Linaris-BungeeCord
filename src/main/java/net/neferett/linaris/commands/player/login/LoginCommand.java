package net.neferett.linaris.commands.player.login;

import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.commands.PlayerCommand;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.utils.password.PasswordSecurity;

public class LoginCommand extends PlayerCommand {

	public LoginCommand() {
		super("login", false, "log", "l");
	}

	@Override
	public void execute(final BPlayer p, final PlayerData pd, final String[] args) {

		if (p.isLogged()) {
			p.sendMessage("§cVous êtes déjà connecté !");
			return;
		}

		if (!pd.contains("password")) {
			p.sendMessage("§aVotre compte n'existe pas encore !");
			p.sendMessage("§f");
			p.sendMessage("§eEnregistrez votre compte avec la commande suivante§f:");
			p.sendMessage("§c/register <MotDePasse> <MotDePasse>");
			return;
		}

		if (args.length >= 1) {

			final String password = args[0];

			try {

				if (PasswordSecurity.comparePasswordWithHash(password, pd.get("password")))
					p.logPlayer();
				else
					p.sendMessage("§cLe mot de passe est faux !");

			} catch (final Exception e) {
				p.sendMessage("§cErreur lors de la connexion !");
				e.printStackTrace();
			}

			return;
		}

		p.sendMessage("§e/login <MotDePasse>");

	}

}
