package net.neferett.linaris.commands.player.login;

import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.commands.PlayerCommand;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.utils.password.HashAlgorithm;
import net.neferett.linaris.utils.password.PasswordSecurity;

public class RegisterCommand extends PlayerCommand {

	private static final String	PASSWORD_PATTERN	= "\\w{4,20}\\b";
	private Matcher				matcher;

	private final Pattern		pattern;

	public RegisterCommand() {
		super("register", false, "reg");
		this.pattern = Pattern.compile(PASSWORD_PATTERN);
	}

	@Override
	public void execute(final BPlayer p, final PlayerData pd, final String[] args) {

		if (p.isLogged()) {
			p.sendMessage("§cVous êtes déjà connecté !");
			return;
		}

		if (pd.contains("password")) {
			p.sendMessage("§aVotre compte existe déjà..");
			p.sendMessage("§f");
			p.sendMessage("§eConnectez vous avec la commande suivante§f:");
			p.sendMessage("§c/login <MotDePasse>");
			return;
		}

		if (args.length >= 2) {

			final String password = args[0];
			final String repassword = args[1];

			if (!password.equals(repassword)) {
				p.sendMessage("§cLes mots de passes ne correspondent pas !");
				return;
			}

			if (!this.validate(password)) {
				p.sendMessage("§cVotre mot de passe n'est pas assez sécurisé !");
				p.sendMessage("§cIl doit contenir au minimum:");
				p.sendMessage("§cEt faire entre 4 et 20 caractères");
				return;
			}

			String encryptPassword;
			try {
				encryptPassword = PasswordSecurity.getHash(HashAlgorithm.SHA256, password, p.getName().toLowerCase());
			} catch (final NoSuchAlgorithmException e) {
				p.sendMessage("§cErreur lors de l'inscription");
				e.printStackTrace();
				return;
			}

			pd.set("password", encryptPassword);
			try {
				p.logPlayer();
			} catch (final Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return;
		}

		p.sendMessage("§c/register <MotDePasse> <MotDePasse>");

	}

	public boolean validate(final String password) {

		this.matcher = this.pattern.matcher(password);
		return this.matcher.matches();

	}

}
