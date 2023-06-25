package cz.kuba1428.coincraftcore.coincraftcore.misc;

import cz.kuba1428.coincraftcore.coincraftcore.CoincraftCore;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Database {
    CoincraftCore plugin = CoincraftCore.getPlugin(CoincraftCore.class);
    FileConfiguration config = plugin.getConfig();
    String user = config.getString("database.user");
    String password = config.getString("database.password");
    String url = "jdbc:mysql://" + config.getString("database.host") + ":" + config.getString("database.port") + "/" + config.getString("database.database");

    public void update(String statement) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement stmnt = connection.createStatement();
            stmnt.executeUpdate(statement);
        } catch (SQLException ignored) {
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void query(String statement) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement stmnt = connection.createStatement();
            stmnt.executeQuery(statement);
        } catch (SQLException ignored) {
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet select(String statement) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement stmnt = connection.createStatement();
            return stmnt.executeQuery(statement);

        } catch (SQLException ignored) {
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
