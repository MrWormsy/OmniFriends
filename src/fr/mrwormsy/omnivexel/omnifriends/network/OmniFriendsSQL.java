package fr.mrwormsy.omnivexel.omnifriends.network;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.mysql.jdbc.Connection;

import fr.mrwormsy.omnivexel.omnifriends.Omnifriends;

public class OmniFriendsSQL {
	
	//TODO SAY THAT THE FIRST TIME THEY WILL GET AN ERROR BECAUSE THE SQL's SETTINGS ARE NOT SET
	
    private static final String DATABASE_DRIVER = "com.mysql.jdbc.Driver";
    //private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/OmniFriends";
    //private static final String USERNAME = "root";
    //private static final String PASSWORD = "";
    private static final String MAX_POOL = "250";

    private static Connection connection;
    private static Properties properties;

    // create properties
    private static Properties getProperties() {
        if (properties == null) {
            properties = new Properties();
            properties.setProperty("user", Omnifriends.getPlugin().getConfig().getString("DataBaseUsername"));
            properties.setProperty("password", Omnifriends.getPlugin().getConfig().getString("DataBasePassword"));
            properties.setProperty("MaxPooledStatements", MAX_POOL);
        }
        return properties;
    }

    // connect database
    static public Connection connect() {
        if (connection == null) {
            try {
                Class.forName(DATABASE_DRIVER);
                //connection = (Connection) DriverManager.getConnection(DATABASE_URL, getProperties());
                connection = (Connection) DriverManager.getConnection("jdbc:mysql://" + Omnifriends.getPlugin().getConfig().getString("DataBaseURL"), getProperties());
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    // disconnect database
    static public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static Connection getConnection() {
		return connection;
	}
	
}
