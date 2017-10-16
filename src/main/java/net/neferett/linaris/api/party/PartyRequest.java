package net.neferett.linaris.api.party;

import java.util.Date;

public class PartyRequest {

    private String from;
    private String to;
    @SuppressWarnings("unused")
	private Date sendDate;

    public PartyRequest() {

    }

    public PartyRequest(String from, String to, Date sendDate) {
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
