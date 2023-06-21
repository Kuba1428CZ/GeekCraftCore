package cz.kuba1428.coincraftcore.coincraftcore.misc;

import cz.kuba1428.coincraftcore.coincraftcore.Coincraftcore;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.ArrayList;


public class database {
    Coincraftcore plugin = Coincraftcore.getPlugin(Coincraftcore.class);
    FileConfiguration config = plugin.getConfig();
    String user = config.getString("database.user");
    String password = config.getString("database.password");
    String url = "jdbc:mysql://" + config.getString("database.host") + ":" + config.getString("database.port") + "/" + config.getString("database.database");

    public void update(String statement){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement stmnt = connection.createStatement();
            stmnt.executeUpdate(statement);
        }catch (SQLException e){} catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void query(String statement){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement stmnt = connection.createStatement();
            stmnt.executeQuery(statement);
        }catch (SQLException e){} catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public ResultSet select(String statement){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement stmnt = connection.createStatement();
            ResultSet rs = stmnt.executeQuery(statement);
            return rs;

        }catch (SQLException e){} catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
