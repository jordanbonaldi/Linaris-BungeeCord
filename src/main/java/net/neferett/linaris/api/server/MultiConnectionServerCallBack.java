package net.neferett.linaris.api.server;

import java.util.List;

import net.neferett.linaris.managers.player.BPlayer;

public interface MultiConnectionServerCallBack {

	public void done(GameServer server, ConnectionStatus status, List<BPlayer> players);

}
