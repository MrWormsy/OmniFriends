package fr.mrwormsy.omnivexel.omnifriends.lang;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
 
public enum Lang {
	
	//Friends
	FRIENDS_INVENTORY_NAME("friends_inventory_name", "&aFriends GUI"),
	PREVIOUS_PAGE("previous_page", "&ePrevious page"),
	NEXT_PAGE("next_page", "&eNext page"),
	RETURN_ARROW("return_arrow", "&eReturn to your inventory"),
	JOIN_DATE("join_date", "&eJoined the &7<month>/<day>/<year>"),
	NUMBER_FRIENDS("number_friends", "&7This player has &a<number> &7Friends"),
	FRIEND_SINCE("friend_since", "&eFriendaversary &7<month>/<day>/<year>")
	;
 
    private String path;
    private String def;
    private static YamlConfiguration LANG;

    Lang(String path, String start) {
        this.path = path;
        this.def = start;
    }
 
    public static void setFile(YamlConfiguration config) {
        LANG = config;
    }
 
    @Override
    public String toString() {
        return ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def));
    }
 
    public String getDefault() {
        return this.def;
    }
 
    public String getPath() {
        return this.path;
    }
}