package net.neferett.linaris.commands.administration;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.neferett.linaris.GameServers;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.utils.stringutils.Strings;

public class PlayerDataConsoleCommand extends Command {

	public PlayerDataConsoleCommand() {
		super("datasetter");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender, String[] args) {

		if (sender instanceof ProxiedPlayer) return;
		
		if (args.length < 3) {
			sender.sendMessage("§c/datasetter [Joueur] [Key] [Value]");
			return;
		}
		
		PlayerData target = GameServers.get().getPlayerDataManager().getPlayerData(args[0]);
		if (target == null) return;
		if(args[1].equalsIgnoreCase("coins")){
			int coins = (int)target.getCoins();
			int total = coins + Integer.parseInt(args[2]);
			target.set("coins", Integer.toString(total));
			
			sendMessageToStaff(Strings.modPrefix + "§b" + sender.getName() + " §8§l> §e" + args[0].toLowerCase() + "§9 " + args[1] + "§7 : §9" + total);
		}else if(args[1].equalsIgnoreCase("legendarycoins")){
			int coins = (int)target.getSuperCoins();
			int total = coins + Integer.parseInt(args[2]);
			target.set("legendarycoins", Integer.toString(total));
			sendMessageToStaff(Strings.modPrefix + "§b" + sender.getName() + " §8§l> §e" + args[0].toLowerCase() + "§9 " + args[1] + "§7 : §9" + total);
		}else if(args[1].equalsIgnoreCase("rank")){
			target.set(args[1], args[2]);
			sendMessageToStaff(Strings.modPrefix + "§b" + sender.getName() + " §8§l> §e" + args[0].toLowerCase() + "§9 " + args[1] + "§7 : §9" + args[2] );
		}
	}
	
	@SuppressWarnings("deprecation")
	public void sendMessageToStaff(String message) {
		for (ProxiedPlayer pl : GameServers.get().getProxy().getPlayers()) {

			PlayerData target = GameServers.get().getPlayerDataManager().getPlayerData(pl.getName());
			if (target.getRank().getModerationLevel() >= 4)
				pl.sendMessage(message);
		}
	}


}
