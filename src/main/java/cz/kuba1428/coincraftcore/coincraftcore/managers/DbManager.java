package cz.kuba1428.coincraftcore.coincraftcore.managers;

import cz.kuba1428.coincraftcore.coincraftcore.utils.GlobalVars;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;

public class DbManager {
    static FileConfiguration config = GlobalVars.config;
    static String user = config.getString("database.user");
    static String password = config.getString("database.password");
    static String url = "jdbc:mysql://" + config.getString("database.host") + ":" + config.getString("database.port") + "/" + config.getString("database.database");

    static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static void ExecuteUpdate(String s){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(s);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static ResultSet ExecuteQuery(String s){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(s);
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
