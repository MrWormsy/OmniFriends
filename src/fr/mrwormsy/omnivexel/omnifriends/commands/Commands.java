package fr.mrwormsy.omnivexel.omnifriends.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.mrwormsy.omnivexel.omnifriends.FriendsUtils;

public class Commands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		//The friend command
		if (cmd.getName().equalsIgnoreCase("friend") || cmd.getName().equalsIgnoreCase("f") || cmd.getName().equalsIgnoreCase("friends")) {
			
			if (sender instanceof Player) {
				Player player = (Player) sender;
				
				// /friend or /friend list --> shows the friend list gui
				if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("list"))) {
					FriendsUtils.openFriendsGUI(player, FriendsUtils.getPlayerId(player.getName()), 1);				
				}
				
				// /friend add [friend] --> send a friend request to the player "friend" (note: a friend request last only 30 seconds...)
				
				// /friend accept [friend] --> accept the friend request of the player "friend" and add the connection to the database
				
				// /friend deny [friend] --> deny a friend request (tell the other player he doesn't want to be friend with this guy...)
				
				// /friend remove [friend] --> remove the player "friend" from the player's friend list (can be done through gui)
				
				return true;
			} else return false;
			
			
		}
		return false; 
	}
	
}
