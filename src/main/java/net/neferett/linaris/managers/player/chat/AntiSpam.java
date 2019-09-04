package net.neferett.linaris.managers.player.chat;

import java.util.HashMap;
import java.util.Map;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.BPlayerHandler;
import net.neferett.linaris.utils.stringutils.LevenshteinDistance;

public class AntiSpam implements Listener {
	private final Map<String, Integer>	duplicateCount		= new HashMap<>();
	private final Map<String, Integer>	fastMessageCount	= new HashMap<>();
	private final Map<String, String>	lastMessage			= new HashMap<>();
	private final Map<String, Long>		lastMessageTime		= new HashMap<>();
	private final Map<String, Long>		mutes				= new HashMap<>();

	public AntiSpam() {}

	private String antiCaps(final String message) {
		final String noSpace = message.replace(" ", "");
		final float length = noSpace.length();
		if (length < 5)
			return message;
		float caps = 0;
		for (final char c : noSpace.toCharArray())
			if (String.valueOf(c).matches("[A-Z]"))
				caps++;
		if (caps / length > 0.4)
			return message.toLowerCase();
		else
			return message;
	}

	private boolean antiCharRepetition(final String message) {
		return message.matches(".*?(.)\\1{4,}.*?");
	}

	private boolean antiFlood(final String uuid) {
		final long now = System.currentTimeMillis();
		final long lastMsgMillis = this.lastMessageTime.containsKey(uuid) ? this.lastMessageTime.get(uuid) : 0;
		// Anti flood
		if (now - lastMsgMillis < 1000) {
			final int fastMsgCount = 1
					+ (this.fastMessageCount.containsKey(uuid) ? this.fastMessageCount.get(uuid) : 0);
			this.fastMessageCount.put(uuid, fastMsgCount);
			if (fastMsgCount >= 2)
				return true;
		} else
			this.fastMessageCount.put(uuid, 0);
		this.lastMessageTime.put(uuid, now);
		return false;
	}

	private boolean antiSpam(final String uuid, final String message) {
		/*
		 * Si true, bloque le message
		 */

		final long now = System.currentTimeMillis();
		final long lastMsgMillis = this.lastMessageTime.containsKey(uuid) ? this.lastMessageTime.get(uuid) : 0;

		if (now - lastMsgMillis > 6000) {
			this.duplicateCount.put(uuid, 0);
			return false;
		}

		// Anti répétition
		if (this.lastMessage.containsKey(uuid)
				&& LevenshteinDistance.similarity(this.lastMessage.get(uuid), message) >= 0.8) {
			if (!this.duplicateCount.containsKey(uuid))
				this.duplicateCount.put(uuid, 0);
			this.duplicateCount.put(uuid, this.duplicateCount.get(uuid) + 1);
		} else
			this.duplicateCount.put(uuid, 1);
		this.lastMessage.put(uuid, message);

		return this.duplicateCount.get(uuid) > 2;
	}

	public void onChat(final ChatEvent e) {
		if (!(e.getSender() instanceof ProxiedPlayer))
			return;
		final ProxiedPlayer p = (ProxiedPlayer) e.getSender();
		final String message = e.getMessage();

		if (BPlayerHandler.get().getPlayer(p).getRank().getModerationLevel() > 1)
			return;
		if (this.mutes.containsKey(p.getName().toLowerCase()))
			if (this.mutes.get(p.getName().toLowerCase()) > System.currentTimeMillis()) {
				p.sendMessage(TextComponent.fromLegacyText("§f[§c§lGuarden§f] §7Vous ne pouvez pas parler."));
				e.setCancelled(true);
				return;
			} else
				this.mutes.remove(p.getName().toLowerCase());
		if (this.antiFlood(p.getName().toLowerCase())) {
			p.sendMessage(TextComponent.fromLegacyText(
					"§f[§c§lGuarden§f] §7Spam détecté. Vous avez été réduit au silence pour 10 minutes."));
			this.mutes.put(p.getName().toLowerCase(), System.currentTimeMillis() + 600000);
			// mute 10 minutes
			e.setCancelled(true);
			return;
		}
		if (this.antiSpam(p.getName().toLowerCase(), message)) {
			e.setCancelled(true);
			p.sendMessage(TextComponent.fromLegacyText(
					"§f[§c§lGuarden§f] §7Spam détecté. Merci de ne pas répéter plusieurs fois le même message !"));
		}

		if (this.antiCharRepetition(message)) {
			e.setCancelled(true);
			p.sendMessage(TextComponent
					.fromLegacyText("§f[§c§lGuarden§f] §7Spam détecté. Merci de ne pas spammer les lettres !"));
		}

		e.setMessage(this.antiCaps(message));
	}
}
