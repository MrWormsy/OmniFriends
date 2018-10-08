package fr.mrwormsy.omnivexel.omnifriends.events;

import java.sql.Timestamp;
import java.time.Instant;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.mrwormsy.omnivexel.omnifriends.FriendsUtils;
import fr.mrwormsy.omnivexel.omnifriends.Omnifriends;
import fr.mrwormsy.omnivexel.omnifriends.lang.Lang;

public class Events implements Listener {

	@EventHandler //Triggered when the player joins the server
	public static void onJoin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		
		//First we check if the player has already been registered
		if (!FriendsUtils.isPlayerAlreadyRegistered(player.getName())) {
			
			//We create the player's database
			FriendsUtils.createPlayerDataBase(player);
		}
		
		//As the player has already been registered we can notify his friends that he is online...
		else {
			for (int id : FriendsUtils.getFriendsListIds(FriendsUtils.getPlayerId(player.getName()))) {
				
				//Check if the player with this id is online, and if so we notify it
				Player playerOfId = Omnifriends.getPlugin().getServer().getPlayer(FriendsUtils.getPlayerRealNameById(id));
				if (playerOfId != null) {
					playerOfId.sendMessage(Lang.FRIEND_JOINED.toString().replaceAll("<name>", player.getName()));
				}
			}
		}
	}
	
	@EventHandler
	public static void onQuit(PlayerQuitEvent event) {
		
		Player player = event.getPlayer();
		
		//We set the player last seen to now
		FriendsUtils.setLastSeen(FriendsUtils.getPlayerId(player.getName()), Timestamp.from(Instant.now()));
		
		//We loop the player's friends to notify them that he is leaving
		for (int id : FriendsUtils.getFriendsListIds(FriendsUtils.getPlayerId(player.getName()))) {
			
			//Check if the player with this id is online, and if so we notify it
			Player playerOfId = Omnifriends.getPlugin().getServer().getPlayer(FriendsUtils.getPlayerRealNameById(id));
			if (playerOfId != null) {
				playerOfId.sendMessage(Lang.FRIEND_QUIT.toString().replaceAll("<name>", player.getName()));
			}
		}
	}
	
	
	//TODO When the player type @[player] this send a message to his friend with the nickname (only use of the nickname, with the join/quit ?)
	
}
