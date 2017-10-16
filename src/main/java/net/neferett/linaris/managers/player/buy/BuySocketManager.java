package net.neferett.linaris.managers.player.buy;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.neferett.linaris.managers.player.buy.PlayerBuy.BuyItem;

public class BuySocketManager {

	private final String		amount;
	private final String		date;
	private final BuyItem		i;
	private final String		ip;
	private final String		item;
	private final List<String>	msg;
	private final String		name;
	private final String		serv;

	public BuySocketManager(final String msg) {
		this.msg = Arrays.asList(msg.split("@"));
		this.i = this.msg.get(1).equals("token") ? BuyItem.TOKEN : BuyItem.OTHER;
		this.name = this.msg.get(2);
		this.amount = this.msg.get(3);
		this.item = this.msg.get(4);
		this.date = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
		this.serv = this.msg.get(6);
		this.ip = ProxyServer.getInstance().getPlayer(this.name).getAddress().getAddress().getHostAddress();
	}

	public void build() {
		ProxyServer.getInstance().broadcast(
				TextComponent.fromLegacyText("§f[§cGuarden§f] §e" + this.name + " §7vient d'acheter §e" + this.item));
		new PlayerBuy(this.i, this.name, Integer.parseInt(this.amount), this.ip, this.date, this.item, this.serv)
				.addToRedis();
	}

}
