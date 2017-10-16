package net.neferett.linaris.managers.player.buy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import net.neferett.linaris.GameServers;
import redis.clients.jedis.Jedis;

public class PlayerBuy {

	public enum BuyItem {
		OTHER, TOKEN;
	}

	private int					amount;
	private String				date;
	private final BuyItem		i;
	private String				ip;
	private String				item;
	private Map<String, String>	map;

	private String				name;

	private String				server;

	public PlayerBuy(final BuyItem i) {
		this.i = i;
		final Jedis j = GameServers.get().getConnector().getTokenResource();
		this.map = j.hgetAll(this.i == BuyItem.TOKEN ? "tokens" : "buys");
		j.close();
	}

	public PlayerBuy(final BuyItem i, final String name, final int amount, final String ip, final String date,
			final String server) {
		this.i = i;
		this.name = name;
		this.ip = ip;
		this.date = date;
		this.amount = amount;
		this.server = server;
		this.item = "TOKENS";
	}

	public PlayerBuy(final BuyItem i, final String name, final int amount, final String ip, final String date,
			final String item, final String server) {
		this(i, name, amount, ip, date, server);
		this.item = item;
	}

	public void addToRedis() {
		GameServers.get().getTasksManager().addTask(() -> {
			final Jedis j = GameServers.get().getConnector().getTokenResource();
			j.hset(this.i == BuyItem.TOKEN ? "tokens" : "buys", this.name + "@" + this.date,
					this.amount + "@" + this.ip + "@" + this.date + "@" + this.item + "@" + this.server);
			j.close();
		});
	}

	public TreeMap<String, PlayerBuy> getAll() {
		return new TreeMap<>(this.map.entrySet().stream()
				.collect(Collectors.toMap((e) -> e.getKey(),
						(e) -> new PlayerBuy(this.i, e.getKey().split("@")[0],
								Integer.parseInt(e.getValue().split("@")[0]), e.getValue().split("@")[1],
								e.getValue().split("@")[2], e.getValue().split("@")[3], e.getValue().split("@")[4]))));
	}

	public int getAmount() {
		return this.amount;
	}

	public TreeMap<String, PlayerBuy> getByAmount(final int amount) {
		return new TreeMap<>(this.map.entrySet().stream()
				.filter((k) -> k.getValue().split("@")[0].equalsIgnoreCase(String.valueOf(amount)))
				.collect(Collectors.toMap((e) -> e.getKey(),
						(e) -> new PlayerBuy(this.i, e.getKey().split("@")[0],
								Integer.parseInt(e.getValue().split("@")[0]), e.getValue().split("@")[1], e.getValue()
										.split("@")[2],
								e.getValue().split("@")[3], e.getValue().split("@")[4]))));
	}

	public TreeMap<String, PlayerBuy> getByDate(final String date) {
		return new TreeMap<>(this.map.entrySet().stream().filter((k) -> k.getValue().split("@")[2].contains(date))
				.collect(Collectors.toMap((e) -> e.getKey(),
						(e) -> new PlayerBuy(this.i, e.getKey().split("@")[0],
								Integer.parseInt(e.getValue().split("@")[0]), e.getValue().split("@")[1],
								e.getValue().split("@")[2], e.getValue().split("@")[3], e.getValue().split("@")[4]))));
	}

	public TreeMap<String, PlayerBuy> getByIP(final String ip) {
		return new TreeMap<>(this.map.entrySet().stream().filter((k) -> k.getValue().split("@")[1].equalsIgnoreCase(ip))
				.collect(Collectors.toMap((e) -> e.getKey(),
						(e) -> new PlayerBuy(this.i, e.getKey().split("@")[0],
								Integer.parseInt(e.getValue().split("@")[0]), e.getValue().split("@")[1],
								e.getValue().split("@")[2], e.getValue().split("@")[3], e.getValue().split("@")[4]))));
	}

	public TreeMap<String, PlayerBuy> getByItem(final String item) {
		if (!(this.i == BuyItem.OTHER))
			return null;
		return new TreeMap<>(this.map.entrySet().stream()
				.filter((k) -> k.getValue().split("@")[3].substring(2).equalsIgnoreCase(item))
				.collect(Collectors.toMap((e) -> e.getKey(),
						(e) -> new PlayerBuy(this.i, e.getKey().split("@")[0],
								Integer.parseInt(e.getValue().split("@")[0]), e.getValue().split("@")[1], e.getValue()
										.split("@")[2],
								e.getValue().split("@")[3], e.getValue().split("@")[4]))));
	}

	public TreeMap<String, PlayerBuy> getByPlayer(final String name) {
		return new TreeMap<>(this.map.entrySet().stream().filter((k) -> k.getKey().split("@")[0].equalsIgnoreCase(name))
				.collect(Collectors.toMap((e) -> e.getKey(),
						(e) -> new PlayerBuy(this.i, e.getKey().split("@")[0],
								Integer.parseInt(e.getValue().split("@")[0]), e.getValue().split("@")[1],
								e.getValue().split("@")[2], e.getValue().split("@")[3], e.getValue().split("@")[4]))));
	}

	public TreeMap<String, PlayerBuy> getByServer(final String name) {
		return new TreeMap<>(this.map.entrySet().stream()
				.filter((k) -> k.getValue().split("@")[4].equalsIgnoreCase(name))
				.collect(Collectors.toMap((e) -> e.getKey(),
						(e) -> new PlayerBuy(this.i, e.getKey().split("@")[0],
								Integer.parseInt(e.getValue().split("@")[0]), e.getValue().split("@")[1],
								e.getValue().split("@")[2], e.getValue().split("@")[3], e.getValue().split("@")[4]))));
	}

	public String getDate() {
		return this.date;
	}

	public String getIp() {
		return this.ip;
	}

	public String getItem() {
		return this.item;
	}

	public String getName() {
		return this.name;
	}

	public String getServer() {
		return this.server;
	}

	public long getTimeMillis(final Map.Entry<String, String> o1) {
		final SimpleDateFormat formatter = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss");
		Date date = null;
		try {
			date = formatter.parse(o1.getValue().split("@")[2]);
		} catch (final ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date.getTime();
	}
}
