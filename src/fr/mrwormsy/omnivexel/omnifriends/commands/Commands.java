package fr.mrwormsy.omnivexel.omnifriends.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.mrwormsy.omnivexel.omnifriends.FriendsUtils;
import fr.mrwormsy.omnivexel.omnifriends.Omnifriends;
import fr.mrwormsy.omnivexel.omnifriends.lang.Lang;

public class Commands implements CommandExecutor {

	//TODO REQUESTS
	//TODO PERMISSION ACCORDING TO THE NUMBER OF FRIENDS THEY CAN HAVE
	//TODO MAKE A DENY THING TO TPA AND FRIEND REQUEST
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		//The friend command
		if (cmd.getName().equalsIgnoreCase("friend") || cmd.getName().equalsIgnoreCase("f") || cmd.getName().equalsIgnoreCase("friends")) {
			
			if (sender instanceof Player) {
				Player player = (Player) sender;
				
				// /friend or /friend list --> shows the friend list gui
				if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("list"))) {
					FriendsUtils.openFriendsGUI(player, FriendsUtils.getPlayerId(player.getName()), 1);
					return true;
				}
				
				// /friend add [friend] --> send a friend request to the player "friend" (note: a friend request last only 30 seconds...)
				else if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
					Player friend = Omnifriends.getPlugin().getServer().getPlayer(args[1]);
					if (friend != null) {
						
						
						//FriendsUtils.sendFriendRequest(player, friend);
						
						
					} else {
						//Player not found
						player.sendMessage(Lang.PLAYER_NOT_FOUND.toString());
					}
				}
				
				// /friend accept [friend] --> accept the friend request of the player "friend" and add the connection to the database
				else if (args.length == 2 && args[0].equalsIgnoreCase("accept")) {
					
					//Check if the player is online
					Player friend = Omnifriends.getPlugin().getServer().getPlayer(args[1]);
					if (friend != null) {
						
						//Check if those player are not already friends
						if (!FriendsUtils.areTheyFriends(FriendsUtils.getPlayerId(player.getName()), FriendsUtils.getPlayerId(args[1]))) {
							
							//We add a friendship between those two
							//FriendsUtils.acceptFriendRequestFrom(player, friend);
							
						} else {
							//If yes, we say that they are already friends
							player.sendMessage(Lang.PLAYER_ALREADY_FRIEND.toString().replaceAll("<friend>", args[1]));
						}						
						
					} else {
						//Player not found
						player.sendMessage(Lang.PLAYER_NOT_FOUND.toString());
					}
				}
				
				// /friend deny [friend] --> deny a friend request (tell the other player he doesn't want to be friend with this guy...)
				else if (args.length == 2 && args[0].equalsIgnoreCase("deny")) {
					Player friend = Omnifriends.getPlugin().getServer().getPlayer(args[1]);
					if (friend != null) {
						
						//We send a message to the friend to tell him the player is not willing to be friend with him
						friend.sendMessage(Lang.PLAYER_DENIED_FRIEND_REQUEST.toString().replaceAll("<friends>", player.getName()));
				
					} else {
						//Player not found
						player.sendMessage(Lang.PLAYER_NOT_FOUND.toString());
					}
				}
				
				// /friend remove [friend] --> remove the player "friend" from the player's friend list (can be done through gui)
				else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
					
					//Check if the player is friend with the second player
					if (FriendsUtils.areTheyFriends(FriendsUtils.getPlayerId(player.getName()), FriendsUtils.getPlayerId(args[1]))) {
						
						//We remove the friendship between the player and his ancient friend
						//FriendsUtils.removeFriendship(FriendsUtils.getPlayerId(player.getName()), FriendsUtils.getPlayerId(args[1]));
						
					} else {
						//If not, we say that they are not friends
						player.sendMessage(Lang.PLAYER_NOT_FRIEND.toString().replaceAll("<friend>", args[1]));
					}
				}
				
				// /friend nickname [friend] [nickname] --> set the friend's nickname showed to the player (idf the nickname is empty we suppose the player want the real name)
				else if ((args.length == 2 && args[0].equalsIgnoreCase("nickname")) || (args.length == 3 && args[0].equalsIgnoreCase("nickname"))) {
					
					//Check if the player is friend with the second player
					if (FriendsUtils.areTheyFriends(FriendsUtils.getPlayerId(player.getName()), FriendsUtils.getPlayerId(args[1]))) {
						
						//If the length of args is 2 we suppose the player wants to remove the friend's nickname
						if (args.length == 2) {
							//We remove the nickname of the friend
							//FriendsUtils.addNicknameToFriend(FriendsUtils.getPlayerId(player.getName()), FriendsUtils.getPlayerId(args[1]), null); //TODO CHECK THE NULL !!!!
						}
						
						//Otherwise we put the nickname to args[2]
						else {
							//We add a nickname to the friend of the player (only for the player)
							//FriendsUtils.addNicknameToFriend(FriendsUtils.getPlayerId(player.getName()), FriendsUtils.getPlayerId(args[1]), args[2]);
						}
						
					} else {
						//If not, we say that they are not friends
						player.sendMessage(Lang.PLAYER_NOT_FRIEND.toString().replaceAll("<friend>", args[1]));
					}
				}
				
				// /friend tpa [friend] <deny/accept> --> teleport the player to his friend if this latter accpeted the request
				else if ((args.length == 2 && args[0].equalsIgnoreCase("tpa")) || (args.length == 3 && args[0].equalsIgnoreCase("tpa"))) {
					
					//Check if the player is friend with the second player
					if (FriendsUtils.areTheyFriends(FriendsUtils.getPlayerId(player.getName()), FriendsUtils.getPlayerId(args[1]))) {
						
						//Check if the player is online
						Player friend = Omnifriends.getPlugin().getServer().getPlayer(args[1]);
						if (friend != null) {
							
							//If there are only two args, that means the player wants to tp
							if (args.length == 2) {
								
								//We send a tp request to the friend
								//FriendsUtils.sendTpRequest(player, friend);
								
							} else {
								
								//The player has the choice to deny or accept the tp
								if (args[2].equalsIgnoreCase("accept")) {
									
									
									
								} else if (args[2].equalsIgnoreCase("deny")) {
									
									
								
								} else {
									player.sendMessage(Lang.SUBCOMMAND_NOT_FOUND.toString());
								}
								
							}
						} 
						
						else {
							//Player not online
							player.sendMessage(Lang.PLAYER_NOT_ONLINE.toString());
						}
						
					} else {
						//If not, we say that they are not friends
						player.sendMessage(Lang.PLAYER_NOT_FRIEND.toString().replaceAll("<friend>", args[1]));
					}
				}
				
				// /friend help
				else if (args[0].equalsIgnoreCase("help")) {
					
					//We send the help to the player
					for (String str : Lang.HELP.toString().split("\n")) {
						player.sendMessage(str);
					}
					
				}
				
				//That means the player typed a mistake
				else {
					player.sendMessage(Lang.SUBCOMMAND_NOT_FOUND.toString());
				}
				
				return true;
			} else return false;
			
			
		}
		return false; 
	}
	
}
