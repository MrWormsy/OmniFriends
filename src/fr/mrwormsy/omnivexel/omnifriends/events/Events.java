package fr.mrwormsy.omnivexel.omnifriends.events;



import java.sql.Timestamp;
import java.time.Instant;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

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
					
					//Check if the friend has a nickname given by the player
					String nickname = FriendsUtils.getFriendNickname(FriendsUtils.getPlayerId(playerOfId.getName()), FriendsUtils.getPlayerId(player.getName()));
					if (nickname != null) {
						playerOfId.sendMessage(Lang.FRIEND_JOINED.toString().replaceAll("<name>", player.getName() + ChatColor.GRAY + " (" + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', nickname) + ChatColor.GRAY + ")"));
					} else {
						playerOfId.sendMessage(Lang.FRIEND_JOINED.toString().replaceAll("<name>", player.getName()));
					}
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
				
				//Check if the friend has a nickname given by the player
				String nickname = FriendsUtils.getFriendNickname(FriendsUtils.getPlayerId(playerOfId.getName()), FriendsUtils.getPlayerId(player.getName()));
				if (nickname != null) {
					playerOfId.sendMessage(Lang.FRIEND_QUIT.toString().replaceAll("<name>", player.getName() + ChatColor.GRAY + " (" + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', nickname) + ChatColor.GRAY + ")"));
				} else {
					playerOfId.sendMessage(Lang.FRIEND_QUIT.toString().replaceAll("<name>", player.getName()));
				}
			}
		}
	}
	
	
	//TODO When the player type @[player] this send a message to his friend with the nickname and with color codes (only use of the nickname, with the join/quit ?). Make it interactive (if we click on the message it puts @[player] back in the chat)
	@EventHandler
	public void playerChat(AsyncPlayerChatEvent event) {
	    
		String message = event.getMessage();
		
		//We first check if the message begins with @[player]
		if (message.charAt(0) == '@') {
			Player player = event.getPlayer();
			event.setCancelled(true);
			
			//Then we get the possible name
			String name = ((message.split(" "))[0]).substring(1, (message.split(" "))[0].length());
			message = message.substring(name.length() + 1, message.length());
			
			//Then we check if this player is online
			Player friend = Omnifriends.getPlugin().getServer().getPlayer(name);
			if (friend != null) {
				
				//Now we check if the player is his friend
				if (FriendsUtils.areTheyFriends(FriendsUtils.getPlayerId(player.getName()), FriendsUtils.getPlayerId(friend.getName()))) {
					
					//If the nickname is not null we add it after the name
					String nickname = FriendsUtils.getFriendNickname(FriendsUtils.getPlayerId(friend.getName()), FriendsUtils.getPlayerId(player.getName()));
					if (nickname != null) {
						friend.sendMessage(ChatColor.GREEN + "@" + player.getName() + ChatColor.GRAY + " (" + ChatColor.translateAlternateColorCodes('&', nickname) + ChatColor.GRAY + ") " + ChatColor.YELLOW + " >> " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', message));
					} else {
						friend.sendMessage(ChatColor.GREEN + "@" + player.getName() + ChatColor.YELLOW + "  >> " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', message));
					}
					
					//If the nickname is not null we add it after the name
					nickname = FriendsUtils.getFriendNickname(FriendsUtils.getPlayerId(player.getName()), FriendsUtils.getPlayerId(friend.getName()));
					if (nickname != null) {
						player.sendMessage(ChatColor.GREEN + "@" + friend.getName() + ChatColor.GRAY + " (" + ChatColor.translateAlternateColorCodes('&', nickname) + ChatColor.GRAY + ") " + ChatColor.YELLOW + " << " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', message));
					} else {
						player.sendMessage(ChatColor.GREEN + "@" + friend.getName() + ChatColor.YELLOW + "  << " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', message));
					}
					
				} else {
					player.sendMessage(Lang.PLAYER_NOT_FRIEND.toString().replaceAll("<friend>", friend.getName()));
				}
				
			} else {
				//We tell the player the friend player is not online
				player.sendMessage(Lang.PLAYER_NOT_ONLINE.toString());
			}	
		}
	}
	
	//When the player change his current inventory
	@EventHandler
	public void onInventoryChange(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) {
			return;
		}
			
		Player player = (Player) event.getWhoClicked();
		Inventory inventory = event.getInventory();
		
		//If the player is clicking his own inventory and this inventory is a friend inventory
		if (inventory.getItem(((FriendsUtils.getRowsForFriendsToShow() + 1) * 9) - 9).getType() == Material.SKULL_ITEM && inventory.getItem(((FriendsUtils.getRowsForFriendsToShow() + 1) * 9) - 8).getType() == Material.SKULL_ITEM && inventory.getItem(((FriendsUtils.getRowsForFriendsToShow() + 1) * 9) - 2).getType() == Material.SKULL_ITEM) {
			event.setCancelled(true);
			
			//If we are not clicking in the inventory we stop the event
			if (!(event.getRawSlot() >= 0 && event.getRawSlot() <= 54)) {
				return;
			}
			
			//Get the current page
			int currentPage = Integer.valueOf((inventory.getName().split("-")[1]).replaceAll("\\(", "").replaceAll("\\)", "").replaceAll(" ", ""));
					
			//(Previous page) and the current page is not 0, we let the player view his previous page
			if (event.getRawSlot() == ((FriendsUtils.getRowsForFriendsToShow() + 1) * 9) - 8) {
				//If the page is not 1 or lower
				if ((currentPage - 1) >= 1) {
					FriendsUtils.openFriendsGUI(player, FriendsUtils.getPlayerId(inventory.getItem(((FriendsUtils.getRowsForFriendsToShow() + 1) * 9) - 5).getItemMeta().getDisplayName().replaceAll("" + ChatColor.GREEN, "")), currentPage - 1);
				}
			}
					
			//(Next page) and the current page is not the last one, we let the player view his next page
			else if (event.getRawSlot() == ((FriendsUtils.getRowsForFriendsToShow() + 1) * 9) - 2) {
				//If the page the player wants to reach can be opened (if the friend list is big enough)
				
				//Maximum number of pages that can be shown
				double highestPageNumber = Math.ceil(FriendsUtils.getFriendsListIds(FriendsUtils.getPlayerId(inventory.getItem(((FriendsUtils.getRowsForFriendsToShow() + 1) * 9) - 5).getItemMeta().getDisplayName().replaceAll("" + ChatColor.GREEN, ""))).size() / ((double) 9 * FriendsUtils.getRowsForFriendsToShow()));
				if (((currentPage + 1)) <= highestPageNumber) {
					FriendsUtils.openFriendsGUI(player, FriendsUtils.getPlayerId(inventory.getItem(((FriendsUtils.getRowsForFriendsToShow() + 1) * 9) - 5).getItemMeta().getDisplayName().replaceAll("" + ChatColor.GREEN, "")), currentPage + 1);
				}
			}
					
			//We go back to the player's friend list
			else if (event.getRawSlot() == ((FriendsUtils.getRowsForFriendsToShow() + 1) * 9) - 9) {
				FriendsUtils.openFriendsGUI(player, FriendsUtils.getPlayerId(player.getName()), 1);
			}
		}
	}
}
