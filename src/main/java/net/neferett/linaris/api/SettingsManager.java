package net.neferett.linaris.api;

import java.util.HashMap;
import java.util.Map;

import net.neferett.linaris.GameServers;

public class SettingsManager {

    public static Map<String, String> getSettings(String player) {
        Map<String, String> data = GameServers.get().getPlayerDataManager().getPlayerData(player).getValues();
        HashMap<String, String> settings = new HashMap<>();
        data.entrySet().stream().filter(line -> line.getKey().startsWith("settings.")).forEach(line -> {
            String setting = line.getKey().split(".")[0];
            settings.put(setting, line.getValue());
        });

        return settings;
    }

    public static String getSetting(String player, String setting) {
		return GameServers.get().getPlayerDataManager().getPlayerData(player).get("settings." + setting);
    }

    public static String getSetting(String player, String setting, String def) {
        String val = getSetting(player, setting);
        return (val == null) ? def : val;
    }

    public static boolean isEnabled(String player, String setting) {
        return GameServers.get().getPlayerDataManager().getPlayerData(player).getBoolean("settings." + setting);
    }

    public static boolean isEnabled(String player, String setting, boolean val) {
        return GameServers.get().getPlayerDataManager().getPlayerData(player).getBoolean("settings." + setting, val);
    }

    public static void setSetting(String player, String setting, String value) {
        GameServers.get().getPlayerDataManager().getPlayerData(player).set("settings." + setting, value);
    }
}
