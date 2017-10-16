package net.neferett.linaris.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.neferett.linaris.GameServers;

public class ShopItemsManager {

    public static List<ItemInfo> getItems(String player) {
        Map<String, String> data = GameServers.get().getPlayerDataManager().getPlayerData(player).getValues();
        List<ItemInfo> items = new ArrayList<ItemInfo>();
        data.entrySet().stream().filter(line -> line.getKey().startsWith("items.")).forEach(line -> {
            String setting = line.getKey().split(".")[0];
            try {
				int level = Integer.parseInt(line.getValue());
	            items.add(new ItemInfo(setting, level));
			} catch (Exception e) {
			}
        });

        return items;
    }

    public static ItemInfo getItem(String player, String item) {
    	try {
    		int level =Integer.parseInt(GameServers.get().getPlayerDataManager().getPlayerData(player).get("items." + item));
    		return new ItemInfo(item, level);
		} catch (Exception e) {
			return null;
		}
    }
    
    public static boolean haveItem(String player, String item) {
    	return GameServers.get().getPlayerDataManager().getPlayerData(player).contains("items." + item);
    }
    
    public static void setItem(String player, ItemInfo item) {
        GameServers.get().getPlayerDataManager().getPlayerData(player).set("items." + item.getUUID(), Integer.toString(item.getLevel()));
    }
    
}
