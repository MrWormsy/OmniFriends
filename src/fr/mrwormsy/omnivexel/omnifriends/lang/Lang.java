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
	FRIEND_SINCE("friend_since", "&eFriendaversary &7<month>/<day>/<year>"),
	FRIEND_JOINED("friend_joined", "&aYour friend &e<name> &ahas joined the server !"),
	FRIEND_QUIT("friend_quit", "&aYour friend &e<name> &ahas quit the server !"),
	CONNECTED("connected", "&aConnected"),
	DISCONNECTED("disconnected", "&cDisconnected"),
	LAST_SEEN("last_seen", " &7(Last seen <lastseen> ago)"),
	DAYS("days","&7<days> Days"),
	HOURS("hours", "&7<hours> Hours"),
	MINUTES("minutes", "&7<minutes> Minutes"),
	SUBCOMMAND_NOT_FOUND("subcommand_not_found", "&cSorry subcommand not found, try &e/friend help&c."),
	PLAYER_NOT_FOUND("player_not_found", "&cSorry player not found..."),
	PLAYER_NOT_ONLINE("player_not_online", "&cSorry player not online..."),
	PLAYER_NOT_FRIEND("player_not_friend", "&cSorry &e<friend> &cis not your friend..."),
	PLAYER_ALREADY_FRIEND("player_already_friend", "&aYou are already friend with &e<friend>"),
	PLAYER_DENIED_FRIEND_REQUEST("player_denied_friend_request", "&e<friend> &cdenied your friend request..."),
	HELP("help" ,"...")
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