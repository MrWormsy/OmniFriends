package fr.mrwormsy.omnivexel.omnifriends.lang;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
 
public enum Lang {
	
	//Friends
	FRIENDS_INVENTORY_NAME("friends_inventory_name", "&aFriends GUI"),
	PREVIOUS_PAGE("previous_page", "&ePrevious page"),
	NEXT_PAGE("next_page", "&eNext page"),
	RETURN_ARROW("return_arrow", "&eReturn to your friend list"),
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
	HELP("help" ,"&e-&6*&e-&6*&e-&6*&e-&6*&e-&6*&e-&6*&e-&6*&e-&6*&e-&6*&e- &6&lOmniFriends &eHelp &e-&6*&e-&6*&e-&6*&e-&6*&e-&6*&e-&6*&e-&6*&e-&6*&e-&6*&e-\n" + 
				"&6/friend list <player> &eshow the friend list of a player\n" + 
				"&6/friend add [player] &esend a friend request to the player\n" + 
				"&6/friend accept [player] &eaccept the friend request sent by the player\n" + 
				"&6/friend deny [player] &edeny the friend request sent by the player\n" + 
				"&6/friend remove [player] &eremove the player from your friend list\n" + 
				"&6/friend nickname [player] <nickname> &egive a nickname to a friend (remove it if no nickname)\n" + 
				"&e-&6*&e-&6*&e-&6*&e-&6*&e-&6*&e-&6*&e-&6*&e-&6*&e-&6*&e- &6&lOmniFriends &eHelp &e-&6*&e-&6*&e-&6*&e-&6*&e-&6*&e-&6*&e-&6*&e-&6*&e-&6*&e-"),
	CANNOT_WITH_ITSSELF("cannot_with_itsself" ,"&cYou cannot do friend's stuffs with yourself..."),
	SEND_FRIEND_REQUEST("send_friend_request", "&aYou sent a friend request to &e<player>"),
	RECEIVE_FRIEND_REQUEST("receive_friend_request", "&e<player> &awants to be friend with you, type &e/friend &aaccept&7/&cdeny &e<player>"),
	FRIEND_REQUEST_HAS_EXPIRED("friend_request_has_expired", "&cYour friend request from &e<player> &chas expired"),
	ALREADY_SENT_FRIEND_REQUEST("already_sent_friend_request", "&cYou have already sent a friend request to &e<player>"),
	FRIEND_RESQUEST_NOT_RECEIVED("friend_request_not_received", "&cYou did not receive any friend request from &e<player>"),
	FRIEND_REQUEST_ACCEPTED("friend_request_accepted", "&aCongratulations, you are now friend with &e<player>"),
	REMOVE_FRIENDSHIP("remove_friendship" ,"&aYou are no longer friend with &e<player>"),
	NEW_NICKNAME("new_nickname" ,"&aYour friend will now be known as &r<nickname>"),
	REMOVE_NICKNAME("remove_nickname" ,"&aYou will now know your friend by his true name"),
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