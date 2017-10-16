package net.neferett.linaris.commands.administration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.commands.PlayerCommand;
import net.neferett.linaris.managers.player.BPlayer;
import net.neferett.linaris.managers.player.buy.PlayerBuy;
import net.neferett.linaris.managers.player.buy.PlayerBuy.BuyItem;

public class ShopManagerCommand extends PlayerCommand {

	public ShopManagerCommand() {
		super("shopmanager", true);
	}

	public void displayInformation(final BPlayer p, final PlayerBuy v) {
		p.sendMessage("§e" + v.getName() + " §7a reçu §c" + v.getAmount() + " " + v.getItem() + "§7 le §a" + v.getDate()
				+ "§7 sur l'ip §b" + v.getIp() + "§7 depuis le §e" + v.getServer());
	}

	@Override
	public void execute(final BPlayer p, final PlayerData pd, final String[] args) {
		if (pd.getRank().getModerationLevel() < 2)
			return;

		if (args.length < 2) {
			p.sendMessage(
					"§c/shopmanager <Token | Store> <limit> <date | amount | ip | itemname(only store) | player | server> <data>");
			return;
		}

		final PlayerBuy pl = new PlayerBuy(args[0].contains("token") ? BuyItem.TOKEN : BuyItem.OTHER);

		if (args.length == 2) {
			p.sendMessage("§f§m----------- §r§c Guarden §f§m-----------");
			p.sendMessage("");
			this.imposeLimit(pl.getAll(), Integer.parseInt(args[1])).forEach(v -> this.displayInformation(p, v));
			return;
		} else if (args.length >= 4) {
			p.sendMessage("§f§m----------- §r§c Guarden §f§m-----------");
			p.sendMessage("");
			TreeMap<String, PlayerBuy> map;
			switch (args[2]) {
				case "date":
					final StringBuilder b = new StringBuilder(args[3]);
					for (int i = 4; i < args.length; i++)
						b.append(" " + args[i]);
					map = pl.getByDate(b.toString());
					if (map.size() == 0) {
						p.sendMessage("Aucunes données");
						break;
					}
					this.imposeLimit(map, Integer.parseInt(args[1])).forEach(v -> this.displayInformation(p, v));
					break;
				case "amount":
					map = pl.getByAmount(Integer.parseInt(args[3]));
					if (map.size() == 0) {
						p.sendMessage("Aucunes données");
						break;
					}
					this.imposeLimit(map, Integer.parseInt(args[1])).forEach(v -> this.displayInformation(p, v));
					break;
				case "ip":
					map = pl.getByIP(args[3]);
					if (map.size() == 0) {
						p.sendMessage("Aucunes données");
						break;
					}
					this.imposeLimit(map, Integer.parseInt(args[1])).forEach(v -> this.displayInformation(p, v));
					break;
				case "itemname":
					final StringBuilder m = new StringBuilder(args[3]);
					for (int i = 4; i < args.length; i++)
						m.append(" " + args[i]);
					map = pl.getByItem(m.toString());
					if (map.size() == 0) {
						p.sendMessage("Aucunes données");
						break;
					}
					this.imposeLimit(map, Integer.parseInt(args[1])).forEach(v -> this.displayInformation(p, v));
					break;
				case "player":
					map = pl.getByPlayer(args[3]);
					if (map.size() == 0) {
						p.sendMessage("Aucunes données");
						break;
					}
					this.imposeLimit(map, Integer.parseInt(args[1])).forEach(v -> this.displayInformation(p, v));
					break;
				case "server":
					map = pl.getByServer(args[3]);
					if (map.size() == 0) {
						p.sendMessage("Aucunes données");
						break;
					}
					this.imposeLimit(map, Integer.parseInt(args[1])).forEach(v -> this.displayInformation(p, v));
					break;
				default:
					p.sendMessage(
							"§cMerci de specifier une valeur : date | amount | ip | itemname(only store) | player | server");
					break;
			}
			return;
		}
		p.sendMessage(
				"§cUtilisation incorrecte: §c/shopmanager <Token | Store> <limit> <date | amount | ip | itemname(only store) | player | server> <data>");
	}

	public long getTimeMillis(final PlayerBuy o1) {
		final SimpleDateFormat formatter = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss");
		Date date = null;
		try {
			date = formatter.parse(o1.getDate());
		} catch (final ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date.getTime();
	}

	public List<PlayerBuy> imposeLimit(final NavigableMap<String, PlayerBuy> map, final int limit) {
		return map.values().stream().sorted((o1, o2) -> (int) (this.getTimeMillis(o2) - this.getTimeMillis(o1)))
				.limit(limit).collect(Collectors.toList());
	}

}
