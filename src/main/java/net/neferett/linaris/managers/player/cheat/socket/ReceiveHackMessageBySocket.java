package net.neferett.linaris.managers.player.cheat.socket;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.managers.bans.BanManager;
import net.neferett.linaris.managers.bans.BanReason;
import net.neferett.linaris.managers.player.BPlayerHandler;
import net.neferett.linaris.managers.player.cheat.HackEnum;
import net.neferett.linaris.managers.player.cheat.data.CheatData;
import net.neferett.linaris.utils.json.JSONException;
import net.neferett.linaris.utils.json.JSONObject;
import net.neferett.linaris.utils.messages.MyBuilder;

public class ReceiveHackMessageBySocket {

	public void HandleMessage(final String msg) throws JSONException {
		if (msg == null)
			return;
		final JSONObject js = new JSONObject(msg);

		final String name = js.getString("name");
		final String cheat = js.getString("cheat");
		final int violation = js.getInt("violation");
		final String server = js.getString("serv");
		final boolean kick = js.getBoolean("ban");

		final CheatData cheatdata = new CheatData(name);

		final HackEnum m = HackEnum.getEnumByName(cheat);

		if (kick) {
			cheatdata.addBan(m);

			final PlayerData pd = GameServers.get().getPlayerDataManager().getPlayerData(name);
			final BanReason b = BanManager.get().getReasons().stream().filter(e -> e.getName().equals("Cheat"))
					.findFirst().orElse(null);

			int time = 0;

			if (!pd.contains("bans-" + b.getName())) {
				pd.setInt("bans-" + b.getName(), b.getTimeinc());
				time = b.getTimeinc();
			} else if (pd.getInt("bans-" + b.getName()) < b.getTimemax()) {
				final int i = pd.getInt("bans-" + b.getName());
				pd.setInt("bans-" + b.getName(), i + b.getTimeinc());
				time = i;
			} else
				time = b.getTimemax();

			BanManager.get().pseudoBan(name, "Guarden", time, "Cheat", false, null, true);

			final ProxiedPlayer p = ProxyServer.getInstance().getPlayer(name);

			if (p != null)
				p.disconnect(TextComponent.fromLegacyText(BanManager.get().BannedEject(time < 0 ? true : false,
						b.getName(), System.currentTimeMillis(), time, p.getName())));

			return;
		} else
			BPlayerHandler.get().getOnlineStaffs().forEach(staff -> {
				staff.sendMessage(new MyBuilder("§c§lGuarden §7-> §f[§e" + server + "§f]" + " §a" + name + "§7 -> §c"
						+ m.getName() + " §f(§cVL §e" + violation + "§f)")
								.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										new MyBuilder("Clique pour te téléporter en ghost au joueur !").create()))
								.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/track " + name)).create());
			});

	}

}
