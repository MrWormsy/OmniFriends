package fr.mrwormsy.omnivexel.omnifriends;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mysql.jdbc.Statement;

import fr.mrwormsy.omnivexel.omnifriends.lang.Lang;
import fr.mrwormsy.omnivexel.omnifriends.network.OmniFriendsSQL;

public class FriendsUtils {

	//Config setting to know how many rows we show to the player when doing /friend
	private static int rowsForFriendsToShow;
	
	//Enum to get custom heads from URL
	public enum HeadFromURL {
		LEFT_ARROW("http://textures.minecraft.net/texture/86971dd881dbaf4fd6bcaa93614493c612f869641ed59d1c9363a3666a5fa6"), 
		RIGHT_ARROW("http://textures.minecraft.net/texture/f32ca66056b72863e98f7f32bd7d94c7a0d796af691c9ac3a9136331352288f9"), 
		BACKWARD("http://textures.minecraft.net/texture/74133f6ac3be2e2499a784efadcfffeb9ace025c3646ada67f3414e5ef3394");

		private String url;

		private HeadFromURL(String url) {
			this.url = url;
		}

		public String getText() {
			return this.url;
		}
	}
	
	// Get skull from URL
	public static ItemStack getSkullFromURL(HeadFromURL headFromURL) {
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		if (headFromURL.getText().isEmpty())
			return head;

		SkullMeta headMeta = (SkullMeta) head.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", headFromURL.getText()).getBytes());
		profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
		Field profileField = null;
		try {
			profileField = headMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(headMeta, profile);
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		head.setItemMeta(headMeta);
		return head;
	}
	
	// Get player's skull
	public static ItemStack getPlayerSkull(String player) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM);
        skull.setDurability((short)3);
        SkullMeta sm = (SkullMeta) skull.getItemMeta();
        sm.setOwner(player);
        skull.setItemMeta(sm);
        return skull;
	}
	
	//The load method run when the server starts...
	public static void load() {
		
		//TABLES PART
				
		//We try to create this table (to register the player's informations)
		//CREATE TABLE `omnifriends`.`playerdatabase` ( `idPlayer` INT NOT NULL AUTO_INCREMENT , `playerName` TINYTEXT NOT NULL , `idCustomHead` INT NOT NULL , `dateJoined` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP , PRIMARY KEY (`idPlayer`)) ENGINE = MyISAM;
		try {
			Statement stmt;
			stmt = (Statement) OmniFriendsSQL.getConnection().createStatement();
			
			//We add the player database table where it creates a brand new "player id card" for the OmniFriends plugin
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `playerdatabase` ( `idPlayer` INT NOT NULL AUTO_INCREMENT , `playerName` TINYTEXT NOT NULL, `realName` TINYTEXT NOT NULL , `idCustomHead` INT NOT NULL , `dateJoined` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, `lastSeen` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP , PRIMARY KEY (`idPlayer`)) ENGINE = MyISAM;");
			
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

	//Check if this this plugin must be run inside the Omnivexel Project or not
	public static boolean isPartOfOmnivexelProject() {
		if (Omnifriends.getPlugin().getServer().getPluginManager().getPlugin("Omnivexel") != null) {
			return true;
		} else {
			return false;
		}
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
	
	//Get the name of a player using its ID
	public static String getPlayerNameById(int idPlayer) {
		try {
			Statement stmt;
			stmt = (Statement) OmniFriendsSQL.getConnection().createStatement();
			ResultSet result = stmt.executeQuery("SELECT playerName FROM playerdatabase WHERE idPlayer = '" + idPlayer + "'");
				
			//We return the name
			if (result.next()) {
				return result.getString("playerName");
			}
				
			result.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
		return "";
	}
	
	//Get the real name of a player using its ID
	public static String getPlayerRealNameById(int idPlayer) {
		try {
			Statement stmt;
			stmt = (Statement) OmniFriendsSQL.getConnection().createStatement();
			ResultSet result = stmt.executeQuery("SELECT realName FROM playerdatabase WHERE idPlayer = '" + idPlayer + "'");
					
			//We return the real name
			if (result.next()) {
				return result.getString("realName");
			}
					
			result.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
				
		return "";
	}
	
	//Get the custom head id if the player got one (else return 0)
	public static int getPlayerCustomHeadId(int idPlayer) {
		try {
			Statement stmt;
			stmt = (Statement) OmniFriendsSQL.getConnection().createStatement();
			ResultSet result = stmt.executeQuery("SELECT idCustomHead FROM playerdatabase WHERE idPlayer = '" + idPlayer + "'");
				
			//We return the id
			if (result.next()) {
				return result.getInt("idCustomHead");
			}
				
			result.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
		//If the id is 0, there is a problem :o
		return 0;
		}
	
	//Get the last seen formated String of the player
	public static String getLastSeen(int idPlayer) {
			
		try {
			Statement stmt;
			stmt = (Statement) OmniFriendsSQL.getConnection().createStatement();
				
			//We want to know when the player joined the server
			ResultSet result = stmt.executeQuery("SELECT `lastSeen` FROM `playerdatabase` WHERE idPlayer = '" + idPlayer + "'");
				
			//If result.next() returns true, we return the last seen date formated...
			if (result.next()) {
				
				//String to return
				String toReturn = "";
				
				//Get the lastSeen time bewteen now and the last time the player has logged out (in seconds)
				long lastSeen = (System.currentTimeMillis() - result.getTimestamp("lastSeen").getTime());
				
				//Get the number of days
				long days = TimeUnit.MILLISECONDS.toDays(lastSeen);
				if (days != 0) {
					toReturn = toReturn.concat(Lang.DAYS.toString().replaceAll("<days>", String.valueOf(days))).concat(", ");
				}
				
				//Get the number of hours
				long hours = TimeUnit.MILLISECONDS.toHours(lastSeen - TimeUnit.DAYS.toMillis(days));
				if (hours != 0) {
					toReturn = toReturn.concat(Lang.HOURS.toString().replaceAll("<hours>", String.valueOf(hours))).concat(", ");
				}
				
				//Get the number of minutes
				long minutes = TimeUnit.MILLISECONDS.toMinutes(lastSeen - TimeUnit.DAYS.toMillis(days) - TimeUnit.HOURS.toMillis(hours));
				if (minutes != 0) {
					toReturn = toReturn.concat(Lang.MINUTES.toString().replaceAll("<minutes>", String.valueOf(minutes)));
				}
				
				if (toReturn.charAt(toReturn.length() - 2) == ',') {
					toReturn = toReturn.substring(0, toReturn.length() - 3);
				}
				
				return toReturn;
			}
				
			result.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
		//If we reach here there is a problem :'(
		return null;
	}
	
	//Set the Timestamp of the last seen date
	public static void setLastSeen(int idPlayer, Timestamp timestamp) {
		
		try {
			Statement stmt;
			stmt = (Statement) OmniFriendsSQL.getConnection().createStatement();
			
			//We update the lastSeen timestamp according to the player
			stmt.executeUpdate("UPDATE `playerdatabase` SET `lastSeen`='"+ timestamp.toString() +"' WHERE idPlayer = '" + idPlayer + "'");
			
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	//Get the join date of the player
	public static Timestamp getJoinDate(int idPlayer) {
				
		try {
			Statement stmt;
			stmt = (Statement) OmniFriendsSQL.getConnection().createStatement();
					
			//We want to know when the player joined the server
			ResultSet result = stmt.executeQuery("SELECT `dateJoined` FROM `playerdatabase` WHERE idPlayer = '" + idPlayer + "'");
					
			//If result.next() returns true, we return the join date
			if (result.next()) {
				return result.getTimestamp("dateJoined");
			}
					
			result.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
				
		//If we reach here there is a problem :'(
		return null;
	}
	
	//Add the player to the player database
	public static void createPlayerDataBase(Player player) {
		try {
			Statement stmt;
			stmt = (Statement) OmniFriendsSQL.getConnection().createStatement();
			stmt.execute("INSERT INTO `playerdatabase`(`playerName`, `realName`, `idCustomHead`) VALUES ('" + player.getName().toLowerCase() + "', '" + player.getName() + "','0')");
			
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
			ResultSet result = stmt.executeQuery("SELECT `idFriend1` FROM `FriendList` WHERE (idFriend1 = '" + player1ID + "' AND idFriend2 = '" + player2ID + "') OR (idFriend1 = '" + player2ID + "' AND idFriend2 = '" + player1ID + "')");
			
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
			ResultSet result = stmt.executeQuery("SELECT `date` FROM `FriendList` WHERE (idFriend1 = '" + player1ID + "' AND idFriend2 = '" + player2ID + "') OR (idFriend1 = '" + player2ID + "' AND idFriend2 = '" + player1ID + "')");
			
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
			friendGUI.setItem(forID, getFriendInfos(friendsList.get(indexID), idPlayerFriendList));
			
			indexID++;
			
		}
		
		//And then put the last row to put all the "buttons"...
		
		ItemMeta meta;
		
		//The return button to OInventory
		ItemStack returnToOInventory = getSkullFromURL(HeadFromURL.BACKWARD);
		meta = returnToOInventory.getItemMeta();
		meta.setDisplayName(Lang.RETURN_ARROW.toString());
		returnToOInventory.setItemMeta(meta);
		friendGUI.setItem(9 * (rowsForFriendsToShow + 1) - 9, returnToOInventory);
		
		//The Previous page
		ItemStack leftArrow = getSkullFromURL(HeadFromURL.LEFT_ARROW);
		meta = leftArrow.getItemMeta();
		meta.setDisplayName(Lang.PREVIOUS_PAGE.toString());
		leftArrow.setItemMeta(meta);
		friendGUI.setItem(9 * (rowsForFriendsToShow + 1) - 8, leftArrow);
		
		//The Next page
		ItemStack rightArrow = getSkullFromURL(HeadFromURL.RIGHT_ARROW);
		meta = rightArrow.getItemMeta();
		meta.setDisplayName(Lang.NEXT_PAGE.toString());
		rightArrow.setItemMeta(meta);
		friendGUI.setItem(9 * (rowsForFriendsToShow + 1) - 2, rightArrow);
		
		player.openInventory(friendGUI);
	}

	//Return the skull of the player with his informations...
	private static ItemStack getFriendInfos(int playerID, int playerFriendID) {
		
		//We see if the player has a custom head (returns the custom head id) and we will set this head as the player's head, or the custom head id == 0 and we put the default player head
		int customHeadId = getPlayerCustomHeadId(playerID);
		
		//The head item
		ItemStack head;
		
		//The lore
		ArrayList<String> lore = new ArrayList<String>();
		
		//If it is 0, we put the player's "basic" head
		if (customHeadId == 0) {
			head = getPlayerSkull(getPlayerNameById(playerID));
		} else {
			head = new ItemStack(Material.SKULL); //TODO Change this for the custom head
		}
		
		//The meta
		ItemMeta meta = head.getItemMeta();
		
		//Registered since
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(getJoinDate(playerID).getTime());
		lore.add(Lang.JOIN_DATE.toString().replaceAll("<month>", String.valueOf(cal.get(Calendar.MONTH))).replaceAll("<day>", String.valueOf(cal.get(Calendar.DAY_OF_MONTH))).replaceAll("<year>", String.valueOf(cal.get(Calendar.YEAR))));
		
		//New line
		lore.add(" ");
		
		//Nb of friends
		lore.add(Lang.NUMBER_FRIENDS.toString().replaceAll("<number>", String.valueOf(getFriendsListIds(playerID).size())));
		
		//New line
		lore.add(" ");
		
		//Friend since
		cal.setTimeInMillis(getFriendaversaryDate(playerID, playerFriendID).getTime());
		lore.add(Lang.FRIEND_SINCE.toString().replaceAll("<month>", String.valueOf(cal.get(Calendar.MONTH))).replaceAll("<day>", String.valueOf(cal.get(Calendar.DAY_OF_MONTH))).replaceAll("<year>", String.valueOf(cal.get(Calendar.YEAR))));
		
		//New line
		lore.add(" ");
		
		//Say if this player is connected
		if (Omnifriends.getPlugin().getServer().getPlayer(getPlayerRealNameById(playerID)) != null) {
			lore.add(Lang.CONNECTED.toString());
		} else {
			lore.add(Lang.DISCONNECTED.toString() + Lang.LAST_SEEN.toString().replaceAll("<lastseen>", getLastSeen(playerID))); //TODO Last seen in term of minutes, hours and days
		}
		
		//This part is reserved for the OmnivexelProject...
		if (isPartOfOmnivexelProject()) {
			//Level
			
			//Clan
			
			//Rank
			
			//Last OWorld
		}
				
		//We set the lore, display name and the meta
		meta.setLore(lore);
		meta.setDisplayName(ChatColor.GREEN + FriendsUtils.getPlayerRealNameById(playerID));
		
		//TODO IF THE FRIEND HAS A NICKNAME BY THE PLAYER WE PUT IT ON THE SKULL "name (nickname)"
		
		head.setItemMeta(meta);
		
		//We return the head
		return head;
	}
	
}
