package net.neferett.linaris.managers.others;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class AutoMessages {

	final boolean	command;
	String			desc;
	String			infos;
	String			type;

	public AutoMessages(final String t, final String d, final boolean c, final String i) {
		this.type = t;
		this.desc = d;
		this.command = c;
		this.infos = i;
	}

	public void display() {
		final TextComponent t = new TextComponent("\n\n        §b§l«§6§l-§r§b§l»§b§m§l- §r§b§l«§6§l« §c§l"
				+ this.getType() + " §6§l»§b§l»§b§l§m- §r§b§l«§6§l-§b§l»\n\n");

		t.addExtra("  " + this.getDesc() + "\n\n");

		if (this.isCommand()) {
			t.addExtra("    §a➜ Faites §b/" + this.getInfos() + "§a pour en savoir plus !");
			t.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + this.getInfos()));
			t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					TextComponent.fromLegacyText("§cClique pour effectuer la commande")));
		} else {
			t.addExtra("    §a➜  Cliquez ici§f: §b§n" + this.getInfos());
			t.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, this.getInfos()));
			t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					TextComponent.fromLegacyText("§cClique pour ouvrir la page web")));
		}

		t.addExtra("\n\n");
		ProxyServer.getInstance().getPlayers().forEach(p -> p.sendMessage(t));
	}

	public String getDesc() {
		return this.desc;
	}

	public String getInfos() {
		return this.infos;
	}

	public String getType() {
		return this.type;
	}

	public boolean isCommand() {
		return this.command;
	}

}
