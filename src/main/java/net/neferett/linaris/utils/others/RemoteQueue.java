package net.neferett.linaris.utils.others;

import java.util.Date;

import net.neferett.linaris.api.Games;

public class RemoteQueue {
	
	String playerName;
	Games game;
	String mapId;
	QueueType type;
	boolean first;
	Date startDate;
	Date lastSay;
	
	boolean finish;

	public Games getGame() {
		return game;
	}
	
	public String getMapID() {
		return mapId;
	}
	
	public String getMapName() {
		return mapId.replace("_", " ");
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public boolean isFirst() {
		return first;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public QueueType getType() {
		return type;
	}
	
	public RemoteQueue(String playerName, Games game,String mapId,QueueType type,boolean first) {
		this.playerName = playerName;
		this.game = game;
		this.type = type;
		this.mapId = mapId;
		this.first = first;
		this.startDate = new Date();
		this.lastSay = new Date();
	}
	
	public void setFinish(boolean finish) {
		this.finish = finish;
	}
	
	public boolean isFinish() {
		return this.finish;
	}
	
	public Date getLastSay() {
		return lastSay;
	}
	
	public void setLastSay(Date lastSay) {
		this.lastSay = lastSay;
	}
	
	public enum QueueType {
		ALONE,
		PARTY
	}
}
