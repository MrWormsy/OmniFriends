package fr.mrwormsy.omnivexel.omnifriends;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import fr.mrwormsy.omnivexel.omnifriends.commands.Commands;
import fr.mrwormsy.omnivexel.omnifriends.lang.Lang;
import fr.mrwormsy.omnivexel.omnifriends.network.OmniFriendsSQL;


public class Omnifriends extends JavaPlugin {

	public static YamlConfiguration LANG;
	public static File LANG_FILE;
	
	private static Plugin plugin;
	
    @Override
    public void onEnable() {
    	//Config file
    	// Save a copy of the default config.yml if one is not there
        this.saveDefaultConfig();
    	
    	//We set a value to the plugin
    	plugin = this;
    	
    	//Load lang file
    	loadLang();
    	
    	//Connect to MySQL server
    	OmniFriendsSQL.connect();
    	
    	//Load the OmniFriends Utilities
    	FriendsUtils.load();
    	
        //Register commands
        this.getCommand("friend").setExecutor(new Commands());
    	
    }
    
    @Override
    public void onDisable() {
    	plugin = null;
    	
    	OmniFriendsSQL.disconnect();
    }
    
    
    @SuppressWarnings("deprecation")
	public YamlConfiguration loadLang() {
        File lang = new File(getDataFolder(), "lang.yml");
        if (!lang.exists()) {
            try {
                getDataFolder().mkdir();
                lang.createNewFile();
                InputStream defConfigStream = this.getResource("lang.yml");
                if (defConfigStream != null) {
                    YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                    defConfig.save(lang);
                    Lang.setFile(defConfig);
                    return defConfig;
                }
            } catch(IOException e) {
                e.printStackTrace(); // So they notice
                Bukkit.getLogger().severe("[PluginName] Couldn't create language file.");
                Bukkit.getLogger().severe("[PluginName] This is a fatal error. Now disabling");
                this.setEnabled(false); // Without it loaded, we can't send them messages
            }
        }
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
        for(Lang item:Lang.values()) {
            if (conf.getString(item.getPath()) == null) {
                conf.set(item.getPath(), item.getDefault());
            }
        }
        Lang.setFile(conf);
        Omnifriends.LANG = conf;
        Omnifriends.LANG_FILE = lang;
        try {
            conf.save(getLangFile());
        } catch(IOException e) {
        	Bukkit.getLogger().log(Level.WARNING, "PluginName: Failed to save lang.yml.");
        	Bukkit.getLogger().log(Level.WARNING, "PluginName: Report this stack trace to <your name>.");
            e.printStackTrace();
        }
		return conf;
    }
    
    public YamlConfiguration getLang() {
        return LANG;
    }
     
    public File getLangFile() {
        return LANG_FILE;
    }


	public static Plugin getPlugin() {
		return plugin;
	}
	
}
