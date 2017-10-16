package net.neferett.linaris.managers.player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BStaff extends BPlayer {

	StaffPlayer	infos;
	private int	points;

	public BStaff(final ProxiedPlayer p) {
		super(p);
		this.infos = new StaffPlayer("staff", this.name, this.getRank());
		this.points = this.infos.contains("points") ? this.infos.getInt("points") : this.infos.setInt("points", 100);
		if (this.points <= 0)
			this.removeFromStaff();
	}

	public void addPoints(final int points) {
		this.points += points;
		this.sendMessage("§7Vous venez de gagner §c" + points + " points");
		this.infos.setInt("points", this.points);
	}

	public TextComponent build() {
		final TextComponent text = new TextComponent(
				this.getRank().getPrefix(this.pd) + "§" + this.getRank().getColor() + this.p.getName());

		text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/m " + this.p.getName()));
		text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				TextComponent.fromLegacyText(this.getPlayersInfo().toString())));
		return text;
	}

	char getColorPoints() {
		return this.points > 75 ? 'a' : this.points > 50 ? 'b' : this.points > 30 ? 'e' : this.points > 15 ? '6' : 'c';
	}

	private StringBuilder getPlayersInfo() {
		return new StringBuilder("§7Serveur§f: §a" + this.getServName()).append("\n")
				.append("§7Grade§f: §" + this.getRank().getColor() + this.getRank().getName()).append("\n")
				.append("§7Points§f: §" + this.getColorPoints() + this.points).append("\n")
				.append("§7En jeu depuis§f: §c" + this.onlineSince()).append("\n")
				.append("§7Version§f: §c" + this.getVersion()).append("\n")
				.append("§eClique pour envoyer un message !");
	}

	public void removePoints(final int points) {
		this.points -= points;
		if (this.points <= 0)
			this.removeFromStaff();
		this.sendMessage("§7Vous venez de perdre §c" + points + " points");
		this.infos.setInt("points", this.points);
	}

	public void setPoints(final int points) {
		this.points = points;
		this.sendMessage("§7Vos points ont été reinisialisé à §c" + points + " points");
		this.infos.setInt("points", this.points);
	}

}
