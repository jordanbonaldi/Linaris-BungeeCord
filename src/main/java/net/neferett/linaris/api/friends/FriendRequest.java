package net.neferett.linaris.api.friends;

import java.util.Date;

public class FriendRequest {

    private String from;
    private String to;
    @SuppressWarnings("unused")
	private Date sendDate;

    public FriendRequest() {

    }

    public FriendRequest(String from, String to, Date sendDate) {
        this.from = from;
        this.to = to;
        this.sendDate = sendDate;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
}
