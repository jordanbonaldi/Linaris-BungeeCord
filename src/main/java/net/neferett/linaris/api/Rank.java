package net.neferett.linaris.api;

public enum Rank {

	ADMIN(9, "Admin", "§c[Admin] ", 4, 4, 'c', null, null),
	AMI(5, "Ami", "§fAmi §b✪§f ", 4, 0, 'f', null, null),
	DEV(11, "DEV", "§c[Dev] ", 4, 2, 'c', null, null),
	FONDATEUR(12, "Fondateur", "§c[Fondateur] ", 4, 5, 'c', null, null),
	HELPEUR(6, "Helpeur", "§3[Helpeur] ", 3, 1, '3', null, null),
	Joueur(0, "Joueur", "§7", 0, 0, '7', null, null),
	MINIVIP(1, "MiniVIP", "§f[MiniVIP] ", 1, 0, 'f', null, null),
	MOD(7, "Modo", "§6[Modérateur] ", 4, 2, '6', null, null),
	RESP(8, "Responsable", "§6[Resp.Modo] ", 4, 3, '6', null, null),
	SUPERVIP(4, "Héros", "§aHéros %c%s§a ", 4, 0, 'a', "§e", "✪"),
	VIP(2, "VIP", "§f[VIP] ", 2, 0, 'f', null, null),
	VIPPLUS(3, "VIP+", "§b[VIP%c+§b] ", 3, 0, 'b', "§b", null),
	YT(10, "YT", "§fY§cT %c%s§f ", 4, 0, 'f', "§c", "❤");

	public static Rank get(final int i) {
		for (final Rank rank : values())
			if (rank.id == i)
				return rank;
		return null;
	}

	public static Rank get(final String name) {
		for (final Rank rank : values())
			if (rank.getName().equals(name))
				return rank;
		return null;
	}

	private char	color;

	private int		id;
	private String	logo;
	private String	logocolor;
	private int		moderationLevel;

	private String	name;

	private String	prefix;

	private int		vipLevel;

	private Rank(final int id, final String name, final String prefix, final int vipLevel, final int moderationLevel,
			final char color, final String logocolor, final String logo) {
		this.id = id;
		this.logo = logo;
		this.logocolor = logocolor;
		this.name = name;
		this.prefix = prefix;
		this.vipLevel = vipLevel;
		this.moderationLevel = moderationLevel;
		this.color = color;
	}

	public int getCoinsBonus() {
		if (this.vipLevel == 0)
			return 0;
		else if (this.vipLevel == 1)
			return 50;
		else if (this.vipLevel == 2)
			return 100;
		else if (this.vipLevel == 3)
			return 200;
		else if (this.vipLevel == 4)
			return 400;
		else
			return 0;
	}

	public char getColor() {
		return this.color;
	}

	public double getECMultiplier() {
		if (this.vipLevel == 0)
			return 0;
		else if (this.vipLevel == 1)
			return 2;
		else if (this.vipLevel == 2)
			return 3;
		else if (this.vipLevel == 3)
			return 4;
		else if (this.vipLevel == 4)
			return 4;
		else
			return 0;
	}

	public int getID() {
		return this.id;
	}

	public double getLCMultiplier() {
		if (this.vipLevel == 0)
			return 0;
		else if (this.vipLevel == 1)
			return 0;
		else if (this.vipLevel == 2)
			return 0;
		else if (this.vipLevel == 3)
			return 0;
		else if (this.vipLevel == 4)
			return 2;
		else
			return 0;
	}

	public String getLogo(final PlayerData pd) {
		if (this.prefix.contains("%c") && this.prefix.contains("%s"))
			return (pd.contains("logocolor") ? pd.get("logocolor") : this.logocolor)
					+ (pd.contains("logo") ? pd.get("logo") : this.logo);
		else
			return pd.contains("logocolor") ? pd.get("logocolor") : this.logocolor;
	}

	public int getMCoinsBonus() {
		if (this.vipLevel == 0)
			return 0;
		else if (this.vipLevel == 1)
			return 0;
		else if (this.vipLevel == 2)
			return 0;
		else if (this.vipLevel == 3)
			return 0;
		else if (this.vipLevel == 4)
			return 200;
		else
			return 0;
	}

	public int getModerationLevel() {
		return this.moderationLevel;
	}

	public String getName() {
		return this.name;
	}

	public String getPrefix(final PlayerData pd) {
		if (this.prefix.contains("%c") && this.prefix.contains("%s"))
			return this.prefix.replace("%c", pd.contains("logocolor") ? pd.get("logocolor") : this.logocolor)
					.replace("%s", pd.contains("logo") ? pd.get("logo") : this.logo);
		else if (this.prefix.contains("%c"))
			return this.prefix.replace("%c", pd.contains("logocolor") ? pd.get("logocolor") : this.logocolor);
		else
			return this.prefix;
	}

	public int getVipLevel() {
		return this.vipLevel;
	}
}
