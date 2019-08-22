package net.neferett.linaris.utils.config;

import java.io.File;
import java.io.IOException;

import net.craftminecraft.bungee.bungeeyaml.bukkitapi.file.FileConfiguration;
import net.craftminecraft.bungee.bungeeyaml.bukkitapi.file.YamlConfiguration;
import net.neferett.linaris.GameServers;

public class ConfigUtils {

	private File 				file;
	private FileConfiguration 	config;
	
	public ConfigUtils(boolean def){
		file = new File("./plugins/BungeeAPI/config.yml");
		config = GameServers.get().getConfig();
	}
	
	public ConfigUtils(String name){
		file = new File("./plugins/BungeeAPI/" + name);
		config = YamlConfiguration.loadConfiguration(file);
		save();
	}
	
	public void save (){ try { this.config.save(file); } catch (IOException e) { e.printStackTrace(); } }
	
	public FileConfiguration getConfig() {
		return config;
	}
	
	public ConfigUtils getThis(){
		return this;
	}
	
}
