package net.neferett.linaris.managers.others;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;

import net.md_5.bungee.api.ServerPing;
import net.neferett.linaris.managers.bans.BanManager;
import net.neferett.linaris.managers.bans.BanReason;
import net.neferett.linaris.utils.files.DownloaderFile;
import net.neferett.linaris.utils.files.ReadFile;
import net.neferett.linaris.utils.json.JSONArray;
import net.neferett.linaris.utils.json.JSONException;
import net.neferett.linaris.utils.json.JSONObject;

public class ConfigManager {

	int						cheat;
	ArrayList<String>		files;
	JSONObject				j;
	StringBuilder			jsonfile;
	String					motd;
	String					motdfull;
	List<String>			news;
	ServerPing.PlayerInfo[]	playersPing;
	int						slots;
	String					topmotd;

	public ConfigManager() {
		this.refresh();
	}

	public void build() {
		try {
			this.read("bg");
			this.slots = this.j.getInt("slots");
			this.cheat = this.j.getInt("cheat");

			this.read("motd");
			this.loadJSONMotd();

			this.read("messages");
			this.loadNews();
			this.loadPings();
			this.handleAutoMessages();

//			this.read("banreasons");
//			this.readBanReason();
		} catch (final JSONException | IOException e) {
			e.printStackTrace();
		}
	}

	public int getCheat() {
		return this.cheat;
	}

	JSONObject getFromArray(final JSONArray j, final int i) {
		try {
			return j.getJSONObject(i);
		} catch (final JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	String getJSON(final JSONObject e, final String key) {
		try {
			return e.getString(key);
		} catch (final JSONException e1) {
			e1.printStackTrace();
		}
		return key;
	}

	public String getMotd() {
		return this.motd;
	}

	public String getMotdfull() {
		return this.motdfull;
	}

	public List<String> getNews() {
		return this.news;
	}

	public ServerPing.PlayerInfo[] getPlayersPing() {
		return this.playersPing;
	}

	public int getSlots() {
		return this.slots;
	}

	public String getTopmotd() {
		return this.topmotd;
	}

	void handleAutoMessages() throws JSONException {
		AutoMessageManager.get().kill();
		final JSONObject news = this.j.getJSONObject("automessage");

		final JSONArray newsarray = news.getJSONArray("list");

		IntStream.range(0, newsarray.length()).mapToObj(e -> this.getFromArray(newsarray, e))
				.collect(Collectors.toList()).forEach(e -> {
					try {
						AutoMessageManager.get().addAutoMessage(e.getString("type"), e.getString("desc"),
								e.getBoolean("command"), e.getString("info"));
					} catch (final JSONException e1) {
						e1.printStackTrace();
					}
				});
		AutoMessageManager.get().start();
	}

	public void loadJSONMotd() throws JSONException {
		this.motd = this.j.getBoolean("announce") ? this.j.getString("motdannounce") : this.j.getString("motd");
		this.topmotd = this.j.getString("top");
		this.motdfull = this.j.getString("motdfull");
	}

	void loadNews() throws JSONException {
		JSONObject news = this.j.getJSONObject("news");

		if (!news.getBoolean("activated")) {
			news = null;
			return;
		}

		final JSONArray newsarray = news.getJSONArray("news");

		this.news = IntStream.range(0, newsarray.length()).mapToObj(e -> this.getFromArray(newsarray, e))
				.map(e -> "  §e" + this.getJSON(e, "name") + "§f: §6" + this.getJSON(e, "desc"))
				.collect(Collectors.toList());

	}

	void loadPings() throws JSONException {
		final JSONObject ping = this.j.getJSONObject("ping");

		final List<String> lines = Arrays.asList(ping.getString("message").replace("\t", "").split("²"));
		this.playersPing = new ServerPing.PlayerInfo[lines.size()];
		for (int i = 0; i < this.playersPing.length; i++)
			this.playersPing[i] = new ServerPing.PlayerInfo(lines.get(i), "");

	}

	void read(final String configname) throws IOException, JSONException {
		this.files = new ReadFile("plugins/BungeeAPI/" + configname + ".json").getFile();

		this.jsonfile = new StringBuilder().append(StringUtils.join(this.files, " "));

		this.j = new JSONObject(this.jsonfile.toString());

	}

	void readBanReason() throws JSONException {
		final JSONArray jsarr = this.j.getJSONArray("reasons");

		final BanManager bm = BanManager.get();
		bm.clearReasons();
		IntStream.range(0, jsarr.length()).mapToObj(e -> this.getFromArray(jsarr, e)).forEach(e -> {
			try {
				bm.addReason(new BanReason(e.getString("name"), e.getInt("inc"), e.getInt("max")));
			} catch (final JSONException e1) {
				e1.printStackTrace();
			}
		});
	}

	public void refresh() {
		DownloaderFile.downloadFromURL("http://163.172.82.135:8989/bg.json", "./plugins/BungeeAPI/");
//		DownloaderFile.downloadFromURL("http://163.172.82.135:8989/banreasons.json", "./plugins/BungeeAPI/config/");
		DownloaderFile.downloadFromURL("http://163.172.82.135:8989/messages.json", "./plugins/BungeeAPI/");
		DownloaderFile.downloadFromURL("http://163.172.82.135:8989/motd.json", "./plugins/BungeeAPI/");
//		DownloaderFile.downloadFromURL("http://163.172.82.135:8989/blacklist.txt", "./plugins/BungeeAPI/");
//		DownloaderFile.downloadFromURL("http://163.172.82.135:8989/ignored.txt", "./plugins/BungeeAPI/");
		this.build();
	}

}
