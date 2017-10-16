package net.neferett.linaris.api.server;

import net.neferett.linaris.managers.player.BPlayer;

public interface SoloConnectionServerCallBack {

	public void done(GameServer server, ConnectionStatus status, BPlayer player);

}
