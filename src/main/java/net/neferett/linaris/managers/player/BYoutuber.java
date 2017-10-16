package net.neferett.linaris.managers.player;

import java.math.BigInteger;

import com.google.api.services.youtube.model.ChannelStatistics;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.neferett.linaris.api.Rank;
import net.neferett.linaris.managers.player.yt.SubsCount;

public class BYoutuber extends BPlayer {

	ChannelStatistics	ch;
	StaffPlayer			infos;
	SubsCount			sc;

	public BYoutuber(final ProxiedPlayer p) {
		super(p);
		this.infos = new StaffPlayer("yt", p.getName().toLowerCase(), Rank.YT);
		if (this.isConfig()) {
			this.sc = new SubsCount().buildSearch((e) -> e.setId(this.infos.get("channelid")));
			this.ch = this.sc.getChannel().getStatistics();
		}
	}

	public BigInteger getSubs() {
		return this.ch.getSubscriberCount();
	}

	public BigInteger getVideos() {
		return this.ch.getVideoCount();
	}

	public BigInteger getViews() {
		return this.ch.getViewCount();
	}

	public boolean isConfig() {
		return this.infos.contains("channelid");
	}

}
