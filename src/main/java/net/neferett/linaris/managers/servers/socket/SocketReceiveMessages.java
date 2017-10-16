package net.neferett.linaris.managers.servers.socket;

import net.neferett.linaris.managers.player.buy.BuySocketManager;
import net.neferett.linaris.managers.player.cheat.socket.ReceiveHackMessageBySocket;
import net.neferett.linaris.utils.json.JSONException;
import net.neferett.socket.events.manager.EventListener;
import net.neferett.socket.events.manager.SocketEvent;
import net.neferett.socket.packet.event.ReceiveMessageEvent;

public class SocketReceiveMessages implements EventListener {

	@SocketEvent
	public void onReceiveMessage(final ReceiveMessageEvent e) throws JSONException {
		final String message = e.getPacket().getMessage();
		if (message == null)
			return;

		if (message.startsWith("buy")) {
			new BuySocketManager(message).build();
			return;
		}
		// } else if (message.startsWith("exec ")) {
		// new SocketMessageHandler(message);
		// return;
		// }
		new ReceiveHackMessageBySocket().HandleMessage(message);

	}

}
