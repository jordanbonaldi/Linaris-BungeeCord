package net.neferett.linaris.managers.player.chat;

import java.io.IOException;
import java.text.Normalizer;
import java.util.List;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.managers.player.BPlayerHandler;
import net.neferett.linaris.utils.files.ReadFile;

public class AntiSwear implements Listener {

	List<String>	badwords;
	List<String>	ignoredwords;

	int				maxlength;

	public AntiSwear() {
		try {
			this.refresh();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public int getMaxSearLength() {
		int max = 0;

		for (final String s : this.badwords)
			if (s.length() > max)
				max = s.length();
		return max;
	}

	public void onSwear(final ChatEvent e) {
		if (BPlayerHandler.get().getPlayer((ProxiedPlayer) e.getSender()).getRank().getModerationLevel() > 1)
			return;
		final String message = this.removeByPassingAttempts(e.getMessage().toLowerCase());

		for (int i = 0; i < message.length(); i++)
			for (int offset = 1; offset < message.length() + 1 - i && offset < this.maxlength; offset++) {
				final String tocheck = message.substring(i, i + offset);
				if (this.badwords.contains(tocheck)) {
					for (final String s : this.ignoredwords)
						if (message.contains(s))
							return;
					((ProxiedPlayer) e.getSender()).sendMessage(
							TextComponent.fromLegacyText("§f[§c§lGuarden§f] §7Votre phrase contient des insultes !"));
					e.setCancelled(true);
					return;
				}
			}
	}

	public void refresh() throws IOException {
		this.badwords = new ReadFile(GameServers.get().getDataFolder().getAbsolutePath() + "/blacklist.txt").getFile();
		this.ignoredwords = new ReadFile(GameServers.get().getDataFolder().getAbsolutePath() + "/ignored.txt")
				.getFile();
		if (this.badwords == null)
			return;
		this.maxlength = this.getMaxSearLength();
	}

	public String removeByPassingAttempts(String a) {
		a = Normalizer.normalize(a.replaceAll("(.)\\1{1,}", "$1"), Normalizer.Form.NFD)
				.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").trim();
		return a.replaceAll("1", "i").replaceAll("!", "i").replaceAll("3", "e").replaceAll("4", "a")
				.replaceAll("@", "a").replaceAll("5", "s").replaceAll("7", "t").replaceAll("0", "o")
				.replaceAll("9", "g");
	}

}
