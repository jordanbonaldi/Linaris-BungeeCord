package net.neferett.linaris.managers.bans;

public class BanReason {

	String	name;

	int		timeinc;

	int		timemax;

	public BanReason(final String name, final int inc, final int max) {
		this.name = name;
		this.timeinc = inc;
		this.timemax = max;
	}

	public String getName() {
		return this.name;
	}

	public int getTimeinc() {
		return this.timeinc;
	}

	public int getTimemax() {
		return this.timemax;
	}

}
