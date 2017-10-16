package net.neferett.linaris.managers.player.yt;

import java.io.IOException;
import java.util.List;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;

public class SubsCount {

	public interface searchCallBack {
		public void set(YouTube.Channels.List d);
	}

	String					APIKey;
	List<Channel>			ch;
	HttpRequestInitializer	hi;
	YouTube.Channels.List	searchengine;

	YouTube					yt;

	public SubsCount() {
		this.APIKey = "AIzaSyAKRaZt69XTBV4r9d79ju9zX8vWCGI3R4k";
		this.hi = a -> {};

		this.yt = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), this.hi)
				.setApplicationName("subcount").build();
	}

	public SubsCount buildSearch(final searchCallBack c) {
		try {
			this.searchengine = this.yt.channels().list("statistics");
			c.set(this.searchengine);
			this.searchengine.setKey(this.APIKey);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this;
	}

	public Channel getChannel() {
		try {
			return this.searchengine.execute().getItems().stream().findFirst().orElse(null);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
