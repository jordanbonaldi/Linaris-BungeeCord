package net.neferett.linaris.commands.player.login;

import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.commands.PlayerCommand;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.utils.password.HashAlgorithm;
import net.neferett.linaris.utils.password.PasswordSecurity;

public class ChangePWCommand extends PlayerCommand {

	private static final String	PASSWORD_PATTERN	= "\\w{4,20}\\b";
	private Matcher				matcher;

	private final Pattern		pattern;

	public ChangePWCommand() {
		super("changepassword", false, "changepw", "cpw");
		this.pattern = Pattern.compile(PASSWORD_PATTERN);
	}

	@Override
	public void execute(final BPlayer p, final PlayerData pd, final String[] args) {

		if (args.length >= 2) {

			final String oldpassword = args[0];
			final String newpassword = args[1];

			if (oldpassword.equals(newpassword)) {
				p.sendMessage("");
				p.sendMessage("");
				p.sendMessage("§cLes mots de passes sont les mêmes");
				p.sendMessage("");
				p.sendMessage("");
				return;
			}

			if (!this.validate(newpassword)) {
				p.sendMessage("");
				p.sendMessage("");
				p.sendMessage("§cVotre mot de passe n'est pas assez sécurisé !");
				p.sendMessage("§cIl doit contenir au minimum:");
				p.sendMessage("§cEt faire entre 4 et 20 caractères");
				p.sendMessage("");
				p.sendMessage("");

				return;
			}
			try {
				if (PasswordSecurity.comparePasswordWithHash(oldpassword, pd.get("password"))) {

					String encryptPassword;
					try {
						encryptPassword = PasswordSecurity.getHash(HashAlgorithm.SHA256, newpassword,
								p.getName().toLowerCase());
					} catch (final NoSuchAlgorithmException e) {
						p.sendMessage("§cErreur lors du changement de mot de passe");
						e.printStackTrace();
						return;
					}

					pd.set("password", encryptPassword);
					p.sendMessage("");
					p.sendMessage("");
					p.sendMessage("§a§nMot de passe changé !");
					p.sendMessage("");
					p.sendMessage("");

				} else {
					p.sendMessage("");
					p.sendMessage("");
					p.sendMessage("§cVotre mot de passe actuel est faux !");
					p.sendMessage("");
					p.sendMessage("");

				}
			} catch (final NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			return;
		}

		p.sendMessage("§c/changepw <Ancien MotDePasse> <Nouveau MotdePasse>");

	}

	public boolean validate(final String password) {

		this.matcher = this.pattern.matcher(password);
		return this.matcher.matches();

	}

}
