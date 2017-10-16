package net.neferett.linaris.managers.player.cheat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.neferett.linaris.managers.player.cheat.data.CheatData;

public enum HackEnum {

	ANGLE("KillAura.v4"),
	BADPACKETS("BadPackets"),
	CRITICALS("Criticals"),
	FASTBOW("FastBow"),
	FIGHTSPEED("FightSpeed"),
	FORCEFIELD("KillAura.v3"),
	HITBOX("KillAura.v1"),
	JESUS("Jesus"),
	KILLAURA("KillAura.v2"),
	KNOCKBACK("AntiKnockBack.v1"),
	REACH("Reach"),
	REGEN("FastRegen"),
	VELOCITY("AntiKnockBack.v2");

	public static HackEnum getEnumByName(final String name) {
		final HackEnum hack = Arrays.asList(values()).stream()
				.filter(h -> h.toString().toLowerCase().equalsIgnoreCase(name.toLowerCase()))
				.collect(Collectors.toList()).get(0);
		if (hack == null)
			return null;
		else
			return hack;
	}

	public static List<HackEnum> valuesAsList() {
		return Arrays.asList(values());
	}

	private String name;

	private HackEnum(final String name) {
		this.name = name;
	}

	public int getBans(final CheatData d, final String name) {
		return d.getBan(HackEnum.getEnumByName(name));
	}

	public String getName() {
		return this.name;
	}

}
