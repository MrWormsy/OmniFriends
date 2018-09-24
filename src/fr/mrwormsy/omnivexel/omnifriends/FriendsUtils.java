package fr.mrwormsy.omnivexel.omnifriends;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mysql.jdbc.Statement;

import fr.mrwormsy.omnivexel.omnifriends.lang.Lang;
import fr.mrwormsy.omnivexel.omnifriends.network.OmniFriendsSQL;
import fr.mrwormsy.omnivexel.utils.Utils;
import fr.mrwormsy.omnivexel.utils.Utils.HeadFromURL;

public class FriendsUtils {

	private static int rowsForFriendsToShow;
	
	//The load method run when the server starts...
	public static void load() {
		
		//TABLES PART
				
		//We try to create this table (to register the player's informations)
		//CREATE TABLE `omnifriends`.`playerdatabase` ( `idPlayer` INT NOT NULL AUTO_INCREMENT , `playerName` TINYTEXT NOT NULL , `idCustomHead` INT NOT NULL , `dateJoined` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP , PRIMARY KEY (`idPlayer`)) ENGINE = MyISAM;
		try {
			Statement stmt;
			stmt = (Statement) OmniFriendsSQL.getConnection().createStatement();
			
			//We add the player database table where it creates a brand new "player id card" for the OmniFriends plugin
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `playerdatabase` ( `idPlayer` INT NOT NULL AUTO_INCREMENT , `playerName` TINYTEXT NOT NULL , `idCustomHead` INT NOT NULL , `dateJoined` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP , PRIMARY KEY (`idPlayer`)) ENGINE = MyISAM;");
			
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		//We try to create this table (to store the friends' connections)
		//CREATE TABLE IF NOT EXISTS `friendlist` (`idFriend1` int(11) NOT NULL, `idFriend2` int(11) NOT NULL, `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`idFriend1`,`idFriend2`)) ENGINE=MyISAM;
		try {
			Statement stmt;
			stmt = (Statement) OmniFriendsSQL.getConnection().createStatement();
			
			//We add the friend list table where it connects two player with a friendaversary date
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `friendlist` (`idFriend1` int(11) NOT NULL, `idFriend2` int(11) NOT NULL, `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`idFriend1`,`idFriend2`)) ENGINE=MyISAM;");
			
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//TODO Custom head table	
		
		//We set the number of rows for the GUI friendInventory
		rowsForFriendsToShow = Omnifriends.getPlugin().getConfig().getInt("RowsForFriendsToShow");
	}

	//Check if the player is already registered in the player database
	public static boolean isPlayerAlreadyRegistered(String player) {
		try {
			Statement stmt;
			stmt = (Statement) OmniFriendsSQL.getConnection().createStatement();
			ResultSet result = stmt.executeQuery("SELECT idPlayer FROM playerdatabase WHERE playerName = '" + player.toLowerCase() + "'");
			
			//If result.next() returns true, the player is already registered
			if (result.next()) {
				result.close();
				stmt.close();
				return true;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//Otherwise he is not
		return false;
	}
	
	//Get the id of an offline player (player.getName())
	public static int getPlayerId(String player) {
		try {
			Statement stmt;
			stmt = (Statement) OmniFriendsSQL.getConnection().createStatement();
			ResultSet result = stmt.executeQuery("SELECT idPlayer FROM playerdatabase WHERE playerName = '" + player.toLowerCase() + "'");
			
			//We return the id
			if (result.next()) {
				return result.getInt("idPlayer");
			}
			
			result.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//If the id is 0, there is a problem :o
		return 0;
	}
	
	//Add the player to the player database
	public static void createPlayerDataBase(Player player) {
		try {
			Statement stmt;
			stmt = (Statement) OmniFriendsSQL.getConnection().createStatement();
			stmt.execute("INSERT INTO `playerdatabase`(`playerName`, `idCustomHead`) VALUES ('" + player.getName().toLowerCase() + "','0')");
			
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	//Return the friends list of a certain player
	public static ArrayList<Integer> getFriendsListIds(int playerID) {
		
		//We first create the empty list to put all the player's friends into it (IDs)
		ArrayList<Integer> friendsListIds = new ArrayList<Integer>();
		
		try {
			Statement stmt;
			stmt = (Statement) OmniFriendsSQL.getConnection().createStatement();
			
			
			//First loop for idFriend1
			ResultSet result = stmt.executeQuery("SELECT `idFriend1` FROM `FriendList` WHERE idFriend2 = '" + playerID + "'");
			
			//While there is still friends...
			while (result.next()) {
				//Add the friend to the list
				friendsListIds.add(result.getInt("idFriend1"));
			}
			result.close();
			
			//Second loop for idFriend2
			result = stmt.executeQuery("SELECT `idFriend2` FROM `FriendList` WHERE idFriend1 = '" + playerID + "'");
			
			//While there is still friends...
			while (result.next()) {
				//Add the friend to the list
				friendsListIds.add(result.getInt("idFriend2"));
			}
			result.close();
			
			
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//We return the friend list
		return friendsListIds;
	}
	
	//Return true if the players are already friends, false in the other case
	public static boolean areTheyFriends(int player1ID, int player2ID) {
		
		try {
			Statement stmt;
			stmt = (Statement) OmniFriendsSQL.getConnection().createStatement();
			
			//We want to know if we can gather the id of the first player when these two are friends
			ResultSet result = stmt.executeQuery("SELECT `idFriend1` FROM `FriendList` WHERE idFriend1 = '" + player1ID + "' AND idFriend2 = '" + player2ID + "'");
			
			//If this returns true, we know that they are already friends
			if (result.next()) {
				return true;
			}
			
			result.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//If not, they are not
		return false;
	}
	
	//Add two friends (that are not initialy friends)
	public static void addFriendToFriendsList(int player1ID, int player2ID) {
		
		try {
			Statement stmt;
			stmt = (Statement) OmniFriendsSQL.getConnection().createStatement();
			
			//We add a friendship link between those two players (the date is automatic)
			stmt.executeUpdate("INSERT INTO `FriendList`(`idFriend1`, `idFriend2`) VALUES ('" + player1ID + "', '" + player1ID + "')");
			
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//Get the friendaversary date of two players
	public static Timestamp getFriendaversaryDate(int player1ID, int player2ID) {
		
		try {
			Statement stmt;
			stmt = (Statement) OmniFriendsSQL.getConnection().createStatement();
			
			//We want to know the friendaversary date of those two players...
			ResultSet result = stmt.executeQuery("SELECT `date` FROM `FriendList` WHERE idFriend1 = '" + player1ID + "' AND idFriend2 = '" + player2ID + "'");
			
			//If result.next() returns true, we return the friendaversary date
			if (result.next()) {
				return result.getTimestamp("date");
			}
			
			result.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//If we reach here there is a problem :'(
		return null;
	}
	
	//Open the id's friend list to player, we suppose that the page exist already (that is to say the player has more than ((1 - page) * friendsToShow) friends)
	public static void openFriendsGUI(Player player, int idPlayerFriendList, int page) {
		
		//The GUI
		Inventory friendGUI = Bukkit.createInventory(null, 9 * (rowsForFriendsToShow + 1), Lang.FRIENDS_INVENTORY_NAME.toString());
		
		//The friend list
		ArrayList<Integer> friendsList = FriendsUtils.getFriendsListIds(idPlayerFriendList);
		
		int forID;
		
		int indexID = (1 - page) * (rowsForFriendsToShow * 9);
		
		//We loop all the first rowsForFriendsToShow to put the friends' ids
		for (forID = 0; forID < (rowsForFriendsToShow * 9); forID++) {
					
			//If we have reach the end of the friend list we can escape the loop...
			if (indexID >= friendsList.size()) {
				break;
			}
			
			//Then we put one of the player friend's informations
			//friendGUI.setItem(forID, getFriendInfos(idPlayerFriendList, PlayerData.getPlayerId(player.getName())));
			
			indexID++;
			
		}
		
		//And then put the last row to put all the "buttons"...
		
		ItemMeta meta;
		
		//The return button to OInventory
		ItemStack returnToOInventory = Utils.getSkullFromURL(HeadFromURL.BACKWARD);
		meta = returnToOInventory.getItemMeta();
		meta.setDisplayName(Lang.RETURN_ARROW.toString());
		returnToOInventory.setItemMeta(meta);
		friendGUI.setItem(9 * (rowsForFriendsToShow + 1) - 9, returnToOInventory);
		
		//The Previous page
		ItemStack leftArrow = Utils.getSkullFromURL(HeadFromURL.LEFT_ARROW);
		meta = leftArrow.getItemMeta();
		meta.setDisplayName(Lang.PREVIOUS_PAGE.toString());
		leftArrow.setItemMeta(meta);
		friendGUI.setItem(9 * (rowsForFriendsToShow + 1) - 8, leftArrow);
		
		//The Next page
		ItemStack rightArrow = Utils.getSkullFromURL(HeadFromURL.RIGHT_ARROW);
		meta = rightArrow.getItemMeta();
		meta.setDisplayName(Lang.NEXT_PAGE.toString());
		rightArrow.setItemMeta(meta);
		friendGUI.setItem(9 * (rowsForFriendsToShow + 1) - 2, rightArrow);
		
		player.openInventory(friendGUI);
	}

	//Return the skull of the player with his informations...
	@SuppressWarnings("unused")
	private static ItemStack getFriendInfos(int playerID, int viewerID) {
		
		//We see if the player has a custom head (returns the custom head id) and we will set this head as the player's head, or the custom head id == 0 and we put the default player head
		
		
		//Level
		
		//Registered since
		
		//Nb of friends
		
		//Clan
		
		//Friend since
		
		return null;
	}
	
}
